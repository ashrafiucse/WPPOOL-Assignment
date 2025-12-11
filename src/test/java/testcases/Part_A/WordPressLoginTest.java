package testcases.Part_A;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.Assert;
import utilities.DriverSetup;
import pages.Part_A.WordPressLoginPage;
import pages.Part_A.WordPressDashboardPage;
import testcases.BaseTest;

public class WordPressLoginTest extends BaseTest {
    
@Test(description = "Verify WordPress Login Functionality")
    public void verifyWordpressLogin() {
        System.out.println("[DEBUG] ===== STARTING WORDPRESS LOGIN TEST =====");
        WebDriver driver = DriverSetup.getDriver();
        System.out.println("[DEBUG] Driver initialized");

        // Initialize page objects
        WordPressLoginPage loginPage = new WordPressLoginPage(driver);
        WordPressDashboardPage dashboardPage = new WordPressDashboardPage(driver);
        System.out.println("[DEBUG] Page objects initialized");
        
        // Perform login using environment credentials (handled in doLogin method)
        System.out.println("[DEBUG] Performing WordPress login");
        loginPage.doLogin();
        System.out.println("[DEBUG] Login completed");

        // Handle headless mode rendering issues
        System.out.println("[DEBUG] Handling headless mode rendering issues");
        handleHeadlessModeIssues();
        handleHeadlessModeIssues();
        handleHeadlessModeIssues();

        // Verify dashboard page locators are visible with enhanced checks for headless mode
        System.out.println("[DEBUG] Verifying dashboard elements");
        boolean isHomeButtonVisible = dashboardPage.isDashboardHomeVisible();
        boolean isPluginsMenuVisible = dashboardPage.isPluginsMenuVisible();
        System.out.println("[DEBUG] Home button visible: " + isHomeButtonVisible);
        System.out.println("[DEBUG] Plugins menu visible: " + isPluginsMenuVisible);
        
        // Assert that dashboard elements are visible
        Assert.assertTrue(isHomeButtonVisible, "Dashboard Home button should be visible after login");
        Assert.assertTrue(isPluginsMenuVisible, "Plugins menu should be visible after login");
    }
}
