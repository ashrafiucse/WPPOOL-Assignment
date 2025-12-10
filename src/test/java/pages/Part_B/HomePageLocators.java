package pages.Part_B;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class HomePageLocators extends BasePage {
    public HomePageLocators(WebDriver driver) {
        super(driver);
    }
    public By shopLink = By.xpath("//a[contains(@href,'/shop/')]");
    public By myAccountLink = By.xpath("(//a[contains(@href,'/my-account/')])[1]");
}
