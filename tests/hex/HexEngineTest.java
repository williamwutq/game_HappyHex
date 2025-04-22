package tests.hex;

import hex.Hex;
import hex.HexEngine;
import hex.Piece;
import hex.Block;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class HexEngineTest {

    private HexEngine createTestEngine() {
        return new HexEngine(2, Color.GRAY, Color.BLUE);
    }

    @Test
    void testConstructor() {
        HexEngine engine = createTestEngine();
        assertEquals(2, engine.getRadius(), "Radius should be 2");
        assertEquals(Color.GRAY, engine.getEmptyBlockColor(), "Empty block color should be GRAY");
        assertEquals(Color.BLUE, engine.getFilledBlockColor(), "Filled block color should be BLUE");
        assertEquals(7, engine.length(), "Length should be 1 + 3*2*1 = 7 for radius 2");
        assertFalse(engine.getBlock(0, 0).getState(), "Blocks should be unoccupied initially");
        assertEquals(Color.GRAY, engine.getBlock(0, 0).color(), "Blocks should have empty color initially");
    }

    @Test
    void testReset() {
        HexEngine engine = createTestEngine();
        engine.setState(0, 0, true);
        engine.setBlock(0, 0, new Block(0, 0, Color.RED, true));

        engine.reset();
        assertFalse(engine.getBlock(0, 0).getState(), "Block should be unoccupied after reset");
        assertEquals(Color.GRAY, engine.getBlock(0, 0).color(), "Block should have empty color after reset");
        assertEquals(7, engine.length(), "Length should remain unchanged after reset");
    }

    @Test
    void testGetRadius() {
        HexEngine engine = createTestEngine();
        assertEquals(2, engine.getRadius(), "Radius should be 2");
    }

    @Test
    void testLengthAndBlocks() {
        HexEngine engine = createTestEngine();
        assertEquals(7, engine.length(), "Length should be 7");
        Block[] blocks = engine.blocks();
        assertEquals(7, blocks.length, "Blocks array length should be 7");
        assertTrue(blocks[0].inRange(2), "All blocks should be in range");
    }

    @Test
    void testInRange() {
        HexEngine engine = createTestEngine();
        assertTrue(engine.inRange(0, 0), "Coordinate (0,0) should be in range");
        assertTrue(engine.inRange(1, 1), "Coordinate (1,1) should be in range");
        assertFalse(engine.inRange(3, 3), "Coordinate (3,3) should be out of range");
    }

    @Test
    void testGetAndSetBlock() {
        HexEngine engine = createTestEngine();
        Block block = engine.getBlock(0, 0);
        assertNotNull(block, "Block at (0,0) should exist");
        assertEquals(0, block.getLineI(), "Block I coordinate should be 0");
        assertEquals(0, block.getLineK(), "Block K coordinate should be 0");

        Block newBlock = new Block(0, 0, Color.RED, true);
        engine.setBlock(0, 0, newBlock);
        assertEquals(newBlock, engine.getBlock(0, 0), "Set block should match");
        assertEquals(Color.RED, engine.getBlock(0, 0).color(), "Block color should be RED");
    }

    @Test
    void testSetState() {
        HexEngine engine = createTestEngine();
        engine.setState(0, 0, true);
        Block block = engine.getBlock(0, 0);
        assertTrue(block.getState(), "Block should be occupied");
        assertEquals(Color.BLUE, block.color(), "Block should have filled color");

        engine.setState(0, 0, false);
        assertFalse(block.getState(), "Block should be unoccupied");
        assertEquals(Color.GRAY, block.color(), "Block should have empty color");
    }

    @Test
    void testSetDefaultBlockColors() {
        HexEngine engine = createTestEngine();
        engine.setDefaultBlockColors(Color.WHITE, Color.BLACK);
        assertEquals(Color.WHITE, engine.getEmptyBlockColor(), "Empty block color should be WHITE");
        assertEquals(Color.BLACK, engine.getFilledBlockColor(), "Filled block color should be BLACK");

        engine.setState(0, 0, true);
        assertEquals(Color.BLACK, engine.getBlock(0, 0).color(), "Filled block should be BLACK");
        engine.setState(0, 0, false);
        assertEquals(Color.WHITE, engine.getBlock(0, 0).color(), "Empty block should be WHITE");
    }

    @Test
    void testCheckAdd() {
        HexEngine engine = createTestEngine();
        Piece piece = new Piece(2, Color.RED);
        piece.add(0, 0);
        piece.add(0, 1);

        Hex origin = Hex.hex(0, 0);
        assertTrue(engine.checkAdd(origin, piece), "Adding piece at (0,0) should be valid");

        engine.setState(0, 0, true);
        assertFalse(engine.checkAdd(origin, piece), "Adding piece over occupied block should be invalid");

        Hex outOfRange = Hex.hex(3, 3);
        assertFalse(engine.checkAdd(outOfRange, piece), "Adding piece out of range should be invalid");
    }

    @Test
    void testAdd() {
        HexEngine engine = createTestEngine();
        Piece piece = new Piece(2, Color.RED);
        piece.add(0, 0);
        piece.add(0, 1);

        Hex origin = Hex.hex(0, 0);
        engine.add(origin, piece);
        assertTrue(engine.getBlock(0, 0).getState(), "Block at (0,0) should be occupied");
        assertTrue(engine.getBlock(0, 1).getState(), "Block at (0,1) should be occupied");
        assertEquals(Color.RED, engine.getBlock(0, 0).color(), "Block color should be RED");

        Piece overlappingPiece = new Piece(1, Color.BLUE);
        overlappingPiece.add(0, 0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            engine.add(origin, overlappingPiece);
        });
        assertEquals("Cannot add into existing block", exception.getMessage(), "Exception message should match");
    }

    @Test
    void testCheckPositions() {
        HexEngine engine = createTestEngine();
        Piece piece = new Piece(1, Color.RED);
        piece.add(0, 0);

        ArrayList<Hex> positions = engine.checkPositions(piece);
        assertFalse(positions.isEmpty(), "There should be valid positions");
        assertTrue(positions.getFirst().equals(Hex.hex(0, 0)), "Position (0,0) should be and in front");
        engine.setState(0, 0, true);
        positions = engine.checkPositions(piece);
        assertFalse(positions.contains(Hex.hex(0, 0)), "Position (0,0) should be invalid after occupation");
    }

    @Test
    void testEliminate() {
        HexEngine engine = createTestEngine();
        // Set up a full - Set up blocks in I-line 0 to be fully occupied
        engine.setState(1, 0, true);
        engine.setState(1, 1, true);
        engine.setState(1, 2, true);

        Block[] eliminated = engine.eliminate();
        System.out.println(Arrays.toString(eliminated));
        assertEquals(3, eliminated.length, "Should eliminate 3 blocks");
        assertFalse(engine.getBlock(1, 0).getState(), "Block at (0,0) should be unoccupied");
        assertFalse(engine.getBlock(1, 1).getState(), "Block at (0,1) should be unoccupied");
        assertFalse(engine.getBlock(1, 2).getState(), "Block at (0,2) should be unoccupied");

        // Test with no full lines
        engine.setState(0, 0, true);
        eliminated = engine.eliminate();
        assertEquals(0, eliminated.length, "No blocks should be eliminated");
    }

    @Test
    void testCheckEliminate() {
        HexEngine engine = createTestEngine();
        assertFalse(engine.checkEliminate(), "No lines should be full initially");

        engine.setState(0, 0, true);
        engine.setState(0, 1, true);
        engine.setState(0, 2, true);
        assertTrue(engine.checkEliminate(), "I-line 0 should be full");

        engine.setState(0, 1, false);
        assertFalse(engine.checkEliminate(), "I-line 0 should not be full");
    }

    @Test
    void testComputeDenseIndex() {
        HexEngine engine = createTestEngine();
        Piece piece = new Piece(1, Color.RED);
        piece.add(0, 0);

        engine.setState(0, 0, true); // Neighbor to (1,1)
        double index = engine.computeDenseIndex(Hex.hex(1, 1), piece);
        assertTrue(index > 0, "Density index should be positive with a neighbor");
        assertEquals(1.0 / 6.0, index, 0.001, "Density index should be 1/6 with one neighbor");

        Piece invalidPiece = new Piece(1, Color.RED);
        invalidPiece.add(0, 0); // Overlaps with occupied block
        assertEquals(0.0, engine.computeDenseIndex(Hex.hex(0, 0), invalidPiece),
                "Density index should be 0 for invalid placement");
    }

    @Test
    void testToString() {
        HexEngine engine = createTestEngine();
        String str = engine.toString();
        assertTrue(str.contains("{I = 0, J = 0, K = 0},false"), "String should include block states");
        assertTrue(str.startsWith("{HexEngine: "), "String should start with HexEngine prefix");
    }

    @Test
    void testClone() throws CloneNotSupportedException {
        HexEngine engine = createTestEngine();
        engine.setState(0, 0, true);
        HexEngine cloned = (HexEngine) engine.clone();

        assertEquals(engine.getRadius(), cloned.getRadius(), "Cloned radius should match");
        assertEquals(engine.getEmptyBlockColor(), cloned.getEmptyBlockColor(),
                "Cloned empty color should match");
        assertEquals(engine.getFilledBlockColor(), cloned.getFilledBlockColor(),
                "Cloned filled color should match");
        assertEquals(engine.length(), cloned.length(), "Cloned length should match");
        assertTrue(cloned.getBlock(0, 0).getState(), "Cloned block state should match");
        assertNotSame(engine.blocks(), cloned.blocks(), "Cloned blocks array should be different");
        assertNotSame(engine.getBlock(0, 0), cloned.getBlock(0, 0),
                "Cloned block objects should be different");
    }
}
