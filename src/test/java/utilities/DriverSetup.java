package utilities;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverSetup {
    // Initialize console encoding to handle special characters properly
    static {
        try {
            // Load logging configuration to suppress WebDriver warnings
            InputStream logConfig =
                    DriverSetup.class.getClassLoader().getResourceAsStream(
                            "logging.properties"
                    );
            if (logConfig != null) {
                LogManager.getLogManager().readConfiguration(logConfig);
            }

            // Additional programmatic suppression as fallback
            Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
            Logger.getLogger("org.openqa.selenium.devtools").setLevel(
                    Level.SEVERE
            );
            Logger.getLogger("org.openqa.selenium.chromium").setLevel(
                    Level.SEVERE
            );

            // Set system properties for proper character encoding
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("console.encoding", "UTF-8");

            // Suppress WebDriver warnings
            System.setProperty("webdriver.chrome.verboseLogging", "false");
            System.setProperty("webdriver.chrome.silentOutput", "true");
            System.setProperty("selenium.logs.level", "SEVERE");
        } catch (Exception e) {
            // Fallback silently if encoding setup fails
            System.err.println(
                    "[WARN] Could not set UTF-8 encoding: " + e.getMessage()
            );
        }
    }

    private static final ThreadLocal<String> BROWSER_NAME = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> HEADLESS_MODE =
            new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> LOCAL_DRIVER =
            new ThreadLocal<>();

    public static void initDriver() {
        initDriver(null, null);
    }

    public static void initDriver(String browser, Boolean headless) {
        if (LOCAL_DRIVER.get() == null) {
            // Set browser and headless mode for this thread
            BROWSER_NAME.set(
                    browser != null
                            ? browser
                            : System.getProperty("browser", "chrome")
            );
            HEADLESS_MODE.set(
                    headless != null
                            ? headless
                            : Boolean.parseBoolean(
                            System.getProperty("headless", "false")
                    )
            );

            WebDriver driver = createBrowser(BROWSER_NAME.get());
            driver.manage().window().maximize();
            // Remove implicit wait to use explicit waits only
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            // Set page load timeout
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // Set script timeout for JavaScript executions
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));

            LOCAL_DRIVER.set(driver);
        }
    }

    public static WebDriver getDriver() {
        return LOCAL_DRIVER.get();
    }

    public static String getCurrentBrowser() {
        String browser = BROWSER_NAME.get();
        if (browser == null) {
            browser = System.getProperty("browser", "chrome");
        }
        return browser;
    }

    public static Boolean isHeadless() {
        Boolean headless = HEADLESS_MODE.get();
        if (headless == null) {
            headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        }
        return headless;
    }

    public static void quitDriver() {
        WebDriver driver = LOCAL_DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error closing driver: " + e.getMessage());
            } finally {
                LOCAL_DRIVER.remove();
                BROWSER_NAME.remove();
                HEADLESS_MODE.remove();
            }
        }
    }

    private static WebDriver createBrowser(String browserName) {
        boolean headless = HEADLESS_MODE.get();

        switch (browserName.toLowerCase()) {
            case "chrome":
                return createChromeDriver(headless);
            case "firefox":
                return createFirefoxDriver(headless);
            case "edge":
                return createEdgeDriver(headless);
            default:
                throw new RuntimeException(
                        "Browser not supported: " +
                                browserName +
                                ". Supported browsers: chrome, firefox, edge"
                );
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        // Check project drivers folder first
        String projectDriverPath = "./drivers/chromedriver.exe";
        if (new java.io.File(projectDriverPath).exists()) {
            System.setProperty("webdriver.chrome.driver", projectDriverPath);
        } else {
            try {
                WebDriverManager.chromedriver().setup();
            } catch (Exception e) {
                // Silently continue if WebDriverManager fails
            }
        }

        ChromeOptions options = new ChromeOptions();

        // Common Chrome options for stability and performance
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption(
                "excludeSwitches",
                new String[] { "enable-automation" }
        );

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-logging");
            options.addArguments("--disable-dev-tools");
            options.addArguments("--no-first-run");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");
            options.addArguments("--disable-features=TranslateUI");
            options.addArguments("--password-store=basic");
            options.addArguments("--use-mock-keychain");
            // Additional headless mode fixes for WordPress
            options.addArguments("--force-device-scale-factor=1");
            options.addArguments("--disable-features=TranslateUI,VizDisplayCompositor");
            options.addArguments("--disable-ipc-flooding-protection");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--disable-accelerated-2d-canvas");
            options.addArguments("--disable-accelerated-jpeg-decoding");
            options.addArguments("--disable-accelerated-video-decode");
        }

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        // Check project drivers folder first
        String projectDriverPath = "./drivers/geckodriver.exe";
        if (new java.io.File(projectDriverPath).exists()) {
            System.setProperty("webdriver.gecko.driver", projectDriverPath);
        } else {
            try {
                WebDriverManager.firefoxdriver().setup();
            } catch (Exception e) {
                // Silently try alternative setup
                try {
                    // Alternative: Try to setup with specific configuration
                    WebDriverManager.firefoxdriver()
                            .clearDriverCache()
                            .clearResolutionCache()
                            .setup();
                } catch (Exception e2) {
                    // Try to use GeckoDriver from system PATH
                    String geckoDriverPath = findGeckoDriverInSystem();
                    if (geckoDriverPath != null) {
                        System.setProperty(
                                "webdriver.gecko.driver",
                                geckoDriverPath
                        );
                    } else {
                        System.err.println(
                                "[WARN] Could not find GeckoDriver. Please ensure geckodriver.exe is in your PATH or download it manually."
                        );
                    }
                }
            }
        }

        FirefoxOptions options = new FirefoxOptions();

        // Enhanced Firefox options for performance and stability
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.volume_scale", "0.0");
        options.addPreference("media.autoplay.default", 5); // Block autoplay
        options.addPreference("permissions.default.image", 1); // Load images normally
        options.addPreference("dom.ipc.plugins.enabled", false); // Disable plugins for performance
        options.addPreference("extensions.update.enabled", false); // Disable extension updates
        options.addPreference("app.update.enabled", false); // Disable browser updates
        
        // Performance optimizations
        options.addPreference("browser.cache.disk.enable", false);
        options.addPreference("browser.cache.memory.enable", false);
        options.addPreference("browser.cache.offline.enable", false);
        options.addPreference("network.http.use-cache", false);
        options.addPreference("network.http.pipelining", true);
        options.addPreference("network.http.proxy.pipelining", true);
        options.addPreference("network.http.pipelining.maxrequests", 8);
        options.addPreference("content.notify.interval", 500);
        options.addPreference("content.notify.ontimer", true);
        options.addPreference("content.switch.threshold", 500);
        options.addPreference("browser.sessionstore.interval", 999999);
        
        // Security and privacy settings
        options.addPreference("privacy.trackingprotection.enabled", false); // Disable for testing
        options.addPreference("security.fileuri.strict_origin_policy", false);
        options.addPreference("signon.rememberSignons", false);

        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        try {
            // First priority: Check project drivers folder
            String projectDriverPath = "./drivers/msedgedriver.exe";
            if (new java.io.File(projectDriverPath).exists()) {
                System.setProperty("webdriver.edge.driver", projectDriverPath);
            } else {
                // Second priority: Find in system locations
                String edgeDriverPath = findEdgeDriverInSystem();
                if (edgeDriverPath != null) {
                    System.setProperty("webdriver.edge.driver", edgeDriverPath);
                } else {
                    // Last resort: Try WebDriverManager
                    try {

                        WebDriverManager.edgedriver()
                                .clearDriverCache()
                                .clearResolutionCache()
                                .setup();

                    } catch (Exception wdmException) {
                        // Provide detailed setup instructions
                        String edgeVersion = getEdgeBrowserVersion();
                        System.err.println("[ERROR] Edge driver setup failed!");
                        System.err.println("");
                        System.err.println("=== EDGE DRIVER SETUP REQUIRED ===");
                        System.err.println("Your Edge version: " + (edgeVersion != null ? edgeVersion : "Unknown"));
                        System.err.println("");
                        System.err.println("QUICK FIX:");
                        System.err.println("1. Download: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/");
                        if (edgeVersion != null) {
                            System.err.println("2. Get driver for version: " + edgeVersion);
                        }
                        System.err.println("3. Extract msedgedriver.exe to: " + new java.io.File("./drivers/").getAbsolutePath());
                        System.err.println("4. Run tests again");
                        System.err.println("");
                        System.err.println("Alternative: Place driver in system PATH");
                        System.err.println("================================");

                        throw new RuntimeException("Edge driver setup required - see instructions above", wdmException);
                    }
                }
            }
        } catch (RuntimeException re) {
            throw re; // Re-throw setup instructions
        } catch (Exception e) {
            System.err.println("[ERROR] Edge driver initialization failed: " + e.getMessage());
            throw new RuntimeException("Edge driver initialization failed", e);
        }

        EdgeOptions options = new EdgeOptions();

        // Enhanced Edge options for performance and stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-web-security");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption(
                "excludeSwitches",
                new String[] { "enable-automation" }
        );
        
        // Performance optimizations
        options.addArguments("--disable-features=VizDisplayCompositor,TranslateUI,BlinkGenPropertyTrees");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--disable-logging");
        options.addArguments("--disable-extensions-except");
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--password-store=basic");
        options.addArguments("--use-mock-keychain");
        
        // Memory and CPU optimizations
        options.addArguments("--max_old_space_size=4096");
        options.addArguments("--optimize-for-size");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-accelerated-2d-canvas");
        options.addArguments("--disable-accelerated-jpeg-decoding");
        options.addArguments("--disable-accelerated-video-decode");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-logging");
            options.addArguments("--no-first-run");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-dev-tools");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-sync");
            options.addArguments("--metrics-recording-only");
            options.addArguments("--no-default-browser-check");
        }

        return new EdgeDriver(options);
    }

    private static String findEdgeDriverInSystem() {
        // Common locations where Edge driver might be found
        String[] possiblePaths = {
                "msedgedriver.exe", // If it's in PATH
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedgedriver.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedgedriver.exe",
                System.getProperty("user.home") +
                        "\\AppData\\Local\\Microsoft\\Edge\\Application\\msedgedriver.exe",
                System.getProperty("user.home") +
                        "\\AppData\\Local\\Programs\\Microsoft\\Edge\\Application\\msedgedriver.exe",
                ".\\msedgedriver.exe",
                // Check in WebDriverManager cache
                System.getProperty("user.home") + "\\.cache\\selenium\\msedgedriver.exe",
                System.getProperty("user.home") + "\\.m2\\repository\\webdriver\\msedgedriver.exe"
        };

        for (String path : possiblePaths) {
            try {
                // First check if file exists
                java.io.File file = new java.io.File(path);
                if (file.exists() && file.canExecute()) {
                    // Test if it's actually the Edge driver
                    ProcessBuilder pb = new ProcessBuilder(path, "--version");
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        return path;
                    }
                }
            } catch (Exception e) {
                // Continue trying other paths
                System.out.println("[DEBUG] Could not verify driver at " + path + ": " + e.getMessage());
            }
        }

        return null;
    }

    private static String findEdgeBrowser() {
        String[] edgePaths = {
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
                System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Edge\\Application\\msedge.exe"
        };

        for (String path : edgePaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                return path;
            }
        }
        return null;
    }

    private static String getEdgeBrowserVersion() {
        String edgePath = findEdgeBrowser();
        if (edgePath != null) {
            try {
                ProcessBuilder pb = new ProcessBuilder("powershell", "-Command",
                        "(Get-ItemProperty '" + edgePath + "').VersionInfo.ProductVersion");
                pb.redirectErrorStream(true);
                Process process = pb.start();
                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());
                if (scanner.hasNextLine()) {
                    String version = scanner.nextLine().trim();
                    scanner.close();
                    return version;
                }
                scanner.close();
            } catch (Exception e) {
                // Ignore and return null
            }
        }
        return null;
    }

    private static String findGeckoDriverInSystem() {
        // Common locations where GeckoDriver might be found
        String[] possiblePaths = {
                "./drivers/geckodriver.exe", // Local drivers folder (first priority)
                ".\\drivers\\geckodriver.exe", // Local drivers folder (Windows path)
                "geckodriver.exe", // If it's in PATH
                "geckodriver", // If it's in PATH (no extension)
                "C:\\Program Files\\Mozilla Firefox\\geckodriver.exe",
                "C:\\Program Files (x86)\\Mozilla Firefox\\geckodriver.exe",
                System.getProperty("user.home") +
                        "\\AppData\\Local\\Mozilla Firefox\\geckodriver.exe",
                ".\\geckodriver.exe",
        };

        for (String path : possiblePaths) {
            try {
                ProcessBuilder pb = new ProcessBuilder(path, "--version");
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return path;
                }
            } catch (Exception ignored) {
                // Continue trying other paths
            }
        }

        return null;
    }
}
