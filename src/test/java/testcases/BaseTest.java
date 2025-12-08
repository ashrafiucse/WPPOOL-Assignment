package testcases;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utilities.DriverSetup;
import utilities.PerformanceMonitor;

public class BaseTest {
    
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    @Parameters({ "browser", "headless", "performanceMonitoring" })
    public void setup(
        @Optional("chrome") String browser,
        @Optional("false") String headless,
        @Optional("false") String performanceMonitoring
    ) {
        // Start performance monitoring
        if (Boolean.parseBoolean(performanceMonitoring)) {
            PerformanceMonitor.startTimer("test_setup");
            PerformanceMonitor.recordMemoryUsage("setup_start");
        }
        
        // Initialize driver with parameterized browser and headless mode
        DriverSetup.initDriver(browser, Boolean.parseBoolean(headless));
        driver = DriverSetup.getDriver();

        // Record setup completion
        if (Boolean.parseBoolean(performanceMonitoring)) {
            PerformanceMonitor.endTimer("test_setup");
            PerformanceMonitor.recordMemoryUsage("setup_complete");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            // Record test completion metrics safely
            PerformanceMonitor.recordMetric("test_result", result.getStatus());
            PerformanceMonitor.recordMetric("test_name", result.getName());
            
            // Get browser info safely
            String browser = DriverSetup.getCurrentBrowser();
            if (browser != null) {
                PerformanceMonitor.recordMetric("browser", browser);
            }
            
            PerformanceMonitor.recordMemoryUsage("test_complete");
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
    
    public void cleanup() {
        // Performance monitoring is silent - no report generation in terminal
        
        DriverSetup.quitDriver(); // Quit browser & clear ThreadLocal
        driver = null;
        
        // Clear performance metrics for this thread
        PerformanceMonitor.clearMetrics();
    }
}