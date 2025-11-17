package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.operations.*;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import hk.edu.polyu.comp.comp2021.clevis.model.util.*;
import java.util.*;

/**
 * Main model class for the Clevis vector graphics tool.
 * Coordinates all shape operations and maintains the drawing state.
 * This class serves as the facade for the model layer in the MVC pattern.
 */
public class Clevis {
    // Core data structures
    public Map<String, Shape> shapes = new LinkedHashMap<>();
    public List<Shape> drawOrder = new ArrayList<>();
    public Map<String, Group> groups = new HashMap<>();
    
    // Operation handlers
    private final ShapeFactory factory;
    private final GroupManager groupManager;
    private final ShapeManager shapeManager;
    private final ShapeMover mover;
    private final ShapeQueryHandler queryHandler;
    private final ShapeListFormatter formatter;
    
    /**
     * Initializes a new Clevis instance with all necessary components.
     */
    public Clevis() {
        this.shapes = new LinkedHashMap<>();
        this.drawOrder = new ArrayList<>();
        this.groups = new HashMap<>();
        this.factory = new ShapeFactory(shapes, drawOrder);
        this.groupManager = new GroupManager(shapes, drawOrder, groups, factory);
        this.shapeManager = new ShapeManager(shapes, drawOrder, groups);
        this.mover = new ShapeMover(shapes, drawOrder, groups);
        this.queryHandler = new ShapeQueryHandler(shapes, drawOrder);
        this.formatter = new ShapeListFormatter(shapes, groups);
    }
    
    /**
     * @return all shapes in the drawing
     */
    public Collection<Shape> all() {
        return shapes.values();
    }
    
    // =============================
    // REQ2-5: Shape creation methods
    // =============================
    
    public Rectangle rectangle(String n, double x, double y, double w, double h) {
        return factory.createRectangle(n, x, y, w, h);
    }
    
    public Line line(String n, double x1, double y1, double x2, double y2) {
        return factory.createLine(n, x1, y1, x2, y2);
    }
    
    public Circle circle(String n, double x, double y, double r) {
        return factory.createCircle(n, x, y, r);
    }
    
    public Square square(String n, double x, double y, double sideLength) {
        return factory.createSquare(n, x, y, sideLength);
    }
    
    // =============================
    // REQ6-7: Group operations
    // =============================
    
    public Group group(String groupName, List<String> shapeNames) {
        return groupManager.group(groupName, shapeNames);
    }
    
    public void ungroup(String groupName) {
        groupManager.ungroup(groupName);
    }
    
    // =============================
    // REQ8: Delete operation
    // =============================
    
    public void deleteShape(String name) {
        shapeManager.deleteShape(name);
    }
    
    // =============================
    // REQ9: Bounding box calculation
    // =============================
    
    public BoundingBox boundingBox(String name) {
        return queryHandler.boundingBox(name);
    }
    
    // =============================
    // REQ10: Move operation
    // =============================
    
    public void move(String shapeName, double dx, double dy) {
        mover.move(shapeName, dx, dy);
    }
    
    // =============================
    // REQ11: Find topmost shape at point
    // =============================
    
    public String shapeAt(double x, double y) {
        return queryHandler.shapeAt(x, y);
    }
    
    // =============================
    // REQ12: Intersection detection
    // =============================
    
    public boolean intersect(String name1, String name2) {
        return queryHandler.intersect(name1, name2);
    }
    
    // =============================
    // REQ13-14: Listing operations
    // =============================
    
    public String list(String name) {
        return formatter.list(name);
    }
    
    public String listAll() {
        return formatter.listAll();
    }
}