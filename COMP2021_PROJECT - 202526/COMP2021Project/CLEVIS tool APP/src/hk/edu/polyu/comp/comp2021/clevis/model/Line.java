package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.Locale;

/**
 * Represents a line segment shape in the Clevis system.
 * <p>
 * Defined by two endpoints (x1, y1) and (x2, y2).
 */
public final class Line implements Shape {

    private String name;
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    /**
     * Constructs a Line.
     *
     * @param name shape name (must be unique and non-empty)
     * @param x1   x-coordinate of the first endpoint
     * @param y1   y-coordinate of the first endpoint
     * @param x2   x-coordinate of the second endpoint
     * @param y2   y-coordinate of the second endpoint
     */
    public Line(final String name, final double x1, final double y1,
                final double x2, final double y2) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (x1 == x2 && y1 == y2) {
            throw new IllegalArgumentException("Line endpoints must differ.");
        }

        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public double getArea() {
        return 0.0;
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
                "Line[name:%s,(x1,y1):(%.2f,%.2f),(x2,y2):(%.2f,%.2f)]",
                name, x1, y1, x2, y2);
    }

    @Override
    public void move(final double dx, final double dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }

    @Override
    public String getBoundingBox() {
        final double minX = Math.min(x1, x2);
        final double minY = Math.min(y1, y2);
        final double width = Math.abs(x2 - x1);
        final double height = Math.abs(y2 - y1);
        return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f",
                minX, minY, width, height);
    }
    @Override
    public boolean coversPoint(final double px, final double py) {
        // Calculate the distance from point to line segment
        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        
        // If line has zero length, check if point equals the single point
        if (lineLength == 0) {
            return Math.abs(px - x1) < 1e-6 && Math.abs(py - y1) < 1e-6;
        }
        
        // Calculate where the point projects onto the line
        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / (lineLength * lineLength);
        
        // If projection is outside the segment, the point is not on the segment
        if (t < 0 || t > 1) {
            return false;
        }
        
        // Find the closest point on the line segment
        double closestX = x1 + t * (x2 - x1);
        double closestY = y1 + t * (y2 - y1);
        
        // Calculate distance from point to closest point on line
        double distance = Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));
        
        // Return true if distance is within tolerance (e.g., 2 pixels for click detection)
        return distance <= 0.05;
    }

    @Override
    public void changeName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = newName;
    }
}

