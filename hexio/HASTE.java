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

package hexio;

import GUI.GameEssentials;
import hex.HexEngine;

import java.io.*;
import java.util.zip.CRC32;

/**
 * The {@code HASTE} class converts a {@link HexEngine} to and from HASTE (HexAgonal Sprite Transfer Standard codEc)
 * containers, as defined in {@code HASTE.txt}. HappyHex supports only a subset of HASTE, so this class has no
 * general sprite object; it works on engines directly.
 * <p>
 * Supported containers:
 * <ul>
 *     <li>Side lengths {@code A = B = C = r}, which are exactly the engines of radius {@code r}. The cell order of
 *         HASTE is the block order of {@code HexEngine}, so blocks are stored unchanged.</li>
 *     <li>FourCC {@code "MASK"}, a bitmask with 1 bit per cell (p = 0). A set bit is a filled block.
 *         Colors are not stored.</li>
 *     <li>FourCC {@code "RGBA"}, sRGBA with 32 bits per cell (p = 5). The bytes of a cell are R, G, B, A in that
 *         order. An alpha of 0 is an empty block; any other alpha is a filled block.</li>
 *     <li>FourCC {@code "BGRA"}, the same as {@code "RGBA"} but the bytes of a cell are B, G, R, A in that order.</li>
 * </ul>
 * Anything else is rejected with an {@link IOException} on decoding. Direction and scale describe only how a sprite is
 * presented and are never needed to decode the cells, so decoding ignores them. Encoding writes direction 3 and scale 0,
 * the orientation of the HappyHex coordinate diagram, unless told otherwise.
 * <p>
 * Colors of blocks are palette indices that depend on the theme. The {@code "RGBA"} methods convert them with
 * {@link GameEssentials#generateColor(int)}, so they show the colors of the current theme. Because an RGBA value is not
 * a palette index, reading maps each color back to the nearest color of the palette, which is exact for any
 * container written by {@link #encodeRgba(HexEngine)} under the same theme.
 *
 * @version 2.1
 * @author William Wu
 * @since 2.1
 */
public final class HASTE {
    private HASTE() {}

    /** The FourCC of a bitmask container, {@code "MASK"}. */
    public static final int FOURCC_MASK = 0x4D41534B;
    /** The FourCC of an sRGBA container, {@code "RGBA"}. */
    public static final int FOURCC_RGBA = 0x52474241;
    /** The FourCC of an sRGBA container with blue first, {@code "BGRA"}. */
    public static final int FOURCC_BGRA = 0x42475241;
    /** The direction written by default, which matches the HappyHex coordinate diagram. */
    public static final int DEFAULT_DIRECTION = 3;
    /** The scale written by default, which is one sprite cell per base cell. */
    public static final int DEFAULT_SCALE = 0;
    /** The largest engine radius that can be decoded by this. */
    public static final int MAX_RADIUS = 1024;
    /** The number of palette colors, indices 0 to 11. Larger indices render as the default filled color. */
    private static final int PALETTE_SIZE = 12;

    private static final int HEADER_SIZE = 13;
    private static final int CRC_SIZE = 4;
    private static final int MASK_EXPONENT = 0;
    private static final int RGBA_EXPONENT = 5; // Shared by RGBA and BGRA

    // Write

    /**
     * Encodes an engine as a {@code "MASK"} container with the default direction and scale.
     *
     * @param engine the engine, whose block states are stored
     * @return the container
     * @throws IllegalArgumentException if the engine is null or has a radius above {@link #MAX_RADIUS}
     */
    public static byte[] encodeMask(HexEngine engine) {
        return encodeMask(engine, DEFAULT_DIRECTION, DEFAULT_SCALE);
    }

    /**
     * Encodes an engine as a {@code "MASK"} container.
     *
     * @param engine    the engine, whose block states are stored
     * @param direction the direction, 0 to 11
     * @param scale     the scale index, 0 to 4095
     * @return the container
     * @throws IllegalArgumentException if the engine is null, too large, or direction or scale is out of range
     */
    public static byte[] encodeMask(HexEngine engine, int direction, int scale) {
        if (engine == null) throw new IllegalArgumentException("Engine is null");
        if (engine.getRadius() < 1 || engine.getRadius() > MAX_RADIUS) {
            // In general, this should not happen
            throw new IllegalArgumentException("Engine radius must be 1 to " + MAX_RADIUS + ": " + engine.getRadius());
        }
        int n = engine.length();
        byte[] out = container(engine.getRadius(), direction, scale, FOURCC_MASK, MASK_EXPONENT, (n + 7) / 8);
        for (int i = 0; i < n; i++) {
            if (engine.getBlock(i).getState()) {
                out[HEADER_SIZE + (i >> 3)] |= (byte) (1 << (i & 7));
            }
        }
        return seal(out);
    }

