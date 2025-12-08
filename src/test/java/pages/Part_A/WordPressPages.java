package pages.Part_A;

import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

import static utilities.DriverSetup.getDriver;

public class WordPressPages extends BasePage {
    public WordPressPages(WebDriver driver) {
        super(driver);
    }

    WordPressDashboardPage wordPressDashboardPage = new WordPressDashboardPage(getDriver());
    Faker faker = new Faker();
    public By addPageButton = By.xpath("(//a[contains(text(),'Add Page')])[2]");
    public By pageTitleField = By.xpath("//h1[contains(@class, 'editor-post-title__input') and @contenteditable='true']");
    public By modalCloseButton = By.xpath("//button[@aria-label='Close']");
    public By blockInserterButton = By.xpath("//button[@id=':r1:']");
    public By blockSearchField = By.xpath("//input[@id='components-search-control-0']");
    public By shortCodeBlockToSelect = By.xpath("//button[contains(@class, 'editor-block-list-item-shortcode')]");
    public By shortCodeInputFieldToCreatePage = By.xpath("//textarea[@id='blocks-shortcode-input-0']");
    public By publishButton = By.xpath("//button[contains(text(),'Publish')]");
    public By confirmPublishButton = By.xpath("(//button[@aria-disabled='false' and contains(text(),'Publish')])[2]");
    public By viewPageButton = By.xpath("//a[contains(text(),'View Page')]");
    public By existingPageSearchButton = By.xpath("//input[@id='post-search-input']");

    public String createPageUsingShortCode(String pageTitle, String shortCode) {
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.pagesMenu);
        clickOnElement(addPageButton);
        boolean isModalOpened;
        isModalOpened = isElementVisible(modalCloseButton);
        if (isModalOpened) {
            clickOnElement(modalCloseButton);
            typeIntoRichTextEditor(pageTitleField, pageTitle);
            clickOnElement(blockInserterButton);
            sendKeysText(blockSearchField, "ShortCode");
            clickOnElement(shortCodeBlockToSelect);
            typeIntoRichTextEditor(shortCodeInputFieldToCreatePage, shortCode);
            clickOnElement(publishButton);
            clickOnElement(confirmPublishButton);

            String PageUrl = getElementAttribute(viewPageButton, "href");
            return PageUrl;
        }
        else {
            typeIntoRichTextEditor(pageTitleField, pageTitle);
            clickOnElement(blockInserterButton);
            sendKeysText(blockSearchField, "ShortCode");
            clickOnElement(shortCodeBlockToSelect);
            typeIntoRichTextEditor(shortCodeInputFieldToCreatePage, shortCode);
            clickOnElement(publishButton);
            clickOnElement(confirmPublishButton);

            String PageUrl = getElementAttribute(viewPageButton, "href");
            return PageUrl;
        }
    }
}
