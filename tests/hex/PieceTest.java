package hex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PieceTest {

    @Test
    void testDefaultConstructor() {
        Piece piece = new Piece();
        assertEquals(1, piece.length(), "Length should be 1");
        assertEquals(SolidColor.BLACK, piece.getColor(), "SolidColor should be BLACK");
        assertNotNull(piece.getBlock(0), "Block at index 0 should not be null");
        assertEquals(0, piece.getBlock(0).getLineI(), "Block I coordinate should be 0");
        assertEquals(0, piece.getBlock(0).getLineK(), "Block K coordinate should be 0");
        assertTrue(piece.getBlock(0).getState(), "Block should be occupied");
    }

    @Test
    void testConstructorWithLengthAndColor() {
        Piece piece = new Piece(3, SolidColor.BLUE);
        assertEquals(3, piece.length(), "Length should be 3");
        assertEquals(SolidColor.BLUE, piece.getColor(), "SolidColor should be BLUE");
        assertNull(piece.getBlock(0), "Blocks should initially be null");
    }

    @Test
    void testConstructorWithInvalidLength() {
        Piece piece = new Piece(0, SolidColor.RED);
        assertEquals(1, piece.length(), "Length should be 1 when invalid length provided");
        assertEquals(SolidColor.RED, piece.getColor(), "SolidColor should be RED");
    }

    @Test
    void testSetColor() {
        Piece piece = new Piece(2, SolidColor.GREEN);
        piece.add(Block.block(0, 0, SolidColor.GREEN));
        piece.add(Block.block(1, 1, SolidColor.GREEN));

        piece.setColor(SolidColor.YELLOW);
        assertEquals(SolidColor.YELLOW, piece.getColor(), "Piece color should be YELLOW");
        assertEquals(SolidColor.YELLOW, piece.getBlock(0).color(), "First block color should be YELLOW");
        assertEquals(SolidColor.YELLOW, piece.getBlock(1).color(), "Second block color should be YELLOW");
    }

    @Test
    void testAddBlock() {
        Piece piece = new Piece(2, SolidColor.BLUE);
        Block block1 = Block.block(0, 0, SolidColor.BLUE);
        Block block2 = Block.block(1, 1, SolidColor.BLUE);

        assertTrue(piece.add(block1), "Adding first block should succeed");
        assertTrue(piece.add(block2), "Adding second block should succeed");
        assertFalse(piece.add(Block.block(2, 2, SolidColor.BLUE)), "Adding to full piece should fail");

        assertEquals(block1, piece.getBlock(0), "First block should match");
        assertEquals(block2, piece.getBlock(1), "Second block should match");
        assertEquals(SolidColor.BLUE, piece.getBlock(0).color(), "First block color should be BLUE");
        assertTrue(piece.getBlock(0).getState(), "First block should be occupied");
    }

    @Test
    void testAddBlockByCoordinates() {
        Piece piece = new Piece(2, SolidColor.RED);
        assertTrue(piece.add(0, 0), "Adding block at (0,0) should succeed");
        assertTrue(piece.add(1, 1), "Adding block at (1,1) should succeed");
        assertFalse(piece.add(2, 2), "Adding to full piece should fail");

        Block block1 = piece.getBlock(0);
        assertEquals(0, block1.getLineI(), "First block I coordinate should be 0");
        assertEquals(0, block1.getLineK(), "First block K coordinate should be 0");
        assertEquals(SolidColor.RED, block1.color(), "First block color should be RED");
        assertTrue(block1.getState(), "First block should be occupied");
    }

    @Test
    void testBlocksMethod() {
        Piece piece = new Piece(5, SolidColor.GREEN);
        piece.add(1, 1);
        piece.add(0, 0);
        // Leave three slots null

        Block[] blocks = piece.blocks();
        assertEquals(5, blocks.length, "Blocks array length should be 3");
        assertEquals(0, blocks[0].getLineI(), "First block I should be 0");
        assertEquals(1, blocks[1].getLineI(), "Second block I should be 1");
        assertEquals(0, blocks[2].getLineI(), "Third block (dummy) I should be 0");
        assertEquals(SolidColor.GREEN, blocks[2].color(), "Dummy block should have piece color");
    }

    @Test
    void testInRangeAndGetBlock() {
        Piece piece = new Piece(2, SolidColor.BLUE);
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
        Piece piece = new Piece(4, SolidColor.RED);
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
        Piece piece = new Piece(2, SolidColor.RED);
        piece.add(0, 0);
        piece.add(1, 1);

        Block block0 = piece.getBlock(0);
        Block block1 = piece.getBlock(1);

        assertEquals(0, block0.getLineI(), "Block 0 I coordinate should be 0");
        assertEquals(1, block1.getLineI(), "Block 1 I coordinate should be 1");
    }

    @Test
    void testAddHexGridThrowsException() {
        Piece piece = new Piece(2, SolidColor.BLUE);
        HexGrid other = new Piece(1, SolidColor.RED);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piece.add(new Hex(), other);
        });
        assertEquals("Adding Grid to piece prohibited. Please add block by block.",
                exception.getMessage(), "Exception message should match");
    }

    @Test
    void testSort() {
        Piece piece = new Piece(3, SolidColor.GREEN);
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
        Piece piece = new Piece(2, SolidColor.BLUE);
        piece.add(0, 0);
        piece.add(1, 1);

        String expected = "{Piece: {I = 0, J = 0, K = 0}{I = 1, J = 0, K = 1}}";
        assertEquals(expected, piece.toString(), "toString output should match");
    }

    @Test
    void testToByte() {
        Piece piece = new Piece(3, SolidColor.GREEN);
        piece.add(1, 1);
        piece.add(0, 0);
        piece.add(0, 1);

        assertEquals(piece.toByte(), 0b00001101, "toByte output should be 0b00001101");
    }

    @Test
    void testEquals() {
        Piece piece1 = new Piece(2, SolidColor.BLUE);
        piece1.add(0, 0);
        piece1.add(1, 1);

        Piece piece2 = new Piece(2, SolidColor.RED); // Different color, but same structure
        piece2.add(1, 1);
        piece2.add(0, 0);

        Piece piece3 = new Piece(2, SolidColor.BLUE);
        piece3.add(0, 0);
        piece3.add(2, 2); // Different structure

        assertTrue(piece1.equals(piece2), "Pieces with same structure should be equal");
        assertFalse(piece1.equals(piece3), "Pieces with different structure should not be equal");

        Piece piece4 = new Piece(3, SolidColor.BLUE); // Different length
        piece4.add(0, 0);
        piece4.add(1, 1);
        assertFalse(piece1.equals(piece4), "Pieces with different lengths should not be equal");
    }
}
