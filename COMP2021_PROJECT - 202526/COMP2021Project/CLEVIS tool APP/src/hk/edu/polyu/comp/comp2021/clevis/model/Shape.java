package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Abstract base class for all shapes in Clevis.
 * Defines common behavior and interface for geometric shapes.
 */
public interface Shape {

    /**
     * Returns the area of this shape.
     *
     * @return area
     */
    double getArea();

    /**
     * Returns the name of this shape.
     *
     * @return shape name
     */
    String getName();

    /**
     * Returns descriptive information about this shape.
     *
     * @return info string
     */
    String getInfo();

    /**
     * Moves this shape by the specified offset.
     *
     * @param dx horizontal movement
     * @param dy vertical movement
     */
    void move(double dx, double dy);

    /**
     * Returns the bounding box of this shape as a formatted string "x y w h".
     *
     * @return bounding box string
     */
    String getBoundingBox();

    /**
     * Determines whether the shape covers a given point (x, y).
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the point is covered, false otherwise
     */
    boolean coversPoint(double x, double y);
}
