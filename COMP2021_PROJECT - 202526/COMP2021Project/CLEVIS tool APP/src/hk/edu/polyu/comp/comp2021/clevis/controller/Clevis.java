package hk.edu.polyu.comp.comp2021.clevis.controller;

import hk.edu.polyu.comp.comp2021.clevis.model.*;
import hk.edu.polyu.comp.comp2021.clevis.model.ClevisException.DuplicateShapeException;
import hk.edu.polyu.comp.comp2021.clevis.view.ConsoleView;

import java.util.*;

/**
 * Clevis main logic and shapes.
 * REQ2..REQ15.
 *
 * Notes:
 *  - Logging (REQ1) is handled by hk.edu.polyu.comp.comp2021.clevis.model.ClevisLogger.
 *  - Integrated with Application.java; run the Application to start.
 */
public class Clevis {

    private final ShapeManager shapeManager;
    private final CommandParser parser;
    private final ConsoleView view;

    /**
     * Initialize Clevis system with given log paths.
     *
     * @param htmlPath HTML log path
     * @param txtPath  text log path
     */
    public Clevis(final String htmlPath, final String txtPath) {
        // Initialize the static logger
        ClevisLogger.initializeLogger(txtPath, htmlPath);
        this.shapeManager = new ShapeManager();
        this.parser = new CommandParser(shapeManager);
        this.view = new ConsoleView();
    }
        /**
     * Get the shape manager for GUI access.
     */
    public ShapeManager getShapeManager() {
        return shapeManager;
    }

    /**
     * Get the command parser for GUI access.
     */
    public CommandParser getParser() {
        return parser;
    }

