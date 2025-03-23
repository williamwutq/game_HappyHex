package Hex;

/** Interface for a grid of {@link Block} objects */
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
     * @param origin the reference {@code Hex} position for alignment.
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
        add(new Block(), other);
    }
}
