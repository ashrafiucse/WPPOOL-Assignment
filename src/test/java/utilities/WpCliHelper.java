package utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WpCliHelper {

    private String wpPath;
    private String sitePath;

    public WpCliHelper(String wpPath, String sitePath) {
        this.wpPath = wpPath;
        this.sitePath = sitePath;
    }

    public String runCommand(String command) throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command.split(" "));
        builder.directory(new java.io.File(sitePath));
        builder.redirectErrorStream(true);

        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();
        return output.toString().trim();
    }

    public String createPage(String title, String content) throws Exception {
        String cmd = wpPath + " post create --post_title=\"" + title + "\" --post_content=\"" + content + "\" --post_status=publish --porcelain";
        return runCommand(cmd); // returns page ID
    }

    public String getPageUrl(String pageId) throws Exception {
        String cmd = wpPath + " post get " + pageId + " --field=link";
        return runCommand(cmd);
    }
}
