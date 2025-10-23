package hk.edu.polyu.comp.comp2021.clevis.model;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class Logger{
    private final static String file_name = "log.txt";
    private static PrintWriter writer;
    private static int index = 0;
    private static Map<Integer, String> classes;
    private static Map<Integer, String> methods; 

    static{

        try{
            writer = new PrintWriter(new FileWriter(file_name, true));
            classes = new HashMap<>();
            methods = new HashMap<>();

        }
        catch (IOException e){
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }
    public Logger(){}

    private static void Log(String class_name, String method_name) {
        if(writer != null){
            StringBuilder log_message = new StringBuilder();
            log_message.append(index+". "+method_name);
            classes.put(index,class_name);
            methods.put(index++,method_name);
            writer.println(log_message);
 
        }
    }

    public static void main(String[] args) {
        Logger.Log("am","amcik");
        
    }
}
