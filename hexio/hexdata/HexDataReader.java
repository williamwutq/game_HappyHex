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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A utility class for reading and parsing hexadecimal-encoded data from files.
 * The {@code HexDataReader} class complements {@link HexDataWriter} by reading
 * data from files written in hexadecimal format, whether as text or binary files.
 * It supports extraction of various primitive data types encoded in hexadecimal
 * (e.g. {@code boolean}, {@code long}, {@code int}, {@code char}, {@code byte},
 * {@code short}, {@code String}, {@code float}, {@code double})
 * along with internal tracking to support sequential reading.
 * <p>
 * It also supports utility methods for data comparison, file path matching, and
 * automatic regeneration of a matching {@code HexDataWriter}.
 * <p>
 * Example usage:
 * <pre>{@code
 *     HexDataReader reader = new HexDataReader("output", "bin");
 *     reader.readBinary(); // Read from "output.bin"
 *     int number = reader.nextInt();
 *     String text = reader.nextString(5);
 * }</pre>
 *
 * @author William Wu
 * @version 1.3.3
 * @since 1.3
 */
public class HexDataReader {
    private final String filePath;
    private final String suffix;
    private String data;
    private int pointer;

    /**
     * Constructs a {@code HexDataReader} with a specified file path and suffix.
     * @param filePath the relative path to the file to read from
     * @param suffix   the file suffix (extension) to use for reading
     */
    public HexDataReader(String filePath, String suffix) {
        this.filePath = filePath;
        this.suffix = suffix;
        this.data = "";
        this.pointer = 0;
    }
    /**
     * Constructs a {@code HexDataReader} with a specified file path and suffix, and preloaded data.
     * Use this constructor with caution, as it may not reflect the state of the real file.
     * @param data     the initial data of the reader
     * @param filePath the relative path to the file to read from
     * @param suffix   the file suffix (extension) to use for reading
     * @see #HexDataReader(String, String)
     */
    public HexDataReader(String filePath, String suffix, String data) {
        this.filePath = filePath;
        this.suffix = suffix;
        this.pointer = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : data.toUpperCase().toCharArray()) {
            if (Character.digit(c, 16) != -1) {
                builder.append(c);
            } else {
                builder.append('0');
            }
        }
        this.data = builder.toString();
    }
    /**
     * Returns the relative file name to the file to read from, excluding suffix
     * @return the relative file name to the targeted file
     * @since 1.3.3
     */
    public String getFileName(){
        return filePath;
    }
    /**
     * Returns the suffix to the file to read from.
     * @return the suffix to the targeted file
     * @since 1.3.3
     */
    public String getFileSuffix(){
        return suffix;
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
     * Reads hexadecimal characters from a text file into internal data.
     * This will remove all existing data from the data field and use the data read from the file instead.
     * @throws IOException if file reading fails.
     */
    public void readText() throws IOException {
        data = Files.readString(Path.of(getFullPath() + ".txt")).replaceAll("\\s", "").toUpperCase();
        pointer = 0;
    }
    /**
     * Reads binary file contents and converts them into a hexadecimal string.
     * This will remove all existing data from the data field and use the data read from the file instead.
     * @throws IOException if file reading fails.
     */
    public void readBinary() throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(getFullPath()));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        data = sb.toString();
        pointer = 0;
    }

    /**
     * Clears the internal data and resets the pointer.
     */
    public void clear() {
        data = "";
        pointer = 0;
    }

    /**
     * Returns a new {@link HexDataWriter} with the same file path, suffix, and data.
     * @return a {@code HexDataWriter} initialized with current state.
     * @see HexDataWriter
     */
    public HexDataWriter getHexDataWriter() {
        HexDataWriter writer = new HexDataWriter(filePath, suffix);
        writer.addHex(data);
        return writer;
    }

    /**
     * Returns the current hexadecimal data as a hexadecimal string.
     * @return the internal hexadecimal string data.
     */
    public String getData() {
        return data;
    }
    /**
     * Returns a substring of the data from a specified index and length.
     * This handles out of bounds indexes automatically.
     * Replaces any out of bound portion with 0s. If the length is 0, returns an empty string.
     * @param index  the starting index.
     * @param length the number of characters to extract.
     * @return a hexadecimal substring.
     */
    public String get(int index, int length) {
        StringBuilder result = new StringBuilder();
        if (length < 0){
            index += length;
            length = -length;
        }
        if (index >= 0){
            if (index + length <= data.length()){
                result.append(data, index, index + length);
            } else if (index <= data.length()){
                result.append(data, index, data.length());
                for (int i = 0; i < index + length - data.length(); i ++){
                    result.append("0");
                }
            } else {
                for (int i = 0; i < length; i ++){
                    result.append("0");
                }
            }
        } else if (index + length >= 0){
            if (index + length <= data.length()) {
                for (int i = index; i < 0; i++) {
                    result.append("0");
                }
                result.append(data, 0, index + length);
            } else {
                for (int i = index; i < 0; i++) {
                    result.append("0");
                }
                result.append(data, 0, data.length());
                for (int i = 0; i < index + length - data.length(); i++) {
                    result.append("0");
                }
            }
        } else {
            for (int i = 0; i < length; i ++){
                result.append("0");
            }
        }
        return result.toString();
    }

    /**
     * Returns a {@code boolean} decoded from a single hexadecimal character at the given index.
     * <p>
     * The hexadecimal character is parsed to an integer. If the value is odd, returns true;
     * if even, returns false. This is equivalent to checking the least significant bit.
     * @param index the starting index.
     * @return the decoded {@code boolean} value.
     * @throws IOException if the decoding fails.
     */
    public boolean getBoolean(int index) throws IOException {
        try {
            int value = Integer.parseInt(get(index, 1), 16);
            return (value % 2) == 1;
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode boolean at index " + index, e);
        }
    }
    /**
     * Returns a {@code boolean} decoded from a specific bit position within a single
     * hexadecimal character at the given index.
     * <p>
     * The hexadecimal character is parsed to an integer, and the bit at the specified
     * position (0-3, where 0 is the least significant bit) is checked. Returns true if
     * the bit is 1, false if the bit is 0.
     * @param index the starting index of the hexadecimal character.
     * @param position the bit position (0-3) to decode, where 0 is the least significant bit.
     * @return the decoded {@code boolean} value at the specified bit position.
     * @throws IllegalArgumentException if position is not between 0 and 3.
     * @throws IOException if the decoding fails.
     */
    public boolean getBooleanAtBit(int index, int position) throws IOException {
        if (position < 0 || position > 3) {
            throw new IllegalArgumentException("Boolean position must be between 0 and 3");
        }
        try {
            int value = Integer.parseInt(get(index, 1), 16);
            return ((value >> position) & 1) == 1;
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode boolean bit at index " + index + ", position " + position, e);
        }
    }
    /**
     * Returns a {@code long} decoded from 16 hexadecimal characters at the given index.
     * @param index the starting index.
     * @return the decoded {@code long} value.
     * @throws IOException if the decoding fails.
     */
    public long getLong(int index) throws IOException {
        try {
            return Long.parseUnsignedLong(get(index, 16), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode long at index " + index, e);
        }
    }
    /**
     * Returns an {@code int} decoded from 8 hexadecimal characters at the given index.
     * @param index the starting index.
     * @return the decoded {@code int} value.
     * @throws IOException if the decoding fails.
     */
    public int getInt(int index) throws IOException {
        try {
            return Integer.parseUnsignedInt(get(index, 8), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode int at index " + index, e);
        }
    }
    /**
     * Returns a {@code short} decoded from 4 hexadecimal characters at the given index.
     * @param index the starting index.
     * @return the decoded {@code short} value.
     * @throws IOException if the decoding fails.
     */
    public short getShort(int index) throws IOException {
        try {
            return (short) Integer.parseInt(get(index, 4), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode short at index " + index, e);
        }
    }
    /**
     * Returns a {@code char} decoded from 4 hexadecimal characters at the given index.
     * @param index the starting index.
     * @return the decoded {@code char} value.
     * @throws IOException if the decoding fails.
     */
    public char getChar(int index) throws IOException {
        try {
            return (char) Integer.parseUnsignedInt(get(index, 4), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode char at index " + index, e);
        }
    }
    /**
     * Returns a {@code byte} decoded from 2 hexadecimal characters at the given index.
     * @param index the starting index.
     * @return the decoded {@code byte} value.
     * @throws IOException if the decoding fails.
     */
    public byte getByte(int index) throws IOException {
        try {
            return (byte) Integer.parseInt(get(index, 2), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode byte at index " + index, e);
        }
    }
    /**
     * Returns a string by reading a sequence of {@code char}s from the data.
     * @param index the starting index.
     * @param stringLength number of characters in the string.
     * @return the decoded {@code String}.
     * @throws IOException if the decoding fails.
     */
    public String getString(int index, int stringLength) throws IOException {
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < stringLength; i++) {
                builder.append(getChar(index + i * 4));
            }
        } catch (IOException e) {
            throw new IOException("Failed to decode string at index " + index + " with length " + stringLength, e);
        }
        return builder.toString();
    }
    /**
     * Returns a {@code double} decoded from 16 hexadecimal characters at the given index.
     * <p>
     * The 16 hexadecimal characters are parsed as a long value, which is then converted
     * to a double using {@link Double#longBitsToDouble(long)}.
     * @param index the starting index.
     * @return the decoded {@code double} value.
     * @throws IOException if the decoding fails.
     */
    public double getDouble(int index) throws IOException {
        try {
            return Double.longBitsToDouble(Long.parseUnsignedLong(get(index, 16), 16));
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode double at index " + index, e);
        }
    }
    /**
     * Returns a {@code float} decoded from 8 hexadecimal characters at the given index.
     * <p>
     * The 8 hexadecimal characters are parsed as an int value, which is then converted
     * to a float using {@link Float#intBitsToFloat(int)}.
     * @param index the starting index.
     * @return the decoded {@code float} value.
     * @throws IOException if the decoding fails.
     */
    public float getFloat(int index) throws IOException {
        try {
            return Float.intBitsToFloat(Integer.parseUnsignedInt(get(index, 8), 16));
        } catch (NumberFormatException e) {
            throw new IOException("Failed to decode float value at index " + index, e);
        }
    }

    /**
     * Return the current index of reading (the pointer).
     * The pointer is automatically undated when {@link #next} methods are called, or can be
     * explicitly incremented with the {@link #advance} methods. Pointers can not move backward
     * @return the pointer points to the current index in the hexadecimal data string.
     */
    public int pointer(){
        return pointer;
    }
    /**
     * Increment the index pointer to the current index in the hexadecimal data string by one.
     * This equals skipping one hexadecimal character.
     * @see #advance(int)
     */
    public void advance(){
        pointer ++;
    }
    /**
     * Increment the index pointer of the hexadecimal data string by a specific length.
     * This equals skipping the hexadecimal characters in this range.
     * @param length the length of the characters to skip.
     * @see #advance()
     */
    public void advance(int length){
        pointer += length;
    }
    /**
     * Reads the next hexadecimal character and advances the pointer.
     * @return the next hexadecimal character.
     */
    public String next() {
        String result = get(pointer, 1);
        pointer ++;
        return result;
    }
    /**
     * Reads the next string of hexadecimal characters and advances the pointer.
     * @param length the length of the hexadecimal string to read.
     * @return the next hexadecimal string of the certain length.
     */
    public String next(int length) {
        String result = get(pointer, length);
        pointer += length;
        return result;
    }
    /**
     * Reads the next {@code boolean} value and advances the pointer.
     * <p>
     * Decodes a single hexadecimal character at the current pointer.
     * Returns true if the parsed value is odd, false if even, and advances the pointer.
     * @return the next {@code boolean} value.
     * @throws IOException if the decoding fails.
     */
    public boolean nextBoolean() throws IOException {
        boolean result = getBoolean(pointer);
        pointer ++;
        return result;
    }
    /**
     * Reads the next {@code long} value and advances the pointer.
     * @return the next {@code long} value.
     * @throws IOException if the decoding fails.
     */
    public long nextLong() throws IOException {
        long result = getLong(pointer);
        pointer += 16;
        return result;
    }
    /**
     * Reads the next {@code int} value and advances the pointer.
     * @return the next {@code int} value.
     * @throws IOException if the decoding fails.
     */
    public int nextInt() throws IOException {
        int result = getInt(pointer);
        pointer += 8;
        return result;
    }
    /**
     * Reads the next {@code short} value and advances the pointer.
     * @return the next {@code short} value.
     * @throws IOException if the decoding fails.
     */
    public short nextShort() throws IOException {
        short result = getShort(pointer);
        pointer += 4;
        return result;
    }
    /**
     * Reads the next {@code char} value and advances the pointer.
     * @return the next {@code char} value.
     * @throws IOException if the decoding fails.
     */
    public char nextChar() throws IOException {
        char result = getChar(pointer);
        pointer += 4;
        return result;
    }
    /**
     * Reads the next {@code byte} value and advances the pointer.
     * @return the next {@code byte} value.
     * @throws IOException if the decoding fails.
     */
    public byte nextByte() throws IOException {
        byte result = getByte(pointer);
        pointer += 2;
        return result;
    }
    /**
     * Reads the next {@code String} of specified length and advances the pointer.
     * @param length number of characters in the string.
     * @return the decoded {@code String}.
     * @throws IOException if the decoding fails.
     */
    public String nextString(int length) throws IOException {
        String result = getString(pointer, length);
        pointer += length * 4;
        return result;
    }
    /**
     * Reads the next {@code double} value and advances the pointer.
     * <p>
     * Decodes 16 hexadecimal characters starting at the current pointer as a double.
     * and advances the pointer by 16.
     * @return the next {@code double} value.
     * @throws IOException if the decoding fails.
     */
    public double nextDouble() throws IOException {
        double result = getDouble(pointer);
        pointer += 16;
        return result;
    }
    /**
     * Reads the next {@code float} value and advances the pointer.
     * <p>
     * Decodes 8 hexadecimal characters starting at the current pointer as a float.
     * and advances the pointer by 8.
     * @return the next {@code float} value.
     * @throws IOException if the decoding fails.
     */
    public float nextFloat() throws IOException {
        float result = getFloat(pointer);
        pointer += 8;
        return result;
    }
    /**
     * Returns whether this reader and another read from the same file.
     * @param other the other reader.
     * @return true if file paths and suffixes match.
     */
    public boolean readSameFile(HexDataReader other) {
        return this.getFullPath().equals(other.getFullPath());
    }
    /**
     * Returns whether this reader contains the same data as another.
     * @param other the other reader.
     * @return true if data matches exactly.
     */
    public boolean containSameData(HexDataReader other) {
        return this.data.equals(other.data);
    }
    /**
     * Checks whether another object is a {@code HexDataReader} with the same data and file.
     * @param other the other object
     * @return true if both data and file match
     * @see #containSameData
     * @see #readSameFile
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof HexDataReader reader) {
            return readSameFile(reader) && containSameData(reader);
        }
        return false;
    }

    /**
     * Returns a string representation of the hexadecimal data stored in this reader.
     * @return the hexadecimal data string
     */
    public String toString() {
        return data;
    }
}

