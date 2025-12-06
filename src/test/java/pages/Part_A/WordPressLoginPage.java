package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class WordPressLoginPage extends BasePage {
    public WordPressLoginPage(WebDriver driver) {
        super(driver);
    }
    public By adminUserNameField = By.xpath("//input[@id='user_login']");
    public By adminPasswordField = By.xpath("//input[@id='user_pass']");
    public By loginButton = By.xpath("//input[@id='wp-submit']");

    public void doLogin(String userName, String password) {
        sendKeysText(adminUserNameField, userName);
        sendKeysText(adminPasswordField, password);
        clickOnElement(loginButton);
    }
}
