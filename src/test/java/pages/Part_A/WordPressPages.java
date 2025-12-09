package pages.Part_A;

import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

import java.lang.InterruptedException;

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

    public String createPageUsingShortCode(String pageTitle, String shortCode) throws InterruptedException {
        // Handle headless mode before clicking Pages menu
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript(
                "// Ensure admin menu is visible and expanded" +
                "var adminMenu = document.querySelector('#adminmenu');" +
                "if(adminMenu) {" +
                "  adminMenu.style.display = 'block';" +
                "  adminMenu.style.visibility = 'visible';" +
                "  adminMenu.style.opacity = '1';" +
                "  adminMenu.style.position = 'relative';" +
                "  adminMenu.style.left = '0px';" +
                "}" +
                "// Ensure all menu items are visible" +
                "var menuItems = document.querySelectorAll('#adminmenu li');" +
                "for(var i = 0; i < menuItems.length; i++) {" +
                "  menuItems[i].style.display = 'block';" +
                "  menuItems[i].style.visibility = 'visible';" +
                "  menuItems[i].style.opacity = '1';" +
                "  menuItems[i].style.position = 'relative';" +
                "  menuItems[i].style.left = '0px';" +
                "}"
            );
            Thread.sleep(1000);
        } catch (Exception e) {
            // Silent fallback
        }
        
        wordPressDashboardPage.clickPagesMenu();
        Thread.sleep(2000);
        
        // Enhanced Add Page button clicking for headless mode
        By alternativeAddPageButton = By.xpath("//a[contains(@href,'post-new.php?post_type=page')]");
        try {
            clickOnElement(addPageButton);
        } catch (Exception e) {
            // Try alternative locators for Add Page button
            try {
                clickOnElement(alternativeAddPageButton);
            } catch (Exception e2) {
                // Try JavaScript click
                JavascriptExecutor js = (JavascriptExecutor) getDriver();
                js.executeScript("arguments[0].click();", 
                    getDriver().findElement(addPageButton));
            }
        }
        
        Thread.sleep(2000);
        
        boolean isModalOpened;
        isModalOpened = isElementVisible(modalCloseButton);
        if (isModalOpened) {
            clickOnElement(modalCloseButton);
            Thread.sleep(1000);
        }
        
        typeIntoRichTextEditor(pageTitleField, pageTitle);
        Thread.sleep(1000);
        
        clickOnElement(blockInserterButton);
        Thread.sleep(2000);
        
        sendKeysText(blockSearchField, "ShortCode");
        Thread.sleep(1000);
        
        clickOnElement(shortCodeBlockToSelect);
        Thread.sleep(1000);
        
        typeIntoRichTextEditor(shortCodeInputFieldToCreatePage, shortCode);
        Thread.sleep(1000);
        
        clickOnElement(publishButton);
        Thread.sleep(2000);
        
        clickOnElement(confirmPublishButton);
        Thread.sleep(3000);

        String PageUrl = getElementAttribute(viewPageButton, "href");
        return PageUrl;
    }
}