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
            double distance = Math.sqrt(
                    Math.pow(this.centerX - c.centerX, 2) +
                            Math.pow(this.centerY - c.centerY, 2)
            );

            // Two circles intersect if they overlap but neither contains the other
            double sumRadii = this.radius + c.radius;
            double diffRadii = Math.abs(this.radius - c.radius);

            return distance < sumRadii && distance > diffRadii;
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
