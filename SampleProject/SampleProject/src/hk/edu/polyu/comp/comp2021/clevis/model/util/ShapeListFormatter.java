package hk.edu.polyu.comp.comp2021.clevis.model.util;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import java.util.*;

/**
 * Formats shape information for display.
 * Implements REQ13 (list command) and REQ14 (listAll command).
 */
public class ShapeListFormatter {
    private final Map<String, Shape> shapes;
    private final Map<String, Group> groups;
    
    public ShapeListFormatter(Map<String, Shape> shapes, Map<String, Group> groups) {
        this.shapes = shapes;
        this.groups = groups;
    }
    
    /**
     * Lists basic information about a single shape (REQ13).
     * @param name name of the shape
     * @return formatted string with shape information
     * @throws IllegalArgumentException if name is null/empty or shape doesn't exist
     */
    public String list(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }
        
        Shape shape = shapes.get(name);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }
        
        return shape.listInfo();
    }
    
    /**
     * Lists all shapes in decreasing Z-order with indentation for groups (REQ14).
     * @return formatted string listing all shapes
     */
    public String listAll() {
        List<Shape> roots = new ArrayList<>();
        Set<Shape> nestedShapes = new HashSet<>();
        
        for (Group group : groups.values()) {
            collectNestedShapes(group, nestedShapes);
        }
        
        for (Shape shape : shapes.values()) {
            if (!nestedShapes.contains(shape)) {
                roots.add(shape);
            }
        }
        
        roots.sort(Comparator.comparingInt(Shape::z).reversed());
        
        StringBuilder sb = new StringBuilder();
        Set<Shape> visited = new HashSet<>();
        
        for (Shape root : roots) {
            appendShapeInfo(root, 0, sb, visited);
        }
        
        return sb.toString();
    }
    
    private void collectNestedShapes(Group group, Set<Shape> nested) {
        for (Shape member : group.getShapes()) {
            if (nested.add(member) && member instanceof Group) {
                collectNestedShapes((Group) member, nested);
            }
        }
    }
    
    private void appendShapeInfo(Shape shape, int depth, StringBuilder sb, Set<Shape> visited) {
        if (!visited.add(shape)) {
            return;
        }
        
        if (sb.length() > 0) {
            sb.append(System.lineSeparator());
        }
        
        String indent = depth <= 0 ? "" : "  ".repeat(depth);
        sb.append(indent).append(shape.listInfo());
        
        if (shape instanceof Group) {
            List<Shape> members = new ArrayList<>(((Group) shape).getShapes());
            members.sort(Comparator.comparingInt(Shape::z).reversed());
            for (Shape member : members) {
                appendShapeInfo(member, depth + 1, sb, visited);
            }
        }
    }
}
