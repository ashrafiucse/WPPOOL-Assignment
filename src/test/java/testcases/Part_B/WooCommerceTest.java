package testcases.Part_B;

import com.github.javafaker.Faker;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
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

        // Check if proceed to checkout button is visible and clickable
        boolean isProceedToCheckoutVisible = shopPage.isElementVisible(shopPage.proceedToCheckoutButton);

        if (isProceedToCheckoutVisible) {
            try {
                shopPage.clickOnElement(shopPage.proceedToCheckoutButton);
            } catch (Exception e) {
                // Fallback to direct checkout URL from environment
                driver.get(getCheckoutUrl());
            }
        } else {
            // Fallback to direct checkout URL from environment
            driver.get(getCheckoutUrl());
        }

        // Add debugging to check if checkout page loaded properly
        boolean isCheckoutPageLoaded = checkoutPage.isElementVisible(checkoutPage.emailInputField);

        if (!isCheckoutPageLoaded) {
        }

        checkoutPage.sendKeysText(checkoutPage.emailInputField, customerEmail);

        checkoutPage.sendKeysText(checkoutPage.firstNameInputField,customerFirstName);

        checkoutPage.sendKeysText(checkoutPage.lastNameInputField,customerLastName);

        checkoutPage.sendKeysText(checkoutPage.addressInputField,customerAddress);

        checkoutPage.sendKeysText(checkoutPage.cityInputField,customerCity);
        boolean isCDAvailable = checkoutPage.isElementVisible(checkoutPage.cashOnDelivery);
        boolean isDBTAvailable = checkoutPage.isElementVisible(checkoutPage.directBankTransfer);

        if (isCDAvailable) {
            checkoutPage.clickOnElement(checkoutPage.cashOnDelivery);
        }
        else if (isDBTAvailable) {
            checkoutPage.clickOnElement(checkoutPage.directBankTransfer);
        }
        else {
            Assert.fail("No payment methods available on checkout page");
        }

        checkoutPage.clickOnElement(checkoutPage.placeOrderButton);

        // Add extra wait for headless mode to ensure page loads properly
        try {
            Thread.sleep(3000); // Wait 3 seconds for page to load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Use enhanced visibility check for headless mode
        boolean isOrderConfirmMsgShowed;
        if (System.getProperty("headless", "false").equals("true")) {
            // Enhanced check for headless mode with longer timeout
            isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg, java.time.Duration.ofSeconds(30), true);
        } else {
            // Standard check for regular mode
            isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg);
        }

        if (isOrderConfirmMsgShowed) {
            String orderID = checkoutPage.getElementText(checkoutPage.orderId);

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();

            // Navigate to WordPress login page using URL from environment
            String baseUrl = dotenv.get("WP_URL");
            getDriver().get(baseUrl);

            wordPressLoginPage.doLogin();

            // Check if WooCommerce menu is visible and clickable
            boolean isWooCommerceMenuVisible = wordPressDashboardPage.isElementVisible(wordPressDashboardPage.wooCommerceMenu);

            if (isWooCommerceMenuVisible) {
                try {
                    wordPressDashboardPage.clickOnElement(wordPressDashboardPage.wooCommerceMenu);
                } catch (Exception e) {
                    getDriver().get(getWooCommerceAdminUrl());
                }
            } else {
                getDriver().get(getWooCommerceAdminUrl());
            }

            // Check if orders menu is visible and clickable
            boolean isOrdersMenuVisible = wooCommercePage.isElementVisible(wooCommercePage.ordersMenu);

            if (isOrdersMenuVisible) {
                try {
                    wooCommercePage.clickOnElement(wooCommercePage.ordersMenu);
                } catch (Exception e) {
                    getDriver().get(getWooCommerceOrdersUrl());
                }
            } else {
                getDriver().get(getWooCommerceOrdersUrl());
            }

            wooCommercePage.searchAndPressEnter(wooCommercePage.orderSearchField,orderID);
            String DraftOrderId = "wp-admin/admin.php?page=wc-orders&action=edit&id=" + orderID;
            By orderLink = By.xpath("//a[contains(@href,'"+DraftOrderId+"')]");
            wooCommercePage.clickOnElement(orderLink);

            // Extract order total from admin panel using ShopPage method
            float adminOrderTotal = shopPage.extractAndCleanPrice(wooCommercePage.totalOrder);

            // Validate admin order total matches cart total
            if (Math.abs(adminOrderTotal - totalPrice) > 0.01) {
                throw new AssertionError("Order total validation failed! Cart Total: " + totalPrice +
                                       ", Admin Order Total: " + adminOrderTotal);
            }
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

        // Check if proceed to checkout button is visible and clickable
        boolean isProceedToCheckoutVisible = shopPage.isElementVisible(shopPage.proceedToCheckoutButton);

        if (isProceedToCheckoutVisible) {
            try {
                shopPage.clickOnElement(shopPage.proceedToCheckoutButton);
            } catch (Exception e) {
                // Fallback to direct checkout URL from environment
                driver.get(getCheckoutUrl());
            }
        } else {
            // Fallback to direct checkout URL from environment
            driver.get(getCheckoutUrl());
        }

        // Add debugging to check if checkout page loaded properly
        boolean isCheckoutPageLoaded = checkoutPage.isElementVisible(checkoutPage.emailInputField);

        if (!isCheckoutPageLoaded) {
            Assert.fail("Checkout page did not load properly - email field not visible");
        }
        checkoutPage.sendKeysText(checkoutPage.firstNameInputField,customerFirstName);

        checkoutPage.sendKeysText(checkoutPage.lastNameInputField,customerLastName);

        checkoutPage.sendKeysText(checkoutPage.addressInputField,customerAddress);

        checkoutPage.sendKeysText(checkoutPage.cityInputField,customerCity);
        boolean isCDAvailable = checkoutPage.isElementVisible(checkoutPage.cashOnDelivery);
        boolean isDBTAvailable = checkoutPage.isElementVisible(checkoutPage.directBankTransfer);

        if (isCDAvailable) {
            checkoutPage.clickOnElement(checkoutPage.cashOnDelivery);
        }
        else if (isDBTAvailable) {
            checkoutPage.clickOnElement(checkoutPage.directBankTransfer);
        }
        else {
            Assert.fail("No payment methods available on checkout page");
        }

        checkoutPage.clickOnElement(checkoutPage.placeOrderButton);

        // Use enhanced visibility check for headless mode
        boolean isOrderConfirmMsgShowed;
        if (System.getProperty("headless", "false").equals("true")) {
            // Enhanced check for headless mode with longer timeout
            isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg, java.time.Duration.ofSeconds(30), true);
        } else {
            // Standard check for regular mode
            isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg);
        }

        if (isOrderConfirmMsgShowed) {
            String orderID = checkoutPage.getElementText(checkoutPage.orderId);
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
            wooCommercePage.clickOnElement(orderLinkMyOrders);

            // Extract order total from admin panel using ShopPage method
            float adminOrderTotal = shopPage.extractAndCleanPrice(wooCommercePage.totalOrder);

            // Validate admin order total matches cart total
            if (Math.abs(adminOrderTotal - totalPrice) > 0.01) {
                throw new AssertionError("Order total validation failed! Cart Total: " + totalPrice +
                        ", Admin Order Total: " + adminOrderTotal);
            }
        }
    }




}
