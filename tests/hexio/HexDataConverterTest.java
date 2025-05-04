package hexio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import hex.*;
import java.awt.Color;

public class HexDataConverterTest {
    @Test
    void testConvertHex() {
        Hex hex = Hex.hex(-4, 5);
        String hexString = HexDataConverter.convertHex(hex);
        assertEquals("FFFFFFFC00000005", hexString, "Converted string should be FFFFFFFC00000005");

        hexString = HexDataConverter.convertHex((Hex) null);
        assertEquals("0000000000000000", hexString, "Null hex I should be all 0s");
    }
    @Test
    void testConvertBlock() {
        Block block = Block.block(3, 23, Color.BLACK);
        block.setState(true);
        String hexString = HexDataConverter.convertBlock(block);
        assertEquals("0000000300000017F", hexString, "Converted string should be 0000000300000017F");

        hexString = HexDataConverter.convertBlock((Block) null);
        assertEquals("00000000000000000", hexString, "Null block I should be all 0s");
    }
    @Test
    void testConvertPiece() {
        Piece piece = new Piece(3, Color.BLUE);
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
        HexEngine engine = new HexEngine(3, Color.GRAY, Color.BLUE);
        engine.setState(0, 0, true);
        engine.setState(1, 2, true);
        engine.setState(1, -1, true);
        engine.setState(2, 3, true);
        String hexString = HexDataConverter.convertBooleanEngine(engine);
        assertEquals("0000000312400", hexString, "Converted string should be 0000000312400");

        hexString = HexDataConverter.convertBooleanEngine((HexEngine) null);
        assertEquals("000000010", hexString, "Null piece I should be all 0s");
    }
    @Test
    void testConvertMove() {
        Piece piece = new Piece(3, Color.BLUE);
        piece.add(1, 1);
        piece.add(0, 0);
        piece.add(0, 1);
        String hexString = HexDataConverter.convertBooleanMove(138, Hex.hex(5, 4), piece);
        assertEquals("0000008AFF0000000500000004FF0D", hexString, "Converted string should be 0000008AFF0000000500000004FF0D");
    }
}
