package hexio;

import hex.*;

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
}
