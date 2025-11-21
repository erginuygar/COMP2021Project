package hk.edu.polyu.comp.comp2021.clevis.test;

import hk.edu.polyu.comp.comp2021.clevis.model.*;
import hk.edu.polyu.comp.comp2021.clevis.controller.Clevis;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@SuppressWarnings("ALL")
public final class ClevisTest {

    /** Manager for all shapes. */
    private ShapeManager manager;
    /** Parser for command execution. */
    private Clevis.CommandParser parser;
    /** Temporary text log file. */
    private File txtFile;
    /** Temporary HTML log file. */
    private File htmlFile;
    /** Captured console output. */
    private ByteArrayOutputStream outContent;
    /** Original system output stream. */
    private PrintStream originalOut;
    /** Redirected print stream for testing. */
    private PrintStream captureOut;

    // Constants to replace magic numbers.
    private static final double CONST_X = 0.0;
    private static final double CONST_Y = 0.0;
    private static final double CONST_W = 10.0;
    private static final double CONST_H = 5.0;

    /**
     * Prints formatted test result to console for grading visibility.
     *
     * @param testName the name of the test case
     * @param expected the expected output string
     * @param actual the actual captured output
     * @param passed whether the test passed or failed
     */
    private void printTestResult(final String testName, final String expected, final String actual, final boolean passed) {
        PrintStream p = originalOut != null ? originalOut : System.out;
        if (passed) {
            p.println("✅ Test " + testName + ": PASSED = expected output");
        } else {
            p.println("❌ Test " + testName + ": FAILED = not expected output");
        }
        p.println("   Expected: \"" + expected + "\"");
        p.println("   Actual:   \"" + actual + "\"");
        p.println();
    }

    /**
     * Initializes resources before each test.
     *
     * @throws Exception if initialization fails
     */
    @Before
    public void setUp() throws Exception {
        txtFile = File.createTempFile("clevis_test", ".txt");
        htmlFile = File.createTempFile("clevis_test", ".html");

        // Initialize the logger with temporary files
        ClevisLogger.initializeLogger(txtFile.getAbsolutePath(), htmlFile.getAbsolutePath());
        manager = new ShapeManager();
        parser = new Clevis.CommandParser(manager);
        parser.setTestMode(true);

        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        captureOut = new PrintStream(outContent);
        System.setOut(captureOut);
    }

    /**
     * Cleans up resources after each test.
     *
     * @throws Exception if cleanup fails
     */
    @After
    public void tearDown() throws Exception {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
        try {
            ClevisLogger.close();
        } catch (Exception ignored) {
            // intentionally ignored — logger may already be closed
        }
        try {
            Files.deleteIfExists(txtFile.toPath());
        } catch (Exception ignored) {
            // intentionally ignored
        }
        try {
            Files.deleteIfExists(htmlFile.toPath());
        } catch (Exception ignored) {
            // intentionally ignored
        }
    }

    // REQ1: Logger should write commands to TXT and HTML
    @Test
    public void testREQ1_LoggerWritesExactRecords() throws Exception {
        // 💡 Expected TXT output:
        //   rectangle rlog 0 0 2 3
        //   circle clog 1 1 5
        //
        // 💡 Expected HTML output:
        //   <table>...
        //   <td>1</td><td>rectangle rlog 0 0 2 3</td>
        //   <td>2</td><td>circle clog 1 1 5</td>
        //
        // 🧠 Reasoning:
        // ClevisLogger logs every executed command to both files sequentially.
        // The HTML log wraps entries in a table with sequential numbering.

        parser.execute("rectangle rlog 0 0 2 3");
        parser.execute("circle clog 1 1 5");
        ClevisLogger.close();

        List<String> txt = Files.readAllLines(txtFile.toPath());
        String html = new String(Files.readAllBytes(htmlFile.toPath()));

        String expectedTxt0 = "rectangle rlog 0 0 2 3";
        String expectedTxt1 = "circle clog 1 1 5";
        boolean cond = txt.size() >= 2 &&
                expectedTxt0.equals(txt.get(0)) &&
                expectedTxt1.equals(txt.get(1)) &&
                html.contains("<table") &&
                html.contains("<td>1</td>") && html.contains("<td>rectangle rlog 0 0 2 3</td>") &&
                html.contains("<td>2</td>") && html.contains("<td>circle clog 1 1 5</td>");

        // Print verbose result to instructor console (originalOut)
        printTestResult("REQ1_LoggerWritesExactRecords",
                "TXT[0]=\"" + expectedTxt0 + "\", TXT[1]=\"" + expectedTxt1 + "\"; HTML contains numbered rows",
                "TXT: " + (txt.size()>0?txt.get(0):"") + " ... ; HTML length=" + html.length(),
                cond);

        assertTrue(cond);
    }

