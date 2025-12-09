package pages.Part_B;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class MyAccountsPage extends BasePage {
    public MyAccountsPage(WebDriver driver) {
        super(driver);
    }
    public By myOrdersMenu = By.xpath("//a[contains(@href,'my-account/orders/') and contains(text(),'Orders')]");
    public By totalAmount = By.xpath("//tr[th[contains(text(),'Total:')]]//span[@class='woocommerce-Price-amount amount']");
    public By logOutButton = By.xpath("//a[contains(@href,'my-account/customer-logout') and contains(text(),'Log out')]");
}
