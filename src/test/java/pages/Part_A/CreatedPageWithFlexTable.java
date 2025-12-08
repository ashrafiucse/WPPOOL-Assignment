package pages.Part_A;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.BasePage;

import java.util.List;

public class CreatedPageWithFlexTable extends BasePage {
    public CreatedPageWithFlexTable(WebDriver driver) {
        super(driver);
    }
    public By NameColumn = By.xpath("//tr//td[1]//div");
}
