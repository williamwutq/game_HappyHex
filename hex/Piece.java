package hex;

import java.awt.*;

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
 * Piece p = new Piece(3, Color.BLUE);
 * p.add(Block.block(0, 0));
 * p.add(Block.block(0, 1));
 * p.add(Block.block(1, 1));
 * }</pre>
 * This produces a shape containing blocks at line coordinates (0,0), (0,1), and (1,1).
 *
 * @see Block
 * @see Color
 * @see Hex
 * @see HexGrid
 * @see Hex#getLines()
 * @since 1.0
 * @author William Wu
 * @version 1.2
 */
public class Piece implements HexGrid{
    private Block[] blocks;
    private Color color;

    // Constructor
    /** Constructs a default {@code Piece} with a single {@link Block} at (0,0) and color black. */
    public Piece(){
        this.blocks = new Block[1];
        this.color = Color.BLACK;
        this.blocks[0] = new Block(0, 0, color);
    }
    /**
     * Constructs an empty {@code Piece} with the specified capacity and color.
     * The piece can later be filled using {@link #add(Block)}.
     *
     * @param length the number of blocks this piece can hold; must be greater or equal to 1.
     * @param color  the {@link Color} for this piece's blocks.
     * @see #length()
     * @see HexGrid
     */
    public Piece(int length, Color color){
        if(length < 1){
            length = 1;
        }
        this.blocks = new Block[length];
        this.color = color;
    }

    // Color
    /**
     * Sets the color of this piece and applies it to all current blocks.
     * @param color the new {@link Color} to assign.
     */
    public void setColor(Color color){
        this.color = color;
        // write to all
        for(int i = 0; i < length(); i ++){
            if(blocks[i] != null){
                blocks[i].setColor(color); // Set all color to color
            }
        }
    }
    /**
     * Returns the current {@link Color} of this piece.
     * The color applies for all {@link Block} in this piece.
     * @return the color of the piece.
     */
    public Color getColor(){
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
            // Sort by getLineI(), then getLineK() if equal
            while (j >= 0 && (blocks[j].getLineI() > key.getLineI() || (blocks[j].getLineI() == key.getLineI() && blocks[j].getLineK() > key.getLineK()))) {
                blocks[j + 1] = blocks[j];
                j--;
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
        StringBuilder str = new StringBuilder("{Piece: ");
        for (Block block : blocks) {
            str.append(block.getLines());
        }
        return str + "}";
    }
    /**
     * Compares this piece to another for equality.
     * Two pieces are equal if they have the same {@link #length()} and identical {@link Block} positions.
     * {@link Color} is not involved in this comparison.
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
