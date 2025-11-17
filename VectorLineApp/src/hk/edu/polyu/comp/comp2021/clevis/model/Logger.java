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
    private static PrintWriter writer;


    public static void initializeLogger(String log_file, String html_file){ 
        if (writer != null) {
            
            close(); 
        }

        LOG_FILE_PATH = DIRECTORY_PATH.resolve(log_file);
        
        try {

            Files.createDirectories(DIRECTORY_PATH); 
            System.out.println("Log directory ensured: " + DIRECTORY_PATH.toAbsolutePath());

            writer = new PrintWriter(
                Files.newBufferedWriter(LOG_FILE_PATH, 
                    StandardOpenOption.CREATE, // Creates the file if it doesn't exist
                    StandardOpenOption.APPEND, // Appends to the end of the file
                    StandardOpenOption.WRITE));
            
            System.out.println("Log file ready at: " + LOG_FILE_PATH.toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            e.printStackTrace(); // Good practice to print the stack trace for debugging
        }
    }
    public static void log(String className, String methodName, Object... arguments) {
        if (writer == null) {
            System.err.println("Logger not initialized properly");
            return;
        }
        
        String parsedArgs = messageParser(className, methodName, arguments);
        writer.println(parsedArgs);
        writer.flush();
    }

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

    public static void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    public static void clearAlt() {
        try {
            Files.deleteIfExists(LOG_FILE_PATH);
            Files.createFile(LOG_FILE_PATH);
            System.out.println("Log file cleared successfully");
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Logger.initializeLogger("log.txt", "log.html");
        Logger.log("n","b",'c',1,2);
    }
    

}