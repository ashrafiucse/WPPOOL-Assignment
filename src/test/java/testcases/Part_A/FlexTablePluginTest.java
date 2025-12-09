package testcases.Part_A;

import com.github.javafaker.Faker;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        
        // Handle headless mode rendering issues after login
        handleHeadlessModeIssues();
    }

    @Test(priority = 1, description = "Verify FlexTable Plugin Activation Status")
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

    @Test(priority = 2, description = "Navigate to FlexTable Dashboard")
    public void navigateToFlexTableDashboard() {
        wordPressDashboardPage.clickFlexTableMenu();

        boolean isCreateButtonVisible = flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableButton);
        if (isCreateButtonVisible) {
            Assert.assertTrue(isCreateButtonVisible, "FlexTable Dashboard did not load correctly");
        }
        else {
            Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableLink));
            Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.existingTableSearchField));
        }
    }

    @Test(priority = 3, description = "Create a New Table Using Google Sheet Input")
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
        wordPressDashboardPage.clickFlexTableMenu();

        // Get shortcode from first table
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

    @Test(priority = 5, description = "Enable 'Show Table Title' and 'Show Table Description Below Table'")
    public void verifyShowTableTableAndShowTableDescriptionDisplayProperly() throws InterruptedException {
        String title = faker.commerce().productName();
        String description = faker.lorem().paragraph(1);
        flexTablePluginPage.createNewTableWithGoogleSheet(title,description);

        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickFlexTableMenu();

        // Get shortcode from first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        String PageUrl = wordPressPages.createPageUsingShortCode(title, shortCode);

        getDriver().get(PageUrl);
        
        // Handle headless mode rendering issues
        handleHeadlessModeIssues();
        Thread.sleep(2000);
        
        By titleInPage = wordPressPages.getElementThroughTagAndText("h3",title);
        By descriptionInPage = wordPressPages.getElementThroughTagAndText("p",description);
        
        // Enhanced visibility checks for headless mode
        boolean titleInitiallyVisible = wordPressPages.isElementVisibleHeadless(titleInPage);
        boolean descriptionInitiallyVisible = wordPressPages.isElementVisibleHeadless(descriptionInPage);
        
        System.out.println("Title initially visible: " + titleInitiallyVisible);
        System.out.println("Description initially visible: " + descriptionInitiallyVisible);
        
        Assert.assertFalse(titleInitiallyVisible, "Title should not be visible initially");
        Assert.assertFalse(descriptionInitiallyVisible, "Description should not be visible initially");
        
        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();

        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        getDriver().get(baseUrl);

        // Handle headless mode after navigation
        handleHeadlessModeIssues();
        Thread.sleep(2000);

        wordPressDashboardPage.clickFlexTableMenu();
        Thread.sleep(2000);
        
        flexTablePluginPage.sendKeysText(flexTablePluginPage.existingTableSearchField,title);
        Thread.sleep(1000);
        
        By tableEdit = flexTablePluginPage.getTableEditTag(title);
        flexTablePluginPage.clickOnElement(tableEdit);
        Thread.sleep(3000);
        
        // Handle headless mode for toggle interactions
        handleHeadlessModeIssues();
        Thread.sleep(2000);
        
        flexTablePluginPage.clickOnElement(flexTablePluginPage.tableCustomizationMenu);
        Thread.sleep(2000);
        
        // Enhanced toggle clicking for headless mode
        try {
            flexTablePluginPage.clickOnElement(flexTablePluginPage.showTitleToggle);
            Thread.sleep(1000);
            flexTablePluginPage.clickOnElement(flexTablePluginPage.showDescriptionToggle);
            Thread.sleep(1000);
        } catch (Exception e) {
            // Try JavaScript click if regular click fails
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].click();", 
                getDriver().findElement(flexTablePluginPage.showTitleToggle));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", 
                getDriver().findElement(flexTablePluginPage.showDescriptionToggle));
            Thread.sleep(1000);
        }
        
        flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButtonToSaveCustomization);
        Thread.sleep(3000);
        
        getDriver().get(PageUrl);
        
        // Handle headless mode for final verification
        handleHeadlessModeIssues();
        Thread.sleep(3000);
        
        By titleInPageVisible = wordPressPages.getElementThroughTagAndText("h3",title);
        By descriptionInPageVisible = wordPressPages.getElementThroughTagAndText("p",description);
        
        // Enhanced visibility checks with multiple attempts
        boolean titleFinallyVisible = false;
        boolean descriptionFinallyVisible = false;
        
        for (int i = 0; i < 5; i++) {
            titleFinallyVisible = wordPressPages.isElementVisibleHeadless(titleInPageVisible);
            descriptionFinallyVisible = wordPressPages.isElementVisibleHeadless(descriptionInPageVisible);
            
            if (titleFinallyVisible && descriptionFinallyVisible) {
                break;
            }
            
            Thread.sleep(2000);
            handleHeadlessModeIssues();
        }
        
        System.out.println("Title finally visible: " + titleFinallyVisible);
        System.out.println("Description finally visible: " + descriptionFinallyVisible);
        
        Assert.assertTrue(titleFinallyVisible, "Title should be visible after enabling");
        Assert.assertTrue(descriptionFinallyVisible, "Description should be visible after enabling");
    }

    @Test(priority = 6, description = "Enable Entry Info & Pagination")
    public void verifyEntryInfoDisplayCorrectlyAndPaginationFunctional() throws InterruptedException {
        String title = faker.commerce().productName();
        String description = faker.lorem().paragraph(1);
        flexTablePluginPage.createNewTableWithGoogleSheet(title,description);

        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickFlexTableMenu();

        // Get shortcode from first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        String PageUrl = wordPressPages.createPageUsingShortCode(title, shortCode);
        getDriver().get(PageUrl);

        Assert.assertFalse(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.entryInfo));
        Assert.assertFalse(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.firstPaginationNumber));

        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();

        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        getDriver().get(baseUrl);

        wordPressDashboardPage.clickFlexTableMenu();
        flexTablePluginPage.sendKeysText(flexTablePluginPage.existingTableSearchField,title);
        By tableEdit = flexTablePluginPage.getTableEditTag(title);
        flexTablePluginPage.clickOnElement(tableEdit);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.tableCustomizationMenu);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.showEntryInfoToggle);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.showPaginationToggle);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButtonToSaveCustomization);

        getDriver().get(PageUrl);
        Assert.assertTrue(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.entryInfo));
        Assert.assertTrue(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.firstPaginationNumber));

        boolean isNextPageAvailable = createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.secondPaginationNumber);
        if(isNextPageAvailable) {
            String firstPageName = createdPageWithFlexTable.getElementText(createdPageWithFlexTable.firstRowFirstColumnData);
            String firstPageId = createdPageWithFlexTable.getElementText(createdPageWithFlexTable.firstRowSecondColumnData);

            createdPageWithFlexTable.clickOnElement(createdPageWithFlexTable.secondPaginationNumber);

            String secondPageName = createdPageWithFlexTable.getElementText(createdPageWithFlexTable.firstRowFirstColumnData);
            String secondPageId = createdPageWithFlexTable.getElementText(createdPageWithFlexTable.firstRowSecondColumnData);

            Assert.assertNotEquals(firstPageName, secondPageName);
            Assert.assertNotEquals(firstPageId, secondPageId);
        }
        else {
            System.out.println("Only 1 Page Available!");
        }
    }

    @Test(priority = 7, description = "Update 'Rows Per Page & Table Height")
    public void verifyUpdatingRowPerPageAndTableHeightWorksProperly() throws InterruptedException {
        String title = faker.commerce().productName();
        String description = faker.lorem().paragraph(1);
        flexTablePluginPage.createNewTableWithGoogleSheet(title,description);

        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickFlexTableMenu();

        // Get shortcode from first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        String PageUrl = wordPressPages.createPageUsingShortCode(title, shortCode);
        getDriver().get(PageUrl);

        Thread.sleep(3000);
        List<WebElement> nameElements = createdPageWithFlexTable.getElements(createdPageWithFlexTable.IDColumn);
        int defaultRowSize = nameElements.size();

        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();

        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        getDriver().get(baseUrl);
        wordPressDashboardPage.clickFlexTableMenu();
        flexTablePluginPage.sendKeysText(flexTablePluginPage.existingTableSearchField,title);
        By tableEdit = flexTablePluginPage.getTableEditTag(title);
        flexTablePluginPage.clickOnElement(tableEdit);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.tableCustomizationMenu);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.tableStylingButton);
        if (defaultRowSize+1>5) {
            flexTablePluginPage.dropDownOptionSelectByText(flexTablePluginPage.rowPerPageDropDown,"5");
            flexTablePluginPage.dropDownOptionSelectByText(flexTablePluginPage.tableHeightDropDown,"1000px");
            flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButtonToSaveCustomization);

            getDriver().get(PageUrl);
            Thread.sleep(3000);
            List<WebElement> updatedNameElements = createdPageWithFlexTable.getElements(createdPageWithFlexTable.IDColumn);
            int updatedRowSize = updatedNameElements.size();
            String defaultStyleOfRow = createdPageWithFlexTable.getElementAttribute(createdPageWithFlexTable.tableStyleAttributeToGetTableHeight,"style");
            Assert.assertNotEquals(defaultRowSize,updatedRowSize);

            String[] styleParts = defaultStyleOfRow.split("height:");
            String heightValue = styleParts[1].split(";")[0].trim();
            Assert.assertEquals(heightValue,"1000px");
        }
        else {
            flexTablePluginPage.dropDownOptionSelectByText(flexTablePluginPage.rowPerPageDropDown,"1");
            flexTablePluginPage.dropDownOptionSelectByText(flexTablePluginPage.tableHeightDropDown,"1000px");
            flexTablePluginPage.clickOnElement(flexTablePluginPage.saveChangesButtonToSaveCustomization);

            getDriver().get(PageUrl);
            Thread.sleep(3000);
            List<WebElement> updatedNameElements = createdPageWithFlexTable.getElements(createdPageWithFlexTable.IDColumn);
            int updatedRowSize = updatedNameElements.size();
            String defaultStyleOfRow = createdPageWithFlexTable.getElementAttribute(createdPageWithFlexTable.tableStyleAttributeToGetTableHeight,"style");
            Assert.assertNotEquals(defaultRowSize,updatedRowSize);
            String[] styleParts = defaultStyleOfRow.split("height:");
            String heightValue = styleParts[1].split(";")[0].trim();
            Assert.assertEquals(heightValue,"1000px");
        }
    }

    @Test(priority = 8, description = "Delete the Table and Verify Frontend Removal")
    public void verifyThatAfterDeleteTableProperMessageDisplayedInFrontEnd() throws InterruptedException {
        String title = faker.commerce().productName();
        String description = faker.lorem().paragraph(1);
        flexTablePluginPage.createNewTableWithGoogleSheet(title,description);

        // Navigate to FlexTable Dashboard
        wordPressDashboardPage.clickFlexTableMenu();

        // Get shortcode from first table
        String shortCode = flexTablePluginPage.getElementText(flexTablePluginPage.listFirstTableShortCode);
        shortCode = shortCode.replace("[gswpts_table=\"", "[gswpts_table id=\"");
        String PageUrl = wordPressPages.createPageUsingShortCode(title, shortCode);
        getDriver().get(PageUrl);
        Assert.assertTrue(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.firstRowFirstColumnData));
        // Load .env file directly
        Dotenv dotenv = Dotenv.configure().load();

        // Navigate to WordPress login page using URL from environment
        String baseUrl = dotenv.get("WP_URL");
        getDriver().get(baseUrl);
        wordPressDashboardPage.clickFlexTableMenu();
        flexTablePluginPage.sendKeysText(flexTablePluginPage.existingTableSearchField,title);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.tableDeleteButton);
        flexTablePluginPage.clickOnElement(flexTablePluginPage.modalDeleteButton);
        getDriver().get(PageUrl);
        Assert.assertFalse(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.firstRowFirstColumnData));
        Assert.assertTrue(createdPageWithFlexTable.isElementVisible(createdPageWithFlexTable.deletedTableMsg));
    }
}