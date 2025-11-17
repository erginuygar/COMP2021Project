package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.*;

/**
 * Clevis main logic and shapes.
 * REQ2..REQ15.
 *
 * Notes:
 *  - Logging (REQ1) is handled by hk.edu.polyu.comp.comp2021.clevis.model.ClevisLogger (separate file).
 *  - And all of it including this file is integrated into Application. Just run the Appplication.java.
 */
public class Clevis {

    private final ShapeManager shapeManager;
    private final CommandParser parser;
    private final ClevisLogger logger;

    public Clevis(String htmlPath, String txtPath) {
        this.logger = new ClevisLogger(htmlPath, txtPath);
        this.shapeManager = new ShapeManager();
        this.parser = new CommandParser(shapeManager, logger);
    }

    /**
     * Run interactive CLI. If commands != null and not empty, run them first as batch (script)
     * then enter interactive mode. Program terminates only on 'quit'.
     */
    public void run() {
        // Run batch commands first (if any)
//        if (batchCommands != null) {
//            for (String c : batchCommands) {
//                parser.execute(c);
//            }
//        }

        // Interactive loop
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to Clevis! Type commands or 'quit' to exit.");
        while (true) {
            System.out.print("> ");
            String line;
            try {
                line = in.nextLine();
            } catch (NoSuchElementException | IllegalStateException e) {
                break;
            }
            if (line == null) break;
            parser.execute(line);
        }
        System.out.println("Clevis terminated.");
    }

    // Shape & Manager Classes
    abstract static class Shape {
        public Shape() {}
        public abstract double getArea();
        public abstract String getName();
        public abstract String getInfo();
        public abstract void move(double dx, double dy);
        public abstract String getBoundingBox();
        public abstract boolean coversPoint(double x, double y);
    }

    /**
     * ShapeManager: maintains insertion order (Z-order: later shapes on top)
     * and provides operations used by CommandParser.
     */
    static class ShapeManager {
        private final Map<String, Shape> shapesByName = new LinkedHashMap<>(); // preserves insertion order

        public void addShape(Shape s) throws DuplicateShapeException {
            if (shapesByName.containsKey(s.getName())) {
                throw new DuplicateShapeException("The Shape " + s.getName() + " is already in the list");
            }
            shapesByName.put(s.getName(), s);
        }

        /**
         * Delete a shape. If it's a group, delete the group and (per REQ8) its members.
         */
        public void deleteShape(String name) throws ShapeNotFoundException {
            Shape s = shapesByName.get(name);
            if (s == null) throw new ShapeNotFoundException("The Shape " + name + " is not in the list");
            // If group, remove its members as well
            if (s instanceof Group) {
                Group g = (Group) s;
                for (Shape member : g.getMembers()) {
                    shapesByName.remove(member.getName());
                }
            }
            shapesByName.remove(name);
        }

        public Shape getShape(String name) {
            return shapesByName.get(name);
        }

        /**
         * Return list bottom-to-top (increasing Z) — LinkedHashMap preserves insertion order,
         * so values() iterates bottom-to-top (older first).
         */
        public List<Shape> getAllShapes() {
            return new ArrayList<>(shapesByName.values());
        }

        /**
         * Return bounding box string "x y w h" for a named shape.
         */
        public String getBoundingBox(String name) throws ShapeNotFoundException {
            Shape s = getShape(name);
            if (s == null) throw new ShapeNotFoundException("Shape not found: " + name);
            return s.getBoundingBox();
        }
    }

    // Command Parser (REQ2..REQ15)
    static class CommandParser {
        private final ShapeManager manager;
        private final ClevisLogger logger;

        public CommandParser(ShapeManager manager, ClevisLogger logger) {
            this.manager = manager;
            this.logger = logger;
        }

