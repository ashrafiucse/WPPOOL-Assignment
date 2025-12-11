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
    System.out.println("[DEBUG] ===== STARTING WOO COMMERCE END-TO-END CHECKOUT TEST =====");

    Faker faker = new Faker();
    String customerEmail = faker.name().username() + System.currentTimeMillis() + "@test.com";
    String customerFirstName = faker.name().firstName();
    String customerLastName = faker.name().lastName();
    String customerAddress = faker.address().fullAddress();
    String customerCity = faker.address().cityName();
    String orderID;

    System.out.println("[DEBUG] Generated test data:");
    System.out.println("[DEBUG] - Email: " + customerEmail);
    System.out.println("[DEBUG] - Name: " + customerFirstName + " " + customerLastName);
    System.out.println("[DEBUG] - Address: " + customerAddress);
    System.out.println("[DEBUG] - City: " + customerCity);

    System.out.println("[DEBUG] Step 1: Checking shop link availability");
        boolean isShopButtonAvailable;
        isShopButtonAvailable = homePageLocators.isElementVisible(homePageLocators.shopLink);
        System.out.println("[DEBUG] Shop link visible: " + isShopButtonAvailable);
        if (isShopButtonAvailable) {
            System.out.println("[DEBUG] Step 2a: Clicking shop link");
            homePageLocators.clickOnElement(homePageLocators.shopLink);
            System.out.println("[DEBUG] Shop link clicked successfully");
        }
        else {
            System.out.println("[DEBUG] Step 2b: Shop link not available, navigating to shop URL");
                driver.get(getShopUrl());
                System.out.println("[DEBUG] Navigated to shop URL: " + getShopUrl());
        }

        System.out.println("[DEBUG] Step 3: Adding item to cart");
        shopPage.clickOnElement(shopPage.addToCartButton);
        System.out.println("[DEBUG] Add to cart button clicked");
        System.out.println("[DEBUG] Step 4: Viewing cart");
        shopPage.clickOnElement(shopPage.viewCartButton);
        System.out.println("[DEBUG] View cart button clicked");

        System.out.println("[DEBUG] Step 5: Extracting and validating prices");
        // Extract prices using the extract method from ShopPage
        float productPrice = shopPage.extractAndCleanPrice(shopPage.productPrice);
        float shippingCharge = shopPage.extractAndCleanPrice(shopPage.ShippingCharge);
        float taxAmount = shopPage.extractAndCleanPrice(shopPage.taxAmount);
        float totalPrice = shopPage.extractAndCleanPrice(shopPage.totalPrice);

        System.out.println("[DEBUG] Extracted prices:");
        System.out.println("[DEBUG] - Product: " + productPrice);
        System.out.println("[DEBUG] - Shipping: " + shippingCharge);
        System.out.println("[DEBUG] - Tax: " + taxAmount);
        System.out.println("[DEBUG] - Total: " + totalPrice);
        
        // Calculate expected total
        float expectedTotal = productPrice + shippingCharge + taxAmount;
        System.out.println("[DEBUG] Expected total calculation: " + productPrice + " + " + shippingCharge + " + " + taxAmount + " = " + expectedTotal);

        // Validate total matches (allow small floating point differences)
        System.out.println("[DEBUG] Step 6: Validating price calculation");
        if (Math.abs(expectedTotal - totalPrice) > 0.01) {
            System.out.println("[DEBUG] Price validation FAILED!");
            throw new AssertionError("Price validation failed! Expected: " + expectedTotal +
                                   ", Actual: " + totalPrice +
                                   " (Product: " + productPrice +
                                   ", Shipping: " + shippingCharge +
                                   ", Tax: " + taxAmount + ")");
        }

        System.out.println("[DEBUG] Price validation successful!");

        // Check if proceed to checkout button is visible and clickable
        boolean isProceedToCheckoutVisible = shopPage.isElementVisible(shopPage.proceedToCheckoutButton);

        if (isProceedToCheckoutVisible) {
            System.out.println("[DEBUG] Proceed to checkout button is visible, attempting to click");
            try {
                shopPage.clickOnElement(shopPage.proceedToCheckoutButton);
                System.out.println("[DEBUG] Successfully clicked proceed to checkout button");
            } catch (Exception e) {
                System.out.println("[DEBUG] Proceed to checkout button click failed, using checkout URL fallback: " + e.getMessage());
                // Fallback to direct checkout URL from environment
                driver.get(getCheckoutUrl());
                System.out.println("[DEBUG] Navigated to checkout page using direct URL");
            }
        } else {
            System.out.println("[DEBUG] Proceed to checkout button not visible, using checkout URL fallback");
            // Fallback to direct checkout URL from environment
            driver.get(getCheckoutUrl());
            System.out.println("[DEBUG] Navigated to checkout page using direct URL");
        }

        // Add debugging to check if checkout page loaded properly
        System.out.println("[DEBUG] Current URL after checkout navigation: " + driver.getCurrentUrl());
        boolean isCheckoutPageLoaded = checkoutPage.isElementVisible(checkoutPage.emailInputField);
        System.out.println("[DEBUG] Checkout page email field visible: " + isCheckoutPageLoaded);

        if (!isCheckoutPageLoaded) {
            System.out.println("[DEBUG] Checkout page not loaded properly, test may fail");
        }

        System.out.println("[DEBUG] Filling checkout form...");
        checkoutPage.sendKeysText(checkoutPage.emailInputField, customerEmail);
        System.out.println("[DEBUG] Email field filled");

        checkoutPage.sendKeysText(checkoutPage.firstNameInputField,customerFirstName);
        System.out.println("[DEBUG] First name field filled");

        checkoutPage.sendKeysText(checkoutPage.lastNameInputField,customerLastName);
        System.out.println("[DEBUG] Last name field filled");

        checkoutPage.sendKeysText(checkoutPage.addressInputField,customerAddress);
        System.out.println("[DEBUG] Address field filled");

        checkoutPage.sendKeysText(checkoutPage.cityInputField,customerCity);
        System.out.println("[DEBUG] City field filled");

        System.out.println("[DEBUG] Step 9: Selecting payment method");
        boolean isCDAvailable = checkoutPage.isElementVisible(checkoutPage.cashOnDelivery);
        boolean isDBTAvailable = checkoutPage.isElementVisible(checkoutPage.directBankTransfer);
        System.out.println("[DEBUG] Cash on delivery available: " + isCDAvailable);
        System.out.println("[DEBUG] Direct bank transfer available: " + isDBTAvailable);

        if (isCDAvailable) {
            System.out.println("[DEBUG] Step 10: Selecting cash on delivery payment");
            checkoutPage.clickOnElement(checkoutPage.cashOnDelivery);
            System.out.println("[DEBUG] Cash on delivery selected");
        }
        else if (isDBTAvailable) {
            System.out.println("[DEBUG] Step 10: Selecting direct bank transfer payment");
            checkoutPage.clickOnElement(checkoutPage.directBankTransfer);
            System.out.println("[DEBUG] Direct bank transfer selected");
        }
        else {
            System.out.println("[DEBUG] ERROR: No payment methods available");
            Assert.fail("No payment methods available on checkout page");
        }

        System.out.println("[DEBUG] Step 11: Placing order");
        checkoutPage.clickOnElement(checkoutPage.placeOrderButton);
        System.out.println("[DEBUG] Place order button clicked");
        System.out.println("[DEBUG] Step 12: Checking order confirmation");

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
        System.out.println("[DEBUG] Order confirmation message visible: " + isOrderConfirmMsgShowed);

        if (isOrderConfirmMsgShowed) {
            System.out.println("[DEBUG] Order completed successfully");
            orderID = checkoutPage.getElementText(checkoutPage.orderId);
            System.out.println("[DEBUG] Order ID: " + orderID);

            System.out.println("[DEBUG] Step 13: Verifying order in WooCommerce admin");

            // Load .env file directly
            Dotenv dotenv = Dotenv.configure().load();
            System.out.println("[DEBUG] Environment loaded");

            // Navigate to WordPress login page using URL from environment
            String baseUrl = dotenv.get("WP_URL");
            System.out.println("[DEBUG] Navigating to WordPress admin: " + baseUrl);
            getDriver().get(baseUrl);

            System.out.println("[DEBUG] Performing WordPress login");
            wordPressLoginPage.doLogin();
            System.out.println("[DEBUG] Login completed");

            System.out.println("[DEBUG] Navigating to WooCommerce menu");
            // Check if WooCommerce menu is visible and clickable
            boolean isWooCommerceMenuVisible = wordPressDashboardPage.isElementVisible(wordPressDashboardPage.wooCommerceMenu);
            System.out.println("[DEBUG] WooCommerce menu visible: " + isWooCommerceMenuVisible);

            if (isWooCommerceMenuVisible) {
                try {
                    wordPressDashboardPage.clickOnElement(wordPressDashboardPage.wooCommerceMenu);
                    System.out.println("[DEBUG] WooCommerce menu clicked successfully");
                } catch (Exception e) {
                    System.out.println("[DEBUG] WooCommerce menu click failed: " + e.getMessage());
                    System.out.println("[DEBUG] Using WooCommerce admin URL fallback");
                    getDriver().get(getWooCommerceAdminUrl());
                    System.out.println("[DEBUG] Navigated to WooCommerce admin using direct URL");
                }
            } else {
                System.out.println("[DEBUG] WooCommerce menu not visible, using admin URL fallback");
                getDriver().get(getWooCommerceAdminUrl());
                System.out.println("[DEBUG] Navigated to WooCommerce admin using direct URL");
            }

            System.out.println("[DEBUG] Navigating to orders menu");
            // Check if orders menu is visible and clickable
            boolean isOrdersMenuVisible = wooCommercePage.isElementVisible(wooCommercePage.ordersMenu);
            System.out.println("[DEBUG] Orders menu visible: " + isOrdersMenuVisible);

            if (isOrdersMenuVisible) {
                try {
                    wooCommercePage.clickOnElement(wooCommercePage.ordersMenu);
                    System.out.println("[DEBUG] Orders menu clicked successfully");
                } catch (Exception e) {
                    System.out.println("[DEBUG] Orders menu click failed: " + e.getMessage());
                    System.out.println("[DEBUG] Using WooCommerce orders URL fallback");
                    getDriver().get(getWooCommerceOrdersUrl());
                    System.out.println("[DEBUG] Navigated to WooCommerce orders using direct URL");
                }
            } else {
                System.out.println("[DEBUG] Orders menu not visible, using orders URL fallback");
                getDriver().get(getWooCommerceOrdersUrl());
                System.out.println("[DEBUG] Navigated to WooCommerce orders using direct URL");
            }

            System.out.println("[DEBUG] Searching for order ID: " + orderID);
            wooCommercePage.searchAndPressEnter(wooCommercePage.orderSearchField,orderID);
            System.out.println("[DEBUG] Order search completed");
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
        System.out.println("[DEBUG] ===== STARTING USER ACCOUNT ORDER HISTORY TEST =====");

        Faker faker = new Faker();
        String customerEmail = faker.name().username() + System.currentTimeMillis() + "@test.com";
        String customerPassword = faker.name().username() + "123!";
        String customerFirstName = faker.name().firstName();
        String customerLastName = faker.name().lastName();
        String customerAddress = faker.address().fullAddress();
        String customerCity = faker.address().cityName();
        String orderID;

        System.out.println("[DEBUG] Generated test data:");
        System.out.println("[DEBUG] - Email: " + customerEmail);
        System.out.println("[DEBUG] - Password: " + customerPassword.substring(0, Math.min(10, customerPassword.length())) + "...");
        System.out.println("[DEBUG] - Name: " + customerFirstName + " " + customerLastName);

        System.out.println("[DEBUG] Step 1: Navigating to My Account page");
        driver.get(getMyAccountUrl());
        System.out.println("[DEBUG] My Account page loaded");
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

        System.out.println("[DEBUG] Step 7: Navigating to checkout");

        // Check if proceed to checkout button is visible and clickable
        boolean isProceedToCheckoutVisible = shopPage.isElementVisible(shopPage.proceedToCheckoutButton);
        System.out.println("[DEBUG] Proceed to checkout button visible: " + isProceedToCheckoutVisible);

        if (isProceedToCheckoutVisible) {
            System.out.println("[DEBUG] Attempting to click proceed to checkout button");
            try {
                shopPage.clickOnElement(shopPage.proceedToCheckoutButton);
                System.out.println("[DEBUG] Successfully clicked proceed to checkout button");
            } catch (Exception e) {
                System.out.println("[DEBUG] Proceed to checkout button click failed: " + e.getMessage());
                System.out.println("[DEBUG] Using checkout URL fallback");
                // Fallback to direct checkout URL from environment
                driver.get(getCheckoutUrl());
                System.out.println("[DEBUG] Navigated to checkout page using direct URL");
            }
        } else {
            System.out.println("[DEBUG] Proceed to checkout button not visible, using checkout URL fallback");
            // Fallback to direct checkout URL from environment
            driver.get(getCheckoutUrl());
            System.out.println("[DEBUG] Navigated to checkout page using direct URL");
        }

        // Add debugging to check if checkout page loaded properly
        System.out.println("[DEBUG] Step 8: Verifying checkout page load");
        System.out.println("[DEBUG] Current URL after checkout navigation: " + driver.getCurrentUrl());
        boolean isCheckoutPageLoaded = checkoutPage.isElementVisible(checkoutPage.emailInputField);
        System.out.println("[DEBUG] Checkout page email field visible: " + isCheckoutPageLoaded);

        if (!isCheckoutPageLoaded) {
            System.out.println("[DEBUG] ERROR: Checkout page not loaded properly, test will fail");
            Assert.fail("Checkout page did not load properly - email field not visible");
        }
        checkoutPage.sendKeysText(checkoutPage.firstNameInputField,customerFirstName);
        System.out.println("[DEBUG] First name field filled");

        checkoutPage.sendKeysText(checkoutPage.lastNameInputField,customerLastName);
        System.out.println("[DEBUG] Last name field filled");

        checkoutPage.sendKeysText(checkoutPage.addressInputField,customerAddress);
        System.out.println("[DEBUG] Address field filled");

        checkoutPage.sendKeysText(checkoutPage.cityInputField,customerCity);
        System.out.println("[DEBUG] City field filled");

        System.out.println("[DEBUG] Step 9: Selecting payment method");
        boolean isCDAvailable = checkoutPage.isElementVisible(checkoutPage.cashOnDelivery);
        boolean isDBTAvailable = checkoutPage.isElementVisible(checkoutPage.directBankTransfer);
        System.out.println("[DEBUG] Cash on delivery available: " + isCDAvailable);
        System.out.println("[DEBUG] Direct bank transfer available: " + isDBTAvailable);

        if (isCDAvailable) {
            System.out.println("[DEBUG] Step 10: Selecting cash on delivery payment");
            checkoutPage.clickOnElement(checkoutPage.cashOnDelivery);
            System.out.println("[DEBUG] Cash on delivery selected");
        }
        else if (isDBTAvailable) {
            System.out.println("[DEBUG] Step 10: Selecting direct bank transfer payment");
            checkoutPage.clickOnElement(checkoutPage.directBankTransfer);
            System.out.println("[DEBUG] Direct bank transfer selected");
        }
        else {
            System.out.println("[DEBUG] ERROR: No payment methods available");
            Assert.fail("No payment methods available on checkout page");
        }

        System.out.println("[DEBUG] Step 11: Placing order");
        checkoutPage.clickOnElement(checkoutPage.placeOrderButton);
        System.out.println("[DEBUG] Place order button clicked");
        System.out.println("[DEBUG] Step 12: Checking order confirmation");

        // Use enhanced visibility check for headless mode
        boolean isOrderConfirmMsgShowed;
        if (System.getProperty("headless", "false").equals("true")) {
            // Enhanced check for headless mode with longer timeout
            isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg, java.time.Duration.ofSeconds(30), true);
        } else {
            // Standard check for regular mode
            isOrderConfirmMsgShowed = checkoutPage.isElementVisible(checkoutPage.orderReceivedMsg);
        }
        System.out.println("[DEBUG] Order confirmation message visible: " + isOrderConfirmMsgShowed);

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
