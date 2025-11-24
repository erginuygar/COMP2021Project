package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.*;

/**
 * Represents a group of shapes in the Clevis system.
 * <p>
 * A group behaves as a single shape composed of multiple member shapes.
 */
public final class Group implements Shape {

    private String name;
    private final List<Shape> members;

    /**
     * Original member names captured at grouping time (parallel to members list).
     * Used to restore original names when ungrouping (if possible).
     */
    private final List<String> originalNames;

    /**
     * Original indices (z-order positions) captured at grouping time, -1 if unknown.
     * Note: restoring exact z-order requires ShapeManager support for indexed insertion.
     */
    private final List<Integer> originalIndices;
    
    /**
     * Constructs a new Group containing a list of shapes.
     *
     * @param name    the group name (must be unique and non-empty)
     * @param members the shapes to include in the group (must be non-empty)
     */
    public Group(final String name, final List<Shape> members) {
        this(name, members,
                // capture names
                members == null ? List.of() : members.stream().map(Shape::getName).toList(),
                // default indices to -1
                members == null ? List.of() : members.stream().map(s -> -1).toList());
    }
    /**
     * Extended constructor that accepts metadata captured by the caller (grouping logic).
     */
    public Group(final String name,
                 final List<Shape> members,
                 final List<String> originalNames,
                 final List<Integer> originalIndices) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member.");
        }
        if (originalNames == null || originalNames.size() != members.size()) {
            throw new IllegalArgumentException("originalNames must be same length as members.");
        }
        if (originalIndices == null || originalIndices.size() != members.size()) {
            throw new IllegalArgumentException("originalIndices must be same length as members.");
        }

        this.name = name;
        this.members = new ArrayList<>(members);
        this.originalNames = new ArrayList<>(originalNames);
        this.originalIndices = new ArrayList<>(originalIndices);
    }
    
    /**
     * Returns an unmodifiable list of member shapes.
     *
     * @return list of shapes in this group
     */
    public List<Shape> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Return the original member names captured when the group was created.
     */
    public List<String> getOriginalNames() {
        return Collections.unmodifiableList(originalNames);
    }

    /**
     * Return the original member indices captured when the group was created.
     */
    public List<Integer> getOriginalIndices() {
        return Collections.unmodifiableList(originalIndices);
    }

    @Override
    public double getArea() {
        double totalArea = 0.0;
        for (Shape s : members) {
            totalArea += s.getArea();
        }
        return totalArea;
    }
    

    @Override
    public String getName() {
        return name;
    }

    /**
     * Groups are not intended to be renamed by ungroup; implement setName to explicitly
     * prevent renaming via the general Shape#setName contract.
     */
    @Override
    public void setName(final String newName) {
        throw new UnsupportedOperationException("Group renaming is not supported.");
    }
    
    @Override
    public String getInfo() {
        return String.format(Locale.ROOT, "Group[name:%s,members:%d]",
                name, members.size());
    }

    @Override
    public void move(final double dx, final double dy) {
        for (Shape s : members) {
            s.move(dx, dy);
        }
    }

    @Override
    public String getBoundingBox() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Shape s : members) {
            final String[] bb = s.getBoundingBox().split("\\s+");
            final double x = Double.parseDouble(bb[0]);
            final double y = Double.parseDouble(bb[1]);
            final double w = Double.parseDouble(bb[2]);
            final double h = Double.parseDouble(bb[3]);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + w);
            maxY = Math.max(maxY, y + h);
        }

        return String.format(Locale.ROOT, "%.2f %.2f %.2f %.2f",
                minX, minY, (maxX - minX), (maxY - minY));
    }

    @Override
    public boolean coversPoint(final double px, final double py) {
        for (Shape s : members) {
            if (s.coversPoint(px, py)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void changeName(String newName) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = newName;
    }

}
