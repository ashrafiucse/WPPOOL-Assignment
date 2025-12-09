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
        WebDriver driver = DriverSetup.getDriver();
        
        // Initialize page objects
        WordPressLoginPage loginPage = new WordPressLoginPage(driver);
        WordPressDashboardPage dashboardPage = new WordPressDashboardPage(driver);
        
        // Perform login using environment credentials (handled in doLogin method)
        loginPage.doLogin();
        
        // Handle headless mode rendering issues
        handleHeadlessModeIssues();
        
        // Verify dashboard page locators are visible with enhanced checks for headless mode
        boolean isHomeButtonVisible = dashboardPage.isDashboardHomeVisible();
        boolean isPluginsMenuVisible = dashboardPage.isPluginsMenuVisible();
        
        // Assert that dashboard elements are visible
        Assert.assertTrue(isHomeButtonVisible, "Dashboard Home button should be visible after login");
        Assert.assertTrue(isPluginsMenuVisible, "Plugins menu should be visible after login");
    }
}
