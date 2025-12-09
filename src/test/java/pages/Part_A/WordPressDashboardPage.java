package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.BasePage;
import static utilities.DriverSetup.getDriver;

import java.util.List;

public class WordPressDashboardPage extends BasePage {
    public WordPressDashboardPage(WebDriver driver) {
        super(driver);
    }
    public By dashboardHomeButton = By.xpath("//a[contains(text(),'Home') and contains(@href,'index.php')]");
    public By pluginsMenu = By.cssSelector("a[href='plugins.php'] .wp-menu-name");
    
    // Alternative locators for headless mode
    public By dashboardHomeButtonAlt = By.cssSelector("#menu-dashboard .wp-menu-name");
    public By pluginsMenuAlt = By.cssSelector("a[href='plugins.php']");
    
    /**
     * Enhanced method to check dashboard home button visibility
     * Uses multiple locators for better headless compatibility
     */
    public boolean isDashboardHomeVisible() {
        // Try primary locator first
        if (isElementVisible(dashboardHomeButton)) {
            return true;
        }
        // Try alternative locator
        if (isElementVisible(dashboardHomeButtonAlt)) {
            return true;
        }
        // Try more generic approach
        return isElementVisible(By.cssSelector("#menu-dashboard"));
    }
    
    /**
     * Enhanced method to check plugins menu visibility
     * Uses multiple locators for better headless compatibility
     */
    public boolean isPluginsMenuVisible() {
        // Try primary locator first
        if (isElementVisible(pluginsMenu)) {
            return true;
        }
        // Try alternative locator
        if (isElementVisible(pluginsMenuAlt)) {
            return true;
        }
        // Try more generic approach
        return isElementVisible(By.cssSelector("a[href*='plugins']"));
    }
    public By flexTableMenu = By.xpath("//div[contains(text(),'FlexTable') and contains(@class,'wp-menu-name')]");
    public By flexTableMenuAlt = By.xpath("//a[contains(@href,'flextable') or contains(@href,'gswpts')]//div[contains(text(),'FlexTable')]");
    public By flexTableMenuGeneric = By.xpath("//a[contains(@href,'admin.php') and .//div[contains(text(),'FlexTable')]]");
    public By flexTableMenuText = By.xpath("//a[contains(text(),'FlexTable')]");
    public By flexTableMenuPartial = By.xpath("//a[contains(.,'FlexTable')]");
    public By flexTableMenuCaseInsensitive = By.xpath("//a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'flextable')]");
    public By pagesMenu = By.xpath("//a[contains(@href,'post_type=page')]//div[contains(text(),'Pages')]");
    public By pagesMenuAlt = By.cssSelector("a[href*='post_type=page'] .wp-menu-name");
    public By pagesMenuGeneric = By.cssSelector("a[href*='post_type=page']");
    

    
    /**
     * Enhanced method to find Pages menu with multiple fallback strategies
     */
    public By findFlexTableMenu() {
        // Try primary locator first
        if (isElementVisible(flexTableMenu)) {
            return flexTableMenu;
        }
        // Try alternative locator
        if (isElementVisible(flexTableMenuAlt)) {
            return flexTableMenuAlt;
        }
        // Try generic locator
        if (isElementVisible(flexTableMenuGeneric)) {
            return flexTableMenuGeneric;
        }
        // Last resort - find by text content
        try {
            List<WebElement> allMenus = getDriver().findElements(By.cssSelector("#adminmenu a"));
            for (WebElement menu : allMenus) {
                if (menu.getText().toLowerCase().contains("flextable")) {
                    return By.xpath("//a[contains(text(),'" + menu.getText() + "')]");
                }
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return flexTableMenu; // Return default as last resort
    }
    
    /**
     * Enhanced method to find Pages menu with multiple fallback strategies
     */
    public By findPagesMenu() {
        // Try primary locator first
        if (isElementVisible(pagesMenu)) {
            return pagesMenu;
        }
        // Try alternative locator
        if (isElementVisible(pagesMenuAlt)) {
            return pagesMenuAlt;
        }
        // Try generic locator
        if (isElementVisible(pagesMenuGeneric)) {
            return pagesMenuGeneric;
        }
        // Last resort - find by text content
        try {
            List<WebElement> allMenus = getDriver().findElements(By.cssSelector("#adminmenu a"));
            for (WebElement menu : allMenus) {
                if (menu.getText().toLowerCase().contains("pages")) {
                    return By.xpath("//a[contains(text(),'" + menu.getText() + "')]");
                }
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return pagesMenu; // Return default as last resort
    }
    
    /**
     * Enhanced click method for Pages menu with headless mode support
     */
    public void clickPagesMenu() {
        By menuLocator = findPagesMenu();
        
        // Force menu to be visible before clicking
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript(
                "// Ensure admin menu is visible and expanded" +
                "var adminMenu = document.querySelector('#adminmenu');" +
                "if(adminMenu) {" +
                "  adminMenu.style.display = 'block';" +
                "  adminMenu.style.visibility = 'visible';" +
                "  adminMenu.style.opacity = '1';" +
                "  adminMenu.style.position = 'relative';" +
                "  adminMenu.style.left = '0px';" +
                "}" +
                "// Ensure all menu items are visible" +
                "var menuItems = document.querySelectorAll('#adminmenu li');" +
                "for(var i = 0; i < menuItems.length; i++) {" +
                "  menuItems[i].style.display = 'block';" +
                "  menuItems[i].style.visibility = 'visible';" +
                "  menuItems[i].style.opacity = '1';" +
                "  menuItems[i].style.position = 'relative';" +
                "  menuItems[i].style.left = '0px';" +
                "}"
            );
            Thread.sleep(1000);
        } catch (Exception e) {
            // Silent fallback
        }
        
        clickOnElement(menuLocator);
    }
    
    /**
     * Enhanced click method for FlexTable menu with headless mode support
     */
    public void clickFlexTableMenu() {
        // Force menu to be visible before finding
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript(
                "// Ensure admin menu is visible and expanded" +
                "var adminMenu = document.querySelector('#adminmenu');" +
                "if(adminMenu) {" +
                "  adminMenu.style.display = 'block';" +
                "  adminMenu.style.visibility = 'visible';" +
                "  adminMenu.style.opacity = '1';" +
                "  adminMenu.style.position = 'relative';" +
                "  adminMenu.style.left = '0px';" +
                "}" +
                "// Ensure all menu items are visible" +
                "var menuItems = document.querySelectorAll('#adminmenu li');" +
                "for(var i = 0; i < menuItems.length; i++) {" +
                "  menuItems[i].style.display = 'block';" +
                "  menuItems[i].style.visibility = 'visible';" +
                "  menuItems[i].style.opacity = '1';" +
                "  menuItems[i].style.position = 'relative';" +
                "  menuItems[i].style.left = '0px';" +
                "}" +
                "// Force all menu links to be clickable" +
                "var menuLinks = document.querySelectorAll('#adminmenu a');" +
                "for(var i = 0; i < menuLinks.length; i++) {" +
                "  menuLinks[i].style.pointerEvents = 'auto';" +
                "  menuLinks[i].style.cursor = 'pointer';" +
                "  menuLinks[i].style.position = 'relative';" +
                "  menuLinks[i].style.zIndex = '1000';" +
                "}"
            );
            Thread.sleep(2000);
        } catch (Exception e) {
            // Silent fallback
        }
        
        By menuLocator = findFlexTableMenu();
        
        // Debug: Print what locator we're using
        System.out.println("Attempting to click FlexTable menu with locator: " + menuLocator);
        
        // Try direct JavaScript click if regular click fails
        try {
            System.out.println("Trying regular click...");
            clickOnElement(menuLocator);
            System.out.println("Regular click successful!");
        } catch (Exception e) {
            System.out.println("Regular click failed: " + e.getMessage());
            try {
                System.out.println("Trying JavaScript click with found locator...");
                WebElement element = getDriver().findElement(menuLocator);
                JavascriptExecutor js = (JavascriptExecutor) getDriver();
                js.executeScript("arguments[0].click();", element);
                System.out.println("JavaScript click successful!");
            } catch (Exception jsException) {
                System.out.println("JavaScript click failed: " + jsException.getMessage());
                // Last resort - find by text and click
                try {
                    System.out.println("Trying text-based search and click...");
                    List<WebElement> allMenus = getDriver().findElements(By.cssSelector("#adminmenu a, #adminmenu .wp-menu-name"));
                    System.out.println("Found " + allMenus.size() + " menu items");
                    
                    for (WebElement menu : allMenus) {
                        String menuText = menu.getText().trim();
                        System.out.println("Checking menu item: '" + menuText + "'");
                        if (menuText.toLowerCase().contains("flextable") || 
                            menuText.toLowerCase().contains("gswpts") ||
                            menuText.toLowerCase().contains("flex table")) {
                            System.out.println("Found FlexTable menu! Clicking: " + menuText);
                            JavascriptExecutor js = (JavascriptExecutor) getDriver();
                            js.executeScript("arguments[0].click();", menu);
                            System.out.println("Text-based click successful!");
                            return;
                        }
                    }
                    throw new RuntimeException("FlexTable menu not found in any menu items");
                } catch (Exception finalException) {
                    System.out.println("Text-based click failed: " + finalException.getMessage());
                    throw new RuntimeException("Failed to click FlexTable menu after all attempts", finalException);
                }
            }
        }
    }
}
