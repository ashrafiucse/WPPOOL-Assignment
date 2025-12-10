package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class CleanTestListener implements ITestListener {
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🚀 STARTING TEST SUITE");
        System.out.println("=".repeat(50));
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String browserInfo = getBrowserInfo();
        
        if (description != null && !description.isEmpty()) {
            System.out.println("\n📋 Running: " + description + " " + browserInfo);
        } else {
            System.out.println("\n📋 Running: " + testName + " " + browserInfo);
        }
        System.out.println("-".repeat(30));
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String browserInfo = getBrowserInfo();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        System.out.println("\n✅ PASSED: " + (description != null ? description : testName) + " " + browserInfo);
        System.out.println("⏱️  Duration: " + (duration / 1000.0) + "s");
        System.out.println("-".repeat(30));
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String browserInfo = getBrowserInfo();
        
        // Show only failure status, no error details
        System.out.println("\n❌ FAILED: " + (description != null ? description : testName) + " " + browserInfo);
        System.out.println("-".repeat(30));
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String browserInfo = getBrowserInfo();
        
        System.out.println("\n⏭️ SKIPPED: " + (description != null ? description : testName) + " " + browserInfo);
        System.out.println("-".repeat(30));
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🏁 TEST SUITE COMPLETED");
        System.out.println("📊 Total Tests: " + context.getAllTestMethods().length);
        System.out.println("✅ Passed: " + context.getPassedTests().size());
        System.out.println("❌ Failed: " + context.getFailedTests().size());
        System.out.println("⏭️ Skipped: " + context.getSkippedTests().size());
        System.out.println("=".repeat(50));
    }
    
    private String getBrowserInfo() {
        try {
            String browser = System.getProperty("browser", "chrome");
            Boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
            return String.format(
                "[%s%s]",
                browser.toUpperCase(),
                headless ? "-HEADLESS" : "-HEADED"
            );
        } catch (Exception e) {
            return "[CHROME-HEADED]";
        }
    }
    
    // Unused methods from interface
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {}
}