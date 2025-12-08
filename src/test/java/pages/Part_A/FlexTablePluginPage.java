package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

//    // WP-CLI Method - Create page with shortcode
//    public String createPageWithShortcodeCLI(String shortCode) {
//        try {
//            logInfo("Creating page with shortcode using WP-CLI...");
//
//            // Check if WP-CLI is available
//            if (!WpCliUtils.isWpCliAvailable()) {
//                throw new RuntimeException("WP-CLI is not available. Please install WP-CLI.");
//            }
//
//            String pageTitle = "FlexTable Automation Page " + System.currentTimeMillis();
//            String pageId = WpCliUtils.createPageWithShortcode(pageTitle, shortCode);
//
//            if (pageId == null) {
//                throw new RuntimeException("Failed to create page - pageId is null");
//            }
//
//            String pageUrl = WpCliUtils.getPageUrl(pageId);
//            logInfo("✅ Page created successfully!");
//            logInfo("🔗 Page URL: " + pageUrl);
//            logInfo("📋 Page ID: " + pageId);
//
//            return pageUrl;
//
//        } catch (Exception e) {
//            logError("❌ Failed to create page with shortcode", e);
//            throw new RuntimeException("Failed to create page with shortcode: " + e.getMessage(), e);
//        }
//    }
//
//    // REST API Method - Create page with shortcode
//    public String createPageWithShortcodeREST(String shortCode) {
//        try {
//            logInfo("Creating page with shortcode using REST API...");
//
//            String baseUrl = ConfigManager.getBaseUrl();
//            String username = ConfigManager.getAdminUsername();
//            String password = ConfigManager.getAdminPassword();
//
//            // Create page content with shortcode
//            String pageTitle = "FlexTable REST API Page " + System.currentTimeMillis();
//            String pageContent = shortCode;
//
//            // Use WordPress REST API to create page
//            String apiUrl = baseUrl + "/wp-json/wp/v2/pages";
//
//            // Build the REST API request
//            URL url = new URL(apiUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Authorization", "Basic " +
//                java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
//            connection.setDoOutput(true);
//
//            // Create JSON payload
//            String jsonPayload = String.format(
//                "{\"title\":\"%s\",\"content\":\"%s\",\"status\":\"publish\"}",
//                pageTitle.replace("\"", "\\\""),
//                pageContent.replace("\"", "\\\"")
//            );
//
//            // Send request
//            connection.getOutputStream().write(jsonPayload.getBytes());
//
//            // Get response
//            int responseCode = connection.getResponseCode();
//            if (responseCode == 201) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                reader.close();
//
//                // Extract URL from response
//                String jsonResponse = response.toString();
//                Pattern urlPattern = Pattern.compile("\"link\":\"([^\"]+)\"");
//                Matcher matcher = urlPattern.matcher(jsonResponse);
//
//                if (matcher.find()) {
//                    String pageUrl = matcher.group(1).replace("\\/", "/");
//                    logInfo("✅ Page created successfully via REST API!");
//                    logInfo("🔗 Page URL: " + pageUrl);
//                    return pageUrl;
//                }
//            }
//
//            throw new RuntimeException("Failed to create page via REST API. Response code: " + responseCode);
//
//        } catch (Exception e) {
//            logError("❌ Failed to create page with shortcode via REST API", e);
//            throw new RuntimeException("Failed to create page with shortcode via REST API: " + e.getMessage(), e);
//        }
//    }
//
//    // Check if REST API is accessible
//    public boolean isRESTAPIAccessible() {
//        try {
//            String baseUrl = ConfigManager.getBaseUrl();
//            URL url = new URL(baseUrl + "/wp-json/wp/v2/");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//
//            int responseCode = connection.getResponseCode();
//            return responseCode == 200;
//
//        } catch (Exception e) {
//            logError("REST API accessibility check failed", e);
//            return false;
//        }
//    }
//
//    // Read Google Sheet data from environment
//    public List<List<String>> readGoogleSheetFromEnv() {
//        try {
//            String googleSheetUrl = ConfigManager.get("GOOGLE_SHEET_LINK");
//            if (googleSheetUrl == null || googleSheetUrl.trim().isEmpty()) {
//                throw new RuntimeException("GOOGLE_SHEET_LINK not found in environment variables");
//            }
//
//            String csvUrl = convertGoogleSheetToCSVUrl(googleSheetUrl);
//            return readGoogleSheetCSV(csvUrl);
//
//        } catch (Exception e) {
//            logError("Failed to read Google Sheet from environment", e);
//            throw new RuntimeException("Failed to read Google Sheet from environment: " + e.getMessage(), e);
//        }
//    }
//
//    // Convert Google Sheet URL to CSV export URL
//    public String convertGoogleSheetToCSVUrl(String googleSheetUrl) {
//        try {
//            // Extract sheet ID from Google Sheets URL
//            Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9-_]+)");
//            Matcher matcher = pattern.matcher(googleSheetUrl);
//
//            if (matcher.find()) {
//                String sheetId = matcher.group(1);
//
//                // Check if there's a specific gid (sheet tab)
//                Pattern gidPattern = Pattern.compile("[?&]gid=([0-9]+)");
//                Matcher gidMatcher = gidPattern.matcher(googleSheetUrl);
//                String gid = gidMatcher.find() ? gidMatcher.group(1) : "0";
//
//                return "https://docs.google.com/spreadsheets/d/" + sheetId + "/export?format=csv&gid=" + gid;
//            }
//
//            throw new RuntimeException("Invalid Google Sheets URL format");
//
//        } catch (Exception e) {
//            logError("Failed to convert Google Sheet URL to CSV", e);
//            throw new RuntimeException("Failed to convert Google Sheet URL to CSV: " + e.getMessage(), e);
//        }
//    }
//
//    // Read CSV data from Google Sheets
//    private List<List<String>> readGoogleSheetCSV(String csvUrl) {
//        List<List<String>> data = new ArrayList<>();
//
//        try {
//            URL url = new URL(csvUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(10000);
//            connection.setReadTimeout(10000);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                List<String> row = parseCsvRow(line);
//                data.add(row);
//            }
//            reader.close();
//
//            logInfo("✅ Successfully read " + data.size() + " rows from Google Sheets");
//
//        } catch (Exception e) {
//            logError("Failed to read CSV from Google Sheets", e);
//            throw new RuntimeException("Failed to read CSV from Google Sheets: " + e.getMessage(), e);
//        }
//
//        return data;
//    }
//
//    // Verify table data on frontend
//    public void verifyTableDataOnFrontendREST(String pageUrl, String csvUrl, String testName) {
//        try {
//            logInfo("🔍 Verifying table data on frontend for: " + testName);
//
//            // Navigate to the page
//            getDriver().get(pageUrl);
//
//            // Wait for page to load
//            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
//            wait.until(webDriver ->
//                ((org.openqa.selenium.JavascriptExecutor) getDriver())
//                .executeScript("return document.readyState").equals("complete"));
//
//            // Wait for table to be visible
//            wait.until(ExpectedConditions.visibilityOfElementLocated(frontendTableContainer));
//
//            // Get expected data from CSV
//            List<List<String>> expectedData = readGoogleSheetCSV(csvUrl);
//
//            // Get actual data from frontend
//            List<List<String>> actualData = extractTableDataFromFrontend();
//
//            // Verify data
//            verifyTableData(expectedData, actualData);
//
//            logInfo("✅ Table data verification completed successfully");
//
//        } catch (Exception e) {
//            logError("❌ Failed to verify table data on frontend", e);
//            throw new RuntimeException("Failed to verify table data on frontend: " + e.getMessage(), e);
//        }
//    }
//
//    // Extract table data from frontend
//    private List<List<String>> extractTableDataFromFrontend() {
//        List<List<String>> tableData = new ArrayList<>();
//
//        try {
//            // Wait for table to be present
//            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
//            wait.until(ExpectedConditions.presenceOfElementLocated(frontendTableRows));
//
//            // Get all rows
//            List<WebElement> rows = getElements(frontendTableRows);
//
//            for (WebElement row : rows) {
//                List<String> rowData = new ArrayList<>();
//                List<WebElement> cells = row.findElements(By.tagName("td"));
//
//                // If no td elements, try th (for header row)
//                if (cells.isEmpty()) {
//                    cells = row.findElements(By.tagName("th"));
//                }
//
//                for (WebElement cell : cells) {
//                    rowData.add(cell.getText().trim());
//                }
//
//                if (!rowData.isEmpty()) {
//                    tableData.add(rowData);
//                }
//            }
//
//        } catch (Exception e) {
//            logError("Failed to extract table data from frontend", e);
//            throw new RuntimeException("Failed to extract table data from frontend: " + e.getMessage(), e);
//        }
//
//        return tableData;
//    }
//
//    // Verify table data matches
//    private void verifyTableData(List<List<String>> expectedData, List<List<String>> actualData) {
//        if (expectedData.isEmpty()) {
//            throw new RuntimeException("Expected data is empty");
//        }
//
//        if (actualData.isEmpty()) {
//            throw new RuntimeException("Actual data is empty - table may not be rendered properly");
//        }
//
//        // Compare dimensions
//        if (expectedData.size() > actualData.size()) {
//            logWarning("⚠️  Warning: Expected more rows than actual. Expected: " +
//                expectedData.size() + ", Actual: " + actualData.size());
//        }
//
//        // Compare first few rows of data
//        int rowsToCompare = Math.min(5, Math.min(expectedData.size(), actualData.size()));
//
//        for (int i = 0; i < rowsToCompare; i++) {
//            List<String> expectedRow = expectedData.get(i);
//            List<String> actualRow = actualData.get(i);
//
//            int colsToCompare = Math.min(expectedRow.size(), actualRow.size());
//
//            for (int j = 0; j < colsToCompare; j++) {
//                String expectedValue = expectedRow.get(j).trim();
//                String actualValue = actualRow.get(j).trim();
//
//                if (!expectedValue.equals(actualValue)) {
//                    logWarning("Data mismatch at row " + (i+1) + ", column " + (j+1));
//                    logWarning("   Expected: '" + expectedValue + "'");
//                    logWarning("   Actual:   '" + actualValue + "'");
//                }
//            }
//        }
//
//        logInfo("✅ Table data verification completed - checked " + rowsToCompare + " rows");
//    }
//
//    // Get environment variable
//    public String getEnv(String key) {
//        return ConfigManager.get(key);
//    }

    public List<List<String>> getCsvData() throws Exception {
        Dotenv dotenv = Dotenv.configure().load();
        String googleSheetURL = dotenv.get("GOOGLE_SHEET_LINK");
        String csvUrl = convertGoogleSheetToCsvUrl(googleSheetURL);
        return readCsvFromUrl(csvUrl);
    }
}
