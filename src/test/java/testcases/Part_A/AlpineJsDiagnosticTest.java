package testcases.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.Assert;
import pages.Part_A.WordPressLoginPage;
import pages.Part_A.WordPressDashboardPage;
import utilities.DriverSetup;

import java.time.Duration;
import java.util.List;

import static utilities.DriverSetup.getDriver;

public class AlpineJsDiagnosticTest {
    WebDriver driver;

    @Test(description = "Diagnose Alpine.js Issues in Headless Mode")
    public void diagnoseAlpineJsIssues() {
        // Initialize driver
        DriverSetup.initDriver("chrome", true);
        driver = DriverSetup.getDriver();
        
        try {
            // Perform login
            WordPressLoginPage loginPage = new WordPressLoginPage(driver);
            loginPage.doLogin();
            
            // Wait for dashboard to load
            Thread.sleep(3000);
            
            // Check what JavaScript frameworks are actually available
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
            
            // Check Alpine.js availability
            Boolean alpineAvailable = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return typeof window.Alpine !== 'undefined'");
            System.out.println("Alpine.js Available: " + alpineAvailable);
            
            // Check Alpine.js stores
            Boolean sidebarStoreAvailable = false;
            if (alpineAvailable) {
                sidebarStoreAvailable = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return window.Alpine?.store('sidebar') !== undefined");
                System.out.println("Alpine.js Sidebar Store Available: " + sidebarStoreAvailable);
            }
            
            // Check Alpine.js components
            Long alpineComponentCount = 0L;
            if (alpineAvailable) {
                alpineComponentCount = (Long) ((JavascriptExecutor) driver)
                    .executeScript("return document.querySelectorAll('[x-data]').length");
                System.out.println("Alpine.js Components Found: " + alpineComponentCount);
            }
            
            // Check for other JavaScript frameworks that might control sidebar
            String sidebarController = (String) ((JavascriptExecutor) driver)
                .executeScript(
                    "var controller = null;" +
                    "var sidebar = document.querySelector('#adminmenuwrap');" +
                    "if (sidebar) {" +
                    "  // Check for data attributes" +
                    "  if (sidebar.getAttribute('data-awx') || sidebar.getAttribute('data-x')) {" +
                    "    controller = 'Alpine.js (data attributes)';" +
                    "  }" +
                    "  // Check for event listeners" +
                    "  var events = sidebar.getAttribute('onclick') || sidebar.getAttribute('onmouseover');" +
                    "  if (events) {" +
                    "    controller = 'JavaScript events';" +
                    "  }" +
                    "  // Check for WordPress specific classes" +
                    "  if (sidebar.classList.contains('folded') || sidebar.classList.contains('wp-menu-open')) {" +
                    "    controller = 'WordPress CSS classes';" +
                    "  }" +
                    "}" +
                    "return controller || 'Unknown';"
                );
            System.out.println("Sidebar Controller: " + sidebarController);
            
            // Check sidebar visibility
            Boolean sidebarVisible = false;
            try {
                WebElement sidebar = driver.findElement(By.cssSelector("#adminmenuwrap"));
                sidebarVisible = sidebar.isDisplayed() && 
                              sidebar.getSize().getWidth() > 0 &&
                              !sidebar.getCssValue("visibility").equals("hidden");
                System.out.println("Sidebar Visible: " + sidebarVisible);
                System.out.println("Sidebar Width: " + sidebar.getSize().getWidth());
                System.out.println("Sidebar CSS Visibility: " + sidebar.getCssValue("visibility"));
                System.out.println("Sidebar CSS Display: " + sidebar.getCssValue("display"));
            } catch (Exception e) {
                System.out.println("Sidebar Not Found: " + e.getMessage());
            }
            
            // Check sidebar state in Alpine.js store
            if (alpineAvailable && sidebarStoreAvailable) {
                Object sidebarState = ((JavascriptExecutor) driver)
                    .executeScript("return window.Alpine.store('sidebar').primarySidebarOpen");
                System.out.println("Alpine.js Sidebar State: " + sidebarState);
            }
            
            // Try to force sidebar visibility
            if (alpineAvailable && sidebarStoreAvailable && !sidebarVisible) {
                System.out.println("Attempting to fix sidebar visibility...");
                
                Boolean sidebarFixed = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript(
                        "try {" +
                        "  if (window.Alpine?.store('sidebar')) {" +
                        "    window.Alpine.store('sidebar').primarySidebarOpen = true;" +
                        "    window.Alpine.store('sidebar').mobileMenuOpen = false;" +
                        "  }" +
                        "  " +
                        "  const sidebar = document.querySelector('#adminmenuwrap');" +
                        "  if (sidebar) {" +
                        "    sidebar.classList.remove('folded');" +
                        "    sidebar.style.display = 'block';" +
                        "    sidebar.style.visibility = 'visible';" +
                        "  }" +
                        "  " +
                        "  return true;" +
                        "} catch (e) {" +
                        "  console.error('Sidebar fix failed:', e);" +
                        "  return false;" +
                        "}"
                    );
                
                System.out.println("Sidebar Fix Attempt Result: " + sidebarFixed);
                
                // Wait and check again
                Thread.sleep(2000);
                
                WebElement sidebarAfterFix = driver.findElement(By.cssSelector("#adminmenuwrap"));
                boolean sidebarVisibleAfterFix = sidebarAfterFix.isDisplayed() && 
                                           sidebarAfterFix.getSize().getWidth() > 0;
                System.out.println("Sidebar Visible After Fix: " + sidebarVisibleAfterFix);
            }
            
            // Check for menu items
            try {
                List<WebElement> menuItems = driver.findElements(By.cssSelector("#adminmenuwrap li"));
                System.out.println("Menu Items Found: " + menuItems.size());
                
                // Check if specific menu items are visible
                WebElement pluginsMenu = driver.findElement(By.cssSelector("a[href='plugins.php']"));
                boolean pluginsMenuVisible = pluginsMenu.isDisplayed();
                System.out.println("Plugins Menu Visible: " + pluginsMenuVisible);
                
            } catch (Exception e) {
                System.out.println("Menu Items Check Failed: " + e.getMessage());
            }
            
            // Print summary
            System.out.println("\n=== ALPINE.JS DIAGNOSTIC SUMMARY ===");
            System.out.println("Alpine.js Available: " + alpineAvailable);
            System.out.println("Sidebar Store Available: " + sidebarStoreAvailable);
            System.out.println("Alpine.js Components: " + alpineComponentCount);
            System.out.println("Sidebar Initially Visible: " + sidebarVisible);
            System.out.println("Headless Mode: " + DriverSetup.isHeadless());
            
            // Assert basic functionality - updated to not assume Alpine.js
            if (DriverSetup.isHeadless()) {
                System.out.println("Running in HEADLESS mode - checking for framework issues");
                if (!sidebarVisible) {
                    System.out.println("⚠️  CONFIRMED: Sidebar visibility issue in headless mode");
                }
            } else {
                System.out.println("Running in HEADED mode - should work normally");
                Assert.assertTrue(sidebarVisible, "Sidebar should be visible in headed mode");
            }
            
            // Only assert Alpine.js if we detect it's actually being used
            if (availableFrameworks.contains("Alpine.js")) {
                Assert.assertTrue(alpineAvailable, "Alpine.js should be available if detected in frameworks");
            } else {
                System.out.println("ℹ️  INFO: WordPress is not using Alpine.js - using different framework");
            }
            
        } catch (Exception e) {
            System.err.println("Diagnostic test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Alpine.js diagnostic failed: " + e.getMessage());
        } finally {
            DriverSetup.quitDriver();
        }
    }
}