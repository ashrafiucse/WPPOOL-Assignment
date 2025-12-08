package pages.Part_A;

import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.WpCliUtils;
import utilities.ConfigManager;
import pages.BasePage;
import static utilities.DriverSetup.getDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.github.cdimascio.dotenv.Dotenv;

public class FlexTablePluginPage extends BasePage {
    WordPressDashboardPage wordPressDashboardPage = new WordPressDashboardPage(getDriver());
    Faker faker = new Faker();

    public FlexTablePluginPage(WebDriver driver) {
        super(driver);
    }
    
    // Existing locators
    public By createNewTableButton = By.xpath("//button[contains(text(),'Create new table')]");
    public By existingTableSearchField = By.xpath("//input[@placeholder='Search tables']");
    public By googleSheetInputField = By.xpath("//input[@id='sheet-url']");
    public By createTableFromUrlButton = By.xpath("//button[contains(text(),'Create table from URL')]");
    public By tableTitleField = By.xpath("//input[@id='table-name']");
    public By tableDescriptionField = By.xpath("//textarea[@id='table-description']");
    public By allTablesLink = By.xpath("//a[contains(text(),'All Tables')]");
    public By saveChangesButton = By.xpath("//button[contains(text(),'Save changes')]");
    public By createNewTableLink = By.xpath("//a[contains(text(),'Create new table')]");
    public By listFirstTableShortCode = By.xpath("(//span[contains(text(),'[gswpts_table=')])[1]");
    
    // New locators for table verification
    public By frontendTableContainer = By.cssSelector(".gswpts-table-container");
    public By frontendTableRows = By.cssSelector(".gswpts-table-container tr");
    public By frontendTableHeaders = By.cssSelector(".gswpts-table-container th");
    public By frontendTableCells = By.cssSelector(".gswpts-table-container td");

    public void createNewTableWithGoogleSheet(String tableTitle, String tableDescription) {
        wordPressDashboardPage.clickOnElement(wordPressDashboardPage.flexTableMenu);
        boolean isExistingTableAvailable = isElementVisible(existingTableSearchField);
        if (isExistingTableAvailable) {
            boolean isCreateNewTableButtonVisible = isElementVisible(createNewTableButton);
            if (isCreateNewTableButtonVisible) {
                clickOnElement(createNewTableButton);

                // Load .env file directly
                Dotenv dotenv = Dotenv.configure().load();

                String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
                sendKeysText(googleSheetInputField, googleSheetURL);
                clickOnElement(createTableFromUrlButton);

                sendKeysText(tableTitleField, tableTitle);
                sendKeysText(tableDescriptionField, tableDescription);
                clickOnElement(saveChangesButton);
                clickOnElement(wordPressDashboardPage.flexTableMenu);
                By newlyAddedTableTitle = getElementThroughTagAndText("h4", tableTitle);
                Assert.assertEquals(getElementText(newlyAddedTableTitle), tableTitle);
            }
            else {
                clickOnElement(createNewTableLink);

                // Load .env file directly
                Dotenv dotenv = Dotenv.configure().load();

                String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
                sendKeysText(googleSheetInputField, googleSheetURL);
                clickOnElement(createTableFromUrlButton);

                sendKeysText(tableTitleField, tableTitle);
                sendKeysText(tableDescriptionField, tableDescription);
                clickOnElement(saveChangesButton);
                clickOnElement(wordPressDashboardPage.flexTableMenu);
                By newlyAddedTableTitle = getElementThroughTagAndText("h4", tableTitle);
                Assert.assertEquals(getElementText(newlyAddedTableTitle), tableTitle);
            }
        } else {
            clickOnElement(createNewTableButton);

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();

            String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
            sendKeysText(googleSheetInputField, googleSheetURL);
            clickOnElement(createTableFromUrlButton);

            sendKeysText(tableTitleField, tableTitle);
            sendKeysText(tableDescriptionField, tableDescription);
            clickOnElement(saveChangesButton);
            clickOnElement(wordPressDashboardPage.flexTableMenu);
        }
    }

    public List<List<String>> getCsvData() throws Exception {
        Dotenv dotenv = Dotenv.configure().load();
        String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
        String csvUrl = convertGoogleSheetToCsvUrl(googleSheetURL);
        return readCsvFromUrl(csvUrl);
    }
}
