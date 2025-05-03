package hexio.hexdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;

/**
 * A utility class for recording and writing data in hexadecimal format to files.
 * The {@code HexDataWriter} class provides methods to accumulate hexadecimal data
 * from primitive data types (e.g., {@code boolean},{@code long}, {@code int}, {@code char},
 * {@code byte}, {@code String}, {@code float}, {@code double})
 * or arrays of such data and write the data to files in either text or binary format.
 * It also provides direct accumulation and serialization of already encoded hexadecimal Strings
 * or character arrays, as well as convenience methods for adding spacing and dividers.
 * It supports flexible file path, suffix configuration, data comparison, and data removal.
 * <p>
 * The class maintains an internal string buffer ({@code data}) to store hexadecimal characters
 * (0-9, A-F) and provides methods to add data in a controlled manner, ensuring proper hexadecimal
 * encoding. It also supports writing the accumulated data to a file with a specified path and suffix,
 * either as a human-readable text file (with a .txt extension) or as a binary file.
 * <p>
 * This class is particularly useful for applications that need to log or store data in hexadecimal
 * format, such as debugging, data serialization, and efficient data storage for low level programs.
 * <p>
 * Example usage:
 * <pre>{@code
 *     HexDataWriter writer = new HexDataWriter("output", "bin");
 *     writer.add(255); // Adds "000000FF" (8 hex characters for an int)
 *     writer.addHex("A56C"); // Adds "A56C" (add hexadecimal encoded String directly)
 *     writer.add("Hello"); // Adds hexadecimal encoding of "Hello"
 *     writer.writeAsBinary(); // Writes data to "output.bin"
 *     writer.writeAsText(); // Writes data to "output.bin.txt"
 * }</pre>
 *
 * @author William Wu
 * @version 1.3
 * @since 1.3
 */
public class HexDataWriter {
    private String data;
    private String filePath;
    private String suffix;

    /**
     * Create an empty {@code HexDataWriter} that can be used to record and write data.
     * The default file will be named data with no suffix.
     */
    public HexDataWriter(){
        data = "";
        filePath = "data";
        suffix = "";
    }
    /**
     * Create a {@code HexDataWriter} with the specific file path and file suffix.
     * that can be used to record and write data to the specific file.
     * @param filePath the relative path to the file to write to.
     * @param suffix the suffix of the file to write to.
     */
    public HexDataWriter(String filePath, String suffix){
        data = "";
        setFile(filePath, suffix);
    }

    /**
     * Change the file path and file suffix of the {@code HexDataWriter}.
     * @param filePath the new relative path to the file to write to.
     * @param suffix the new suffix of the file to write to.
     */
    public void setFile(String filePath, String suffix){
        this.filePath = filePath;
        this.suffix = suffix;
    }
    /**
     * Change the file path of the {@code HexDataWriter}.
     * @param filePath the new relative path to the file to write to.
     */
    public void changeFile(String filePath){
        this.filePath = filePath;
    }
    /**
     * Remove all data from this logger.
     */
    public void clear(){
        this.data = "";
    }
    /**
     * Returns a new {@link HexDataReader} with the same file path, suffix, and data.
     * This bypasses writing to file by directly pass the data to {@code HexDataReader}.
     * @return a {@code HexDataReader} initialized with current state.
     * @see HexDataReader
     */
    public HexDataReader getHexDataReader() {
        return new HexDataReader(filePath, suffix, data);
    }
    /**
     * Get the full file path of the {@code HexDataWriter}.
     * @return the full file path of the file written to, include the suffix.
     */
    public String getFullPath(){
        if (suffix.isEmpty()) {
            return filePath;
        } else return filePath + "." + suffix;
    }

