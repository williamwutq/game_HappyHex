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
 * Represents a shape or unit made up of multiple {@link Block} instances,
 * typically forming a logical structure such as a game piece.
 * <p>
 * A {@code Piece} implements the {@link HexGrid} interface and behaves like
 * a small, self-contained hexagonal grid. It holds a fixed-size array of
 * {@link Block} elements and supports block addition, color management,
 * coordinate lookup, and comparison.
 * <p>
 * It is recommended to not modify any {@code Piece} once they are created and
 * properly filled with sufficient {@code Block}. If changes are needed, it is
 * a good practice to copy the piece block by block. For efficiency reason, cloning
 * is not supported and multiple reference to the same object is preferred.
 * <p>
 * Coordinate access uses the "line" system (I, K), defined in {@link Hex},
 * which simplifies hex grid navigation by avoiding raw (I, J, K) coordinates.
 * <p>
 * A typical creation sequence might look like:
 * <pre>{@code
 * Piece p = new Piece(3, new SolidColor(0, 0, 255));
 * p.add(Block.block(0, 0));
 * p.add(Block.block(0, 1));
 * p.add(Block.block(1, 1));
 * }</pre>
 * This produces a shape containing blocks at line coordinates (0,0), (0,1), and (1,1).
 * <p>
 * <h5>Standard 7-{@code Block} piece</h5>
 * <p>
 * A standard 7-{@link Block} grid is defined by the game as a {@link HexGrid} that contains blocks at:
 * <ul>
 *     <li>Line coordinate {-1, 0, -1}</li>
 *     <li>Line coordinate {-1, 1, 0}</li>
 *     <li>Line coordinate {0, -1, -1}</li>
 *     <li>Line coordinate {0, 0, 0}</li>
 *     <li>Line coordinate {0, 1, 1}</li>
 *     <li>Line coordinate {1, -1, 0}</li>
 *     <li>Line coordinate {1, 0, 1}</li>
 * </ul>
 * This grid represent a grid of {@link Hex#inRange radius} 2 and center at {@link Hex#hex() zero}.
 * <p>
 * In application of the {@link Piece} class, this means the piece have {@link #length} of at least one
 * and only contain {@link Block}s at the specific positions listed above. Pieces can contain fewer than
 * 7 blocks, and empty blocks can be represented either as missing blocks or blocks that are
 * {@link Block#getState empty}.
 *
 * @see Block
 * @see Hex
 * @see HexGrid
 * @see Hex#getLines()
 * @since 1.0
 * @author William Wu
 * @version 1.3
 */
public class Piece implements HexGrid{
    private Block[] blocks;
    private int color;

    // Constructor
    /** Constructs a default {@code Piece} with a single {@link Block} at (0,0) and default color. */
    public Piece(){
        this.blocks = new Block[1];
        this.blocks[0] = new Block(0, 0);
        this.blocks[0].setState(true);
    }
    /**
     * Constructs an empty {@code Piece} with the specified capacity and color.
     * The piece can later be filled using {@link #add(Block)}.
     *
     * @param length the number of blocks this piece can hold; must be greater or equal to 1.
     * @param color  the color index for this piece's blocks.
     * @see #length()
     * @see HexGrid
     */
    public Piece(int length, int color){
        if(length < 1){
            length = 1;
        }
        this.blocks = new Block[length];
        this.color = color;
    }

    // Color
    /**
     * Sets the color of this piece and applies it to all current blocks.
     * @param color the new color to assign.
     */
    public void setColor(int color){
        this.color = color;
        // write to all
        for(int i = 0; i < length(); i ++){
            if(blocks[i] != null){
                blocks[i].setColor(color); // Set all color to color
            }
        }
    }
    /**
     * Returns the current color of this piece.
     * The color applies for all {@link Block} in this piece.
     * @return the color of the piece.
     */
    public int getColor(){
        return color;
    }

    /**
     * Adds a {@link Block} to the first available slot in this piece.
     * Automatically applies the current color and marks the block as occupied.
     * If the piece is full, the block is not added and the method return false.
     * @param block the block to add.
     * @return {@code true} if the block was added; {@code false} if the piece is full.
     */
    public boolean add(Block block){
        for(int i = 0; i < length(); i ++){
            if(blocks[i] == null){
                // find
                blocks[i] = block;
                blocks[i].setColor(color); // Set all color to color
                blocks[i].setState(true); // All should be occupied
                return true;
            }
        }
        // not added
        return false;
    }
    /**
     * Create a block at (I, K) and add to the first available slot in this piece.
     * Automatically applies the current color and marks the block as occupied.
     * If the piece is full, the block is not added and the method return false.
     * @param i the I-line coordinate of the block to add.
     * @param k the K-line coordinate of the block to add.
     * @return {@code true} if the block was added; {@code false} if the piece is full.
     * @see #add(Block)
     * @since 1.1
     */
    public boolean add(int i, int k){
        for(int index = 0; index < length(); index ++){
            if(blocks[index] == null){
                // find
                blocks[index] = Block.block(i, k, color);
                blocks[index].setState(true); // All should be occupied
                return true;
            }
        }
        // not added
        return false;
    }

    // Implements HexGrid
    /** {@inheritDoc} */
    public int length(){
        if(blocks == null){
            return 0;
        }else{
            return blocks.length;
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * Returns all {@link Block} elements in the piece. Null blocks are replaced
     * with dummy placeholders at (-1, -1) to preserve order.
     */
    public Block[] blocks(){
        // Remove null
        sort();
        Block[] result = blocks;
        for(int i = 0; i < length(); i ++){
            if(blocks[i] == null){
                result[i] = new Block(0, 0, color);
            }else{
                result[i] = blocks[i];
            }
        }
        return result;
    }
    /**
     * {@inheritDoc}
     * This implementation performs a linear search to check if a block exists at the given line coordinates.
     * @see #getBlock(int, int)
     */
    public boolean inRange(int i, int k){
        // Attempt to find it
        return this.getBlock(i, k) != null;
    }
    /**
     * Retrieves a {@link Block} at the specified line coordinates, if present.
     * @param i the I-line coordinate of the target block
     * @param k the K-line coordinate of the target block
     * @return the block at the specified {@link Hex} line coordinate
     */
    public Block getBlock(int i, int k){
        // Linear search
        for (int index = 0; index < length(); index ++){
            if(blocks[index] != null){
                Block target = blocks[index];
                if(target.getLineI() == i && target.getLineK() == k){
                    return target;
                }
            }
        }
        return null;
    }
    /**
     * Retrieves the state of a {@link Block} at the specified line coordinates. If the block does not exist,
     * it is counted as empty
     * @param i the I-line coordinate of the target block
     * @param k the K-line coordinate of the target block
     * @return the block at the specified {@link Hex} line coordinate
     * @see #getBlock(int, int)
     * @since 1.2
     */
    public boolean getState(int i, int k){
        Block block = getBlock(i, k);
        return block != null && block.getState();
    }
    /**{@inheritDoc}*/
    public Block getBlock(int index){
        return blocks[index];
    }
    /**
     * {@inheritDoc}
     * <p>
     * Always throws an exception as pieces cannot be merged with other pieces.
     * For adding {@link Piece} to {@link HexEngine}, use the {@link HexEngine#add} method.
     * <p>
     * For forcefully merging two pieces, when absolutely necessary, please add them block by block as implemented:
     * <pre>{@code
     *     Piece p; Piece q; // Assumptions
     *     Piece newPiece = new Piece(p.length() + q.length(), p.getColor());
     *     for (int i = 0; i < p.length(); i ++){
     *         newPiece.add(p.getBlock(i));
     *     }
     *     for (int i = p.length(); i < newPiece.length(); i ++){
     *         newPiece.add(q.getBlock(i));
     *     }
     * }</pre>
     * @throws IllegalArgumentException always
     */
    public void add(Hex origin, HexGrid other) throws IllegalArgumentException{
        throw new IllegalArgumentException("Adding Grid to piece prohibited. Please add block by block.");
    }

    /**
     * Sorts the {@link Block} contained in-place using insertion sort based on {@link Hex} line-coordinates (I, K).
     */
    private void sort() {
        int n = blocks.length;
        for (int i = 1; i < n; i++) {
            Block key = blocks[i];
            int j = i - 1;
            if (key != null) {
                // Sort by getLineI(), then getLineK() if equal, skipping nulls
                while (j >= 0 && (blocks[j] == null || (blocks[j].getLineI() > key.getLineI() ||
                      blocks[j].getLineI() == key.getLineI() && blocks[j].getLineK() > key.getLineK()))) {
                    blocks[j + 1] = blocks[j];
                    j--;
                }
                blocks[j + 1] = key;
            }
            blocks[j + 1] = key;
        }
    }
    /**
     * Returns a string representation of the piece and its block line coordinates.
     * @return a string describing this piece.
     * @see Block#toString()
     */
    public String toString(){
        StringBuilder str = new StringBuilder("Piece{");
        if (blocks.length > 0){
            if (blocks[0] == null) {
                str.append("null");
            } else {
                str.append(blocks[0].toBasicString());
            }
        }
        for (int i = 1; i < blocks.length; i++) {
            Block block = blocks[i];
            str.append(", ");
            if (block == null) {
                str.append("null");
            } else {
                str.append(block.toBasicString());
            }
        }
        return str + "}";
    }
    /**
     * Returns a byte representation of the blocks in this {@code Piece}, if this piece confines
     * to the standard of a 7-{@link Block} piece.
     * <p>
     * Empty blocks will be represented with 0s and filled blocks will be represented with 1s.
     * This method does not record color.
     * @return a byte representing this 7-block piece. The first bit is empty.
     * @since 1.1
     */
    public byte toByte(){
        byte mask = 0x7F;
        byte data = 0;
        if (getState(-1, -1)) data ++;
        data = (byte) (data << 1);
        if (getState(-1, 0)) data ++;
        data = (byte) (data << 1);
        if (getState(0, -1)) data ++;
        data = (byte) (data << 1);
        if (getState(0, 0)) data ++;
        data = (byte) (data << 1);
        if (getState(0, 1)) data ++;
        data = (byte) (data << 1);
        if (getState(1, 0)) data ++;
        data = (byte) (data << 1);
        if (getState(1, 1)) data ++;
        return (byte) (data & mask);
    }
    /**
     * Construct a standard 7 or less {@link Block} piece from a byte data.
     * Throws {@link IllegalArgumentException} if the byte data represent an empty piece or have an extra bit.
     * <p>
     * The byte data conversion is in accordance with the {@link #toByte()} method.
     * @param data the byte data used to create this {@code piece}.
     * @param color the color for this piece's blocks.
     * @return a piece constructed from the byte data with the input color applied to its {@link Block}s.
     * @since 1.3
     */
    public static Piece pieceFromByte(byte data, int color) throws IllegalArgumentException{
        if (data < 0){
            throw new IllegalArgumentException ("Data must have empty most significant bit");
        } else if (data == 0){
            throw new IllegalArgumentException ("Data must contain at least one block");
        } else {
            int count = 0;
            for (int i = 0; i < 7; i++) {
                count += (data >> i) & 1;
            }
            Piece piece = new Piece(count, color);
            if ((data >> 6 & 1) == 1) piece.add(-1, -1);
            if ((data >> 5 & 1) == 1) piece.add(-1, 0);
            if ((data >> 4 & 1) == 1) piece.add(0, -1);
            if ((data >> 3 & 1) == 1) piece.add(0, 0);
            if ((data >> 2 & 1) == 1) piece.add(0, 1);
            if ((data >> 1 & 1) == 1) piece.add(1, 0);
            if ((data & 1) == 1) piece.add(1, 1);
            return piece;
        }
    }
    /**
     * Compares this piece to another for equality.
     * Two pieces are equal if they have the same {@link #length()} and identical {@link Block} positions.
     * Color index is not involved in this comparison.
     * @param piece the other piece to compare to.
     * @return {@code true} if both pieces are structurally equal; {@code false} otherwise.
     */
    public boolean equals(Piece piece) {
        if(piece.length() != this.length()) {return false;}
        this.sort();
        piece.sort();
        for(int i = 0; i < this.length(); i ++){
            if(!this.blocks[i].equals(piece.blocks[i])){
                return false;
            }
        }
        return true;
    }
}
