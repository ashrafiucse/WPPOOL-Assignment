package testcases;

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
     * Enhanced headless mode handling for WordPress dashboard elements
     * Call this method after login to ensure elements are properly rendered
     */
    public void handleHeadlessModeIssues() {
        try {
            WebDriver driver = DriverSetup.getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // Wait for page to be fully loaded
            Thread.sleep(3000);
            
            // Comprehensive WordPress menu fixes for headless mode
            js.executeScript(
                "// Force WordPress admin menu to be visible and expanded" +
                "if(document.querySelector('#adminmenu')) {" +
                "  var adminMenu = document.querySelector('#adminmenu');" +
                "  adminMenu.style.display = 'block';" +
                "  adminMenu.style.visibility = 'visible';" +
                "  adminMenu.style.opacity = '1';" +
                "  adminMenu.style.position = 'relative';" +
                "  adminMenu.style.left = '0px';" +
                "  adminMenu.style.width = 'auto';" +
                "}" +
                "if(document.querySelector('#wpwrap')) {" +
                "  document.querySelector('#wpwrap').classList.add('wp-responsive-open');" +
                "  document.querySelector('#wpwrap').classList.remove('wp-responsive');" +
                "}" +
                "if(document.querySelector('#wpcontent')) {" +
                "  document.querySelector('#wpcontent').style.marginLeft = '160px';" +
                "}" +
                "if(document.querySelector('#wpfooter')) {" +
                "  document.querySelector('#wpfooter').style.marginLeft = '160px';" +
                "}" +
                "// Force all menu items to be visible and clickable" +
                "var menuItems = document.querySelectorAll('#adminmenu li');" +
                "for(var i = 0; i < menuItems.length; i++) {" +
                "  menuItems[i].style.display = 'block';" +
                "  menuItems[i].style.visibility = 'visible';" +
                "  menuItems[i].style.opacity = '1';" +
                "  menuItems[i].style.position = 'relative';" +
                "  menuItems[i].style.left = '0px';" +
                "  menuItems[i].style.width = 'auto';" +
                "  menuItems[i].style.height = 'auto';" +
                "  menuItems[i].style.overflow = 'visible';" +
                "}" +
                "// Fix submenu items" +
                "var submenus = document.querySelectorAll('#adminmenu .wp-submenu');" +
                "for(var i = 0; i < submenus.length; i++) {" +
                "  submenus[i].style.display = 'block';" +
                "  submenus[i].style.visibility = 'visible';" +
                "  submenus[i].style.opacity = '1';" +
                "  submenus[i].style.position = 'absolute';" +
                "  submenus[i].style.left = '160px';" +
                "  submenus[i].style.top = '0px';" +
                "  submenus[i].style.zIndex = '9999';" +
                "}" +
                "// Ensure menu links are clickable" +
                "var menuLinks = document.querySelectorAll('#adminmenu a');" +
                "for(var i = 0; i < menuLinks.length; i++) {" +
                "  menuLinks[i].style.pointerEvents = 'auto';" +
                "  menuLinks[i].style.cursor = 'pointer';" +
                "  menuLinks[i].style.position = 'relative';" +
                "  menuLinks[i].style.zIndex = '1000';" +
                "}"
            );
            
            // Additional wait for rendering
            Thread.sleep(2000);
            
            // Force viewport to proper size
            js.executeScript(
                "window.resizeTo(1920, 1080);" +
                "document.body.style.width = '1920px';" +
                "document.body.style.height = '1080px';" +
                "document.documentElement.style.width = '1920px';" +
                "document.documentElement.style.height = '1080px';"
            );
            
            // Final wait for all changes to take effect
            Thread.sleep(1000);
        } catch (Exception ignored) {
            // Silent fallback
        }
    }
    
    public void cleanup() {
        
        DriverSetup.quitDriver(); // Quit browser & clear ThreadLocal
        driver = null;
        
    }
}