    /**
     * Encodes an engine as an {@code "RGBA"} container with the default direction and scale. A filled block is
     * written as its color from {@link GameEssentials#generateColor(int)}, and an empty block is written fully
     * transparent.
     *
     * @param engine the engine, whose block states and colors are stored
     * @return the container
     * @throws IllegalArgumentException if the engine is null or has a radius above {@link #MAX_RADIUS}
     */
    public static byte[] encodeRgba(HexEngine engine) {
        return encodeRgba(engine, DEFAULT_DIRECTION, DEFAULT_SCALE);
    }

    /**
     * Encodes an engine as an {@code "RGBA"} container, see {@link #encodeRgba(HexEngine)}.
     *
     * @param engine    the engine, whose block states and colors are stored
     * @param direction the direction, 0 to 11
     * @param scale     the scale index, 0 to 4095
     * @return the container
     * @throws IllegalArgumentException if the engine is null, too large, or direction or scale is out of range
     */
    public static byte[] encodeRgba(HexEngine engine, int direction, int scale) {
        return encodeColor(engine, direction, scale, FOURCC_RGBA);
    }

    /**
     * Encodes an engine as a {@code "BGRA"} container with the default direction and scale. This is
     * {@link #encodeRgba(HexEngine)} with the red and blue bytes of each cell swapped.
     *
     * @param engine the engine, whose block states and colors are stored
     * @return the container
     * @throws IllegalArgumentException if the engine is null or has a radius above {@link #MAX_RADIUS}
     */
    public static byte[] encodeBgra(HexEngine engine) {
        return encodeBgra(engine, DEFAULT_DIRECTION, DEFAULT_SCALE);
    }

    /**
     * Encodes an engine as a {@code "BGRA"} container, see {@link #encodeBgra(HexEngine)}.
     *
     * @param engine    the engine, whose block states and colors are stored
     * @param direction the direction, 0 to 11
     * @param scale     the scale index, 0 to 4095
     * @return the container
     * @throws IllegalArgumentException if the engine is null, too large, or direction or scale is out of range
     */
    public static byte[] encodeBgra(HexEngine engine, int direction, int scale) {
        return encodeColor(engine, direction, scale, FOURCC_BGRA);
    }

    /** Encodes an engine as a 32-bit color container, the FourCC being either RGBA or BGRA. */
    private static byte[] encodeColor(HexEngine engine, int direction, int scale, int fourCC) {
        if (engine == null) throw new IllegalArgumentException("Engine is null");
        if (engine.getRadius() < 1 || engine.getRadius() > MAX_RADIUS) {
            // In general, this should not happen
            throw new IllegalArgumentException("Engine radius must be 1 to " + MAX_RADIUS + ": " + engine.getRadius());
        }
        int n = engine.length();
        byte[] out = container(engine.getRadius(), direction, scale, fourCC, RGBA_EXPONENT, 4 * n);
        for (int i = 0; i < n; i++) {
            if (!engine.getBlock(i).getState()) continue; // transparent black is all zeros
            // A filled block must not read back as empty, so its alpha is never 0.
            int argb = GameEssentials.generateColor(engine.getBlock(i).getColor()).getRGB() | 0xFF000000;
            int at = HEADER_SIZE + 4 * i;
            boolean bgra = fourCC == FOURCC_BGRA;
            out[at] =     (byte) (bgra ? argb : argb >>> 16); // R or B
            out[at + 1] = (byte) (argb >>>  8);               // G
            out[at + 2] = (byte) (bgra ? argb >>> 16 : argb); // B or R
            out[at + 3] = (byte) (argb >>> 24);               // A
        }
        return seal(out);
    }

    /**
     * Writes an engine to a stream as a {@code "MASK"} container. The stream is not closed.
     *
     * @param engine the engine
     * @param out    the stream
     * @throws IOException if writing fails
     * @see #encodeMask(HexEngine)
     */
    public static void writeMask(HexEngine engine, OutputStream out) throws IOException {
        out.write(encodeMask(engine));
    }
    /**
     * Writes an engine to a stream as an {@code "RGBA"} container. The stream is not closed.
     *
     * @param engine the engine
     * @param out    the stream
     * @throws IOException if writing fails
     * @see #encodeRgba(HexEngine)
     */
    public static void writeRgba(HexEngine engine, OutputStream out) throws IOException {
        out.write(encodeRgba(engine));
    }
    /**
     * Writes an engine to a stream as a {@code "BGRA"} container. The stream is not closed.
     *
     * @param engine the engine
     * @param out    the stream
     * @throws IOException if writing fails
     * @see #encodeBgra(HexEngine)
     */
    public static void writeBgra(HexEngine engine, OutputStream out) throws IOException {
        out.write(encodeBgra(engine));
    }

