package pages.Part_A;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

import static utilities.DriverSetup.getDriver;

public class WordPressLoginPage extends BasePage {
    public WordPressLoginPage(WebDriver driver) {
        super(driver);
    }
    public By adminUserNameField = By.xpath("//input[@id='user_login']");
    public By adminPasswordField = By.xpath("//input[@id='user_pass']");
    public By loginButton = By.xpath("//input[@id='wp-submit']");

    public void doLogin() {
        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();
        
        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        getDriver().get(baseUrl);
        
        // Perform login using credentials from environment
        String username = dotenv.get("WP_USER");
        String password = dotenv.get("WP_PASS");
        
        sendKeysText(adminUserNameField, username);
        sendKeysText(adminPasswordField, password);
        clickOnElement(loginButton);
    }
}
