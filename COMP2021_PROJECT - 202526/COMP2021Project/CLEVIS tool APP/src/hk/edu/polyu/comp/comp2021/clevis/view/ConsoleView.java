package hk.edu.polyu.comp.comp2021.clevis.view;

/**
 * ConsoleView handles all text-based interactions with the user.
 * <p>
 * This class separates I/O from the Clevis controller to follow the MVC pattern.
 */
public final class ConsoleView {

    /**
     * Displays the welcome message when Clevis starts.
     */
    public void showWelcomeMessage() {
        System.out.println("Welcome to Clevis!\n" +
                "Type commands (type 'help' for instructions or 'quit' to exit).");
    }

    /**
     * Displays the CLI prompt.
     */
    public void showPrompt() {
        System.out.print("> ");
    }

    /**
     * Displays the termination message when Clevis exits.
     */
    public void showTerminationMessage() {
        System.out.println("Clevis terminated.");
    }

    /**
     * Displays an arbitrary message.
     *
     * @param message the message to display
     */
    public void showMessage(final String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message.
     *
     * @param error the error message to display
     */
    public void showError(final String error) {
        System.err.println("Error: " + error);
    }
}
