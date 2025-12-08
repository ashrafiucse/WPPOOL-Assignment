package testcases.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.Assert;
import pages.Part_A.WordPressLoginPage;
import utilities.DriverSetup;

import java.time.Duration;

import static utilities.DriverSetup.getDriver;

public class SimpleAlpineJsDiagnosticTest {
    WebDriver driver;

    @Test(description = "Simple Alpine.js Diagnosis")
    public void diagnoseAlpineJsIssues() {
        // Initialize driver
        DriverSetup.initDriver("chrome", true);
        driver = DriverSetup.getDriver();
        
        try {
            // Perform login
            WordPressLoginPage loginPage = new WordPressLoginPage(driver);
            loginPage.doLogin();
            
            // Wait for dashboard to load
            Thread.sleep(5000);
            
            // Check what JavaScript frameworks are available
            String availableFrameworks = (String) ((JavascriptExecutor) driver)
                .executeScript(
                    "var frameworks = [];" +
                    "if (typeof window.Alpine !== 'undefined') frameworks.push('Alpine.js');" +
                    "if (typeof window.jQuery !== 'undefined') frameworks.push('jQuery');" +
                    "if (typeof window.React !== 'undefined') frameworks.push('React');" +
                    "if (typeof window.Vue !== 'undefined') frameworks.push('Vue.js');" +
                    "if (typeof window.angular !== 'undefined') frameworks.push('Angular');" +
                    "return frameworks.join(', ');"
                );
            System.out.println("Available Frameworks: " + availableFrameworks);
            
            // Check Alpine.js specifically
            Boolean alpineAvailable = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return typeof window.Alpine !== 'undefined'");
            System.out.println("Alpine.js Available: " + alpineAvailable);
            
            // Check sidebar visibility
            Boolean sidebarVisible = false;
            try {
                WebElement sidebar = driver.findElement(By.cssSelector("#adminmenuwrap"));
                sidebarVisible = sidebar.isDisplayed() && 
                              sidebar.getSize().getWidth() > 0 &&
                              !sidebar.getCssValue("visibility").equals("hidden");
                System.out.println("Sidebar Visible: " + sidebarVisible);
                System.out.println("Sidebar Width: " + sidebar.getSize().getWidth());
                System.out.println("Sidebar CSS Display: " + sidebar.getCssValue("display"));
                System.out.println("Sidebar CSS Visibility: " + sidebar.getCssValue("visibility"));
            } catch (Exception e) {
                System.out.println("Sidebar Not Found: " + e.getMessage());
            }
            
            // Check for menu items
            try {
                WebElement pluginsMenu = driver.findElement(By.cssSelector("a[href='plugins.php']"));
                boolean pluginsMenuVisible = pluginsMenu.isDisplayed();
                System.out.println("Plugins Menu Visible: " + pluginsMenuVisible);
                
            } catch (Exception e) {
                System.out.println("Menu Items Check Failed: " + e.getMessage());
            }
            
            // Print summary
            System.out.println("\n=== DIAGNOSTIC SUMMARY ===");
            System.out.println("Available Frameworks: " + availableFrameworks);
            System.out.println("Alpine.js Available: " + alpineAvailable);
            System.out.println("Sidebar Initially Visible: " + sidebarVisible);
            System.out.println("Headless Mode: " + DriverSetup.isHeadless());
            
            // Check if this is actually an Alpine.js issue
            if (DriverSetup.isHeadless() && !sidebarVisible) {
                System.out.println("⚠️  CONFIRMED: Sidebar visibility issue in headless mode");
                System.out.println("This appears to be a CSS/viewport issue, not Alpine.js specific");
            }
            
            if (DriverSetup.isHeadless() && sidebarVisible) {
                System.out.println("✅ Sidebar is visible in headless mode - no Alpine.js issue detected");
            }
            
            // Basic assertions
            Assert.assertNotNull(availableFrameworks, "Should detect some frameworks");
            Assert.assertTrue(sidebarVisible, "Sidebar should be visible after login");
            
        } catch (Exception e) {
            System.err.println("Diagnostic test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Diagnostic failed: " + e.getMessage());
        } finally {
            DriverSetup.quitDriver();
        }
    }
}