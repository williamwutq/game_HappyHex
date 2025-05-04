package hexio;

import hex.*;

import java.io.IOException;

/**
 * The {@code HexDataConverter} class provides utility methods for converting hexagonal game components
 * (such as {@link Hex}, {@link Block}, {@link Piece}, {@link HexEngine}, and game moves) to and from
 * {@link hexio.hexdata.HexDataFactory HexData} representations. This class serves as an add-on package for the {@link hex}
 * package, facilitating serialization and deserialization of hexagonal grid-based game data for
 * storage, transmission, or interoperability. The class is designed to handle null inputs gracefully
 * and provides robust error handling for invalid binary or hexadecimal string data.
 *
 * @version 1.3
 * @author William Wu
 * @since 1.3
 */
public class HexDataConverter {
    /**
     * Converts a {@code Hex} coordinate to a hexadecimal string.
     * The hexadecimal string contains the I and K line coordinates of the hexagonal grid.
     * These coordinates are stored as integers and J coordinate can be calculated.
     * If the input is null, returns a hexadecimal string with all coordinates set to 0.
     *
     * @param hex the {@code Hex} object to convert
     * @return a hexadecimal string representing the hexagonal coordinates
     * @see Hex#hex
     */
    public static String convertHex(Hex hex){
        StringBuilder builder = new StringBuilder();
        if (hex == null) {
            builder.append(String.format("%08X", 0));
            builder.append(String.format("%08X", 0));
        } else {
            builder.append(String.format("%08X", hex.getLineI()));
            builder.append(String.format("%08X", hex.getLineK()));
        }
        return builder.toString();
    }
    /**
     * Converts a {@code Block} to a hexadecimal string.
     * The hexadecimal string includes the block's I and K coordinates and its state (occupied or not).
     * These coordinates are stored as integers and J coordinate can be calculated.
     * If the input is null, returns a hexadecimal string with coordinates set to 0 and state set to false.
     *
     * @param block the {@code Block} object to convert
     * @return a hexadecimal string representing the block
     * @see Block#block
     * @see #convertHex(Hex)
     */
    public static String convertBlock(Block block){
        StringBuilder builder = new StringBuilder();
        if (block == null) {
            builder.append(String.format("%08X", 0));
            builder.append(String.format("%08X", 0));
            builder.append("0");
        } else {
            builder.append(String.format("%08X", block.getLineI()));
            builder.append(String.format("%08X", block.getLineK()));
            builder.append(String.format(block.getState() ? "F" : "0"));
        }
        return builder.toString();
    }
    /**
     * Converts a {@link Piece} to a hexadecimal string.
     * This conversion use the internal build conversion methods to get an ordinary piece's byte representation
     * and convert it into hexadecimal string. It has nothing to do with {@link #convertBlock(Block)}.
     *
     * @param piece the {@code Piece} object to convert
     * @return a hexadecimal string representing the piece
     * @see Piece#Piece
     * @see Piece#toByte()
     */
    public static String convertBooleanPiece(Piece piece){
        return String.format("%02X", piece.toByte());
    }
    /**
     * Converts a {@link HexEngine} to a hexadecimal string.
     * This conversion convert the engine's radius into an int value and {@link Block} state into an array of booleans
     * and convert it into hexadecimal string. It has nothing to do with {@link #convertBlock(Block)}.
     *
     * @param engine the {@code HexEngine} object to convert
     * @return a hexadecimal string representing the engine
     * @see HexEngine#HexEngine
     */
    public static String convertBooleanEngine(HexEngine engine){
        int length = engine.length();
        StringBuilder builder = new StringBuilder(String.format("%08X", engine.getRadius()));
        int fullLength = (length + 3) / 4 * 4; // Round up to multiple of 4
        for (int i = 0; i < fullLength; i += 4) {
            int hexValue = 0;
            for (int j = 0; j < 4 && i + j < length; j++) {
                if (engine.getBlock(i + j).getState()) {
                    // Set bit j for true
                    hexValue |= (1 << j);
                }
            }
            builder.append(String.format("%X", hexValue));
        }
        return builder.toString();
    }
    /**
     * Converts a game move to a hexadecimal string.
     * The hexadecimal string includes the move order, center coordinate, and piece.
     *
     * @param moveOrder the order of the move
     * @param center    the {@code Hex} center coordinate of the move
     * @param piece     the {@code Piece} involved in the move
     * @return a hexadecimal string representing the move
     * @see #convertHex(Hex)
     * @see #convertBooleanPiece(Piece)
     */
    public static String convertBooleanMove(int moveOrder, Hex center, Piece piece){
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%08X", moveOrder));
        builder.append("FF");
        builder.append(convertHex(center));
        builder.append("FF");
        builder.append(convertBooleanPiece(piece));
        return builder.toString();
    }

