package hk.edu.polyu.comp.comp2021.clevis.model.operations;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import hk.edu.polyu.comp.comp2021.clevis.model.util.ShapeDistanceCalculator;
import java.util.*;

/**
 * Handles queries about shapes.
 * Implements REQ9 (boundingbox), REQ11 (shapeAt), and REQ12 (intersect).
 */
public class ShapeQueryHandler {
    private final Map<String, Shape> shapes;
    private final List<Shape> drawOrder;
    
    public ShapeQueryHandler(Map<String, Shape> shapes, List<Shape> drawOrder) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
    }
    
    /**
     * Calculates the bounding box of a shape (REQ9).
     * @param name name of the shape
     * @return the bounding box
     * @throws IllegalArgumentException if name is null/empty or shape doesn't exist
     */
    public BoundingBox boundingBox(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }
        
        Shape shape = shapes.get(name);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }
        
        return shape.bbox();
    }
    
    /**
     * Finds the topmost shape that covers a point (REQ11).
     * A shape covers a point if the minimum distance from the point to the shape's 
     * outline is less than 0.05.
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return name of the topmost shape covering the point, or null if none
     */
    public String shapeAt(double x, double y) {
        for (int i = drawOrder.size() - 1; i >= 0; i--) {
            Shape shape = drawOrder.get(i);
            if (covers(shape, x, y)) {
                return shape.name();
            }
        }
        return null;
    }
    
    private boolean covers(Shape shape, double x, double y) {
        if (shape instanceof Group) {
            Group group = (Group) shape;
            for (Shape member : group.getShapes()) {
                if (covers(member, x, y)) {
                    return true;
                }
            }
            return false;
        }
        
        double distance = ShapeDistanceCalculator.distanceToShape(shape, x, y);
        return distance < 0.05;
    }
    
    /**
     * Checks if two shapes intersect (REQ12).
     * Two shapes intersect if their bounding boxes share any internal points.
     * @param name1 name of first shape
     * @param name2 name of second shape
     * @return true if the shapes intersect
     * @throws IllegalArgumentException if either name is null/empty or shape doesn't exist
     */
    public boolean intersect(String name1, String name2) {
        if (name1 == null || name1.isBlank() || name2 == null || name2.isBlank()) {
            throw new IllegalArgumentException("Shape names cannot be null or empty");
        }
        
        Shape shape1 = shapes.get(name1);
        Shape shape2 = shapes.get(name2);
        
        if (shape1 == null) {
            throw new IllegalArgumentException("Shape not found: " + name1);
        }
        if (shape2 == null) {
            throw new IllegalArgumentException("Shape not found: " + name2);
        }
        
        return doBoundingBoxesIntersect(shape1.bbox(), shape2.bbox());
    }
    
    private boolean doBoundingBoxesIntersect(BoundingBox box1, BoundingBox box2) {
        if (box1.x + box1.w <= box2.x || box2.x + box2.w <= box1.x) {
            return false;
        }
        if (box1.y + box1.h <= box2.y || box2.y + box2.h <= box1.y) {
            return false;
        }
        return true;
    }
}