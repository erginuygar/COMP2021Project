package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.*;

/**
 * Represents a group of shapes in the Clevis system.
 * <p>
 * A group behaves as a single shape composed of multiple member shapes.
 */
public final class Group implements Shape {

    private final String name;
    private final List<Shape> members;

    /**
     * Constructs a new Group containing a list of shapes.
     *
     * @param name    the group name (must be unique and non-empty)
     * @param members the shapes to include in the group (must be non-empty)
     */
    public Group(final String name, final List<Shape> members) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member.");
        }

        this.name = name;
        this.members = new ArrayList<>(members);
    }

    /**
     * Returns an unmodifiable list of member shapes.
     *
     * @return list of shapes in this group
     */
    public List<Shape> getMembers() {
        return Collections.unmodifiableList(members);
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
}
