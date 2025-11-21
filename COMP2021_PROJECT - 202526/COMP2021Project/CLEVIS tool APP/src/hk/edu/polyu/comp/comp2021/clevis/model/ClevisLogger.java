package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClevisLogger {
    private static Path LOG_FILE_PATH;
    private static Path HTML_FILE_PATH;
    private static PrintWriter logWriter;
    private static PrintWriter htmlWriter;
    private static int logIndex = 1;
    private static boolean initialized = false;

    /**
     * Initialize logger with user-specified file paths
     */
    public static void initializeLogger(String txtPath, String htmlPath) {
        close();
        
        LOG_FILE_PATH = Paths.get(txtPath);
        HTML_FILE_PATH = Paths.get(htmlPath);
        logIndex = 1;
        initialized = false;

        try {
            // Create parent directories if they don't exist
            createParentDirs(LOG_FILE_PATH);
            createParentDirs(HTML_FILE_PATH);

            // Initialize text log file (overwrite mode)
            logWriter = new PrintWriter(
                Files.newBufferedWriter(LOG_FILE_PATH, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE));

            // Initialize HTML log file (overwrite mode)
            htmlWriter = new PrintWriter(
                Files.newBufferedWriter(HTML_FILE_PATH,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE));
            
            // Write HTML header
            htmlWriter.println("<!DOCTYPE html>");
            htmlWriter.println("<html>");
            htmlWriter.println("<head>");
            htmlWriter.println("<title>Clevis Command Log</title>");
            htmlWriter.println("<style>");
            htmlWriter.println("table { border-collapse: collapse; width: 100%; }");
            htmlWriter.println("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
            htmlWriter.println("th { background-color: #f2f2f2; }");
            htmlWriter.println("</style>");
            htmlWriter.println("</head>");
            htmlWriter.println("<body>");
            htmlWriter.println("<h1>Clevis Command Log</h1>");
            htmlWriter.println("<table>");
            htmlWriter.println("<tr><th>Index</th><th>Command</th></tr>");
            
            initialized = true;
            System.out.println("Logger initialized successfully");
            System.out.println("  Text log: " + LOG_FILE_PATH.toAbsolutePath());
            System.out.println("  HTML log: " + HTML_FILE_PATH.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            e.printStackTrace();
            initialized = false;
        }
    }

    /**
     * Create parent directories for a file path, handling null parent (current directory)
     */
    private static void createParentDirs(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
            System.out.println("Created directory: " + parent.toAbsolutePath());
        }
        // If parent is null, the file is in current directory - no need to create directories
    }

    /**
     * Log a command to both files according to requirements
     */
    public static void logCommand(String command) {
        if (!initialized || logWriter == null || htmlWriter == null) {
            System.err.println("Logger not initialized properly. Call initializeLogger() first.");
            return;
        }

        try {
            // Write to plain text file (one command per line)
            logWriter.println(command);
            logWriter.flush();

            // Write to HTML file (table row with index and command)
            htmlWriter.println("<tr>");
            htmlWriter.println("<td>" + logIndex + "</td>");
            // Escape HTML special characters in the command
            String escapedCommand = command.replace("&", "&amp;")
                                         .replace("<", "&lt;")
                                         .replace(">", "&gt;")
                                         .replace("\"", "&quot;")
                                         .replace("'", "&#39;");
            htmlWriter.println("<td>" + escapedCommand + "</td>");
            htmlWriter.println("</tr>");
            htmlWriter.flush();
            
            logIndex++;

        } catch (Exception e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    /**
     * Close the logger and write HTML footer
     */
    public static void close() {
        if (htmlWriter != null) {
            try {
                htmlWriter.println("</table>");
                htmlWriter.println("</body>");
                htmlWriter.println("</html>");
            } catch (Exception e) {
                // Ignore errors during closing
            }
            htmlWriter.close();
            htmlWriter = null;
        }
        
        if (logWriter != null) {
            logWriter.close();
            logWriter = null;
        }
        
        initialized = false;
    }

    /**
     * Check if logger is properly initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Get the current log index (for testing purposes)
     */
    public static int getLogIndex() {
        return logIndex;
    }
}