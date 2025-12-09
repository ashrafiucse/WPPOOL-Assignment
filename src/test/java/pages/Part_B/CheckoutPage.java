package pages.Part_B;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class CheckoutPage extends BasePage {
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }
    public By emailInputField = By.xpath("//input[@id='email']");
    public By firstNameInputField = By.xpath("//input[@id='shipping-first_name']");
    public By lastNameInputField = By.xpath("//input[@id='shipping-last_name']");
    public By addressInputField = By.xpath("//input[@id='shipping-address_1']");
    public By cityInputField = By.xpath("//input[@id='shipping-city']");
    public By cashOnDelivery = By.xpath("//input[@id='radio-control-wc-payment-method-options-cod']");
    public By directBankTransfer = By.xpath("//input[@id='radio-control-wc-payment-method-options-bacs']");
    public By placeOrderButton = By.xpath("//div[contains(text(),'Place Order')]");
    public By orderReceivedMsg = By.xpath("//p[contains(text(),'Thank you. Your order has been received.')]");
    public By verifyEmailAddress = By.xpath("//input[@id='verify-email']");
    public By confirmEmailAndViewOrderButton = By.xpath("//input[@id='verify-email-submit']");
    public By orderId = By.xpath("(//ul//li//span[2])[1]");
}
