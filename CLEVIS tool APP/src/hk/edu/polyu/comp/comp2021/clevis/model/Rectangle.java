package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.Locale;

/**
 * Represents a rectangle shape in the Clevis system.
 * <p>
 * Defined by its top-left corner (x, y) and dimensions (width, height).
 */
public final class Rectangle implements Shape {

    private final String name;
    private double x;
    private double y;
    private final double width;
    private final double height;

    /**
     * Constructs a Rectangle.
     *
     * @param name   shape name (must be unique and non-empty)
     * @param x      x-coordinate of top-left corner
     * @param y      y-coordinate of top-left corner
     * @param width  rectangle width (must be positive)
     * @param height rectangle height (must be positive)
     */
    public Rectangle(final String name, final double x, final double y,
                     final double width, final double height) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive.");
        }

        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public double getArea() {
        return width * height;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return String.format("Rectangle(top-left=(%.2f,%.2f), width=%.2f, height=%.2f)", x, y, width, height);
    }

    @Override
    public void move(final double dx, final double dy) {
        x += dx;
        y += dy;
    }

    @Override
    public String getBoundingBox() {
        return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f", x, y, width, height);
    }

    @Override
    public boolean coversPoint(final double px, final double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
