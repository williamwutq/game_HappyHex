package tests.hex;

import hex.Hex;
import hex.Block;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;

public class BlockTest {

    @Test
    void testBasicConstructorWithColor() {
        Block block = new Block(2, 3, Color.RED);
        assertEquals(2, block.I(), "I coordinate should be 2");
        assertEquals(3, block.K(), "K coordinate should be 3");
        assertEquals(Color.RED, block.color(), "Color should be RED");
        assertFalse(block.getState(), "State should be false (unoccupied)");
    }

    @Test
    void testBasicConstructorWithColorAndState() {
        Block block = new Block(1, 4, Color.BLUE, true);
        assertEquals(1, block.I(), "I coordinate should be 1");
        assertEquals(4, block.K(), "K coordinate should be 4");
        assertEquals(Color.BLUE, block.color(), "Color should be BLUE");
        assertTrue(block.getState(), "State should be true (occupied)");
    }

    @Test
    void testHexConstructorWithColor() {
        Hex hex = new Hex(3, 2);
        Block block = new Block(hex, Color.GREEN);
        assertEquals(3, block.I(), "I coordinate should be 3");
        assertEquals(2, block.K(), "K coordinate should be 2");
        assertEquals(Color.GREEN, block.color(), "Color should be GREEN");
        assertFalse(block.getState(), "State should be false (unoccupied)");
    }

    @Test
    void testHexConstructorWithColorAndState() {
        Hex hex = new Hex(0, 5);
        Block block = new Block(hex, Color.YELLOW, true);
        assertEquals(0, block.I(), "I coordinate should be 0");
        assertEquals(5, block.K(), "K coordinate should be 5");
        assertEquals(Color.YELLOW, block.color(), "Color should be YELLOW");
        assertTrue(block.getState(), "State should be true (occupied)");
    }

    @Test
    void testStaticBlockFactory() {
        Block block = Block.block(1, 2, Color.MAGENTA);
        assertEquals(3, block.I(), "I coordinate should be 3");
        assertEquals(0, block.K(), "K coordinate should be 0");
        assertEquals(1, block.getLineI(), "Line I should be 1");
        assertEquals(2, block.getLineK(), "Line K should be 2");
        assertEquals(Color.MAGENTA, block.color(), "Color should be MAGENTA");
        assertFalse(block.getState(), "State should be false (unoccupied)");
    }

    @Test
    void testColorAndStateSetters() {
        Block block = new Block(0, 0, Color.BLACK);
        block.setColor(Color.WHITE);
        assertEquals(Color.WHITE, block.color(), "Color should be WHITE after setColor");

        block.setState(true);
        assertTrue(block.getState(), "State should be true after setState");

        block.changeState();
        assertFalse(block.getState(), "State should be false after changeState");

        block.changeState();
        assertTrue(block.getState(), "State should be true after second changeState");
    }

    @Test
    void testShiftMethods() {
        Block block = new Block(0, 0, Color.RED);
        Block shiftedI = block.shiftI(1);
        assertEquals(2, shiftedI.I(), "Shifted I should be 2");
        assertEquals(-1, shiftedI.K(), "Shifted K should be -1");
        assertEquals(Color.RED, shiftedI.color(), "Color should remain RED");
        assertEquals(block.getState(), shiftedI.getState(), "State should remain unchanged");
        assertSame(block, shiftedI, "shiftI should return same instance");

        Block shiftedJ = block.shiftJ(1);
        assertEquals(3, shiftedJ.I(), "Shifted I should be 3");
        assertEquals(0, shiftedJ.K(), "Shifted K should be 0");
        assertSame(block, shiftedJ, "shiftJ should return same instance");

        Block shiftedK = block.shiftK(1);
        assertEquals(2, shiftedK.I(), "Shifted I should be 2");
        assertEquals(2, shiftedK.K(), "Shifted K should be 2");
        assertSame(block, shiftedK, "shiftK should return same instance");
    }

    @Test
    void testAddAndSubtract() {
        Block block = Block.block(2, 3, Color.BLUE);
        block.changeState();
        Hex other = Hex.hex(1, 1);

        Block added = block.add(other);
        assertEquals(3, added.getLineI(), "Added I should be 3");
        assertEquals(4, added.getLineK(), "Added K should be 4");
        assertEquals(Color.BLUE, added.color(), "Color should remain BLUE");
        assertTrue(added.getState(), "State should remain true");

        Block subtracted = block.subtract(other);
        assertEquals(1, subtracted.getLineI(), "Subtracted I should be 1");
        assertEquals(2, subtracted.getLineK(), "Subtracted K should be 2");
        assertEquals(Color.BLUE, subtracted.color(), "Color should remain BLUE");
        assertTrue(subtracted.getState(), "State should remain true");
    }

    @Test
    void testToString() {
        Block block = new Block(3, 6, Color.RED, true);
        String expected = "{Color = {255, 0, 0}; I,J,K = {3, 9, 6}; Line I,J,K = {5, -1, 4}; X,Y = {" +
                (Math.sqrt(3) / 4 * 9) + ", " + (-3 / 4.0) + "}; State = true;}";
        assertEquals(expected, block.toString(), "toString output should match");
    }

    @Test
    void testClone() {
        Block block = new Block(2, -7, Color.GREEN, true);
        Block cloned = block.clone();
        assertEquals(block.I(), cloned.I(), "Cloned I should match");
        assertEquals(block.J(), cloned.J(), "Cloned J should match");
        assertEquals(block.K(), cloned.K(), "Cloned K should match");
        assertEquals(block.color(), cloned.color(), "Cloned color should match");
        assertEquals(block.getState(), cloned.getState(), "Cloned state should match");
        assertNotSame(block, cloned, "Cloned object should be different instance");
        assertNotSame(block.color(), cloned.color(), "Cloned color should be different instance");
    }
}
