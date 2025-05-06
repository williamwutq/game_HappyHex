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

package hex;

/**
 * Interface for a two-dimensional hexagonal grid composed of {@link Block} objects.
 * <p>
 * The {@code HexGrid} represents a grid layout based on axial coordinates (I-line and K-line),
 * where each valid position may contain a {@link Block} object. This interface supports querying,
 * iteration, merging, and neighbor-counting functionalities.
 * <p>
 * It is designed to be flexible enough to support a wide range of hex-based systems, including
 * tile-based games, simulations, or cellular automata. Implementations of this interface must
 * ensure consistent behavior when accessing blocks by coordinate or index, and must provide
 * methods for determining grid boundaries and merging with other grids.
 *
 * <h3>Coordinate System</h3>
 * The grid uses a hexagonal coordinate system where positions are indexed by two integers: (i, k),
 * representing the I-line and K-line respectively. These correspond to {@link Hex} objects used
 * throughout the interface. See {@link Hex} for detailed description on coordinate system in use.
 *
 * <h3>Usage</h3>
 * Typical use involves iterating through blocks using {@link #length()} and {@link #getBlock(int)},
 * or accessing specific positions using {@link #getBlock(int, int)}. Additional functionality includes
 * merging entire grids using the {@link #add(Hex, HexGrid)} method and checking in-bounds positions
 * with {@link #inRange(int, int)}.
 * @since 1.0
 * @author William Wu
 * @version 1.2
 */
public interface HexGrid {
    /**
     * The size of the grid. Use with {@link #getBlock(int)} to iterate through all blocks.
     *
     * @return the number of {@link Block blocks} in the grid.
     */
    int length();
    /**
     * Returns an array containing all non-null {@link Block} objects in the grid.
     * Implementations should ensure the array is sorted by {@link Hex} coordinate.
     * @return an array of all non-null blocks.
     */
    Block[] blocks();
    /**
     * Checks whether the specified {@link Hex} coordinates (i, k) are within the valid range of the grid.
     *
     * @param i the I-line coordinate as in {@link Hex#getLineI()}.
     * @param k the K-line coordinate as in {@link Hex#getLineK()}.
     * @return {@code true} if the coordinates are within range, {@code false} otherwise.
     * @see #getBlock(int, int)
     */
    boolean inRange(int i, int k);
    /**
     * Retrieves the {@link Block} object located at the specified I-line and K-line coordinates.
     *
     * @param i the I-line coordinate as in {@link Hex#getLineI()}.
     * @param k the K-line coordinate as in {@link Hex#getLineK()}.
     * @return the block at the specified coordinates, or {@code null} if not found or out of range.
     * @see #getBlock(int)
     * @see #inRange(int, int)
     */
    Block getBlock(int i, int k);
    /**
     * Retrieves the {@link Block} at the specified index within the grid.
     * This method is typically used with {@link #length()} in a for-loop.
     *
     * @param index the index of the block in the grid.
     * @return the block at the specified index.
     */
    Block getBlock(int index); // Use this with length(); in a for loop for any grid
    /**
     * Adds all blocks from another {@link HexGrid} to this grid, aligning them based on a specified {@link Hex} coordinate.
     *
     * @param origin the reference {@code hex} position for alignment.
     * @param other  the other {@code HexGrid} to merge into this grid.
     * @throws IllegalArgumentException if the grids cannot be merged due to alignment issues.
     */
    void add(Hex origin, HexGrid other) throws IllegalArgumentException;
    /**
     * Adds all blocks from another {@link HexGrid} to this grid without shifting the positions.
     *
     * @param other the other {@code HexGrid} to merge into this grid.
     * @throws IllegalArgumentException if the grids cannot be merged.
     * @see #add(Hex, HexGrid)
     */
    default void add (HexGrid other) throws IllegalArgumentException{
        add(new Hex(), other);
    }
    /**
     * Counts the number of occupied neighboring {@link Block} around the given {@link Hex position} in the hexagonal grid.
     * <p>
     * This method checks up to six adjacent positions to the block located at coordinates (i, k).
     * A neighbor is considered "occupied" if the block at that position is non-null and its state is {@code true}.
     * If a neighboring position is out of range or contains a {@code null} block, it is either counted as occupied
     * or ignored based on the {@code includeNull} flag.
     *
     * @param i the I-line coordinate of the block whose neighbors are to be counted.
     * @param k the K-line coordinate of the block whose neighbors are to be counted.
     * @param includeNull whether to treat {@code null} or out-of-bounds neighbors as occupied ({@code true}) or unoccupied ({@code false}).
     * @return the number of occupied neighbors surrounding the block at (i, k). Returns 0 if the center position itself is out of range.
     * @since 1.2
     */
    default int countNeighbors(int i, int k, boolean includeNull){
        int count = 0;
        if (inRange(i, k)){
            if (inRange(i - 1, k - 1)){
                if (getBlock(i - 1, k - 1).getState()) count ++;
            } else if (includeNull) count ++;
            if (inRange(i - 1, k)){
                if (getBlock(i - 1, k).getState()) count ++;
            } else if (includeNull) count ++;
            if (inRange(i, k - 1)){
                if (getBlock(i, k - 1).getState()) count ++;
            } else if (includeNull) count ++;
            if (inRange(i, k + 1)){
                if (getBlock(i, k + 1).getState()) count ++;
            } else if (includeNull) count ++;
            if (inRange(i + 1, k)){
                if (getBlock(i + 1, k).getState()) count ++;
            } else if (includeNull) count ++;
            if (inRange(i + 1, k + 1)){
                if (getBlock(i + 1, k + 1).getState()) count ++;
            } else if (includeNull) count ++;
            return count;
        } else return 0;
    }
}
