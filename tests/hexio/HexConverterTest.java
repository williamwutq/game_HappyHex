package hexio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.json.*;
import hex.*;
import java.io.IOException;

public class HexConverterTest {
    @Test
    void testConvertHex() {
        Hex hex = Hex.hex(1, 2);
        JsonObject json = HexConverter.convertHex(hex);
        assertEquals(1, json.getInt("I"), "I coordinate should be 1");
        assertEquals(1, json.getInt("J"), "J coordinate should be 1");
        assertEquals(2, json.getInt("K"), "K coordinate should be 2");

        JsonObject nullJson = HexConverter.convertHex((Hex) null);
        assertEquals(0, nullJson.getInt("I"), "Null hex I should be 0");
        assertEquals(0, nullJson.getInt("J"), "Null hex J should be 0");
        assertEquals(0, nullJson.getInt("K"), "Null hex K should be 0");
    }

    @Test
    void testConvertBlock() {
        Block block = Block.block(2, -1, 2);
        block.setState(true);
        JsonObject json = HexConverter.convertBlock(block);
        assertEquals(2, json.getInt("I"), "I coordinate should be 2");
        assertEquals(-3, json.getInt("J"), "J coordinate should be -3");
        assertEquals(-1, json.getInt("K"), "K coordinate should be -1");
        assertTrue(json.getBoolean("state"), "State should be true");

        JsonObject nullJson = HexConverter.convertBlock((Block) null);
        assertEquals(0, nullJson.getInt("I"), "Null block I should be 0");
        assertEquals(0, nullJson.getInt("J"), "Null block J should be 0");
        assertEquals(0, nullJson.getInt("K"), "Null block K should be 0");
        assertFalse(nullJson.getBoolean("state"), "Null block state should be false");
    }

    @Test
    void testConvertColoredBlock() {
        Block block = Block.block(3, 4, 2);
        block.setState(true);
        JsonObject json = HexConverter.convertIndexColoredBlock(block);
        assertEquals(3, json.getInt("I"), "I coordinate should be 3");
        assertEquals(1, json.getInt("J"), "J coordinate should be 1");
        assertEquals(4, json.getInt("K"), "K coordinate should be 4");
        assertEquals(2, json.getInt("C"), "Color component should be 2");
        assertTrue(json.getBoolean("state"), "State should be true");

        JsonObject nullJson = HexConverter.convertIndexColoredBlock(null);
        assertEquals(-1, nullJson.getInt("C"), "Null block R should be -1");
        assertFalse(nullJson.getBoolean("state"), "Null block state should be false");
    }

    @Test
    void testConvertPiece() {
        Piece piece = new Piece(2, 4);
        piece.add(0, 0);
        piece.add(1, 1);

        JsonArray blocks = HexConverter.convertPiece(piece);
        assertEquals(7, blocks.size(), "Piece should convert to 7 blocks");
        JsonObject block00 = blocks.getJsonObject(3); // (0,0)
        assertEquals(0, block00.getInt("I"), "Block at (0,0) I should be 0");
        assertEquals(0, block00.getInt("K"), "Block at (0,0) K should be 0");
        assertTrue(block00.getBoolean("state"), "Block at (0,0) should be occupied");

        JsonObject block11 = blocks.getJsonObject(6); // (1,1)
        assertEquals(1, block11.getInt("I"), "Block at (1,1) I should be 1");
        assertEquals(1, block11.getInt("K"), "Block at (1,1) K should be 1");
        assertTrue(block11.getBoolean("state"), "Block at (1,1) should be occupied");

        JsonObject blockMinus1 = blocks.getJsonObject(0); // (-1,-1)
        assertFalse(blockMinus1.getBoolean("state"), "Dummy block at (-1,-1) should be unoccupied");
    }

    @Test
    void testConvertColoredPiece() {
        Piece piece = new Piece(2, 4);
        piece.add(0, 0);
        piece.add(1, 1);

        JsonArray blocks = HexConverter.convertIndexColoredPiece(piece);
        assertEquals(7, blocks.size(), "Colored piece should convert to 7 blocks");
        JsonObject block00 = blocks.getJsonObject(3); // (0,0)
        assertEquals(0, block00.getInt("I"), "Block at (0,0) I should be 0");
        assertEquals(0, block00.getInt("K"), "Block at (0,0) K should be 0");
        assertEquals(4, block00.getInt("C"), "Block at (0,0) R should be 0");
        assertTrue(block00.getBoolean("state"), "Block at (0,0) should be occupied");
    }

