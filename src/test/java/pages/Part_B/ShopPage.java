package pages.Part_B;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class ShopPage extends BasePage {
    public ShopPage(WebDriver driver) {
        super(driver);
    }
    public By addToCartButton = By.xpath("(//span[contains(text(),'Add to cart')])[1]");
    public By viewCartButton = By.xpath("//a[contains(@href,'/cart/') and contains(text(),'View cart')]");
    public By productPrice = By.xpath("//span[contains(@class,'price')]//span[contains(@class,'wc-block-components-product-price__value')]");
    public By ShippingCharge = By.xpath("//span[text()='Flat rate']/following-sibling::span[contains(@class, 'wc-block-components-totals-item__value')]");
    public By taxAmount = By.xpath("//span[text()='Tax']/following-sibling::span[contains(@class, 'wc-block-components-totals-item__value')]");
    public By totalPrice = By.xpath("//span[text()='Estimated total']/following-sibling::div//span[contains(@class, 'wc-block-formatted-money-amount')]");
    public By proceedToCheckoutButton = By.xpath("//div[contains(text(),'Proceed to Checkout')]");
    /**
     * Extracts price text from element and cleans currency characters
     * @param priceLocator By locator for the price element
     * @return cleaned price as float, or 0 if element doesn't exist
     */
    public float extractAndCleanPrice(By priceLocator) {
        try {
            // Check if element exists and is visible
            if (!isElementVisible(priceLocator)) {
                return 0.0f;
            }
            
            String rawPriceText = getElementText(priceLocator);
            
            // Clean currency characters, commas, spaces - keep only numbers and decimal point
            String cleanPrice = rawPriceText.replaceAll("[^0-9.]", "");
            
            if (cleanPrice.isEmpty()) {
                return 0.0f;
            }
            
            return Float.parseFloat(cleanPrice);
            
        } catch (Exception e) {
            // Return 0 if any error occurs during extraction
            return 0.0f;
        }
    }

}
