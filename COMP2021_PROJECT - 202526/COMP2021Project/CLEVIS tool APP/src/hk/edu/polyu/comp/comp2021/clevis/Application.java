package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.view.ClevisGUI; // This should work now!
import javax.swing.*;

/**
 * Responsibilities:
 * - Initialize Clevis system (REQ1: Logging setup)
 * - Pass HTML and text log file paths to Clevis
 * - Start the interactive command loop OR GUI
 */
public class Application {

    /**
     * Main entry point for the Clevis system.
     *
     * @param args command-line arguments: 
     *             -html [html_file] -txt [txt_file] 
     *             -gui (optional: launch GUI instead of CLI)
     */
    public static void main(final String[] args) {
        // Default file paths
        String htmlLog = "clevis_log.html";
        String txtLog = "clevis_log.txt";
        boolean useGUI = false;
        boolean showHelp = false;

        // === REQ1: Parse command-line arguments for log file paths ===
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-html":
                        if (i + 1 < args.length) {
                            htmlLog = args[++i];
                            if (htmlLog.isEmpty()) {
                                System.err.println("Error: HTML file path cannot be empty");
                                printUsage();
                                return;
                            }
                        } else {
                            System.err.println("Error: -html option requires a file path");
                            printUsage();
                            return;
                        }
                        break;
                    case "-txt":
                        if (i + 1 < args.length) {
                            txtLog = args[++i];
                            if (txtLog.isEmpty()) {
                                System.err.println("Error: Text file path cannot be empty");
                                printUsage();
                                return;
                            }
                        } else {
                            System.err.println("Error: -txt option requires a file path");
                            printUsage();
                            return;
                        }
                        break;
                    case "-gui":
                        useGUI = true;
                        break;
                    case "-help":
                    case "--help":
                        showHelp = true;
                        break;
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

        if (showHelp) {
            printUsage();
            return;
        }

        System.out.println("======================================");
        System.out.println("  CLEVIS System Starting...");
        System.out.println("  HTML Log: " + htmlLog);
        System.out.println("  Text Log: " + txtLog);
        System.out.println("  Mode: " + (useGUI ? "GUI" : "CLI"));
        System.out.println("======================================");

        try {
            // Initialize Clevis core (REQ2–REQ15)
            final Clevis clevis = new Clevis(htmlLog, txtLog);

            if (useGUI) {
                // Launch GUI version on Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    try {
                        new ClevisGUI(clevis.getShapeManager(), clevis.getParser());
                        System.out.println("Clevis GUI launched successfully!");
                    } catch (Exception e) {
                        System.err.println("Failed to launch GUI: " + e.getMessage());
                        e.printStackTrace();
                        // Fall back to CLI mode
                        System.out.println("Falling back to CLI mode...");
                        clevis.run();
                    }
                });
            } else {
                // Start CLI version (original behavior)
                clevis.run();
            }
            
        } catch (Exception e) {
            System.err.println("Failed to start Clevis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
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
        System.out.println("  -html <file>    Specify HTML log file path");
        System.out.println("  -txt <file>     Specify text log file path"); 
        System.out.println("  -gui            Launch graphical user interface");
        System.out.println("  -help           Display this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  # CLI mode with default log files");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application");
        System.out.println();
        System.out.println("  # CLI mode with custom log files");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application -html mylog.html -txt mylog.txt");
        System.out.println();
        System.out.println("  # GUI mode with default log files");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application -gui");
        System.out.println();
        System.out.println("  # GUI mode with custom log files");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application -html guilog.html -txt guilog.txt -gui");
        System.out.println();
        System.out.println("  # Show help");
        System.out.println("  java hk.edu.polyu.comp.comp2021.clevis.Application -help");
        System.out.println();
    }
}