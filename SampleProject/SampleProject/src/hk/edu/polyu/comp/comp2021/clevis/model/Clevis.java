package hk.edu.polyu.comp.comp2021.clevis.model;
import java.util.*;

public class Clevis {
	private  Map<String, Shape> shapes = new LinkedHashMap<>();
    private List<Shape> drawOrder = new ArrayList<>();
    private Map<String, Group> groups = new HashMap<>();
    

    public Clevis()
	{
		this.shapes = new LinkedHashMap<>();
        this.drawOrder = new ArrayList<>();
        this.groups = new HashMap<>();
	}

    // Storage with stable insertion order → natural Z-order.
    private int nextZ = 1;

    // Shared shape contract .
    interface Shape {
        String name();
        int z();
        BoundingBox bbox();
        String listInfo();
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
	// int x,y coordinate w, width h, height z, laters
    static final class Rectangle implements Shape {
        private final String name;
        private final int z;
        public final double x, y, w, h;
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
	// int x1,2,y1,2 4 coordinates  z, laters
    static final class Line implements Shape {
        private final String name;
        public int z;
        public double x1, y1, x2, y2;
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
        public int z;
        public double centerX, centerY, radius;

        /**
         * @throws IllegalArgumentException name need to be unique and cannot be null
         * @throws IllegalArgumentException radius must be positive
         */
        Circle(String name, int z, double centerX, double centerY, double radius) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required!");
            if (radius <= 0) throw new IllegalArgumentException("radius must be positive!!");
            this.name = name; 
            this.z = z;
            this.centerX = centerX; 
            this.centerY = centerY; 
            this.radius = radius;
        }

        @Override public String name() { return name; }
        @Override public int z() { return z; }
        
        @Override public BoundingBox bbox() {
            // Bounding box for circle: from (centerX-radius, centerY-radius) to (centerX+radius, centerY+radius)
            double x = centerX - radius;
            double y = centerY - radius;
            double width = 2 * radius;
            double height = 2 * radius;
            return new BoundingBox(x, y, width, height);
        }
        
        @Override public String listInfo() {
            return String.format(Locale.US, "%s circle %.2f %.2f %.2f", name, centerX, centerY, radius);
        }
        
        // Getters for internal calculations
        public double getCenterX() { return centerX; }
        public double getCenterY() { return centerY; }
        public double getRadius() { return radius; }
    }

    // =============================
    // REQ5 — support drawing a square
    // =============================
    static final class Square implements Shape {
        private final String name;
        public int z;
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

    public Collection<Shape> all() { return shapes.values(); }

	//name need to be unique and cannot be null
    private void ensureUnique(String n) {
        if (n == null || n.isBlank()) throw new IllegalArgumentException("name is required");
        if (shapes.containsKey(n)) throw new IllegalArgumentException("Name already used: " + n);
    }

	

	// =============================
    // REQ6 — support grouping shapes
    // =============================
    static final class Group implements Shape {
        private final String name;
        private final int z;
        private final List<Shape> shapes;
        
        /**
         * @throws IllegalArgumentException if name is null/blank or shapes list is empty
         */
        Group(String name, int z, List<Shape> shapes) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required!");
            if (shapes == null || shapes.isEmpty()) throw new IllegalArgumentException("group must contain at least one shape!");
            this.name = name;
            this.z = z;
            this.shapes = new ArrayList<>(shapes); 
        }
        
        @Override public String name() { return name; }
        @Override public int z() { return z; }
        
        @Override public BoundingBox bbox() {
            if (shapes.isEmpty()) {
                throw new IllegalStateException("Group is empty!");
            }
            
            // Initialize with the first shape's bounding box
            BoundingBox first = shapes.get(0).bbox();
            double minX = first.x;
            double minY = first.y;
            double maxX = first.x + first.w;
            double maxY = first.y + first.h;
            
// Find the encompassing bounds of all shapes, where the logic is compare every shape's bbox and update minX, minY, maxX, maxY accordingly
            for (int i = 1; i < shapes.size(); i++) {
                BoundingBox box = shapes.get(i).bbox();
                minX = Math.min(minX, box.x);
                minY = Math.min(minY, box.y);
                maxX = Math.max(maxX, box.x + box.w);
                maxY = Math.max(maxY, box.y + box.h);
            }
            
            return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
        }
        
