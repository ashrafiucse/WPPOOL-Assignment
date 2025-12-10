package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

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
    public By pagesMenu = By.xpath("//a[contains(@href,'post_type=page')]//div[contains(text(),'Pages')]");
}