    @Test
    void testConvertEngine() {
        HexEngine engine = new HexEngine(2);
        engine.setState(0, 0, true);

        JsonObject json = HexConverter.convertEngine(engine);
        assertEquals(2, json.getInt("radius"), "Radius should be 2");
        JsonArray blocks = json.getJsonArray("blocks");
        assertEquals(7, blocks.size(), "Engine should have 7 blocks");

        JsonObject block00 = blocks.getJsonObject(0); // Assuming sorted order
        assertEquals(0, block00.getInt("I"), "Block at (0,0) I should be 0");
        assertEquals(0, block00.getInt("K"), "Block at (0,0) K should be 0");
        assertTrue(block00.getBoolean("state"), "Block at (0,0) should be occupied");
    }

    @Test
    void testConvertColoredEngine() {
        HexEngine engine = new HexEngine(2);
        engine.setState(0, 0, true);

        JsonObject json = HexConverter.convertIndexColoredEngine(engine);
        assertEquals(2, json.getInt("radius"), "Radius should be 2");
        JsonArray blocks = json.getJsonArray("blocks");
        assertEquals(7, blocks.size(), "Engine should have 7 blocks");

        JsonObject block00 = blocks.getJsonObject(0); // Assuming sorted order
        assertEquals(0, block00.getInt("I"), "Block at (0,0) I should be 0");
        assertEquals(0, block00.getInt("K"), "Block at (0,0) K should be 0");
        assertEquals(-2, block00.getInt("C"), "Block at (0,0) Color should be default");
        assertTrue(block00.getBoolean("state"), "Block at (0,0) should be occupied");
    }

    @Test
    void testConvertMove() {
        Hex center = Hex.hex(1, 2);
        Piece piece = new Piece(1, 5);
        piece.add(0, 0);

        JsonObject json = HexConverter.convertMove(1, center, piece);
        assertEquals(1, json.getInt("order"), "Move order should be 1");
        JsonObject centerJson = json.getJsonObject("center");
        assertEquals(1, centerJson.getInt("I"), "Center I should be 1");
        assertEquals(1, centerJson.getInt("J"), "Center J should be 1");
        assertEquals(2, centerJson.getInt("K"), "Center K should be 2");
        JsonArray pieceJson = json.getJsonArray("piece");
        assertEquals(7, pieceJson.size(), "Piece should have 7 blocks");
    }

    @Test
    void testConvertColoredMove() {
        Hex center = Hex.hex(1, 2);
        Piece piece = new Piece(1, 5);
        piece.add(0, 0);

        JsonObject json = HexConverter.convertIndexColoredMove(1, center, piece);
        assertEquals(1, json.getInt("order"), "Move order should be 1");
        JsonArray pieceJson = json.getJsonArray("piece");
        JsonObject block00 = pieceJson.getJsonObject(3); // (0,0)
        assertEquals(5, block00.getInt("C"), "Block at (0,0) Color should be 5");
        assertTrue(block00.getBoolean("state"), "Block at (0,0) should be occupied");
    }

    @Test
    void testConvertHexFromJson() throws IOException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("I", 1).add("J", 2).add("K", 3);
        JsonObject json = builder.build();

        Hex hex = HexConverter.convertHex(json);
        assertEquals(1, hex.getLineI(), "Converted hex I should be 1");
        assertEquals(2, hex.getLineJ(), "Converted hex J should be 2");
        assertEquals(3, hex.getLineK(), "Converted hex K should be 3");

        builder = Json.createObjectBuilder();
        builder.add("i", 1).add("j", 2).add("k", 3);
        hex = HexConverter.convertHex(builder.build());
        assertEquals(1, hex.getLineI(), "Converted hex with lowercase keys should work");

        JsonObject invalidJson = Json.createObjectBuilder().add("I", 1).add("J", 0).add("K", 2).build();
        assertThrows(IOException.class, () -> HexConverter.convertHex(invalidJson),
                "Invalid coordinates should throw IOException");

