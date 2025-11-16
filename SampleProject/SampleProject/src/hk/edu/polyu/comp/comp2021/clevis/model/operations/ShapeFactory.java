package hk.edu.polyu.comp.comp2021.clevis.model.operations;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import java.util.*;

/**
 * Factory class for creating shape instances.
 * Handles REQ2-REQ5 (creating rectangles, lines, circles, and squares).
 */
public class ShapeFactory {
    private final Map<String, Shape> shapes;
    private final List<Shape> drawOrder;
    private int nextZ = 1;
    
    public ShapeFactory(Map<String, Shape> shapes, List<Shape> drawOrder) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
    }
    
    /**
     * Ensures a name is unique and valid.
     * @throws IllegalArgumentException if name is null, blank, or already used
     */
    private void ensureUnique(String n) {
        if (n == null || n.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (shapes.containsKey(n)) {
            throw new IllegalArgumentException("Name already used: " + n);
        }
    }
    
    /**
     * Creates a rectangle (REQ2).
     */
    public Rectangle createRectangle(String n, double x, double y, double w, double h) {
        ensureUnique(n);
        Rectangle r = new Rectangle(n, nextZ++, x, y, w, h);
        shapes.put(n, r);
        drawOrder.add(r);
        return r;
    }
    
    /**
     * Creates a line segment (REQ3).
     */
    public Line createLine(String n, double x1, double y1, double x2, double y2) {
        ensureUnique(n);
        Line l = new Line(n, nextZ++, x1, y1, x2, y2);
        shapes.put(n, l);
        drawOrder.add(l);
        return l;
    }
    
    /**
     * Creates a circle (REQ4).
     */
    public Circle createCircle(String n, double x, double y, double r) {
        ensureUnique(n);
        Circle c = new Circle(n, nextZ++, x, y, r);
        shapes.put(n, c);
        drawOrder.add(c);
        return c;
    }
    
    /**
     * Creates a square (REQ5).
     */
    public Square createSquare(String n, double x, double y, double sideLength) {
        ensureUnique(n);
        Square s = new Square(n, nextZ++, x, y, sideLength);
        shapes.put(n, s);
        drawOrder.add(s);
        return s;
    }
    
    public int getNextZ() {
        return nextZ;
    }
    
    public void setNextZ(int z) {
        this.nextZ = z;
    }
}