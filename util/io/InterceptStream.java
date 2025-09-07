package util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An output stream that intercepts all written data, capturing it into an internal buffer.
 * This class extends {@link OutputStream} and provides methods to retrieve,
 * clear, and iterate over the buffered content. It is useful for logging or
 * debugging purposes where you want to capture output data while still
 * writing it to a target output stream.
 * <p>
 * Features:
 * <ul>
 *   <li>Writes all data to a specified target {@link OutputStream}.</li>
 *   <li>Captures all written data into an internal {@link StringBuilder} buffer.</li>
 *   <li>Provides methods to retrieve the current content of the buffer as a String.</li>
 *   <li>Allows clearing the buffer and retrieving its content before clearing.</li>
 *   <li>Optionally clears the buffer automatically after each full line is written.</li>
 *   <li>Supports iteration over lines or characters in the buffer with {@link Iterable}.</li>
 * </ul>
 * <p>
 * Note: It is recommended to manually clear instead of relying on autoClear, as that may lead to unexpected data loss.
 * If auto-clear is enabled, do not write to the stream while iterating over its content,
 * as it may lead to unexpected behavior.
 */
public class InterceptStream extends OutputStream {
    private final OutputStream target;
    private final StringBuilder buffer;
    private final boolean autoClear; // if true, clear buffer after each line
    /**
     * Creates an InterceptStream that writes to the specified target OutputStream
     * and captures all written data into an internal buffer.
     * @param target the target OutputStream to write to
     */
    public InterceptStream(OutputStream target){
        this.target = target;
        this.buffer = new StringBuilder();
        this.autoClear = false;
    }
    /**
     * Creates an InterceptStream that writes to the specified target OutputStream
     * and captures all written data into an internal buffer.
     * @param target the target OutputStream to write to
     * @param autoClear if true, the buffer is cleared automatically after each full line is written
     */
    public InterceptStream(OutputStream target, boolean autoClear){
        this.target = target;
        this.buffer = new StringBuilder();
        this.autoClear = autoClear;
    }
    /**
     * Returns the target OutputStream that this InterceptStream writes to.
     * @return the target OutputStream
     */
    public OutputStream getTargetStream() {
        return target;
    }
    /**
     * Writes the specified byte to the target stream and appends it to the internal buffer.
     * @param b the byte to write.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(int b) throws IOException {
        target.write(b);
        buffer.append((char)b);
        if (b == '\n') {
            autoClear();
        }
    }
    /**
     * Writes len bytes from the specified byte array starting at offset off to the target stream
     * and appends the same data to the internal buffer.
     * @param b     the data.
     * @param off   the start offset in the data.
     * @param len   the number of bytes to write.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        target.write(b, off, len);
        buffer.append(new String(b, off, len));
        autoClear();
    }
    /**
     * Writes the specified byte array to the target stream and appends it to the internal buffer.
     * @param b   the data.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(byte[] b) throws IOException {
        target.write(b);
        buffer.append(new String(b));
        autoClear();
    }
    /**
     * Flushes the target stream. This method should be called to ensure that all
     * buffered data is written out to the target stream.
     * @throws IOException if an I/O error occurs while flushing the target stream.
     */
    @Override
    public void flush() throws IOException {
        target.flush();
    }
    /**
     * Closes the target stream. This method should be called when the stream is no longer needed.
     * @throws IOException if an I/O error occurs while closing the target stream.
     */
    @Override
    public void close() throws IOException {
        target.close();
    }
    /**
     * Automatically clears the buffer if autoClear is enabled and a full line (ending with '\n') is present.
     */
    private void autoClear(){
        if (autoClear && buffer.indexOf("\n") != -1) {
            int lastNewlineIndex = buffer.lastIndexOf("\n");
            buffer.delete(0, lastNewlineIndex + 1); // Remove up to and including the last newline
        }
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
     * Returns the last, incomplete line from the buffer, if available.
     * A line is after all lines ending with a newline character ('\n').
     * If there are no full lines, the buffer itself is the last line.
     * <p>
     * This method is useful when used together with autoClear mode,
     * to get the current incomplete line being built up in the buffer.
     * @return the last line from the buffer, or the whole buffer if no newline is found
     */
    public String lastLine() {
        int lastNewlineIndex = buffer.lastIndexOf("\n");
        if (lastNewlineIndex == -1) {
            return buffer.toString(); // No newline found, return the whole buffer
        } else {
            return buffer.substring(lastNewlineIndex + 1); // Return the substring after the last newline
        }
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
     * <p>
     * Do not modify the buffer when autoClear is enabled, as it may lead to unexpected behavior.
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
                String line = nextLine();
                if (line == null) {
                    throw new NoSuchElementException("No complete line available");
                }
                return line;
            }
        };
    }
    /**
     * Returns an iterable over the characters in the buffer.
     * The iterable will return character by character in the buffer.
     * <p>
     * Do not modify the buffer when autoClear is enabled, as it may lead to unexpected behavior.
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
                if (buffer.length() == 0) {
                    throw new NoSuchElementException("No character available");
                }
                return nextChar();
            }
        };
    }
}
