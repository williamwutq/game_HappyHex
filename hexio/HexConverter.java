package hexio;

import javax.json.*;
import hex.*;

public final class HexConverter {
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
}
