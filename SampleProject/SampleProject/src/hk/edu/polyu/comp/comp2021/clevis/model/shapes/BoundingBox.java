package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import java.util.Locale;

/**
 * Immutable value object representing a minimum bounding box.
 * Used in REQ9 (boundingbox command) and REQ12 (intersection detection).
 */
public final class BoundingBox {
    public final double x, y, w, h;
    
    /**
     * Creates a bounding box.
     * @param x x-coordinate of top-left corner
     * @param y y-coordinate of top-left corner
     * @param w width (must be non-negative)
     * @param h height (must be non-negative)
     * @throws IllegalArgumentException if width or height is negative
     */
    public BoundingBox(double x, double y, double w, double h) {
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        this.x = x; 
        this.y = y; 
        this.w = w; 
        this.h = h;
    }
    
    @Override 
    public String toString() {
        return String.format(Locale.US, "%.2f %.2f %.2f %.2f", x, y, w, h);
    }
}