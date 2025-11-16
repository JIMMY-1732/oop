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
    
    public GroupManager(Map<String, Shape> shapes, List<Shape> drawOrder, 
                       Map<String, Group> groups, ShapeFactory factory) {
        this.shapes = shapes;
        this.drawOrder = drawOrder;
        this.groups = groups;
        this.factory = factory;
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
            Shape shape = shapes.get(name);
            if (shape == null) {
                throw new IllegalArgumentException("Shape not found: " + name);
            }
            
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
        
        Group group = new Group(groupName, factory.getNextZ(), groupShapes);
        factory.setNextZ(factory.getNextZ() + 1);
        shapes.put(groupName, group);
        drawOrder.add(group);
        groups.put(groupName, group);
        
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
        
        shapes.remove(groupName);
        drawOrder.remove(group);
        groups.remove(groupName);
    }
}
