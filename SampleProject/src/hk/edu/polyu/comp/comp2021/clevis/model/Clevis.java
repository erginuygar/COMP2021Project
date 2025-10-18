package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.*;
import java.util.logging.*;

class Logger {
    private String htmlLogPath;
    private String txtLogPath;

    public Logger(String[] args) {

        // Constructor implementation
    }

    /**
     * Logs all operations executed during a Clevis session into two files.
     * The first file is in HTML format, and the second is in plain TXT format.
     */
    public void log(String command) {
        // Implementation here
    }
}

public class CLEVISTool {
    private static Logger logger;
    private static final Logger clevis = logger.getLogger(CLEVISTool.class.getName());

    public static void main(String[] args) {
        // Main method to start the Clevis application.
    }
}

class ShapeManager {
    /**
     * [REQ1] All of the operations executed during a Clevis session should be logged
     * into two files. The first file is in HTML format, where the commands are recorded
     * in a table, and the second file is in plain TXT format.
     */
    public void addShape(Shape shape) throws DuplicateShapeException {
        // Implementation here
    }

    /**
     * [REQ8] The tool should support deleting a shape.
     * Command: delete n
     * Effect: Deletes the shape named n. If a shape is a group, all its members are also deleted.
     */
    public void deleteShape(String name) throws ShapeNotFoundException {
        // Implementation here
    }

    /**
     * Retrieves a shape by name.
     */
    public Shape getShape(String name) {
        // Implementation here
        return null;
    }

    /**
     * Returns a list of all shapes.
     */
    public List<Shape> getAllShapes() {
        // Implementation here
        return null;
    }
}

class CommandParser {
    private ShapeManager shapeManager;
    private Logger logger;

    public CommandParser(ShapeManager shapeManager, Logger logger) {
        // Constructor implementation
    }

    /**
     * Executes a command entered by the user.
     * Handles various shape operations based on the command.
     */
    public void execute(String command) {
        
        String[] tokens = command.trim().split("\\s+");
        String operation = tokens[0].toLowerCase();

        switch(operation){
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
                findTopmostShape(tokens);
                break;
            case "intersect":
                checkIntersection(tokens);
                break;
            case "list":
                listShape(tokens);
                break;
            case "listall":
                listAllShapes(tokens);
                break;
            case "quit":
                quitProgram(tokens);
                break;
            default:
                System.out.println("Unknown command: " + operation); 
        }

        //TODO: error handling
    }

    /**
     * [REQ2] The tool should support drawing a rectangle.
     * Command: rectangle n x y w h
     * Effect: Creates a new rectangle that has a name n, whose top-left corner is at
     * location (x, y), and whose width and height are w and h, respectively.
     */
    private void createRectangle(String[] tokens) {
        
        if (tokens.length != 6) {
            throw new IllegalArgumentException("Rectangle command required 5 parameters: name, x, y, width, height");
        }

        String name = tokens[1].trim();
        double x = Double.parseDouble(tokens[2]);
        double y = Double.parseDouble(tokens[3]);
        double width = Double.parseDouble(tokens[4]);
        double height = Double.parseDouble(tokens[5]);

        Rectangle rectangle = new Rectangle(n, x, y, width, height);

        System.out.println("Created a Rectangle named: " + name);

        //TODO: error handling
    }

    /**
     * [REQ3] The tool should support drawing a line segment.
     * Command: line n x1 y1 x2 y2
     * Effect: Creates a new line segment that has a name n and whose two ends are at
     * locations (x1, y1) and (x2, y2), respectively.
     */
    private void createLine(String[] tokens) {

        if (tokens.length != 6) {
            throw new IllegalArgumentException("Line command required 5 parameters: name, x1, y1, x2, y2");
        }

        String name = tokens[1].trim();
        double x1 = Double.parseDouble(tokens[2]);
        double y1 = Double.parseDouble(tokens[3]);
        double x2 = Double.parseDouble(tokens[4]);
        double y2 = Double.parseDouble(tokens[5]);

        System.out.println("Created a Line named: " + name);
    }

