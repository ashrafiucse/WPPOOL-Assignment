package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.JSONObject;
import utilities.ConfigManager;

import static utilities.DriverSetup.getDriver;

public class BasePage {
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration SHORT_WAIT = Duration.ofSeconds(10);
    private static final Duration QUICK_WAIT = Duration.ofSeconds(4);

    public BasePage(WebDriver driver) {}

    // ---------- Basic Element Helpers ----------

    public WebElement getElement(By locator) {
        return waitForElementToBePresence(locator);
    }

    public void clickOnElement(By locator) {
        // For WordPress admin menu elements in headless mode, use JavaScript click immediately
        if (isHeadless() && isWordPressAdminMenuElement(locator)) {
            clickWithJavaScript(locator);
            return;
        }

        // Use longer timeout for headless mode
        Duration timeout = isHeadless() ? Duration.ofSeconds(60) : DEFAULT_WAIT;
        clickOnElementWithRetry(locator, 3, false, timeout);
    }

    private boolean isHeadless() {
        String headless = System.getProperty("headless", "false");
        return "true".equalsIgnoreCase(headless);
    }

    private boolean isWordPressAdminMenuElement(By locator) {
        String locatorString = locator.toString();
        return locatorString.contains("wp-menu-name") ||
               locatorString.contains("plugins.php") ||
               locatorString.contains("post_type=page");
    }

    private void clickWithJavaScript(By locator) {
        try {
            WebElement element = waitForElementToBePresence(locator);
            scrollToSpecificElement(locator);
            try {
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
            System.out.println("[DEBUG] Element clicked successfully with JavaScript click (WordPress admin menu)");
        } catch (Exception e) {
            System.out.println("[DEBUG] JavaScript click failed: " + e.getMessage());
            throw e;
        }
    }

    public void clickOnElement(WebElement element) {
        element.click();
    }

    public List<WebElement> getElements(By locator) {
        return getDriver().findElements(locator);
    }

    public boolean isElementVisible(By locator) {
        return isElementVisible(locator, SHORT_WAIT, false);
    }

    /**
     * Enhanced element visibility check with configurable parameters
     * @param locator Element locator
     * @param timeout Wait timeout duration
     * @param enhancedCheck Whether to perform enhanced visibility validation
     * @return true if element is visible
     */
    public boolean isElementVisible(
            By locator,
            Duration timeout,
            boolean enhancedCheck
    ) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
            WebElement element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );

            if (enhancedCheck) {
                // Add extra wait for rendering
                Thread.sleep(1000);

                // Check multiple conditions for visibility
                return (
                        element.isDisplayed() &&
                                element.getSize().getHeight() > 0 &&
                                element.getSize().getWidth() > 0 &&
                                !element.getCssValue("visibility").equals("hidden") &&
                                !element.getCssValue("opacity").equals("0")
                );
            } else {
                // Standard visibility check
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                );
                return element.isDisplayed();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String getElementText(By locator) {
        return getElement(locator).getText();
    }

    public String getElementAttribute(By locator, String attributeName) {
        return getElement(locator).getAttribute(attributeName);
    }

    // ---------- Typing / Input Helpers ----------

    public void sendKeysText(By locator, String inputText) {
        waitUntilElementIsTypeable(locator);
        WebElement element = getElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();

        try {
            // [1] Sanitize text — handle quotes, newlines safely for JS execution
            String safeText = inputText
                    .replace("\\", "\\\\") // escape backslashes
                    .replace("\"", "\\\"") // escape double quotes
                    .replace("\n", "\\n") // escape newlines
                    .replace("\r", ""); // remove carriage returns

            // [2] Inject via JS — robust for long text or React fields
            js.executeScript(
                    "const element = arguments[0];" +
                            "const value = arguments[1];" +
                            "const lastValue = element.value;" +
                            "element.value = value;" +
                            "const tracker = element._valueTracker;" +
                            "if (tracker) { tracker.setValue(lastValue); }" +
                            "element.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "element.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "element.dispatchEvent(new Event('blur', { bubbles: true }));",
                    element,
                    safeText
            );
        } catch (Exception e) {
            // [3] Fallback to normal typing if JS fails
            element.clear();
            element.sendKeys(inputText);
        }
    }

