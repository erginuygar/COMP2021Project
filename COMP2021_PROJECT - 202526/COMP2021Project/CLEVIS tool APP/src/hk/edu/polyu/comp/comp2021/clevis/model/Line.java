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
        final double tolerance = 0.05;
        
        // Calculate the distance from point to line segment
        double distance = distanceToLineSegment(px, py);
        
        return distance <= tolerance;
    }

    private double distanceToLineSegment(double px, double py) {
        // Quick bounding box check first
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);
        
        // If point is outside extended bounding box (including tolerance), it can't be on the line
        if (px < minX - 0.05 || px > maxX + 0.05 || py < minY - 0.05 || py > maxY + 0.05) {
            return Double.MAX_VALUE;
        }
        
        // Calculate the squared length of the line segment
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lineLengthSquared = dx * dx + dy * dy;
        
        // If line is essentially a point
        if (lineLengthSquared < 1e-10) {
            return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));
        }
        
        // Calculate the projection parameter t = [(P-A)·(B-A)] / |B-A|²
        double t = ((px - x1) * dx + (py - y1) * dy) / lineLengthSquared;
        
        if (t < 0) {
            // Closest to start point A
            return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));
        } else if (t > 1) {
            // Closest to end point B  
            return Math.sqrt(Math.pow(px - x2, 2) + Math.pow(py - y2, 2));
        } else {
            // Closest point is between A and B - calculate perpendicular distance
            double closestX = x1 + t * dx;
            double closestY = y1 + t * dy;
            return Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));
        }
}

    @Override
    public void changeName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = newName;
    }
}