        assertThrows(IOException.class, () -> HexConverter.convertHex((JsonObject) null),
                "Null JSON should throw IOException");
    }

    @Test
    void testConvertColorIndex() throws IOException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("C", 5);
        JsonObject json = builder.build();

        int color = HexConverter.convertColorIndex(json);
        assertEquals(5, color, "Converted color should be 5");

        builder = Json.createObjectBuilder();
        builder.add("c", 6);
        color = HexConverter.convertColorIndex(builder.build());
        assertEquals(6, color, "Converted color with lowercase keys should work");

        assertThrows(IOException.class, () -> HexConverter.convertColorIndex(null),
                "Null JSON should throw IOException");
    }

    @Test
    void testConvertBlockFromJson() throws IOException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("I", 1).add("J", 1).add("K", 2)
                .add("C", 4).add("state", true);
        JsonObject json = builder.build();

        Block block = HexConverter.convertBlock(json);
        assertEquals(1, block.getLineI(), "Converted block I should be 1");
        assertEquals(1, block.getLineJ(), "Converted block J should be 1");
        assertEquals(2, block.getLineK(), "Converted block K should be 2");
        assertEquals(4, block.getColor(), "Converted block color should be 4");
        assertTrue(block.getState(), "Converted block state should be true");

        // Test with missing color and state
        builder = Json.createObjectBuilder();
        builder.add("I", 0).add("J", 0).add("K", 0);
        block = HexConverter.convertBlock(builder.build());
        assertEquals(-1, block.getColor(), "Missing color should default to -1");
        assertFalse(block.getState(), "Missing state should default to false");

        assertThrows(IOException.class, () -> HexConverter.convertBlock((JsonObject) null),
                "Null JSON should throw IOException");
    }

    @Test
    void testConvertPieceFromJson() throws IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", 0).add("J", 0).add("K", 0)
                .add("C", 7).add("state", true));
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", -1).add("J", 2).add("K", 1)
                .add("C", 7).add("state", true));
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", 1).add("J", 0).add("K", 1)
                .add("C", 7).add("state", true));
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", 0).add("J", 0).add("K", 0)
                .add("state", false)); // Non-occupied block
        JsonObject json = Json.createObjectBuilder().add("blocks", arrayBuilder).build();

        Piece piece = HexConverter.convertPiece(json);
        assertEquals(3, piece.length(), "Piece should have length 3");
        assertEquals(7, piece.getColor(), "Piece color should be 7");
        assertNotNull(piece.getBlock(0, 0), "Block at (0,0) should exist");
        assertNotNull(piece.getBlock(-1, 1), "Block at (-1,1) should exist");
        assertTrue(piece.getBlock(0, 0).getState(), "Block at (0,0) should be occupied");

        // Test with no valid blocks
        arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", 0).add("J", 0).add("K", 0)
                .add("state", false));
        json = Json.createObjectBuilder().add("blocks", arrayBuilder).build();
        JsonObject finalJson = json;
        assertThrows(IOException.class, () -> HexConverter.convertPiece(finalJson),
                "Piece with no valid blocks should throw IOException");
    }

    @Test
    void testConvertEngineFromJson() throws IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", 0).add("J", 0).add("K", 0)
                .add("C", 7).add("state", true));
        arrayBuilder.add(Json.createObjectBuilder()
                .add("I", 0).add("J", -1).add("K", -1)
                .add("C", 7).add("state", false));
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("radius", 2).add("blocks", arrayBuilder);
        JsonObject json = jsonBuilder.build();

        HexEngine engine = HexConverter.convertEngine(json);
        assertEquals(2, engine.getRadius(), "Engine radius should be 2");
        assertEquals(7, engine.length(), "Engine should have 7 blocks");
        assertTrue(engine.getBlock(0, 0).getState(), "Block at (0,0) should be occupied");
        assertEquals(7, engine.getBlock(0, 0).getColor(), "Block at (0,0) should be 7");

        // Test with missing radius
        jsonBuilder = Json.createObjectBuilder().add("blocks", arrayBuilder);
        JsonObject finalJson = jsonBuilder.build();
        assertThrows(IOException.class, () -> HexConverter.convertEngine(finalJson),
                "Missing radius should throw IOException");
    }
}
