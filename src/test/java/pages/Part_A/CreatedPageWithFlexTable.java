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
    public By IDColumn = By.xpath("//tr//td[2]//div");
    public By entryInfo = By.xpath("//div[contains(text(),'Showing') and contains(text(),'entries')]");
    public By firstPaginationNumber = By.xpath("//a[@data-dt-idx='1']");
    public By secondPaginationNumber = By.xpath("//a[@data-dt-idx='2']");
    public By firstRowFirstColumnData = By.xpath("(//tr//td//div)[1]");
    public By firstRowSecondColumnData = By.xpath("(//tr//td//div)[2]");
    public By tableStyleAttributeToGetTableHeight = By.xpath("//div[@class='dataTables_scrollBody']");
}