    /**
     * [REQ4] The tool should support drawing a circle.
     * Command: circle n x y r
     * Effect: Creates a new circle that has a name n, whose center is at location (x, y),
     * and whose radius is r.
     */
    private void createCircle(String[] tokens) {

        if (tokens.length != 5) {
            throw new IllegalArgumentException("Circle command required 4 parameters: name, x, y, radius");
        }

        String name = tokens[1].trim();
        double x = Double.parseDouble(tokens[2]);
        double y = Double.parseDouble(tokens[3]);
        double radius = Double.parseDouble(tokens[4]);

        System.out.println("Created a Circle named: " + name);
    }

    /**
     * [REQ5] The tool should support drawing a square.
     * Command: square n x y l
     * Effect: Creates a new square that has a name n, whose top-left corner is at
     * location (x, y), and whose side length is l.
     */
    private void createSquare(String[] tokens) {

        if (tokens.length != 5) {
            throw new IllegalArgumentException("Square command required 4 parameters: name, x, y, length");
        }

        String name = tokens[1].trim();
        double x = Double.parseDouble(tokens[2]);
        double y = Double.parseDouble(tokens[3]);
        double length = Double.parseDouble(tokens[4]);

        System.out.println("Created a Square named: " + name);
    }

    /**
     * [REQ6] The tool should support grouping a non-empty list of shapes into one shape.
     * Command: group n n1 n2 ...
     * Effect: Creates a new shape named n by grouping existing shapes named n1, n2, ...
     */
    private void groupShapes(String[] tokens) {

        if (tokens.length < 3) {
            throw new IllegalArgumentException("Group command required at least 3 parameters: group name, name1, name2, nameN...");
        }

        String name = tokens[1].trim();

        System.out.println("Grouped Shapes named: " + name);
    }

    /**
     * [REQ7] The tool should support ungrouping a shape that was created by grouping shapes.
     * Command: ungroup n
     * Effect: Ungroups shape n into its component shapes.
     */
    private void ungroupShapes(String[] tokens) {

         if (tokens.length != 2) {
            throw new IllegalArgumentException("Ungroup command required 1 parameters: name");
        }

        String name = tokens[1].trim();

        System.out.println("Ungrouped a Shape named: " + name);
    }
    /**
     * [REQ9] The tool should support calculating the minimum bounding box of a shape.
     * Command: boundingbox n
     * Effect: Calculates and outputs the minimum bounding box of the shape name n.
     */
    private void calculateBoundingBox(String[] tokens) {

        if (tokens.length != 2) {
            throw new IllegalArgumentException("Boundingbox command required 1 parameters: name");
        }

        String name = tokens[1].trim();

    }

    /**
     * [REQ10] The tool should support moving a shape.
     * Command: move n dx dy
     * Effect: Moves the shape named n, horizontally by dx and vertically by dy.
     */
    private void moveShape(String[] tokens) {

        if (tokens.length != 4) {
            throw new IllegalArgumentException("Boundingbox command required 3 parameters: name, dx, dy");
        }

        String name = tokens[1].trim();
        double dx = Double.parseDouble(tokens[2]);
        double dy = Double.parseDouble(tokens[3]);

        System.out.println("Moved Shape " + name + " with (x) " + dx + " and (y) " + dy);
    }

    /**
     * [REQ11] The tool should support finding the topmost shape that covers a point.
     * Command: shapeAt x y
     * Effect: Returns the name of the shape with the highest Z-index that covers point (x, y).
     */
    private void findTopmostShape(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ12] The tool should support reporting whether two shapes intersect with each other.
     * Command: intersect n1 n2
     * Effect: Reports whether two shapes n1 and n2 intersect with each other.
     */
    private void checkIntersection(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ13] The tool should support listing the basic information about a shape.
     * Command: list n
     * Effect: Lists the basic information about the shape named n.
     */
    private void listShape(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ14] The tool should support listing all shapes that have been drawn.
     * Command: listAll
     * Effect: Lists the basic information about all of the shapes that have been drawn in decreasing Z-order.
     */
    private void listAllShapes() {
        // Implementation here
    }
}

