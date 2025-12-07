package testcases.Part_A;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import pages.Part_A.FlexTablePluginPage;
import pages.Part_A.WordPressPluginsPage;
import utilities.DriverSetup;
import pages.Part_A.WordPressLoginPage;
import pages.Part_A.WordPressDashboardPage;
import testcases.BaseTest;

public class FlexTablePluginTest extends BaseTest {
    WebDriver driver = DriverSetup.getDriver();

    // Initialize page objects
    WordPressLoginPage loginPage = new WordPressLoginPage(driver);
    WordPressDashboardPage wordPressDashboardPage = new WordPressDashboardPage(driver);
    WordPressPluginsPage wordPressPluginsPage = new WordPressPluginsPage(driver);
    FlexTablePluginPage flexTablePluginPage = new FlexTablePluginPage(driver);

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


}
