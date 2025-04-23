package hexio;

import javax.json.*;
import hex.*;

import java.io.IOException;

public final class HexConverter {
    // Hex to Json
    public static JsonObject convertHex(Hex hex){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (hex == null) {
            builder.add("I", 0);
            builder.add("J", 0);
            builder.add("K", 0);
        } else {
            builder.add("I", hex.getLineI());
            builder.add("J", hex.getLineJ());
            builder.add("K", hex.getLineK());
        }
        return builder.build();
    }
    public static JsonObject convertBlock(Block block){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (block == null) {
            builder.add("I", 0);
            builder.add("J", 0);
            builder.add("K", 0);
            builder.add("state", false);
        } else {
            builder.add("I", block.getLineI());
            builder.add("J", block.getLineJ());
            builder.add("K", block.getLineK());
            builder.add("state", block.getState());
        }
        return builder.build();
    }
    public static JsonObject convertColoredBlock(Block block){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (block == null) {
            builder.add("I", 0);
            builder.add("J", 0);
            builder.add("K", 0);
            builder.add("R", 0);
            builder.add("G", 0);
            builder.add("B", 0);
            builder.add("state", false);
        } else {
            builder.add("I", block.getLineI());
            builder.add("J", block.getLineJ());
            builder.add("K", block.getLineK());
            builder.add("R", block.color().getRed());
            builder.add("G", block.color().getGreen());
            builder.add("B", block.color().getBlue());
            builder.add("state", block.getState());
        }
        return builder.build();
    }
    private static JsonObject convertPieceBlock(int i, int k, Piece piece){
        Block block = piece.getBlock(i, k);
        if (block == null){
            block = Block.block(i, k, piece.getColor());
        } else {
            block = block.clone();
        }
        return convertBlock(block);
    }
    public static JsonObject convertPiece(Piece piece){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(convertPieceBlock(-1, -1, piece));
        builder.add(convertPieceBlock(-1, 0, piece));
        builder.add(convertPieceBlock(0, -1, piece));
        builder.add(convertPieceBlock(0, 0, piece));
        builder.add(convertPieceBlock(0, 1, piece));
        builder.add(convertPieceBlock(1, 0, piece));
        builder.add(convertPieceBlock(1, 1, piece));
        return builder.build().asJsonObject();
    }
    private static JsonObject convertColoredPieceBlock(int i, int k, Piece piece){
        Block block = piece.getBlock(i, k);
        if (block == null){
            block = Block.block(i, k, piece.getColor());
        } else {
            block = block.clone();
        }
        return convertColoredBlock(block);
    }
    public static JsonObject convertColoredPiece(Piece piece){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(convertColoredPieceBlock(-1, -1, piece));
        builder.add(convertColoredPieceBlock(-1, 0, piece));
        builder.add(convertColoredPieceBlock(0, -1, piece));
        builder.add(convertColoredPieceBlock(0, 0, piece));
        builder.add(convertColoredPieceBlock(0, 1, piece));
        builder.add(convertColoredPieceBlock(1, 0, piece));
        builder.add(convertColoredPieceBlock(1, 1, piece));
        return builder.build().asJsonObject();
    }
    public static JsonObject convertEngine(HexEngine engine){
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        objectBuilder.add("radius", engine.getRadius());
        // Add array of blocks
        for (int i = 0; i < engine.length(); i ++){
            arrayBuilder.add(convertBlock(engine.getBlock(i)));
        }
        objectBuilder.add("blocks", arrayBuilder);
        return objectBuilder.build();
    }
    public static JsonObject convertColoredEngine(HexEngine engine){
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        objectBuilder.add("radius", engine.getRadius());
        // Add array of blocks
        for (int i = 0; i < engine.length(); i ++){
            arrayBuilder.add(convertColoredBlock(engine.getBlock(i)));
        }
        objectBuilder.add("blocks", arrayBuilder);
        return objectBuilder.build();
    }
    public static JsonObject convertMove(int moveOrder, Hex center, Piece piece){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("order", moveOrder);
        builder.add("center", convertHex(center));
        builder.add("piece", convertPiece(piece));
        return builder.build();
    }
    public static JsonObject convertColoredMove(int moveOrder, Hex center, Piece piece){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("order", moveOrder);
        builder.add("center", convertHex(center));
        builder.add("piece", convertColoredPiece(piece));
        return builder.build();
    }

    // Json to Hex
    public static Hex convertHex(JsonObject jsonObject) throws IOException {
        if (jsonObject == null) throw new IOException("\"Hex\" object is null or not found");
        int i, j, k;
        try {
            i = jsonObject.getInt("I");
        } catch (Exception e) {
            try {
                i = jsonObject.getInt("i");
            } catch (Exception ex) {
                throw new IOException("Coordinate I in \"Hex\" not found");
            }
        }
        try {
            j = jsonObject.getInt("J");
        } catch (Exception e) {
            try {
                j = jsonObject.getInt("j");
            } catch (Exception ex) {
                throw new IOException("Coordinate J in \"Hex\" not found");
            }
        }
        try {
            k = jsonObject.getInt("K");
        } catch (Exception e) {
            try {
                k = jsonObject.getInt("k");
            } catch (Exception ex) {
                throw new IOException("Coordinate K in \"Hex\" not found");
            }
        }
        // Verify coordinate
        if (i + j != k) {
            throw new IOException("Coordinate I, J, K does not match");
        } else {
            return Hex.hex(i, j);
        }
    }
    public static java.awt.Color convertColor(JsonObject jsonObject) throws IOException {
        if (jsonObject == null) throw new IOException("\"Color\" object is null or not found");
        int r, g, b;
        try {
            r = jsonObject.getInt("R");
        } catch (Exception e) {
            try {
                r = jsonObject.getInt("r");
            } catch (Exception ex) {
                throw new IOException("Component R in \"Color\" not found");
            }
        }
        try {
            g = jsonObject.getInt("G");
        } catch (Exception e) {
            try {
                g = jsonObject.getInt("g");
            } catch (Exception ex) {
                throw new IOException("Component G in \"Color\" not found");
            }
        }
        try {
            b = jsonObject.getInt("B");
        } catch (Exception e) {
            try {
                b = jsonObject.getInt("b");
            } catch (Exception ex) {
                throw new IOException("Component B in \"Color\" not found");
            }
        }
        // Check values
        if (r > 255 || r < 0) {
            throw new IOException("Component R in \"Color\" not in range of [0, 255]");
        }
        if (g > 255 || g < 0) {
            throw new IOException("Component G in \"Color\" not in range of [0, 255]");
        }
        if (b > 255 || b < 0) {
            throw new IOException("Component B in \"Color\" not in range of [0, 255]");
        }
        return new java.awt.Color(r, g, b);
    }
    // Coordinate portion must be valid
    // Color and state portion can be missing, which will be default to BLACK and unoccupied (false)
    public static Hex convertBlock(JsonObject jsonObject) throws IOException {
        if (jsonObject == null) throw new IOException("\"Block\" object is null or not found");
        Hex hex = convertHex(jsonObject);
        java.awt.Color color = java.awt.Color.BLACK;
        boolean state = false;
        try {
            color = convertColor(jsonObject);
        } catch (Exception e) {}
        try {
            state = jsonObject.getBoolean("state");
        } catch (Exception e) {}
        return new Block(hex, color, state);
    }
}
