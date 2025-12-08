package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    private long suiteStartTime = 0;
    private String currentTestName = "";
    private String currentBrowserInfo = "";

    @Override
    public void onStart(ITestContext context) {
        suiteStartTime = System.currentTimeMillis();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 STARTING TEST SUITE");
        System.out.println("=".repeat(60));
    }

    @Override
    public void onTestStart(ITestResult result) {
        totalTests++;
        currentTestName = getTestDisplayName(result);
        currentBrowserInfo = getBrowserInfo();
        
        // Show starting test on new line
        System.out.println("\n📋 Running: " + currentTestName + " " + currentBrowserInfo);
        System.out.flush();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        long duration = result.getEndMillis() - result.getStartMillis();
        
        // Move cursor up one line and replace "Running" with "Passed"
        System.out.print("\033[F"); // Move cursor up one line
        System.out.print("\r✅ Passed: " + currentTestName + " " + currentBrowserInfo);
        System.out.printf(" (%.1fs)\n", duration / 1000.0);
        System.out.flush();
        
        // Reset current test info
        currentTestName = "";
        currentBrowserInfo = "";
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        long duration = result.getEndMillis() - result.getStartMillis();
        
        // Move cursor up one line and replace "Running" with "Failed"
        System.out.print("\033[F"); // Move cursor up one line
        System.out.print("\r❌ Failed: " + currentTestName + " " + currentBrowserInfo);
        System.out.printf(" (%.1fs)\n", duration / 1000.0);
        System.out.flush();
        
        // Suppress error details - only show status
        // Reset current test info
        currentTestName = "";
        currentBrowserInfo = "";
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        
        // Move cursor up one line and replace "Running" with "Skipped"
        System.out.print("\033[F"); // Move cursor up one line
        System.out.print("\r⏭️ Skipped: " + currentTestName + " " + currentBrowserInfo);
        System.out.println();
        System.out.flush();
        
        // Reset current test info
        currentTestName = "";
        currentBrowserInfo = "";
    }

    @Override
    public void onFinish(ITestContext context) {
        long totalTime = System.currentTimeMillis() - suiteStartTime;
        double totalTimeSeconds = totalTime / 1000.0;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🏁 TEST SUITE COMPLETED");
        System.out.println("=".repeat(60));
        System.out.printf("📊 Total Tests: %d\n", totalTests);
        System.out.printf("✅ Passed: %d\n", passedTests);
        System.out.printf("❌ Failed: %d\n", failedTests);
        System.out.printf("⏭️ Skipped: %d\n", skippedTests);
        System.out.printf("⏱️ Total Duration: %.1fs\n", totalTimeSeconds);
        
        if (failedTests == 0) {
            System.out.println("🎉 ALL TESTS PASSED!");
        } else {
            System.out.println("⚠️ SOME TESTS FAILED");
        }
        System.out.println("=".repeat(60));
    }

    private String getTestDisplayName(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String testName = result.getTestName();
        
        // Use test name if available, otherwise use method name
        if (testName != null && !testName.isEmpty() && !testName.equals(methodName)) {
            return testName;
        }
        
        // Format method name for better readability
        return methodName.replaceAll("([A-Z])", " $1").trim();
    }

    private String getBrowserInfo() {
        try {
            // First try to get from DriverSetup ThreadLocal
            String browser = DriverSetup.getCurrentBrowser();
            Boolean headless = DriverSetup.isHeadless();
            if (browser != null && headless != null) {
                return String.format(
                    "[%s%s]",
                    browser.toUpperCase(),
                    headless ? "-HEADLESS" : "-HEADED"
                );
            }
            
            // Fallback to system properties
            browser = System.getProperty("browser", "chrome");
            headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
            return String.format(
                "[%s%s]",
                browser.toUpperCase(),
                headless ? "-HEADLESS" : "-HEADED"
            );
        } catch (Exception e) {
            // Final fallback - return default
            return "[CHROME-HEADED]";
        }
    }
}