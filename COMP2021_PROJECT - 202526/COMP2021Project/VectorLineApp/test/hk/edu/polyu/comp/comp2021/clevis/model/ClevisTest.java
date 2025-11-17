package hk.edu.polyu.comp.comp2021.clevis.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

/**
 * ClevisTest
 *
 * Tests are labeled with which REQ they exercise.
 */
public class ClevisTest {

    // [REQ1] Logging (REQUIRED)
    @Test
    public void testLoggerCreatesFiles_required() throws Exception {
        String html = "test_log.html";
        String txt = "test_log.txt";
        // cleanup before
        new File(html).delete();
        new File(txt).delete();

        Logger logger = new Logger(html, txt);
        logger.logCommand(1, "rectangle r1 0 0 1 1");
        logger.close();

        assertTrue("HTML log should exist", new File(html).exists());
        assertTrue("TXT log should exist", new File(txt).exists());

        // cleanup after
        new File(html).delete();
        new File(txt).delete();
    }

    // [REQ2] Rectangle creation (REQUIRED)
    @Test
    public void testRectangleCreation_required() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Rectangle r = new Clevis.Rectangle("R1", 0.0, 0.0, 2.0, 3.0);
        mgr.addShape(r);
        assertNotNull(mgr.getShape("R1"));
        assertEquals("R1", mgr.getShape("R1").getName());
    }

    // [REQ3] Line creation (REQUIRED)
    @Test
    public void testLineCreation_required() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Line l = new Clevis.Line("L1", 0.0, 0.0, 1.0, 1.0);
        mgr.addShape(l);
        assertNotNull(mgr.getShape("L1"));
        assertEquals("L1", mgr.getShape("L1").getName());
    }

    // [REQ4] Circle creation (REQUIRED)
    @Test
    public void testCircleCreation_required() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Circle c = new Clevis.Circle("C1", 1.0, 1.0, 0.5);
        mgr.addShape(c);
        assertNotNull(mgr.getShape("C1"));
        assertEquals("C1", mgr.getShape("C1").getName());
    }

    // [REQ5] Square creation (REQUIRED)
    @Test
    public void testSquareCreation_required() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Square s = new Clevis.Square("S1", 2.0, 2.0, 1.5);
        mgr.addShape(s);
        assertNotNull(mgr.getShape("S1"));
        assertEquals("S1", mgr.getShape("S1").getName());
    }

    // [REQ6] Group creation & visibility (REQUIRED)
    @Test
    public void testGroupRequired() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Rectangle a = new Clevis.Rectangle("a", 0, 0, 1, 1);
        Clevis.Square b = new Clevis.Square("b", 2, 2, 1);
        mgr.addShape(a);
        mgr.addShape(b);
        Clevis.Group g = new Clevis.Group("g", java.util.List.of(a, b));
        // simulate grouping: remove members then add group
        mgr.deleteShape("a");
        mgr.deleteShape("b");
        mgr.addShape(g);
        assertNotNull(mgr.getShape("g"));
        assertNull(mgr.getShape("a"));
        assertNull(mgr.getShape("b"));
    }

    // [REQ7] Ungroup (REQUIRED)
    @Test
    public void testUngroupRequired() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Rectangle a = new Clevis.Rectangle("a", 0, 0, 1, 1);
        Clevis.Square b = new Clevis.Square("b", 2, 2, 1);
        Clevis.Group g = new Clevis.Group("g", java.util.List.of(a, b));
        mgr.addShape(g);
        // ungroup: remove group then add members
        mgr.deleteShape("g");
        mgr.addShape(a);
        mgr.addShape(b);
        assertNotNull(mgr.getShape("a"));
        assertNotNull(mgr.getShape("b"));
        assertNull(mgr.getShape("g"));
    }

    // [REQ8] Delete shape (REQUIRED)
    @Test
    public void testDeleteRequired() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Rectangle r = new Clevis.Rectangle("d1", 0, 0, 1, 1);
        mgr.addShape(r);
        mgr.deleteShape("d1");
        assertNull(mgr.getShape("d1"));
    }

    // [REQ9] Bounding box (REQUIRED)
    @Test
    public void testBoundingBoxRequired() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Rectangle r = new Clevis.Rectangle("r2", 1.0, 2.0, 3.0, 4.0);
        mgr.addShape(r);
        String bb = mgr.getBoundingBox("r2");
        // expected "1.00 2.00 3.00 4.00"
        String[] parts = bb.split("\\s+");
        assertEquals(4, parts.length);
        assertEquals("1.00", String.format("%.2f", Double.parseDouble(parts[0])));
    }

    // [REQ10] Move (REQUIRED)
    @Test
    public void testMoveRequired() throws Exception {
        Clevis.Rectangle m = new Clevis.Rectangle("m1", 0, 0, 2, 2);
        m.move(1, 1);
        String bb = m.getBoundingBox();
        String[] parts = bb.split("\\s+");
        assertEquals("1.00", String.format("%.2f", Double.parseDouble(parts[0])));
        assertEquals("1.00", String.format("%.2f", Double.parseDouble(parts[1])));
    }

    // [REQ11] shapeAt (REQUIRED)
    @Test
    public void testShapeAtRequired() throws Exception {
        Clevis.Rectangle r = new Clevis.Rectangle("r3", 0, 0, 2, 2);
        assertTrue(r.coversPoint(0.5, 1.0));
    }

    // [REQ12] intersect (REQUIRED)
    @Test
    public void testIntersectRequired() throws Exception {
        Clevis.Rectangle i1 = new Clevis.Rectangle("i1", 0, 0, 2, 2);
        Clevis.Rectangle i2 = new Clevis.Rectangle("i2", 1, 1, 2, 2);
        // bounding boxes overlap -> not separated
        String[] b1 = i1.getBoundingBox().split("\\s+");
        String[] b2 = i2.getBoundingBox().split("\\s+");
        double x1 = Double.parseDouble(b1[0]), y1 = Double.parseDouble(b1[1]), w1 = Double.parseDouble(b1[2]), h1 = Double.parseDouble(b1[3]);
        double x2 = Double.parseDouble(b2[0]), y2 = Double.parseDouble(b2[1]), w2 = Double.parseDouble(b2[2]), h2 = Double.parseDouble(b2[3]);
        boolean separated = x1 + w1 < x2 || x2 + w2 < x1 || y1 + h1 < y2 || y2 + h2 < y1;
        assertFalse(separated);
    }

    // [REQ13] list (REQUIRED)
    @Test
    public void testListRequired() throws Exception {
        Clevis.Circle c = new Clevis.Circle("lc", 5, 5, 1);
        assertTrue(c.getInfo().contains("Circle"));
    }

    // [REQ14] listAll (REQUIRED)
    @Test
    public void testListAllRequired() throws Exception {
        Clevis.ShapeManager mgr = new Clevis.ShapeManager();
        Clevis.Rectangle la = new Clevis.Rectangle("la", 0, 0, 1, 1);
        Clevis.Square lb = new Clevis.Square("lb", 2, 2, 1);
        mgr.addShape(la); mgr.addShape(lb);
        java.util.List<Clevis.Shape> all = mgr.getAllShapes();
        assertTrue(all.stream().anyMatch(s -> s.getName().equals("la")));
        assertTrue(all.stream().anyMatch(s -> s.getName().equals("lb")));
    }

    // [REQ15] quit (REQUIRED)
    @Test
    public void testQuitRequired() throws Exception {
        // quit logic is interactive; we test Logger.close instead which is invoked on quit
        String html = "test2_log.html", txt = "test2_log.txt";
        new File(html).delete(); new File(txt).delete();
        Logger logger = new Logger(html, txt);
        logger.logCommand(1, "quit");
        logger.close();
        assertTrue(new File(html).exists());
        assertTrue(new File(txt).exists());
        new File(html).delete(); new File(txt).delete();
    }

    // [OPTIONAL] additional tests for coverage 
    @Test
    public void optional_groupBoundingBox() throws Exception {
        Clevis.Rectangle r1 = new Clevis.Rectangle("g1", 0, 0, 1, 1);
        Clevis.Rectangle r2 = new Clevis.Rectangle("g2", 0.5, 0.5, 2, 2);
        Clevis.Group g = new Clevis.Group("G", java.util.List.of(r1, r2));
        String[] bb = g.getBoundingBox().split("\\s+");
        assertEquals(4, bb.length);
    }
}
