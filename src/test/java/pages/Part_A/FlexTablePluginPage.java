package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class FlexTablePluginPage extends BasePage {
    public FlexTablePluginPage(WebDriver driver) {
        super(driver);
    }
    public By createNewTableButton = By.xpath("//button[contains(text(),'Create new table')]");
    public By existingTableSearchField = By.xpath("//input[@placeholder='Search tables']");
    public By googleSheetInputField = By.xpath("//input[@id='sheet-url']");
    public By createTableFromUrlButton = By.xpath("//button[contains(text(),'Create table from URL')]");
    public By tableTitleField = By.xpath("//input[@id='table-name']");
    public By tableDescriptionField = By.xpath("//textarea[@id='table-description']");
    public By allTablesLink = By.xpath("//a[contains(text(),'All Tables')]");
    public By saveChangesButton = By.xpath("//button[contains(text(),'Save changes')]");
    public By createNewTableLink = By.xpath("//a[contains(text(),'Create new table')]");

}
