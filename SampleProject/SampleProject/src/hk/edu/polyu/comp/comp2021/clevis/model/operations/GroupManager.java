package hk.edu.polyu.comp.comp2021.clevis.model.operations;

import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import java.util.*;

/**
 * Manages grouping and ungrouping of shapes.
 * Implements REQ6 (group command) and REQ7 (ungroup command).
 */
public class GroupManager {
    private final Map<String, Shape> shapes;
    private final List<Shape> drawOrder;
    private final Map<String, Group> groups;
    private final ShapeFactory factory;

    // Track which shapes are hidden because they're in groups
    private final Set<String> hiddenShapeNames;

    public GroupManager(Map<String, Shape> shapes, List<Shape> drawOrder,
                        Map<String, Group> groups, ShapeFactory factory) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
        this.groups = groups;
        this.factory = factory;
        this.hiddenShapeNames = new HashSet<>();
    }

    /**
     * Groups multiple shapes into a single group (REQ6).
     * @param groupName name for the new group
     * @param shapeNames names of shapes to include in the group
     * @return the created group
     * @throws IllegalArgumentException if groupName is already used, any shape doesn't exist,
     *         or any shape is already in another group
     */
    public Group group(String groupName, List<String> shapeNames) {
        if (groupName == null || groupName.isBlank()) {
            throw new IllegalArgumentException("Group name is required");
        }
        if (shapeNames == null || shapeNames.isEmpty()) {
            throw new IllegalArgumentException("At least one shape must be specified");
        }

        if (shapes.containsKey(groupName)) {
            throw new IllegalArgumentException("Name already used: " + groupName);
        }

        List<Shape> groupShapes = new ArrayList<>();
        for (String name : shapeNames) {
            // Check if shape is already hidden (in another group)
            if (hiddenShapeNames.contains(name)) {
                throw new IllegalArgumentException("Shape is already in a group: " + name);
            }

            Shape shape = shapes.get(name);
            if (shape == null) {
                throw new IllegalArgumentException("Shape not found: " + name);
            }

            groupShapes.add(shape);
        }

        // Create group with highest z-index
        Group group = new Group(groupName, factory.getNextZ(), groupShapes);
        factory.setNextZ(factory.getNextZ() + 1);

        // Add group to collections
        shapes.put(groupName, group);
        drawOrder.add(group);
        groups.put(groupName, group);

        // Hide member shapes - they can't be accessed individually anymore
        for (String name : shapeNames) {
            hiddenShapeNames.add(name);
            // Remove from drawOrder so they don't render individually
            Shape shape = shapes.get(name);
            drawOrder.remove(shape);
        }

        return group;
    }

    /**
     * Ungroups a group shape, making its components available again (REQ7).
     * @param groupName name of the group to ungroup
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

        // Restore member shapes to individual access
        for (Shape member : group.getShapes()) {
            hiddenShapeNames.remove(member.name());
            // Re-add to drawOrder with their original z-index
            drawOrder.add(member);
        }

        // Remove group
        shapes.remove(groupName);
        drawOrder.remove(group);
        groups.remove(groupName);
    }

    /**
     * Check if a shape name is hidden (part of a group).
     */
    public boolean isHidden(String name) {
        return hiddenShapeNames.contains(name);
    }

    /**
     * Get all hidden shape names.
     */
    public Set<String> getHiddenShapeNames() {
        return Collections.unmodifiableSet(hiddenShapeNames);
    }
}