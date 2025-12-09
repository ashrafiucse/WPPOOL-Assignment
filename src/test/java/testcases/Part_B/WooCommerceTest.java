package testcases.Part_B;

import com.github.javafaker.Faker;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BasePage;
import pages.Part_A.WordPressDashboardPage;
import pages.Part_A.WordPressLoginPage;
import pages.Part_B.*;
import testcases.BaseTest;

import static utilities.DriverSetup.getDriver;

public class WooCommerceTest extends BaseTest {
    HomePageLocators homePageLocators;
    ShopPage shopPage;
    CheckoutPage checkoutPage;
    WordPressDashboardPage wordPressDashboardPage;
    WooCommercePage wooCommercePage;
    WordPressLoginPage wordPressLoginPage;
    CustomerRegistrationPage customerRegistrationPage;
    MyAccountsPage myAccountsPage;

    @BeforeMethod
    public void navigateToHomePage() {
        // Use inherited driver from BaseTest (initialized in BaseTest.setup())
        homePageLocators = new HomePageLocators(driver);
        shopPage = new ShopPage(driver);
        checkoutPage = new CheckoutPage(driver);
        wordPressDashboardPage = new WordPressDashboardPage(driver);
        wooCommercePage = new WooCommercePage(driver);
        wordPressLoginPage = new WordPressLoginPage(driver);
        customerRegistrationPage = new CustomerRegistrationPage(driver);
        myAccountsPage = new MyAccountsPage(driver);
        driver.get(getHomepageUrl());
    }

@Test(description = "End-to-End Checkout Flow")
    public void verifyEndToEndCheckoutFlow() throws InterruptedException {
    Faker faker = new Faker();
    String customerEmail = faker.name().username() + System.currentTimeMillis() + "@test.com";
    String customerFirstName = faker.name().firstName();
    String customerLastName = faker.name().lastName();
    String customerAddress = faker.address().fullAddress();
    String customerCity = faker.address().cityName();
    String orderID;
        boolean isShopButtonAvailable;
        isShopButtonAvailable = homePageLocators.isElementVisible(homePageLocators.shopLink);
        if (isShopButtonAvailable) {
            homePageLocators.clickOnElement(homePageLocators.shopLink);
        }
        else {
                driver.get(getShopUrl());
        }
        shopPage.clickOnElement(shopPage.addToCartButton);
        shopPage.clickOnElement(shopPage.viewCartButton);
        
        // Extract prices using the extract method from ShopPage
        float productPrice = shopPage.extractAndCleanPrice(shopPage.productPrice);
        float shippingCharge = shopPage.extractAndCleanPrice(shopPage.ShippingCharge);
        float taxAmount = shopPage.extractAndCleanPrice(shopPage.taxAmount);
        float totalPrice = shopPage.extractAndCleanPrice(shopPage.totalPrice);
        
        // Calculate expected total
        float expectedTotal = productPrice + shippingCharge + taxAmount;
        
        // Validate total matches (allow small floating point differences)
        if (Math.abs(expectedTotal - totalPrice) > 0.01) {
            throw new AssertionError("Price validation failed! Expected: " + expectedTotal + 
                                   ", Actual: " + totalPrice + 
                                   " (Product: " + productPrice + 
                                   ", Shipping: " + shippingCharge + 
                                   ", Tax: " + taxAmount + ")");
        }
        
        System.out.println("Price validation successful!");
        shopPage.clickOnElement(shopPage.proceedToCheckoutButton);
        checkoutPage.sendKeysText(checkoutPage.emailInputField, customerEmail);
        checkoutPage.sendKeysText(checkoutPage.firstNameInputField,customerFirstName);
        checkoutPage.sendKeysText(checkoutPage.lastNameInputField,customerLastName);
        checkoutPage.sendKeysText(checkoutPage.addressInputField,customerAddress);
        checkoutPage.sendKeysText(checkoutPage.cityInputField,customerCity);
        boolean isCDAvailable = checkoutPage.isElementVisible(checkoutPage.cashOnDelivery);
        if (isCDAvailable) {
            checkoutPage.clickOnElement(checkoutPage.cashOnDelivery);
            checkoutPage.clickOnElement(checkoutPage.placeOrderButton);
        }
        else {
            checkoutPage.clickOnElement(checkoutPage.directBankTransfer);
            checkoutPage.clickOnElement(checkoutPage.placeOrderButton);
        }
        boolean isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg);
        if (isOrderConfirmMsgShowed) {
            orderID = checkoutPage.getElementText(checkoutPage.orderId);

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();

            // Navigate to WordPress login page using URL from environment
            String baseUrl = dotenv.get("WP_URL");
            getDriver().get(baseUrl);
            wordPressLoginPage.doLogin();
            wordPressDashboardPage.clickOnElement(wordPressDashboardPage.wooCommerceMenu);
            wooCommercePage.clickOnElement(wooCommercePage.ordersMenu);
            wooCommercePage.searchAndPressEnter(wooCommercePage.orderSearchField,orderID);
            String DraftOrderId = "wp-admin/admin.php?page=wc-orders&action=edit&id=" + orderID;
            By orderLink = By.xpath("//a[contains(@href,'"+DraftOrderId+"')]");
            System.out.println(wooCommercePage.getElementText(orderLink));
            wooCommercePage.clickOnElement(orderLink);
            
            // Extract order total from admin panel using ShopPage method
            float adminOrderTotal = shopPage.extractAndCleanPrice(wooCommercePage.totalOrder);
            
            // Validate admin order total matches cart total
            if (Math.abs(adminOrderTotal - totalPrice) > 0.01) {
                throw new AssertionError("Order total validation failed! Cart Total: " + totalPrice + 
                                       ", Admin Order Total: " + adminOrderTotal);
            }
            
            System.out.println("Order total validation successful!");
        }
        else {
            System.out.println("No Msg showed.");
        }
    }


    @Test(description = "User Account Order History")
    public void verifyUserCanViewOrderHistory() throws InterruptedException {
        Faker faker = new Faker();
        String customerEmail = faker.name().username() + System.currentTimeMillis() + "@test.com";
        String customerPassword = faker.name().username() + "123!";
        String customerFirstName = faker.name().firstName();
        String customerLastName = faker.name().lastName();
        String customerAddress = faker.address().fullAddress();
        String customerCity = faker.address().cityName();
        String orderID;
        driver.get(getMyAccountUrl());
        boolean isPasswordFieldAvailable = customerRegistrationPage.isElementVisible(customerRegistrationPage.registrationPasswordInputField);
        if (isPasswordFieldAvailable) {
            customerRegistrationPage.sendKeysText(customerRegistrationPage.registrationEmailInputField,customerEmail);
            customerRegistrationPage.sendKeysText(customerRegistrationPage.registrationPasswordInputField,customerPassword);
        }
        else {
            customerRegistrationPage.sendKeysText(customerRegistrationPage.registrationEmailInputField,customerEmail);
        }
        customerRegistrationPage.clickOnElement(customerRegistrationPage.registerButton);

        boolean isShopButtonAvailable;
        isShopButtonAvailable = homePageLocators.isElementVisible(homePageLocators.shopLink);
        if (isShopButtonAvailable) {
            homePageLocators.clickOnElement(homePageLocators.shopLink);
        }
        else {
            driver.get(getShopUrl());
        }
        shopPage.clickOnElement(shopPage.addToCartButton);
        shopPage.clickOnElement(shopPage.viewCartButton);

        // Extract prices using the extract method from ShopPage
        float productPrice = shopPage.extractAndCleanPrice(shopPage.productPrice);
        float shippingCharge = shopPage.extractAndCleanPrice(shopPage.ShippingCharge);
        float taxAmount = shopPage.extractAndCleanPrice(shopPage.taxAmount);
        float totalPrice = shopPage.extractAndCleanPrice(shopPage.totalPrice);

        // Calculate expected total
        float expectedTotal = productPrice + shippingCharge + taxAmount;

        // Validate total matches (allow small floating point differences)
        if (Math.abs(expectedTotal - totalPrice) > 0.01) {
            throw new AssertionError("Price validation failed! Expected: " + expectedTotal +
                    ", Actual: " + totalPrice +
                    " (Product: " + productPrice +
                    ", Shipping: " + shippingCharge +
                    ", Tax: " + taxAmount + ")");
        }

        System.out.println("Price validation successful!");
        shopPage.clickOnElement(shopPage.proceedToCheckoutButton);
        checkoutPage.sendKeysText(checkoutPage.firstNameInputField,customerFirstName);
        checkoutPage.sendKeysText(checkoutPage.lastNameInputField,customerLastName);
        checkoutPage.sendKeysText(checkoutPage.addressInputField,customerAddress);
        checkoutPage.sendKeysText(checkoutPage.cityInputField,customerCity);
        boolean isCDAvailable = checkoutPage.isElementVisible(checkoutPage.cashOnDelivery);
        if (isCDAvailable) {
            checkoutPage.clickOnElement(checkoutPage.cashOnDelivery);
            checkoutPage.clickOnElement(checkoutPage.placeOrderButton);
        }
        else {
            checkoutPage.clickOnElement(checkoutPage.directBankTransfer);
            checkoutPage.clickOnElement(checkoutPage.placeOrderButton);
        }
        boolean isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg);
        if (isOrderConfirmMsgShowed) {
            orderID = checkoutPage.getElementText(checkoutPage.orderId);
            driver.get(getMyAccountUrl());
            myAccountsPage.clickOnElement(myAccountsPage.myOrdersMenu);
            String DraftOrderId = "my-account/view-order/" + orderID;
            By orderLink = By.xpath("//a[contains(@href,'"+DraftOrderId+"')]");
            myAccountsPage.clickOnElement(orderLink);
            
            // Extract order total from My Account page
            float myAccountOrderTotal = shopPage.extractAndCleanPrice(myAccountsPage.totalAmount);
            
            // Validate My Account order total matches cart total
            if (Math.abs(myAccountOrderTotal - totalPrice) > 0.01) {
                throw new AssertionError("My Account order total validation failed! Cart Total: " + totalPrice + 
                                       ", My Account Order Total: " + myAccountOrderTotal);
            }
            
            System.out.println("My Account order total validation successful!");

            myAccountsPage.clickOnElement(myAccountsPage.logOutButton);

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();

            // Navigate to WordPress login page using URL from environment
            String baseUrl = dotenv.get("WP_URL");
            getDriver().get(baseUrl);
            wordPressLoginPage.doLogin();
            wordPressDashboardPage.clickOnElement(wordPressDashboardPage.wooCommerceMenu);
            wooCommercePage.clickOnElement(wooCommercePage.ordersMenu);
            wooCommercePage.searchAndPressEnter(wooCommercePage.orderSearchField,orderID);
            String DraftOrderIdCustomer = "wp-admin/admin.php?page=wc-orders&action=edit&id=" + orderID;
            By orderLinkMyOrders = By.xpath("//a[contains(@href,'"+DraftOrderIdCustomer+"')]");
            System.out.println(wooCommercePage.getElementText(orderLinkMyOrders));
            wooCommercePage.clickOnElement(orderLinkMyOrders);

            // Extract order total from admin panel using ShopPage method
            float adminOrderTotal = shopPage.extractAndCleanPrice(wooCommercePage.totalOrder);

            // Validate admin order total matches cart total
            if (Math.abs(adminOrderTotal - totalPrice) > 0.01) {
                throw new AssertionError("Order total validation failed! Cart Total: " + totalPrice +
                        ", Admin Order Total: " + adminOrderTotal);
            }

            System.out.println("Order total validation successful!");
        }
        else {
            System.out.println("No Msg showed.");
        }
    }




}
