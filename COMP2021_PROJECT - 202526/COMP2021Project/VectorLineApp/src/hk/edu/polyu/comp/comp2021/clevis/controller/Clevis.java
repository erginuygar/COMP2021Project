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
    private final ClevisLogger logger;
    private final ConsoleView view;

    /**
     * Initialize Clevis system with given log paths.
     *
     * @param htmlPath HTML log path
     * @param txtPath  text log path
     */
    public Clevis(final String htmlPath, final String txtPath) {
        this.logger = new ClevisLogger(htmlPath, txtPath);
        this.shapeManager = new ShapeManager();
        this.parser = new CommandParser(shapeManager, logger);
        this.view = new ConsoleView();
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
        }
    }


    /**
     * CommandParser is responsible for interpreting and executing all Clevis commands.
     *
     * It implements the main functional requirements (REQ2–REQ16), handling:
     *
     *   Shape creation (rectangle, line, circle, square)
     *   Shape manipulation (move, delete, group, ungroup)
     *   Information queries (list, listAll, boundingbox, shapeAt, intersect)
     *   User commands (help, quit)
     * @version 1.0
     * @since COMP2021 Project — Clevis Tool
     */
    static class CommandParser {
        private final ShapeManager manager;
        private final ClevisLogger logger;

        /**
         * Constructs a new {@code CommandParser} instance.
         * <p>
         * This parser interprets and executes all user commands entered in the Clevis console.
         * It uses the provided {@link ShapeManager} to manage shapes and the {@link ClevisLogger}
         * to record every command execution for REQ1 compliance.
         *
         * @param manager the shape manager responsible for storing and manipulating all shapes
         * @param logger  the logger responsible for recording user commands
         */
        CommandParser(final ShapeManager manager, final ClevisLogger logger) {
            this.manager = manager;
            this.logger = logger;
        }

        /**
         * Execute a single command string.
         * Logs every executed command (REQ1).
         *
         * @param command user input command string
         */
        public void execute(final String command) {
            if (command == null) {
                return;
            }

            final String trimmed = command.trim();
            if (trimmed.isEmpty()) {
                return;
            }

            // Log command (REQ1). We log before execution so even failing commands appear.
            logger.logCommand(trimmed);

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
                        // Log this quit command (REQ1)
                        logger.logCommand(trimmed);
                        System.out.println("Clevis session ended. Logs saved.");
                        logger.close(); // properly closes HTML file tags
                        System.exit(0);
                        break;
                    case "help":
                        showHelp();
                        break;

                    default:
                        System.out.println("Unknown command: " + op);
                }
            } catch (ClevisException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid number format.");
            } catch (RuntimeException e) {
                System.out.println("Runtime error: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }



        //Command handlers
        /**
         * [REQ2] The tool should support drawing a rectangle.
         * Command: rectangle n x y w h
         * Effect: Creates a new rectangle that has a name n, whose top-left corner is at
         * location (x, y), and whose width and height are w and h, respectively.
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
                System.out.println("Created a Rectangle named: " + name);
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ3] The tool should support drawing a line segment.
         * Command: line n x1 y1 x2 y2
         * Effect: Creates a new line segment that has a name n and whose two ends are at
         * locations (x1, y1) and (x2, y2), respectively.
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
                System.out.println("Created a Line named: " + name);
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ4] The tool should support drawing a circle.
         * Command: circle n x y r
         * Effect: Creates a new circle that has a name n, whose center is at location (x, y),
         * and whose radius is r.
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
                System.out.println("Created a Circle named: " + name);
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ5] The tool should support drawing a square.
         * Command: square n x y l
         * Effect: Creates a new square that has a name n, whose top-left corner is at
         * location (x, y), and whose side length is l.
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
                System.out.println("Created a Square named: " + name);
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ6] The tool should support grouping a non-empty list of shapes into one shape.
         * Command: group n n1 n2 ...
         * Effect: Creates a new shape named n by grouping existing shapes named n1, n2, ...
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

            System.out.println("Group '" + groupName + "' created with " + members.size() + " shapes.");
        }

        /**
         * [REQ7] The tool should support ungrouping a shape that was created by grouping shapes.
         * Command: ungroup n
         * Effect: Ungroups shape n into its component shapes.
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

            manager.deleteShape(groupName);
            for (Shape m : g.getMembers()) {
                manager.addShape(m);
            }
            System.out.println("Group '" + groupName + "' has been ungrouped.");
        }

        /**
         * [REQ8] Delete a shape by name.
         */
        private void deleteShape(final String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: delete n");
            }
            manager.deleteShape(tokens[1]);
            System.out.println("Deleted shape: " + tokens[1]);
        }

        /**
         * [REQ9] The tool should support calculating the minimum bounding box of a shape.
         * Command: boundingbox n
         * Effect: Calculates and outputs the minimum bounding box of the shape name n.
         */
        private void calculateBoundingBox(final String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: boundingbox n");
            }

            final String name = tokens[1].trim();
            final String bbox = manager.getBoundingBox(name);
            System.out.println("The bounding box for " + name + ": " + bbox);
        }

        /**
         * [REQ10] The tool should support moving a shape.
         * Command: move n dx dy
         * Effect: Moves the shape named n, horizontally by dx and vertically by dy.
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
                System.out.println("Moved Shape " + name + " by dx=" + dx + ", dy=" + dy);
            } catch (NumberFormatException e) {
                throw new ClevisException("The parameters except for name must be valid numbers.");
            }
        }

        /**
         * [REQ11] The tool should support finding the topmost shape that covers a point.
         * Command: shapeAt x y
         * Effect: Returns the name of the shape with the highest Z-index that covers point (x, y).
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
         * [REQ12] The tool should support reporting whether two shapes intersect with each other.
         * Command: intersect n1 n2
         * Effect: Reports whether two shapes n1 and n2 intersect with each other.
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
            final String result = separated ? "No" : "Yes";
            System.out.println("Do shapes '" + n1 + "' and '" + n2 + "' intersect? " + result);

        }

        /**
         * [REQ13] The tool should support listing the basic information about a shape.
         * Command: list n
         * Effect: Lists the basic information about the shape named n.
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
            System.out.println("Shape Info:\n" + shape.getInfo());

        }

        /**
         * [REQ14] The tool should support listing all shapes that have been drawn.
         * Command: listAll
         * Effect: Lists the basic information about all shapes in decreasing Z-order.
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
            }
        }

        /**
         * [REQ15] Quit Command
         * Command: quit
         * Effect: Exits the Clevis application safely.
         */
        private void quit() {
            System.out.println("Clevis session ended. Logs saved.");
            logger.close();
            System.exit(0);
        }
        /**
         * [REQ16] Help command: displays all available commands and usage instructions.
         * Command: help
         * Effect: Prints information about how to use Clevis and its supported commands.
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
        }
    }
}
