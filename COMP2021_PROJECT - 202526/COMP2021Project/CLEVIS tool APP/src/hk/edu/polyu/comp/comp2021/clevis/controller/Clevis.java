package hk.edu.polyu.comp.comp2021.clevis.controller;

import hk.edu.polyu.comp.comp2021.clevis.model.*;
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
        try (Scanner in = new Scanner(System.in)) {
            view.showWelcomeMessage();

            while (true) {
                view.showPrompt();
                final String line;

                try {
                    line = in.nextLine();
                } catch (NoSuchElementException | IllegalStateException e) {
                    break;
                }

                if (line == null) {
                    break;
                }

                parser.execute(line);
            }

            view.showTerminationMessage();
        } finally {
            // Ensure logger is properly closed
            ClevisLogger.close();
        }
    }

    /**
     * CommandParser is responsible for interpreting and executing all Clevis commands.
     */
    public static class CommandParser {
        private final ShapeManager manager;
        private boolean testMode = false;

        /**
         * Constructs a new {@code CommandParser} instance.
         */
        public CommandParser(final ShapeManager manager) {
            this.manager = manager;
        }
        
        /**
         * Execute a single command string.
         * Logs every executed command (REQ1).
         */
        public void execute(final String command) {
            if (command == null) {
                return;
            }

            final String trimmed = command.trim();
            if (trimmed.isEmpty()) {
                return;
            }

            // Log command (REQ1) - this is the main logging call
            ClevisLogger.logCommand(trimmed);

            final String[] tokens = trimmed.split("\\s+");
            final String op = tokens[0].toLowerCase(Locale.ROOT);

            try {
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
                    case "boundingbox":
                        calculateBoundingBox(tokens);
                        break;
                    case "move":
                        moveShape(tokens);
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
                        // Log unknown command - just use logCommand
                        ClevisLogger.logCommand("Error: Unknown command - " + op);
                }
            } catch (ClevisException e) {
                System.out.println("Error: " + e.getMessage());
                // Log the error - use logCommand for error messages too
                ClevisLogger.logCommand("Error: " + e.getMessage() + " [Command: " + trimmed + "]");
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid number format.");
                ClevisLogger.logCommand("Error: Invalid number format [Command: " + trimmed + "]");
            } catch (RuntimeException e) {
                System.out.println("Runtime error: " + e.getMessage());
                ClevisLogger.logCommand("Runtime Error: " + e.getMessage() + " [Command: " + trimmed + "]");
            }
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

            final String groupName = tokens[1];
            final List<Shape> members = new ArrayList<>();

            for (int i = 2; i < tokens.length; i++) {
                final Shape s = manager.getShape(tokens[i]);
                if (s == null) {
                    throw new ClevisException.ShapeNotFoundException("Shape not found: " + tokens[i]);
                }
                members.add(s);
            }

            if (members.isEmpty()) {
                throw new ClevisException("Group must have at least one member.");
            }

            final Group newGroup = new Group(groupName, members);
            for (Shape s : members) {
                manager.deleteShape(s.getName());
            }
            manager.addShape(newGroup);

            StringBuilder memberNames = new StringBuilder();
            for (int i = 0; i < members.size(); i++) {
                if (i > 0) memberNames.append(",");
                memberNames.append(members.get(i).getName());
            }

            System.out.printf("Created group %s containing: %s%n", groupName, memberNames.toString());
            // No additional logging needed - command is already logged
        }

        /**
         * [REQ7] Ungroup shapes
         */
        private void ungroupShapes(final String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: ungroup n");
            }

            final String groupName = tokens[1];
            final Shape s = manager.getShape(groupName);
            if (s == null) {
                throw new ClevisException.ShapeNotFoundException("Shape not found: " + groupName);
            }
            if (!(s instanceof Group g)) {
                throw new ClevisException("Shape '" + groupName + "' is not a group.");
            }

            StringBuilder memberNames = new StringBuilder();
            List<Shape> members = g.getMembers();
            for (int i = 0; i < members.size(); i++) {
                if (i > 0) memberNames.append(",");
                memberNames.append(members.get(i).getName());
            }

            manager.deleteShape(groupName);
            for (Shape m : g.getMembers()) {
                manager.addShape(m);
            }

            System.out.printf("Ungrouped %s into: %s%n", groupName, memberNames.toString());
            // No additional logging needed
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
        private void showHelp() {
            System.out.println("""
            ========================= CLEVIS HELP =========================
            Clevis is a command-line drawing and shape manipulation tool.

            Available commands:
              "rectangle n x y w h": Create a rectangle named n with top-left corner (x, y), width w, and height h.
              "line n x1 y1 x2 y2": Create a line segment named n from (x1, y1) to (x2, y2).
              circle n x y r: Create a circle named n with center (x, y) and radius r.
              "square n x y l": Create a square named n with top-left corner (x, y) and side length l.
              "group n n1 n2 ...": Group shapes n1, n2, ... into one shape named n.
              "ungroup n": Ungroup the group shape named n back into its members.
              "delete n": Delete a shape (or group) named n.
              "move n dx dy": Move shape n by dx horizontally and dy vertically.
              "boundingbox n": Display the minimum bounding box of shape n.
              "shapeAt x y": Find the topmost shape covering point (x, y).
              "intersect n1 n2": Check if two shapes (n1, n2) intersect.
              "list n": Show detailed info about a single shape.
              "listAll": List all shapes in Clevis (bottom to top).
              "help": Show this help guide.
              "quit": Exit Clevis and save logs.
            =================================================================
            """);
            // No additional logging needed - help command is already logged
        }
    }
        
}