        @Override public String listInfo() {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" group");
            for (Shape s : shapes) {
                sb.append(" ").append(s.name());
            }
            return sb.toString();
        }
        
        // Get a copy of the shapes in this group
        public List<Shape> getShapes() {
            return new ArrayList<>(shapes);
        }
    }
    
    /**
     * Groups a list of shapes into a single group shape
     * @param groupName Name for the new group
     * @param shapeNames Names of shapes to include in the group
     * @return The created group
     * @throws IllegalArgumentException if any shape doesn't exist or is in another group
     */
    public Group group(String groupName, List<String> shapeNames) {
        if (groupName == null || groupName.isBlank()) {
            throw new IllegalArgumentException("Group name is required");
        }
        if (shapeNames == null || shapeNames.isEmpty()) {
            throw new IllegalArgumentException("At least one shape must be specified");
        }
        
        // Check if group name already exists
        ensureUnique(groupName);
        
        // Collect shapes and check if they're available
        List<Shape> groupShapes = new ArrayList<>();
        for (String name : shapeNames) {
            Shape shape = shapes.get(name);
            if (shape == null) {
                throw new IllegalArgumentException("Shape not found: " + name);
            }
            
            // Check if this shape is already in another group
            boolean inAnotherGroup = false;
            for (Group g : groups.values()) {
                if (g.getShapes().contains(shape)) {
                    inAnotherGroup = true;
                    break;
                }
            }
            
            if (inAnotherGroup) {
                throw new IllegalArgumentException("Shape is already in a group: " + name);
            }
            
            groupShapes.add(shape);
        }
        
        // Create the group
        Group group = new Group(groupName, nextZ++, groupShapes);
        shapes.put(groupName, group);
        drawOrder.add(group);
        groups.put(groupName, group);
        
        return group;
    }
    
    // =============================
    // REQ7 — support ungrouping a shape
    // =============================
    /**
     * Ungroups a group shape, making its components available again
     * @param groupName Name of the group to ungroup
     * @throws IllegalArgumentException if the shape doesn't exist or isn't a group
     */
    public void ungroup(String groupName) {
        if (groupName == null || groupName.isBlank()) {
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
        
        // Remove the group itself
        shapes.remove(groupName);
        drawOrder.remove(group);
        groups.remove(groupName);
    }
    // =============================
    // REQ8 — support deleting a shape
    // =============================
    public void deleteShape(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }

        Shape s = shapes.get(name);
        if (s == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }

        if (s instanceof Group) {
            Group grp = (Group) s;
            List<Shape> membersCopy = new ArrayList<>(grp.getShapes());

            for (Shape member : membersCopy) {
                if (!member.name().equals(name)) { // prevent self-recursion
                    deleteShape(member.name());
                }
            }
            groups.remove(name);
        }

        shapes.remove(name);
        drawOrder.remove(s);
    }
    // =============================
    // REQ9 — support calculating minimum bounding box
    // =============================
    /**
     * Calculates the bounding box of a shape
     * @param name Name of the shape
     * @return The bounding box coordinates and dimensions
     * @throws IllegalArgumentException if the shape doesn't exist
     */
    public BoundingBox boundingBox(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }
        
