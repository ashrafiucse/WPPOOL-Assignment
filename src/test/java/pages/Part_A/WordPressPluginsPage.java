package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class WordPressPluginsPage extends BasePage {
    public WordPressPluginsPage(WebDriver driver) {
        super(driver);
    }
    public By installedPluginSearchField = By.xpath("//input[@id='plugin-search-input']");
    public By flexTablePluginTitle = By.xpath("//td//strong[contains(text(),'FlexTable')]");
    public By flexTableDeactivateLink = By.xpath("//a[contains(@href,'plugins.php?action=deactivate&plugin=sheets-to-wp-table-live-sync%2Fsheets-to-wp-table-live-sync.php') and contains(text(),'Deactivate')]");
    public By flexTableActivateLink = By.xpath("//a[contains(@href,'plugins.php?action=activate&plugin=sheets-to-wp-table-live-sync%2Fsheets-to-wp-table-live-sync.php') and contains(text(),'Activate')]");
    public By addPluginButton = By.xpath("//a[contains(@href,'/plugin-install.php') and contains(text(),'Add Plugin')]");
    public By pluginSearchFieldToAdd = By.xpath("//input[@id='search-plugins']");
    public By flexTablePluginInstallButton = By.xpath("//a[contains(@href,'wp-admin/update.php?action=install-plugin&plugin=sheets-to-wp-table-live-sync')]");
    public By installedFlexTablePluginActivateButton = By.xpath("//a[contains(@href,'action=activate&plugin=sheets-to-wp-table-live-sync/sheets-to-wp-table-live-sync.php') and contains(text(),'Activate')]");
    public By noPluginsAreAvailableText = By.xpath("//td[contains(text(),'No plugins are currently available.')]");
}