    // Read and Decode

    /**
     * Decodes a container held in its entirety by a buffer.
     * <p>
     * A {@code "MASK"} container gives an engine with default colors. For an {@code "RGBA"} or {@code "BGRA"} container, a cell with alpha
     * 0 is an empty block with the default color, and any other cell is a filled block whose color index is the palette
     * color nearest to the cell, see {@link #nearestColor(int)}.
     *
     * @param container the container, exactly {@code 17 + D} bytes
     * @return the engine
     * @throws IOException if the container is invalid, or is not {@code "MASK"}, {@code "RGBA"} or {@code "BGRA"} with
     *                     {@code A = B = C}, or exceeds {@link #MAX_RADIUS}
     */
    public static HexEngine decode(byte[] container) throws IOException {
        if (container == null || container.length < HEADER_SIZE + CRC_SIZE)
            throw new IOException("HASTE container is truncated");
        int radius = checkHeader(container);
        int fourCC = fourCC(container);
        int n = 1 + 3 * radius * (radius - 1);
        int size = fourCC == FOURCC_MASK ? (n + 7) / 8 : 4 * n;
        if (container.length != HEADER_SIZE + size + CRC_SIZE)
            throw new IOException("HASTE container length " + container.length + " does not match header, expected "
                    + (HEADER_SIZE + size + CRC_SIZE));
        CRC32 crc = new CRC32();
        crc.update(container, 0, HEADER_SIZE + size);
        long stored = 0;
        for (int i = 0; i < 4; i++) stored |= (container[HEADER_SIZE + size + i] & 0xFFL) << (8 * i);
        if (crc.getValue() != stored) throw new IOException("HASTE CRC mismatch");

        HexEngine engine = new HexEngine(radius);
        if (fourCC == FOURCC_MASK) {
            // Decode Bitmask
            int tail = n & 7;
            if (tail != 0 && (container[HEADER_SIZE + size - 1] & ~((1 << tail) - 1)) != 0) {
                throw new IOException("HASTE padding bits are not zero");
            }
            for (int i = 0; i < n; i++) {
                if ((container[HEADER_SIZE + (i >> 3)] >> (i & 7) & 1) != 0) {
                    engine.setState(i, true);
                }
            }
        } else {
            // Decode Color
            boolean bgra = fourCC == FOURCC_BGRA;
            for (int i = 0; i < n; i++) {
                int at    = HEADER_SIZE + 4 * i;
                int alpha = container[at + 3] & 0xFF;
                if (alpha == 0) continue;
                int first = container[at] & 0xFF, third = container[at + 2] & 0xFF; // R and B, or B and R
                int argb = alpha << 24                                 // A
                         | (bgra ? third : first) << 16                // R
                         | (container[at + 1] & 0xFF) << 8             // G
                         | (bgra ? first : third);                     // B
                engine.setState(i, true);
                engine.setColor(i, nearestColor(argb));
            }
        }
        return engine;
    }

    /**
     * Reads one container from a stream. The length of a container is implied by its header, so exactly one container
     * is consumed. The stream is not closed.
     *
     * @param in the stream
     * @return the engine
     * @throws IOException if the stream ends early, or the container is invalid or unsupported
     */
    public static HexEngine read(InputStream in) throws IOException {
        byte[] header = in.readNBytes(HEADER_SIZE);
        if (header.length < HEADER_SIZE) throw new IOException("HASTE container is truncated");
        int radius = checkHeader(header);
        int n = 1 + 3 * radius * (radius - 1);
        int size = fourCC(header) == FOURCC_MASK ? (n + 7) / 8 : 4 * n;
        byte[] rest = in.readNBytes(size + CRC_SIZE);
        if (rest.length < size + CRC_SIZE) throw new IOException("HASTE container is truncated");
        byte[] container = new byte[HEADER_SIZE + rest.length];
        System.arraycopy(header, 0, container, 0, HEADER_SIZE);
        System.arraycopy(rest, 0, container, HEADER_SIZE, rest.length);
        return decode(container);
    }

    // Helpers

