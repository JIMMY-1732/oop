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
     * Returns the shape with the HIGHEST z-index that covers the point.
     * A shape covers a point if the minimum distance from the point to the shape's
     * outline is less than 0.05.
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return name of the topmost shape covering the point, or null if none
     */
    public String shapeAt(double x, double y) {
        Shape topmost = null;
        int highestZ = Integer.MIN_VALUE;

        // Check all shapes in drawOrder and find the one with highest z-index
        for (Shape shape : drawOrder) {
            if (covers(shape, x, y)) {
                int z = shape.z();
                if (z > highestZ) {
                    highestZ = z;
                    topmost = shape;
                }
            }
        }

        return topmost != null ? topmost.name() : null;
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
     * Two shapes intersect if their bounding boxes share any INTERNAL points.
     * This means the bounding boxes must overlap with a non-zero area,
     * not just touch at edges or corners.
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

    /**
     * Check if two bounding boxes share INTERNAL points.
     * They must have overlapping area, not just touching edges.
     *
     * Using STRICT inequality (<) means:
     * - If one box's right edge equals another's left edge, they DON'T intersect
     * - If one box is completely inside another, they DO intersect
     * - If boxes overlap with non-zero area, they DO intersect
     */
    private boolean doBoundingBoxesIntersect(BoundingBox box1, BoundingBox box2) {
        // Check if separated horizontally (with strict inequality)
        // box1 is completely to the left of box2
        if (box1.x + box1.w <= box2.x) {
            return false;
        }
        // box2 is completely to the left of box1
        if (box2.x + box2.w <= box1.x) {
            return false;
        }

        // Check if separated vertically (with strict inequality)
        // box1 is completely above box2
        if (box1.y + box1.h <= box2.y) {
            return false;
        }
        // box2 is completely above box1
        if (box2.y + box2.h <= box1.y) {
            return false;
        }

        // If not separated in any direction, they must share internal points
        return true;
    }
}