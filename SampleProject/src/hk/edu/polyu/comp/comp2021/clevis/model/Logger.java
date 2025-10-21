package hk.edu.polyu.comp.comp2021.clevis.model;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
public class Logger {
    private final static String file_name = "log.txt";
    private static PrintWriter writer;
    private static int index = 0;

    static{

        try{
            writer = new PrintWriter(new FileWriter(file_name, true));

        }
        catch (IOException e){
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    private static void Log(String method_name) {
        if(writer != null){
            String 
        }
    }
}
