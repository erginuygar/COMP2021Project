package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClevisLogger — handles logging for REQ1.
 * Logs all executed commands to both text and HTML files.
 */
public class ClevisLogger {

    private final File txtLog;
    private final File htmlLog;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Create a new logger that writes to given HTML and TXT file paths.
    public ClevisLogger(String htmlPath, String txtPath) {
        this.htmlLog = new File(htmlPath);
        this.txtLog = new File(txtPath);
        initializeLogs();
    }

    // Initialize log files with headers if they don't exist.
    private void initializeLogs() {
        try {
            if (!txtLog.exists()) {
                txtLog.createNewFile();
                try (FileWriter fw = new FileWriter(txtLog, true)) {
                    fw.write("=== CLEVIS LOG START ===\n");
                }
            }

            if (!htmlLog.exists()) {
                htmlLog.createNewFile();
                try (FileWriter fw = new FileWriter(htmlLog, true)) {
                    fw.write("<html><head><title>Clevis Log</title></head><body>");
                    fw.write("<h2>Clevis Command Log</h2><ul>\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing logs: " + e.getMessage());
        }
    }

    // Log a single command with a timestamp (for REQ1).
    public void logCommand(String command) {
        String time = timeFormat.format(new Date());
        String txtLine = "[" + time + "] " + command + "\n";
        String htmlLine = "<li><b>" + time + ":</b> " + escapeHTML(command) + "</li>\n";

        try (FileWriter txtWriter = new FileWriter(txtLog, true);
             FileWriter htmlWriter = new FileWriter(htmlLog, true)) {
            txtWriter.write(txtLine);
            htmlWriter.write(htmlLine);
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }

    // Close the HTML log properly (called at program end).
    public void close() {
        try (FileWriter fw = new FileWriter(htmlLog, true)) {
            fw.write("</ul></body></html>");
        } catch (IOException e) {
            System.err.println("Error closing HTML log: " + e.getMessage());
        }
    }

    // Escape HTML special characters for safety.
    private static String escapeHTML(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
