package hexio;

import javax.json.*;
import hex.*;

import java.io.IOException;

/**
 * The {@code HexConverter} class provides utility methods for converting hexagonal game components
 * (such as {@link Hex}, {@link Block}, {@link Piece}, {@link HexEngine}, and game moves) to and from
 * {@link javax.json JSON} representations. This class serves as an add-on package for the {@link hex}
 * package, facilitating serialization and deserialization of hexagonal grid-based game data for
 * storage, transmission, or interoperability. The class is designed to handle null inputs gracefully
 * and provides robust error handling for invalid JSON data.
 *
 * @version 1.3
 * @author William Wu
 * @since 1.2
 */
public final class HexConverter {
    // Hex to Json
    /**
     * Converts a {@code Hex} coordinate to a JSON object.
     * The JSON object contains the I, J, and K line coordinates of the hexagonal grid.
     * If the input is null, returns a JSON object with all coordinates set to 0.
     * <p>
     * This is the inverse method of {@link #convertHex(JsonObject)}.
     *
     * @param hex the {@code Hex} object to convert
     * @return a {@code JsonObject} representing the hexagonal coordinates
     * @see Hex#hex
     * @see #convertBlock(Block)
     * @see #convertPiece(Piece)
     * @see #convertEngine(HexEngine)
     * @see #convertMove(int, Hex, Piece)
     */
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
    /**
     * Converts a {@code Block} to a JSON object.
     * The JSON object includes the block's I, J, K coordinates and its state (occupied or not).
     * If the input is null, returns a JSON object with coordinates set to 0 and state set to false.
     * <p>
     * For colored generation, see {@link #convertColoredBlock(Block)}.
     * This is the inverse method of {@link #convertBlock(JsonObject)}.
     *
     * @param block the {@code Block} object to convert
     * @return a {@code JsonObject} representing the block
     * @see Block#block
     * @see #convertHex(Hex)
     */
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
    /**
     * Converts a {@code Block} to a JSON object.
     * The JSON object includes the block's I, J, K coordinates, index of color used, and state.
     * If the input is null, returns a JSON object with coordinates set to 0, color index set to -1,
     * and state set to false.
     * <p>
     * For non-colored generation, see {@link #convertBlock(Block)}.
     * For RGB colored generation, see {@link #convertColoredBlock(Block)}.
     * This is the inverse method of {@link #convertBlock(JsonObject)}.
     *
     * @param block the {@code Block} object to convert
     * @param index the color index representing the color in use
     * @return a {@code JsonObject} representing the block
     * @see Block#block
     * @see #convertHex(Hex)
     * @since 1.3
     */
    public static JsonObject convertIndexColoredBlock(Block block, int index){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (block == null) {
            builder.add("I", 0);
            builder.add("J", 0);
            builder.add("K", 0);
            builder.add("C", 0);
            builder.add("state", false);
        } else {
            builder.add("I", block.getLineI());
            builder.add("J", block.getLineJ());
            builder.add("K", block.getLineK());
            builder.add("C", index);
            builder.add("state", block.getState());
        }
        return builder.build();
    }
    /**
     * Converts a colored {@link Block} to a JSON object.
     * The JSON object includes the block's I, J, K line coordinates, RGB color components, and state.
     * If the input is null, returns a JSON object with coordinates and color components set to 0
     * and state set to false.
     * <p>
     * For non-colored generation, see {@link #convertBlock(Block)}.
     * This is the inverse method of {@link #convertBlock(JsonObject)}.
     *
     * @param block the {@code Block} object to convert
     * @return a {@code JsonObject} representing the colored block
     * @see Block#block
     * @see #convertHex(Hex)
     */
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
    /**
     * Converts a specific block from a {@link Piece} to a JSON object based on its coordinates.
     * If the block at the specified coordinates is null, a new block is created with the piece's color.
     *
     * @param i     the I-coordinate of the block
     * @param k     the K-coordinate of the block
     * @param piece the {@code Piece} containing the block
     * @return a {@code JsonObject} representing the block
     * @see Piece#Piece
     * @see #convertBlock(Block)
     * @see #convertPiece(Piece)
     */
    private static JsonObject convertPieceBlock(int i, int k, Piece piece){
        Block block = piece.getBlock(i, k);
        if (block == null){
            block = Block.block(i, k, piece.getColor());
        } else {
            block = block.clone();
        }
        return convertBlock(block);
    }
    /**
     * Converts a {@link Piece} to a JSON array of blocks.
     * The array includes JSON objects for blocks at specific coordinates relative to the piece.
     * <p>
     * For colored generation, see {@link #convertColoredPiece(Piece)}.
     * This is the inverse method of {@link #convertPiece(JsonObject)}.
     *
     * @param piece the {@code Piece} object to convert
     * @return a {@code JsonArray} of block JSON objects
     * @see Piece#Piece
     * @see #convertBlock(Block)
     */
    public static JsonArray convertPiece(Piece piece){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(convertPieceBlock(-1, -1, piece));
        builder.add(convertPieceBlock(-1, 0, piece));
        builder.add(convertPieceBlock(0, -1, piece));
        builder.add(convertPieceBlock(0, 0, piece));
        builder.add(convertPieceBlock(0, 1, piece));
        builder.add(convertPieceBlock(1, 0, piece));
        builder.add(convertPieceBlock(1, 1, piece));
        return builder.build();
    }
    /**
     * Converts a specific colored block from a {@link Piece} to a JSON object based on its coordinates.
     * If the block at the specified coordinates is null, a new block is created with the piece's color.
     *
     * @param i     the I-coordinate of the block
     * @param k     the K-coordinate of the block
     * @param piece the {@code Piece} containing the block
     * @return a {@code JsonObject} representing the colored block
     * @see Piece#Piece
     * @see #convertColoredBlock(Block)
     * @see #convertColoredPiece(Piece)
     */
    private static JsonObject convertColoredPieceBlock(int i, int k, Piece piece){
        Block block = piece.getBlock(i, k);
        if (block == null){
            block = Block.block(i, k, piece.getColor());
        } else {
            block = block.clone();
        }
        return convertColoredBlock(block);
    }
    /**
     * Converts a colored 7-Block {@link Piece} to a JSON array of colored blocks.
     * The array includes JSON objects for blocks at specific coordinates, including color information.
     * <p>
     * For non-colored generation, see {@link #convertPiece(Piece)}.
     * This is the inverse method of {@link #convertPiece(JsonObject)}.
     *
     * @param piece the {@code Piece} object to convert
     * @return a {@code JsonArray} of colored block JSON objects
     * @see Piece#Piece
     * @see #convertColoredBlock(Block)
     */
    public static JsonArray convertColoredPiece(Piece piece){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(convertColoredPieceBlock(-1, -1, piece));
        builder.add(convertColoredPieceBlock(-1, 0, piece));
        builder.add(convertColoredPieceBlock(0, -1, piece));
        builder.add(convertColoredPieceBlock(0, 0, piece));
        builder.add(convertColoredPieceBlock(0, 1, piece));
        builder.add(convertColoredPieceBlock(1, 0, piece));
        builder.add(convertColoredPieceBlock(1, 1, piece));
        return builder.build();
    }
    /**
     * Converts a specific block from a {@link Piece} to a JSON object based on its coordinates.
     * If the block at the specified coordinates is null, a new block is created with the piece's color.
     *
     * @param i     the I-coordinate of the block
     * @param k     the K-coordinate of the block
     * @param piece the {@code Piece} containing the block
     * @param index the color index representing the color in use for this piece
     * @return a {@code JsonObject} representing the block
     * @see Piece#Piece
     * @see #convertIndexColoredBlock(Block, int)
     * @see #convertIndexColoredPiece(Piece, int)
     * @since 1.3
     */
    private static JsonObject convertIndexColoredPieceBlock(int i, int k, Piece piece, int index){
        Block block = piece.getBlock(i, k);
        if (block == null){
            block = Block.block(i, k, piece.getColor());
        } else {
            block = block.clone();
        }
        return convertIndexColoredBlock(block, index);
    }
    /**
     * Converts a {@link Piece} to a JSON array of index colored blocks.
     * The array includes JSON objects for blocks at specific coordinates relative to the piece.
     * <p>
     * For non-colored generation, see {@link #convertPiece(Piece)}.
     * For RGB colored generation, see {@link #convertColoredPiece(Piece)}.
     * This is the inverse method of {@link #convertPiece(JsonObject)}.
     *
     * @param piece the {@code Piece} object to convert
     * @param index the color index representing the color in use for this piece
     * @return a {@code JsonArray} of index colored block JSON objects
     * @see Piece#Piece
     * @see #convertIndexColoredBlock(Block, int)
     * @since 1.3
     */
    public static JsonArray convertIndexColoredPiece(Piece piece, int index){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(convertIndexColoredPieceBlock(-1, -1, piece, index));
        builder.add(convertIndexColoredPieceBlock(-1, 0, piece, index));
        builder.add(convertIndexColoredPieceBlock(0, -1, piece, index));
        builder.add(convertIndexColoredPieceBlock(0, 0, piece, index));
        builder.add(convertIndexColoredPieceBlock(0, 1, piece, index));
        builder.add(convertIndexColoredPieceBlock(1, 0, piece, index));
        builder.add(convertIndexColoredPieceBlock(1, 1, piece, index));
        return builder.build();
    }
    /**
     * Converts a {@link HexEngine} to a JSON object.
     * The JSON object includes the engine's radius and an array of blocks.
     * <p>
     * For colored generation, see {@link #convertColoredEngine(HexEngine)}.
     * This is the inverse method of {@link #convertEngine(JsonObject)}.
     *
     * @param engine the {@code HexEngine} object to convert
     * @return a {@code JsonObject} representing the engine
     * @see HexEngine#HexEngine
     * @see #convertBlock(Block)
     */
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
    /**
     * Converts a colored {@link HexEngine} to a JSON object.
     * The JSON object includes the engine's radius and an array of colored blocks.
     * <p>
     * For non-colored generation, see {@link #convertEngine(HexEngine)}.
     * This is the inverse method of {@link #convertEngine(JsonObject)}.
     *
     * @param engine the {@code HexEngine} object to convert
     * @return a {@code JsonObject} representing the colored engine
     * @see HexEngine#HexEngine
     * @see #convertColoredBlock(Block)
     */
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
    /**
     * Converts an index colored {@link HexEngine} to a JSON object.
     * The JSON object includes the engine's radius and an array of index colored blocks.
     * <p>
     * For non-colored generation, see {@link #convertEngine(HexEngine)}.
     * For RGB colored generation, see {@link #convertColoredEngine(HexEngine)}.
     * This is the inverse method of {@link #convertEngine(JsonObject)}.
     *
     * @param engine the {@code HexEngine} object to convert
     * @param indexes the indexes of the colors of the blocks
     * @return a {@code JsonObject} representing the index colored engine
     * @see HexEngine#HexEngine
     * @see #convertIndexColoredBlock(Block, int)
     * @throws IndexOutOfBoundsException if length of {@code indexes} array does not match {@code engine}
     * @since 1.3
     */
    public static JsonObject convertIndexColoredEngine(HexEngine engine, int[] indexes){
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        objectBuilder.add("radius", engine.getRadius());
        if(indexes.length != engine.length()){
            throw new IndexOutOfBoundsException("Length of input index array " + indexes.length + " does not match engine size");
        }
        // Add array of blocks
        for (int i = 0; i < engine.length(); i ++){
            arrayBuilder.add(convertIndexColoredBlock(engine.getBlock(i), indexes[i]));
        }
        objectBuilder.add("blocks", arrayBuilder);
        return objectBuilder.build();
    }
    /**
     * Converts a game move to a JSON object.
     * The JSON object includes the move order, center coordinate, and piece.
     * <p>
     * For colored generation, see {@link #convertColoredMove(int, Hex, Piece)}.
     * This method has no direct inverse method, but a combination of {@link JsonObject#getInt(String)},
     * {@link #convertHex(JsonObject)}, and {@link #convertPiece(JsonObject)} can do the job.
     *
     * @param moveOrder the order of the move
     * @param center    the {@code Hex} center coordinate of the move
     * @param piece     the {@code Piece} involved in the move
     * @return a {@code JsonObject} representing the move
     * @see #convertHex(Hex)
     * @see #convertPiece(Piece)
     */
    public static JsonObject convertMove(int moveOrder, Hex center, Piece piece){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("order", moveOrder);
        builder.add("center", convertHex(center));
        builder.add("piece", convertPiece(piece));
        return builder.build();
    }
    /**
     * Converts a colored game move to a JSON object.
     * The JSON object includes the move order, center coordinate, and colored piece.
     * <p>
     * For non-colored generation, see {@link #convertMove(int, Hex, Piece)}.
     * This method has no direct inverse method, but a combination of {@link JsonObject#getInt(String)},
     * {@link #convertHex(JsonObject)}, and {@link #convertPiece(JsonObject)} can do the job.
     *
     * @param moveOrder the number of the move order
     * @param center    the {@code Hex} center coordinate of the move
     * @param piece     the {@code Piece} involved in the move
     * @return a {@code JsonObject} representing the colored move
     * @see #convertHex(Hex)
     * @see #convertColoredPiece(Piece)
     */
    public static JsonObject convertColoredMove(int moveOrder, Hex center, Piece piece){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("order", moveOrder);
        builder.add("center", convertHex(center));
        builder.add("piece", convertColoredPiece(piece));
        return builder.build();
    }
    /**
     * Converts an index colored game move to a JSON object.
     * The JSON object includes the move order, center coordinate, and index colored piece.
     * <p>
     * For non-colored generation, see {@link #convertMove(int, Hex, Piece)}.
     * For RGB colored generation, see {@link #convertColoredMove(int, Hex, Piece)}.
     * This method has no direct inverse method, but a combination of {@link JsonObject#getInt(String)},
     * {@link #convertHex(JsonObject)}, and {@link #convertPiece(JsonObject)} can do the job.
     *
     * @param moveOrder the number of the move order
     * @param center    the {@code Hex} center coordinate of the move
     * @param piece     the {@code Piece} involved in the move
     * @param index     the color index representing the color in use for this piece
     * @return a {@code JsonObject} representing the index colored move
     * @see #convertHex(Hex)
     * @see #convertIndexColoredPiece(Piece, int)
     * @since 1.3
     */
    public static JsonObject convertIndexColoredMove(int moveOrder, Hex center, Piece piece, int index){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("order", moveOrder);
        builder.add("center", convertHex(center));
        builder.add("piece", convertIndexColoredPiece(piece, index));
        return builder.build();
    }

