package utilities;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigManager {
    private static final Dotenv dotenv = Dotenv.configure().load();
    
    public static String get(String key) {
        return dotenv.get(key);
    }
    
    public static String get(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }
    
    // URL Configuration
    public static String getBaseUrl() {
        return get("BASE_URL");
    }
    
    public static String getApiBaseUrl() {
        return get("API_BASE_URL");
    }
    
    // Credentials
    public static String getTestUsername() {
        return get("TEST_USERNAME");
    }
    
    public static String getTestPassword() {
        return get("TEST_PASSWORD");
    }
    
    public static String getAdminUsername() {
        return get("ADMIN_USERNAME");
    }
    
    public static String getAdminPassword() {
        return get("ADMIN_PASSWORD");
    }
    
    // Browser Configuration
    public static String getBrowser() {
        return get("BROWSER", "chrome");
    }
    
    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("HEADLESS", "false"));
    }
    
    public static int getBrowserTimeout() {
        return Integer.parseInt(get("BROWSER_TIMEOUT", "30"));
    }
    
    // Wait Times
    public static int getImplicitWait() {
        return Integer.parseInt(get("IMPLICIT_WAIT", "10"));
    }
    
    public static int getExplicitWait() {
        return Integer.parseInt(get("EXPLICIT_WAIT", "20"));
    }
    
    public static int getPageLoadTimeout() {
        return Integer.parseInt(get("PAGE_LOAD_TIMEOUT", "30"));
    }
}