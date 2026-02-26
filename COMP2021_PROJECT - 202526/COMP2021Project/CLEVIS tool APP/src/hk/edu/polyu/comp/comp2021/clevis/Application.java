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
                System.out.println("Starting GUI in background...");
                
                // Start GUI in a completely separate thread
                Thread guiThread = new Thread(() -> {
                    try {
                        // Use invokeAndWait to ensure GUI is created before continuing
                        SwingUtilities.invokeAndWait(() -> {
                            new ClevisGUI(clevis.getShapeManager(), clevis.getParser());
                        });
                        System.out.println("GUI started successfully!");
                    } catch (Exception e) {
                        System.err.println("Failed to start GUI: " + e.getMessage());
                    }
                });
                guiThread.setDaemon(true); // This is key - makes thread not block JVM exit
                guiThread.start();
                
                // Give GUI a moment to initialize
                Thread.sleep(2000);
            }
            
            System.out.println("\n=== TERMINAL ACTIVE ===");
            System.out.println("Type commands below (GUI updates automatically):");
            System.out.println("Type 'quit' to exit");
            System.out.println("=======================\n");
            
            // Start the terminal - this should now work
            clevis.run();
            
        } catch (Exception e) {
            System.err.println("Failed to start Clevis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}