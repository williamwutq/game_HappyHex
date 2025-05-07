package hex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {

    @Test
    void testBasicConstructorWithColor() {
        Block block = new Block(2, 3, 1);
        assertEquals(2, block.I(), "I coordinate should be 2");
        assertEquals(3, block.K(), "K coordinate should be 3");
        assertEquals(1, block.getColor(), "Color should be 1");
        assertFalse(block.getState(), "State should be false (unoccupied)");
    }

    @Test
    void testBasicConstructorWithColorAndState() {
        Block block = new Block(1, 4, 3, true);
        assertEquals(1, block.I(), "I coordinate should be 1");
        assertEquals(4, block.K(), "K coordinate should be 4");
        assertEquals(3, block.getColor(), "Color should be 3");
        assertTrue(block.getState(), "State should be true (occupied)");
    }

    @Test
    void testHexConstructorWithColor() {
        Hex hex = new Hex(3, 2);
        Block block = new Block(hex, 4);
        assertEquals(3, block.I(), "I coordinate should be 3");
        assertEquals(2, block.K(), "K coordinate should be 2");
        assertEquals(4, block.getColor(), "Color should be 4");
        assertFalse(block.getState(), "State should be false (unoccupied)");
    }

    @Test
    void testHexConstructorWithColorAndState() {
        Hex hex = new Hex(0, 5);
        Block block = new Block(hex, 4, true);
        assertEquals(0, block.I(), "I coordinate should be 0");
        assertEquals(5, block.K(), "K coordinate should be 5");
        assertEquals(4, block.getColor(), "Color should be 4");
        assertTrue(block.getState(), "State should be true (occupied)");
    }

    @Test
    void testStaticBlockFactory() {
        Block block = Block.block(1, 2, 11);
        assertEquals(3, block.I(), "I coordinate should be 3");
        assertEquals(0, block.K(), "K coordinate should be 0");
        assertEquals(1, block.getLineI(), "Line I should be 1");
        assertEquals(2, block.getLineK(), "Line K should be 2");
        assertEquals(11, block.getColor(), "Color should be 11");
        assertFalse(block.getState(), "State should be false (unoccupied)");
    }

    @Test
    void testColorAndStateSetters() {
        Block block = new Block(0, 0, 5);
        block.setColor(2);
        assertEquals(2, block.getColor(), "Color should be 2 after setColor");

        block.setState(true);
        assertTrue(block.getState(), "State should be true after setState");

        block.changeState();
        assertFalse(block.getState(), "State should be false after changeState");

        block.changeState();
        assertTrue(block.getState(), "State should be true after second changeState");
    }

    @Test
    void testShiftMethods() {
        Block block = new Block(0, 0, 7);
        Block shiftedI = block.shiftI(1);
        assertEquals(2, shiftedI.I(), "Shifted I should be 2");
        assertEquals(-1, shiftedI.K(), "Shifted K should be -1");
        assertEquals(7, shiftedI.getColor(), "Color should remain 7");
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
        Block block = Block.block(2, 3, 3);
        block.changeState();
        Hex other = Hex.hex(1, 1);

        Block added = block.add(other);
        assertEquals(3, added.getLineI(), "Added I should be 3");
        assertEquals(4, added.getLineK(), "Added K should be 4");
        assertEquals(3, added.getColor(), "Color should remain 3");
        assertTrue(added.getState(), "State should remain true");

        Block subtracted = block.subtract(other);
        assertEquals(1, subtracted.getLineI(), "Subtracted I should be 1");
        assertEquals(2, subtracted.getLineK(), "Subtracted K should be 2");
        assertEquals(3, subtracted.getColor(), "Color should remain 3");
        assertTrue(subtracted.getState(), "State should remain true");
    }

    @Test
    void testToString() {
        Block block = new Block(3, 6, 2, true);
        String expected = "Block[color = 2, coordinates = {5, -1, 4}, state = true]";
        assertEquals(expected, block.toString(), "toString output should match");
    }

    @Test
    void testClone() {
        Block block = new Block(2, -7, 5, true);
        Block cloned = block.clone();
        assertEquals(block.I(), cloned.I(), "Cloned I should match");
        assertEquals(block.J(), cloned.J(), "Cloned J should match");
        assertEquals(block.K(), cloned.K(), "Cloned K should match");
        assertEquals(block.getColor(), cloned.getColor(), "Cloned color should match");
        assertEquals(block.getState(), cloned.getState(), "Cloned state should match");
        assertNotSame(block, cloned, "Cloned object should be different instance");
        assertEquals(block.getColor(), cloned.getColor(), "Cloned color should be the same");
    }
}
