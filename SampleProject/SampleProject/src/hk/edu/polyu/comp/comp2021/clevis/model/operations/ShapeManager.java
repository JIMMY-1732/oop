package hk.edu.polyu.comp.comp2021.clevis.model.operations;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import java.util.*;

/**
 * Manages shape lifecycle operations.
 * Implements REQ8 (delete command).
 */
public class ShapeManager {
    private final Map<String, Shape> shapes;
    private final List<Shape> drawOrder;
    private final Map<String, Group> groups;
    private final GroupManager groupManager;
    
    public ShapeManager(Map<String, Shape> shapes, List<Shape> drawOrder,
                        Map<String, Group> groups, GroupManager groupManager) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
        this.groups = groups;
        this.groupManager = groupManager;
    }
    
    /**
     * Deletes a shape (REQ8).
     * If the shape is a group, all its members are also deleted.
     * @param name name of the shape to delete
     * @throws IllegalArgumentException if name is null/empty or shape doesn't exist
     */
    public void deleteShape(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }

//        // NEW: Check if shape is hidden in a group
//        if (groupManager.isHidden(name)) {
//            throw new IllegalArgumentException("Cannot delete shape that is part of a group: " + name);
//        }

        Shape shape = shapes.get(name);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }

        shapes.remove(name);
        drawOrder.remove(shape);

        if (shape instanceof Group) {
            groups.remove(name);
            Shape[] removeItem = shapes.values().toArray(new Shape[0]);
            for(Shape item : removeItem) {
                deleteShape(item.name());
            }
        }
    }
}