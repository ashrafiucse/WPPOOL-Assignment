package utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WpCliUtils {

    // Run terminal commands
    public static String runCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    // Create WP Page with shortcode
    public static String createPageWithShortcode(String title, String shortcode) {
        String cmd = "wp post create "
                + "--post_type=page "
                + "--post_status=publish "
                + "--post_title=\"" + title + "\" "
                + "--post_content=\"" + shortcode + "\"";

        String result = runCommand(cmd);
        System.out.println("WP-CLI Create Page Output:\n" + result);

        return extractPageId(result);
    }

    // Extract Page ID from WP-CLI output
    private static String extractPageId(String cliOutput) {
        Pattern pattern = Pattern.compile("post (\\d+)");
        Matcher matcher = pattern.matcher(cliOutput);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    // Fetch Page URL using Page ID
    public static String getPageUrl(String pageId) {
        String cmd = "wp post url " + pageId;
        return runCommand(cmd).trim();
    }
}
