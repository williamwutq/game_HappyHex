package hex;

import java.awt.Color;
import java.util.ArrayList;

/**
 * The {@code HexEngine} class implements the {@link HexGrid} interface and provides a
 * complete engine for managing a two-dimensional hexagonal block grid. This grid is
 * used for constructing and interacting with hex-based shapes the game.
 * <p>
 * The engine maintains an array of {@link Block} instances arranged in a hexagonal
 * pattern with its leftmost {@code Block} at origin (0,0), and provides operations such as:
 * <ul>
 *     <li>Grid {@link #HexEngine initialization} and {@link #reset}</li>
 *     <li>Automatic coloring through {@link #setDefaultBlockColors}</li>
 *     <li>Efficient block {@link #getBlock(int) lookup} using {@link #search binary search}</li>
 *     <li>Grid placement {@link #checkAdd validation} and piece {@link #add insertion}</li>
 *     <li>Line detection and {@link #eliminate elimination} across I/J/K axes</li>
 *     <li>Deep copy support through the {@link #clone} method</li>
 * </ul>
 *
 * <p><h3>Grid Structure:</h3>
 * The hex grid uses an axial coordinate system (i, k), where i - j + k = 0, and j is
 * implicitly derived as j = i + k. The coordinate system has three axes, I, J, K (not
 * to be confused with 3D). I+ is 60 degree from J+, J+ is 60 degree from K+, and K+ is
 * 60 degree from I-. Raw coordinate (or hex coordinate) refers to the distance of a
 * point along one of the axes multiplied by 2. Line-coordinate (usually (I, K) is sufficient)
 * refers to the perpendicular distance to those axes and are calculated based on raw coordinates.
 * Blocks are constructed and stored in a sorted array by increasing raw coordinate i and then k.
 * See {@link Hex} for more coordinate information.
 *
 * <p><h3>Grid Size:</h3>
 * The total number of blocks in a hexagonal grid of radius {@code r} is calculated as:
 * <pre>
 *     Aₖ = 1 + 3(r)(r - 1)
 * </pre>
 * This is derived from the recursive pattern:
 * <pre>
 *     {@code Aₖ = Aₖ₋₁ + 6(k - 1); A₁ = 1}
 * </pre>
 *
 * <p><h3>Block Coloring:</h3>
 * By default, blocks are visually represented using two colors: one for the empty {@link Block#getState state}
 * (false) and one for the filled {@code state} (true). These default colors can be configured using the
 * {@code setDefaultBlockColors} method. When a block's state is updated using {@code setState} or during
 * {@link #HexEngine initialization} and {@link #reset}, it automatically changes to the corresponding default color.
 * For full control, the {@link #setBlock} method can be used to manually assign a specific color to a block,
 * independent of its state. See {@link Block#setColor} for coloring blocks.
 *
 * <p><h3>Machine Learning:</h3>
 * The {@code HexEngine} supports machine learning reward functions by exposing utility methods that help evaluate
 * the quality and validity of in-game actions. These metrics can guide reinforcement learning agents in making
 * smarter decisions within the block blast game environment.
 * <p>
 * Invalid moves can be discouraged by using the {@link #checkAdd(Hex, HexGrid)} method, which returns {@code false}
 * for placements that are not allowed—such as overlapping with existing occupied blocks. This can be used to
 * assign negative rewards to agents attempting illegal placements.
 * <p>
 * Rewarding effective gap-filling and spatial efficiency is made possible through the {@link #computeDenseIndex(Hex, HexGrid)}
 * method. This computes a normalized score (0 to 1) representing how densely the placed grid would interact
 * with surrounding blocks, encouraging agents to maximize filled neighbors and minimize empty space.
 * @since 1.0
 * @author William Wu
 * @version 1.2
 */
