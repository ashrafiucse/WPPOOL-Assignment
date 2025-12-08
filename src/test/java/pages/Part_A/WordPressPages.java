package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class WordPressPages extends BasePage {
    public WordPressPages(WebDriver driver) {
        super(driver);
    }
    public By addPageButton = By.xpath("(//a[contains(text(),'Add Page')])[2]");
    public By pageTitleField = By.xpath("//h1[contains(@class, 'editor-post-title__input') and @contenteditable='true']");
    public By modalCloseButton = By.xpath("//button[@aria-label='Close']");
    public By blockInserterButton = By.xpath("//button[@id=':r1:']");
    public By blockSearchField = By.xpath("//input[@id='components-search-control-0']");
    public By shortCodeBlockToSelect = By.xpath("//span[@data-wp-component='Truncate' and contains(text(),'Shortcode')]");
    public By shortCodeInputFieldToCreatePage = By.xpath("//textarea[@id='blocks-shortcode-input-0']");
    public By publishButton = By.xpath("//button[contains(text(),'Publish')]");
    public By confirmPublishButton = By.xpath("(//button[@aria-disabled='false' and contains(text(),'Publish')])[2]");
    public By viewPageButton = By.xpath("//a[contains(text(),'View Page')]");

}
