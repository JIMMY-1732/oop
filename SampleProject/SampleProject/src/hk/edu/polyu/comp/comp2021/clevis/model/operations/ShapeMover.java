package hk.edu.polyu.comp.comp2021.clevis.model.operations;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import java.util.*;

/**
 * Handles moving shapes and groups.
 * Implements REQ10 (move command).
 */
public class ShapeMover {
    private final Map<String, Shape> shapes;
    private final List<Shape> drawOrder;
    private final Map<String, Group> groups;
    
    public ShapeMover(Map<String, Shape> shapes, List<Shape> drawOrder, 
                     Map<String, Group> groups) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
        this.groups = groups;
    }
    
    /**
     * Moves a shape by the specified deltas (REQ10).
     * If the shape is a group, all its component shapes are moved.
     * @param shapeName name of the shape to move
     * @param dx horizontal displacement
     * @param dy vertical displacement
     * @throws IllegalArgumentException if shapeName is null/empty or shape doesn't exist
     */
    public void move(String shapeName, double dx, double dy) {
        if (shapeName == null || shapeName.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }
        
        if (dx == 0 && dy == 0) {
            return;
        }
        
        if (shapes.containsKey(shapeName)) {
            if (shapes.get(shapeName) == null) {
                throw new IllegalArgumentException("Shape not found: " + shapeName);
            }
            moveShape(shapes.get(shapeName), dx, dy, new HashSet<>());
        }
    }
    
    private void moveShape(Shape shape, double dx, double dy, Set<Shape> visited) {
        if (!visited.add(shape)) {
            return;
        }
        
        if (shape instanceof Group group) {
            for (Shape member : group.getShapes()) {
                moveShape(member, dx, dy, visited);
            }
            return;
        }
        
        if (shape instanceof Rectangle rect) {
            Rectangle moved = new Rectangle(rect.name(), rect.z(), 
                rect.x + dx, rect.y + dy, rect.w, rect.h);
            replaceShapeInCollections(rect, moved);
        } else if (shape instanceof Line line) {
            Line moved = new Line(line.name(), line.z(), 
                line.x1 + dx, line.y1 + dy, line.x2 + dx, line.y2 + dy);
            replaceShapeInCollections(line, moved);
        } else if (shape instanceof Circle circle) {
            Circle moved = new Circle(circle.name(), circle.z, 
                circle.centerX + dx, circle.centerY + dy, circle.radius);
            replaceShapeInCollections(circle, moved);
        } else if (shape instanceof Square square) {
            Square moved = new Square(square.name(), square.z, 
                square.x + dx, square.y + dy, square.length);
            replaceShapeInCollections(square, moved);
        }
    }
    
    private void replaceShapeInCollections(Shape oldShape, Shape newShape) {
        shapes.put(newShape.name(), newShape);
        
        int index = drawOrder.indexOf(oldShape);
        if (index >= 0) {
            drawOrder.set(index, newShape);
        }
        
        for (Group group : groups.values()) {
            List<Shape> members = group.getShapes();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) == oldShape) {
                    members.set(i, newShape);
                }
            }
        }
    }
}