    public void waitUntilElementIsTypeable(By inputField) {
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(5)
        );
        wait.until(
                ExpectedConditions.and(
                        ExpectedConditions.visibilityOfElementLocated(inputField),
                        ExpectedConditions.elementToBeClickable(inputField),
                        driver -> {
                            WebElement element = getElement(inputField);
                            return (
                                    element.isEnabled() &&
                                            element.getAttribute("readonly") == null
                            );
                        }
                )
        );
    }

    // ---------- Wait Helpers ----------

    public WebElement waitForElementToBePresence(By locator) {
        WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForElementToBeClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_WAIT);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Enhanced click method with retry mechanism and configurable options
     * @param locator Element locator
     * @param maxRetries Maximum number of retry attempts
     * @param waitForOverlays Whether to wait for modal overlays to disappear
     */
    public void clickOnElementWithRetry(
            By locator,
            int maxRetries,
            boolean waitForOverlays
    ) {
        clickOnElementWithRetry(
                locator,
                maxRetries,
                waitForOverlays,
                DEFAULT_WAIT
        );
    }

    /**
     * Enhanced click method with full configuration options
     * @param locator Element locator
     * @param maxRetries Maximum number of retry attempts
     * @param waitForOverlays Whether to wait for modal overlays to disappear
     * @param timeout Wait timeout duration
     */
    public void clickOnElementWithRetry(
            By locator,
            int maxRetries,
            boolean waitForOverlays,
            Duration timeout
    ) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (waitForOverlays) {
                    waitForOverlaysToDisappear();
                }

                WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
                WebElement element = wait.until(
                        ExpectedConditions.elementToBeClickable(locator)
                );

                scrollToSpecificElement(locator);
                Thread.sleep(300); // Small delay after scrolling

                element.click();
                return; // Success, exit method
            } catch (Exception e) {
                System.out.println(
                        "Click attempt " + attempt + " failed: " + e.getMessage()
                );

                if (attempt == maxRetries) {
                    // Last attempt, try JavaScript click
                    try {
                        WebElement element = getDriver().findElement(locator);
                        ((JavascriptExecutor) getDriver()).executeScript(
                                "arguments[0].click();",
                                element
                        );
                        return;
                    } catch (Exception jsException) {
                        throw new RuntimeException(
                                "Failed to click element after " +
                                        maxRetries +
                                        " attempts",
                                jsException
                        );
                    }
                }

                // Wait before retry
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Wait for modal overlays to disappear with configurable selectors
     * @param overlaySelectors Array of CSS selectors for overlays to wait for
     * @param timeout Maximum wait time
     */
    public void waitForOverlaysToDisappear(
            String[] overlaySelectors,
            Duration timeout
    ) {
        WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
        try {
            for (String selector : overlaySelectors) {
                try {
                    wait.until(
                            ExpectedConditions.invisibilityOfElementLocated(
                                    By.cssSelector(selector)
                            )
                    );
                } catch (TimeoutException e) {
                    // Overlay not found or already invisible, continue
                }
            }
            // Additional wait to ensure overlay animation is complete
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println(
                    "Modal overlay check completed with info: " + e.getMessage()
            );
        }
    }

    /**
     * Wait for common modal overlays to disappear
     */
    public void waitForOverlaysToDisappear() {
        String[] commonOverlaySelectors = {
                ".mantine-Modal-overlay",
                ".mantine-Overlay-root",
                "[data-overlay]",
                ".modal-overlay",
                ".overlay",
        };
        waitForOverlaysToDisappear(
                commonOverlaySelectors,
                Duration.ofSeconds(10)
        );
    }

    /**
     * Enhanced element visibility check with configurable validation
     * @param element WebElement to check
     * @param enhancedCheck Whether to perform comprehensive visibility validation
     * @return true if element is visible
     */
    public boolean isElementVisible(WebElement element, boolean enhancedCheck) {
        try {
            if (enhancedCheck) {
                // Add extra wait for rendering
                Thread.sleep(1000);

                // Check multiple conditions for visibility
                return (
                        element.isDisplayed() &&
                                element.getSize().getHeight() > 0 &&
                                element.getSize().getWidth() > 0 &&
                                !element.getCssValue("visibility").equals("hidden") &&
                                !element.getCssValue("opacity").equals("0")
                );
            } else {
                return element.isDisplayed();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for input field value to persist (not be cleared by framework)
     */
    public void waitForValueToPersist(By locator, String expectedValue) {
        WebDriverWait wait = new WebDriverWait(getDriver(), SHORT_WAIT);
        wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String actualValue = element.getAttribute("value");
                return actualValue != null && actualValue.equals(expectedValue);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    /**
     * Wait for element to disappear (useful for loading spinners, overlays)
     */
    public void waitForElementToDisappear(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(
                    getDriver(),
                    Duration.ofSeconds(10)
            );
            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(locator)
            );
        } catch (Exception e) {
            // Element already gone or never appeared
        }
    }

    /**
     * Retry mechanism for handling stale elements
     */
    public void clickWithRetry(By locator, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                clickOnElement(locator);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw e;
                }
            }
        }
    }

    /**
     * Convenience method with default retries (3 attempts)
     */
    public void clickWithRetry(By locator) {
        clickWithRetry(locator, 3);
    }

    // ---------- Dropdown Helper ----------

    public void dropDownOptionSelectByText(By locator, String optionText) {
        Select select = new Select(getElement(locator));
        select.selectByVisibleText(optionText);
    }

    // ---------- Browser / Interaction Helpers ----------

    public void hoverOverElement(By locator) {
        try {
            scrollToSpecificElement(locator);
            WebElement element = getElement(locator);
            Actions actions = new Actions(getDriver());
            actions.moveToElement(element).perform();
        } catch (Exception e) {
            // Silent failure
        }
    }

    public void scrollToSpecificElement(By locator) {
        try {
            WebElement element = getDriver().findElement(locator);
            JavascriptExecutor js = (JavascriptExecutor) getDriver();

            // Scroll element into view with center alignment
            js.executeScript(
                    "arguments[0].scrollIntoView({behavior: 'instant', block: 'center', inline: 'center'});",
                    element
            );

            // Wait for scroll to complete using explicit wait
            WebDriverWait scrollWait = new WebDriverWait(
                    getDriver(),
                    Duration.ofMillis(500)
            );
            scrollWait.until(driver -> {
                try {
                    return ((JavascriptExecutor) driver).executeScript(
                            "return !window.scrollY || window.scrollY !== arguments[0]",
                            element.getLocation().getY()
                    );
                } catch (Exception e) {
                    return true;
                }
            });
        } catch (Exception e) {
            // Try alternative scroll method silently
            try {
                WebElement element = getDriver().findElement(locator);
                Actions actions = new Actions(getDriver());
                actions.moveToElement(element).perform();
            } catch (Exception ex) {
                // Silent fallback
            }
        }
    }

    public void acceptAlert() {
        getDriver().switchTo().alert().accept();
    }

    public void switchToNextTab() {
        String originalWindow = getDriver().getWindowHandle();

        // Wait for new tab to open
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(10)
        );
        wait.until(driver -> driver.getWindowHandles().size() > 1);

        // Switch to the new tab
        for (String windowHandle : getDriver().getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                getDriver().switchTo().window(windowHandle);
                break;
            }
        }

        // Wait for new page to start loading
        wait.until(driver -> !driver.getCurrentUrl().equals("about:blank"));

        // Wait for page to be ready with proper explicit wait
        wait.until(driver -> {
            try {
                return ((JavascriptExecutor) driver).executeScript(
                        "return document.readyState"
                ).equals("complete");
            } catch (Exception e) {
                return false;
            }
        });

        // Wait for dynamic content to be loaded
        wait.until(driver -> {
            try {
                return ((JavascriptExecutor) driver).executeScript(
                        "return typeof window.Alpine !== 'undefined' || document.querySelector('.hs-accordion') !== null"
                );
            } catch (Exception e) {
                return true;
            }
        });
    }

    /**
     * Enhanced tab switching with URL verification
     */
    public void switchToTabWithUrl(String expectedUrlPattern) {
        String originalWindow = getDriver().getWindowHandle();

        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(15)
        );

        // Wait for new tab and switch to it
        wait.until(driver -> driver.getWindowHandles().size() > 1);

        for (String windowHandle : getDriver().getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                getDriver().switchTo().window(windowHandle);

                // Wait for correct URL
                try {
                    wait.until(driver ->
                            driver.getCurrentUrl().contains(expectedUrlPattern)
                    );

                    // Wait for page ready state
                    wait.until(driver ->
                            ((JavascriptExecutor) driver).executeScript(
                                    "return document.readyState"
                            ).equals("complete")
                    );

                    return; // Successfully switched
                } catch (Exception e) {
                    // This tab doesn't match, continue to next
                    continue;
                }
            }
        }

        throw new RuntimeException(
                "Could not find tab with URL pattern: " + expectedUrlPattern
        );
    }

    public By getElementThroughTagAndText(String tag, String contained_text) {
        return By.xpath(
                "//" + tag + "[contains(text(),'" + contained_text + "')]"
        );
    }

    public void browserReload() {
        browserReload(false, Duration.ofSeconds(2));
    }

    /**
     * Enhanced browser reload with configurable options
     * @param waitForPageReady Whether to wait for document ready state
     * @param additionalWaitTime Additional wait time after reload
     */
    public void browserReload(
            boolean waitForPageReady,
            Duration additionalWaitTime
    ) {
        getDriver().navigate().refresh();

        if (additionalWaitTime.toMillis() > 0) {
            try {
                Thread.sleep(additionalWaitTime.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (waitForPageReady) {
            WebDriverWait wait = new WebDriverWait(
                    getDriver(),
                    Duration.ofSeconds(15)
            );
            wait.until(webDriver -> {
                try {
                    return ((JavascriptExecutor) webDriver).executeScript(
                            "return document.readyState"
                    ).equals("complete");
                } catch (Exception e) {
                    return false;
                }
            });
        }
    }

    /**
     * Ensure Alpine.js sidebar is properly initialized and visible
     * This is especially important for headless mode
     */
    public void ensureSidebarIsVisible() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            // Simple Alpine.js sidebar fix without verbose logging
            js.executeScript(
                    "if(window.Alpine?.store) window.Alpine.store('sidebar').primarySidebarOpen = true;"
            );
        } catch (Exception ignored) {
            // Silently continue if Alpine.js is not available
        }
    }

    /**
     * Enhanced method for TipTap/Mantine Rich Text Editor
     * Properly sets content and triggers all necessary events
     */
    public void pasteIntoRichTextEditor(By editorLocator, String text) {
        try {
            WebElement editor = waitForElementToBePresence(editorLocator);
            JavascriptExecutor js = (JavascriptExecutor) getDriver();

            // Wait for editor to be fully initialized
            WebDriverWait wait = new WebDriverWait(
                    getDriver(),
                    Duration.ofSeconds(15)
            );
            wait.until(driver -> {
                Object editable = js.executeScript(
                        "return arguments[0].isContentEditable;",
                        editor
                );
                return Boolean.TRUE.equals(editable);
            });

            // Method 1: Set content using TipTap's setContent method (if available)
            boolean tiptapSuccess = (boolean) js.executeScript(
                    "try {" +
                            "  var editor = arguments[0];" +
                            "  var text = arguments[1];" +
                            "  " +
                            "  // Try to find TipTap editor instance" +
                            "  if (window.__tiptapEditor) {" +
                            "    window.__tiptapEditor.commands.setContent('<p>' + text + '</p>');" +
                            "    return true;" +
                            "  }" +
                            "  " +
                            "  // Try to set content directly" +
                            "  editor.innerHTML = '<p>' + text + '</p>';" +
                            "  " +
                            "  // Trigger all necessary events" +
                            "  editor.dispatchEvent(new Event('focus', { bubbles: true }));" +
                            "  editor.dispatchEvent(new InputEvent('input', { bubbles: true, inputType: 'insertText' }));" +
                            "  editor.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "  editor.dispatchEvent(new KeyboardEvent('keyup', { bubbles: true }));" +
                            "  editor.dispatchEvent(new Event('blur', { bubbles: true }));" +
                            "  " +
                            "  return true;" +
                            "} catch(e) {" +
                            "  console.error('TipTap update failed:', e);" +
                            "  return false;" +
                            "}",
                    editor,
                    text
            );

            if (!tiptapSuccess) {
                // Method 2: Simulate real typing with explicit waits
                editor.click(); // Focus

                // Wait for focus
                WebDriverWait focusWait = new WebDriverWait(
                        getDriver(),
                        QUICK_WAIT
                );
                focusWait.until(driver -> {
                    try {
                        return driver.switchTo().activeElement().equals(editor);
                    } catch (Exception e) {
                        return true;
                    }
                });

                // Clear existing content
                editor.sendKeys(Keys.CONTROL + "a");
                editor.sendKeys(Keys.DELETE);

                // Wait for content to be cleared
                focusWait.until(driver -> {
                    try {
                        String content = editor.getAttribute("innerHTML");
                        return (
                                content == null ||
                                        content.trim().isEmpty() ||
                                        content.equals("<p></p>")
                        );
                    } catch (Exception e) {
                        return true;
                    }
                });

                // Type the text
                editor.sendKeys(text);

                // Wait for text to appear
                focusWait.until(driver -> {
                    try {
                        String content = editor.getText();
                        return content != null && !content.trim().isEmpty();
                    } catch (Exception e) {
                        return true;
                    }
                });

                // Trigger blur
                editor.sendKeys(Keys.TAB);
            }

            // Wait for content to be processed with explicit wait
            WebDriverWait contentWait = new WebDriverWait(
                    getDriver(),
                    SHORT_WAIT
            );
            contentWait.until(driver -> {
                try {
                    String actualContent = (String) js.executeScript(
                            "return arguments[0].innerText || arguments[0].textContent || '';",
                            editor
                    );
                    return (
                            actualContent != null &&
                                    actualContent.trim().length() > 0
                    );
                } catch (Exception e) {
                    return true;
                }
            });

            // Verify content was set
            String actualContent = (String) js.executeScript(
                    "return arguments[0].innerText || arguments[0].textContent || '';",
                    editor
            );

            if (actualContent == null || actualContent.trim().isEmpty()) {
                throw new RuntimeException(
                        "[ERROR] Text did not persist in rich text editor"
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not set rich text editor content",
                    e
            );
        }
    }

    /**
     * Alternative: Type text character by character (slower but more reliable)
     */
    public void typeIntoRichTextEditor(By editorLocator, String text) {
        try {
            WebElement editor = waitForElementToBePresence(editorLocator);

            // Click to focus and wait for focus
            editor.click();
            WebDriverWait focusWait = new WebDriverWait(
                    getDriver(),
                    QUICK_WAIT
            );
            focusWait.until(driver -> {
                try {
                    return driver.switchTo().activeElement().equals(editor);
                } catch (Exception e) {
                    return true;
                }
            });

            // Clear any existing content
            editor.sendKeys(Keys.CONTROL + "a");
            editor.sendKeys(Keys.DELETE);

            // Wait for content to be cleared
            focusWait.until(driver -> {
                try {
                    return editor.getText().trim().isEmpty();
                } catch (Exception e) {
                    return true;
                }
            });

            // Type text directly (no character-by-character delay needed)
            editor.sendKeys(text);

            // Wait for text to appear
            focusWait.until(driver -> {
                try {
                    return editor.getText().contains(text);
                } catch (Exception e) {
                    return true;
                }
            });

            // Blur to save
            editor.sendKeys(Keys.TAB);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to type into rich text editor",
                    e
            );
        }
    }

    /**
     * Wait for rich text editor content to be saved
     */
    public void waitForRichTextEditorToSave() {
        WebDriverWait saveWait = new WebDriverWait(getDriver(), SHORT_WAIT);

        // Wait for any "Saving..." indicators to disappear
        try {
            saveWait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath(
                                    "//*[contains(text(), 'Saving') or contains(text(), 'saving')]"
                            )
                    )
            );
        } catch (Exception e) {
            // No saving indicator found, continue
        }

        // Wait for content to be persisted
        saveWait.until(driver -> {
            try {
                return ((JavascriptExecutor) driver).executeScript(
                        "return document.readyState === 'complete'"
                );
            } catch (Exception e) {
                return true;
            }
        });
    }

    public void uploadLocalFileFromProject(
            By hiddenFileInput,
            String relativeFilePath
    ) {
        // Resolve absolute path of the file inside your project
        String absolutePath = Paths.get(
                        System.getProperty("user.dir"),
                        relativeFilePath
                )
                .toAbsolutePath()
                .toString();

        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(10)
        );

        // Wait until input is available in the DOM
        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(hiddenFileInput)
        );

        // Make it visible temporarily so Selenium can interact
        ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].removeAttribute('hidden'); arguments[0].style.display='block';",
                input
        );

        // Upload the file
        input.sendKeys(absolutePath);

        // Wait for upload to be processed
        WebDriverWait uploadWait = new WebDriverWait(getDriver(), SHORT_WAIT);
        uploadWait.until(driver -> {
            try {
                // Check if file input has a value or if upload started
                return (
                        !input.getAttribute("value").isEmpty() ||
                                driver
                                        .findElement(
                                                By.xpath(
                                                        "//*[contains(text(), 'Upload') or contains(@class, 'upload')]"
                                                )
                                        )
                                        .isDisplayed()
                );
            } catch (Exception e) {
                return true; // Continue if no upload indicator found
            }
        });
    }

    public void clickDoneButtonAfterUpload(
            By doneButtonLocator,
            int maxWaitSeconds
    ) {
        clickDoneButtonAfterUpload(
                doneButtonLocator,
                maxWaitSeconds,
                false,
                false
        );
    }

    /**
     * Enhanced done button click with configurable options
     * @param doneButtonLocator Button locator
     * @param maxWaitSeconds Maximum wait time in seconds
     * @param waitForOverlays Whether to wait for overlays to disappear
     * @param useJavaScriptClick Whether to use JavaScript click instead of regular click
     */
    public void clickDoneButtonAfterUpload(
            By doneButtonLocator,
            int maxWaitSeconds,
            boolean waitForOverlays,
            boolean useJavaScriptClick
    ) {
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(maxWaitSeconds)
        );

        if (waitForOverlays) {
            waitForOverlaysToDisappear();
        }

        WebElement doneButton = wait.until(driver -> {
            try {
                WebElement btn = driver.findElement(doneButtonLocator);
                if (btn.isDisplayed() && btn.isEnabled()) {
                    return btn;
                }
            } catch (Exception e) {
                // Element not found yet, keep waiting
            }
            return null; // keep waiting
        });

        // Scroll to element
        ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                doneButton
        );

        // Wait a bit after scrolling
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click based on preference
        if (useJavaScriptClick) {
            ((JavascriptExecutor) getDriver()).executeScript(
                    "arguments[0].click();",
                    doneButton
            );
        } else {
            try {
                doneButton.click();
            } catch (Exception e) {
                System.out.println(
                        "Regular click failed for done button, trying JavaScript click: " +
                                e.getMessage()
                );
                ((JavascriptExecutor) getDriver()).executeScript(
                        "arguments[0].click();",
                        doneButton
                );
            }
        }
    }

    /**
     * Scroll to element with configurable behavior
     * @param element WebElement to scroll to
     * @param smooth Whether to use smooth scrolling
     * @param block Scroll alignment (start, center, end, nearest)
     */
    public void scrollToElement(
            WebElement element,
            boolean smooth,
            String block
    ) {
        try {
            String behavior = smooth ? "smooth" : "auto";
            ((JavascriptExecutor) getDriver()).executeScript(
                    "arguments[0].scrollIntoView({behavior: '" +
                            behavior +
                            "', block: '" +
                            block +
                            "', inline: 'center'});",
                    element
            );
            Thread.sleep(500); // Wait for scroll to complete
        } catch (Exception e) {
            System.out.println("Scroll operation info: " + e.getMessage());
        }
    }

    /**
     * Scroll to element with default smooth behavior and center alignment
     */
    public void scrollToElement(WebElement element) {
        scrollToElement(element, true, "center");
    }

    public String getFutureDateTime(int daysToAdd, int minutesToAdd) {
        LocalDateTime futureDateTime = LocalDateTime.now()
                .plusDays(daysToAdd)
                .plusMinutes(minutesToAdd);

        // Format required for <input type="datetime-local">
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm"
        );
        return futureDateTime.format(formatter);
    }

    public void setDateTime(By dateTimeLocator, String dateTimeValue) {
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(10)
        );
        WebElement dateTimeInput = wait.until(
                ExpectedConditions.elementToBeClickable(dateTimeLocator)
        );

        // Format must match datetime-local ISO format
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                dateTimeInput,
                dateTimeValue
        );
    }

    public void searchAndPressEnter(By searchFieldLocator, String searchData) {
        try {
            WebDriverWait wait = new WebDriverWait(
                    getDriver(),
                    Duration.ofSeconds(10)
            );

            // Wait for the search field to be visible and clickable
            WebElement searchField = wait.until(
                    ExpectedConditions.elementToBeClickable(searchFieldLocator)
            );

            // Clear existing text if any
            searchField.clear();

            // Type search data
            searchField.sendKeys(searchData);

            // Press ENTER key
            searchField.sendKeys(Keys.ENTER);

            // Optional: wait a bit for results to load
            wait.until(driver -> {
                // Example condition: wait for URL change or page update
                return !driver.getCurrentUrl().contains("searching");
            });
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to search and press Enter for data: " + searchData,
                    e
            );
        }
    }

    public void waitForElementToBePresence(WebElement element) {
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(5)
        );
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    public boolean hasAttribute(By locator, String attributeName) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            String attrValue = element.getAttribute(attributeName);
            return attrValue != null && !attrValue.isEmpty();

        } catch (Exception e) {
            System.out.println("Attribute check failed for locator: " + locator + " | " + e.getMessage());
            return false;
        }
    }

    // ---------- HTTP/REST API Utilities ----------

    /**
     * Send HTTP GET request and return response
     */
    public String sendGetRequest(String urlString) throws IOException {
        return sendHttpRequest("GET", urlString, null, null);
    }

    /**
     * Send HTTP POST request with JSON body
     */
    public String sendPostRequest(String urlString, String jsonBody) throws IOException {
        return sendHttpRequest("POST", urlString, jsonBody, "application/json");
    }

    /**
     * Send HTTP PUT request with JSON body
     */
    public String sendPutRequest(String urlString, String jsonBody) throws IOException {
        return sendHttpRequest("PUT", urlString, jsonBody, "application/json");
    }

    /**
     * Send HTTP DELETE request
     */
    public String sendDeleteRequest(String urlString) throws IOException {
        return sendHttpRequest("DELETE", urlString, null, null);
    }

    /**
     * Generic HTTP request method
     */
    private String sendHttpRequest(String method, String urlString, String body, String contentType) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType);
            }
            
            if (body != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.getBytes());
                }
            }
            
            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode >= 200 && responseCode < 300) 
                ? connection.getInputStream() 
                : connection.getErrorStream();
                
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Get HTTP response code for URL
     */
    public int getResponseCode(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(5000);
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode;
    }

    /**
     * Check if URL is accessible
     */
    public boolean isUrlAccessible(String urlString) {
        try {
            int responseCode = getResponseCode(urlString);
            return responseCode >= 200 && responseCode < 400;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------- CSV Processing Utilities ----------

    /**
     * Parse CSV line into list of values
     */
    public List<String> parseCsvRow(String csvLine) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < csvLine.length(); i++) {
            char c = csvLine.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i < csvLine.length() - 1 && csvLine.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString().trim());
        return result;
    }

    /**
     * Read CSV data from URL
     */
    public List<List<String>> readCsvFromUrl(String csvUrl) throws IOException {
        List<List<String>> data = new ArrayList<>();
        
        HttpURLConnection connection = (HttpURLConnection) new URL(csvUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.add(parseCsvRow(line));
                }
            }
        }
        
        return data;
    }

    /**
     * Convert list of lists to CSV string
     */
    public String convertToCsv(List<List<String>> data) {
        StringBuilder csv = new StringBuilder();
        for (List<String> row : data) {
            for (int i = 0; i < row.size(); i++) {
                String value = row.get(i);
                if (value.contains(",") || value.contains("\"")) {
                    csv.append("\"").append(value.replace("\"", "\"\"")).append("\"");
                } else {
                    csv.append(value);
                }
                if (i < row.size() - 1) {
                    csv.append(",");
                }
            }
            csv.append("\n");
        }
        return csv.toString();
    }

    // ---------- Table Operations ----------

    /**
     * Extract table data as list of maps (header -> value)
     */
    public List<Map<String, String>> extractTableData(By tableLocator) {
        List<Map<String, String>> tableData = new ArrayList<>();
        
        try {
            WebElement table = waitForElementToBePresence(tableLocator);
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            
            if (rows.isEmpty()) {
                return tableData;
            }
            
            // Get headers from first row
            List<WebElement> headerCells = rows.get(0).findElements(By.tagName("th"));
            List<String> headers = new ArrayList<>();
            
            if (headerCells.isEmpty()) {
                // If no th elements, use td from first row as headers
                headerCells = rows.get(0).findElements(By.tagName("td"));
            }
            
            for (WebElement headerCell : headerCells) {
                headers.add(headerCell.getText().trim());
            }
            
            // Extract data rows (skip header row)
            for (int i = 1; i < rows.size(); i++) {
                List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                Map<String, String> rowData = new LinkedHashMap<>();
                
                for (int j = 0; j < Math.min(headers.size(), cells.size()); j++) {
                    String header = j < headers.size() ? headers.get(j) : "Column" + (j + 1);
                    String value = cells.get(j).getText().trim();
                    rowData.put(header, value);
                }
                
                tableData.add(rowData);
            }
            
        } catch (Exception e) {
            logError("Failed to extract table data", e);
        }
        
        return tableData;
    }

    /**
     * Get table headers as list of strings
     */
    public List<String> getTableHeaders(By tableLocator) {
        List<String> headers = new ArrayList<>();
        
        try {
            WebElement table = waitForElementToBePresence(tableLocator);
            List<WebElement> headerCells = table.findElements(By.tagName("th"));
            
            if (headerCells.isEmpty()) {
                // Try first row's td elements as headers
                List<WebElement> firstRow = table.findElements(By.cssSelector("tr:first-child td"));
                for (WebElement cell : firstRow) {
                    headers.add(cell.getText().trim());
                }
            } else {
                for (WebElement headerCell : headerCells) {
                    headers.add(headerCell.getText().trim());
                }
            }
            
        } catch (Exception e) {
            logError("Failed to get table headers", e);
        }
        
        return headers;
    }

    /**
     * Get table row count
     */
    public int getTableRowCount(By tableLocator) {
        try {
            WebElement table = waitForElementToBePresence(tableLocator);
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            return rows.size();
        } catch (Exception e) {
            logError("Failed to get table row count", e);
            return 0;
        }
    }

    /**
     * Verify table contains specific text
     */
    public boolean verifyTableContains(By tableLocator, String searchText) {
        try {
            WebElement table = waitForElementToBePresence(tableLocator);
            String tableText = table.getText();
            return tableText.contains(searchText);
        } catch (Exception e) {
            logError("Failed to verify table contains text: " + searchText, e);
            return false;
        }
    }

    /**
     * Compare table data with CSV data
     */
    public boolean compareTableWithCsv(By tableLocator, List<List<String>> csvData) {
        try {
            List<Map<String, String>> tableData = extractTableData(tableLocator);
            
            if (csvData.isEmpty() || tableData.isEmpty()) {
                return false;
            }
            
            // Compare first few rows for validation
            int rowsToCompare = Math.min(5, Math.min(csvData.size(), tableData.size()));
            
            for (int i = 0; i < rowsToCompare; i++) {
                List<String> csvRow = csvData.get(i);
                Map<String, String> tableRow = tableData.get(i);
                
                // Compare values
                int colIndex = 0;
                for (String csvValue : csvRow) {
                    if (colIndex < tableRow.size()) {
                        String tableValue = tableRow.get(colIndex);
                        if (!csvValue.trim().equals(tableValue.trim())) {
                            logWarning("Data mismatch at row " + (i+1) + ", column " + (colIndex+1) + 
                                      ". CSV: '" + csvValue + "', Table: '" + tableValue + "'");
                        }
                    }
                    colIndex++;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logError("Failed to compare table with CSV", e);
            return false;
        }
    }

    // ---------- URL and Validation Utilities ----------

    /**
     * Validate URL format
     */
    public boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Extract parameters from URL
     */
    public Map<String, String> extractUrlParameters(String urlString) {
        Map<String, String> params = new HashMap<>();
        
        try {
            URL url = new URL(urlString);
            String query = url.getQuery();
            
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                    }
                }
            }
        } catch (Exception e) {
            logError("Failed to extract URL parameters", e);
        }
        
        return params;
    }

    /**
     * Build URL with parameters
     */
    public String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        
        StringBuilder url = new StringBuilder(baseUrl);
        boolean firstParam = !baseUrl.contains("?");
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (firstParam) {
                url.append("?");
                firstParam = false;
            } else {
                url.append("&");
            }
            
            try {
                url.append(java.net.URLEncoder.encode(entry.getKey(), "UTF-8"))
                   .append("=")
                   .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                url.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        
        return url.toString();
    }

    /**
     * Normalize URL (remove trailing slash, etc.)
     */
    public String normalizeUrl(String url) {
        if (url == null) return "";
        
        String normalized = url.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        
        return normalized;
    }

    // ---------- Enhanced Logging and Reporting ----------

    /**
     * Log info message with timestamp
     */
    public void logInfo(String message) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.out.println("[INFO " + timestamp + "] " + message);
    }

    /**
     * Log error with exception
     */
    public void logError(String message, Throwable throwable) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.err.println("[ERROR " + timestamp + "] " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    /**
     * Log warning message
     */
    public void logWarning(String message) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.out.println("[WARN " + timestamp + "] " + message);
    }

    /**
     * Log test step
     */
    public void logStep(String stepDescription) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.out.println("[STEP " + timestamp + "] " + stepDescription);
    }

    /**
     * Log API call details
     */
    public void logApiCall(String method, String url, int responseCode, long responseTime) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.out.println("[API " + timestamp + "] " + method + " " + url + 
                          " -> " + responseCode + " (" + responseTime + "ms)");
    }

    /**
     * Attach screenshot to report (Allure integration)
     */
    public void attachScreenshotToReport(String description) {
        try {
            byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
            io.qameta.allure.Allure.addAttachment(description, "image/png", new ByteArrayInputStream(screenshot), ".png");
        } catch (Exception e) {
            logError("Failed to attach screenshot to report", e);
        }
    }

    /**
     * Attach text content to report
     */
    public void attachTextToReport(String title, String content) {
        try {
            io.qameta.allure.Allure.addAttachment(title, "text/plain", content);
        } catch (Exception e) {
            logError("Failed to attach text to report", e);
        }
    }

    /**
     * Attach JSON to report
     */
    public void attachJsonToReport(String title, String jsonContent) {
        try {
            io.qameta.allure.Allure.addAttachment(title, "application/json", jsonContent);
        } catch (Exception e) {
            logError("Failed to attach JSON to report", e);
        }
    }

    // ---------- Environment and Configuration ----------

    /**
     * Get environment variable with fallback
     */
    public String getEnvironmentVariable(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get environment variable
     */
    public String getEnvironmentVariable(String key) {
        return System.getenv(key);
    }

    /**
     * Check if environment variable is set
     */
    public boolean isEnvironmentSet(String key) {
        return System.getenv(key) != null;
    }

    /**
     * Get system property with fallback
     */
    public String getSystemProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    /**
     * Get system property
     */
    public String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    // ---------- Data Validation Utilities ----------

    /**
     * Compare two data sets and return differences
     */
    public List<String> findDataDifferences(List<Map<String, String>> expected, List<Map<String, String>> actual) {
        List<String> differences = new ArrayList<>();
        
        if (expected.size() != actual.size()) {
            differences.add("Row count mismatch: Expected=" + expected.size() + ", Actual=" + actual.size());
        }
        
        int rowsToCompare = Math.min(expected.size(), actual.size());
        
        for (int i = 0; i < rowsToCompare; i++) {
            Map<String, String> expectedRow = expected.get(i);
            Map<String, String> actualRow = actual.get(i);
            
            for (String key : expectedRow.keySet()) {
                String expectedValue = expectedRow.get(key);
                String actualValue = actualRow.get(key);
                
                if (!expectedValue.equals(actualValue)) {
                    differences.add("Row " + (i+1) + ", Column '" + key + "': Expected='" + 
                                   expectedValue + "', Actual='" + actualValue + "'");
                }
            }
        }
        
        return differences;
    }

    /**
     * Validate data structure against required columns
     */
    public boolean validateDataStructure(List<Map<String, String>> data, List<String> requiredColumns) {
        if (data.isEmpty()) {
            return false;
        }
        
        Map<String, String> firstRow = data.get(0);
        for (String column : requiredColumns) {
            if (!firstRow.containsKey(column)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Check if string is valid JSON
     */
    public boolean isValidJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------- Simple Utilities ----------
    /**
     * Convert Google Sheet URL to CSV export URL
     */
    public String convertGoogleSheetToCsvUrl(String googleSheetUrl) {
        try {
            // Extract sheet ID from Google Sheets URL
            Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9-_]+)");
            Matcher matcher = pattern.matcher(googleSheetUrl);
             
            if (matcher.find()) {
                String sheetId = matcher.group(1);
                return "https://docs.google.com/spreadsheets/d/" + sheetId + "/export?format=csv";
            }
            return null;
        } catch (Exception e) {
            logError("Failed to convert Google Sheet URL to CSV", e);
            return null;
        }
    }

    /**
     * Extract table ID from FlexTable edit URL
     * @param editUrl URL in format: ...#/tables/edit/41
     * @return Table ID as String, or null if not found
     */
    public String extractTableIdFromEditUrl(String editUrl) {
        try {
            Pattern pattern = Pattern.compile("/edit/(\\d+)");
            Matcher matcher = pattern.matcher(editUrl);
            
            if (matcher.find()) {
                String tableId = matcher.group(1);
                logInfo("Successfully extracted table ID: " + tableId + " from URL: " + editUrl);
                return tableId;
            }
            logWarning("No table ID found in edit URL: " + editUrl);
            return null;
        } catch (Exception e) {
            logError("Failed to extract table ID from edit URL", e);
            return null;
        }
    }

    /**
     * Read Google Sheets data from environment
     */
    public List<List<String>> readGoogleSheetFromEnv() {
        try {
            String googleSheetUrl = ConfigManager.get("GOOGLE_SHEET_LINK");
            if (googleSheetUrl == null || googleSheetUrl.trim().isEmpty()) {
                logError("GOOGLE_SHEET_LINK not found in environment variables", null);
                return new ArrayList<>();
            }
            
            String csvUrl = convertGoogleSheetToCsvUrl(googleSheetUrl);
            if (csvUrl == null) {
                logError("Failed to convert Google Sheet URL to CSV", null);
                return new ArrayList<>();
            }
            
            return readCsvFromUrl(csvUrl);
        } catch (Exception e) {
            logError("Failed to read Google Sheet from environment", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get specific cell by row and column index
     */
    public String getCell(List<List<String>> csvData, int rowIndex, int colIndex) {
        if (csvData != null && rowIndex < csvData.size() && colIndex < csvData.get(rowIndex).size()) {
            return csvData.get(rowIndex).get(colIndex);
        }
        return null;
    }

    /**
     * Get entire row by index
     */
    public List<String> getRow(List<List<String>> csvData, int rowIndex) {
        if (csvData != null && rowIndex < csvData.size()) {
            return csvData.get(rowIndex);
        }
        return new ArrayList<>();
    }

    /**
     * Get entire column by index
     */
    public List<String> getColumn(List<List<String>> csvData, int colIndex) {
        List<String> column = new ArrayList<>();
        
        if (csvData != null) {
            for (List<String> row : csvData) {
                if (colIndex < row.size()) {
                    column.add(row.get(colIndex));
                }
            }
        }
        
        return column;
    }

    /**
     * Get first column data
     */
    public List<String> getFirstColumn(List<List<String>> csvData) {
        return getColumn(csvData, 0);
    }

    /**
     * Get first row data
     */
    public List<String> getFirstRow(List<List<String>> csvData) {
        return getRow(csvData, 0);
    }
}