public class HexEngine implements HexGrid{
    private Color emptyBlockColor;
    private Color filledBlockColor;
    private int radius;
    private Block[] blocks;
    /**
     * Constructs a {@code HexEngine} with the specified radius and default colors.
     * Populates the hexagonal {@link HexGrid block grid} with valid blocks.
     * <p>
     * Each valid grid cell is tested via {@link Block#inRange(int)},
     * and only valid (i, k) combinations are included.
     * <p>
     * The blocks are inserted in row-major order (i.e., by i, then by k).
     * This property is used by the {@link #search binary search} for lookup efficiency.
     *
     * @param radius the radius of the hexagonal grid, where radius should be greater than 1.
     * @param emptyBlockColor the default color of {@link Block} when it is empty.
     * @param filledBlockColor the default color of {@link Block} when it is filled.
     * @see HexGrid
     * @see Block
     */
    public HexEngine(int radius, Color emptyBlockColor, Color filledBlockColor){
        this.radius = radius;
        this.emptyBlockColor = emptyBlockColor;
        this.filledBlockColor = filledBlockColor;
        // Calculate array size
        // Recursive Formula Ak = A(k-1) + 6 * (k-1)
        // General Formula: Ak = 1 + 3 * (k-1)*(k)
        this.blocks = new Block[1 + 3*(radius)*(radius-1)];
        // Add into array to generate the grid
        int i = 0;
        for(int a = 0; a <= radius*2-1; a++){
            for(int b = 0; b <= radius*2-1; b++){
                Block nb = new Block(new Hex(), emptyBlockColor);
                nb.moveI(b);
                nb.moveK(a);
                if(nb.inRange(radius)){
                    blocks[i] = nb;
                    i ++;
                }
            }
        }
        // Already sorted by first I then K
    }
    /**
     * Resets all blocks to their default state and color.
     * This does not change reference of the blocks.
     */
    public void reset(){
        // Set all to empty and default color
        Block[] newBlocks = new Block[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            newBlocks[i] = new Block (blocks[i], emptyBlockColor);
        }
        blocks = newBlocks;
    }
    /**
     * Returns the radius of the grid.
     * @return radius of the grid
     */
    public int getRadius(){
        return radius;
    }
    // Implements HexGrid
    /**
     * Returns the number of blocks in the grid.
     * @return length of the block array
     * @see #getBlock(int)
     */
    public int length(){
        if(blocks == null){
            return 0;
        }else{
            return blocks.length;
        }
    }
    /**
     * Returns all blocks in the grid in an array
     * @return array of blocks
     * @see #getBlock(int)
     * @see #length()
     */
    public Block[] blocks(){
        return this.blocks;
    }
    /**
     * Checks if a hexagonal coordinate is within the bounds of the grid.
     * This method delegate to {@link Hex#inRange(int)} for checking.
     * @param i the I-coordinate
     * @param k the K-coordinate
     * @return true if the coordinate is in range
     */
    public boolean inRange(int i, int k){
        // Use line
        return Hex.hex(i, k).inRange(radius);
    }
    /**
     * Retrieves a {@link Block} at the given (i, k) coordinate.
     * This performs a {@link #search binary search} for efficient lookup.
     *
     * @param i the I-coordinate
     * @param k the K-coordinate
     * @return the {@code Block} if found, or null otherwise
     * @see #getBlock(int)
     * @see #setBlock(int, int, Block)
     */
    public Block getBlock(int i, int k){
        if(inRange(i, k)){
            int index = search(i, k, 0, length()-1);
            if (index >= 0) {
                return getBlock(index); // private binary search
            }
        }
        return null;
    }
    /**
     * Retrieves the {@link Block} at the specified array index.
     * @param index the block array index
     * @return the {@code Block} at the given index
     * @see #getBlock(int, int)
     * @see #length()
     */
    public Block getBlock(int index){
        return blocks[index];
    }
    /**
     * Sets the {@link Block} at a specific grid coordinate.
     * This performs a {@link #search binary search} to obtain the targeting block.
     *
     * @param i I coordinate
     * @param k K coordinate
     * @param block the new {@code Block} to place at this position
     */
    public void setBlock(int i, int k, Block block){
        if(inRange(i, k)){
            int index = search(i, k, 0, length()-1);
            if (index >= 0) {
                blocks[index] = block;
            }
        }
    }
    /**
     * Sets the state of a {@link Block} at a specific grid coordinate.
     * This performs a {@link #search binary search} to obtain the targeting block.
     * This automatically set the color of the block depending on its state.
     *
     * @param i I coordinate
     * @param k K coordinate
     * @param state the new state of the block (true = occupied).
     * @since 1.2
     */
    public void setState(int i, int k, boolean state){
        if(inRange(i, k)){
            int index = search(i, k, 0, length()-1);
            if (index >= 0) {
                Block block = blocks[index];
                if(block.getState() != state){
                    block.setState(state);
                    if(state){
                        block.setColor(filledBlockColor);
                    } else block.setColor(emptyBlockColor);
                }
            }
        }
    }
    /**
     * Performs a binary search to locate a block at (i, k).
     * Assumes the array is sorted by I, then K.
     *
     * @param i I coordinate
     * @param k K coordinate
     * @param start search range start index
     * @param end search range end index
     * @return index of the block in the array, or -1 if not found
     */
    private int search(int i, int k, int start, int end){
        if(start > end){return -1;}
        int middleIndex = (start + end)/2;
        Block middle = blocks[middleIndex];
        if(middle.getLineI() == i && middle.getLineK() == k){
            return middleIndex;
        } else if (middle.getLineI() < i){
            // second half
            return search(i, k, middleIndex+1, end);
        } else if (middle.getLineI() > i){
            // first half
            return search(i, k, start, middleIndex-1);
        } else if (middle.getLineK() < k) {
            // second half
            return search(i, k, middleIndex+1, end);
        } else {
            // first half
            return search(i, k, start, middleIndex-1);
        }
    }
    /**
     * Sets the default block colors used by the HexEngine.
     * <p>
     * These default colors are used when a block's state is changed via {@link #setState} or on initialization
     * and {@link #reset}. For custom coloring, use {@link #setBlock} to manually assign a color to a block.
     *
     * @param emptyBlockColor the color used for blocks in the empty (false) state
     * @param filledBlockColor the color used for blocks in the filled (true) state
     * @since 1.2
     */
    public void setDefaultBlockColors(Color emptyBlockColor, Color filledBlockColor){
        this.emptyBlockColor = emptyBlockColor;
        this.filledBlockColor = filledBlockColor;
    }
    /**
     * Returns the current default color used for {@link Block} in the empty (false) state.
     * @return the default filled block color
     * @since 1.2
     */
    public Color getEmptyBlockColor(){
        return emptyBlockColor;
    }
    /**
     * Returns the current default color used for {@link Block} in the filled (true) state.
     * @return the default filled block color
     * @since 1.2
     */
    public Color getFilledBlockColor(){
        return filledBlockColor;
    }
    /**
     * Checks whether the {@code other} grid can be added to this grid
     * at the given {@code origin} without overlap or out-of-bounds errors.
     *
     * @param origin origin offset for placement
     * @param other the other hex grid to check
     * @return true if placement is valid
     * @see #add
     * @see #checkPositions
     */
    public boolean checkAdd(Hex origin, HexGrid other){
        // Iterate through other
        Block[] otherBlocks = other.blocks();
        for(int i = 0; i < other.length(); i ++){
            Block current = otherBlocks[i];
            // Null check and state check
            if (current != null && current.getState()){
                current = current.add(origin); // placement
                // Check for this HexGrid
                Block selfTarget = this.getBlock(current.getLineI(), current.getLineK());
                if (selfTarget == null || selfTarget.getState()){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * {@inheritDoc}
     * Throws exception if the operation would go out of bounds or overlap.
     * <p>
     * This method will directly modify the hexagonal grid and have access to all the {@link Block}
     * contained in this {@code HexEngine}. This change is permanent. The addition process consumes
     * processing power, so it is recommended to only call this method when addition can be ensured.
     * <p>
     * Use {@link #checkPositions} to check potential adding coordinates and {@link #checkAdd} to
     * check the boolean possibility of addition. For elimination, use {@link #eliminate()}.
     *
     * @param origin the offset to place the new grid
     * @param other the grid to add
     * @throws IllegalArgumentException if the placement is invalid
     */
    public void add(Hex origin, HexGrid other) throws IllegalArgumentException{
        // Iterate through other
        Block[] otherBlocks = other.blocks();
        for(int i = 0; i < other.length(); i ++){
            Block current = otherBlocks[i];
            // Null check and state check
            if (current != null && current.getState()){
                current = current.add(origin); // placement
                // Check for this HexGrid
                Block selfTarget = this.getBlock(current.getLineI(), current.getLineK());
                if (selfTarget == null){
                    // If it cannot be found, it must be out of range
                    throw new IllegalArgumentException(new IndexOutOfBoundsException("Block out of grid when adding"));
                } else if (selfTarget.getState()){
                    // If this position is already occupied, it can't be added neither
                    throw new IllegalArgumentException("Cannot add into existing block");
                } else {
                    // If all checks, proceed to add block
                    setBlock(current.getLineI(), current.getLineK(), current);
                }
            }
        }
    }
    /**
     * Returns all valid positions where {@code other} grid can be added.
     * If it is only needed to determine the possibility of addition, use {@link #checkAdd}.
     * @param other the hex grid to place
     * @return list of possible hex origins for valid placement
     * @see #add
     */
    public ArrayList<Hex> checkPositions(HexGrid other){
        ArrayList<Hex> positions = new ArrayList<Hex>();
        // Try to find positions by checking all available space
        for (Block block : blocks){
            Hex hex = block.thisHex();
            // Use blocks as hex
            if(checkAdd(hex, other)){
                // If it is possible to add, record this position
                positions.add(hex);
            }
        }
        // Return
        return positions;
    }
    /**
     * Eliminates fully occupied lines along I, J, or K axes then return the blocks that are being
     * eliminated.
     * <p>
     * This method will directly modify the hexagonal grid and have access to all the {@link Block}
     * contained in this {@code HexEngine}. This change is permanent. The elimination process consumes
     * processing power, so it is recommended to only call this method when elimination can be ensured.
     * <p>
     * For checking the possibility of eliminating a piece, use the {@link #checkEliminate()} method
     * instead. For adding pieces into this {@code HexEngine}, see {@link #add}.
     * @return blocks eliminated
     */
    public Block[] eliminate(){
        // Eliminate according to I, J, K, then return how many blocks are being eliminated
        ArrayList<Block> eliminate = new ArrayList<Block>();
        // Check I
        for(int i = 0; i < radius*2 - 1; i ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineI() == i){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            eliminate.addAll(line);
        }
        // Check J
        for(int j = 1 - radius; j < radius; j ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineJ() == j){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            eliminate.addAll(line);
        }
        // Check K
        for(int k = 0; k < radius*2 - 1; k ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineK() == k){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            eliminate.addAll(line);
        }
        // Eliminate
        Block[] eliminated = new Block[eliminate.size()];
        for (int i = 0; i < eliminate.size(); i++) {
            Block block = eliminate.get(i);
            eliminated[i] = block.clone();
            setState(block.getLineI(), block.getLineK(), false);
        }
        return eliminated; // blocks being eliminated
    }
    /**
     * Checks whether any full line can be eliminated in the hex grid.
     * @return true if at least one line is full and able to be eliminated.
     * @see #checkEliminateI(int)
     * @see #checkEliminateJ(int)
     * @see #checkEliminateK(int)
     */
    public boolean checkEliminate(){
        // Check I
        for(int i = 0; i < radius*2 - 1; i ++){
            if(checkEliminateI(i)) return true;
        }
        // Check J
        for(int j = 1 - radius; j < radius; j ++){
            if(checkEliminateJ(j)) return true;
        }
        // Check K
        for(int k = 0; k < radius*2 - 1; k ++){
            if(checkEliminateK(k)) return true;
        }
        return false;
    }
    /**
     * Checks if the entire line of constant I can be eliminated.
     * @param i the I-line to check
     * @return true if all blocks are filled and part of a valid piece
     * @see Block#getLineI()
     * @see #checkEliminate()
     */
    public boolean checkEliminateI(int i){
        for(int index = 0; index < length(); index ++){
            if(blocks[index].getLineI() == i && !blocks[index].getState()) return false;
        }
        return true;
    }
    /**
     * Checks if the entire line of constant J can be eliminated.
     * @param j the J-line to check
     * @return true if all blocks are filled and part of a valid piece
     * @see Block#getLineJ()
     * @see #checkEliminate()
     */
    public boolean checkEliminateJ(int j){
        for(int index = 0; index < length(); index ++){
            if(blocks[index].getLineJ() == j && !blocks[index].getState()) return false;
        }
        return true;
    }
    /**
     * Checks if the entire line of constant K can be eliminated.
     * @param k the K-line to check
     * @return true if all blocks are filled and part of a valid piece
     * @see Block#getLineK()
     * @see #checkEliminate()
     */
    public boolean checkEliminateK(int k){
        for(int index = 0; index < length(); index ++){
            if(blocks[index].getLineK() == k && !blocks[index].getState()) return false;
        }
        return true;
    }
    /**
     * Computes a density index score for hypothetically placing another {@link HexGrid} onto this grid.
     * <p>
     * The density index is a value between 0 and 1 that represents how "dense" the surrounding area is
     * when the given {@code other} grid were placed on this grid at the specified {@code origin} coordinate.
     * A score of 1 represents that all the surrounding blocks of that grid would be filled and 0 represent
     * that the grid would be "alone" after addition. A higher score indicates a more tightly packed
     * configuration, which may correlate with better outcomes in certain game strategies or ML reward functions.
     * <p>
     * This method is used in reinforcement learning contexts to evaluate placement strategies and learn high-scoring
     * moves. It checks whether the {@link Block} in {@code other} can be placed without overlapping existing occupied
     * blocks. For each valid block, it computes how many potential neighbors are unoccupied in {@code other},
     * and how many are actually occupied in {@code this} grid. These values contribute to the ratio:
     * <pre>{@code
     *     denseIndex = totalPopulatedNeighbors / totalPossibleNeighbors
     * }</pre>
     *
     * @param origin the position in which the {@code other} grid is added for hypothetical placement.
     * @param other the {@link HexGrid} representing a piece to be evaluated for placement.
     * @return a density index between 0 and 1. Returns 0 if placement is invalid or no potential neighbors exist.
     * @see #countNeighbors(int, int, boolean)
     * @since 1.2
     */
    public double computeDenseIndex(Hex origin, HexGrid other){
        int totalPossible = 0;
        int totalPopulated = 0;
        Block[] otherBlocks = other.blocks();
        for(int i = 0; i < other.length(); i ++){
            Hex current = otherBlocks[i];
            // Null check and state check
            if (current != null && otherBlocks[i].getState()){
                current = current.add(origin); // placement
                Block selfTarget = this.getBlock(current.getLineI(), current.getLineK());
                if (selfTarget == null || selfTarget.getState()){
                    // Check for addition possibility
                    return 0;
                } else {
                    totalPossible += (6 - other.countNeighbors(otherBlocks[i].getLineI(), otherBlocks[i].getLineK(), false));
                    totalPopulated += this.countNeighbors(current.getLineI(), current.getLineK(), true);
                }
            }
        }
        if (totalPossible == 0){
            return 0;
        } else return (double) totalPopulated / totalPossible;
    }


    /**
     * Returns a string representation of the grid and block states.
     * @return string showing block positions and states
     * @see Block#toString()
     */
    public String toString(){
        StringBuilder str = new StringBuilder("{HexEngine: ");
        for (Block block : blocks) {
            str.append(block.getLines());
            str.append(",");
            str.append(block.getState());
            str.append("; ");
        }
        return str + "}";
    }

    /**
     * Returns an identical deep clone of this {@code HexEngine}.
     * Each {@link Block} object contained in this instance is cloned individually.
     * @return a deep copy of this {@code HexEngine} object.
     * @see Block#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        HexEngine newEngine;
        try{
            newEngine = (HexEngine) super.clone();
            newEngine.radius = this.radius;
        } catch (CloneNotSupportedException e) {
            newEngine = new HexEngine(this.radius, this.emptyBlockColor, this.filledBlockColor);
        }
        for(int i = 0; i < this.length(); i ++){
            newEngine.blocks[i] = this.blocks[i].clone();
        }
        return newEngine;
    }
}
