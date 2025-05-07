/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package hexio.hexdata;

import java.io.IOException;

/**
 * A factory class providing utility methods to create and manage {@link HexDataWriter} and
 * {@link HexDataReader} instances for handling hexadecimal data operations. This class
 * simplifies the creation, writing, and reading of hexadecimal data to and from files,
 * as well as checking file path equality between writers and readers.
 * <p>
 * The factory methods encapsulate the instantiation and operation of {@code HexDataWriter}
 * and {@code HexDataReader}, handling file I/O operations and providing convenient
 * methods to perform common tasks such as writing hexadecimal data to a file or reading
 * binary data from a file. It also includes methods to compare file paths between
 * instances to ensure they operate on the same file.
 * <p>
 * Example usage:
 * <pre>{@code
 *     // Create a writer and write data
 *     HexDataWriter writer = HexDataFactory.createWriter("output", "bin");
 *     writer.addHex("A56C");
 *     HexDataFactory.write(writer);
 *
 *     // Create a reader and read data
 *     HexDataReader reader = HexDataFactory.createReader("output", "bin");
 *     HexDataFactory.read(reader);
 *
 *     // Check if writer and reader use the same file
 *     boolean sameFile = HexDataFactory.useSameFile(writer, reader);
 * }</pre>
 *
 * @author William Wu
 * @version 1.3
 * @since 1.3
 */
public class HexDataFactory {
    /**
     * Creates a new {@link HexDataWriter} instance with the specified file path and suffix.
     * @param filePath the relative path to the file where data will be written
     * @param suffix the file suffix (extension) for the output file
     * @return a new {@code HexDataWriter} instance configured with the given file path and suffix
     */
    public static HexDataWriter createWriter(String filePath, String suffix){
        return new HexDataWriter(filePath, suffix);
    }
    /**
     * Writes the data stored in the specified {@link HexDataWriter} to a binary
     * file and clears the writer's internal buffer.
     * @param writer the {@code HexDataWriter} containing the data to write
     * @return true if the write operation is successful and no I/O error occurs
     */
    public static boolean write(HexDataWriter writer){
        boolean success = false;
        try {
            writer.writeAsBinary();
            writer.clear();
            success = true;
        } catch (IOException e) {}
        return success;
    }
    /**
     * Writes specific data it to a binary file by creating a {@link HexDataWriter}.
     * @param filePath the relative path to the file where data will be written
     * @param suffix the file suffix (extension) for the output file
     * @param data the hexadecimal data to write
     * @return true if the write operation is successful and no I/O error occurs
     */
    public static boolean write(String filePath, String suffix, String data){
        boolean success = false;
        try {
            HexDataWriter writer = createWriter(filePath, suffix);
            writer.addHex(data);
            writer.writeAsBinary();
            success = true;
        } catch (IOException e) {}
        return success;
    }

    /**
     * Creates a new {@link HexDataReader} instance with the specified file path and suffix.
     * @param filePath the relative path to the file to read from
     * @param suffix the file suffix (extension) of the input file
     * @return a new {@code HexDataReader} instance configured with the given file path and suffix
     */
    public static HexDataReader createReader(String filePath, String suffix){
        return new HexDataReader(filePath, suffix);
    }
    /**
     * Reads binary data from the file associated with the specified
     * {@link HexDataReader} and stores it as hexadecimal data.
     * @param reader the {@code HexDataReader} to read data into
     * @return true if the read operation is successful and no I/O error occurs
     */
    public static boolean read(HexDataReader reader){
        boolean success = false;
        try {
            reader.readBinary();
            success = true;
        } catch (IOException e) {}
        return success;
    }
    /**
     * Creates a {@link HexDataReader}, reads binary data from the specified file,
     * and returns the reader with the loaded data.
     * @param filePath the relative path to the file to read from
     * @param suffix the file suffix (extension) of the input file
     * @return a {@code HexDataReader} instance with the data read from the file
     */
    public static HexDataReader read(String filePath, String suffix){
        HexDataReader reader = createReader(filePath, suffix);
        try {
            reader.readBinary();
        } catch (IOException e) {}
        return reader;
    }

    /**
     * Checks if two {@link HexDataWriter} or {@link HexDataReader} operate on the same file.
     * @param writer1 a {@code HexDataWriter}
     * @param writer2 a {@code HexDataWriter}
     * @return true if the two reader or writer use the same file path and suffix
     */
    public static boolean useSameFile(HexDataWriter writer1, HexDataWriter writer2){
        return writer1.writeToSameFile(writer2);
    }
    /**
     * Checks if two {@link HexDataWriter} or {@link HexDataReader} operate on the same file.
     * @param reader1 a {@code HexDataReader}
     * @param reader2 a {@code HexDataReader}
     * @return true if the two reader or writer use the same file path and suffix
     */
    public static boolean useSameFile(HexDataReader reader1, HexDataReader reader2){
        return reader1.readSameFile(reader2);
    }
    /**
     * Checks if two {@link HexDataWriter} or {@link HexDataReader} operate on the same file.
     * @param reader a {@code HexDataReader}
     * @param writer a {@code HexDataWriter}
     * @return true if the two reader or writer use the same file path and suffix
     */
    public static boolean useSameFile(HexDataReader reader, HexDataWriter writer){
        return writer.getFullPath().equals(reader.getFullPath());
    }
    /**
     * Checks if two {@link HexDataWriter} or {@link HexDataReader} operate on the same file.
     * @param writer a {@code HexDataWriter}
     * @param reader a {@code HexDataReader}
     * @return true if the two reader or writer use the same file path and suffix
     */
    public static boolean useSameFile(HexDataWriter writer, HexDataReader reader){
        return writer.getFullPath().equals(reader.getFullPath());
    }
}
