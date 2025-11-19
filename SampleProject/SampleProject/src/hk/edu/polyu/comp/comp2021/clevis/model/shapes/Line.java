package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import hk.edu.polyu.comp.comp2021.clevis.model.operations.ShapeQueryHandler;

import java.util.Locale;

/**
 * Represents a line segment.
 * Implements REQ3: line n x1 y1 x2 y2
 */
public final class Line implements Shape {
    private final String name;
    public int z;
    public double x1, y1, x2, y2;
    
    /**
     * Creates a line segment.
     * @param name unique name for the line
     * @param z z-index (drawing order)
     * @param x1 x-coordinate of first endpoint
     * @param y1 y-coordinate of first endpoint
     * @param x2 x-coordinate of second endpoint
     * @param y2 y-coordinate of second endpoint
     * @throws IllegalArgumentException if name is null/blank or endpoints are identical
     */
    public Line(String name, int z, double x1, double y1, double x2, double y2) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required!");
        }
        if (x1 == x2 && y1 == y2) {
            throw new IllegalArgumentException("a line needs two distinct points!!");
        }
        this.name = name; 
        this.z = z;
        this.x1 = x1; 
        this.y1 = y1; 
        this.x2 = x2; 
        this.y2 = y2;
    }
    
    @Override public String name() { return name; }
    @Override public int z() { return z; }
    
    @Override 
    public BoundingBox bbox() {
        double minX = Math.min(x1, x2), minY = Math.min(y1, y2);
        double maxX = Math.max(x1, x2), maxY = Math.max(y1, y2);
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }
    
    @Override 
    public String listInfo() {
        return String.format(Locale.US, "%s line %.2f %.2f %.2f %.2f", name, x1, y1, x2, y2);
    }
    
    public double x1() { return x1; }
    public double y1() { return y1; }
    public double x2() { return x2; }
    public double y2() { return y2; }
    @Override
    public boolean intersects(Shape other) {
        if (other instanceof Line) {
            Line line = (Line) other;
            return ShapeQueryHandler.lineSegmentsIntersect(
                    this.x1, this.y1, this.x2, this.y2,
                    line.x1, line.y1, line.x2, line.y2
            );
        }

        if (other instanceof Circle) {
            Circle c = (Circle) other;
            return ShapeQueryHandler.lineIntersectsCircle(
                    this.x1, this.y1, this.x2, this.y2,
                    c.centerX, c.centerY, c.radius
            );
        }

        if (other instanceof Rectangle || other instanceof Square) {
            Rectangle r = (Rectangle) other;
            return ShapeQueryHandler.lineIntersectsRectangle(
                    this.x1, this.y1, this.x2, this.y2,
                    r.x(), r.y(), r.w(), r.h()
            );
        }

        if (other instanceof Group) {
            return other.intersects(this);
        }

        return false;
    }
}