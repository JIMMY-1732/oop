package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import hk.edu.polyu.comp.comp2021.clevis.model.operations.ShapeQueryHandler;

import java.util.Locale;

/**
 * Represents a square.
 * Implements REQ5: square n x y l
 */
public final class Square implements Shape {
    private final String name;
    public int z;
    public double x, y, length;

    /**
     * Creates a square.
     * @param name unique name for the square
     * @param z z-index (drawing order)
     * @param x x-coordinate of top-left corner
     * @param y y-coordinate of top-left corner
     * @param length side length (must be positive)
     * @throws IllegalArgumentException if name is null/blank or side length is non-positive
     */
    public Square(String name, int z, double x, double y, double length) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required!");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("side length must be positive!!");
        }
        this.name = name;
        this.z = z;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    @Override public String name() { return name; }
    @Override public int z() { return z; }

    @Override
    public BoundingBox bbox() {
        return new BoundingBox(x, y, length, length);
    }

    @Override
    public String listInfo() {
        return String.format(Locale.US, "%s square %.2f %.2f %.2f", name, x, y, length);
    }

    public double x() { return x; }
    public double y() { return y; }
    public double s() { return length; }
    @Override
    public boolean intersects(Shape other) {
        if (other instanceof Square) {
            Square s = (Square) other;

            boolean xOverlap = Math.max(this.x, s.x) <= Math.min(this.x + this.length, s.x + s.length);
            boolean yOverlap = Math.max(this.y, s.y) <= Math.min(this.y + this.length, s.y + s.length);

            return xOverlap && yOverlap;
        }

        if (other instanceof Rectangle) {
            Rectangle r = (Rectangle) other;

            boolean xOverlap = Math.max(this.x, r.x()) <= Math.min(this.x + this.length, r.x() + r.w());
            boolean yOverlap = Math.max(this.y, r.y()) <= Math.min(this.y + this.length, r.y() + r.h());

            return xOverlap && yOverlap;
        }

        if (other instanceof Circle) {
            Circle c = (Circle) other;
            return ShapeQueryHandler.circleIntersectsRectangle(
                    c.centerX, c.centerY, c.radius,
                    this.x, this.y, this.length, this.length
            );
        }

        if (other instanceof Line) {
            Line line = (Line) other;
            return ShapeQueryHandler.lineIntersectsRectangle(
                    line.x1(), line.y1(), line.x2(), line.y2(),
                    this.x, this.y, this.length, this.length
            );
        }

        if (other instanceof Group) {
            return other.intersects(this);
        }

        return false;
    }
}