        Shape shape = shapes.get(name);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }
        
        return shape.bbox();
    }
	public Rectangle rectangle(String n, double x, double y, double w, double h) {
        ensureUnique(n);
        Rectangle r = new Rectangle(n, nextZ++, x, y, w, h);
        shapes.put(n, r);
        drawOrder.add(r);
        return r;
    }


    public Line line(String n, double x1, double y1, double x2, double y2) {
        ensureUnique(n);
        Line l = new Line(n, nextZ++, x1, y1, x2, y2);
        shapes.put(n, l);
        drawOrder.add(l);
        return l;
    }


    public Circle circle(String n, double x, double y, double r) {
        ensureUnique(n);
        Circle c = new Circle(n, nextZ++, x, y, r);
        shapes.put(n, c);
        drawOrder.add(c);
        return c;
    }
    public Square square(String n, double x, double y, double sideLength) {
        ensureUnique(n);
        Square s = new Square(n, nextZ++, x, y, sideLength);
        shapes.put(n, s);
        drawOrder.add(s);
        return s;
    }

    // =============================================
    // REQ10: The tool should support moving a shape
    // =============================================

    public void move(String shapeName, double dx, double dy) {
        if (shapeName == null || shapeName.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }

        if (dx == 0 && dy == 0) {
            return;
        }

        if(shapes.containsKey(shapeName)) {
            if (shapes.get(shapeName) == null) {
                throw new IllegalArgumentException("Shape not found: " + shapeName);
            }
            moveShape(shapes.get(shapeName), dx, dy, new HashSet<>());
        }

        if(groups.containsKey(shapeName)) {
            if (groups.get(shapeName) == null) {
                throw new IllegalArgumentException("Shape not found: " + shapeName);
            }
//            moveShape(groups.get(shapeName), dx, dy, new HashSet<>());
        }



    }

    private void moveShape(Shape shape, double dx, double dy, Set<Shape> visited) {
        if (!visited.add(shape)) {
            return;
        }

        if (shape instanceof Group group) {
            for (Shape member : group.getShapes()) {
                moveShape(member, dx, dy, visited);
            }
            return;
        }

        if (shape instanceof Rectangle rect) {
            Rectangle moved = new Rectangle(rect.name(), rect.z(), rect.x + dx, rect.y + dy, rect.w, rect.h);
            replaceShapeInCollections(rect, moved);
        } else if (shape instanceof Line line) {
            Line moved = new Line(line.name(), line.z(), line.x1 + dx, line.y1 + dy, line.x2 + dx, line.y2 + dy);
            replaceShapeInCollections(line, moved);
        } else if (shape instanceof Circle circle) {
            Circle moved = new Circle(circle.name(), circle.z, circle.centerX + dx, circle.centerY + dy, circle.radius);
            replaceShapeInCollections(circle, moved);
        } else if (shape instanceof Square square) {
            Square moved = new Square(square.name(), square.z, square.x + dx, square.y + dy, square.sideLength);
            replaceShapeInCollections(square, moved);
        } else {
            throw new IllegalArgumentException("Unsupported shape type: " + shape.getClass().getSimpleName());
        }
    }

    private void replaceShapeInCollections(Shape oldShape, Shape newShape) {
        shapes.put(newShape.name(), newShape);

        int index = drawOrder.indexOf(oldShape);
        if (index >= 0) {
            drawOrder.set(index, newShape);
        }

        for (Group group : groups.values()) {
            List<Shape> members = group.getShapes();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) == oldShape) {
                    members.set(i, newShape);
                }
            }
        }
    }

// =============================
// REQ11 — Find topmost shape at point
// =============================
public String shapeAt(double x, double y) {
    // Start from the top (highest Z-index)
    for (int i = drawOrder.size() - 1; i >= 0; i--) {
        Shape shape = drawOrder.get(i);
        if (covers(shape, x, y)) {
            return shape.name();
        }
    }
    return null; // No shape found
}

private boolean covers(Shape shape, double x, double y) {
    if (shape instanceof Group) {
        Group group = (Group) shape;
        for (Shape member : group.getShapes()) {
            if (covers(member, x, y)) {
                return true;
            }
        }
        return false;
    }
    
    double distance = distanceToShape(shape, x, y);
    return distance < 0.05;
}
private double distanceToLine(Line line, double x, double y) {
    double x1 = line.x1, y1 = line.y1, x2 = line.x2, y2 = line.y2;
    
    // Calculate distance from point to line segment
    double lineLength = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    if (lineLength == 0) return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    
    // Calculate projection parameter
    double t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / lineLength;
    t = Math.max(0, Math.min(1, t)); // Clamp to line segment
    
    // Find closest point on line segment
    double px = x1 + t * (x2 - x1);
    double py = y1 + t * (y2 - y1);
    
    return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
}

private double distanceToRectangle(Rectangle rect, double x, double y) {
    double rx = rect.x, ry = rect.y, rw = rect.w, rh = rect.h;
    
    // If inside rectangle, find distance to nearest edge
    if (x >= rx && x <= rx + rw && y >= ry && y <= ry + rh) {
        return Math.min(
            Math.min(x - rx, rx + rw - x),
            Math.min(y - ry, ry + rh - y)
        );
    }
    
    // Outside rectangle, find distance to nearest edge or corner
    double dx = Math.max(rx - x, Math.max(0, x - (rx + rw)));
    double dy = Math.max(ry - y, Math.max(0, y - (ry + rh)));
    return Math.sqrt(dx * dx + dy * dy);
}

