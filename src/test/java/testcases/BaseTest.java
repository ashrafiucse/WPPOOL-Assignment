package testcases;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utilities.DriverSetup;

public class BaseTest {
    
    protected WebDriver driver;

@BeforeMethod(alwaysRun = true)
    @Parameters({ "browser", "headless" })
    public void setup(
        @Optional("chrome") String browser,
        @Optional("false") String headless
    ) {
        // Initialize driver with parameterized browser and headless mode
        DriverSetup.initDriver(browser, Boolean.parseBoolean(headless));
        driver = DriverSetup.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            
            // Get browser info safely
            String browser = DriverSetup.getCurrentBrowser();
            if (browser != null) {
            }
            
        } catch (Exception e) {
            // Silently ignore performance monitoring errors
        }

        // Test result logging is handled by TestListener.java
        // No duplicate logging needed here
        cleanup();
    }
    
    /**
     * Handle headless mode issues with WordPress dashboard elements
     * Call this method after login to ensure elements are properly rendered
     */
    public void handleHeadlessModeIssues() {
        try {
            WebDriver driver = DriverSetup.getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // Wait for page to be fully loaded
            Thread.sleep(2000);
            
            // Force WordPress menu to be visible in headless mode
            js.executeScript(
                "if(document.querySelector('#adminmenu')) {" +
                "  document.querySelector('#adminmenu').style.display = 'block';" +
                "}" +
                "if(document.querySelector('#wpwrap')) {" +
                "  document.querySelector('#wpwrap').classList.add('wp-responsive-open');" +
                "}" +
                "// Force all menu items to be visible" +
                "var menuItems = document.querySelectorAll('#adminmenu li');" +
                "for(var i = 0; i < menuItems.length; i++) {" +
                "  menuItems[i].style.display = 'block';" +
                "  menuItems[i].style.visibility = 'visible';" +
                "  menuItems[i].style.opacity = '1';" +
                "}"
            );
            
            // Additional wait for rendering
            Thread.sleep(2000);
        } catch (Exception ignored) {
            // Silent fallback
        }
    }
    
public String getHomepageUrl() {
        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();
        
        // Get WP_URL from environment
        String wpUrl = dotenv.get("WP_URL");
        
        // Trim /wp-admin suffix if present (case-insensitive)
        if (wpUrl != null) {
            return wpUrl.replaceAll("(?i)/wp-admin/?$", "");
        }
        return null;
    }
    
    public String getShopUrl() {
        String homepageUrl = getHomepageUrl();
        return homepageUrl != null ? homepageUrl + "/shop" : null;
    }
    
    public void cleanup() {
        
        DriverSetup.quitDriver(); // Quit browser & clear ThreadLocal
        driver = null;
        
    }
}
