package hexio;

import hexio.hexdata.*;
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
     * The JSON object contains the I and K line coordinates of the hexagonal grid.
     * These coordinates are stored as integers and J coordinate can be calculated.
     * If the input is null, returns a hexadecimal string with all coordinates set to 0.
     * <p>
     *
     * @param hex the {@code Hex} object to convert
     * @return a {@code JsonObject} representing the hexagonal coordinates
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
}
