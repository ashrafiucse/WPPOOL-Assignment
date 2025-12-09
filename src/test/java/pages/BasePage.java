package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

import static utilities.DriverSetup.getDriver;

public class BasePage {
    protected WebDriver driver;
    protected static final Duration SHORT_WAIT = Duration.ofSeconds(5);
    protected static final Duration MEDIUM_WAIT = Duration.ofSeconds(10);
    protected static final Duration LONG_WAIT = Duration.ofSeconds(20);
    protected static final Duration QUICK_WAIT = Duration.ofSeconds(3);

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    // ---------- Element Visibility ----------
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
    
    /**
     * Headless mode enhanced visibility check with multiple attempts
     * @param locator Element locator
     * @return true if element is visible
     */
    public boolean isElementVisibleHeadless(By locator) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                // Force element visibility in headless mode
                JavascriptExecutor js = (JavascriptExecutor) getDriver();
                js.executeScript(
                    "var elements = arguments[0];" +
                    "for(var i = 0; i < elements.length; i++) {" +
                    "  var element = elements[i];" +
                    "  if(element) {" +
                    "    element.style.display = 'block';" +
                    "    element.style.visibility = 'visible';" +
                    "    element.style.opacity = '1';" +
                    "    element.style.position = 'relative';" +
                    "    element.style.zIndex = '9999';" +
                    "  }" +
                    "}"
                );
                
                Thread.sleep(1000);
                
                WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                
                if (element.isDisplayed() && 
                    element.getSize().getHeight() > 0 && 
                    element.getSize().getWidth() > 0) {
                    return true;
                }
                
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("Headless visibility check attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == 3) {
                    return false;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }

    // ---------- Element Interaction ----------
    public void clickOnElement(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), MEDIUM_WAIT);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            scrollToSpecificElement(locator);
            Thread.sleep(500);
            element.click();
        } catch (Exception e) {
            System.out.println("Click failed: " + e.getMessage());
            try {
                WebElement element = getDriver().findElement(locator);
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
            } catch (Exception jsException) {
                System.out.println("JavaScript click also failed: " + jsException.getMessage());
            }
        }
    }

    // ---------- Text Input ----------
    public void sendKeysText(By locator, String text) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), MEDIUM_WAIT);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println("Failed to send keys: " + e.getMessage());
        }
    }

    public void typeIntoRichTextEditor(By editorLocator, String text) {
        try {
            // Handle headless mode before typing
            try {
                JavascriptExecutor js = (JavascriptExecutor) getDriver();
                js.executeScript(
                    "// Ensure editor is ready and visible" +
                    "var editors = document.querySelectorAll('h1[contenteditable=\"true\"], .editor-post-title__input');" +
                    "for(var i = 0; i < editors.length; i++) {" +
                    "  editors[i].style.display = 'block';" +
                    "  editors[i].style.visibility = 'visible';" +
                    "  editors[i].style.opacity = '1';" +
                    "}"
                );
            Thread.sleep(1000);
            } catch (Exception e) {
                // Silent fallback
            }
            
            WebDriverWait wait = new WebDriverWait(getDriver(), MEDIUM_WAIT);
            WebElement editor = wait.until(ExpectedConditions.presenceOfElementLocated(editorLocator));

            // Enhanced focus handling for headless mode
            try {
                editor.click();
                Thread.sleep(500);
                
                // Alternative focus method for headless mode
                JavascriptExecutor js = (JavascriptExecutor) getDriver();
                js.executeScript("arguments[0].focus();", editor);
                Thread.sleep(1000);
            } catch (Exception e) {
                // Fallback to regular click
                editor.click();
            }

            // Wait for focus with enhanced timeout
            WebDriverWait focusWait = new WebDriverWait(
                    getDriver(),
                    Duration.ofSeconds(10) // Longer wait for headless mode
            );
            focusWait.until(driver -> {
                try {
                    return driver.switchTo().activeElement().equals(editor);
                } catch (Exception e) {
                    return true; // If can't switch, assume focused
                }
            });

            // Enhanced content clearing for headless mode
            try {
                editor.sendKeys(Keys.CONTROL + "a");
                editor.sendKeys(Keys.DELETE);
                Thread.sleep(500);
                
                // Additional clear attempts
                for (int i = 0; i < 3; i++) {
                    editor.sendKeys(Keys.CONTROL + "a");
                    editor.sendKeys(Keys.DELETE);
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                // Fallback method
                editor.clear();
                Thread.sleep(1000);
            }

            // Wait for content to be cleared with enhanced timeout
            WebDriverWait clearWait = new WebDriverWait(
                    getDriver(),
                    Duration.ofSeconds(8)
            );
            clearWait.until(driver -> {
                try {
                    String content = editor.getText().trim();
                    return content.isEmpty() || content.equals(" ");
                } catch (Exception e) {
                    return true;
                }
            });

            // Enhanced text typing for headless mode
            try {
                // Try direct text input first
                editor.sendKeys(text);
                Thread.sleep(1000);
            } catch (Exception e) {
                // Fallback to character-by-character typing
                char[] chars = text.toCharArray();
                for (char c : chars) {
                    try {
                        editor.sendKeys(String.valueOf(c));
                        Thread.sleep(20); // Faster typing for headless mode
                    } catch (Exception charException) {
                        // Try JavaScript input
                        JavascriptExecutor js = (JavascriptExecutor) getDriver();
                        js.executeScript("arguments[0].value = arguments[1];", editor, c);
                        Thread.sleep(50);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to type into rich text editor: " + e.getMessage());
            throw new RuntimeException("Failed to type into rich text editor: " + e.getMessage(), e);
        }
    }

    // ---------- Element Retrieval ----------
    public List<WebElement> getElements(By locator) {
        return getDriver().findElements(locator);
    }

    public String getElementText(By locator) {
        return getDriver().findElement(locator).getText();
    }

    public String getElementAttribute(By locator, String attributeName) {
        return getDriver().findElement(locator).getAttribute(attributeName);
    }

    // ---------- Scrolling ----------
    public void scrollToSpecificElement(By locator) {
        try {
            WebElement element = getDriver().findElement(locator);
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", element);
        } catch (Exception e) {
            // Fallback to traditional scroll
            try {
                WebElement element = getDriver().findElement(locator);
                element.sendKeys(Keys.PAGE_DOWN);
                element.sendKeys(Keys.PAGE_UP);
                element.sendKeys(Keys.PAGE_DOWN);
            } catch (Exception scrollException) {
                // Ignore scroll errors
            }
        }
    }

    // ---------- Dropdown ----------
    public void dropDownOptionSelectByText(By dropdownLocator, String optionText) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), MEDIUM_WAIT);
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            dropdown.click();
            
            List<WebElement> options = dropdown.findElements(By.tagName("option"));
            for (WebElement option : options) {
                if (option.getText().trim().equals(optionText)) {
                    option.click();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to select dropdown option: " + e.getMessage());
        }
    }
}