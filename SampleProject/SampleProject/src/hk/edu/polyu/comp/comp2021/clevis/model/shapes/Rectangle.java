package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import hk.edu.polyu.comp.comp2021.clevis.model.operations.ShapeQueryHandler;

import java.util.Locale;

/**
 * Represents a rectangle shape.
 * Implements REQ2: rectangle n x y w h
 */
public final class Rectangle implements Shape {
    private final String name;
    private final int z;
    public final double x, y, w, h;

    /**
     * Creates a rectangle.
     * @param name unique name for the rectangle
     * @param z z-index (drawing order)
     * @param x x-coordinate of top-left corner
     * @param y y-coordinate of top-left corner
     * @param w width (must be positive)
     * @param h height (must be positive)
     * @throws IllegalArgumentException if name is null/blank or dimensions are non-positive
     */
    public Rectangle(String name, int z, double x, double y, double w, double h) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required!");
        }
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("width and height must be positive!!");
        }
        this.name = name;
        this.z = z;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override public String name() { return name; }
    @Override public int z() { return z; }
    @Override public BoundingBox bbox() { return new BoundingBox(x, y, w, h); }

    @Override
    public String listInfo() {
        return String.format(Locale.US, "%s rectangle %.2f %.2f %.2f %.2f", name, x, y, w, h);
    }

    public double x() { return x; }
    public double y() { return y; }
    public double w() { return w; }
    public double h() { return h; }
    @Override
    public boolean intersects(Shape other) {
        if (other instanceof Rectangle || other instanceof Square) {
            Rectangle r = (Rectangle) other;

            boolean xOverlap = Math.max(this.x, r.x) < Math.min(this.x + this.w, r.x + r.w);
            boolean yOverlap = Math.max(this.y, r.y) < Math.min(this.y + this.h, r.y + r.h);

            return xOverlap && yOverlap;
        }

        if (other instanceof Circle) {
            Circle c = (Circle) other;
            return ShapeQueryHandler.circleIntersectsRectangle(
                    c.centerX, c.centerY, c.radius,
                    this.x, this.y, this.w, this.h
            );
        }

        if (other instanceof Line) {
            Line line = (Line) other;
            return ShapeQueryHandler.lineIntersectsRectangle(
                    line.x1(), line.y1(), line.x2(), line.y2(),
                    this.x, this.y, this.w, this.h
            );
        }

        if (other instanceof Group) {
            return other.intersects(this);
        }

        return false;
    }
}