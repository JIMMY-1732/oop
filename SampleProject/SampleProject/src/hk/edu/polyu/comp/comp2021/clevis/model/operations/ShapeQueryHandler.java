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
        int highestZ = 0;

        // Check all shapes in drawOrder and find the one with highest z-index
        for (Shape shape : drawOrder) {
//            System.out.println(shape);
//            System.out.println(covers(shape, x, y));
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
        System.out.println("distance " + distance);
        return distance < 0.05;
    }

    /**
     * Checks if two shapes intersect (REQ12).
     * Two shapes intersect if they share any points or cross each other.
     * Uses actual geometric intersection, not just bounding box overlap.
     *
     * @param name1 name of first shape
     * @param name2 name of second shape
     * @return true if the shapes intersect
     * @throws IllegalArgumentException if either name is null/empty or shape doesn't exist
     */
    public boolean intersect(String name1, String name2) {
        if (name1 == null || name1.isBlank() || name2 == null || name2.isBlank()) {
            throw new IllegalArgumentException();
        }

        Shape shape1 = shapes.get(name1);
        Shape shape2 = shapes.get(name2);

        if (shape1 == null) {
            throw new IllegalArgumentException("Shape not found: " + name1);
        }
        if (shape2 == null) {
            throw new IllegalArgumentException("Shape not found: " + name2);
        }
        // Use the shape's own intersects method for proper geometric intersection
        return shape1.intersects(shape2);
    }

    // ============================================================================
    // GEOMETRIC INTERSECTION HELPERS
    // These are public static so shapes can use them
    // ============================================================================

    /**
     * Check if a line segment intersects with a circle.
     */
    public static boolean lineIntersectsCircle(double x1, double y1, double x2, double y2,
                                               double cx, double cy, double radius) {
        // Find the closest point on the line segment to the circle center
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            // Line segment is a point
            double dist = Math.sqrt((x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy));
            return dist <= radius;
        }

        // Parameter t represents position along line segment (0 to 1)
        double t = ((cx - x1) * dx + (cy - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t)); // Clamp to [0, 1]

        // Find closest point on segment
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        // Check distance from circle center to closest point
        double distance = Math.sqrt(
                (closestX - cx) * (closestX - cx) +
                        (closestY - cy) * (closestY - cy)
        );

        return distance <= radius;
    }

    /**
     * Check if a line segment intersects with a rectangle.
     */
    public static boolean lineIntersectsRectangle(double x1, double y1, double x2, double y2,
                                                  double rx, double ry, double rw, double rh) {
        // Check if either endpoint is inside the rectangle
        if (pointInRectangle(x1, y1, rx, ry, rw, rh) ||
                pointInRectangle(x2, y2, rx, ry, rw, rh)) {
            return true;
        }

        // Check if line intersects any of the four edges of the rectangle
        // Top edge
        if (lineSegmentsIntersect(x1, y1, x2, y2, rx, ry, rx + rw, ry)) return true;
        // Bottom edge
        if (lineSegmentsIntersect(x1, y1, x2, y2, rx, ry + rh, rx + rw, ry + rh)) return true;
        // Left edge
        if (lineSegmentsIntersect(x1, y1, x2, y2, rx, ry, rx, ry + rh)) return true;
        // Right edge
        if (lineSegmentsIntersect(x1, y1, x2, y2, rx + rw, ry, rx + rw, ry + rh)) return true;

        return false;
    }

    /**
     * Check if a circle intersects with a rectangle.
     */
    public static boolean circleIntersectsRectangle(double cx, double cy, double radius,
                                                    double rx, double ry, double rw, double rh) {
        double closestX = Math.max(rx, Math.min(cx, rx + rw));
        double closestY = Math.max(ry, Math.min(cy, ry + rh));

        double dx = cx - closestX;
        double dy = cy - closestY;
        double distanceSquared = dx * dx + dy * dy;

        if (distanceSquared <= radius * radius) {
            return true; // circle reaches the rectangle
        }

        // Circle center inside rectangle? They intersect (circle crosses rectangle)
        if (pointInRectangle(cx, cy, rx, ry, rw, rh)) {
            return true;
        }

        // Circle might contain rectangle: check corners
        double r2 = radius * radius;
        boolean allCornersInside =
                distSquared(cx, cy, rx, ry) <= r2 &&
                        distSquared(cx, cy, rx + rw, ry) <= r2 &&
                        distSquared(cx, cy, rx, ry + rh) <= r2 &&
                        distSquared(cx, cy, rx + rw, ry + rh) <= r2;

        return allCornersInside;
    }

    /**
     * Check if two line segments intersect.
     */
    public static boolean lineSegmentsIntersect(double x1, double y1, double x2, double y2,
                                                double x3, double y3, double x4, double y4) {
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        if (Math.abs(d) < 1e-10) {
            // Lines are parallel or coincident
            return false;
        }

        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / d;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / d;

        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    /**
     * Check if a point is inside a rectangle.
     */
    public static boolean pointInRectangle(double px, double py, double rx, double ry,
                                           double rw, double rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    /**
     * Calculate squared distance between two points.
     */
    private static double distSquared(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}