    /**
     * Converts a hexadecimal string to a {@link Hex} coordinate.
     * The hexadecimal string must follow the format for the coordinate and be of 16 characters long.
     * This conversion use line coordinates.
     * <p>
     * This is the inverse method of {@link #convertHex(Hex)}.
     *
     * @param hexString the hexadecimal string containing the hex coordinates
     * @return a {@code Hex} object
     * @throws IOException if the hexadecimal string is null, or missing hexadecimal characters
     * @see Hex#hex
     * @see #convertBlock(String)
     * @see #convertPiece(String)
     * @see #convertEngine(String)
     */
    public static Hex convertHex(String hexString) throws IOException {
        if (hexString == null) {
            throw new IOException("\"Hex\" object is null or not found");
        } else if (hexString.length() != 16){
            throw new IOException("\"Hex\" object hexadecimal string length is not 16");
        }
        int i; int k;
        try {
            i = Integer.parseUnsignedInt(hexString.substring(0, 8), 16);
            k = Integer.parseUnsignedInt(hexString.substring(8, 16), 16);
        } catch (NumberFormatException e) {
            throw new IOException("\"Hex\" object hexadecimal string format is invalid");
        }
        return Hex.hex(i, k);
    }
    /**
     * Converts a hexadecimal string to a {@link Block}.
     * The hexadecimal string must contain two integer represent the coordinates and the state of the block.
     * Color is ignored and replaced with black. The length of the string must be 17.
     * <p>
     * This is the inverse method of {@link #convertBlock(Block)}.
     *
     * @param hexString the hexadecimal string containing the block data
     * @return a {@code Block} object
     * @throws IOException if the hexadecimal string is null or is not of length 17
     * @see Block#block
     * @see #convertHex(String)
     */
    public static Block convertBlock(String hexString) throws IOException {
        if (hexString == null) {
            throw new IOException("\"Block\" object is null or not found");
        } else if (hexString.length() != 17){
            throw new IOException("\"Block\" object hexadecimal string length is not 17");
        }
        Hex hex = convertHex(hexString.substring(0,16));
        boolean state;
        try {
            char stateChar = hexString.charAt(16);
            if (stateChar == 'F'){
                state = true;
            } else if (stateChar == '0'){
                state = false;
            } else {
                throw new IOException("\"Block\" object does not contain valid state");
            }
        } catch (NumberFormatException e) {
            throw new IOException("\"Block\" object does not contain valid state");
        }
        return new Block(hex, java.awt.Color.BLACK, state);
    }
    /**
     * Converts a hexadecimal string to a {@link Piece}.
     * The hexadecimal string must be of length 2 and following the format for piece.
     * <p>
     * This is the inverse method of {@link #convertBooleanPiece(Piece)}.
     * This method is not related to {@link #convertBlock(String)}.
     *
     * @param hexString the hexadecimal string containing the piece data
     * @return a {@code Piece} object
     * @throws IOException if the hexadecimal string is null, is not 2 characters long, or contain invalid characters
     * @see Piece#Piece
     */
    public static Piece convertPiece(String hexString) throws IOException {
        if (hexString == null) {
            throw new IOException("\"Piece\" object is null or not found");
        } else if (hexString.length() != 2){
            throw new IOException("\"Piece\" object hexadecimal string length is not 2");
        }
        Piece piece;
        try {
            piece = Piece.pieceFromByte((byte) Integer.parseUnsignedInt(hexString, 16), java.awt.Color.BLACK);
        } catch (IllegalArgumentException e) {
            throw new IOException("\"Piece\" object creation failed due to invalid format or missing blocks");
        }
        return piece;
    }
    /**
     * Converts a hexadecimal string to a {@link HexEngine}.
     * The hexadecimal string must contain a radius and an array of boolean representing blocks.
     * The total length of the hexadecimal string must match the engine size.
     * <p>
     * This is the inverse method of {@link #convertBooleanEngine(HexEngine)}.
     * This method is not related to {@link #convertBlock(String)}.
     *
     * @param hexString the hexadecimal string containing the engine data
     * @return a {@code HexEngine} object
     * @throws IOException if the hexadecimal string is null, have invalid radius, or size does not match
     * @see HexEngine#HexEngine
     * @since 1.3
     */
    public static HexEngine convertEngine(String hexString) throws IOException {
        if (hexString == null) {
            throw new IOException("\"HexEngine\" object is null or not found");
        } else if (hexString.length() < 9){
            throw new IOException("\"HexEngine\" object hexadecimal string length is invalid");
        }
        // Get radius
        int radius;
        try {
            radius = Integer.parseUnsignedInt(hexString.substring(0, 8), 16);
        } catch (NumberFormatException e) {
            throw new IOException("\"HexEngine\" object cannot be generated because radius cannot be read");
        }
        if (radius <= 0) throw new IOException("\"HexEngine\" object cannot be generated because radius is negative or 0");
        // Create engine
        HexEngine engine = new HexEngine(radius, java.awt.Color.BLACK, java.awt.Color.WHITE);
        if ((engine.length()+3)/4 != hexString.length() - 8){
            throw new IOException("\"HexEngine\" object cannot be generated because input string have incorrect length");
        }
        int index = 0;
        for (int i = 8; i < hexString.length(); i++) {
            int hexValue = Character.digit(hexString.charAt(i), 16);
            for (int bit = 0; bit < 4 && index < engine.length(); bit++) {
                boolean state = (hexValue & (1 << bit)) != 0;
                engine.getBlock(index).setState(state);
                index ++;
            }
        }
        return engine;
    }
}