// Shape classes
class Rectangle {
    private String name;
    private double x, y, width, height;

    public Rectangle(String name, double x, double y, double w, double h) {

        if (name == null){
            throw new IllegalArgumentException("Name can not be null or empty.");
        }
        if (w <= 0){
            throw new IllegalArgumentException("Weight must be positive.");
        }
        if (h <= 0){
            throw new IllegalArgumentException("Height must be positive.");
        }
        
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
    }

    /**
     * Returns the values of the rectangle.
     */
    public String getName() {
        return name;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    /**
     * Returns information about the rectangle.
     */
    public String getInfo() {
        String rectangleInfo = "Rectangle[name: %s, x: %.2f, y: %.2f, width: %.2f, height: %.2f]";
        rectangleInfo = String.format(rectangleInfo, name, x, y, width, height);
        return rectangleInfo;
    }

    /**
     * Moves the rectangle by specified deltas.
     */
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns the bounding box of the rectangle.
     */
    public String getBoundingBox() {
        String boudingBox = "%.2f %.2f %.2f %.2f";
        boundingBox = String.format(boundingBox, x ,y, width, height);
        return boundingBox;
    }
}

class Line {
    private String name;
    private double x1, y1, x2, y2;

    public Line(String name, double x1, double y1, double x2, double y2) {
        // Constructor implementation
    }

    /**
     * Returns the name of the line.
     */
    public String getName() {
        // Implementation here
        return null;
    }

    /**
     * Returns information about the line.
     */
    public String getInfo() {
        // Implementation here
        return null;
    }

    /**
     * Moves the line by specified deltas.
     */
    public void move(double dx, double dy) {
        // Implementation here
    }

    /**
     * Returns the bounding box of the line.
     */
    public String getBoundingBox() {
        // Implementation here
        return null;
    }
}

class Circle {
    private String name;
    private double x, y, radius;

    public Circle(String name, double x, double y, double radius) {
        // Constructor implementation
    }

    /**
     * Returns the name of the circle.
     */
    public String getName() {
        // Implementation here
        return null;
    }

    /**
     * Returns information about the circle.
     */
    public String getInfo() {
        // Implementation here
        return null;
    }

    /**
     * Moves the circle by specified deltas.
     */
    public void move(double dx, double dy) {
        // Implementation here
    }

    /**
     * Returns the bounding box of the circle.
     */
    public String getBoundingBox() {
        // Implementation here
        return null;
    }
}

class Square {
    private String name;
    private double x, y, length;

    public Square(String name, double x, double y, double length) {
        // Constructor implementation
    }

    /**
     * Returns the name of the square.
     */
    public String getName() {
        // Implementation here
        return null;
    }

    /**
     * Returns information about the square.
     */
    public String getInfo() {
        // Implementation here
        return null;
    }

    /**
     * Moves the square by specified deltas.
     */
    public void move(double dx, double dy) {
        // Implementation here
    }

    /**
     * Returns the bounding box of the square.
     */
    public String getBoundingBox() {
        // Implementation here
        return null;
    }
}

// Implement custom exceptions
class ClevisException extends Exception {
    public ClevisException(String message) {
        // Constructor implementation
    }
}

class DuplicateShapeException extends ClevisException {
    public DuplicateShapeException(String message) {
        // Constructor implementation
    }
}

class ShapeNotFoundException extends ClevisException {
    public ShapeNotFoundException(String message) {
        // Constructor implementation
    }
}

class GroupingException extends ClevisException {
    public GroupingException(String message) {
        // Constructor implementation
    }
}

