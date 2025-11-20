package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClevisLogger {
    // ... [DIRECTORY_PATH, LOG_FILE_PATH, HTML_FILE_PATH, writers, logIndex remain the same] ...
    private final static Path DIRECTORY_PATH = Paths.get("COMP2021_PROJECT - 202526/COMP2021Project/CLEVIS tool APP/src/hk/edu/polyu/comp/comp2021/clevis/model/logs");
    
    private static Path LOG_FILE_PATH;
    private static Path HTML_FILE_PATH;
    private static PrintWriter logWriter;
    private static PrintWriter htmlWriter;
    private static int logIndex = 1; 

    public static void initializeLogger(String logFileName, String htmlFileName) {
        // ... [Initialization logic remains the same] ...
        close(); 
        LOG_FILE_PATH = DIRECTORY_PATH.resolve(logFileName);
        HTML_FILE_PATH = DIRECTORY_PATH.resolve(htmlFileName);
        logIndex = 1; 

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
            
            htmlWriter.println("<!DOCTYPE html>");
            htmlWriter.println("<html>");
            htmlWriter.println("<head><title>Application Log Report</title></head>");
            htmlWriter.println("<body><h1>Application Activity Log</h1>");
            htmlWriter.println("<table border=\"1\">"); 
            htmlWriter.println("<tr><th>Index</th><th>Method Log</th></tr>"); 

        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Automatically retrieves calling class and method names using the call stack.
     */
       public static void log(Object... arguments) {
        if (logWriter == null || htmlWriter == null) {
            System.err.println("Logger not initialized properly. Call initializeLogger() first.");
            return;
        }

        // Get the stack trace element for the method that called 'ClevisLogger.log()'
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // Index 0 is getStackTrace(), Index 1 is ClevisLogger.log(), Index 2 is the actual caller.
        StackTraceElement caller = stackTrace[2]; 
        
        // Extract only the simple class name (right-most part after last dot)
        String fullClassName = caller.getClassName();
        String simpleClassName = getSimpleClassName(fullClassName);
        String methodName = caller.getMethodName();

        String parsedArgs = messageParser(simpleClassName, methodName, arguments);
        
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

    /**
     * Extracts the simple class name from a fully qualified class name.
     * Example: "hk.edu.polyu.comp.comp2021.clevis.controller.Clevis" -> "Clevis"
     */
private static String getSimpleClassName(String fullClassName) {

    int lastDotIndex = fullClassName.lastIndexOf('.');
    String simpleName = (lastDotIndex != -1) ? 
        fullClassName.substring(lastDotIndex + 1) : fullClassName;

    return simpleName;
}

    // messageParser method remains the same (it still needs class/method names internally)
    public static String messageParser(String className, String methodName, Object... arguments) {
        StringBuilder parsed = new StringBuilder();
        parsed.append(className).append(".").append(methodName);
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

    // ... [close(), clearAlt() methods remain the same] ...
    public static void close() {
        if (logWriter != null) {
            logWriter.close();
            logWriter = null;
        }
        if (htmlWriter != null) {
            htmlWriter.println("</table>"); 
            htmlWriter.println("</body></html>");
            htmlWriter.close();
            htmlWriter = null;
        }
    }

    public static void clearAlt() {
        if (LOG_FILE_PATH == null || HTML_FILE_PATH == null) {
            System.err.println("Cannot clear log file: Logger not initialized.");
            return;
        }
        try {
            Files.deleteIfExists(LOG_FILE_PATH); 
            System.out.println("Log file cleared successfully. Call initializeLogger again to start logging.");
            Files.deleteIfExists(HTML_FILE_PATH); 
            System.out.println("Html file cleared successfully. Call initializeLogger again to start logging.");
            close(); 
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }
    public static void logCommand(String command) {
        log(command); 
}
}