private double distanceToCircle(Circle circle, double x, double y) {
    // Distance from point to center minus radius
    double dx = x - circle.centerX;
    double dy = y - circle.centerY;
    double distance = Math.sqrt(dx * dx + dy * dy);
    return Math.abs(distance - circle.radius);
}

private double distanceToSquare(Square square, double x, double y) {
    // Square is a special case of rectangle
    return distanceToRectangle(
        new Rectangle(square.name(), square.z(), square.x, square.y, 
                      square.sideLength, square.sideLength),
        x, y
    );
}

private double distanceToShape(Shape shape, double x, double y) {
    // to check whether is this a instanceof Line, Rectangle, Circle, Square, ues give you the distance calculation method
    if (shape instanceof Line) {
        return distanceToLine((Line) shape, x, y);
    } else if (shape instanceof Rectangle rectangle) {
        return distanceToRectangle(rectangle, x, y);
    } else if (shape instanceof Circle circle) {
        return distanceToCircle(circle, x, y);
    } else if (shape instanceof Square square) {
        return distanceToSquare(square, x, y);
    }
    throw new IllegalArgumentException("Unsupported shape type");
}

// =============================
// REQ12 — Check if two shapes intersect
// =============================
public boolean intersect(String name1, String name2) {
    if (name1 == null || name1.isBlank() || name2 == null || name2.isBlank()) {
        throw new IllegalArgumentException("Shape names cannot be null or empty");
    }
    
    Shape shape1 = shapes.get(name1);
    Shape shape2 = shapes.get(name2);
    //I think this is self-explanatory if it is null throw exception else return the intersection result
    if (shape1 == null) {
        throw new IllegalArgumentException("Shape not found: " + name1);
    }
    if (shape2 == null) {
        throw new IllegalArgumentException("Shape not found: " + name2);
    }
    
    return doBoundingBoxesIntersect(shape1.bbox(), shape2.bbox());
}

private boolean doBoundingBoxesIntersect(BoundingBox box1, BoundingBox box2) {
    // No intersection if one box is to the left/right/top/bottom of the other
    if (box1.x + box1.w <= box2.x || box2.x + box2.w <= box1.x) {
        return false;
    }
    if (box1.y + box1.h <= box2.y || box2.y + box2.h <= box1.y) {
        return false;
    }
    // Must be intersect
    return true;
}

    // =============================
    // REQ13 — support listing shape information
    // =============================
    public String list(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }

        Shape shape = shapes.get(name);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }

        return shape.listInfo();
    }

    // =============================
    // REQ14 — support listing all shapes
    // =============================
    public String listAll() {
        List<Shape> roots = new ArrayList<>();
        Set<Shape> nestedShapes = new HashSet<>();

        for (Group group : groups.values()) {
            collectNestedShapes(group, nestedShapes);
        }

        for (Shape shape : shapes.values()) {
            if (!nestedShapes.contains(shape)) {
                roots.add(shape);
            }
        }

        roots.sort(Comparator.comparingInt(Shape::z).reversed());

        StringBuilder sb = new StringBuilder();
        Set<Shape> visited = new HashSet<>();

        for (Shape root : roots) {
            appendShapeInfo(root, 0, sb, visited);
        }

        return sb.toString();
    }

    private void collectNestedShapes(Group group, Set<Shape> nested) {
        for (Shape member : group.getShapes()) {
            if (nested.add(member) && member instanceof Group) {
                collectNestedShapes((Group) member, nested);
            }
        }
    }

    private void appendShapeInfo(Shape shape, int depth, StringBuilder sb, Set<Shape> visited) {
        if (!visited.add(shape)) {
            return;
        }

        if (sb.length() > 0) {
            sb.append(System.lineSeparator());
        }

        String indent = depth <= 0 ? "" : "  ".repeat(depth);
        sb.append(indent).append(shape.listInfo());

        if (shape instanceof Group) {
            List<Shape> members = new ArrayList<>(((Group) shape).getShapes());
            members.sort(Comparator.comparingInt(Shape::z).reversed());
            for (Shape member : members) {
                appendShapeInfo(member, depth + 1, sb, visited);
            }
        }
    } 
}

