package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.Locale;

/**
 * Represents a circle shape in the Clevis system.
 * <p>
 * Defined by its center (x, y) and radius (r).
 */
public final class Circle implements Shape {

    private final String name;
    private double x;
    private double y;
    private final double radius;

    /**
     * Constructs a Circle.
     *
     * @param name   shape name (must be unique and non-empty)
     * @param x      x-coordinate of the center
     * @param y      y-coordinate of the center
     * @param radius circle radius (must be positive)
     */
    public Circle(final String name, final double x, final double y, final double radius) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive.");
        }

        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return String.format("Circle(center=(%.2f,%.2f), radius=%.2f)", x, y, radius);
    }

    @Override
    public void move(final double dx, final double dy) {
        x += dx;
        y += dy;
    }

    @Override
    public String getBoundingBox() {
        final double topLeftX = x - radius;
        final double topLeftY = y - radius;
        final double diameter = 2 * radius;
        return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f",
                topLeftX, topLeftY, diameter, diameter);
    }

    @Override
    public boolean coversPoint(final double px, final double py) {
        final double dx = px - x;
        final double dy = py - y;
        return (dx * dx + dy * dy) <= radius * radius;
    }
}
