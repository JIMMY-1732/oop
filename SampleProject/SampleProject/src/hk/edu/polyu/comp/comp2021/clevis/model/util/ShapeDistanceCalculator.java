package hk.edu.polyu.comp.comp2021.clevis.model.util;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;

/**
 * Utility class for calculating distances from points to shapes.
 * Supports REQ11 by determining if a point is within 0.05 units of a shape's outline.
 */
public class ShapeDistanceCalculator {
    
    /**
     * Calculates the minimum distance from a point to a shape's outline.
     * @param shape the shape
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return minimum distance to the shape's outline
     */
    public static double distanceToShape(Shape shape, double x, double y) {
        if (shape instanceof Line) {
            return distanceToLine((Line) shape, x, y);
        } else if (shape instanceof Rectangle rectangle) {
            return distanceToRectangle(rectangle, x, y);
        } else if (shape instanceof Circle circle) {
            return distanceToCircle(circle, x, y);
        } else if (shape instanceof Square square) {
            return distanceToSquare(square, x, y);
        }
        throw new IllegalArgumentException("Unsupported shape type");
    }
    
    private static double distanceToLine(Line line, double x, double y) {
        double x1 = line.x1, y1 = line.y1, x2 = line.x2, y2 = line.y2;
        
        double lineLength = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (lineLength == 0) {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        }
        
        double t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / lineLength;
        t = Math.max(0, Math.min(1, t));
        
        double px = x1 + t * (x2 - x1);
        double py = y1 + t * (y2 - y1);
        
        return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
    }
    
    private static double distanceToRectangle(Rectangle rect, double x, double y) {
        double rx = rect.x, ry = rect.y, rw = rect.w, rh = rect.h;
        
        if (x >= rx && x <= rx + rw && y >= ry && y <= ry + rh) {
            return Math.min(
                Math.min(x - rx, rx + rw - x),
                Math.min(y - ry, ry + rh - y)
            );
        }
        
        double dx = Math.max(rx - x, Math.max(0, x - (rx + rw)));
        double dy = Math.max(ry - y, Math.max(0, y - (ry + rh)));
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private static double distanceToCircle(Circle circle, double x, double y) {
        double dx = x - circle.centerX;
        double dy = y - circle.centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return Math.abs(distance - circle.radius);
    }
    
    private static double distanceToSquare(Square square, double x, double y) {
        return distanceToRectangle(
            new Rectangle(square.name(), square.z(), square.x, square.y, 
                          square.length, square.length),
            x, y
        );
    }
}