package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles logging for REQ1.
 * <p>
 * Logs all executed commands to both text and HTML files.
 */
public final class ClevisLogger {

    private final File txtLog;
    private final File htmlLog;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new logger that writes to the given HTML and text file paths.
     *
     * @param htmlPath path to HTML log file
     * @param txtPath  path to text log file
     */
    public ClevisLogger(final String htmlPath, final String txtPath) {
        // Use project-relative path based on class location
        final String logPath = System.getProperty("user.dir")
                + File.separator + "VectorLineApp"
                + File.separator + "src"
                + File.separator + "hk"
                + File.separator + "edu"
                + File.separator + "polyu"
                + File.separator + "comp"
                + File.separator + "comp2021"
                + File.separator + "clevis"
                + File.separator + "model"
                + File.separator + "logs";

        File logDir = new File(logPath);
        if (!logDir.exists() && !logDir.mkdirs()) {
            System.err.println("Warning: Failed to create logs directory at: " + logDir.getAbsolutePath());
        }

        this.htmlLog = new File(logDir, htmlPath);
        this.txtLog = new File(logDir, txtPath);
        initializeLogs();
    }

    /**
     * Initializes log files with headers if they do not already exist.
     */
    private void initializeLogs() {
        try {
            if (!txtLog.exists() && txtLog.createNewFile()) {
                try (FileWriter fw = new FileWriter(txtLog, true)) {
                    fw.write("=== CLEVIS LOG START ===\n");
                }
            }

            if (!htmlLog.exists() && htmlLog.createNewFile()) {
                try (FileWriter fw = new FileWriter(htmlLog, true)) {
                    fw.write("""
                        <html><head><title>Clevis Log</title></head><body>
                        <h2>Clevis Command Log</h2><ul>
                        """);
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing logs: " + e.getMessage());
        }
    }

    /**
     * Logs a single command with a timestamp (REQ1).
     *
     * @param command the command to log
     */
    public void logCommand(final String command) {
        final String time = timeFormat.format(new Date());
        final String txtLine = "[" + time + "] " + command + "\n";
        final String htmlLine = "<li><b>" + time + ":</b> " + escapeHTML(command) + "</li>\n";

        try (FileWriter txtWriter = new FileWriter(txtLog, true);
             FileWriter htmlWriter = new FileWriter(htmlLog, true)) {
            txtWriter.write(txtLine);
            htmlWriter.write(htmlLine);
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }

    /**
     * Closes the HTML log properly (called at program end).
     */
    public void close() {
        try (FileWriter fw = new FileWriter(htmlLog, true)) {
            fw.write("</ul></body></html>");
        } catch (IOException e) {
            System.err.println("Error closing HTML log: " + e.getMessage());
        }
    }

    /**
     * Escapes HTML special characters for safety.
     *
     * @param s string to escape
     * @return escaped HTML-safe string
     */
    private static String escapeHTML(final String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