    /**
 * Run interactive CLI.
 * The program terminates only when 'quit' is entered.
 */
    public void run() {
        Scanner in = new Scanner(System.in);
        
        try {
            view.showWelcomeMessage();
            
            // Add clear messaging for GUI mode
            System.out.println("\n=== TERMINAL INPUT ACTIVE ===");
            System.out.println("Type commands below (GUI will update automatically)");
            System.out.println("Type 'quit' to exit terminal mode");
            System.out.println("================================\n");

            while (true) {
                // Show prompt and ensure it's visible
                view.showPrompt();
                System.out.flush(); // Force the prompt to display
                
                String line = null;
                
                // Check if there's input available
                if (in.hasNextLine()) {
                    line = in.nextLine();
                } else {
                    // No input available, small delay to prevent busy waiting
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                    continue;
                }

                if (line == null) {
                    continue;
                }
                
                String trimmed = line.trim();
                
                // Check for quit command
                if ("quit".equalsIgnoreCase(trimmed)) {
                    break;
                }
                
                // Skip empty lines
                if (trimmed.isEmpty()) {
                    continue;
                }

                // Execute the command
                parser.execute(trimmed);
            }

            view.showTerminationMessage();
            
        } catch (Exception e) {
            System.err.println("Unexpected error in CLI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure resources are closed
            in.close();
            ClevisLogger.close();
        }
    }

    /**
     * CommandParser is responsible for interpreting and executing all Clevis commands.
     */
    
public static class CommandParser {
    private final ShapeManager manager;
    private boolean testMode = false;
    
    // Simple command history - only store the command strings
    private final List<String> commandHistory = new ArrayList<>();
    private int currentHistoryIndex = -1;
    private boolean isReplaying = false; // Add this flag

    public CommandParser(final ShapeManager manager) {
        this.manager = manager;
    }
    
    /**
     * Execute a single command string with undo/redo support
     */
    public void execute(final String command) {
        if (command == null) {
            return;
        }

        final String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        // Log the command (except undo/redo themselves)
        if (!trimmed.equals("undo") && !trimmed.equals("redo")) {
            ClevisLogger.logCommand(trimmed);
        }

        final String[] tokens = trimmed.split("\\s+");
        final String op = tokens[0].toLowerCase(Locale.ROOT);

        try {
            switch (op) {
                case "rectangle":
                    createRectangle(tokens);
                    addToHistory(trimmed);
                    break;
                case "line":
                    createLine(tokens);
                    addToHistory(trimmed);
                    break;
                case "circle":
                    createCircle(tokens);
                    addToHistory(trimmed);
                    break;
                case "square":
                    createSquare(tokens);
                    addToHistory(trimmed);
                    break;
                case "group":
                    groupShapes(tokens);
                    addToHistory(trimmed);
                    break;
                case "ungroup":
                    ungroupShapes(tokens);
                    addToHistory(trimmed);
                    break;
                case "delete":
                    deleteShape(tokens);
                    addToHistory(trimmed);
                    break;
                case "move":
                    moveShape(tokens);
                    addToHistory(trimmed);
                    break;
                case "undo":
                    undoCommand(tokens);
                    break;
                case "redo":
                    redoCommand(tokens);
                    break;
                case "boundingbox":
                    calculateBoundingBox(tokens);
                    break;
                case "shapeat":
                    findTopmost(tokens);
                    break;
                case "intersect":
                    intersect(tokens);
                    break;
                case "list":
                    listShape(tokens);
                    break;
                case "listall":
                    listAll(tokens);
                    break;
                case "quit":
                    quit();
                    break;
                case "help":
                    showHelp();
                    break;
                default:
                    System.out.println("Unknown command: " + op);
                    ClevisLogger.logCommand("Error: Unknown command - " + op);
            }
        } catch (ClevisException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number format.");
        } catch (RuntimeException e) {
            System.out.println("Runtime error: " + e.getMessage());
        }
    }
    
    /**
     * Add command to history (only mutable commands)
     */
    private void addToHistory(String command) {
        // Remove any future commands if we're in the middle of history
        if (currentHistoryIndex < commandHistory.size() - 1) {
            commandHistory.subList(currentHistoryIndex + 1, commandHistory.size()).clear();
        }
        commandHistory.add(command);
        currentHistoryIndex = commandHistory.size() - 1;
        
        System.out.println("History: " + (currentHistoryIndex + 1) + "/" + commandHistory.size() + " commands");
    }
    
    /**
     * Execute undo command by replaying all commands except the last one
     */
    private void undoCommand(final String[] tokens) throws ClevisException {
        if (tokens.length != 1) {
            throw new ClevisException("Usage: undo");
        }
        
        if (currentHistoryIndex < 0) {
            throw new ClevisException("Nothing to undo.");
        }
        
        System.out.println("Undoing last command...");
        
        // Move back in history
        currentHistoryIndex--;
        
        // Replay all commands up to the new current position
        replayHistoryUpToCurrent();
        
        ClevisLogger.logCommand("undo");
        System.out.println("Undo completed. " + getHistoryStatus());
    }
    
    /**
     * Execute redo command by replaying all commands up to the next one
     */
    private void redoCommand(final String[] tokens) throws ClevisException {
        if (tokens.length != 1) {
            throw new ClevisException("Usage: redo");
        }
        
        if (currentHistoryIndex >= commandHistory.size() - 1) {
            throw new ClevisException("Nothing to redo.");
        }
        
        System.out.println("Redoing next command...");
        
        // Move forward in history
        currentHistoryIndex++;
        
        // Replay all commands up to the new current position
        replayHistoryUpToCurrent();
        
        ClevisLogger.logCommand("redo");
        System.out.println("Redo completed. " + getHistoryStatus());
    }
    
    /**
     * Replay command history from beginning up to currentHistoryIndex
     */
    private void replayHistoryUpToCurrent() throws ClevisException {
        isReplaying = true;
        try {
            clearAllShapes();
            
            for (int i = 0; i <= currentHistoryIndex; i++) {
                String command = commandHistory.get(i);
                executeReplayCommand(command);
            }
        } finally {
            isReplaying = false;
        }
    }
    
    /**
     * Execute a command during history replay
     */
    private void executeReplayCommand(final String command) throws ClevisException {
        final String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        final String[] tokens = trimmed.split("\\s+");
        final String op = tokens[0].toLowerCase(Locale.ROOT);

        switch (op) {
            case "rectangle":
                createRectangle(tokens);
                break;
            case "line":
                createLine(tokens);
                break;
            case "circle":
                createCircle(tokens);
                break;
            case "square":
                createSquare(tokens);
                break;
            case "group":
                groupShapes(tokens);
                break;
            case "ungroup":
                ungroupShapes(tokens);
                break;
            case "delete":
                deleteShape(tokens);
                break;
            case "move":
                moveShape(tokens);
                break;
            default:
                // Skip other commands during replay
                break;
        }
    }
    
    /**
     * Clear all shapes from the manager
     */
    private void clearAllShapes() {
        // Get all shape names first to avoid concurrent modification
        List<String> shapeNames = new ArrayList<>();
        for (Shape shape : manager.getAllShapes()) {
            shapeNames.add(shape.getName());
        }
        
        // Delete all shapes
        for (String name : shapeNames) {
            try {
                manager.deleteShape(name);
            } catch (ClevisException e) {
                // Ignore errors during cleanup
            }
        }
    }
    
    /**
     * Get current history status
     */
    private String getHistoryStatus() {
        return "Current state: " + (currentHistoryIndex + 1) + "/" + commandHistory.size() + " commands";
    }
    
    /**
     * Get undo/redo status for GUI
     */
    public String getUndoRedoStatus() {
        boolean canUndo = currentHistoryIndex >= 0;
        boolean canRedo = currentHistoryIndex < commandHistory.size() - 1;
        
        return String.format("Undo: %s | Redo: %s | History: %d commands", 
            canUndo ? "Yes" : "No", 
            canRedo ? "Yes" : "No",
            commandHistory.size());
    }

        /**
         * Enables or disables test mode.
         */
        public void setTestMode(boolean mode) {
            this.testMode = mode;
        }

        // Command handlers - remove all the detailed logging calls, only command logging is needed

        /**
         * [REQ2] Create rectangle
         */
        private void createRectangle(final String[] tokens) throws ClevisException {
            if (tokens.length != 6) {
                throw new ClevisException("Rectangle command requires 5 parameters: name, x, y, width, height");
            }
            try {
                final String name = tokens[1].trim();
                final double x = Double.parseDouble(tokens[2]);
                final double y = Double.parseDouble(tokens[3]);
                final double width = Double.parseDouble(tokens[4]);
                final double height = Double.parseDouble(tokens[5]);

                final Rectangle rectangle = new Rectangle(name, x, y, width, height);
                manager.addShape(rectangle);
                System.out.printf("Created a Rectangle named %s at (%.2f,%.2f) w=%.2f h=%.2f%n",
                        name, x, y, width, height);
                
                // No need for additional logging - the command is already logged by execute()
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ3] Create line
         */
        private void createLine(final String[] tokens) throws ClevisException {
            if (tokens.length != 6) {
                throw new ClevisException("Line command requires 5 parameters: name, x1, y1, x2, y2");
            }
            try {
                final String name = tokens[1].trim();
                final double x1 = Double.parseDouble(tokens[2]);
                final double y1 = Double.parseDouble(tokens[3]);
                final double x2 = Double.parseDouble(tokens[4]);
                final double y2 = Double.parseDouble(tokens[5]);

                final Line line = new Line(name, x1, y1, x2, y2);
                manager.addShape(line);
                System.out.printf("Created line %s from (%.2f,%.2f) to (%.2f,%.2f)%n",
                        name, x1, y1, x2, y2);
                
                // No additional logging needed
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ4] Create circle
         */
        private void createCircle(final String[] tokens) throws ClevisException {
            if (tokens.length != 5) {
                throw new ClevisException("Circle command requires 4 parameters: name, x, y, radius");
            }
            try {
                final String name = tokens[1].trim();
                final double x = Double.parseDouble(tokens[2]);
                final double y = Double.parseDouble(tokens[3]);
                final double radius = Double.parseDouble(tokens[4]);

                final Circle circle = new Circle(name, x, y, radius);
                manager.addShape(circle);
                System.out.printf("Created circle %s center=(%.2f,%.2f) r=%.2f%n",
                        name, x, y, radius);
                
                // No additional logging needed
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ5] Create square
         */
        private void createSquare(final String[] tokens) throws ClevisException {
            if (tokens.length != 5) {
                throw new ClevisException("Square command requires 4 parameters: name, x, y, length");
            }
            try {
                final String name = tokens[1].trim();
                final double x = Double.parseDouble(tokens[2]);
                final double y = Double.parseDouble(tokens[3]);
                final double length = Double.parseDouble(tokens[4]);

                final Square square = new Square(name, x, y, length);
                manager.addShape(square);
                System.out.printf("Created square %s at (%.2f,%.2f) side=%.2f%n", name, x, y, length);
                
                // No additional logging needed
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ6] Group shapes
         */
        private void groupShapes(final String[] tokens) throws ClevisException {
            if (tokens.length < 3) {
                throw new ClevisException("Usage: group n n1 n2 ...");
            }

            final String groupName = tokens[1].trim();

            // Ensure group name not already used
            if (manager.getShape(groupName) != null) {
                throw new ClevisException("Shape already exists: " + groupName);
            }

            final List<Shape> members = new ArrayList<>();
            final List<String> originalNames = new ArrayList<>();
            final List<Integer> originalIndices = new ArrayList<>();

            // capture global order to compute indices (bottom-to-top)
            final List<Shape> global = manager.getAllShapes();

            for (int i = 2; i < tokens.length; i++) {
                final String mname = tokens[i].trim();
                final Shape s = manager.getShape(mname);
                if (s == null) {
                    throw new ClevisException.ShapeNotFoundException("Shape not found: " + mname);
                }
                // don't allow grouping an existing group (avoid nested groups unless desired)
                if (s instanceof Group) {
                    throw new ClevisException("Cannot group an existing group: " + mname);
                }
                members.add(s);
                originalNames.add(s.getName());
                originalIndices.add(global.indexOf(s)); // index in the current z-order
            }

            if (members.isEmpty()) {
                throw new ClevisException("Group must have at least one member.");
            }

            // Remove members from manager (they become exclusively accessible via the group)
            for (Shape s : members) {
                manager.deleteShape(s.getName());
            }

            // Create Group that holds references and metadata
            final Group newGroup = new Group(groupName, members, originalNames, originalIndices);
            manager.addShape(newGroup);

            final StringBuilder memberNames = new StringBuilder();
            for (int i = 0; i < originalNames.size(); i++) {
                if (i > 0) memberNames.append(",");
                memberNames.append(originalNames.get(i));
            }

            System.out.printf("Created group %s containing: %s%n", groupName, memberNames.toString());
        }

        /**
         * [REQ7] Ungroup shapes
         */
        private void ungroupShapes(final String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: ungroup n");
            }

            final String groupName = tokens[1].trim();
            final Shape s = manager.getShape(groupName);
            if (s == null) {
                throw new ClevisException.ShapeNotFoundException("Shape not found: " + groupName);
            }
            if (!(s instanceof Group g)) {
                throw new ClevisException("Shape '" + groupName + "' is not a group.");
            }

            // copy meta & members so we don't depend on group's internal mutability
            final List<Shape> members = new ArrayList<>(g.getMembers());
            final List<String> originalNames = g.getOriginalNames();
            final List<Integer> originalIndices = g.getOriginalIndices();

            // Remove the group from manager first so original names are not reported as conflicts against themselves
            manager.deleteShape(groupName);

            // Build tuples of (index, member, desiredName) and sort ascending by index
            final List<Triple> triples = new ArrayList<>();
            for (int i = 0; i < members.size(); i++) {
                final Shape member = members.get(i);
                final String desired = (i < originalNames.size()) ? originalNames.get(i) : member.getName();
                final int idx = (i < originalIndices.size()) ? originalIndices.get(i) : -1;
                triples.add(new Triple(idx, member, desired));
            }
            triples.sort(Comparator.comparingInt(t -> t.index < 0 ? Integer.MAX_VALUE : t.index));

            final List<String> restoredNamesPrinted = new ArrayList<>();

            for (Triple t : triples) {
                final Shape member = t.member;
                final String desired = t.desiredName;
                final String targetName = makeUniqueName(desired);

                // rename shape to targetName
                member.setName(targetName);

                // insert at index if known, otherwise append
                if (t.index >= 0) {
                    // clamp index into current range
                    int insertIndex = t.index;
                    if (insertIndex < 0) insertIndex = 0;
                    if (insertIndex > manager.getAllShapes().size()) insertIndex = manager.getAllShapes().size();
                    manager.addShapeAt(insertIndex, member);
                } else {
                    manager.addShape(member);
                }

                restoredNamesPrinted.add(targetName);
            }

            System.out.printf("Ungrouped %s into: %s%n", groupName, String.join(",", restoredNamesPrinted));
        }

        /** Helper triple used only in this method scope to keep code tidy. */
        private static final class Triple {
            final int index;
            final Shape member;
            final String desiredName;

            Triple(final int index, final Shape member, final String desiredName) {
                this.index = index;
                this.member = member;
                this.desiredName = desiredName;
            }
        }

        /**
         * Return a name that is not already in manager by appending suffix _n if necessary.
         */
        private String makeUniqueName(String base) {
            if (manager.getShape(base) == null) return base;
            int i = 1;
            String candidate;
            do {
                candidate = base + "_" + i++;
            } while (manager.getShape(candidate) != null);
            return candidate;
        }

        /**
         * [REQ8] Delete shape
         */
        private void deleteShape(final String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: delete n");
            }
            manager.deleteShape(tokens[1]);
            System.out.println("Deleted shape " + tokens[1]);
            // No additional logging needed
        }

        /**
         * [REQ9] Calculate bounding box
         */
        private void calculateBoundingBox(final String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: boundingbox n");
            }

            final String name = tokens[1].trim();
            final String bbox = manager.getBoundingBox(name);

            String[] parts = bbox.split(" ");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double w = Double.parseDouble(parts[2]);
            double h = Double.parseDouble(parts[3]);

            System.out.printf("Bounding box of %s: (x=%.2f, y=%.2f, width=%.2f, height=%.2f)%n",
                    name, x, y, w, h);
            // No additional logging needed
        }

        /**
         * [REQ10] Move shape
         */
        private void moveShape(final String[] tokens) throws ClevisException {
            if (tokens.length != 4) {
                throw new ClevisException("Usage: move n dx dy");
            }
            try {
                final String name = tokens[1].trim();
                final double dx = Double.parseDouble(tokens[2]);
                final double dy = Double.parseDouble(tokens[3]);

                final Shape s = manager.getShape(name);
                if (s == null) {
                    throw new ClevisException.ShapeNotFoundException("Shape not found: " + name);
                }
                s.move(dx, dy);
                System.out.printf("Moved %s by (%.2f,%.2f)%n", name, dx, dy);
                // No additional logging needed
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ11] Find topmost shape at point
         */
        private void findTopmost(final String[] tokens) {
            if (tokens.length != 3) {
                throw new IllegalArgumentException("Usage: shapeAt x y");
            }
            final double x = Double.parseDouble(tokens[1]);
            final double y = Double.parseDouble(tokens[2]);
            final List<Shape> allShapes = manager.getAllShapes();

            for (int i = allShapes.size() - 1; i >= 0; i--) {
                final Shape shape = allShapes.get(i);
                if (shape.coversPoint(x, y)) {
                    System.out.println("The topmost shape covering point (" + x + ", " + y + ") is: " + shape.getName());
                    return;
                }
            }
            System.out.println("No shape covers the given point (" + x + ", " + y + ").");
        }

        /**
         * [REQ12] Check intersection
         */
        private void intersect(final String[] tokens) {
            if (tokens.length != 3) {
                throw new IllegalArgumentException("Usage: intersect n1 n2");
            }

            final String n1 = tokens[1].trim();
            final String n2 = tokens[2].trim();

            final Shape shape1 = manager.getShape(n1);
            final Shape shape2 = manager.getShape(n2);

            if (shape1 == null || shape2 == null) {
                throw new RuntimeException("One or both shapes were not found: " + n1 + ", " + n2);
            }

            final String[] box1 = shape1.getBoundingBox().split("\\s+");
            final String[] box2 = shape2.getBoundingBox().split("\\s+");

            final double x1 = Double.parseDouble(box1[0]);
            final double y1 = Double.parseDouble(box1[1]);
            final double w1 = Double.parseDouble(box1[2]);
            final double h1 = Double.parseDouble(box1[3]);

            final double x2 = Double.parseDouble(box2[0]);
            final double y2 = Double.parseDouble(box2[1]);
            final double w2 = Double.parseDouble(box2[2]);
            final double h2 = Double.parseDouble(box2[3]);

            final boolean separated = (x1 + w1 < x2) || (x2 + w2 < x1) || (y1 + h1 < y2) || (y2 + h2 < y1);
            final boolean result = !separated;
            System.out.printf("Shapes %s and %s intersect: %b%n", n1, n2, result);
            // No additional logging needed
        }

        /**
         * [REQ13] List shape info
         */
        private void listShape(final String[] tokens) {
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Usage: list n");
            }
            final String name = tokens[1].trim();
            final Shape shape = manager.getShape(name);
            if (shape == null) {
                throw new RuntimeException("Shape not found: " + name);
            }
            System.out.println("Shape " + name + ": " + shape.getInfo());
            // No additional logging needed
        }

        /**
         * [REQ14] List all shapes
         */
        private void listAll(final String[] tokens) {
            if (tokens.length != 1) {
                throw new IllegalArgumentException("Usage: listAll");
            }

            final List<Shape> allShapes = manager.getAllShapes();
            if (allShapes.isEmpty()) {
                System.out.println("No shapes currently exist.");
                return;
            }

            System.out.println("Listing all shapes (bottom to top):");
            for (Shape shape : allShapes) {
                System.out.println(" - " + shape.getInfo());

                if (shape instanceof Group group) {
                    for (Shape member : group.getMembers()) {
                        System.out.println("  " + member.getName() + ": " + member.getInfo());
                    }
                }
            }
            // No additional logging needed
        }

        /**
         * [REQ15] Quit Command
         */
        private void quit() {
            System.out.println("Clevis session ended. Logs saved.");
            ClevisLogger.close();
            if (!testMode) {
                System.exit(0);
            }
        }

        /**
         * [REQ16] Help command
         */

/**
 * [REQ16] Help command with updated undo/redo and bounding box info
 */
        private void showHelp() {
            System.out.println("""
            ========================= CLEVIS HELP =========================
            Clevis is a command-line drawing and shape manipulation tool.

            SHAPE CREATION COMMANDS:
            "rectangle n x y w h": Create a rectangle named n with top-left corner (x, y), width w, and height h.
            "line n x1 y1 x2 y2": Create a line segment named n from (x1, y1) to (x2, y2).
            "circle n x y r": Create a circle named n with center (x, y) and radius r.
            "square n x y l": Create a square named n with top-left corner (x, y) and side length l.

            GROUPING AND MODIFICATION COMMANDS:
            "group n n1 n2 ...": Group shapes n1, n2, ... into one shape named n.
            "ungroup n": Ungroup the group shape named n back into its members.
            "delete n": Delete a shape (or group) named n.
            "move n dx dy": Move shape n by dx horizontally and dy vertically.

            UNDO/REDO COMMANDS (Current Session Only):
            "undo": Undo the last command (supports: rectangle, line, circle, square, group, ungroup, delete, move).
            "redo": Redo the last undone command.

            VISUALIZATION COMMANDS:
            "boundingbox n": Display the minimum bounding box of shape n (also shows visual highlight in GUI).
            "shapeAt x y": Find the topmost shape covering point (x, y).
            "intersect n1 n2": Check if two shapes (n1, n2) intersect.

            INFORMATION COMMANDS:
            "list n": Show detailed info about a single shape.
            "listAll": List all shapes in Clevis (bottom to top).

            GUI-SPECIFIC FEATURES:
            - Bounding box visualizations appear when using 'boundingbox' command
            - Click 'Refresh View' to clear bounding box highlights
            - Undo/redo history is maintained for the current session only
            - GUI updates automatically when commands are executed from terminal

            SYSTEM COMMANDS:
            "help": Show this help guide.
            "quit": Exit Clevis and save logs.

            NOTES:
            - Undo/redo works for shape creation, modification, grouping, and deletion
            - Bounding box, intersect, list, and listAll commands cannot be undone/redone
            - Use 'Refresh View' button in GUI to clear bounding box visualizations
            - Shape names must be unique
            - All coordinates and dimensions are floating-point numbers

            ================================================================
            """);
        }
    }
        

}
