package pages.Part_B;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class WooCommercePage extends BasePage {
    public WooCommercePage(WebDriver driver) {
        super(driver);
    }
    public By ordersMenu = By.xpath("//a[contains(@href,'page=wc-orders') and contains(text(),'Orders')]");
    public By orderSearchField = By.xpath("//input[@id='orders-search-input-search-input']");
    public By totalOrder = By.xpath("//tr[td[@class='label' and contains(normalize-space(), 'Order Total')]]//td[@class='total']//span[@class='woocommerce-Price-amount amount']");
}
