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

import java.util.ArrayList;
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
    CreatedPageWithFlexTable createdPageWithFlexTable = new CreatedPageWithFlexTable(driver);
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
        if (isCreateButtonVisible) {
            Assert.assertTrue(isCreateButtonVisible, "FlexTable Dashboard did not load correctly");
        }
        else {
            Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableLink));
            Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.existingTableSearchField));
        }
    }


    @Test(priority = 3, description = "Create a New Table Using Google Sheet Input"
    )
    public void verifyNewTableCreationWithGoogleSheet() throws Exception {
        String tableTitle = faker.commerce().productName();
        String tableDescription = faker.lorem().paragraph(1);
        flexTablePluginPage.createNewTableWithGoogleSheet(tableTitle,tableDescription);
        By newlyAddedTableTitle = flexTablePluginPage.getElementThroughTagAndText("h4", tableTitle);
        Assert.assertEquals(flexTablePluginPage.getElementText(newlyAddedTableTitle), tableTitle);

    }

    @Test(priority = 4, description = "Verify Table Display Using Shortcode")
    public void verifyTableDisplayUsingShortcode() throws Exception {
        // 1. Get CSV data from Google Sheets
        List<List<String>> csvData = flexTablePluginPage.getCsvData();

        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);

        // Get the shortcode from the first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        String pageTitle = faker.commerce().productName();
        String PageUrl = wordPressPages.createPageUsingShortCode(pageTitle, shortCode);

            getDriver().get(PageUrl);

            Thread.sleep(3000);
            List<WebElement> nameElements = createdPageWithFlexTable.getElements(createdPageWithFlexTable.NameColumn);
            for(int i=0; i<nameElements.size(); i++) {
                String text = nameElements.get(i).getText();
                Assert.assertEquals(text, csvData.get(i+1).get(0));
            }

            List<WebElement> idElements = createdPageWithFlexTable.getElements(createdPageWithFlexTable.IDColumn);
            for(int i=0; i<idElements.size(); i++) {
                String text = idElements.get(i).getText();
                Assert.assertEquals(text, csvData.get(i+1).get(1));
            }
        }

    @Test(priority = 5, description = "Enable 'Show Table Title' and 'Show Table Description Below Table")
    public void verifyShowTableTableAndShowTableDescriptionDisplayProperly() throws InterruptedException {
        String title = faker.commerce().productName();
        String description = faker.lorem().paragraph(1);
        flexTablePluginPage.createNewTableWithGoogleSheet(title,description);

        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);

        // Get the shortcode from the first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        String PageUrl = wordPressPages.createPageUsingShortCode(title, shortCode);

        String wpURL = getDriver().getCurrentUrl();
        //System.out.println("WP URL: " + wpURL);
        getDriver().get(PageUrl);
        By titleInPage = wordPressPages.getElementThroughTagAndText("h3",title);
        By descriptionInPage = wordPressPages.getElementThroughTagAndText("p",description);
        Assert.assertFalse(wordPressPages.isElementVisible(titleInPage));
        Assert.assertFalse(wordPressPages.isElementVisible(descriptionInPage));
        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();

        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        getDriver().get(baseUrl);

        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
        flexTablePluginPage.sendKeysText(flexTablePluginPage.existingTableSearchField,title);
        By tableEdit = flexTablePluginPage.getTableEditTag(title);
        flexTablePluginPage.clickOnElement(tableEdit);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.tableCustomizationMenu);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.showTitleToggle);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.showDescriptionToggle);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButtonToSaveCustomization);
        getDriver().get(PageUrl);
        By titleInPageVisible = wordPressPages.getElementThroughTagAndText("h3",title);
        By descriptionInPageVisible = wordPressPages.getElementThroughTagAndText("p",description);
        Assert.assertTrue(wordPressPages.isElementVisible(titleInPageVisible));
        Assert.assertTrue(wordPressPages.isElementVisible(descriptionInPageVisible));
    }


    }


