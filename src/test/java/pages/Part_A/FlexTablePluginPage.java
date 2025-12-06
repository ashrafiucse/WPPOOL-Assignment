package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class FlexTablePluginPage extends BasePage {
    public FlexTablePluginPage(WebDriver driver) {
        super(driver);
    }
    public By createNewTableButton = By.xpath("//button[contains(text(),'Create new table')]");

}