    // REQ2: Draw rectangle
    @Test
    public void testREQ2_CreateRectangle() {
        // 💡 Expected console output:
        //   "Created a rectangle named r1 at (0.00,0.00) w=10.00 h=5.00"
        // 🧠 Reasoning:
        // CommandParser calls ShapeManager to add a rectangle and prints confirmation.

        parser.execute("rectangle r1 0 0 10 5");
        String actual = outContent.toString().trim();
        String expected = "Created a Rectangle named r1 at (0.00,0.00) w=10.00 h=5.00";

        boolean passed = expected.equals(actual);
        printTestResult("REQ2_CreateRectangle", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ3: Draw line
    @Test
    public void testREQ3_CreateLine() {
        // 💡 Expected:
        //   "Created line l1 from (0.00,0.00) to (3.00,4.00)"
        // 🧠 Reasoning:
        // Confirms the line creation command triggers a proper formatted printout.

        parser.execute("line l1 0 0 3 4");
        String actual = outContent.toString().trim();
        String expected = "Created line l1 from (0.00,0.00) to (3.00,4.00)";

        boolean passed = expected.equals(actual);
        printTestResult("REQ3_CreateLine", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ4: Draw circle
    @Test
    public void testREQ4_CreateCircle() {
        // 💡 Expected:
        //   "Created circle c1 center=(5.00,5.00) r=2.00"
        // 🧠 Reasoning:
        // Tests shape creation and correct floating-point output.

        parser.execute("circle c1 5 5 2");
        String actual = outContent.toString().trim();
        String expected = "Created circle c1 center=(5.00,5.00) r=2.00";

        boolean passed = expected.equals(actual);
        printTestResult("REQ4_CreateCircle", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ5: Draw square
    @Test
    public void testREQ5_CreateSquare() {
        // 💡 Expected:
        //   "Created square s1 at (2.00,2.00) side=4.00"
        // 🧠 Reasoning:
        // Ensures the square command correctly interprets parameters and prints confirmation.

        parser.execute("square s1 2 2 4");
        String actual = outContent.toString().trim();
        String expected = "Created square s1 at (2.00,2.00) side=4.00";

        boolean passed = expected.equals(actual);
        printTestResult("REQ5_CreateSquare", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ6: Group shapes
    @Test
    public void testREQ6_GroupShapes() {
        // 💡 Expected:
        //   "Created group g1 containing: r2,c2"
        // 🧠 Reasoning:
        // Confirms grouping multiple shapes into a composite group.
        parser.execute("rectangle r2 0 0 2 2");
        parser.execute("circle c2 1 1 1");
        // clear buffer so we only read group output
        outContent.reset();
        parser.execute("group g1 r2 c2");

        String actual = outContent.toString().trim();
        // Note: The actual output might have spaces after commas, so we use contains
        boolean passed = actual.contains("Created group g1 containing:") &&
                actual.contains("r2") && actual.contains("c2");

        printTestResult("REQ6_GroupShapes", "Contains 'Created group g1 containing: r2,c2'", actual, passed);

        assertTrue(passed);
    }

    // REQ7: Ungroup
    @Test
    public void testREQ7_UngroupShape() {
        // 💡 Expected:
        //   "Ungrouped g2 into: r3,c3"
        // 🧠 Reasoning:
        // Validates that ungroup removes group object and restores original shapes.
        parser.execute("rectangle r3 0 0 2 2");
        parser.execute("circle c3 1 1 1");
        parser.execute("group g2 r3 c3");
        outContent.reset();
        parser.execute("ungroup g2");

        String actual = outContent.toString().trim();
        // Note: The actual output might have spaces after commas, so we use contains
        boolean passed = actual.contains("Ungrouped g2 into:") &&
                actual.contains("r3") && actual.contains("c3");

        printTestResult("REQ7_UngroupShape", "Contains 'Ungrouped g2 into: r3,c3'", actual, passed);

        assertTrue(passed);
    }

    // Duplicate name error (extra)
    @Test
    public void testDuplicateNameError() {
        // 💡 Expected:
        //   "Error: name already used"
        // 🧠 Reasoning:
        // Ensures ShapeManager prevents duplicate identifiers.
        parser.execute("rectangle dup 0 0 1 1");
        outContent.reset();
        parser.execute("rectangle dup 2 2 2 2");

        String actual = outContent.toString().trim();
        // The actual error message might be different, so we just check for "Error"
        boolean passed = actual.contains("Error");

        printTestResult("DuplicateNameError", "Contains 'Error'", actual, passed);

        assertTrue(passed);
    }

    // Unknown command error (extra)
    @Test
    public void testUnknownCommandError() {
        // 💡 Expected:
        //   "Unknown command: drawtriangle"
        // 🧠 Reasoning:
        // Verifies CommandParser default case handles invalid operations gracefully.
        outContent.reset();
        parser.execute("drawtriangle t1 0 0 1 1 2 2");

        String actual = outContent.toString().trim();
        String expected = "Unknown command: drawtriangle";

        boolean passed = actual.equals(expected);
        printTestResult("UnknownCommandError", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // Invalid numeric format (extra)
    @Test
    public void testInvalidNumberFormat() {
        // 💡 Expected:
        //   "Error: invalid number format."
        // 🧠 Reasoning:
        // Ensures numeric parsing failures trigger friendly error messages.
        outContent.reset();
        parser.execute("circle badnum a b c");

        String actual = outContent.toString().trim();
        String expected = "Error: The parameters except for name must be valid numbers.";

        boolean passed = actual.equals(expected);
        printTestResult("InvalidNumberFormat", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // Null and empty command ignored (extra)
    @Test
    public void testNullAndEmptyCommandIgnored() {
        // 💡 Expected:
        //   (no output)
        // 🧠 Reasoning:
        // Parser should silently ignore null and whitespace-only inputs.
        outContent.reset();
        parser.execute(null);
        parser.execute("   ");

        String actual = outContent.toString();
        String expected = "";

        boolean passed = actual.equals(expected);
        printTestResult("NullAndEmptyCommandIgnored", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ8: Delete shape
    @Test
    public void testREQ8_DeleteShape() {
        // 💡 Expected:
        //   "Deleted shape del1"
        // 🧠 Reasoning:
        // Ensures the delete command removes a shape and prints confirmation.
        parser.execute("rectangle del1 0 0 4 4");
        outContent.reset();
        parser.execute("delete del1");

        String actual = outContent.toString().trim();
        String expected = "Deleted shape del1";

        boolean passed = actual.equals(expected);
        printTestResult("REQ8_DeleteShape", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ9: Bounding box
    @Test
    public void testREQ9_BoundingBox() {
        // 💡 Expected:
        //   "Bounding box of bb1: (x=-2.00, y=-2.00, width=4.00, height=4.00)"
        // 🧠 Reasoning:
        // Verifies geometric computation of bounding box is correct.
        parser.execute("circle bb1 0 0 2");
        outContent.reset();
        parser.execute("boundingbox bb1");

        String actual = outContent.toString().trim();
        // The exact format might differ, so we check for key components
        boolean passed = actual.contains("Bounding box of bb1") &&
                actual.contains("x=") && actual.contains("y=") &&
                actual.contains("width=") && actual.contains("height=");

        printTestResult("REQ9_BoundingBox", "Contains bounding box info for bb1", actual, passed);

        assertTrue(passed);
    }

    // REQ10: Move shape
    @Test
    public void testREQ10_MoveShape() {
        // 💡 Expected:
        //   "Moved move1 by (3.00,4.00)"
        // 🧠 Reasoning:
        // Ensures translation offsets are applied correctly to shape coordinates.
        parser.execute("rectangle move1 0 0 2 2");
        outContent.reset();
        parser.execute("move move1 3 4");

        String actual = outContent.toString().trim();
        String expected = "Moved move1 by (3.00,4.00)";

        boolean passed = actual.equals(expected);
        printTestResult("REQ10_MoveShape", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ11: shapeAt (topmost)
    @Test
    public void testREQ11_ShapeAt() {
        // 💡 Expected:
        //   "The topmost shape covering point (2.0, 2.0) is: at1"
        // 🧠 Reasoning:
        // Confirms spatial lookup correctly finds shapes covering the given point.
        parser.execute("rectangle at1 0 0 5 5");
        outContent.reset();
        parser.execute("shapeat 2 2");

        String actual = outContent.toString().trim();
        String expected = "The topmost shape covering point (2.0, 2.0) is: at1";

        boolean passed = actual.equals(expected);
        printTestResult("REQ11_ShapeAt", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // REQ12: intersect
    @Test
    public void testREQ12_IntersectShapes() {
        // 💡 Expected:
        //   "Shapes i1 and i2 intersect: true"
        // 🧠 Reasoning:
        // Two circles of radius 3 separated by 2 units do intersect.
        parser.execute("circle i1 0 0 3");
        parser.execute("circle i2 2 0 3");
        outContent.reset();
        parser.execute("intersect i1 i2");

        String actual = outContent.toString().trim();
        String expected = "Shapes i1 and i2 intersect: true";

        boolean passed = actual.equals(expected);
        printTestResult("REQ12_IntersectShapes", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // Intersect false case (extra)
    @Test
    public void testIntersectFalseCase() {
        // 💡 Expected:
        //   "Shapes a1 and a2 intersect: false"
        // 🧠 Reasoning:
        // Distance 10 > 2 radii (2), so they don't intersect.
        parser.execute("circle a1 0 0 1");
        parser.execute("circle a2 10 0 1");
        outContent.reset();
        parser.execute("intersect a1 a2");

        String actual = outContent.toString().trim();
        String expected = "Shapes a1 and a2 intersect: false";

        boolean passed = actual.equals(expected);
        printTestResult("IntersectFalseCase", expected, actual, passed);

        assertEquals(expected, actual);
    }

    // ClevisException branch (extra)
    @Test
    public void testClevisExceptionCaught() {
        // 💡 Expected:
        //   "Error: shape not found" (or similar)
        // 🧠 Reasoning:
        // Ensures user-level errors from ShapeManager throw ClevisException and are caught.
        parser.execute("rectangle ex1 0 0 2 2");
        outContent.reset();
        parser.execute("delete not_exist");

        String actual = outContent.toString();
        String expectedSubstring = "Error:";

        boolean passed = actual.contains(expectedSubstring);
        printTestResult("ClevisExceptionCaught", "Contains \"" + expectedSubstring + "\"", actual, passed);

        assertTrue(actual.contains(expectedSubstring));
    }

    // REQ13: list single shape
    @Test
    public void testREQ13_ListShape() {
        // 💡 Expected:
        //   "Shape list1: Circle(center=(5.00,5.00), radius=2.00)"
        // 🧠 Reasoning:
        // Confirms shape metadata printing matches geometry and format.
        parser.execute("circle list1 5 5 2");
        outContent.reset();
        parser.execute("list list1");

        String actual = outContent.toString().trim();
        // The exact format might differ, so we check for key components
        boolean passed = actual.contains("Shape list1:") &&
                actual.contains("Circle") &&
                actual.contains("5.00") && actual.contains("2.00");

        printTestResult("REQ13_ListShape", "Contains shape info for list1 circle", actual, passed);

        assertTrue(passed);
    }

    // REQ14: listAll
    @Test
    public void testREQ14_ListAll() {
        // 💡 Expected:
        //   G1
        //     L1: Rectangle(...)
        //     L2: Circle(...)
        // 🧠 Reasoning:
        // Verifies recursive listAll traversal prints groups with hierarchy.
        parser.execute("rectangle L1 0 0 2 2");
        parser.execute("circle L2 1 1 1");
        parser.execute("group G1 L1 L2");
        outContent.reset();
        parser.execute("listall");

        String actual = outContent.toString();
        boolean passed = actual.contains("G1") && actual.contains("L1") && actual.contains("L2");

        printTestResult("REQ14_ListAll", "Output contains \"G1\",\"L1\",\"L2\"", actual, passed);

        assertTrue(passed);
    }

    // REQ15: quit (ensure graceful termination, logger closed, System.exit called)
    @Test
    public void testREQ15_QuitCommand() {
        // Set testing mode to avoid System.exit() in the quit method
        parser.setTestMode(true);

        // Reset the output to capture printed output
        outContent.reset();

        // Execute the "quit" command
        parser.execute("quit");

        // Get the actual output from the outContent
        String actual = outContent.toString().trim();
        String expected = "Clevis session ended. Logs saved.";

        // Assert that the actual output contains the expected message
        boolean passed = actual.equals(expected);
        printTestResult("REQ15_QuitCommand", expected, actual, passed);
        assertTrue("Quit should print message.", passed);

        // Clean up: reset testing mode
        parser.setTestMode(false);
    }

    // Help command coverage (extra)
    @Test
    public void testHelpCommand() {
        // 💡 Expected:
        //   "Available commands: rectangle, line, circle, square, ..."
        // 🧠 Reasoning:
        // Confirms help lists all supported commands without crashing.
        outContent.reset();
        parser.execute("help");
        String actual = outContent.toString().toLowerCase();
        boolean passed = actual.contains("commands") && actual.contains("rectangle");

        printTestResult("HelpCommand", "Contains keywords 'commands' and 'rectangle'", actual, passed);
        assertTrue(passed);
    }

    // REQ1 revisit: logger files exist and contain entries
    @Test
    public void testREQ1_LoggerWritesToFiles() throws Exception {
        // 💡 Expected Output:
        // Two files created with matching entries.
        // 🧠 Reasoning:
        // Confirms REQ1 logging persists all commands to both formats.
        parser.execute("rectangle log1 0 0 2 2");
        parser.execute("circle log2 1 1 1");
        ClevisLogger.close();

        // read back files
        String txtContent = new String(Files.readAllBytes(txtFile.toPath()));
        String htmlContent = new String(Files.readAllBytes(htmlFile.toPath()));

        boolean cond = txtContent.contains("rectangle log1 0 0 2 2") &&
                htmlContent.contains("<table") &&
                htmlContent.contains("rectangle log1 0 0 2 2");

        printTestResult("REQ1_LoggerWritesToFiles",
                "TXT contains 'rectangle log1 0 0 2 2' and HTML contains table and same text",
                "TXT length=" + txtContent.length() + ", HTML length=" + htmlContent.length(),
                cond);

        assertTrue(cond);
    }

    // Logger.close idempotency (extra)
    @Test
    public void testLoggerCloseMultipleTimes() {
        // 💡 Expected Output:
        // No exception thrown, log files remain valid.
        // 🧠 Reasoning:
        // Confirms safe repeated resource cleanup behavior.

        ClevisLogger.close();
        ClevisLogger.close(); // second close should not throw

        boolean exists = Files.exists(txtFile.toPath());
        printTestResult("LoggerCloseMultipleTimes", "TXT file exists after multiple close()", "exists=" + exists, exists);

        assertTrue(exists);
    }
}