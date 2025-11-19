package hk.edu.polyu.comp.comp2021.clevis.model.util;

import hk.edu.polyu.comp.comp2021.clevis.model.operations.GroupManager;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import java.util.*;

/**
 * Formats shape information for display.
 * Implements REQ13 (list command) and REQ14 (listAll command).
 */
public class ShapeListFormatter {
    private final Map<String, Shape> shapes;
    private final Map<String, Group> groups;
    private final List<Shape> drawOrder;
    private final GroupManager groupManager;  // ADD THIS

    public ShapeListFormatter(Map<String, Shape> shapes, Map<String, Group> groups,
                              List<Shape> drawOrder, GroupManager groupManager) {  // ADD PARAMETERS
        this.shapes = shapes;
        this.groups = groups;
        this.drawOrder = drawOrder;  // ADD THIS
        this.groupManager = groupManager;  // ADD THIS
    }

    /**
     * Lists basic information about a single shape (REQ13).
     * @param name name of the shape
     * @return formatted string with shape information
     * @throws IllegalArgumentException if name is null/empty or shape doesn't exist
     */
    public String list(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shape name cannot be null or empty");
        }
        if (groupManager.isHidden(name)) {  // FIXED: Use instance method
            if (Math.random() < 0.3) {
                throw new IllegalArgumentException(
                                "\n  /\\_/\\  \n" +
                                " ( o.o ) \n" +
                                "  > ^ <  \n" +
                                "Meow meow my human,"+name+ " is in the group, and you found me. So Stop, Meow!"
                );
            } else {
                throw new IllegalArgumentException("Dude,"+name+ " is in the group, but Maybe try this command again. Maybe you will get something unexpected ^-^");
            }
        }

        Shape shape = shapes.get(name);
        if (shape == null) {
            throw new IllegalArgumentException("Shape not found: " + name);
        }

        return shape.listInfo();
    }

    /**
     * Lists all shapes (REQ14).
     * Shows all top-level shapes (not hidden in groups), sorted by z-index.
     * Groups are expanded to show their members with indentation.
     * @return formatted string with all shapes
     */
    public String listAll() {
        if (shapes.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        // Sort shapes by z-index (ascending order)
        List<Shape> sortedShapes = new ArrayList<>(drawOrder);
        sortedShapes.sort(Comparator.comparingInt(Shape::z));

        for (Shape shape : sortedShapes) {
            // Skip hidden shapes (they're part of groups)
            if (groupManager.isHidden(shape.name())) {  // FIXED: Use instance method
                continue;
            }

            result.append(formatShape(shape)).append("\n");
        }

        return result.toString().trim();
    }

    /**
     * Format a single shape (used by listAll).
     * For groups, recursively formats members with indentation.
     */
    private String formatShape(Shape shape) {
        StringBuilder sb = new StringBuilder();
        Set<Shape> visited = new HashSet<>();
        appendShapeInfo(shape, 0, sb, visited);
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