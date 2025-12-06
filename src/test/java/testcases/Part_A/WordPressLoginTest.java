package testcases.Part_A;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;
import utilities.DriverSetup;
import pages.Part_A.WordPressLoginPage;
import pages.Part_A.WordPressDashboardPage;

public class WordPressLoginTest {
    
    @BeforeMethod
    public void setUp() {
        DriverSetup.initDriver();
    }
    
    @AfterMethod
    public void tearDown() {
        DriverSetup.quitDriver();
    }
    
    @Test(description = "Verify WordPress login")
    public void verifyWordpressLogin() {
        WebDriver driver = DriverSetup.getDriver();
        
        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();
        
        // Initialize page objects
        WordPressLoginPage loginPage = new WordPressLoginPage(driver);
        WordPressDashboardPage dashboardPage = new WordPressDashboardPage(driver);
        
        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        
        driver.get(baseUrl);
        
        // Perform login using credentials from environment
        String username = dotenv.get("WP_USER");
        String password = dotenv.get("WP_PASS");
        
        loginPage.doLogin(username, password);
        
        // Verify dashboard page locators are visible
        boolean isHomeButtonVisible = dashboardPage.isElementVisible(dashboardPage.dashboardHomeButton);
        boolean isPluginsMenuVisible = dashboardPage.isElementVisible(dashboardPage.pluginsMenu);
        
        // Assert that dashboard elements are visible
        Assert.assertTrue(isHomeButtonVisible, "Dashboard Home button should be visible after login");
        Assert.assertTrue(isPluginsMenuVisible, "Plugins menu should be visible after login");
    }
}