    /**
     * Writes the data stored in this logger to the appropriate text file.
     * If an existing file is found, it writes to it; otherwise, it creates a new one.
     * The suffix of the file will be the .{@code s}.txt for easier recognition, s
     * represents the suffix of the file defined for this particular logger.
     * @throws IOException If writing data to file fails due to other issues.
     */
    public void writeAsText() throws IOException {
        Path path = Path.of(getFullPath() + ".txt");
        Files.writeString(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    /**
     * Writes the data stored in this logger to the appropriate binary file.
     * If an existing file is found, it writes to it; otherwise, it creates a new one.
     * @throws IOException If writing data to file fails due to other issues.
     */
    public void writeAsBinary() throws IOException {
        Path path = Path.of(getFullPath());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if(data.length() % 2 != 0) data += "0"; // Add 0 to the end if needed
        char[] charArray = data.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i+=2) {
            int value1 = Character.digit(charArray[i], 16);
            int value2 = Character.digit(charArray[i+1], 16);
            // For invalid hex numbers, use 0. This should never be reached.
            if (value1 == -1) value1 = 0;
            if (value2 == -1) value2 = 0;
            output.write(value1 << 4 | value2);
        }
        Files.write(path, output.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Return whether this {@code HexDataWriter} contains the same data as another writer.
     * @param other the other writer to compare to.
     * @return true if this writer contains the exact same data as the other writer; false otherwise.
     */
    public boolean containSameData(HexDataWriter other){
        return this.data.equals(other.data);
    }
    /**
     * Return whether this {@code HexDataWriter} writes to the same file as another writer.
     * @param other the other writer to compare to.
     * @return true if this writer writes to the exact same file as the other writer; false otherwise.
     * @see #getFullPath()
     */
    public boolean writeToSameFile(HexDataWriter other){
        return this.getFullPath().equals(other.getFullPath());
    }
    /**
     * Return whether this {@code HexDataWriter} is the same as another object.
     * @param other the other object to compare to.
     * @return true if this writer and the other writer are both {@code HexDataWriter}
     *         that {@link #containSameData contain the same data} and
     *         {@link #writeToSameFile write to the same file}; false otherwise.
     */
    public boolean equals(Object other){
        if (other instanceof HexDataWriter writer){
            return containSameData(writer) && writeToSameFile(writer);
        } else return false;
    }

    /**
     * Return the String representation of the data in hexadecimal format.
     * This string contains characters 0-9, A-F.
     * @return A String in hexadecimal format representing the data.
     */
    public String toString(){
        return data;
    }

    /**
     * Add an empty hexadecimal character to the data stored in the {@code HexDataWriter}.
     */
    public void add() {
        data += "0";
    }
    /**
     * Add a {@code boolean} value encoded in hexadecimal character to the data stored in the {@code HexDataWriter}.
     * <p>
     * The boolean value is encoded as a single hexadecimal character: 'F' for true and '0' for false.
     * @param value the {@code boolean} value to be added.
     */
    public void add(boolean value) {
        data += value ? "F" : "0";
    }
    /**
     * Add a {@code long} value encoded in hexadecimal character to the data stored in the {@code HexDataWriter}.
     * <p>
     * This will add 16 hexadecimal characters.
     * @param value the {@code long} value to be added.
     */
    public void add(long value) {
        data += String.format("%016X", value);
    }
    /**
     * Add a {@code int} value encoded in hexadecimal character to the data stored in the {@code HexDataWriter}.
     * <p>
     * This will add 8 hexadecimal characters.
     * @param value the {@code int} value to be added.
     */
    public void add(int value) {
        data += String.format("%08X", value);
    }
    /**
     * Add a {@code char} value encoded in hexadecimal character to the data stored in the {@code HexDataWriter}.
     * <p>
     * This method can also be used to convert characters in {@link String}.
     * This will add 4 hexadecimal characters.
     * @param value the {@code char} value to be added.
     */
    public void add(char value) {
        data += String.format("%04X", (int) value);
    }
    /**
     * Add a {@link String} encoded in hexadecimal character to the data stored in the {@code HexDataWriter}.
     * <p>
     * The string will be converted to an array of characters, which will each be added by {@link #add(char)}.
     * The characters in the {@code String} will be added sequentially in the order of appearance,
     * each value will add 4 hexadecimal characters to the data.
     * @param value the {@link String} to be added.
     */
    public void add(String value) {
        for (char c : value.toCharArray()) {
            add(c);
        }
    }
    /**
     * Add a {@code byte} value encoded in hexadecimal character to the data stored in the {@code HexDataWriter}.
     * <p>
     * This will add 2 hexadecimal characters.
     * @param value the {@code byte} value to be added.
     */
    public void add(byte value) {
        data += String.format("%02X", value);
    }
    /**
     * Add a {@code double} value encoded in hexadecimal characters to the data stored in the {@code HexDataWriter}.
     * <p>
     * The double value is converted to its raw long bits using {@link Double#doubleToRawLongBits(double)},
     * and then encoded as 16 hexadecimal characters.
     * @param value the {@code double} value to be added.
     */
    public void add(double value) {
        data += String.format("%016X", Double.doubleToRawLongBits(value));
    }
    /**
     * Add a {@code float} value encoded in hexadecimal characters to the data stored in the {@code HexDataWriter}.
     * <p>
     * The float value is converted to its raw int bits using {@link Float#floatToRawIntBits(float)},
     * and then encoded as 8 hexadecimal characters.
     * @param value the {@code float} value to be added.
     */
    public void add(float value) {
        data += String.format("%08X", Float.floatToRawIntBits(value));
    }

    /**
     * Add an array of {@code boolean} values encoded in hexadecimal characters
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * Every four boolean values are encoded into a single hexadecimal character.
     * If the array length is not divisible by 4, false values are appended to complete
     * the last character. The boolean values are packed in little-endian order, where
     * the first boolean corresponds to the least significant bit.
     * @param values the {@code boolean} values to be added.
     * @see #add(boolean)
     */
    public void add(boolean[] values) {
        int fullLength = (values.length + 3) / 4 * 4; // Round up to multiple of 4
        for (int i = 0; i < fullLength; i += 4) {
            int hexValue = 0;
            for (int j = 0; j < 4 && i + j < values.length; j++) {
                if (values[i + j]) {
                    // Set bit j for true
                    hexValue |= (1 << j);
                }
            }
            data += String.format("%X", hexValue);
        }
    }
    /**
     * Add an array of {@code long} values encoded in hexadecimal character
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The values will be added sequentially in the order of appearance in the array,
     * each value will add 16 hexadecimal characters to the data.
     * There is no separation characters in between values.
     * @param values the {@code long} values to be added.
     * @see #add(long)
     */
    public void add(long[] values) {
        for (long value : values) {
            add(value);
        }
    }
    /**
     * Add an array of {@code int} values encoded in hexadecimal character
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The values will be added sequentially in the order of appearance in the array,
     * each value will add 8 hexadecimal characters to the data.
     * There is no separation characters in between values.
     * @param values the {@code int} values to be added.
     * @see #add(int)
     */
    public void add(int[] values) {
        for (int value : values) {
            add(value);
        }
    }
    /**
     * Add an array of {@code char} values encoded in hexadecimal character
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The values will be added sequentially in the order of appearance in the array,
     * each value will add 4 hexadecimal characters to the data.
     * There is no separation characters in between values.
     * <p>
     * This method can also be used to convert characters in {@link String},
     * although {@link #add(String)} is preferred over this.
     * @param values the {@code char} values to be added.
     * @see #add(char)
     */
    public void add(char[] values) {
        for (char value : values) {
            add(value);
        }
    }
    /**
     * Add an array of {@code byte} values encoded in hexadecimal character
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The values will be added sequentially in the order of appearance in the array,
     * each value will add 2 hexadecimal characters to the data.
     * There is no separation characters in between values.
     * @param values the {@code byte} values to be added.
     * @see #add(byte)
     */
    public void add(byte[] values) {
        for (byte value : values) {
            add(value);
        }
    }
    /**
     * Add an array of {@link String} encoded in hexadecimal character
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The strings will be added sequentially in the order of appearance in the array.
     * There is no separation characters in between the input strings.
     * @param values the {@code String} inputs to be added.
     * @see #add(String)
     */
    public void add(String[] values) {
        for (String value : values) {
            add(value);
        }
    }
    /**
     * Add an array of {@code double} values encoded in hexadecimal characters
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The values will be added sequentially in the order of appearance in the array,
     * each value will add 16 hexadecimal characters to the data.
     * There is no separation characters in between values.
     * @param values the {@code double} values to be added.
     * @see #add(double)
     */
    public void add(double[] values) {
        for (double value : values) {
            add(value);
        }
    }
    /**
     * Add an array of {@code float} values encoded in hexadecimal characters
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * The values will be added sequentially in the order of appearance in the array,
     * each value will add 8 hexadecimal characters to the data.
     * There is no separation characters in between values.
     * @param values the {@code float} values to be added.
     * @see #add(float)
     */
    public void add(float[] values) {
        for (float value : values) {
            add(value);
        }
    }

    /**
     * Add a defined length of spacing hexadecimal characters ("0")
     * to the data stored in the {@code HexDataWriter}.
     * @param length the length of the spacing, or number of 0s wanted.
     * @see #add()
     */
    public void addSpace(int length){
        for(int i = 0; i < length; i ++){
            data += "0";
        }
    }
    /**
     * Add a defined length of hexadecimal character divider ("FF")
     * to the data stored in the {@code HexDataWriter}.
     * @param length the length of the dividers, which
     *               is half of the number of characters added.
     */
    public void addDivider(int length){
        for(int i = 0; i < length; i ++){
            data += "FF";
        }
    }

    /**
     * Removes a specified number of hexadecimal characters
     * from the end of the data stored in this {@code HexDataWriter}.
     * <p>
     * If the specified length is greater than or
     * equal to the current data length, all data is removed.
     * @param length the number of hexadecimal characters to remove from the end of the data.
     */
    public void remove(int length) {
        if (length >= data.length()) {
            data = "";
        } else {
            data = data.substring(0, data.length() - length);
        }
    }

    /**
     * Add an encoded {@link String} of hexadecimal characters
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * This will check the content of the input and purify
     * it by rejecting all invalid characters as 0.
     * @param hex the hexadecimal {@link String} encoding information to be added.
     * @see #addHex(char[])
     */
    public void addHex(String hex) {
        for (char c : hex.toUpperCase().toCharArray()) {
            if (Character.digit(c, 16) != -1) {
                data += c;
            } else {
                data += '0';
            }
        }
    }
    /**
     * Add a series of encoded {@link String} of hexadecimal characters
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * This will check the content of the inputs and purify
     * them by rejecting all invalid characters as 0.
     * <p>
     * The inputs will be added sequentially in the order of appearance in the array,
     * there is no separation characters in between input hexadecimal strings.
     * @param hexStrings array of all hexadecimal {@link String} encoding information to be added.
     * @see #addHex(String)
     */
    public void addHex(String[] hexStrings) {
        for (String hex : hexStrings) {
            addHex(hex);
        }
    }
    /**
     * Add an encoded hexadecimal character array
     * to the data stored in the {@code HexDataWriter}.
     * <p>
     * This will check the content of the input and purify
     * it by rejecting all invalid characters as 0.
     * @param hexChars the hexadecimal character array encoding information to be added.
     * @see #addHex(String)
     */
    public void addHex(char[] hexChars) {
        for (char c : hexChars) {
            if (Character.digit(c, 16) != -1) {
                data += Character.toUpperCase(c);
            } else {
                data += '0';
            }
        }
    }
}
