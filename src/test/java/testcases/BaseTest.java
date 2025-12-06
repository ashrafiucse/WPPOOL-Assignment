package testcases;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utilities.DriverSetup;

public class BaseTest {
    
    @BeforeMethod
    public void setUp() {
        DriverSetup.initDriver();
    }
    
    @AfterMethod
    public void tearDown() {
        DriverSetup.quitDriver();
    }
}