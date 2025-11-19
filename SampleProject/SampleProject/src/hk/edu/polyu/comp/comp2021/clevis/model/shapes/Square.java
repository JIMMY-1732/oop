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

            // Two squares intersect if they overlap but neither contains the other
            boolean overlap = !(this.x + this.length <= s.x ||
                    s.x + s.length <= this.x ||
                    this.y + this.length <= s.y ||
                    s.y + s.length <= this.y);

            if (!overlap) return false;

            // Check if one completely contains the other
            boolean thisContainsOther = (this.x <= s.x &&
                    this.x + this.length >= s.x + s.length &&
                    this.y <= s.y &&
                    this.y + this.length >= s.y + s.length);

            boolean otherContainsThis = (s.x <= this.x &&
                    s.x + s.length >= this.x + this.length &&
                    s.y <= this.y &&
                    s.y + s.length >= this.y + this.length);

            return overlap && !thisContainsOther && !otherContainsThis;
        }

        if (other instanceof Rectangle) {
            Rectangle r = (Rectangle) other;

            // Square vs Rectangle - treat square as rectangle
            boolean overlap = !(this.x + this.length <= r.x() ||
                    r.x() + r.w() <= this.x ||
                    this.y + this.length <= r.y() ||
                    r.y() + r.h() <= this.y);

            if (!overlap) return false;

            // Check if one completely contains the other
            boolean thisContainsOther = (this.x <= r.x() &&
                    this.x + this.length >= r.x() + r.w() &&
                    this.y <= r.y() &&
                    this.y + this.length >= r.y() + r.h());

            boolean otherContainsThis = (r.x() <= this.x &&
                    r.x() + r.w() >= this.x + this.length &&
                    r.y() <= this.y &&
                    r.y() + r.h() >= this.y + this.length);

            return overlap && !thisContainsOther && !otherContainsThis;
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