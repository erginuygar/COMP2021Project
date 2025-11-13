package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;

/**
 * Responsibilities:
 * - Initialize Clevis system (REQ1: Logging setup)
 * - Pass HTML and text log file paths to Clevis
 * - Start the interactive command loop
 */
public class Application {

    /**
     * Main entry point for the Clevis system.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(final String[] args) {
        // === REQ1: Logging Setup ===
        // Define output file paths for logs
        final String htmlLog = "clevis_log.html";
        final String txtLog = "clevis_log.txt";

        System.out.println("======================================");
        System.out.println("  CLEVIS System Starting...");
        System.out.println("  Logs: " + txtLog + " and " + htmlLog);
        System.out.println("======================================");

        // Initialize Clevis core (REQ2–REQ15)
        final Clevis clevis = new Clevis(htmlLog, txtLog);

        // Start main command loop
        clevis.run();
    }
}
