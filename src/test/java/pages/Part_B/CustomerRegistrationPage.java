package pages.Part_B;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class CustomerRegistrationPage extends BasePage {
    public CustomerRegistrationPage(WebDriver driver) {
        super(driver);
    }
    public By registrationEmailInputField = By.xpath("//input[@id='reg_email']");
    public By registrationPasswordInputField = By.xpath("//input[@id='reg_password']");
    public By registerButton = By.xpath("//button[contains(text(),'Register')]");
}
