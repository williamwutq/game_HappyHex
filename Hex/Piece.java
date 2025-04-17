package Hex;

import GUI.GameEssentials;
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
 * @author William Wu
 * @version 1.1
 */
public class Piece implements HexGrid{
    private static boolean easy = false;
    private Block[] blocks;
    private Color color;

    // Static
    /**
     * Returns whether the game or context is currently set to "easy" mode.
     * Easy mode affects game piece generation and is usually respected by {@link special.SpecialFeature}.
     * @return {@code true} if in easy mode, {@code false} otherwise.
     */
    public static boolean isEasy(){return easy;}
    /** Sets the piece generation mode to "easy".*/
    public static void setEasy(){easy = true;}
    /** Sets the piece generation mode to "normal".*/
    public static void setNormal(){easy = false;}

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
                result[i] = new Block(-1, -1, color, false);
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

    // Static pieces
    /**
     * Returns a single-block piece.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 1<br>
     * Position: (0, 0)</p>
     *
     * @return a piece with a single block.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece uno() {
        Piece p = new Piece(1, GameEssentials.generateColor());
        p.add(Block.block(0, 0));
        return p;
    }
    /**
     * Returns a seven-block "big block" piece in the shape of a radius 2 hexagon.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 7<br>
     * Positions: (-1,-1), (-1,0), (0,-1), (0,0), (0,1), (1,0), (1,1)</p>
     *
     * @return a large complex piece with 7 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece bigBlock() {
        Piece p = new Piece(7, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a triangle piece with 3 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (0,0), (0,1), (1,1)</p>
     *
     * @return a triangular piece made of 3 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece triangle3A() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a triangle piece with 3 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,-1), (0,-1), (0,0)</p>
     *
     * @return a triangular piece made of 3 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece triangle3B() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        return p;
    }
    /**
     * Returns a line piece with 3 blocks along the I-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (0,-1), (0,0), (0,1)</p>
     *
     * @return a line piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece line3I() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    /**
     * Returns a line piece with 3 blocks along the J-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,-1), (0,0), (1,1)</p>
     *
     * @return a line piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece line3J() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a line piece with 3 blocks along the K-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,0), (0,0), (1,0)</p>
     *
     * @return a line piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece line3K() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns an L-shape corner piece with 3 blocks symmetrical along the I-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,-1), (0,0), (1,0)</p>
     *
     * @return an L-shape corner piece with length of 3.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner3Il() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns an L-shape corner piece with 3 blocks symmetrical along the J-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,0), (0,-1), (0,0)</p>
     *
     * @return an L-shape corner piece with length of 3.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner3Jl() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        return p;
    }
    /**
     * Returns an L-shape corner piece with 3 blocks symmetrical along the K-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,-1), (0,0), (0,1)</p>
     *
     * @return an L-shape corner piece with length of 3.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner3Kl() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    /**
     * Returns an L-shape corner piece with 3 blocks symmetrical along the I-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (-1,0), (0,0), (1,1)</p>
     *
     * @return an L-shape corner piece with length of 3.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner3Ir() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an L-shape corner piece with 3 blocks symmetrical along the J-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (0,0), (0,1), (1,0)</p>
     *
     * @return an L-shape corner piece with length of 3.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner3Jr() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns an L-shape corner piece with 3 blocks symmetrical along the K-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 3<br>
     * Positions: (0,-1), (0,0), (1,1)</p>
     *
     * @return an L-shape corner piece with length of 3.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner3Kr() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns fan shaped piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,0), (0,1), (1,0)</p>
     *
     * @return a fan shaped piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece fan4A() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns fan shaped piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,0), (0,-1), (0,0), (1,1)</p>
     *
     * @return a fan shaped piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece fan4B() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a rhombus shape piece with 4 blocks symmetrical along the I-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (0,-1), (0,0), (1,0), (1,1)</p>
     *
     * @return a rhombus shape piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece rhombus4I() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a rhombus shape piece with 4 blocks symmetrical along the J-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (-1,0), (0,-1), (0,0)</p>
     *
     * @return a rhombus shape piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece rhombus4J() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        return p;
    }
    /**
     * Returns a rhombus shape piece with 4 blocks symmetrical along the K-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,-1), (0,0), (1,0)</p>
     *
     * @return a rhombus shape piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece rhombus4K() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns a corner piece with 4 blocks symmetrical along the I-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (-1,0), (0,-1), (1,0)</p>
     *
     * @return a corner piece with 4 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner4Ir() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns a corner piece with 4 blocks symmetrical along the I-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,0), (0,1), (1,0), (1,1)</p>
     *
     * @return a corner piece with 4 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner4Il() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a corner piece with 4 blocks symmetrical along the J-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,-1), (1,0), (1,1)</p>
     *
     * @return a corner piece with 4 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner4Jr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a corner piece with 4 blocks symmetrical along the J-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (-1,0), (0,1), (1,1)</p>
     *
     * @return a corner piece with 4 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner4Jl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns a corner piece with 4 blocks symmetrical along the K-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (-1,0), (0,-1), (0,1)</p>
     *
     * @return a corner piece with 4 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner4Kr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 1));
        return p;
    }
    /**
     * Returns a corner piece with 4 blocks symmetrical along the K-axis.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (0,-1), (0,1), (1,0), (1,1)</p>
     *
     * @return a corner piece with 4 blocks.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece corner4Kl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,-1), (0,0), (0,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Ia() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (0,-1), (0,0), (0,1), (1,0)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Ib() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,0), (0,-1), (0,0), (0,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Ic() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (0,-1), (0,0), (0,1), (1,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Id() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,0), (1,0), (1,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Ja() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,0), (0,1), (1,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Jb() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (0,-1), (0,0), (1,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Jc() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (-1,0), (0,0), (1,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Jd() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,0), (0,0), (0,1), (1,0)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Ka() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,-1), (-1,0), (0,0), (1,0)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Kb() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,0), (0,0), (1,0), (1,1)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Kc() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    /**
     * Returns an asymmetrical piece with 4 blocks.
     * Color of this piece is randomly generated by {@link GameEssentials#generateColor()}
     * <p>Length: 4<br>
     * Positions: (-1,0), (0,-1), (0,0), (1,0)</p>
     *
     * @return an asymmetrical piece.
     * @see #getIndexedPiece(int)
     * @see #generatePiece()
     */
    public static Piece asymmetrical4Kd() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    /**
     * Returns the total number of distinct piece types that can be retrieved using {@link #getIndexedPiece(int)}.
     * @return the maximum index (exclusive) that can be used to retrieve a piece using {@code getIndexedPiece}.
     *         For example, valid indices are in the range [0, getMaxPieceIndex()).
     */
    public static int getMaxPieceIndex(){return 36;}
    /**
     * Returns a specific {@link Piece} corresponding to the provided index.
     * <p>
     * This method maps a given index in the range [0, {@link #getMaxPieceIndex()}) to a unique game piece.
     * Each index corresponds to a distinct piece configuration.
     * <p>
     * If the provided index is outside the expected range, a default piece (usually the largest or most general)
     * is returned as a fallback. This method does not throw an exception.
     *
     * @param index the index of the desired piece, in the range [0, {@link #getMaxPieceIndex()}).
     * @return the {@link Piece} instance corresponding to the index, or a fallback piece for invalid indices.
     */
    public static Piece getIndexedPiece(int index){
        if (index == 0){
            return uno();
        } else if (index == 1){
            return triangle3A();
        } else if (index == 2){
            return triangle3B();
        } else if (index == 3){
            return line3I();
        } else if (index == 4){
            return line3J();
        } else if (index == 5){
            return line3K();
        } else if (index == 6){
            return corner3Il();
        } else if (index == 7){
            return corner3Jl();
        } else if (index == 8){
            return corner3Kl();
        } else if (index == 9){
            return corner3Ir();
        } else if (index == 10){
            return corner3Jr();
        } else if (index == 11){
            return corner3Kr();
        } else if (index == 12){
            return rhombus4I();
        } else if (index == 13){
            return rhombus4J();
        } else if (index == 14){
            return rhombus4K();
        } else if (index == 15){
            return fan4A();
        } else if (index == 16){
            return fan4B();
        } else if (index == 17){
            return corner4Ir();
        } else if (index == 18){
            return corner4Il();
        } else if (index == 19){
            return corner4Jr();
        } else if (index == 20){
            return corner4Jl();
        } else if (index == 21){
            return corner4Kr();
        } else if (index == 22){
            return corner4Kl();
        } else if (index == 23){
            return asymmetrical4Ia();
        } else if (index == 24){
            return asymmetrical4Ib();
        } else if (index == 25){
            return asymmetrical4Ic();
        } else if (index == 26){
            return asymmetrical4Id();
        } else if (index == 27){
            return asymmetrical4Ja();
        } else if (index == 28){
            return asymmetrical4Jb();
        } else if (index == 29){
            return asymmetrical4Jc();
        } else if (index == 30){
            return asymmetrical4Jd();
        } else if (index == 31){
            return asymmetrical4Ka();
        } else if (index == 32){
            return asymmetrical4Kb();
        } else if (index == 33){
            return asymmetrical4Kc();
        } else if (index == 34){
            return asymmetrical4Kd();
        } else return bigBlock();
    }
    /**
     * Generates a random {@link Piece} based on the current difficulty mode.
     * <p>
     * The method uses {@link Math#random()} to randomly select and return a specific
     * piece instance. The distribution and pool of possible pieces differ depending
     * on whether the application is in easy mode or not.
     *
     * <p><b>Easy Mode:</b><br>
     * Generates a limited and more beginner-friendly set of pieces with higher
     * probability for simpler shapes. This generation mode tends to generate
     * pieces with length of three and easy to place under most conditions. The
     * special piece {@link #uno()} is only available in this mode.</p>
     *
     * <p><b>Normal Mode:</b><br>
     * Includes a wider range of more complex pieces, with different frequency
     * distributions. This mode contains more unfriendly pieces such as corners
     * and unsymmetrical pieces. Generally, it will generate pieces with more
     * {@link Block}. The special piece {@link #bigBlock()} is only available
     * in this mode.</p>
     *
     * <p>
     * The detailed frequencies of pieces generated is not disclosed and may vary
     * based on game version.
     *
     * @return a randomly selected {@link Piece} appropriate to the current difficulty mode
     */
    public static Piece generatePiece() {
        if(easy) {
            // Easier generation
            int i = (int) (Math.random() * 74);
            if (between(i, 0, 8)) {
                return triangle3A();
            } else if (between(i, 8, 16)) {
                return triangle3B();
            } else if (between(i, 16, 22)) {
                return line3I();
            } else if (between(i, 22, 28)) {
                return line3J();
            } else if (between(i, 28, 34)) {
                return line3K();
            } else if (between(i, 34, 37)) {
                return corner3Ir();
            } else if (between(i, 37, 40)) {
                return corner3Jr();
            } else if (between(i, 40, 43)) {
                return corner3Kr();
            } else if (between(i, 43, 46)) {
                return corner3Il();
            } else if (between(i, 46, 49)) {
                return corner3Jl();
            } else if (between(i, 49, 52)) {
                return corner3Kl();
            } else if (between(i, 52, 56)) {
                return rhombus4I();
            } else if (between(i, 56, 60)) {
                return rhombus4J();
            } else if (between(i, 60, 64)) {
                return rhombus4K();
            }
            int j = (int) (Math.random() * 25);
            if (j == 0 || j == 1) {
                return fan4A();
            } else if (j == 2 || j == 3) {
                return fan4B();
            } else if (j == 4) {
                return corner4Il();
            } else if (j == 5) {
                return corner4Ir();
            } else if (j == 6) {
                return corner4Jl();
            } else if (j == 7) {
                return corner4Jr();
            } else if (j == 8) {
                return corner4Kl();
            } else if (j == 9) {
                return corner4Kr();
            } else if (j == 10) {
                return asymmetrical4Ia();
            } else if (j == 11) {
                return asymmetrical4Ib();
            } else if (j == 12) {
                return asymmetrical4Ic();
            } else if (j == 13) {
                return asymmetrical4Id();
            } else if (j == 14) {
                return asymmetrical4Ja();
            } else if (j == 15) {
                return asymmetrical4Jb();
            } else if (j == 16) {
                return asymmetrical4Jc();
            } else if (j == 17) {
                return asymmetrical4Jd();
            } else if (j == 18) {
                return asymmetrical4Ka();
            } else if (j == 19) {
                return asymmetrical4Kb();
            } else if (j == 20) {
                return asymmetrical4Kc();
            } else if (j == 21) {
                return asymmetrical4Kd();
            } else return uno(); // Should never reach
        } else {
            int i = (int) (Math.random() * 86);
            if (between(i, 0, 6)) {
                return triangle3A();
            } else if (between(i, 6, 12)) {
                return triangle3B();
            } else if (between(i, 12, 16)) {
                return line3I();
            } else if (between(i, 16, 20)) {
                return line3J();
            } else if (between(i, 20, 24)) {
                return line3K();
            } else if (between(i, 24, 26)) {
                return corner3Ir();
            } else if (between(i, 26, 28)) {
                return corner3Jr();
            } else if (between(i, 28, 30)) {
                return corner3Kr();
            } else if (between(i, 30, 32)) {
                return corner3Il();
            } else if (between(i, 32, 34)) {
                return corner3Jl();
            } else if (between(i, 34, 36)) {
                return corner3Kl();
            } else if (between(i, 36, 40)) {
                return rhombus4I();
            } else if (between(i, 40, 44)) {
                return rhombus4J();
            } else if (between(i, 44, 48)) {
                return rhombus4K();
            } else if (between(i, 48, 54)) {
                return fan4A();
            } else if (between(i, 54, 60)) {
                return fan4B();
            } else if (between(i, 60, 62)) {
                return corner4Il();
            } else if (between(i, 62, 64)) {
                return corner4Ir();
            } else if (between(i, 64, 66)) {
                return corner4Jl();
            } else if (between(i, 66, 68)) {
                return corner4Jr();
            } else if (between(i, 68, 70)) {
                return corner4Kl();
            } else if (between(i, 70, 72)) {
                return corner4Kr();
            } else if (i == 72) {
                return asymmetrical4Ia();
            } else if (i == 73) {
                return asymmetrical4Ib();
            } else if (i == 74) {
                return asymmetrical4Ic();
            } else if (i == 75) {
                return asymmetrical4Id();
            } else if (i == 76) {
                return asymmetrical4Ja();
            } else if (i == 77) {
                return asymmetrical4Jb();
            } else if (i == 78) {
                return asymmetrical4Jc();
            } else if (i == 79) {
                return asymmetrical4Jd();
            } else if (i == 80) {
                return asymmetrical4Ka();
            } else if (i == 81) {
                return asymmetrical4Kb();
            } else if (i == 82) {
                return asymmetrical4Kc();
            } else if (i == 83) {
                return asymmetrical4Kd();
            } else return bigBlock();
        }
    }
    /**
     * Checks if a given value is within the half-open interval [start, end).
     *
     * @param value the value to test
     * @param start the inclusive lower bound
     * @param end the exclusive upper bound
     * @return {@code true} if {@code value} is in the range [start, end); {@code false} otherwise
     */
    private static boolean between(int value, int start, int end) {
        return value >= start && value < end;
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
