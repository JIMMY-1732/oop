package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.*;
import hk.edu.polyu.comp.comp2021.clevis.model.shapes.Group;
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

    // ==================== Basic Shape Creation Tests ====================
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

    @Test
    public void testRectangleWithDecimalValues() {
        Rectangle rect = clevis.rectangle("rect2", 1.5, 2.5, 3.7, 4.2);
        assertEquals("rect2 rectangle 1.50 2.50 3.70 4.20", rect.listInfo());
    }

    @Test
    public void testCircleWithLargeRadius() {
        Circle circle = clevis.circle("bigCircle", 100, 100, 500);
        assertEquals("bigCircle circle 100.00 100.00 500.00", circle.listInfo());
    }

    @Test
    public void testLineVertical() {
        Line line = clevis.line("vLine", 5, 0, 5, 10);
        assertEquals("vLine line 5.00 0.00 5.00 10.00", line.listInfo());
    }

    @Test
    public void testLineHorizontal() {
        Line line = clevis.line("hLine", 0, 5, 10, 5);
        assertEquals("hLine line 0.00 5.00 10.00 5.00", line.listInfo());
    }

    // ==================== Invalid Input Tests ====================
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateShapeName() {
        clevis.rectangle("shape1", 0, 0, 5, 3);
        clevis.circle("shape1", 1, 1, 2); // Should throw exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRectangleWidth() {
        clevis.rectangle("rect2", 0, 0, -1, 3); // Negative width
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRectangleHeight() {
        clevis.rectangle("rect3", 0, 0, 5, -2); // Negative height
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCircleRadius() {
        clevis.circle("circ1", 0, 0, -5); // Negative radius
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroRectangleWidth() {
        clevis.rectangle("rect4", 0, 0, 0, 5); // Zero width
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroSquareSide() {
        clevis.square("sq1", 0, 0, 0); // Zero side length
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeSquareSide() {
        clevis.square("sq2", 0, 0, -3); // Negative side length
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyShapeName() {
        clevis.rectangle("", 0, 0, 5, 3); // Empty name
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullShapeName() {
        clevis.rectangle(null, 0, 0, 5, 3); // Null name
    }

    // ==================== Group Operations Tests ====================
    @Test
    public void testGroupCreation() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        List<String> shapes = Arrays.asList("r1", "c1");
        Group group = clevis.group("g1", shapes);
        assertTrue(group.listInfo().contains("g1 group r1 c1"));
    }

    @Test
    public void testGroupMultipleShapes() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.line("l1", 0, 0, 5, 5);
        clevis.square("s1", 2, 2, 3);
        List<String> shapes = Arrays.asList("r1", "c1", "l1", "s1");
        Group group = clevis.group("g1", shapes);
        assertTrue(group.listInfo().contains("r1"));
        assertTrue(group.listInfo().contains("c1"));
        assertTrue(group.listInfo().contains("l1"));
        assertTrue(group.listInfo().contains("s1"));
    }

    @Test
    public void testNestedGroups() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.group("g1", Arrays.asList("r1", "c1"));

        clevis.line("l1", 0, 0, 5, 5);
        clevis.group("g2", Arrays.asList("g1", "l1"));

        assertNotNull(clevis.shapes.get("g2"));
    }

    @Test
    public void testUngroup() {
        try {
            clevis.rectangle("r1", 0, 0, 2, 2);
            clevis.circle("c1", 1, 1, 1);
            List<String> shapes = Arrays.asList("r1", "c1");
            Group g1 = clevis.group("g1", shapes);
            clevis.ungroup("g1");

            g1.getShapes().forEach(System.out::println);
            assertTrue(shapes.contains("r1") && shapes.contains("c1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    public void testUngroupRestoresIndividualShapes() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.group("g1", Arrays.asList("r1", "c1"));
        clevis.ungroup("g1");

        assertNotNull(clevis.shapes.get("r1"));
        assertNotNull(clevis.shapes.get("c1"));
        assertNull(clevis.shapes.get("g1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyGroup() {
        clevis.group("g1", new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupWithNonexistentShape() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.group("g1", Arrays.asList("r1", "nonexistent"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupDuplicateName() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.group("r1", Arrays.asList("r1")); // Using existing name
    }

    // ==================== Move Operation Tests ====================
    @Test
    public void testMoveShape() {
        Rectangle rect = clevis.rectangle("rect1", 0, 0, 2, 2);
        clevis.move("rect1", 3, 4);
        BoundingBox bbox = clevis.boundingBox("rect1");
        assertEquals(3.0, bbox.x, 0.01);
        assertEquals(4.0, bbox.y, 0.01);
    }

    @Test
    public void testMoveShapeNegativeOffset() {
        clevis.rectangle("rect1", 10, 10, 2, 2);
        clevis.move("rect1", -5, -3);
        BoundingBox bbox = clevis.boundingBox("rect1");
        assertEquals(5.0, bbox.x, 0.01);
        assertEquals(7.0, bbox.y, 0.01);
    }

    @Test
    public void testMoveCircle() {
        clevis.circle("c1", 5, 5, 3);
        clevis.move("c1", 2, 3);
        BoundingBox bbox = clevis.boundingBox("c1");
        assertEquals(4.0, bbox.x, 0.01); // center was 5, moved by 2, bbox starts at 7-3=4
        assertEquals(5.0, bbox.y, 0.01);
    }

    @Test
    public void testMoveGroup() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.group("g1", Arrays.asList("r1", "c1"));
        clevis.move("g1", 5, 5);

        BoundingBox bbox = clevis.boundingBox("g1");
        assertNotNull(bbox);
    }

    @Test
    public void testMoveLine() {
        clevis.line("l1", 0, 0, 5, 5);
        clevis.move("l1", 10, 10);
        BoundingBox bbox = clevis.boundingBox("l1");
        assertEquals(10.0, bbox.x, 0.01);
        assertEquals(10.0, bbox.y, 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveNonexistentShape() {
        clevis.move("doesntExist", 5, 5);
    }

    // ==================== Intersection Tests ====================
    @Test
    public void testIntersectionOverlappingRectangles() {
        clevis.rectangle("r1", 0, 0, 4, 4);
        clevis.rectangle("r2", 2, 2, 4, 4);
        assertTrue(clevis.intersect("r1", "r2"));
    }

    @Test
    public void testNoIntersectionSeparateRectangles() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.rectangle("r2", 4, 4, 2, 2);
        assertFalse(clevis.intersect("r1", "r2"));
    }

    @Test
    public void testIntersectionCircles() {
        clevis.circle("c1", 0, 0, 3);
        clevis.circle("c2", 4, 0, 3);
        assertTrue(clevis.intersect("c1", "c2"));
    }

    @Test
    public void testNoIntersectionCircles() {
        clevis.circle("c1", 0, 0, 2);
        clevis.circle("c2", 10, 0, 2);
        assertFalse(clevis.intersect("c1", "c2"));
    }

    @Test
    public void testIntersectionCircleRectangle() {
        clevis.circle("c1", 5, 5, 3);
        clevis.rectangle("r1", 6, 6, 2, 2);
        assertTrue(clevis.intersect("c1", "r1"));
    }

    @Test
    public void testIntersectionLineRectangle() {
        clevis.line("l1", 0, 0, 10, 10);
        clevis.rectangle("r1", 3, 3, 2, 2);
        assertTrue(clevis.intersect("l1", "r1"));
    }

    @Test
    public void testIntersectionLineCircle() {
        clevis.line("l1", 0, 0, 10, 0);
        clevis.circle("c1", 5, 0, 2);
        assertTrue(clevis.intersect("l1", "c1"));
    }

    @Test
    public void testIntersectionTouchingShapes() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.rectangle("r2", 2, 0, 2, 2); // Touching edge
        assertTrue(clevis.intersect("r1", "r2"));
    }

    @Test
    public void testIntersectionContainedShapes() {
        clevis.rectangle("r1", 0, 0, 10, 10);
        clevis.circle("c1", 5, 5, 2); // Circle inside rectangle
        assertTrue(clevis.intersect("r1", "c1"));
    }

    @Test
    public void testIntersectionSquares() {
        clevis.square("s1", 0, 0, 4);
        clevis.square("s2", 2, 2, 4);
        assertTrue(clevis.intersect("s1", "s2"));
    }

    // ==================== ShapeAt Tests ====================
    @Test
    public void testShapeAt() {
        clevis.rectangle("r1", 1, 1, 4, 4);
        clevis.circle("c1", 6, 6, 2);
        assertEquals("r1", clevis.shapeAt(1, 1));
        assertEquals("c1", clevis.shapeAt(6, 8));
    }

    @Test
    public void testShapeAtOnEdge() {
        clevis.rectangle("r1", 0, 0, 10, 10);
        String result = clevis.shapeAt(0, 5); // On left edge
        assertEquals("r1", result);
    }

    @Test
    public void testShapeAtMultipleShapes() {
        clevis.rectangle("r1", 0, 0, 10, 10);
        clevis.circle("c1", 5, 5, 2); // Overlapping
        String result = clevis.shapeAt(5, 5);
        assertEquals("c1", result); // Most recent shape
    }

    @Test
    public void testShapeAtNoShape() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        assertNull(clevis.shapeAt(100, 100));
    }

    @Test
    public void testShapeAtLine() {
        clevis.line("l1", 0, 0, 10, 10);
        assertNotNull(clevis.shapeAt(5, 5)); // On the line
    }

    @Test
    public void testShapeAtZIndexOrder() {
        clevis.rectangle("r1", 0, 0, 10, 10);
        clevis.rectangle("r2", 5, 5, 10, 10);
        clevis.rectangle("r3", 7, 7, 5, 5);
        String result = clevis.shapeAt(8, 8);
        assertEquals("r3", result); // Topmost shape
    }

    // ==================== Bounding Box Tests ====================
    @Test
    public void testBoundingBox() {
        clevis.rectangle("r1", 1, 1, 4, 3);
        BoundingBox bbox = clevis.boundingBox("r1");
        assertEquals(1.0, bbox.x, 0.01);
        assertEquals(1.0, bbox.y, 0.01);
        assertEquals(4.0, bbox.w, 0.01);
        assertEquals(3.0, bbox.h, 0.01);
    }

    @Test
    public void testBoundingBoxCircle() {
        clevis.circle("c1", 5, 5, 3);
        BoundingBox bbox = clevis.boundingBox("c1");
        assertEquals(2.0, bbox.x, 0.01); // 5-3
        assertEquals(2.0, bbox.y, 0.01);
        assertEquals(6.0, bbox.w, 0.01); // diameter
        assertEquals(6.0, bbox.h, 0.01);
    }

    @Test
    public void testBoundingBoxLine() {
        clevis.line("l1", 2, 3, 8, 9);
        BoundingBox bbox = clevis.boundingBox("l1");
        assertEquals(2.0, bbox.x, 0.01);
        assertEquals(3.0, bbox.y, 0.01);
        assertEquals(6.0, bbox.w, 0.01);
        assertEquals(6.0, bbox.h, 0.01);
    }

    @Test
    public void testBoundingBoxSquare() {
        clevis.square("s1", 3, 4, 5);
        BoundingBox bbox = clevis.boundingBox("s1");
        assertEquals(3.0, bbox.x, 0.01);
        assertEquals(4.0, bbox.y, 0.01);
        assertEquals(5.0, bbox.w, 0.01);
        assertEquals(5.0, bbox.h, 0.01);
    }

    @Test
    public void testBoundingBoxGroup() {
        clevis.rectangle("r1", 0, 0, 5, 5);
        clevis.circle("c1", 10, 10, 2);
        clevis.group("g1", Arrays.asList("r1", "c1"));
        BoundingBox bbox = clevis.boundingBox("g1");
        assertNotNull(bbox);
        assertEquals(0.0, bbox.x, 0.01);
        assertEquals(0.0, bbox.y, 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBoundingBoxNonexistent() {
        clevis.boundingBox("doesntExist");
    }

    // ==================== List Operations Tests ====================
    @Test
    public void testList() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        String info = clevis.list("r1");
        assertEquals("r1 rectangle 0.00 0.00 2.00 2.00", info);
    }

    @Test
    public void testListCircle() {
        clevis.circle("c1", 5, 5, 3);
        String info = clevis.list("c1");
        assertEquals("c1 circle 5.00 5.00 3.00", info);
    }

    @Test
    public void testListLine() {
        clevis.line("l1", 0, 0, 5, 5);
        String info = clevis.list("l1");
        assertEquals("l1 line 0.00 0.00 5.00 5.00", info);
    }

    @Test
    public void testListSquare() {
        clevis.square("s1", 2, 3, 4);
        String info = clevis.list("s1");
        assertEquals("s1 square 2.00 3.00 4.00", info);
    }

    @Test
    public void testListGroup() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.group("g1", Arrays.asList("r1", "c1"));
        String info = clevis.list("g1");
        assertTrue(info.contains("g1 group"));
        assertTrue(info.contains("r1"));
        assertTrue(info.contains("c1"));
    }

    @Test
    public void testListAll() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        String allInfo = clevis.listAll();
        assertTrue(allInfo.contains("c1 circle"));
        assertTrue(allInfo.contains("r1 rectangle"));
    }

    @Test
    public void testListAllEmpty() {
        String allInfo = clevis.listAll();
        assertTrue(allInfo.isEmpty() || allInfo.trim().isEmpty());
    }

    @Test
    public void testListAllWithGroups() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.group("g1", Arrays.asList("r1", "c1"));
        String allInfo = clevis.listAll();
        assertTrue(allInfo.contains("g1 group"));
    }

    @Test
    public void testListAllZIndexOrder() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.line("l1", 0, 0, 5, 5);
        String allInfo = clevis.listAll();
        int indexL1 = allInfo.indexOf("l1");
        int indexC1 = allInfo.indexOf("c1");
        int indexR1 = allInfo.indexOf("r1");
        assertTrue(indexL1 < indexC1); // More recent first
        assertTrue(indexC1 < indexR1);
    }

    // ==================== Delete Operation Tests ====================
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

    @Test
    public void testDeleteCircle() {
        clevis.circle("c1", 5, 5, 3);
        clevis.deleteShape("c1");
        assertNull(clevis.shapes.get("c1"));
    }

    @Test
    public void testDeleteLine() {
        clevis.line("l1", 0, 0, 5, 5);
        clevis.deleteShape("l1");
        assertNull(clevis.shapes.get("l1"));
    }

    @Test
    public void testDeleteSquare() {
        clevis.square("s1", 2, 2, 4);
        clevis.deleteShape("s1");
        assertNull(clevis.shapes.get("s1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNonexistent() {
        clevis.deleteShape("doesntExist");
    }

    @Test
    public void testDeleteMultipleShapes() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.line("l1", 0, 0, 5, 5);

        clevis.deleteShape("r1");
        clevis.deleteShape("c1");
        clevis.deleteShape("l1");

        assertNull(clevis.shapes.get("r1"));
        assertNull(clevis.shapes.get("c1"));
        assertNull(clevis.shapes.get("l1"));
    }

    // ==================== Error Handling Tests ====================
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidShapeName() {
        clevis.list("nonexistent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIntersectNonexistentShape() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.intersect("r1", "nonexistent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUngroupNonexistentGroup() {
        clevis.ungroup("nonexistent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUngroupNonGroup() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.ungroup("r1"); // r1 is not a group
    }

    // ==================== Edge Case Tests ====================
    @Test
    public void testVerySmallRectangle() {
        Rectangle rect = clevis.rectangle("tiny", 0, 0, 0.01, 0.01);
        assertNotNull(rect);
    }

    @Test
    public void testVeryLargeRectangle() {
        Rectangle rect = clevis.rectangle("huge", 0, 0, 10000, 10000);
        assertNotNull(rect);
    }

    @Test
    public void testNegativeCoordinates() {
        Rectangle rect = clevis.rectangle("neg", -10, -10, 5, 5);
        BoundingBox bbox = clevis.boundingBox("neg");
        assertEquals(-10.0, bbox.x, 0.01);
        assertEquals(-10.0, bbox.y, 0.01);
    }


    @Test
    public void testDiagonalLine() {
        Line line = clevis.line("diag", 0, 0, 100, 100);
        BoundingBox bbox = clevis.boundingBox("diag");
        assertEquals(100.0, bbox.w, 0.01);
        assertEquals(100.0, bbox.h, 0.01);
    }

    @Test
    public void testManyShapes() {
        for (int i = 0; i < 100; i++) {
            clevis.rectangle("r" + i, i, i, 2, 2);
        }
        String allInfo = clevis.listAll();
        assertTrue(allInfo.contains("r0"));
        assertTrue(allInfo.contains("r99"));
    }

    @Test
    public void testComplexGroupHierarchy() {
        clevis.rectangle("r1", 0, 0, 2, 2);
        clevis.circle("c1", 1, 1, 1);
        clevis.group("g1", Arrays.asList("r1", "c1"));

        clevis.line("l1", 0, 0, 5, 5);
        clevis.square("s1", 2, 2, 3);
        clevis.group("g2", Arrays.asList("l1", "s1"));

        clevis.group("g3", Arrays.asList("g1", "g2"));

        assertNotNull(clevis.shapes.get("g3"));
        String info = clevis.list("g3");
        assertTrue(info.contains("g1"));
        assertTrue(info.contains("g2"));
    }
}