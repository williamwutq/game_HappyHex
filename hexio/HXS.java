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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code HXS} class reads and writes HXS files, the HASTE sprite document format defined in {@code HXS.txt}.
 * {@link HexLogger} records a game as a log of moves. This class records it as a list of board states.
 *
 * <h2>Document</h2>
 * A document is an ordered list of frames. Each frame is the {@link HexEngine} board state at one step of a game. The
 * piece queue is not saved. The moves are not saved directly. They show as the changes between consecutive frames.
 *
 * <h2>Shape</h2>
 * All frames of a document have the same radius, direction and scale:
 *
 * <ul>
 *     <li>The radius is the radius of the engines. It is the side length A = B = C of the frames in the file.</li>
 *     <li>The direction, 0 to 11, is the clock position that the +b axis points to. The default is 3.</li>
 *     <li>The scale, 0 to 4095, is the scale index of {@link HASTE}. The default is 0.</li>
 * </ul>
 * Direction and scale do not change how the cells are read.
 *
 * <h2>Palette</h2>
 * A palette index in an HXS file is a color index in HappyHex. Only the color indices -2 to 11 have an entry. Any other
 * color index is stored as {@value HASTE#INDEX_FILLED}. The palette has {@value HASTE#INDEXED_PALETTE_SIZE} entries:
 *
 * <ul>
 *     <li>Index {@value HASTE#INDEX_EMPTY} stands for color index -1. It is the empty block and is fully
 *         transparent.</li>
 *     <li>Index {@value HASTE#INDEX_FILLED} stands for color index -2. It is the default filled color.</li>
 *     <li>Indices 2 to 13 stand for color indices 0 to 11.</li>
 * </ul>
 * Only index {@value HASTE#INDEX_EMPTY} is empty. Every frame cell takes 4 bits.
 * <p>
 * HappyHex uses the color index to get the color of a block. It does not use the OKLab color, because themes render it
 * inaccurately. Other HXS programs can still use the OKLab color to show the sprite. Each entry holds the color of its
 * color index in the theme used for writing, from {@link GUI.GameEssentials#generateColor(int)}.
 *
 * <h2>File</h2>
 * Each {@code HXS} object is assigned to one file in the {@code data/} directory. The file name ends with
 * {@code .hxs}. The object keeps its frames in memory. {@link #write()} saves them to the file. {@link #read()}
 * loads them from the file.
 *
 * <h2>Errors</h2>
 * {@link #read()} throws {@link IOException} if the file is missing or if {@code HXS.txt} Section 7 rejects it. The
 * document keeps its previous radius, direction, scale and frames in both cases.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * // Record a game
 * HXS record = new HXS(engine.getRadius());
 * record.addFrame(engine);          // after every step
 * record.write();
 *
 * // Replay it
 * HXS replay = new HXS(record.getDataFileName());
 * replay.read();
 * for (HexEngine frame : replay.getFrames()) { ... }
 * }</pre>
 *
 * @version 2.1
 * @author William Wu
 * @since 2.1
 * @see HASTE
 * @see HexLogger
 */
public final class HXS {
    /** The magic at the start of a file, {@code "HXS1"}. */
    public static final int MAGIC = 0x48585331;
    /** The FourCC of a frame container, {@code "OKLI"}. */
    public static final int FOURCC_FRAME = 0x4F4B4C49;
    /** The extension of an HXS file, including the dot. */
    public static final String EXTENSION = ".hxs";

    private static final int DEFAULT_DIRECTION = HASTE.DEFAULT_DIRECTION;
    private static final int DEFAULT_SCALE = HASTE.DEFAULT_SCALE;
    private static final String DATA_DIRECTORY = "data/";
    private static final int HEADER_SIZE = 13;
    private static final int CRC_SIZE = 4;
    private static final int OKLB_EXPONENT = 6;
    // Positions in the array returned by header(byte[], int)
    private static final int A = 2, B = 3, C = 4, FOURCC = 5, EXPONENT = 6;

    /** The file name without {@code .hxs}. */
    private final String dataFile;
    private int radius = 1;
    private int direction = DEFAULT_DIRECTION;
    private int scale = DEFAULT_SCALE;
    /** The frames. The color index of a block is one that has a palette index, see {@link HASTE#paletteIndex}. */
    private ArrayList<HexEngine> frames = new ArrayList<>();

    // Create

    /**
     * Creates a document with no frames, direction 3 and scale 0. The file name is generated in the same way as the
     * file name of a {@link HexLogger}. It ends with {@code .hxs}.
     *
     * @param radius the radius of the frames, 1 to {@link HASTE#MAX_RADIUS}
     * @throws IllegalArgumentException if the radius is out of range
     */
    public HXS(int radius) {
        this(radius, DEFAULT_DIRECTION, DEFAULT_SCALE);
    }

    /**
     * Creates a document with no frames. The file name is generated in the same way as the file name of a
     * {@link HexLogger}. It ends with {@code .hxs}.
     *
     * @param radius    the radius of the frames, 1 to {@link HASTE#MAX_RADIUS}
     * @param direction the direction of the frames, 0 to 11
     * @param scale     the scale index of the frames, 0 to 4095
     * @throws IllegalArgumentException if the radius, direction or scale is out of range
     */
    public HXS(int radius, int direction, int scale) {
        if (radius < 1 || radius > HASTE.MAX_RADIUS || direction < 0 || direction > 11 || scale < 0 || scale > 4095) {
            throw new IllegalArgumentException("Invalid shape: radius " + radius + ", direction " + direction
                    + ", scale " + scale);
        }
        this.radius = radius;
        this.direction = direction;
        this.scale = scale;
        this.dataFile = DATA_DIRECTORY + generateFileName();
    }

    /**
     * Creates a document assigned to a file. The file is not opened. The document has no frames, radius 1, direction 3
     * and scale 0 until {@link #read()} replaces them with the content of the file.
     *
     * @param fileName the path of the file, with or without {@code .hxs}
     */
    public HXS(String fileName) {
        dataFile = fileName.endsWith(EXTENSION)
            ? fileName.substring(0, fileName.length() - EXTENSION.length())
            : fileName;
    }

    // Properties

    /** Returns the path of the file of this document. It always ends with {@code .hxs}. */
    public String getDataFileName() {
        return dataFile + EXTENSION;
    }
    /** Returns the radius of the frames, which is the radius of the engines that {@link #getFrame} returns. */
    public int getRadius() {
        return radius;
    }
    /** Returns the direction of the frames, 0 to 11. */
    public int getDirection() {
        return direction;
    }
    /** Returns the scale index of the frames, 0 to 4095. */
    public int getScale() {
        return scale;
    }
    /** Returns the number of frames. */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Sets the radius, direction and scale of the frames. The shape is not changed if the document has frames, because
     * their size depends on the radius. Use {@link #erase()} first to remove them.
     *
     * @param radius    the radius of the frames, 1 to {@link HASTE#MAX_RADIUS}
     * @param direction the direction of the frames, 0 to 11
     * @param scale     the scale index of the frames, 0 to 4095
     * @return true if the shape is set, false if the document has frames or an argument is out of range
     */
    public boolean setShape(int radius, int direction, int scale) {
        if (!frames.isEmpty()) return false;
        if (radius < 1 || radius > HASTE.MAX_RADIUS || direction < 0 || direction > 11 || scale < 0 || scale > 4095) {
            return false;
        }
        this.radius = radius;
        this.direction = direction;
        this.scale = scale;
        return true;
    }

    // Frames

    /**
     * Appends a frame after the last frame. The board of the engine is copied, so later changes to the engine do not
     * change the frame. Each block is stored as its palette index. See {@link HASTE#paletteIndex(boolean, int)}.
     *
     * @param engine the engine to copy the board state from
     * @return true if the frame is added, false if the engine is null or its radius is not the radius of the document
     */
    public boolean addFrame(HexEngine engine) {
        return addFrame(frames.size(), engine);
    }

    /**
     * Inserts a frame at an index and move all later frames up by one. See {@link #addFrame(HexEngine)}.
     *
     * @param index  the index of the new frame, at least 0 and at most {@link #getFrameCount()}
     * @param engine the engine to copy the board state from
     * @return true if the frame is added, false if the engine is null or its radius is not the radius of the document
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public boolean addFrame(int index, HexEngine engine) {
        if (index < 0 || index > frames.size()) {
            throw new IndexOutOfBoundsException("Frame index " + index + ", frame count " + frames.size());
        }
        HexEngine frame = snapshot(engine);
        if (frame == null) return false;
        frames.add(index, frame);
        return true;
    }

    /**
     * Replaces the frame at an index with the board of an engine. The other frames do not change. See
     * {@link #addFrame(HexEngine)}.
     *
     * @param index  the index of the frame
     * @param engine the engine to copy the board state from
     * @return true if the frame is replaced, false if the engine is null or its radius is not the radius of the
     *         document
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public boolean setFrame(int index, HexEngine engine) {
        if (index < 0 || index >= frames.size()) {
            throw new IndexOutOfBoundsException("Frame index " + index + ", frame count " + frames.size());
        }
        HexEngine frame = snapshot(engine);
        if (frame == null) return false;
        frames.set(index, frame);
        return true;
    }

    /**
     * Returns a new engine with the board of a frame. The engine has the radius of the document. The state of each
     * block is {@link HASTE#stateOfIndex(int)} of its palette index and its color index is
     * {@link HASTE#colorOfIndex(int)}.
     *
     * @param index the index of the frame, at least 0 and less than {@link #getFrameCount()}
     * @return a new {@link HexEngine}, which is not shared with the document
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public HexEngine getFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            throw new IndexOutOfBoundsException("Frame index " + index + ", frame count " + frames.size());
        }
        return snapshot(frames.get(index));
    }

    /**
     * Returns a new engine for each frame in the order of the frames. See {@link #getFrame(int)}.
     *
     * @return a list of new {@link HexEngine}s, or an empty list when the document has no frames
     */
    public List<HexEngine> getFrames() {
        List<HexEngine> result = new ArrayList<>(frames.size());
        for (int i = 0; i < frames.size(); i++) result.add(getFrame(i));
        return result;
    }

    /**
     * Removes the frame at an index. The later frames move down by one.
     *
     * @param index the index of the frame, at least 0 and less than {@link #getFrameCount()}
     * @throws IndexOutOfBoundsException if the index is not a frame
     */
    public void removeFrame(int index) {
        if (index < 0 || index >= frames.size()) {
            throw new IndexOutOfBoundsException("Frame index " + index + ", frame count " + frames.size());
        }
        frames.remove(index);
    }

    /**
     * Removes all frames and sets radius 1, direction 3 and scale 0. The file name is kept and the file is not changed.
     * It does not change the file it points to. This is almost equal to creating another document.
     */
    public void erase() {
        frames.clear();
        radius = 1;
        direction = DEFAULT_DIRECTION;
        scale = DEFAULT_SCALE;
    }

    // File

    /**
     * Scans the {@code data/} directory for all files ending with {@code .hxs}. The files are not read.
     *
     * @return a list of the paths of the files, such as {@code data/NAME.hxs}. It is empty if there is no such file or
     *         if the directory does not exist
     */
    public static ArrayList<String> findHxsFiles() {
        ArrayList<String> result = new ArrayList<>();
        Path dir = Paths.get(DATA_DIRECTORY);
        if (!Files.isDirectory(dir)) return result;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + EXTENSION)) {
            for (Path path : stream) result.add(path.toString());
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(HexLogger.generateSimpleTime() + " HXS: Error occurred finding all HXS files.");
        }
        return result;
    }

    /**
     * Creates a document for every file found by {@link #findHxsFiles()}. The documents have
     * no frames until {@link #read()} is called on them.
     *
     * @return a list of documents, which is empty if no file is found
     */
    public static ArrayList<HXS> generateHxsDocuments() {
        ArrayList<HXS> result = new ArrayList<>();
        for (String path : findHxsFiles()) result.add(new HXS(path));
        return result;
    }

    /**
     * Deletes the file of this document if it exists. The frames in memory are kept.
     *
     * @return true if the file does not exist after the call, false if deleting fails
     */
    public boolean deleteFile() {
        try {
            Files.deleteIfExists(Paths.get(getDataFileName()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Writes the document to its file, replacing the file if it exists. The file holds the magic {@code HXS1}, the
     * palette and the frames in order. Each palette entry is stored with its color in the current theme.
     *
     * @throws IOException if the file cannot be written or the document has no frame, because HXS needs at least one
     * @see #read()
     */
    public void write() throws IOException {
        if (frames.isEmpty()) throw new IOException("HXS needs at least one frame");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(MAGIC >>> 24);
        out.write(MAGIC >>> 16);
        out.write(MAGIC >>> 8);
        out.write(MAGIC);

        // Palette
        int size = HASTE.INDEXED_PALETTE_SIZE;
        byte[] palette = HASTE.container(1, 1, size, 0, 0, HASTE.FOURCC_OKLB, OKLB_EXPONENT, 8 * size);
        for (int i = HASTE.INDEX_FILLED; i < size; i++) { // entry 0 stays all zeros, which is transparent
            float[] lab = HASTE.srgbToOklab(GameEssentials.generateColor(HASTE.colorOfIndex(i)).getRGB());
            int at = HEADER_SIZE + 8 * i;
            HASTE.put16(palette, at, HASTE.toHalf(Math.max(0f, Math.min(1f, lab[0]))));
            HASTE.put16(palette, at + 2, HASTE.toHalf(lab[1]));
            HASTE.put16(palette, at + 4, HASTE.toHalf(lab[2]));
            HASTE.put16(palette, at + 6, 0x3C00); // alpha = 1.0
        }
        out.write(HASTE.seal(palette));

        // Frames
        for (HexEngine frame : frames) out.write(HASTE.encodeIndexed(frame, direction, scale));

        Path path = Paths.get(getDataFileName());
        if (path.getParent() != null) Files.createDirectories(path.getParent());
        Files.write(path, out.toByteArray());
    }

    /**
     * Reads the file of this document into memory. The radius, direction, scale and frames of the document are replaced
     * by those of the file. Each palette index of a frame is converted to a state and a color index. The OKLab colors
     * of the palette are not used.
     * <p>
     * This method delegates to {@link #readAndCheck} with all requirements being null.
     *
     * @throws IOException if the file is missing, cannot be read or is rejected by {@code HXS.txt} Section 7
     * @see #write()
     */
    public void read() throws IOException {
        readAndCheck(null, null, null, null);
    }

    /**
     * Reads the file of this document into memory, checking values against the provided parameters as soon as they
     * are read. Populates the radius, direction, scale and frames of the document. Terminates early by throwing
     * {@link IOException} if a required parameter does not match or if the file is rejected.
     * <p>
     * The radius, direction and scale are checked when the header of the first frame is read. The number of frames is
     * not stored in the file, so it is checked at the end of the file. The document is unchanged if this method throws.
     *
     * @param radiusRequired     the required radius. If non-null, reading fails
     *                           if the radius of the frames does not match.
     * @param directionRequired  the required direction. If non-null, reading fails
     *                           if the direction of the frames does not match.
     * @param scaleRequired      the required scale index. If non-null, reading fails
     *                           if the scale of the frames does not match.
     * @param frameCountRequired the required number of frames. If non-null, reading fails if the number of
     *                           frames does not match.
     * @throws IOException if the file is missing, cannot be read or is rejected by {@code HXS.txt} Section 7, or if a
     *                     non-null requirement does not match
     */
    public void readAndCheck(Integer radiusRequired, Integer directionRequired, Integer scaleRequired,
                             Integer frameCountRequired) throws IOException {
        byte[] d = Files.readAllBytes(Paths.get(getDataFileName()));

        // Magic
        if (d.length < 4
            || (d[0] & 0xFF) << 24 != (MAGIC & 0xFF000000)
            || (d[1] & 0xFF) << 16 != (MAGIC & 0xFF0000)
            || (d[2] & 0xFF) << 8  != (MAGIC & 0xFF00)
            || (d[3] & 0xFF)       != (MAGIC & 0xFF)
        ){
            throw new IOException("HXS file does not start with HXS1");
        }

        // Palette
        int[] h = header(d, 4);
        if (h[FOURCC] != HASTE.FOURCC_OKLB || h[EXPONENT] != OKLB_EXPONENT || h[A] != 1 || h[B] != 1) {
            throw new IOException("HXS palette is not an OKLB container with A = 1, B = 1 and p = 6");
        }
        int n = h[C];
        int pos = checkContainer(d, 4, 8 * n);
        for (int i = 0; i < n; i++) HASTE.readOklab(d, 4 + HEADER_SIZE + 8 * i, i); // validates the entry

        // Frames
        ArrayList<HexEngine> read = new ArrayList<>();
        HASTE.Indexed first = null;
        while (pos < d.length) {
            HASTE.Indexed f;
            try {
                f = HASTE.decodeIndexed(d, pos, n);
            } catch (IOException e) {
                throw new IOException("HXS frame " + read.size() + ": " + e.getMessage(), e);
            }
            if (first == null) {
                first = f;
                if (radiusRequired != null && radiusRequired != f.engine.getRadius())
                    throw new IOException("HXS radius " + f.engine.getRadius() + " does not match " + radiusRequired);
                if (directionRequired != null && directionRequired != f.direction)
                    throw new IOException("HXS direction " + f.direction + " does not match " + directionRequired);
                if (scaleRequired != null && scaleRequired != f.scale)
                    throw new IOException("HXS scale " + f.scale + " does not match " + scaleRequired);
            } else if (f.engine.getRadius() != first.engine.getRadius() || f.direction != first.direction
                    || f.scale != first.scale) {
                throw new IOException("HXS frame " + read.size() + " does not match the shape of frame 0");
            }
            read.add(f.engine);
            if (frameCountRequired != null && read.size() > frameCountRequired)
                throw new IOException("HXS has more than " + frameCountRequired + " frames");
            pos += f.length;
        }
        if (first == null) throw new IOException("HXS has no frame");
        if (frameCountRequired != null && read.size() != frameCountRequired)
            throw new IOException("HXS frame count " + read.size() + " does not match " + frameCountRequired);

        radius = first.engine.getRadius();
        direction = first.direction;
        scale = first.scale;
        frames = read;
    }

    // Helpers

    /**
     * Returns a new engine with the board of an engine, keeping only the color indices that have a palette index. It
     * is null if the engine is null or has another radius than the document.
     */
    private HexEngine snapshot(HexEngine engine) {
        if (engine == null || engine.getRadius() != radius) return null;
        HexEngine copy = new HexEngine(radius);
        for (int i = 0; i < copy.length(); i++) {
            int index = HASTE.paletteIndex(engine.getBlock(i).getState(), engine.getBlock(i).getColor());
            if (index == HASTE.INDEX_EMPTY) continue;
            copy.setState(i, true);
            copy.setColor(i, HASTE.colorOfIndex(index));
        }
        return copy;
    }

    /** Creates a file name in the same way as {@link HexLogger}, 16 hex digits mixing a hash and the time. */
    private static String generateFileName() {
        long hash = HexLogger.generateHash(HexLogger.getID());
        long time = HexLogger.generateTime();
        StringBuilder name = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int shift = (15 - i) * 4;
            name.append(Long.toHexString(((i % 2 == 0 ? hash : time) >> shift) & 0xF).toUpperCase());
        }
        return name.toString();
    }

    /**
     * Reads and checks the header of the container at a position, except its fourCC and pixel size, which depend on
     * the kind of container.
     *
     * @return direction, scale, A, B, C, FourCC and p
     */
    private static int[] header(byte[] d, int at) throws IOException {
        if (d.length - at < HEADER_SIZE) throw new IOException("HXS container is truncated");
        int word = HASTE.get16(d, at);
        int fourCC = HASTE.fourCC(d, at);
        if ((word & 0xF) > 11) throw new IOException("HXS direction is invalid: " + (word & 0xF));
        if (fourCC == 0xFFFFFFFF) throw new IOException("HXS FourCC FF FF FF FF is reserved");
        if ((d[at + 12] & 0xF0) != 0) throw new IOException("HXS pixel size byte has reserved bits set");
        return new int[]{word & 0xF, word >>> 4, HASTE.get16(d, at + 2) + 1, HASTE.get16(d, at + 4) + 1,
                HASTE.get16(d, at + 6) + 1, fourCC, d[at + 12] & 0xF};
    }

    /**
     * Checks that the container at a position has its full length and a correct CRC-32.
     *
     * @return the position after the container
     */
    private static int checkContainer(byte[] d, int at, int dataSize) throws IOException {
        long end = (long) at + HEADER_SIZE + dataSize + CRC_SIZE;
        if (end > d.length) throw new IOException("HXS container is truncated");
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(d, at, HEADER_SIZE + dataSize);
        long stored = 0;
        for (int i = 0; i < CRC_SIZE; i++) stored |= (d[(int) end - CRC_SIZE + i] & 0xFFL) << (8 * i);
        if (crc.getValue() != stored) throw new IOException("HXS CRC mismatch");
        return (int) end;
    }
}
