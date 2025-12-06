package testcases.Part_A;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.Assert;
import pages.Part_A.FlexTablePluginPage;
import pages.Part_A.WordPressPluginsPage;
import utilities.DriverSetup;
import pages.Part_A.WordPressLoginPage;
import pages.Part_A.WordPressDashboardPage;
import testcases.BaseTest;

public class FlexTablePluginTest extends BaseTest {

    @Test(description = "Verify FlexTable Plugin Activation Status")
    public void verifyFlexTablePluginActivation() throws InterruptedException {
        WebDriver driver = DriverSetup.getDriver();

        // Initialize page objects
        WordPressLoginPage loginPage = new WordPressLoginPage(driver);
        WordPressDashboardPage dashboardPage = new WordPressDashboardPage(driver);
        WordPressPluginsPage wordPressPluginsPage = new WordPressPluginsPage(driver);
        FlexTablePluginPage flexTablePluginPage = new FlexTablePluginPage(driver);

        // Perform login using environment credentials (handled in doLogin method)
        loginPage.doLogin();

        // Navigate to plugins page
        dashboardPage.clickOnElement(dashboardPage.pluginsMenu);
        boolean noPluginsAvailableText = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.noPluginsAreAvailableText);
        boolean flexTableTitleVisible = wordPressPluginsPage.isElementVisible(wordPressPluginsPage.flexTablePluginTitle);
        if (noPluginsAvailableText) {
            wordPressPluginsPage.clickOnElement(wordPressPluginsPage.addPluginButton);
            wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.pluginSearchFieldToAdd,"FlexTable");
            wordPressPluginsPage.clickOnElement(wordPressPluginsPage.flexTablePluginInstallButton);
            Thread.sleep(2000);
            wordPressPluginsPage.clickOnElement(wordPressPluginsPage.installedFlexTablePluginActivateButton);
            Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableButton));

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
                    Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableButton));
                }
            } else {
                wordPressPluginsPage.clickOnElement(wordPressPluginsPage.addPluginButton);
                wordPressPluginsPage.searchAndPressEnter(wordPressPluginsPage.pluginSearchFieldToAdd,"FlexTable");
                wordPressPluginsPage.clickOnElement(wordPressPluginsPage.flexTablePluginInstallButton);
                Thread.sleep(15000);
                wordPressPluginsPage.clickOnElement(wordPressPluginsPage.installedFlexTablePluginActivateButton);
                Assert.assertTrue(flexTablePluginPage.isElementVisible(flexTablePluginPage.createNewTableButton));
            }
        }
    }
}