        /**
         * Execute a single command string.
         * Logs every executed command (REQ1).
         */
        public void execute(String command) {
            if (command == null) return;
            String trimmed = command.trim();
            if (trimmed.isEmpty()) return;

            // Log command (REQ1). We log before execution so even failing commands appear.
            logger.logCommand(trimmed);

            String[] tokens = trimmed.split("\\s+");
            String op = tokens[0].toLowerCase(Locale.ROOT);
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
                        logger.close(); // ✅ properly closes HTML file tags
                        System.exit(0);
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
        private void createRectangle(String[] tokens) throws ClevisException {
            try {
                if (tokens.length != 6) {
                    throw new ClevisException("Rectangle command required 5 parameters: name, x, y, width, height");
                }

                String name = tokens[1].trim();
                double x = Double.parseDouble(tokens[2]);
                double y = Double.parseDouble(tokens[3]);
                double width = Double.parseDouble(tokens[4]);
                double height = Double.parseDouble(tokens[5]);

                Rectangle rectangle = new Rectangle(name, x, y, width, height);
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
        private void createLine(String[] tokens) throws ClevisException {
            try {
                if (tokens.length != 6) {
                    throw new ClevisException("Line command required 5 parameters: name, x1, y1, x2, y2");
                }

                String name = tokens[1].trim();
                double x1 = Double.parseDouble(tokens[2]);
                double y1 = Double.parseDouble(tokens[3]);
                double x2 = Double.parseDouble(tokens[4]);
                double y2 = Double.parseDouble(tokens[5]);

                Line line = new Line(name, x1, y1, x2, y2);
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
        private void createCircle(String[] tokens) throws ClevisException {
            try {
                if (tokens.length != 5) {
                    throw new ClevisException("Circle command required 4 parameters: name, x, y, radius");
                }

                String name = tokens[1].trim();
                double x = Double.parseDouble(tokens[2]);
                double y = Double.parseDouble(tokens[3]);
                double radius = Double.parseDouble(tokens[4]);

                Circle circle = new Circle(name, x, y, radius);
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
        private void createSquare(String[] tokens) throws ClevisException {
            try {
                if (tokens.length != 5) {
                    throw new ClevisException("Square command required 4 parameters: name, x, y, length");
                }

                String name = tokens[1].trim();
                double x = Double.parseDouble(tokens[2]);
                double y = Double.parseDouble(tokens[3]);
                double length = Double.parseDouble(tokens[4]);

                Square square = new Square(name, x, y, length);
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
        private void groupShapes(String[] tokens) throws ClevisException {
            if (tokens.length < 3) {
                throw new ClevisException("Usage: group n n1 n2 ...");
            }

            String groupName = tokens[1];
            List<Shape> members = new ArrayList<>();

            for (int i = 2; i < tokens.length; i++) {
                Shape s = manager.getShape(tokens[i]);
                if (s == null) throw new ShapeNotFoundException("Shape not found: " + tokens[i]);
                members.add(s);
            }

            if (members.isEmpty()) throw new ClevisException("Group must have at least one member.");

            Group newGroup = new Group(groupName, members);
            for (Shape s : members) manager.deleteShape(s.getName());
            manager.addShape(newGroup);

            System.out.println("Group '" + groupName + "' created with " + members.size() + " shapes.");
        }

        /**
         * [REQ7] The tool should support ungrouping a shape that was created by grouping shapes.
         * Command: ungroup n
         * Effect: Ungroups shape n into its component shapes.
         */
        private void ungroupShapes(String[] tokens) throws ClevisException {
            if (tokens.length != 2) {
                throw new ClevisException("Usage: ungroup n");
            }

            String groupName = tokens[1];
            Shape s = manager.getShape(groupName);
            if (s == null) throw new ShapeNotFoundException("Shape not found: " + groupName);
            if (!(s instanceof Group)) throw new ClevisException("Shape '" + groupName + "' is not a group.");

            Group g = (Group) s;
            manager.deleteShape(groupName);
            for (Shape m : g.getMembers()) {
                manager.addShape(m);
            }
            System.out.println("Group '" + groupName + "' has been ungrouped.");
        }

        /**
         * [REQ8] Delete a shape by name.
         */
        private void deleteShape(String[] tokens) throws ClevisException {
            if (tokens.length != 2) throw new ClevisException("Usage: delete n");
            manager.deleteShape(tokens[1]);
            System.out.println("Deleted shape: " + tokens[1]);
        }

        /**
         * [REQ9] The tool should support calculating the minimum bounding box of a shape.
         * Command: boundingbox n
         * Effect: Calculates and outputs the minimum bounding box of the shape name n.
         */
        private void calculateBoundingBox(String[] tokens) throws ClevisException {
            try {
                if (tokens.length != 2) {
                    throw new ClevisException("Usage: boundingbox n");
                }

                String name = tokens[1].trim();
                String bbox = manager.getBoundingBox(name);
                System.out.println("The bounding box for " + name + ": " + bbox);
            } catch (ShapeNotFoundException e) {
                throw new ClevisException("Cannot calculate bounding box: " + e.getMessage());
            }
        }

        /**
         * [REQ10] The tool should support moving a shape.
         * Command: move n dx dy
         * Effect: Moves the shape named n, horizontally by dx and vertically by dy.
         */
        private void moveShape(String[] tokens) throws ClevisException {
            try {
                if (tokens.length != 4) {
                    throw new ClevisException("Usage: move n dx dy");
                }

                String name = tokens[1].trim();
                double dx = Double.parseDouble(tokens[2]);
                double dy = Double.parseDouble(tokens[3]);
                Shape s = manager.getShape(name);
                if (s == null) throw new ShapeNotFoundException("Shape not found: " + name);
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
        private void findTopmost(String[] tokens) {

            try {
                if (tokens.length != 3) {
                    throw new IllegalArgumentException("Usage: shapeAt x y");
                }

                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                List<Shape> allShapes = manager.getAllShapes();

                for (int i = allShapes.size() - 1; i >= 0; i--) {
                    Shape shape = allShapes.get(i);
                    if (shape.coversPoint(x, y)) {
                        System.out.println("The topmost shape covering point (" + x + ", " + y + ") is: " + shape.getName());
                        return;
                    }
                }

                System.out.println("No shape covers the given point (" + x + ", " + y + ").");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The parameters must be valid numbers for x and y.", e);
            }
        }


        /**
         * [REQ12] The tool should support reporting whether two shapes intersect with each other.
         * Command: intersect n1 n2
         * Effect: Reports whether two shapes n1 and n2 intersect with each other.
         */
        private void intersect(String[] tokens) {

            try {
                if (tokens.length != 3) {
                    throw new IllegalArgumentException("Usage: intersect n1 n2");
                }

                String n1 = tokens[1].trim();
                String n2 = tokens[2].trim();

                Shape shape1 = manager.getShape(n1);
                Shape shape2 = manager.getShape(n2);

                if (shape1 == null || shape2 == null) {
                    throw new RuntimeException("One or both shapes were not found: " + n1 + ", " + n2);
                }

                String[] box1 = shape1.getBoundingBox().split("\\s+");
                String[] box2 = shape2.getBoundingBox().split("\\s+");

                double x1 = Double.parseDouble(box1[0]);
                double y1 = Double.parseDouble(box1[1]);
                double w1 = Double.parseDouble(box1[2]);
                double h1 = Double.parseDouble(box1[3]);

                double x2 = Double.parseDouble(box2[0]);
                double y2 = Double.parseDouble(box2[1]);
                double w2 = Double.parseDouble(box2[2]);
                double h2 = Double.parseDouble(box2[3]);

                boolean separated = (x1 + w1 < x2) || (x2 + w2 < x1) || (y1 + h1 < y2) || (y2 + h2 < y1);
                String result = separated ? "No" : "Yes";
                System.out.println("Do shapes '" + n1 + "' and '" + n2 + "' intersect? " + result);

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid bounding box data. Ensure all coordinates are numeric.", e);
            }
        }


        /**
         * [REQ13] The tool should support listing the basic information about a shape.
         * Command: list n
         * Effect: Lists the basic information about the shape named n.
         */
        private void listShape(String[] tokens) {

            try {
                if (tokens.length != 2) {
                    throw new IllegalArgumentException("Usage: list n");
                }

                String name = tokens[1].trim();
                Shape shape = manager.getShape(name);
                if (shape == null) throw new ShapeNotFoundException("Shape not found: " + name);

                System.out.println("Shape Info:\n" + shape.getInfo());
            } catch (Exception e) {
                throw new RuntimeException("Error listing shape: " + e.getMessage(), e);
            }
        }


        /**
         * [REQ14] The tool should support listing all shapes that have been drawn.
         * Command: listAll
         * Effect: Lists the basic information about all shapes in decreasing Z-order.
         */
        private void listAll(String[] tokens) {

            if (tokens.length != 1) {
                throw new IllegalArgumentException("Usage: listAll");
            }

            List<Shape> allShapes = manager.getAllShapes();
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
        private void quit(String[] tokens) {
            System.out.println("Exiting Clevis... Goodbye!");
            System.exit(0);
        }
    }

    // Shape Implementations
    static final class Rectangle extends Shape {
        private final String name;
        private double x, y, w, h;

        public Rectangle(String name, double x, double y, double w, double h) {
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException("Name cannot be null or empty.");
            if (w <= 0 || h <= 0)
                throw new IllegalArgumentException("Width/height must be positive.");
            this.name = name;
            this.x = x; this.y = y; this.w = w; this.h = h;
        }

        @Override public double getArea() {
            return w * h;
        }
        @Override public String getName() {
            return name;
        }
        @Override public String getInfo() {
            return String.format("Rectangle[name:%s,(x,y):(%.2f,%.2f),w:%.2f,h:%.2f]", name, x, y, w, h);
        }
        @Override public void move(double dx, double dy) {
            x += dx; y += dy;
        }
        @Override public String getBoundingBox() {
            return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f", x, y, w, h);
        }
        @Override public boolean coversPoint(double px, double py) {
            return px >= x && px <= x + w && py >= y && py <= y + h;
        }
    }

    static final class Line extends Shape {
        private final String name;
        private double x1, y1, x2, y2;

        public Line(String name, double x1, double y1, double x2, double y2) {
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException("Name cannot be null or empty.");
            if (x1 == x2 && y1 == y2)
                throw new IllegalArgumentException("Line endpoints must differ.");
            this.name = name;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        @Override public double getArea() {
            return 0;
        }
        @Override public String getName() {
            return name;
        }
        @Override public String getInfo() {
            return String.format("Line[name:%s,(x1,y1):(%.2f,%.2f),(x2,y2):(%.2f,%.2f)]", name, x1, y1, x2, y2);
        }
        @Override public void move(double dx, double dy) {
            x1 += dx;
            y1 += dy;
            x2 += dx;
            y2 += dy;
        }
        @Override public String getBoundingBox() {
            double minX = Math.min(x1, x2),
                    minY = Math.min(y1, y2);
            double w = Math.abs(x2 - x1),
                    h = Math.abs(y2 - y1);
            return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f", minX, minY, w, h);
        }
        @Override public boolean coversPoint(double px, double py) {
            double minX = Math.min(x1, x2),
                    maxX = Math.max(x1, x2),
                    minY = Math.min(y1, y2),
                    maxY = Math.max(y1, y2);
            return px >= minX && px <= maxX && py >= minY && py <= maxY;
        }
    }

    static final class Circle extends Shape {
        private final String name;
        private double x, y, r;
        public Circle(String name, double x, double y, double r) {
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name cannot be null or empty.");
            if (r <= 0) throw new IllegalArgumentException("Radius must be positive.");
            this.name = name; this.x = x; this.y = y; this.r = r;
        }
        @Override public double getArea() {
            return Math.PI * r * r;
        }
        @Override public String getName() {
            return name;
        }
        @Override public String getInfo() {
            return String.format("Circle[name:%s,(x,y):(%.2f,%.2f),r:%.2f]", name, x, y, r);
        }
        @Override public void move(double dx, double dy) {
            x += dx; y += dy;
        }
        @Override public String getBoundingBox() {
            double tlx = x - r,
                    tly = y - r,
                    wh = 2 * r;
            return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f", tlx, tly, wh, wh);
        }
        @Override public boolean coversPoint(double px, double py) {
            double dx = px - x,
                    dy = py - y;
            return (dx * dx + dy * dy) <= r * r;
        }
    }

    static final class Square extends Shape {
        private final String name;
        private double x, y, len;
        public Square(String name, double x, double y, double len) {
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException("Name cannot be null or empty.");
            if (len <= 0)
                throw new IllegalArgumentException("Length must be positive.");
            this.name = name;
            this.x = x;
            this.y = y;
            this.len = len;
        }
        @Override public double getArea() {
            return len * len;
        }
        @Override public String getName() {
            return name;
        }
        @Override public String getInfo() {
            return String.format("Square[name:%s,(x,y):(%.2f,%.2f),len:%.2f]", name, x, y, len);
        }
        @Override public void move(double dx, double dy) {
            x += dx;
            y += dy;
        }
        @Override public String getBoundingBox() {
            return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f", x, y, len, len);
        }
        @Override public boolean coversPoint(double px, double py) {
            return px >= x && px <= x + len && py >= y && py <= y + len;
        }
    }

    static final class Group extends Shape {
        private final String name;
        private final List<Shape> members;
        public Group(String name, List<Shape> members) {
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name cannot be null or empty.");
            if (members == null || members.isEmpty()) throw new IllegalArgumentException("Group must have members.");
            this.name = name;
            this.members = new ArrayList<>(members);
        }
        public List<Shape> getMembers() {
            return Collections.unmodifiableList(members);
        }
        @Override public double getArea() {
            double a = 0;
            for (Shape s : members) {
                a += s.getArea();
            }
            return a;
        }
        @Override public String getName() {
            return name;
        }
        @Override public String getInfo() {
            return String.format("Group[name:%s,members:%d]", name, members.size());
        }
        @Override public void move(double dx, double dy) {
            for (Shape s : members) s.move(dx, dy);
        }
        @Override public String getBoundingBox() {
            double minX = Double.POSITIVE_INFINITY,
                    minY = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY,
                    maxY = Double.NEGATIVE_INFINITY;
            for (Shape s : members) {
                String[] bb = s.getBoundingBox().split("\\s+");
                double x = Double.parseDouble(bb[0]);
                double y = Double.parseDouble(bb[1]);
                double w = Double.parseDouble(bb[2]);
                double h = Double.parseDouble(bb[3]);
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x + w);
                maxY = Math.max(maxY, y + h);
            }
            return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f", minX, minY, (maxX - minX), (maxY - minY));
        }
        @Override public boolean coversPoint(double px, double py) {
            for (Shape s : members) {
                if (s.coversPoint(px, py)) return true;
            }
            return false;
        }
    }


    // Exceptions
    static class ClevisException extends Exception {
        public ClevisException(String m) {
            super(m);
        }
    }
    static class DuplicateShapeException extends ClevisException {
        public DuplicateShapeException(String m) {
            super(m);
        }
    }
    static class ShapeNotFoundException extends ClevisException {
        public ShapeNotFoundException(String m) {
            super(m);
        }
    }
    static class GroupingException extends ClevisException {
        public GroupingException(String m) {
            super(m);
        }
    }
}
