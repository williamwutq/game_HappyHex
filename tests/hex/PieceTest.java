package hex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PieceTest {

    @Test
    void testDefaultConstructor() {
        Piece piece = new Piece();
        assertEquals(1, piece.length(), "Length should be 1");
        assertEquals(-1, piece.getColor(), "Color should be -1");
        assertNotNull(piece.getBlock(0), "Block at index 0 should not be null");
        assertEquals(0, piece.getBlock(0).getLineI(), "Block I coordinate should be 0");
        assertEquals(0, piece.getBlock(0).getLineK(), "Block K coordinate should be 0");
        assertTrue(piece.getBlock(0).getState(), "Block should be occupied");
    }

    @Test
    void testConstructorWithLengthAndColor() {
        Piece piece = new Piece(3, 4);
        assertEquals(3, piece.length(), "Length should be 3");
        assertEquals(4, piece.getColor(), "Color should be 4");
        assertNull(piece.getBlock(0), "Blocks should initially be null");
    }

    @Test
    void testConstructorWithInvalidLength() {
        Piece piece = new Piece(0, 5);
        assertEquals(1, piece.length(), "Length should be 1 when invalid length provided");
        assertEquals(5, piece.getColor(), "Color should be 5");
    }

    @Test
    void testSetColor() {
        Piece piece = new Piece(2, 2);
        piece.add(Block.block(0, 0, 2));
        piece.add(Block.block(1, 1, 2));

        piece.setColor(9);
        assertEquals(9, piece.getColor(), "Piece color should be 9");
        assertEquals(9, piece.getBlock(0).getColor(), "First block color should be 9");
        assertEquals(9, piece.getBlock(1).getColor(), "Second block color should be 9");
    }

    @Test
    void testAddBlock() {
        Piece piece = new Piece(2, 3);
        Block block1 = Block.block(0, 0, 3);
        Block block2 = Block.block(1, 1, 3);

        assertTrue(piece.add(block1), "Adding first block should succeed");
        assertTrue(piece.add(block2), "Adding second block should succeed");
        assertFalse(piece.add(Block.block(2, 2, 3)), "Adding to full piece should fail");

        assertEquals(block1, piece.getBlock(0), "First block should match");
        assertEquals(block2, piece.getBlock(1), "Second block should match");
        assertEquals(3, piece.getBlock(0).getColor(), "First block color should be 3");
        assertTrue(piece.getBlock(0).getState(), "First block should be occupied");
    }

    @Test
    void testAddBlockByCoordinates() {
        Piece piece = new Piece(2, 0);
        assertTrue(piece.add(0, 0), "Adding block at (0,0) should succeed");
        assertTrue(piece.add(1, 1), "Adding block at (1,1) should succeed");
        assertFalse(piece.add(2, 2), "Adding to full piece should fail");

        Block block1 = piece.getBlock(0);
        assertEquals(0, block1.getLineI(), "First block I coordinate should be 0");
        assertEquals(0, block1.getLineK(), "First block K coordinate should be 0");
        assertEquals(0, block1.getColor(), "First block color should be 0");
        assertTrue(block1.getState(), "First block should be occupied");
    }

    @Test
    void testBlocksMethod() {
        Piece piece = new Piece(5, 10);
        piece.add(1, 1);
        piece.add(0, 0);
        // Leave three slots null

        Block[] blocks = piece.blocks();
        assertEquals(5, blocks.length, "Blocks array length should be 3");
        assertEquals(0, blocks[0].getLineI(), "First block I should be 0");
        assertEquals(1, blocks[1].getLineI(), "Second block I should be 1");
        assertEquals(0, blocks[2].getLineI(), "Third block (dummy) I should be 0");
        assertEquals(10, blocks[2].getColor(), "Dummy block should have piece color");
    }

    @Test
    void testInRangeAndGetBlock() {
        Piece piece = new Piece(2, 3);
        piece.add(0, 0);
        piece.add(1, 1);

        assertTrue(piece.inRange(0, 0), "Should find block at (0,0)");
        assertTrue(piece.inRange(1, 1), "Should find block at (1,1)");
        assertFalse(piece.inRange(2, 2), "Should not find block at (2,2)");

        Block block = piece.getBlock(0, 0);
        assertNotNull(block, "Block at (0,0) should exist");
        assertEquals(0, block.getLineI(), "Block I coordinate should be 0");
        assertEquals(0, block.getLineK(), "Block K coordinate should be 0");

        assertNull(piece.getBlock(2, 2), "Block at (2,2) should be null");
    }

    @Test
    void testGetState() {
        Piece piece = new Piece(4, 3);
        piece.add(0, -1);
        piece.add(-1, 0);
        piece.add(1, 1);

        assertFalse(piece.getState(0, 0), "Block state at (0,0) should be false");
        assertTrue(piece.getState(0, -1), "Block state at (0,-1) should be true");
        assertTrue(piece.getState(-1, 0), "Block state at (-1,0) should be true");
        assertTrue(piece.getState(1, 1), "Block state at (1,1) should be true");
        assertFalse(piece.getState(2, 0), "Block state at (2,0) should be false");
    }

    @Test
    void testGetBlockByIndex() {
        Piece piece = new Piece(2, 3);
        piece.add(0, 0);
        piece.add(1, 1);

        Block block0 = piece.getBlock(0);
        Block block1 = piece.getBlock(1);

        assertEquals(0, block0.getLineI(), "Block 0 I coordinate should be 0");
        assertEquals(1, block1.getLineI(), "Block 1 I coordinate should be 1");
    }

    @Test
    void testAddHexGridThrowsException() {
        Piece piece = new Piece(2, 3);
        HexGrid other = new Piece(1, 4);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piece.add(new Hex(), other);
        });
        assertEquals("Adding Grid to piece prohibited. Please add block by block.",
                exception.getMessage(), "Exception message should match");
    }

    @Test
    void testSort() {
        Piece piece = new Piece(3, 3);
        piece.add(1, 1);
        piece.add(0, 0);
        piece.add(0, 1);

        // After sorting, blocks should be ordered by I, then K: (0,0), (0,1), (1,1)
        Block[] blocks = piece.blocks();
        assertEquals(0, blocks[0].getLineI(), "First block I should be 0");
        assertEquals(0, blocks[0].getLineK(), "First block K should be 0");
        assertEquals(0, blocks[1].getLineI(), "Second block I should be 0");
        assertEquals(1, blocks[1].getLineK(), "Second block K should be 1");
        assertEquals(1, blocks[2].getLineI(), "Third block I should be 1");
        assertEquals(1, blocks[2].getLineK(), "Third block K should be 1");
    }

    @Test
    void testToString() {
        Piece piece = new Piece(2, 7);
        piece.add(0, 0);
        piece.add(1, 1);

        String expected = "{Piece: {I = 0, J = 0, K = 0}{I = 1, J = 0, K = 1}}";
        assertEquals(expected, piece.toString(), "toString output should match");
    }

    @Test
    void testToByte() {
        Piece piece = new Piece(3, 4);
        piece.add(1, 1);
        piece.add(0, 0);
        piece.add(0, 1);

        assertEquals(piece.toByte(), 0b00001101, "toByte output should be 0b00001101");
    }

    @Test
    void testEquals() {
        Piece piece1 = new Piece(2, 4);
        piece1.add(0, 0);
        piece1.add(1, 1);

        Piece piece2 = new Piece(2, 3); // Different color, but same structure
        piece2.add(1, 1);
        piece2.add(0, 0);

        Piece piece3 = new Piece(2, 4);
        piece3.add(0, 0);
        piece3.add(2, 2); // Different structure

        assertTrue(piece1.equals(piece2), "Pieces with same structure should be equal");
        assertFalse(piece1.equals(piece3), "Pieces with different structure should not be equal");

        Piece piece4 = new Piece(3, 4); // Different length
        piece4.add(0, 0);
        piece4.add(1, 1);
        assertFalse(piece1.equals(piece4), "Pieces with different lengths should not be equal");
    }
}
