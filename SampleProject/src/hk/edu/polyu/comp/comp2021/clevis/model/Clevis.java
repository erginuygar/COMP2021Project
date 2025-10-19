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

    private Map<String, Shape> shapes;
    private List<Shape> shapeList;

    public ShapeManager() {
        this.shapes = new HashMap<>();
        this.shapeList = new ArrayList<>();
    }
    /**
     * [REQ1] All of the operations executed during a Clevis session should be logged
     * into two files. The first file is in HTML format, where the commands are recorded
     * in a table, and the second file is in plain TXT format.
     */
    public void addShape(Shape shape) throws DuplicateShapeException {
        if (shapes.containskey(shapes.getName()) {
            throw DuplicateShapeException("The Shape " + name + " is already in the list");
        }
        shapes.put(shape.getName(),shape);
        shapeList.add(shape);
    }

    /**
     * [REQ8] The tool should support deleting a shape.
     * Command: delete n
     * Effect: Deletes the shape named n. If a shape is a group, all its members are also deleted.
     */
    public void deleteShape(String name) throws ShapeNotFoundException {
        Shape shape = shapes.get(name);
        if (shape == null) {
            throw ShapeNotFoundException("The Shape " + name + " is not in the list");
        }
        
        // not yet done
    }

    /**
     * Retrieves a shape by name.
     */
    public Shape getShape(String name) {
        return shapes.get(name);
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
        this.shapeManager = shapeManager;
        this.logger = logger;
    }

    /**
     * Executes a command entered by the user.
     * Handles various shape operations based on the command.
     */
    public void execute(String command) {

        try{
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * [REQ2] The tool should support drawing a rectangle.
     * Command: rectangle n x y w h
     * Effect: Creates a new rectangle that has a name n, whose top-left corner is at
     * location (x, y), and whose width and height are w and h, respectively.
     */
    private void createRectangle(String[] tokens) {

        try{
            if (tokens.length != 6) {
                throw new IllegalArgumentException("Rectangle command required 5 parameters: name, x, y, width, height");
            }
    
            String name = tokens[1].trim();
            double x = Double.parseDouble(tokens[2]);
            double y = Double.parseDouble(tokens[3]);
            double width = Double.parseDouble(tokens[4]);
            double height = Double.parseDouble(tokens[5]);
    
            Rectangle rectangle = new Rectangle(name, x, y, width, height);
            shapeManager.addShape(rectangle);
            System.out.println("Created a Rectangle named: " + name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameters except for name must be a valid number.", e);
        }

    }

    /**
     * [REQ3] The tool should support drawing a line segment.
     * Command: line n x1 y1 x2 y2
     * Effect: Creates a new line segment that has a name n and whose two ends are at
     * locations (x1, y1) and (x2, y2), respectively.
     */
    private void createLine(String[] tokens) {

        try{
            if (tokens.length != 6) {
                throw new IllegalArgumentException("Line command required 5 parameters: name, x1, y1, x2, y2");
            }
    
            String name = tokens[1].trim();
            double x1 = Double.parseDouble(tokens[2]);
            double y1 = Double.parseDouble(tokens[3]);
            double x2 = Double.parseDouble(tokens[4]);
            double y2 = Double.parseDouble(tokens[5]);
    
            Line line = new Line(name, x1, y1, x2, y2);
            shapeManager.addShape(line);
            System.out.println("Created a Line named: " + name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameters except for name must be a valid number.", e);
        }
        
    }

    /**
     * [REQ4] The tool should support drawing a circle.
     * Command: circle n x y r
     * Effect: Creates a new circle that has a name n, whose center is at location (x, y),
     * and whose radius is r.
     */
    private void createCircle(String[] tokens) {

        try{
            if (tokens.length != 5) {
                throw new IllegalArgumentException("Circle command required 4 parameters: name, x, y, radius");
            }
    
            String name = tokens[1].trim();
            double x = Double.parseDouble(tokens[2]);
            double y = Double.parseDouble(tokens[3]);
            double radius = Double.parseDouble(tokens[4]);
    
            Circle circle = new Circle(name, x, y, radius);
            shapeManager.addShape(circle);
            System.out.println("Created a Circle named: " + name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameters except for name must be a valid number.", e);
        }
    }

    /**
     * [REQ5] The tool should support drawing a square.
     * Command: square n x y l
     * Effect: Creates a new square that has a name n, whose top-left corner is at
     * location (x, y), and whose side length is l.
     */
    private void createSquare(String[] tokens) {

        try{
            if (tokens.length != 5) {
                throw new IllegalArgumentException("Square command required 4 parameters: name, x, y, length");
            }
    
            String name = tokens[1].trim();
            double x = Double.parseDouble(tokens[2]);
            double y = Double.parseDouble(tokens[3]);
            double length = Double.parseDouble(tokens[4]);
    
            Square square = new Square(name, x, y, length);
            shapeManager.addShape(square);
            System.out.println("Created a Square named: " + name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameters except for name must be a valid number.", e);
        }
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

        try{
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Boundingbox command required 1 parameters: name");
            }

            String name = tokens[1].trim();
            String boundingBox = shapeManager.getBoundingBox(shapeName);
            System.out.println("The bounding box for " + name + ": " + boundingBox);
            
        } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Cannot calculate bounding box: " + e.getMessage(), e);
        } catch (Exception e) {
        throw new RuntimeException("Error calculating bounding box: " + e.getMessage(), e);
    }

    }

    /**
     * [REQ10] The tool should support moving a shape.
     * Command: move n dx dy
     * Effect: Moves the shape named n, horizontally by dx and vertically by dy.
     */
    private void moveShape(String[] tokens) {

        try{
            if (tokens.length != 4) {
                throw new IllegalArgumentException("Move command required 3 parameters: name, dx, dy");
            }
    
            String name = tokens[1].trim();
            double dx = Double.parseDouble(tokens[2]);
            double dy = Double.parseDouble(tokens[3]);
    
            System.out.println("Moved Shape " + name + " with (x) " + dx + " and (y) " + dy);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameters except for name must be a valid number.", e);
        }
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

    public Rectangle(String name, double x, double y, double width, double height) {

        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name can not be null or empty.");
        }
        if (width <= 0){
            throw new IllegalArgumentException("Width must be positive.");
        }
        if (height <= 0){
            throw new IllegalArgumentException("Height must be positive.");
        }
        
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the name of the rectangle.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns information about the rectangle.
     */
    public String getInfo() {
        String rectangleInfo = "Rectangle[name: %s, (x,y): (%.2f,%.2f), width: %.2f, height: %.2f]";
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
        String boundingBox = "%.2f %.2f %.2f %.2f";
        boundingBox = String.format(boundingBox, x ,y, width, height);
        return boundingBox;
    }
}

class Line {
    private String name;
    private double x1, y1, x2, y2;

    public Line(String name, double x1, double y1, double x2, double y2) {
        
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name can not be null or empty.");
        }
        if (x1 == x2 && y1 == y2){
            throw new IllegalArgumentException("The 2 coordinates cannot be same.");
        }
    
        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Returns the name of the line.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns information about the line.
     */
    public String getInfo() {
        String lineInfo = "Line[name: %s, (x1,y1): (%.2f,%.2f), (x2,y2): (%.2f,%.2f)]";
        lineInfo = String.format(lineInfo, name, x1, y1, x2, y2);
        return lineInfo;
    }

    /**
     * Moves the line by specified deltas.
     */
    public void move(double dx, double dy) {
        this.x1 += dx;
        this.y1 += dy;
        this.x2 += dx;
        this.y2 += dy;
    }

    /**
     * Returns the bounding box of the line.
     */
    public String getBoundingBox() {

        double topLeftX, topLeftY, width, height;
        if (x1 > x2){
            width = x1 - x2;
            topLeftX = x2;
        } else if (x2 > x1){
            width = x2 - x1;
            topLeftX = x1;
        } else {
            width = 0;
            topLeftX = x1;
        }

        if (y1 > y2){
            height = y1 - y2;
            topLeftY = y2;
        } else if (y2 > y1){
            height = y2 - y1;
            topLeftY = y1;
        } else {
            height = 0;
            topLeftY = y1;
        }
        
        String boundingBox = "%.2f %.2f %.2f %.2f";
        boundingBox = String.format(boundingBox, topLeftX , topLeftY, width, height);
        return boundingBox;
    }
} 

class Circle {
    private String name;
    private double x, y, radius;

    public Circle(String name, double x, double y, double radius) {

         if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name can not be null or empty.");
        }
        if (radius <= 0){
            throw new IllegalArgumentException("Radius must be positive.");
        }
        
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    /**
     * Returns the name of the circle.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns information about the circle.
     */
    public String getInfo() {
        String circleInfo = "Circle[name: %s, (x,y): (%.2f,%.2f), radius: %.2f]";
        circleInfo = String.format(circleInfo, name, x, y, radius);
        return circleInfo;
    }

    /**
     * Moves the circle by specified deltas.
     */
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns the bounding box of the circle.
     */
    public String getBoundingBox() {

        double topLeftX = x - radius;
        double topLeftY = y - radius;
        double widthANDheight = 2*radius;
        String boundingBox = "%.2f %.2f %.2f %.2f";
        boundingBox = String.format(boundingBox, topLeftX , topLeftY, widthANDheight, widthANDheight);
        return boundingBox;
    }
}

class Square {
    private String name;
    private double x, y, length;

    public Square(String name, double x, double y, double length) {

        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name can not be null or empty.");
        }
        if (length <= 0){
            throw new IllegalArgumentException("Length must be positive.");
        }
        
        this.name = name;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    /**
     * Returns the name of the square.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns information about the square.
     */
    public String getInfo() {
        String squareInfo = "Square[name: %s, (x,y): (%.2f,%.2f), length: %.2f]";
        squareInfo = String.format(squareInfo, name, x, y, length);
        return squareInfo;
    }

    /**
     * Moves the square by specified deltas.
     */
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns the bounding box of the square.
     */
    public String getBoundingBox() {
        String boundingBox = "%.2f %.2f %.2f %.2f";
        boundingBox = String.format(boundingBox, x , y, length, length);
        return boundingBox;
    }
}

// Implement custom exceptions
class ClevisException extends Exception {
    public ClevisException(String message) {
        super(message);
    }
}

class DuplicateShapeException extends ClevisException {
    public DuplicateShapeException(String message) {
        super(message);
    }
}

class ShapeNotFoundException extends ClevisException {
    public ShapeNotFoundException(String message) {
        super(message);
    }
}

class GroupingException extends ClevisException {
    public GroupingException(String message) {
        super(message);
    }
}


