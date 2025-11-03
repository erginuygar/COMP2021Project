package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private final static Path LOG_PATH = Paths.get("VectorLineApp/src/hk/edu/polyu/comp/comp2021/clevis/model/logs/log.txt");
    private static int index = 0;
    private static List<String> executionHistory = new ArrayList<>();

    static {
        initializeLogger();
    }

    private static void initializeLogger() {
        try {
            Files.createDirectories(LOG_PATH.getParent());
            System.out.println("Log file: " + LOG_PATH.toAbsolutePath());
            
            // Load existing index if log file exists
            if (Files.exists(LOG_PATH)) {
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void log(String message){
        try (PrintWriter writer = new PrintWriter(
                // Step 2: Create a buffered writer with specific options
                Files.newBufferedWriter(LOG_PATH, 
                    StandardOpenOption.CREATE,     // Create file if it doesn't exist
                    StandardOpenOption.APPEND))){ // Append to existing file
            
                        writer.println(message);
                    
    }
    catch (IOException e){
        System.err.println(e.getMessage());

    }
}

    public static void main(String[] args) {
        Logger.log("nigga");
    }
}