package hexio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import hex.*;
import java.io.IOException;

public class HexDataConverterTest {
    @Test
    void testConvertHex() {
        Hex hex = Hex.hex(-4, 5);
        String hexString = HexDataConverter.convertHex(hex);
        assertEquals("FFFC0005", hexString, "Converted string should be FFFC0005");

        hexString = HexDataConverter.convertHex((Hex) null);
        assertEquals("00000000", hexString, "Null hex I should be all 0s");
    }
    @Test
    void testConvertBlock() {
        Block block = Block.block(3, 23, 1);
        block.setState(true);
        String hexString = HexDataConverter.convertBlock(block);
        assertEquals("00030017F", hexString, "Converted string should be 00030017F");

        hexString = HexDataConverter.convertBlock((Block) null);
        assertEquals("000000000", hexString, "Null block I should be all 0s");
    }
    @Test
    void testConvertPiece() {
        Piece piece = new Piece(3, 2);
        piece.add(1, 1);
        piece.add(0, 0);
        piece.add(0, 1);
        String hexString = HexDataConverter.convertBooleanPiece(piece);
        assertEquals("0D", hexString, "Converted string should be 0D");

        hexString = HexDataConverter.convertBooleanPiece((Piece) null);
        assertEquals("00", hexString, "Null piece I should be all 0s");
    }
    @Test
    void testConvertEngine() {
        HexEngine engine = new HexEngine(3);
        engine.setState(0, 0, true);
        engine.setState(1, 2, true);
        engine.setState(2, 3, true);
        String hexString = HexDataConverter.convertBooleanEngine(engine);
        assertEquals("000312400", hexString, "Converted string should be 000312400");

        hexString = HexDataConverter.convertBooleanEngine((HexEngine) null);
        assertEquals("00010", hexString, "Null piece I should be all 0s");
    }
    @Test
    void testConvertMove() {
        Piece piece = new Piece(3, 9);
        piece.add(1, 1);
        piece.add(0, 0);
        piece.add(0, 1);
        String hexString = HexDataConverter.convertBooleanMove(Hex.hex(5, 4), piece);
        assertEquals("000500040D", hexString, "Converted string should be 000500040D");
    }
    @Test
    void testConvertHexFromString() throws IOException {
        Hex hex = HexDataConverter.convertHex("FFFC0005");
        assertEquals(-4, hex.getLineI(), "Converted hex I should be -4");
        assertEquals(9, hex.getLineJ(), "Converted hex J should be 9");
        assertEquals(5, hex.getLineK(), "Converted hex K should be 5");
    }
    @Test
    void testConvertBlockFromString() throws IOException {
        Block block = HexDataConverter.convertBlock("00030017F");
        assertTrue(block.getState(), "Converted block state should be true");
        assertEquals(3, block.getLineI(), "Converted block I should be 3");
        assertEquals(20, block.getLineJ(), "Converted block J should be 20");
        assertEquals(23, block.getLineK(), "Converted block K should be 23");
    }
    @Test
    void testConvertPieceFromString() throws IOException {
        Piece piece = HexDataConverter.convertPiece("0D");
        assertFalse(piece.getState(-1, -1), "Block at (-1,-1) should be unoccupied");
        assertFalse(piece.getState(-1, 0), "Block at (-1,0) should be unoccupied");
        assertFalse(piece.getState(0, -1), "Block at (0,-1) should be unoccupied");
        assertTrue(piece.getState(0, 0), "Block at (0,0) should be occupied");
        assertTrue(piece.getState(0, 1), "Block at (0,1) should be occupied");
        assertFalse(piece.getState(1, 0), "Block at (1,0) should be unoccupied");
        assertTrue(piece.getState(1, 1), "Block at (1,1) should be occupied");
    }
    @Test
    void testConvertEngineFromString() throws IOException {
        HexEngine engine = HexDataConverter.convertEngine("000312400");
        assertEquals(3, engine.getRadius(), "Converted engine radius should be 3");
        assertTrue(engine.getBlock(0, 0).getState(), "Block at (0,0) should be occupied");
        assertFalse(engine.getBlock(1, 1).getState(), "Block at (1,1) should be unoccupied");
        assertTrue(engine.getBlock(1, 2).getState(), "Block at (1,2) should be occupied");
        assertTrue(engine.getBlock(2, 3).getState(), "Block at (2,3) should be occupied");
        assertFalse(engine.getBlock(3, 3).getState(), "Block at (3,3) should be unoccupied");
        String hexString = HexDataConverter.convertBooleanEngine(engine);
        assertEquals("000312400", hexString, "Reverse conversion should work");
    }
}
