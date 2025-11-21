package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;
import java.nio.file.Paths;

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
     * @param args command-line arguments: -html [html_file] -txt [txt_file]
     */
    public static void main(final String[] args) {
        // Base directory for log files
        final String LOG_DIRECTORY = "COMP2021_PROJECT - 202526/COMP2021Project/CLEVIS tool APP/src/hk/edu/polyu/comp/comp2021/clevis/model/logs";
        
        // Default file names (will be placed in the log directory)
        String htmlLog = "clevis_log.html";
        String txtLog = "clevis_log.txt";

        // === REQ1: Parse command-line arguments for log file paths ===
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-html":
                        if (i + 1 < args.length) {
                            htmlLog = args[++i];
                            if (htmlLog.isEmpty()) {
                                System.err.println("Error: HTML file name cannot be empty");
                                printUsage();
                                return;
                            }
                        } else {
                            System.err.println("Error: -html option requires a file name");
                            printUsage();
                            return;
                        }
                        break;
                    case "-txt":
                        if (i + 1 < args.length) {
                            txtLog = args[++i];
                            if (txtLog.isEmpty()) {
                                System.err.println("Error: Text file name cannot be empty");
                                printUsage();
                                return;
                            }
                        } else {
                            System.err.println("Error: -txt option requires a file name");
                            printUsage();
                            return;
                        }
                        break;
                    case "-help":
                    case "--help":
                        printUsage();
                        return;
                    default:
                        System.err.println("Error: Unknown option '" + args[i] + "'");
                        printUsage();
                        return;
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing command-line arguments: " + e.getMessage());
            printUsage();
            return;
        }

        // Build full paths using the specified directory
        String fullHtmlPath = Paths.get(LOG_DIRECTORY, htmlLog).toString();
        String fullTxtPath = Paths.get(LOG_DIRECTORY, txtLog).toString();

        System.out.println("======================================");
        System.out.println("  CLEVIS System Starting...");
        System.out.println("  Log Directory: " + LOG_DIRECTORY);
        System.out.println("  HTML Log: " + htmlLog);
        System.out.println("  Text Log: " + txtLog);
        System.out.println("  Full HTML Path: " + fullHtmlPath);
        System.out.println("  Full Text Path: " + fullTxtPath);
        System.out.println("======================================");

        try {
            // Initialize Clevis core (REQ2–REQ15)
            final Clevis clevis = new Clevis(fullHtmlPath, fullTxtPath);

            // Start main command loop
            clevis.run();
        } catch (Exception e) {
            System.err.println("Failed to start Clevis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Print usage information for the application.
     */
    private static void printUsage() {
        System.out.println();
        System.out.println("Usage: java hk.edu.polyu.comp.comp2021.clevis.Application [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -html <file>    Specify HTML log file name (e.g., log.html)");
        System.out.println("  -txt <file>     Specify text log file name (e.g., log.txt)");
        System.out.println("  -help           Display this help message");
        System.out.println();
        System.out.println("Note: Log files will be created in:");
        System.out.println("      COMP2021_PROJECT - 202526/COMP2021Project/CLEVIS tool APP/src/hk/edu/polyu/comp/comp2021/clevis/model/logs/");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application -html log.html -txt log.txt");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application -html session1.html -txt session1.txt");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application (uses default file names)");
        System.out.println();
    }
}