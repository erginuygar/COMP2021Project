package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Logger {
    private final static Path LOG_PATH = Paths.get("VectorLineApp/src/hk/edu/polyu/comp/comp2021/clevis/model/logs/log.txt");
    private static PrintWriter writer;
private final static ArrayList<String> execution_history = new ArrayList<>();
    
    static {
        initializeLogger();
    }

    private static void initializeLogger() {
        try {
            Files.createDirectories(LOG_PATH.getParent());
            System.out.println("Log file: " + LOG_PATH.toAbsolutePath());
            
            writer = new PrintWriter(
                Files.newBufferedWriter(LOG_PATH, 
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.WRITE));
            
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
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
        parsed.append(className).append("/").append(methodName);
        
        if (arguments == null || arguments.length == 0) {
            parsed.append("");
        } else {
            parsed.append("/");
            for (int i = 0; i < arguments.length; i++) {
                if (i > 0) {
                    parsed.append("/");
                }
                
                if (arguments[i] == null) {
                    parsed.append("null");
                } else {
                    String simpleClassName = arguments[i].getClass().getSimpleName();
                    parsed.append(simpleClassName).append(":").append(arguments[i]);
                }
            }
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
            Files.deleteIfExists(LOG_PATH);
            Files.createFile(LOG_PATH);
            System.out.println("Log file cleared successfully");
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }

    public static void CommandExecute(){
        if(execution_history.size()==0){
            System.err.println("No execution history.");
        }
    }
    public static void ReadLines(){
        String fileName = "log.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_PATH.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                execution_history.add(line);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        Logger.log("a","b","c","d");
        ReadLines();
        for(String a : execution_history){
            System.out.println(a);
        }
    
    }
}