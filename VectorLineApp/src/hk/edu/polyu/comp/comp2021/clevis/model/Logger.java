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

    public static void log(String class_name, String method_name, Object[] arguments){
        try (PrintWriter writer = new PrintWriter(
                // Step 2: Create a buffered writer with specific options
                Files.newBufferedWriter(LOG_PATH, 
                    StandardOpenOption.CREATE,     // Create file if it doesn't exist
                    StandardOpenOption.APPEND))){ // Append to existing fil
            
                        writer.println(message);
                    
    }

    catch (IOException e){
        System.err.println(e.getMessage());

    }
}

    public static String message_parser(Object... arguments) {
        if (arguments == null || arguments.length == 0) {
            return "no_args";
        }
        
        StringBuilder parsed = new StringBuilder();
        for (Object arg : arguments) {
            if (parsed.length() > 0) {
                parsed.append("/");
            }
            
            if (arg == null) {
                parsed.append("null");
            } else {
                parsed.append(arg.getClass().getSimpleName()).append(":").append(arg);
            }
        }
        return parsed.toString();
    }

    public static void main(String[] args) {
        System.out.println(Logger.message_parser(1,1.0f,"nigga"));
    }
}