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
     * <p>
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
     * <p>
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
     * <p>
     *
     * @param piece the {@code Piece} object to convert
     * @see Piece#Piece
     * @see Piece#toByte()
     */
    public static String convertPiece(Piece piece){
        return String.format("%02X", piece.toByte());
    }
}
