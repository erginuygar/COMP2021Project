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
     * Adds a shape at a specific z-order index.
     * index is clamped into [0, size]. If index == size => append (topmost).
     *
     * @param index insertion index (bottom = 0)
     * @param shape shape to add
     * @throws ClevisException.DuplicateShapeException if a shape with the same name already exists
     */
    public void addShapeAt(final int index, final Shape shape) throws ClevisException.DuplicateShapeException {
        Objects.requireNonNull(shape, "shape cannot be null");
        if (shapesByName.containsKey(shape.getName())) {
            throw new ClevisException.DuplicateShapeException(
                    "The shape '" + shape.getName() + "' is already in the list.");
        }
        int idx = index;
        if (idx < 0) idx = 0;
        if (idx > shapesList.size()) idx = shapesList.size();
        shapesList.add(idx, shape);
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

        /**
     * Changes the name of a shape while maintaining its reference in the manager.
     *
     * @param oldName the current name of the shape
     * @param newName the new name for the shape
     * @throws ClevisException if oldName doesn't exist or newName already exists
     */
    public void changeShapeName(String oldName, String newName) throws ClevisException {
        if (!shapesByName.containsKey(oldName)) {
            throw new ClevisException("Shape not found: " + oldName);
        }
        if (shapesByName.containsKey(newName)) {
            throw new ClevisException("Shape name already exists: " + newName);
        }
        
        Shape shape = shapesByName.remove(oldName);
        shape.changeName(newName);
        shapesByName.put(newName, shape);
    }

}
