package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.*;

/**
 * Manages all shapes in the Clevis system.
 * <p>
 * Maintains insertion order (Z-order: later shapes on top) and provides
 * operations used by the controller.
 */
public final class ShapeManager {

    /** Stores shapes by name, preserving insertion order. */
    private final Map<String, Shape> shapesByName = new LinkedHashMap<>();

    /**
     * Adds a new shape to the manager.
     *
     * @param shape shape to add
     * @throws ClevisException.DuplicateShapeException if a shape with the same name already exists
     */
    public void addShape(final Shape shape) throws ClevisException.DuplicateShapeException {
        if (shapesByName.containsKey(shape.getName())) {
            throw new ClevisException.DuplicateShapeException(
                    "The shape '" + shape.getName() + "' is already in the list.");
        }
        shapesByName.put(shape.getName(), shape);
    }

    /**
     * Deletes a shape. If it is a group, deletes the group and its members.
     *
     * @param name name of the shape to delete
     * @throws ClevisException.ShapeNotFoundException if the shape does not exist
     */
    public void deleteShape(final String name) throws ClevisException.ShapeNotFoundException {
        final Shape shape = shapesByName.get(name);
        if (shape == null) {
            throw new ClevisException.ShapeNotFoundException("The shape '" + name + "' is not in the list.");
        }

        // If group, remove its members as well (REQ8)
        if (shape instanceof Group group) {
            for (Shape member : group.getMembers()) {
                shapesByName.remove(member.getName());
            }
        }

        shapesByName.remove(name);
    }

    /**
     * Retrieves a shape by name.
     *
     * @param name shape name
     * @return the Shape object or null if not found
     */
    public Shape getShape(final String name) {
        return shapesByName.get(name);
    }

    /**
     * Returns all shapes in bottom-to-top (increasing Z) order.
     *
     * @return list of all shapes
     */
    public List<Shape> getAllShapes() {
        return new ArrayList<>(shapesByName.values());
    }

    /**
     * Returns the bounding box string for a named shape.
     *
     * @param name shape name
     * @return bounding box string "x y w h"
     * @throws ClevisException.ShapeNotFoundException if the shape does not exist
     */
    public String getBoundingBox(final String name) throws ClevisException.ShapeNotFoundException {
        final Shape shape = getShape(name);
        if (shape == null) {
            throw new ClevisException.ShapeNotFoundException("Shape not found: " + name);
        }
        return shape.getBoundingBox();
    }
}
