package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

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
}
