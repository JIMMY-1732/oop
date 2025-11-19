package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of shapes.
 * Implements REQ6 (group n n1 n2 ...) and supports REQ7 (ungroup n).
 */
public final class Group implements Shape {
    private final String name;
    private final int z;
    private final List<Shape> shapes;
    
    /**
     * Creates a group of shapes.
     * @param name unique name for the group
     * @param z z-index (drawing order)
     * @param shapes list of shapes to group (must be non-empty)
     * @throws IllegalArgumentException if name is null/blank or shapes list is empty
     */
    public Group(String name, int z, List<Shape> shapes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required!");
        }
        if (shapes == null || shapes.isEmpty()) {
            throw new IllegalArgumentException("group must contain at least one shape!");
        }
        this.name = name;
        this.z = z;
        this.shapes = new ArrayList<>(shapes); 
    }
    
    @Override public String name() { return name; }
    @Override public int z() { return z; }
    
    /**
     * Calculates the minimum bounding box that contains all shapes in the group.
     * Supports REQ9 for group shapes.
     */
    @Override 
    public BoundingBox bbox() {
        if (shapes.isEmpty()) {
            throw new IllegalStateException("Group is empty!");
        }
        
        BoundingBox first = shapes.get(0).bbox();
        double minX = first.x;
        double minY = first.y;
        double maxX = first.x + first.w;
        double maxY = first.y + first.h;
        
        for (int i = 1; i < shapes.size(); i++) {
            BoundingBox box = shapes.get(i).bbox();
            minX = Math.min(minX, box.x);
            minY = Math.min(minY, box.y);
            maxX = Math.max(maxX, box.x + box.w);
            maxY = Math.max(maxY, box.y + box.h);
        }
        
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }
    
    @Override 
    public String listInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" group");
        for (Shape s : shapes) {
            sb.append(" ").append(s.name());
        }
        return sb.toString();
    }
    
    /**
     * @return a copy of the shapes in this group
     */
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }
    @Override
    public boolean intersects(Shape other) {
        if (other instanceof Group) {
            Group otherGroup = (Group) other;

            // Check if any shape in this group intersects with any shape in the other group
            for (Shape thisShape : this.shapes) {
                for (Shape otherShape : otherGroup.shapes) {
                    if (thisShape.intersects(otherShape)) {
                        return true;
                    }
                }
            }
            return false;
        }

        // For non-group shapes, check if any member of this group intersects with it
        for (Shape shape : this.shapes) {
            if (shape.intersects(other)) {
                return true;
            }
        }

        return false;
    }
}
