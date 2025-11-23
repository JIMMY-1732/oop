package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import hk.edu.polyu.comp.comp2021.clevis.model.operations.ShapeQueryHandler;

import java.util.Locale;

/**
 * Represents a circle.
 * Implements REQ4: circle n x y r
 */
public final class Circle implements Shape {
    private final String name;
    public int z;
    public double centerX, centerY, radius;
    
    /**
     * Creates a circle.
     * @param name unique name for the circle
     * @param z z-index (drawing order)
     * @param centerX x-coordinate of center
     * @param centerY y-coordinate of center
     * @param radius radius (must be positive)
     * @throws IllegalArgumentException if name is null/blank or radius is non-positive
     */
    public Circle(String name, int z, double centerX, double centerY, double radius) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required!");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive!!");
        }
        this.name = name; 
        this.z = z;
        this.centerX = centerX; 
        this.centerY = centerY; 
        this.radius = radius;
    }
    
    @Override public String name() { return name; }
    @Override public int z() { return z; }
    
    @Override 
    public BoundingBox bbox() {
        double x = centerX - radius;
        double y = centerY - radius;
        double width = 2 * radius;
        double height = 2 * radius;
        return new BoundingBox(x, y, width, height);
    }
    
    @Override 
    public String listInfo() {
        return String.format(Locale.US, "%s circle %.2f %.2f %.2f", name, centerX, centerY, radius);
    }
    public double cx() { return centerX; }
    public double cy() { return centerY; }
    public double r()  { return radius; }
    @Override
    public boolean intersects(Shape other) {
        if (other instanceof Circle) {
            Circle c = (Circle) other;
            double dx = this.centerX - c.centerX;
            double dy = this.centerY - c.centerY;
            double distance = Math.hypot(dx, dy);

            double sumRadii = this.radius + c.radius;

            // True whenever there is at least one interior point in common.
            return distance < sumRadii
                    && distance + Math.min(this.radius, c.radius) >= Math.max(this.radius, c.radius);

        }

        if (other instanceof Rectangle || other instanceof Square) {
            Rectangle r = (Rectangle) other;
            return ShapeQueryHandler.circleIntersectsRectangle(
                    this.centerX, this.centerY, this.radius,
                    r.x(), r.y(), r.w(), r.h()
            );
        }

        if (other instanceof Line) {
            Line line = (Line) other;
            return ShapeQueryHandler.lineIntersectsCircle(
                    line.x1(), line.y1(), line.x2(), line.y2(),
                    this.centerX, this.centerY, this.radius
            );
        }

        if (other instanceof Group) {
            return other.intersects(this);
        }

        return false;
    }
}
