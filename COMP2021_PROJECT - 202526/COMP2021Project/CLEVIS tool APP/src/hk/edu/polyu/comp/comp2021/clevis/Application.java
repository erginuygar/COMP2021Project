package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.view.ClevisGUI;
import javax.swing.*;

public class Application {

    public static void main(final String[] args) {
        // Simple fixed log directory
        final String LOG_DIR = "COMP2021Project/CLEVIS tool APP/src/hk/edu/polyu/comp/comp2021/clevis/logs";
        
        // Fixed log file names in that directory
        String htmlLog = LOG_DIR + "/clevis_log.html";
        String txtLog = LOG_DIR + "/clevis_log.txt";
        boolean useGUI = true;

        // Simple argument parsing - only check for GUI flag
        for (String arg : args) {
            if ("-gui".equals(arg)) {
                useGUI = true;
                break;
            }
        }

        System.out.println("======================================");
        System.out.println("  CLEVIS System Starting...");
        System.out.println("  Log Directory: " + LOG_DIR);
        System.out.println("  HTML Log: " + htmlLog);
        System.out.println("  Text Log: " + txtLog);
        System.out.println("  Mode: " + (useGUI ? "GUI" : "CLI"));
        System.out.println("======================================");

        try {
            final Clevis clevis = new Clevis(htmlLog, txtLog);

            if (useGUI) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        new ClevisGUI(clevis.getShapeManager(), clevis.getParser());
                        System.out.println("Clevis GUI launched successfully!");
                    } catch (Exception e) {
                        System.err.println("Failed to launch GUI: " + e.getMessage());
                        System.out.println("Falling back to CLI mode...");
                        clevis.run();
                    }
                });
            } else {
                clevis.run();
            }
            
        } catch (Exception e) {
            System.err.println("Failed to start Clevis: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
