import java.util.*;

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
        // Implementation here
    }

    /**
     * [REQ2] The tool should support drawing a rectangle.
     * Command: rectangle n x y w h
     * Effect: Creates a new rectangle that has a name n, whose top-left corner is at
     * location (x, y), and whose width and height are w and h, respectively.
     */
    private void createRectangle(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ3] The tool should support drawing a line segment.
     * Command: line n x1 y1 x2 y2
     * Effect: Creates a new line segment that has a name n and whose two ends are at
     * locations (x1, y1) and (x2, y2), respectively.
     */
    private void createLine(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ4] The tool should support drawing a circle.
     * Command: circle n x y r
     * Effect: Creates a new circle that has a name n, whose center is at location (x, y),
     * and whose radius is r.
     */
    private void createCircle(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ5] The tool should support drawing a square.
     * Command: square n x y l
     * Effect: Creates a new square that has a name n, whose top-left corner is at
     * location (x, y), and whose side length is l.
     */
    private void createSquare(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ6] The tool should support grouping a non-empty list of shapes into one shape.
     * Command: group n n1 n2 ...
     * Effect: Creates a new shape named n by grouping existing shapes named n1, n2, ...
     */
    private void groupShapes(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ7] The tool should support ungrouping a shape that was created by grouping shapes.
     * Command: ungroup n
     * Effect: Ungroups shape n into its component shapes.
     */
    private void ungroupShapes(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ9] The tool should support calculating the minimum bounding box of a shape.
     * Command: boundingbox n
     * Effect: Calculates and outputs the minimum bounding box of the shape name n.
     */
    private void calculateBoundingBox(String[] tokens) {
        // Implementation here
    }

    /**
     * [REQ10] The tool should support moving a shape.
     * Command: move n dx dy
     * Effect: Moves the shape named n, horizontally by dx and vertically by dy.
     */
    private void moveShape(String[] tokens) {
        // Implementation here
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
        // Constructor implementation
    }

    /**
     * Returns the name of the rectangle.
     */
    public String getName() {
        // Implementation here
        return null;
    }

    /**
     * Returns information about the rectangle.
     */
    public String getInfo() {
        // Implementation here
        return null;
    }

    /**
     * Moves the rectangle by specified deltas.
     */
    public void move(double dx, double dy) {
        // Implementation here
    }

    /**
     * Returns the bounding box of the rectangle.
     */
    public String getBoundingBox() {
        // Implementation here
        return null;
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
