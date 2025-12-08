package testcases.Part_A;

import com.github.javafaker.Faker;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import pages.Part_A.*;
import utilities.DriverSetup;
import utilities.WpCliHelper;
import utilities.WpCliUtils;
import testcases.BaseTest;

import java.util.List;

import static utilities.DriverSetup.getDriver;

public class FlexTablePluginTest extends BaseTest {
    WebDriver driver = getDriver();

    // Initialize page objects
    WordPressLoginPage loginPage = new WordPressLoginPage(driver);
    WordPressDashboardPage wordPressDashboardPage = new WordPressDashboardPage(driver);
    WordPressPluginsPage wordPressPluginsPage = new WordPressPluginsPage(driver);
    FlexTablePluginPage flexTablePluginPage = new FlexTablePluginPage(driver);
    WordPressPages wordPressPages = new WordPressPages(driver);
    Faker faker = new Faker();

    @BeforeMethod
            public void wordPressLogin() {
        // Perform login using environment credentials (handled in doLogin method)
        loginPage.doLogin();
    }

    @Test(priority = 1,description = "Verify FlexTable Plugin Activation Status")
    public void verifyFlexTablePluginActivation() throws InterruptedException {

        // Navigate to plugins page
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.pluginsMenu);
        boolean noPluginsAvailableText = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.noPluginsAreAvailableText);
        boolean flexTableTitleVisible = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.flexTablePluginTitle);
        if (noPluginsAvailableText) {
            wordPressPluginsPage.clickOnElement(wordPressPluginsPage.addPluginButton);
            wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.pluginSearchFieldToAdd,"FlexTable");
            wordPressPluginsPage.clickOnElement(wordPressPluginsPage.flexTablePluginInstallButton);
            Thread.sleep(2000);
            wordPressPluginsPage.clickOnElement(wordPressPluginsPage.installedFlexTablePluginActivateButton);
            wordPressDashboardPage.clickOnElement(wordPressDashboardPage.pluginsMenu);
            wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.installedPluginSearchField,"FlexTable");
            boolean isActivated = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.flexTableDeactivateLink);
            Assert.assertTrue(isActivated);
        }
        else {
            wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.installedPluginSearchField,"FlexTable");
            if (flexTableTitleVisible) {
                boolean isActivated = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.flexTableDeactivateLink);
                if(isActivated) {
                    System.out.println("Plugin is Activated!");
                }
                else {
                    wordPressPluginsPage.clickOnElement(wordPressPluginsPage.flexTableActivateLink);
                    wordPressDashboardPage.clickOnElement(wordPressDashboardPage.pluginsMenu);
                    wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.installedPluginSearchField,"FlexTable");
                    boolean isActivatedFromDeactivated = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.flexTableDeactivateLink);
                    Assert.assertTrue(isActivatedFromDeactivated);
                }
            } else {
                wordPressPluginsPage.clickOnElement(wordPressPluginsPage.addPluginButton);
                wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.pluginSearchFieldToAdd,"FlexTable");
                wordPressPluginsPage.clickOnElement(wordPressPluginsPage.flexTablePluginInstallButton);
                Thread.sleep(15000);
                wordPressPluginsPage.clickOnElement(wordPressPluginsPage.installedFlexTablePluginActivateButton);
                wordPressDashboardPage.clickOnElement(wordPressDashboardPage.pluginsMenu);
                wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.installedPluginSearchField,"FlexTable");
                boolean isActivated = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.flexTableDeactivateLink);
                Assert.assertTrue(isActivated);
            }
        }
    }

    @Test(priority = 2, description = "Navigate to FlexTable Dashboard", 
          dependsOnMethods = {"verifyFlexTablePluginActivation"})
    public void navigateToFlexTableDashboard() {
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
        
        boolean isCreateButtonVisible = flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableButton);
        Assert.assertTrue(isCreateButtonVisible, "FlexTable Dashboard did not load correctly");
    }


    @Test(priority = 3, description = "Create a New Table Using Google Sheet Input"
    )
    public void verifyNewTableCreationWithGoogleSheet() throws Exception {
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
        boolean isExistingTableAvailable = flexTablePluginPage.isElementVisible(flexTablePluginPage.existingTableSearchField);
        if (isExistingTableAvailable) {
            flexTablePluginPage.clickOnElement(flexTablePluginPage.createNewTableLink);

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();

            String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
            flexTablePluginPage.sendKeysText(flexTablePluginPage.googleSheetInputField, googleSheetURL);
            flexTablePluginPage.clickOnElement(flexTablePluginPage.createTableFromUrlButton);

            String tableTitle = faker.commerce().productName();
            String tableDescription = faker.lorem().paragraph(1);
            flexTablePluginPage.sendKeysText(flexTablePluginPage.tableTitleField, tableTitle);
            flexTablePluginPage.sendKeysText(flexTablePluginPage.tableDescriptionField, tableDescription);
            flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButton);
            flexTablePluginPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
            By newlyAddedTableTitle = flexTablePluginPage.getElementThroughTagAndText("h4", tableTitle);
            Assert.assertEquals(flexTablePluginPage.getElementText(newlyAddedTableTitle), tableTitle);
        } else {
            flexTablePluginPage.clickOnElement(flexTablePluginPage.createNewTableButton);

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();

            String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
            flexTablePluginPage.sendKeysText(flexTablePluginPage.googleSheetInputField, googleSheetURL);
            flexTablePluginPage.clickOnElement(flexTablePluginPage.createTableFromUrlButton);

            String tableTitle = faker.commerce().productName();
            String tableDescription = faker.lorem().paragraph(1);

            flexTablePluginPage.sendKeysText(flexTablePluginPage.tableTitleField, tableTitle);
            flexTablePluginPage.sendKeysText(flexTablePluginPage.tableDescriptionField, tableDescription);
            flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButton);
            flexTablePluginPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
            By newlyAddedTableTitle = flexTablePluginPage.getElementThroughTagAndText("h4", tableTitle);
            Assert.assertEquals(flexTablePluginPage.getElementText(newlyAddedTableTitle), tableTitle);
        }
    }

    @Test(priority = 8, description = "Verify Table Display Using Shortcode with WP-CLI")
    public void verifyTableDisplayUsingShortcode() throws Exception {
        
        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
        
        // Get the shortcode from the first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        System.out.println("Short Code = " + shortCode);

        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.pagesMenu);
        wordPressPages.clickOnElement(wordPressPages.addPageButton);
        boolean isModalOpened;
        isModalOpened = wordPressPages.isElementVisible(wordPressPages.modalCloseButton);
        if(isModalOpened) {
            wordPressPages.clickOnElement(wordPressPages.modalCloseButton);
            String pageTitle = faker.commerce().productName();
          //  wordPressPages.sendKeysText(wordPressPages.pageTitleField,pageTitle);
            wordPressPages.typeIntoRichTextEditor(wordPressPages.pageTitleField,pageTitle);
            wordPressPages.clickOnElement(wordPressPages.blockInserterButton);
            wordPressPages.sendKeysText(wordPressPages.blockSearchField,"ShortCode");
            wordPressPages.clickOnElement(wordPressPages.shortCodeBlockToSelect);
            wordPressPages.typeIntoRichTextEditor(wordPressPages.shortCodeInputFieldToCreatePage,shortCode);
            wordPressPages.clickOnElement(wordPressPages.publishButton);
            System.out.println(wordPressPages.isElementVisible(wordPressPages.confirmPublishButton));
            wordPressPages.clickOnElement(wordPressPages.confirmPublishButton);

            String PageUrl = wordPressPages.getElementAttribute(wordPressPages.viewPageButton,"href");
            getDriver().get(PageUrl);
            Thread.sleep(4000);

        }

    }

}
