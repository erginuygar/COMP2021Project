package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.view.ClevisGUI;
import javax.swing.*;

public class Application {
    public static void main(final String[] args) {
        final String LOG_DIR = "COMP2021_PROJECT - 202526/COMP2021Project/CLEVIS tool APP/src/hk/edu/polyu/comp/comp2021/clevis/logs";
        String htmlLog = LOG_DIR + "/clevis_log.html";
        String txtLog = LOG_DIR + "/clevis_log.txt";
        boolean useGUI = true;

        System.out.println("======================================");
        System.out.println("  CLEVIS System Starting...");
        System.out.println("  Mode: " + (useGUI ? "GUI + Terminal" : "CLI"));
        System.out.println("======================================");

        try {
            final Clevis clevis = new Clevis(htmlLog, txtLog);

            if (useGUI) {
                System.out.println("Starting GUI...");
                
                // Start GUI in Swing thread
                SwingUtilities.invokeLater(() -> {
                    ClevisGUI gui = new ClevisGUI(clevis.getShapeManager(), clevis.getParser());
                    System.out.println("GUI ready! You can use both terminal and GUI.");
                });
                
                // Give GUI time to initialize
                Thread.sleep(2000);
                
                System.out.println("\n=== TERMINAL INPUT ACTIVE ===");
                System.out.println("Type commands here OR use the GUI");
                System.out.println("Both interfaces will stay synchronized");
                System.out.println("Type 'quit' to exit");
                System.out.println("==============================\n");
                
                // Run terminal input in main thread
                clevis.run();
                
            } else {
                // CLI-only mode
                clevis.run();
            }
            
        } catch (Exception e) {
            System.err.println("Failed to start Clevis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}