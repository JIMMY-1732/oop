package hk.edu.polyu.comp.comp2021.clevis.model.shapes;

/**
 * Common interface for all shapes in Clevis.
 * Supports REQ2-REQ6 by defining the contract for all shape types.
 */
public interface Shape {
    /**
     * @return the unique name of this shape
     */
    String name();
    
    /**
     * @return the z-index (drawing order) of this shape
     */
    int z();
    
    /**
     * @return the minimum bounding box of this shape (REQ9)
     */
    BoundingBox bbox();
    
    /**
     * @return formatted string containing basic shape information (REQ13)
     */
    String listInfo();
}