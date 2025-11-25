package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.Locale;

/**
 * Represents a square shape in the Clevis system.
 * <p>
 * Defined by its top-left corner (x, y) and side length (len).
 */
public final class Square implements Shape {

    private String name;
    private double x;
    private double y;
    private double length;

    /**
     * Constructs a Square.
     *
     * @param name   shape name (must be unique and non-empty)
     * @param x      x-coordinate of the top-left corner
     * @param y      y-coordinate of the top-left corner
     * @param length side length (must be positive)
     */
    public Square(final String name, final double x, final double y, final double length) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }

        this.name = name;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    @Override
    public double getArea() {
        return length * length;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = newName;
    }

    @Override
    public String getInfo() {
        return String.format(Locale.ROOT,
                "Square[name:%s,(x,y):(%.2f,%.2f),len:%.2f]",
                name, x, y, length);
    }

    @Override
    public void move(final double dx, final double dy) {
        x += dx;
        y += dy;
    }

    @Override
    public String getBoundingBox() {
        return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f",
                x, y, length, length);
    }

    @Override
    public boolean coversPoint(final double px, final double py) {
        final double tolerance = 0.05;
        
        // Calculate minimum distance to the square's outline
        double minDistance = getDistanceToSquareOutline(px, py);
        
        return minDistance <= tolerance;
    }

    private double getDistanceToSquareOutline(double px, double py) {
        // Check if point is inside the square (excluding interior)
        boolean insideX = px >= this.x && px <= this.x + this.length;
        boolean insideY = py >= this.y && py <= this.y + this.length;
        
        if (insideX && insideY) {
            // Point is inside - find distance to nearest edge
            double distToLeft = px - this.x;
            double distToRight = (this.x + this.length) - px;
            double distToTop = py - this.y;
            double distToBottom = (this.y + this.length) - py;
            
            return Math.min(Math.min(distToLeft, distToRight), Math.min(distToTop, distToBottom));
        }
        
        // Point is outside - find distance to nearest point on outline
        double closestX = Math.max(this.x, Math.min(px, this.x + this.length));
        double closestY = Math.max(this.y, Math.min(py, this.y + this.length));
        
        return Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));
    }
    @Override
    public void changeName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = newName;
    }
}