    // Json to Hex
    /**
     * Converts a JSON object to a {@link Hex} coordinate.
     * The JSON object must contain I, J, and K coordinates, and they must satisfy the hexagonal
     * line coordinate constraint (I + J = K). This conversion use line coordinates.
     * <p>
     * This is the inverse method of {@link #convertHex(Hex)}.
     *
     * @param jsonObject the JSON object containing the hex coordinates
     * @return a {@code Hex} object
     * @throws IOException if the JSON object is null, missing required coordinates, or contains invalid coordinates
     * @see Hex#hex
     * @see #convertBlock(JsonObject)
     * @see #convertPiece(JsonObject)
     * @see #convertEngine(JsonObject)
     */
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
            return Hex.hex(i, k);
        }
    }
    /**
     * Converts a JSON object to a {@link java.awt.Color Color} object.
     * The JSON object must contain R, G, and B components in the range [0, 255].
     *
     * @param jsonObject the JSON object containing the color components
     * @return a {@code Color} object
     * @throws IOException if the JSON object is null, missing required components, or contains invalid values
     * @see java.awt.Color#Color
     */
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
    /**
     * Converts a JSON object to a {@link Block}.
     * The JSON object must contain valid I, J, K coordinates. Color and state are optional, defaulting
     * to black and false (unoccupied) if missing.
     * <p>
     * This is the inverse method of {@link #convertBlock(Block)}.
     *
     * @param jsonObject the JSON object containing the block data
     * @return a {@code Block} object
     * @throws IOException if the JSON object is null or contains invalid coordinates
     * @see Block#block
     * @see #convertColor(JsonObject)
     * @see #convertHex(JsonObject)
     */
    public static Block convertBlock(JsonObject jsonObject) throws IOException {
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
    /**
     * Converts a JSON object to a {@link Piece}.
     * The JSON object must contain an array of valid blocks, with at least one block having a true state.
     * The piece's color is derived from the first valid block.
     * <p>
     * This is the inverse method of {@link #convertPiece(Piece)}.
     *
     * @param jsonObject the JSON object containing the piece data
     * @return a {@code Piece} object
     * @throws IOException if the JSON object is null, lacks a block array, or contains no valid blocks
     * @see Piece#Piece
     * @see #convertColor(JsonObject)
     * @see #convertBlock(JsonObject)
     */
    public static Piece convertPiece(JsonObject jsonObject) throws IOException {
        if (jsonObject == null) throw new IOException("\"Piece\" object is null or not found");
        // Get array
        JsonArray jsonArray;
        try {
            jsonArray = jsonObject.getJsonArray("blocks");
        } catch (Exception e) {
            try {
                jsonArray = jsonObject.getJsonArray("piece");
            } catch (Exception ex) {
                throw new IOException("Block array in \"Piece\" not found");
            }
        }
        return convertPiece(jsonArray);
    }
    /**
     * Converts a JSON array to a {@link Piece}.
     * The JSON array must contain an array of valid blocks, with at least one block having a true state.
     * The piece's color is derived from the first valid block.
     * <p>
     * This is the inverse method of {@link #convertPiece(Piece)}.
     *
     * @param jsonArray the JSON array containing the piece data
     * @return a {@code Piece} object
     * @throws IOException if the JSON array is null or contains no valid blocks
     * @see Piece#Piece
     * @see #convertPiece(JsonObject)
     * @see #convertColor(JsonObject)
     * @see #convertBlock(JsonObject)
     */
    public static Piece convertPiece(JsonArray jsonArray) throws IOException {
        if (jsonArray == null) throw new IOException("\"Piece\" object is null or not found");
        // Check first real block exists, and try to get color
        int size = jsonArray.size();
        int valid = 0;
        java.awt.Color color = java.awt.Color.BLACK;
        for (int i = 0; i < size; i ++){
            try {
                Block block = convertBlock(jsonArray.getJsonObject(i));
                if (block.getState()) {
                    if (valid == 0) color = block.color();
                    valid++;
                }
            } catch (Exception e) {}
        }
        if (valid == 0) throw new IOException("\"Piece\" object does not contain valid blocks");
        // Create piece object
        Piece piece = new Piece(valid, color);
        for (int i = 0; i < size; i ++){
            try {
                Block block = convertBlock(jsonArray.getJsonObject(i));
                if (block.getState()) {
                    piece.add(block);
                }
            } catch (Exception e) {}
        }
        return piece;
    }
    /**
     * Converts a JSON object to a {@link HexEngine}.
     * The JSON object must contain a radius and an array of blocks. Invalid blocks are ignored.
     * The engine's empty and filled colors are optional, defaulting to black and white.
     * <p>
     * This is the inverse method of {@link #convertEngine(HexEngine)}.
     *
     * @param jsonObject the JSON object containing the engine data
     * @return a {@code HexEngine} object
     * @throws IOException if the JSON object is null, lacks a radius, or contains an invalid radius
     * @see HexEngine#HexEngine
     * @see #convertColor(JsonObject)
     * @see #convertBlock(JsonObject)
     */
    public static HexEngine convertEngine(JsonObject jsonObject) throws IOException {
        if (jsonObject == null) throw new IOException("\"HexEngine\" object is null or not found");
        // Read radius, color, and create engine object
        java.awt.Color emptyColor = java.awt.Color.BLACK;
        java.awt.Color filledColor = java.awt.Color.WHITE;
        int radius = 0;
        try {
            emptyColor = convertColor(jsonObject.getJsonObject("empty"));
        } catch (Exception e) {}
        try {
            filledColor = convertColor(jsonObject.getJsonObject("filled"));
        } catch (Exception e) {}
        try {
            radius = jsonObject.getInt("radius");
        } catch (Exception e) {
            throw new IOException("Engine cannot be constructed because critical attribute radius in \"HexEngine\" not found");
        }
        if (radius < 1) {
            throw new IOException("Engine cannot be constructed because critical attribute radius in \"HexEngine\" is 0 or negative");
        }
        HexEngine engine = new HexEngine(radius, emptyColor, filledColor);
        // Get array and populate engine
        JsonArray jsonArray;
        try {
            jsonArray = jsonObject.getJsonArray("blocks");
        } catch (Exception e) {
            try {
                jsonArray = jsonObject.getJsonArray("piece");
            } catch (Exception ex) {
                throw new IOException("Block array in \"HexEngine\" not found");
            }
        }
        int size = jsonArray.size();
        for (int i = 0; i < size; i ++){
            try {
                Block block = convertBlock(jsonArray.getJsonObject(i));
                engine.setState(block.getLineI(), block.getLineK(), block.getState());
            } catch (Exception e) {}
        }
        return engine;
    }
}
