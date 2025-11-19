package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ClevisTest {
    private Clevis clevis;

    @Before
    public void setUp() {
        clevis = new Clevis();
    }

    // Basic shape creation tests
    @Test
    public void testRectangleCreation() {
        Rectangle rect = clevis.rectangle("rect1", 0, 0, 5, 3);
        assertEquals("rect1 rectangle 0.00 0.00 5.00 3.00", rect.listInfo());
    }

    @Test
    public void testLineCreation() {
        Line line = clevis.line("line1", 0, 0, 5, 5);
        assertEquals("line1 line 0.00 0.00 5.00 5.00", line.listInfo());
    }

    @Test
    public void testCircleCreation() {
        Circle circle = clevis.circle("circle1", 3, 3, 2);
        assertEquals("circle1 circle 3.00 3.00 2.00", circle.listInfo());
    }

    @Test
    public void testSquareCreation() {
        Square square = clevis.square("square1", 1, 1, 4);
        assertEquals("square1 square 1.00 1.00 4.00", square.listInfo());
    }

    // Invalid input tests
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateShapeName() {
        clevis.rectangle("shape1", 0, 0, 5, 3);
        clevis.circle("shape1", 1, 1, 2); // Should throw exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRectangleDimensions() {
        clevis.rectangle("rect2", 0, 0, -1, 3); // Negative width
    }

    // Group operations tests
    @Test
    public void testGroupCreation() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        List<String> shapes = Arrays.asList("r1", "c1");
        Group group = clevis.group("g1", shapes);
        assertTrue(group.listInfo().contains("g1 group r1 c1"));
    }

    @Test  // Make sure this annotation is present
    public void testUngroup() {
        try {
            clevis.rectangle("r1", 0, 0, 2, 2);
            clevis.circle("c1", 1, 1, 1);
            List<String> shapes = Arrays.asList("r1", "c1");
            clevis.group("g1", shapes);
            clevis.ungroup("g1");

            // Add debugging statements
            System.out.println("Group g1 contents: " + Clevis.group.getShapes().get(Integer.parseInt("g1")));

            assertNull(Clevis.group.getShapes().get(Integer.parseInt("g1")));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    // Move operation tests
    @Test
    public void testMoveShape() {
        Rectangle rect = clevis.rectangle("rect1", 0, 0, 2, 2);
        clevis.move("rect1", 3, 4);
        BoundingBox bbox = clevis.boundingBox("rect1");
        assertEquals(3.0, bbox.x, 0.01);
        assertEquals(4.0, bbox.y, 0.01);
    }

    // Intersection tests
    @Test
    public void testIntersection() {
        clevis.rectangle("r1", 0, 0, 4, 4);
        clevis.rectangle("r2", 2, 2, 4, 4);
        assertTrue(clevis.intersect("r1", "r2"));
    }

    @Test
    public void testNoIntersection() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.rectangle("r2", 4, 4, 2, 2);
        assertFalse(clevis.intersect("r1", "r2"));
    }

    // ShapeAt tests
    @Test
    public void testShapeAt() {
        clevis.rectangle("r1", 0, 0, 4, 4);
        clevis.circle("c1", 6, 6, 2);
        assertEquals("r1", clevis.shapeAt(0.03, 0.03));
        assertEquals("c1", clevis.shapeAt(6, 8));
    }

    // Bounding box tests
    @Test
    public void testBoundingBox() {
        clevis.rectangle("r1", 1, 1, 4, 3);
        BoundingBox bbox = new BoundingBox(1, 1, 4, 3);
        assertEquals(1.0, bbox.x, 0.01);
        assertEquals(1.0, bbox.y, 0.01);
        assertEquals(4.0, bbox.w, 0.01);
        assertEquals(3.0, bbox.h, 0.01);
    }

    // List operations tests
    @Test
    public void testList() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        String info = clevis.list("r1");
        assertEquals("r1 rectangle 0.00 0.00 2.00 2.00", info);
    }

    @Test
    public void testListAll() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        String allInfo = clevis.listAll();
        assertTrue(allInfo.contains("c1 circle"));
        assertTrue(allInfo.contains("r1 rectangle"));
    }

    // Delete operation tests
    @Test
    public void testDelete() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.deleteShape("r1");
        assertNull(clevis.shapes.get("r1"));
    }

    @Test
    public void testDeleteGroup() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        List<String> shapes = Arrays.asList("r1", "c1");
        clevis.group("g1", shapes);
        clevis.deleteShape("g1");
        assertNull(clevis.shapes.get("g1"));
        assertNull(clevis.shapes.get("r1"));
        assertNull(clevis.shapes.get("c1"));
    }

    // Error handling tests
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidShapeName() {
        clevis.list("nonexistent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyGroup() {
        clevis.group("g1", new ArrayList<>());
    }
}
