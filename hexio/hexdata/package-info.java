
/**
 * Provides utility classes for reading, writing, and managing hexadecimal data to and from files.
 * <p>
 * This package contains the following key classes:
 * <ul>
 *     <li>
 *         {@link hexio.hexdata.HexDataWriter HexDataWriter}: A utility class for accumulating
 *         and writing hexadecimal-encoded data to files in text or binary format.
 *         It supports encoding various primitive data types and strings into hexadecimal format.
 *     </li>
 *     <li>
 *         {@link hexio.hexdata.HexDataReader HexDataReader}: A utility class for reading
 *         and parsing hexadecimal-encoded data from files, supporting extraction of primitive
 *         data types and strings with sequential reading capabilities.
 *     </li>
 *     <li>
 *         {@link hexio.hexdata.HexDataFactory HexDataFactory}: A factory class that simplifies
 *         the creation and management of {@code HexDataWriter} and {@code HexDataReader} instances,
 *         providing methods for file I/O operations and file path equality checks.
 *     </li>
 * </ul>
 * <p>
 * These classes are designed for applications requiring hexadecimal data logging, serialization,
 * or storage, such as debugging, low-level programming, or data interchange. They provide a robust
 * and flexible way to handle hexadecimal data with support for file path configuration, data
 * comparison, and error handling.
 * <p>
 * Example usage:
 * <pre>{@code
 *     import hexio.hexdata.*;
 *
 *     // Write hexadecimal data to a file
 *     HexDataWriter writer = HexDataFactory.createWriter("output", "bin");
 *     writer.addHex("A56C");
 *     HexDataFactory.write(writer);
 *
 *     // Read hexadecimal data from a file
 *     HexDataReader reader = HexDataFactory.createReader("output", "bin");
 *     HexDataFactory.read(reader);
 *     String data = reader.getData();
 * }</pre>
 *
 * @author William Wu
 * @version 1.3
 * @since 1.3
 */
package hexio.hexdata;