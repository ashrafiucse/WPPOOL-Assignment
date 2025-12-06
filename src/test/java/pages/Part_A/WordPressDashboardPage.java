package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class WordPressDashboardPage extends BasePage {
    public WordPressDashboardPage(WebDriver driver) {
        super(driver);
    }
    public By dashboardHomeButton = By.xpath("//a[contains(text(),'Home') and contains(@href,'index.php')]");
    public By pluginsMenu = By.xpath("//a[@href='plugins.php']//div[contains(text(),'Plugins')]");

}
