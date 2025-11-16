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
    
    public ShapeManager(Map<String, Shape> shapes, List<Shape> drawOrder, 
                       Map<String, Group> groups) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
        this.groups = groups;
    }
    
    /**
     * Deletes a shape (REQ8).
     * If the shape is a group, all its members are also deleted.
     * @param name name of the shape to delete
     * @throws IllegalArgumentException if name is null/empty or shape doesn't exist
     */
    public void deleteShape(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }
        
        Shape s = shapes.get(name);
        if (s == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }
        
        if (s instanceof Group) {
            Group grp = (Group) s;
            List<Shape> membersCopy = new ArrayList<>(grp.getShapes());
            
            for (Shape member : membersCopy) {
                if (!member.name().equals(name)) {
                    deleteShape(member.name());
                }
            }
            groups.remove(name);
        }
        
        shapes.remove(name);
        drawOrder.remove(s);
    }
}