package hk.edu.polyu.comp.comp2021.clevis.model;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Clevis {
	public Clevis()
	{
		this.shapes = new LinkedHashMap<>();
        this.drawOrder = new ArrayList<>();
        this.groups = new HashMap<>();
	}

    // Storage with stable insertion order → natural Z-order.
    private final Map<String, Shape> shapes = new LinkedHashMap<>();
    private int nextZ = 1;

    // Shared shape contract .
    interface Shape {
        String name();
        int z();
        BoundingBox bbox();
        String listInfo 
    }
    // Tiny immutable value object for boxes.
    static final class BoundingBox {
        final double x, y, w, h;
        BoundingBox(double x, double y, double w, double h) {
            if (w < 0 || h < 0) throw new IllegalArgumentException("width and height must be positive");
            this.x = x; this.y = y; this.w = w; this.h = h;
        }
        @Override public String toString() {
            return String.format(Locale.US, "%.2f %.2f %.2f %.2f", x, y, w, h);
        }
    }

    // =============================
    // REQ2 —  support drawing a rectangle.
    // =============================
    static final class Rectangle implements Shape {
        private final String name;
        private final int z;
        private final double x, y, w, h;
    //@throws IllegalArgumentException name need to be unique and cannot be null
	//@throws IllegalArgumentException width and height must be positive
        Rectangle(String name, int z, double x, double y, double w, double h) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required!");
            if (w <= 0 || h <= 0) throw new IllegalArgumentException("width and height must be positive!!");
            this.name = name; this.z = z;
            this.x = x; this.y = y; this.w = w; this.h = h;
        }
     //@Override allows the compiler to help you check whether it is actually overridden correctly.
        @Override public String name() { return name; }
        @Override public int z() { return z; }
        @Override public BoundingBox bbox() { return new BoundingBox(x, y, w, h); }
        @Override public String listInfo() {
            return String.format(Locale.US, "%s rectangle %.2f %.2f %.2f %.2f", name, x, y, w, h);
        }
    }

    // =============================
    // REQ3 — support drawing a line segment
    // =============================
    static final class Line implements Shape {
        private final String name;
        private final int z;
        private final double x1, y1, x2, y2;
    //@throws IllegalArgumentException name need to be unique and cannot be null
	//@throws IllegalArgumentException	line needs two distinct points
        Line(String name, int z, double x1, double y1, double x2, double y2) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required!");
            if (x1 == x2 && y1 == y2) throw new IllegalArgumentException("a line needs two distinct points!!");
            this.name = name; this.z = z;
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
        
		//@Override allows the compiler to help you check whether it is actually overridden correctly.
        @Override public String name() { return name; }
        @Override public int z() { return z; }
        @Override public BoundingBox bbox() {
            double minX = Math.min(x1, x2), minY = Math.min(y1, y2);
            double maxX = Math.max(x1, x2), maxY = Math.max(y1, y2);
            return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
        }
        @Override public String listInfo() {
            return String.format(Locale.US, "%s line %.2f %.2f %.2f %.2f", name, x1, y1, x2, y2);
        }
    }
	// =============================
    // REQ4 — support drawing a circle
    // =============================
    static final class Circle implements Shape {
        private final String name;
        private final int z;
        public double x, y, r;

        /**
         * @throws IllegalArgumentException name need to be unique and cannot be null
         * @throws IllegalArgumentException radius must be positive
         */
        Circle(String name, int z, double x, double y, double r) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required!");
            if (radius <= 0) throw new IllegalArgumentException("radius must be positive!!");
            this.name = name; 
            this.z = z;
            this.x = x; 
            this.y = y; 
            this.r = r;
        }

        @Override public String name() { return name; }
        @Override public int z() { return z; }
        
        @Override public BoundingBox bbox() {
            // Bounding box for circle: from (centerX-radius, centerY-radius) to (centerX+radius, centerY+radius)
            double x = x - r;
            double y = y - r;
            double width = 2 * r;
            double height = 2 * r;
            return new BoundingBox(x, y, width, height);
        }
        
        @Override public String listInfo() {
            return String.format(Locale.US, "%s circle %.2f %.2f %.2f", name, x,y, r);
        }
        
        // Getters for internal calculations
        public double getx() { return x; }
        public double gety() { return y; }
        public double getr() { return r; }
    }

    // =============================
    // REQ5 — support drawing a square
    // =============================
    static final class Square implements Shape {
        private final String name;
        private final int z;
        public double x, y, sideLength;

        /**
         * @throws IllegalArgumentException name need to be unique and cannot be null
         * @throws IllegalArgumentException side length must be positive
         */
        Square(String name, int z, double x, double y, double sideLength) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required!");
            if (sideLength <= 0) throw new IllegalArgumentException("side length must be positive!!");
            this.name = name; 
            this.z = z;
            this.x = x; 
            this.y = y; 
            this.sideLength = sideLength;
        }

        @Override public String name() { return name; }
        @Override public int z() { return z; }
        
        @Override public BoundingBox bbox() {
            // Bounding box for square is the square itself
            return new BoundingBox(x, y, sideLength, sideLength);
        }
        
        @Override public String listInfo() {
            return String.format(Locale.US, "%s square %.2f %.2f %.2f", name, x, y, sideLength);
        }
        
        // Getters for internal calculations
        public double getX() { return x; }
        public double getY() { return y; }
        public double getSideLength() { return sideLength; }
    }


    // Public API ,can be used by tiny CLI
    public Rectangle rectangle(String n, double x, double y, double w, double h) {
        ensureUnique(n);
        Rectangle r = new Rectangle(n, nextZ++, x, y, w, h);
        shapes.put(n, r);
        return r;
    }
    public Line line(String n, double x1, double y1, double x2, double y2) {
        ensureUnique(n);
        Line l = new Line(n, nextZ++, x1, y1, x2, y2);
        shapes.put(n, l);
        return l;
    }
	public Circle circle(String n, double x, double y, double r) {
        ensureUnique(n);
        Circle c = new Circle(n, nextZ++, x, y, r);
        shapes.put(n, c);
        return c;
    }
    public Square square(String n, double x, double y, double sideLength) {
        ensureUnique(n);
        Square s = new Square(n, nextZ++, x, y, sideLength);
        shapes.put(n, s);
        return s;
    }
    public Collection<Shape> all() { return shapes.values(); }

	//name need to be unique and cannot be null
    private void ensureUnique(String n) {
        if (n == null || n.isBlank()) throw new IllegalArgumentException("name is required");
        if (shapes.containsKey(n)) throw new IllegalArgumentException("Name already used: " + n);
    }

	

	// =============================================
    // REQ6: Grouping shapes implementation
    // =============================================
    
    /**
     * REQ6: Groups multiple shapes into a single group
     * @param groupName The name for the new group
     * @param shapeNames The names of shapes to be grouped
     * @throws IllegalArgumentException if any shape doesn't exist, group name exists, or insufficient shapes
     */
    public void groupShapes(String groupName, List<String> shapeNames) {
        // Validate input parameters
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }
        if (shapeNames == null || shapeNames.size() < 2) {
            throw new IllegalArgumentException("At least two shapes required for grouping");
        }
        if (shapes.containsKey(groupName) || groups.containsKey(groupName)) {
            throw new IllegalArgumentException("Name already exists: " + groupName);
        }

        // Create new group
        Group group = new Group(groupName);
        
        // Add shapes to group and validate existence
        for (String shapeName : shapeNames) {
            Shape shape = shapes.get(shapeName);
            if (shape == null) {
                throw new IllegalArgumentException("Shape not found: " + shapeName);
            }
            if (shape instanceof Group) {
                throw new IllegalArgumentException("Cannot group existing groups: " + shapeName);
            }
            group.addShape(shape);
        }

        // Remove individual shapes from top-level access and draw order
        for (String shapeName : shapeNames) {
            Shape removedShape = shapes.remove(shapeName);
            drawOrder.remove(removedShape);
        }

        // Register the group
        groups.put(groupName, group);
        shapes.put(groupName, group);
        drawOrder.add(group);
    }

    // =============================================
    // REQ7: Ungrouping shapes implementation  
    // =============================================

    /**
     * REQ7: Ungroups a group shape back into its component shapes
     * @param groupName The name of the group to ungroup
     * @throws IllegalArgumentException if group doesn't exist or is not a group
     */
    public void ungroupShape(String groupName) {
        // Validate input
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }

        Shape shape = shapes.get(groupName);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + groupName);
        }

        if (!(shape instanceof Group)) {
            throw new IllegalArgumentException("Shape is not a group: " + groupName);
        }

        Group group = (Group) shape;
        
        // Get component shapes before ungrouping
        List<Shape> componentShapes = group.getShapes();
        if (componentShapes.isEmpty()) {
            throw new IllegalArgumentException("Group is empty: " + groupName);
        }

        // Add component shapes back to top-level access
        for (Shape component : componentShapes) {
            String componentName = component.getName();
            if (shapes.containsKey(componentName)) {
                throw new IllegalStateException("Shape name conflict during ungroup: " + componentName);
            }
            shapes.put(componentName, component);
            drawOrder.add(component);
        }

        // Remove the group from all collections
        groups.remove(groupName);
        shapes.remove(groupName);
        drawOrder.remove(group);
    }

    // =============================================
    // REQ9: Bounding box calculation implementation
    // =============================================

    /**
     * REQ9: Calculates the bounding box for a specified shape
     * @param shapeName The name of the shape
     * @return double array [x, y, width, height] of the bounding box
     * @throws IllegalArgumentException if shape doesn't exist
     */
    public double[] getBoundingBox(String shapeName) {
        // Validate input
        if (shapeName == null || shapeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }

        Shape shape = shapes.get(shapeName);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + shapeName);
        }

        Rectangle boundingBox = shape.getBoundingBox();
        return new double[]{
            roundToTwoDecimals(boundingBox.getX()),
            roundToTwoDecimals(boundingBox.getY()), 
            roundToTwoDecimals(boundingBox.getWidth()),
            roundToTwoDecimals(boundingBox.getHeight())
        };
    }

    // =============================================
    // REQ11: Finding shape at point implementation
    // =============================================

    /**
     * REQ11: Finds the topmost shape that covers the specified point
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point  
     * @return The name of the topmost shape covering the point, or null if none
     */
    public String findShapeAtPoint(double x, double y) {
        // Search in reverse draw order (top to bottom - shapes created later have higher Z-index)
        for (int i = drawOrder.size() - 1; i >= 0; i--) {
            Shape shape = drawOrder.get(i);
            if (shape.containsPoint(x, y)) {
                return shape.getName();
            }
        }
        return null;
    }

    // =============================================
    // REQ12: Intersection check implementation
    // =============================================

    /**
     * REQ12: Checks if two shapes intersect with each other
     * @param shapeName1 The name of the first shape
     * @param shapeName2 The name of the second shape
     * @return true if the shapes intersect, false otherwise
     * @throws IllegalArgumentException if either shape doesn't exist
     */
    public boolean checkIntersection(String shapeName1, String shapeName2) {
        // Validate inputs
        if (shapeName1 == null || shapeName2 == null) {
            throw new IllegalArgumentException("Shape names cannot be null");
        }

        Shape shape1 = shapes.get(shapeName1);
        Shape shape2 = shapes.get(shapeName2);
        
        if (shape1 == null) {
            throw new IllegalArgumentException("Shape not found: " + shapeName1);
        }
        if (shape2 == null) {
            throw new IllegalArgumentException("Shape not found: " + shapeName2);
        }

        return shape1.intersects(shape2);
    }

    // =============================================
    // Utility methods
    // =============================================

    /**
     * Rounds a double value to 2 decimal places as specified in requirements
     */
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Adds a shape to the system (for use by other team members implementing shape creation)
     */
    public void addShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Shape cannot be null");
        }
        String name = shape.getName();
        if (shapes.containsKey(name)) {
            throw new IllegalArgumentException("Shape name already exists: " + name);
        }
        shapes.put(name, shape);
        drawOrder.add(shape);
    }



	
}
