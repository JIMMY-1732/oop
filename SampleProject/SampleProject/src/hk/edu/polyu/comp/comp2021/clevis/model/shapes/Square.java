package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

import java.util.Locale;

/**
 * Represents a square.
 * Implements REQ5: square n x y l
 */
public final class Square implements Shape {
    private final String name;
    public int z;
    public double x, y, sideLength;
    
    /**
     * Creates a square.
     * @param name unique name for the square
     * @param z z-index (drawing order)
     * @param x x-coordinate of top-left corner
     * @param y y-coordinate of top-left corner
     * @param sideLength side length (must be positive)
     * @throws IllegalArgumentException if name is null/blank or side length is non-positive
     */
    public Square(String name, int z, double x, double y, double sideLength) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required!");
        }
        if (sideLength <= 0) {
            throw new IllegalArgumentException("side length must be positive!!");
        }
        this.name = name; 
        this.z = z;
        this.x = x; 
        this.y = y; 
        this.sideLength = sideLength;
    }
    
    @Override public String name() { return name; }
    @Override public int z() { return z; }
    
    @Override 
    public BoundingBox bbox() {
        return new BoundingBox(x, y, sideLength, sideLength);
    }
    
    @Override 
    public String listInfo() {
        return String.format(Locale.US, "%s square %.2f %.2f %.2f", name, x, y, sideLength);
    }
    
    public double x() { return x; }
    public double y() { return y; }
    public double s() { return sideLength; }
}