    /**
     * Returns the color index of the palette color nearest to an ARGB value, comparing red, green and blue by squared
     * distance. The candidates are the default filled color (-2) and the palette colors 0 to 11 of the current theme,
     * and the first of equally near candidates wins, so the default filled color wins ties.
     *
     * @param argb the ARGB value, whose alpha is ignored
     * @return -2 or a color index from 0 to 11
     */
    public static int nearestColor(int argb) {
        int best = -2;
        long bestDistance = Long.MAX_VALUE;
        for (int index = -2; index < PALETTE_SIZE; index++) {
            if (index == -1) continue; // the empty block color is not a filled block color
            int rgb = GameEssentials.generateColor(index).getRGB();
            long dr = (argb >> 16 & 0xFF) - (rgb >> 16 & 0xFF); // R
            long dg = (argb >>  8 & 0xFF) - (rgb >>  8 & 0xFF); // G
            long db = (argb       & 0xFF) - (rgb       & 0xFF); // B
            long distance = dr * dr + dg * dg + db * db;        // Squared distance
            if (distance < bestDistance) {
                bestDistance = distance;
                best = index;
            }
        }
        return best;
    }

    /**
     * Allocates a container, make header, and zero its data
     * @param radius The radius of the container
     * @param direction The direction of the container, in [0..12)
     * @param scale The scale of the container, in [0..4095]
     * @param fourCC The FOURCC packed into an int. See {@link HASTE#fourCC}.
     * @param exponent exponent of the container
     * @param dataSize the size of the data in bytes
     * @throws IllegalArgumentException if direction or scale is out of range
     */
    private static byte[] container(int radius, int direction, int scale, int fourCC, int exponent, int dataSize) {
        if (direction < 0 || direction > 11) {
            throw new IllegalArgumentException("Direction must be 0 to 11: " + direction);
        }
        if (scale < 0 || scale > 4095) {
            throw new IllegalArgumentException("Scale must be 0 to 4095: " + scale);
        }
        byte[] out = new byte[HEADER_SIZE + dataSize + CRC_SIZE];
        int word = direction | scale << 4;
        out[0] = (byte) word;
        out[1] = (byte) (word >>> 8);
        for (int side = 0; side < 3; side++) {
            out[2 + 2 * side] = (byte) (radius - 1);
            out[3 + 2 * side] = (byte) (radius - 1 >>> 8);
        }
        for (int i = 0; i < 4; i++) out[8 + i] = (byte) (fourCC >>> (24 - 8 * i));
        out[12] = (byte) exponent;
        return out;
    }

    /** Appends the CRC-32 of everything before it. */
    private static byte[] seal(byte[] out) {
        CRC32 crc = new CRC32();
        crc.update(out, 0, out.length - CRC_SIZE);
        long sum = crc.getValue();
        for (int i = 0; i < 4; i++) out[out.length - CRC_SIZE + i] = (byte) (sum >>> (8 * i));
        return out;
    }

    /** Pack a fourCC */
    private static int fourCC(byte[] b) {
        return (b[8] & 0xFF) << 24 | (b[9] & 0xFF) << 16 | (b[10] & 0xFF) << 8 | (b[11] & 0xFF);
    }

    /**
     * Validates the first 13 bytes against the HASTE rules and the supported subset.
     *
     * @return the engine radius
     */
    private static int checkHeader(byte[] bytes) throws IOException {
        int direction = bytes[0] & 0xF;
        if (direction > 11) throw new IOException("HASTE direction is invalid: " + direction);
        int fourCC = fourCC(bytes);
        if (fourCC == 0xFFFFFFFF) throw new IOException("HASTE FourCC FF FF FF FF is reserved");
        if ((bytes[12] & 0xF0) != 0) throw new IOException("HASTE pixel size byte has reserved bits set");
        int exponent = bytes[12] & 0xF;
        if (fourCC == FOURCC_MASK) {
            if (exponent != MASK_EXPONENT) throw new IOException("HASTE FourCC \"MASK\" requires 1 bit per cell");
        } else if (fourCC == FOURCC_RGBA || fourCC == FOURCC_BGRA) {
            if (exponent != RGBA_EXPONENT) throw new IOException("HASTE FourCC \"RGBA\" and \"BGRA\" require 32 bits per cell");
        } else {
            throw new IOException("HASTE format as indicated by FourCC is not supported by HappyHex, only \"MASK\", \"RGBA\" and \"BGRA\" are");
        }
        int a = ((bytes[2] & 0xFF) | (bytes[3] & 0xFF) << 8) + 1;
        int b = ((bytes[4] & 0xFF) | (bytes[5] & 0xFF) << 8) + 1;
        int c = ((bytes[6] & 0xFF) | (bytes[7] & 0xFF) << 8) + 1;
        // Assert that a == b == c
        if (a != b || b != c) throw new IOException("HASTE sprite is not a regular hexagon, which is not supported by HappyHex");
        if (a > MAX_RADIUS)   throw new IOException("HASTE radius " + a + " exceeds the limits of this decoder (HappyHex 2.1)");
        return a; // a = b = c
    }
}
