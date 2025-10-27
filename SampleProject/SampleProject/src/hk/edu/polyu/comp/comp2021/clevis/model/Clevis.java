package hk.edu.polyu.comp.comp2021.clevis.model;
import java.util.*;

public class Clevis {

    public Clevis()
	{
		this.shapes = new HashMap<>();
        this.drawOrder = new ArrayList<>();
        this.groups = new HashMap<>();
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
