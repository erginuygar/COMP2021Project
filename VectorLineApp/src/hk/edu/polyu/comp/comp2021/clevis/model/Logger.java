package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Logger {
    private final static Path DIRECTORY_PATH = Paths.get("VectorLineApp/src/hk/edu/polyu/comp/comp2021/clevis/model/logs/");
    
    private static Path LOG_FILE_PATH;
    private static Path HTML_FILE_PATH;
    private static PrintWriter logWriter;
    private static PrintWriter htmlWriter;
    private static int logIndex = 1; // Add a static counter for the index

    // No static initializer block needed.

    public static void initializeLogger(String logFileName, String htmlFileName) {
        close(); // Close existing writers if re-initializing

        LOG_FILE_PATH = DIRECTORY_PATH.resolve(logFileName);
        HTML_FILE_PATH = DIRECTORY_PATH.resolve(htmlFileName);
        logIndex = 1; // Reset index when initializing

        try {
            Files.createDirectories(DIRECTORY_PATH); 
            // ... (print statements omitted for brevity) ...

            logWriter = new PrintWriter(
                Files.newBufferedWriter(LOG_FILE_PATH, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.APPEND,
                    StandardOpenOption.WRITE));
            
            htmlWriter = new PrintWriter(
                Files.newBufferedWriter(HTML_FILE_PATH,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE));
            
            // Write the initial HTML structure and table header
            htmlWriter.println("<!DOCTYPE html>");
            htmlWriter.println("<html>");
            htmlWriter.println("<head><title>Application Log Report</title></head>");
            htmlWriter.println("<body><h1>Application Activity Log</h1>");
            htmlWriter.println("<table border=\"1\">"); // Start the HTML table with a border
            htmlWriter.println("<tr><th>Index</th><th>Method Log</th></tr>"); // Table Header Row

            // ... (print statements omitted for brevity) ...

        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void log(String className, String methodName, Object... arguments) {
        if (logWriter == null || htmlWriter == null) {
            System.err.println("Logger not initialized properly. Call initializeLogger() first.");
            return;
        }
        
        String parsedArgs = messageParser(className, methodName, arguments);
        
        // Write to the plain text file
        logWriter.println(parsedArgs);
        logWriter.flush();

        // Write to the HTML file in a table row format
        htmlWriter.println("<tr>");
        htmlWriter.println("<td>" + logIndex + "</td>");
        // Escape potential HTML characters in the log message to prevent issues
        String safeParsedArgs = parsedArgs.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        htmlWriter.println("<td>" + safeParsedArgs + "</td>");
        htmlWriter.println("</tr>");
        htmlWriter.flush();
        
        logIndex++; // Increment the index for the next log entry
    }

    // messageParser method remains the same as before
    public static String messageParser(String className, String methodName, Object... arguments) {
        StringBuilder parsed = new StringBuilder();
        parsed.append(className).append(".").append(methodName);
        // ... (rest of the messageParser implementation) ...
        if (arguments == null || arguments.length == 0) {
            parsed.append("()");
        } else {
            parsed.append("(");
            for (int i = 0; i < arguments.length; i++) {
                if (i > 0) {
                    parsed.append(",");
                }
                
                if (arguments[i] == null) {
                    parsed.append("null");
                } else {
                    String simpleClassName = arguments[i].getClass().getSimpleName();
                    parsed.append(simpleClassName).append(":").append(arguments[i]);
                }
            }
            parsed.append(")");
        }
        return parsed.toString();
    }

    public static void close() {
        if (logWriter != null) {
            logWriter.close();
            logWriter = null;
        }
        if (htmlWriter != null) {
            // Write closing HTML tags including the table closing tag
            htmlWriter.println("</table>"); // Close the table tag
            htmlWriter.println("</body></html>");
            htmlWriter.close();
            htmlWriter = null;
        }
    }
    
    // clearAlt method remains the same
    public static void clearAlt() {
        if (LOG_FILE_PATH == null) {
            System.err.println("Cannot clear log file: Logger not initialized.");
            return;
        }
        try {
            Files.deleteIfExists(LOG_FILE_PATH); 
            System.out.println("Log file cleared successfully. Call initializeLogger again to start logging.");
            close(); 
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        Logger.initializeLogger("log.txt", "log.html");
        Logger.log("n","b",'c',1,2);
    }
    

}