package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.Locale;

/**
 * Represents a square shape in the Clevis system.
 * <p>
 * Defined by its top-left corner (x, y) and side length (len).
 */
public final class Square implements Shape {

    private final String name;
    private double x;
    private double y;
    private final double length;

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
        return px >= x && px <= x + length && py >= y && py <= y + length;
    }
}
