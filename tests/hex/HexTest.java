package hex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HexTest {

    @Test
    void testDefaultConstructor() {
        Hex hex = new Hex();
        assertEquals(0, hex.I(), "I coordinate should be 0");
        assertEquals(0, hex.J(), "J coordinate should be 0");
        assertEquals(0, hex.K(), "K coordinate should be 0");
        assertEquals(0, hex.getLineI(), "Line I should be 0");
        assertEquals(0, hex.getLineJ(), "Line J should be 0");
        assertEquals(0, hex.getLineK(), "Line K should be 0");
    }

    @Test
    void testCoordinateConstructor() {
        Hex hex = new Hex(5, 2);
        assertEquals(5, hex.I(), "I coordinate should be 5");
        assertEquals(7, hex.J(), "J coordinate should be 7");
        assertEquals(2, hex.K(), "K coordinate should be 2");
        assertEquals(3, hex.getLineI(), "Line I should be 3");
        assertEquals(1, hex.getLineJ(), "Line J should be 1");
        assertEquals(4, hex.getLineK(), "Line K should be 4");
    }

    @Test
    void testStaticHexFactory() {
        Hex hex = Hex.hex();
        assertEquals(0, hex.I(), "I coordinate should be 0");
        assertEquals(0, hex.J(), "J coordinate should be 0");
        assertEquals(0, hex.K(), "K coordinate should be 0");
    }

    @Test
    void testHexLineFactory() {
        Hex hex = Hex.hex(1, 2);
        assertEquals(3, hex.I(), "I coordinate should be 3");
        assertEquals(3, hex.J(), "J coordinate should be 3");
        assertEquals(0, hex.K(), "K coordinate should be 0");
        assertEquals(1, hex.getLineI(), "Line I should be 1");
        assertEquals(1, hex.getLineJ(), "Line J should be 1");
        assertEquals(2, hex.getLineK(), "Line K should be 2");
    }

    @Test
    void testLineCoordinates() {
        Hex hex = new Hex(3, 6);
        assertEquals(5, hex.getLineI(), "Line I should be 5");
        assertEquals(-1, hex.getLineJ(), "Line J should be -1");
        assertEquals(4, hex.getLineK(), "Line K should be 4");
        assertEquals("{I = 5, J = -1, K = 4}", hex.getLines(), "Line string should match");
    }

    @Test
    void testInLineChecks() {
        Hex hex1 = new Hex(3, 6);
        Hex hex2 = new Hex(0, 3);
        assertTrue(hex1.inLineI(5), "hex1 should be in I line 5");
        assertTrue(hex1.inLineJ(-1), "hex1 should be in J line -1");
        assertTrue(hex1.inLineK(4), "hex1 should be in K line 4");
        assertFalse(hex1.inLineI(hex2), "hex1 and hex2 not in same I line");
        assertTrue(hex1.inLineJ(hex2), "hex1 and hex2 in same J line");
        assertFalse(hex1.inLineK(hex2), "hex1 and hex2 not in same K line");
    }

    @Test
    void testAdjacency() {
        Hex hex1 = Hex.hex(0, 0);
        Hex hex2 = Hex.hex(0, 1); // Front I
        Hex hex3 = Hex.hex(1, 1);  // Front J
        Hex hex4 = Hex.hex(1, 0); // Front K
        assertTrue(hex2.frontI(hex1), "hex1 should be front I of hex2");
        assertTrue(hex3.frontJ(hex1), "hex1 should be front J of hex3");
        assertTrue(hex4.frontK(hex1), "hex1 should be front K of hex4");
        assertTrue(hex1.adjacent(hex2), "hex1 should be adjacent to hex2");
        assertTrue(hex1.backI(hex2), "hex2 should be back I of hex1");
    }

    @Test
    void testMovement() {
        Hex hex = new Hex(0, 0);
        hex.moveI(1);
        assertEquals(2, hex.I(), "I should be 2 after moveI");
        assertEquals(-1, hex.K(), "K should be -1 after moveI");

        hex.moveJ(1);
        assertEquals(3, hex.I(), "I should be 3 after moveJ");
        assertEquals(0, hex.K(), "K should be 0 after moveJ");

        hex.moveK(1);
        assertEquals(2, hex.I(), "I should be 2 after moveK");
        assertEquals(2, hex.K(), "K should be 2 after moveK");
    }

    @Test
    void testShift() {
        Hex hex = new Hex(0, 0);
        Hex shiftedI = hex.shiftI(1);
        Hex shiftedJ = hex.shiftJ(1);
        Hex shiftedK = hex.shiftK(1);

        assertEquals(2, shiftedI.I(), "Shifted I should be 2");
        assertEquals(-1, shiftedI.K(), "Shifted K should be -1");
        assertEquals(1, shiftedJ.I(), "Shifted I should be 1");
        assertEquals(1, shiftedJ.K(), "Shifted K should be 1");
        assertEquals(-1, shiftedK.I(), "Shifted I should be -1");
        assertEquals(2, shiftedK.K(), "Shifted K should be 2");
    }

    @Test
    void testAddSubtract() {
        Hex hex1 = new Hex(2, 3);
        Hex hex2 = new Hex(1, 1);
        Hex sum = hex1.add(hex2);
        Hex diff = hex1.subtract(hex2);

        assertEquals(3, sum.I(), "Sum I should be 3");
        assertEquals(4, sum.K(), "Sum K should be 4");
        assertEquals(1, diff.I(), "Diff I should be 1");
        assertEquals(2, diff.K(), "Diff K should be 2");
    }

    @Test
    void testRectangularConversion() {
        Hex hex = new Hex(3, 6);
        double halfSin60 = Math.sqrt(3) / 4;
        assertEquals(halfSin60 * (3 + 6), hex.X(), "X coordinate conversion incorrect");
        assertEquals((3 - 6) / 4.0, hex.Y(), "Y coordinate conversion incorrect");
    }

    @Test
    void testInRange() {
        Hex hex = Hex.hex(1, 1);
        assertTrue(hex.inRange(2), "Hex should be in range 2");
        assertFalse(hex.inRange(1), "Hex should not be in range 1");
    }

    @Test
    void testEquals() {
        Hex hex1 = new Hex(2, 3);
        Hex hex2 = new Hex(2, 3);
        Hex hex3 = new Hex(3, 2);
        assertTrue(hex1.equals(hex2), "Equal hexes should be equal");
        assertFalse(hex1.equals(hex3), "Different hexes should not be equal");
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        Hex hex = new Hex(2, 3);
        Hex cloned = hex.clone();
        assertEquals(hex.I(), cloned.I(), "Cloned I should match");
        assertEquals(hex.K(), cloned.K(), "Cloned K should match");
        assertNotSame(hex, cloned, "Cloned object should be different instance");
    }

    @Test
    void testToString() {
        Hex hex = new Hex(3, 6);
        String expected = "{hex I,J,K = {3, 9, 6}, Line I,J,K = {5, -1, 4}, Rect X,Y = {" +
                (Math.sqrt(3) / 4 * 9) + ", " + (-3 / 4.0) + "}}";
        assertEquals(expected, hex.toString(), "toString output should match");
    }
}