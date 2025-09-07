package util.io;

import java.io.OutputStream;
import java.util.Iterator;

/**
 * A debugging output stream that captures written data into an internal buffer.
 * This class extends {@link OutputStream} and provides methods to retrieve,
 * clear, and iterate over the buffered content. It is useful for logging or
 * debugging purposes where you want to capture output data without sending it
 * to a physical output destination.
 * <p>
 * Features:
 * <ul>
 *   <li>Captures all written data into an internal {@link StringBuilder} buffer.</li>
 *   <li>Provides methods to retrieve the current content of the buffer as a String.</li>
 *   <li>Allows clearing the buffer and retrieving its content before clearing.</li>
 *   <li>Supports iteration over lines or characters in the buffer with {@link Iterable}.</li>
 * </ul>
 */
public class DebugStream extends OutputStream {
    private final StringBuilder buffer = new StringBuilder();
    // Write implementation
    /**
     * Writes the specified byte to the buffer.
     *
     * @param b the byte to write.
     */
    @Override
    public void write(int b) {
        buffer.append((char) b); // Append the byte as a character
    }
    /**
     * Writes len bytes from the specified byte array starting at offset off to the buffer.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     */
    @Override
    public void write(byte[] b, int off, int len) {
        buffer.append(new String(b, off, len));
    }
    /**
     * Writes the specified byte array to the buffer.
     *
     * @param b   the data.
     */
    @Override
    public void write(byte[] b) {
        buffer.append(new String(b));
    }
    // Content management
    /**
     * Retrieves the current content of the buffer as a String.
     * This is useful for logging or debugging purposes.
     * @return the content of the buffer
     */
    public String getContent() {
        return buffer.toString(); // retrieves what was written
    }
    /**
     * Clears the buffer and returns its content before clearing.
     * This is useful for logging or debugging purposes.
     * @return the content of the buffer before clearing
     */
    public String clearContent() {
        String content = buffer.toString();
        buffer.setLength(0); // clear the buffer
        return content;
    }
    /**
     * Clears the buffer without returning its content.
     * This is useful when you want to discard the current content.
     */
    public void clear() {
        buffer.setLength(0); // clear the buffer
    }
    // Iterator implementation
    /**
     * Checks if there is any content in the buffer.
     * @return true if there is content, false otherwise
     */
    public boolean hasNext() {
        return buffer.length() > 0;
    }
    /**
     * Returns the next line from the buffer, if available.
     * A line is considered to end with a newline character ('\n').
     * If no complete line is available, returns null.
     * @return the next line from the buffer, or null if no complete line is available
     */
    public String nextLine() {
        int newlineIndex = buffer.indexOf("\n");
        if (newlineIndex == -1) {
            return null; // No complete line available
        }
        String line = buffer.substring(0, newlineIndex); // Exclude the newline character
        buffer.delete(0, newlineIndex + 1); // Remove the line from the buffer, include the newline character
        return line;
    }
    /**
     * Returns the next character from the buffer, if available.
     * If no character is available, returns the null character ('\0').
     * @return the next character from the buffer, or '\0' if no character is available
     */
    public char nextChar() {
        if (buffer.length() == 0) {
            return '\0'; // No content available
        }
        char c = buffer.charAt(0); // Get the first character
        buffer.deleteCharAt(0); // Remove the character from the buffer
        return c;
    }
    /**
     * Returns an iterable over the lines in the buffer.
     * The iterable will only return complete lines (ending with '\n').
     * @return an iterable over the lines in the buffer
     */
    public Iterable<String> asLineIterable() {
        return () -> new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return buffer.indexOf("\n") != -1; // Check if there is a complete line available
            }
            @Override
            public String next() {
                return nextLine();
            }
        };
    }
    /**
     * Returns an iterable over the characters in the buffer.
     * The iterable will return character by character in the buffer.
     * @return an iterable over the characters in the buffer
     */
    public Iterable<Character> asCharIterable() {
        return () -> new Iterator<Character>() {
            @Override
            public boolean hasNext() {
                return buffer.length() > 0;
            }
            @Override
            public Character next() {
                return nextChar();
            }
        };
    }
}
