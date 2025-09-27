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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The {@code HexEngine} class implements the {@link HexGrid} interface and provides a
 * complete engine for managing a two-dimensional hexagonal block grid. This grid is
 * used for constructing and interacting with hex-based shapes the game.
 * <p>
 * The engine maintains an array of {@link Block} instances arranged in a hexagonal
 * pattern with its leftmost {@code Block} at origin (0,0), and provides operations such as:
 * <ul>
 *     <li>Grid {@link #HexEngine initialization} and {@link #reset}</li>
 *     <li>Automatic coloring through color indexes</li>
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
 * <p>
 * This coordinate system is used with {@link #search binary search} and index-accelerated algorithm
 * to reduce time complexity significantly in relevant operations such as {@link #eliminate() elimination}
 * and {@link #getBlock(int, int) individual block access}. This also constitutes the non-changing nature
 * of this implementation of {@link HexGrid} interface.
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
 * (false) and one for the filled {@code state} (true). When a block's state is updated using {@code setState} or during
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
 * <p>
 * Punishing moves that leave too much blocks on the grid could be enabled with {@link #getPercentFilled()}, a method that
 * returns the percentage of blocks that are filled. High percentage of filled blocks indicates difficulty regarding elimination,
 * which, although trivial, may ultimately result in severe problems.
 * <p>
 * Rewarding moves the simplifies the board can be made using calculations regarding {@link #computeEntropy() entropy}.
 * Generally, moves that reduce entropy should be awarded and moves that increase entropy significantly should be punished.
 * @since 1.0
 * @author William Wu
 * @version 2.0
 */
public class HexEngine implements HexGrid, Iterable<Block>, Cloneable {
    private static final double logBaseEOf2 = Math.log(2);
    private int radius;
    private Block[] blocks;

    /**
     * Computes the base-2 logarithm of a given value.
     *
     * @param x the value to compute the logarithm for; must be greater than 0
     * @return the base-2 logarithm of {@code x}
     * @throws IllegalArgumentException if {@code x} is less than or equal to 0
     */
    public static double log2(double x) {
        if (x <= 0) throw new IllegalArgumentException("log2 is undefined for non-positive values: " + x);
        return Math.log(x) / logBaseEOf2;
    }
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
     * @see HexGrid
     * @see Block
     */
    public HexEngine(int radius){
        this.radius = radius;
        // Calculate array size
        // Recursive Formula Ak = A(k-1) + 6 * (k-1)
        // General Formula: Ak = 1 + 3 * (k-1)*(k)
        this.blocks = new Block[1 + 3*(radius)*(radius-1)];
        // Add into array to generate the grid
        int i = 0;
        for(int a = 0; a <= radius*2-1; a++){
            for(int b = 0; b <= radius*2-1; b++){
                Block nb = new Block(new Hex());
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
            newBlocks[i] = new Block (blocks[i]);
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
    /**
     * Returns the number of occupied {@link Block}s in the grid.
     * <p>
     * The number returned will always be between 0 and {@link #length()}.
     * @return the number of occupied (filled) blocks
     * @since 1.3
     */
    public int getFilled(){
        int total = 0;
        for (Block block : blocks){
            if (block.getState()) total ++;
        }
        return total;
    }
    /**
     * Returns the percentage of occupied {@link Block}s in the grid, as a double between 0 and 1.
     *
     * @return the percentage of occupied (filled) blocks in respect to all blocks contained in the grid
     * @see #getFilled()
     * @see #length()
     * @since 1.3
     */
    public double getPercentFilled(){
        return (double) getFilled() / blocks.length;
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
     * Retrieves a {@link Block} at the given hex coordinate.
     * This performs a {@link #search binary search} for efficient lookup.
     *
     * @param hex the hex coordinate
     * @return the {@code Block} if found, or null otherwise
     * @see #getBlock(int)
     * @see #setBlock(int, int, Block)
     */
    public Block getBlock(Hex hex){
        if(hex.inRange(radius)){
            int index = search(hex.getLineI(), hex.getLineK(), 0, length()-1);
            if (index >= 0) {
                return getBlock(index); // private binary search
            }
        }
        return null;
    }
    /**
     * Returns a predicate that tests whether a block at a given hex coordinate is within the grid's range.
     * This predicate can be used to filter or evaluate blocks based on their position.
     *
     * @return a predicate that returns true if the block at the given hex coordinate is within range
     * @since 2.0
     */
    public Predicate<Hex> inRangePredicate(){
        return hex -> hex.inRange(radius);
    }
    /**
     * Returns a predicate that tests whether a block at a given hex coordinate is occupied.
     * This predicate can be used to filter or evaluate blocks based on their state.
     *
     * @return a predicate that returns true if the block at the given hex coordinate is occupied
     * @since 2.0
     */
    public Predicate<Hex> statePredicate(){
        return this::getState;
    }
    /**
     * Retrieves the state of a {@link Block} at a specific grid coordinate.
     * This performs a {@link #search binary search} to obtain the targeting block.
     *
     * @param i I coordinate
     * @param k K coordinate
     * @return the state of the block (true = occupied), or false if out of range
     * @since 2.0
     */
    public boolean getState(int i, int k){
        Block block = getBlock(i, k);
        if(block != null){
            return block.getState();
        }
        return false;
    }
    /**
     * Retrieves the state of a {@link Block} at the given hex coordinate.
     * This performs a {@link #search binary search} to obtain the targeting block.
     *
     * @param hex the hex coordinate
     * @return the state of the block (true = occupied), or false if out of range
     * @since 2.0
     */
    public boolean getState(Hex hex){
        Block block = getBlock(hex);
        if(block != null){
            return block.getState();
        }
        return false;
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
     * Sets the {@link Block} state at the specified array index.
     * Automatically set color to default unfilled color if state is false,
     * set color to default filled color if the state is true.
     *
     * @param index the block array index
     * @param state the new state to set for the {@code Block} at this index.
     * @see #length()
     * @since 1.3
     */
    public void setState(int index, boolean state){
        blocks[index].setState(state);
        blocks[index].setColor(state?-2:-1);
    }
    /**
     * Sets the {@link Block} color index at the specified array index.
     *
     * @param index the block array index
     * @param color the new color to set for the {@code Block} at this index.
     * @see #length()
     * @since 1.3
     */
    public void setColor(int index, int color){
        blocks[index].setColor(color);
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
                        block.setColor(-2);
                    } else {
                        block.setColor(-1);
                    }
                }
            }
        }
    }
    /**
     * Return the index of a block at (i, k).
     * @param i I coordinate
     * @param k K coordinate
     * @return index of the block in the array, or -1 if not found
     * @since 1.4
     */
    public int getBlockIndex(int i, int k){
        return search(i, k, 0, length()-1);
    }
    /**
     * Return the index of a block at the specific Hex coordinate.
     * @param hex the hex coordinate
     * @return index of the block in the array, or -1 if not found
     * @since 1.4
     */
    public int getBlockIndex(Hex hex){
        return search(hex.getLineI(), hex.getLineK(), 0, length()-1);
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
     * Return an iterator over all blocks in the grid. This is optimized for performance.
     * <p>
     * The iterator traverses the blocks in the order they are stored in the internal array, as sorted by
     * increasing I coordinate and then K coordinate.
     * @return an iterator over the blocks in the grid
     * @since 2.0
     * @see #blockIterator()
     */
    public Iterator<Block> iterator() {
        return new Iterator<Block>() {
            int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < length();
            }
            @Override
            public Block next() {
                return blocks[currentIndex++];
            }
        };
    }
    /**
     * Return an iterator over all blocks in the grid. This is optimized for performance.
     * <p>
     * The iterator traverses the blocks in the order they are stored in the internal array, as sorted by
     * increasing I coordinate and then K coordinate.
     * @return an iterator over the blocks in the grid
     * @since 2.0
     */
    public Iterator<Block> blockIterator() {
        return iterator();
    }
    /**
     * Returns an iterable over lines of blocks along the I axis.
     * Each iteration returns an array of {@link Block}s representing one line.
     * The lines are returned in order from the topmost line (smallest I) to the bottommost line (largest I).
     * <p>
     * This iterable is useful for operations that need to process or analyze blocks line by line along the I axis,
     * such as rendering, line elimination, or game logic that depends on row-based interactions.
     * <p>
     * The number of lines returned is {@code radius * 2 - 1}, with each line containing a varying number of blocks
     * depending on its position in the hexagonal grid.
     * @return an iterable over lines of blocks along the I axis
     * @since 2.0
     * @see #iteratorI()
     * @see #eliminateI
     * @see #countEliminateI
     */
    public Iterable<Block[]> lineIterableI() {
        return this::iteratorI;
    }
    /**
     * Return an iterator that iterates over lines of blocks along the I axis.
     * Each call to {@code next()} returns an array of {@link Block}s representing one line.
     * The lines are returned in order from the topmost line (smallest I) to the bottommost line (largest I).
     * <p>
     * This iterator is useful for operations that need to process or analyze blocks line by line along the I axis,
     * such as rendering, line elimination, or game logic that depends on row-based interactions.
     * <p>
     * The number of lines returned is {@code radius * 2 - 1}, with each line containing a varying number of blocks
     * depending on its position in the hexagonal grid.
     * @return an iterator over lines of blocks along the I axis
     * @since 2.0
     * @see #eliminateI
     * @see #countEliminateI
     */
    public Iterator<Block[]> iteratorI() {
        return new Iterator<Block[]>() {
            int currentLine = 0;
            final int maxLines = radius * 2 - 1;
            final int constTerm = radius * (radius * 3 - 1) / 2;
            @Override
            public boolean hasNext() {
                return currentLine < maxLines;
            }
            @Override
            public Block[] next() {
                if (currentLine >= maxLines) {
                    throw new IndexOutOfBoundsException("No more I lines");
                }
                Block[] line;
                int index;
                if (currentLine < radius) {
                    // This is equals to the first loop in eliminateI
                    line = new Block[radius + currentLine];
                    index = currentLine * (radius * 2 + currentLine - 1) / 2;
                    System.arraycopy(blocks, index, line, 0, radius + currentLine);
                } else {
                    // This is equals to the second loop in eliminateI
                    int i = maxLines - currentLine - 1;
                    line = new Block[radius + i];
                    index = constTerm + (radius - i - 2) * (radius * 3 - 1 + i) / 2;
                    System.arraycopy(blocks, index, line, 0, line.length);
                }
                currentLine++;
                return line;
            }
        };
    }
    /**
     * Returns an iterable over lines of blocks along the J axis.
     * Each iteration returns an array of {@link Block}s representing one line.
     * The lines are returned in order from the topmost line (smallest J) to the bottommost line (largest J).
     * <p>
     * This iterable is useful for operations that need to process or analyze blocks line by line along the J axis,
     * such as rendering, line elimination, or game logic that depends on row-based interactions.
     * <p>
     * The number of lines returned is {@code radius * 2 - 1}, with each line containing a varying number of blocks
     * depending on its position in the hexagonal grid.
     * @return an iterable over lines of blocks along the J axis
     * @since 2.0
     * @see #iteratorJ()
     * @see #eliminateJ
     * @see #countEliminateJ
     */
    public Iterable<Block[]> lineIterableJ() {
        return this::iteratorJ;
    }
    /**
     * Return an iterator that iterates over lines of blocks along the J axis.
     * Each call to {@code next()} returns an array of {@link Block}s representing one line.
     * The lines are returned in order from the topmost line (smallest J) to the bottommost line (largest J).
     * <p>
     * This iterator is useful for operations that need to process or analyze blocks line by line along the J axis,
     * such as rendering, line elimination, or game logic that depends on row-based interactions.
     * <p>
     * The number of lines returned is {@code radius * 2 - 1}, with each line containing a varying number of blocks
     * depending on its position in the hexagonal grid.
     * @return an iterator over lines of blocks along the J axis
     * @since 2.0
     * @see #eliminateJ
     * @see #countEliminateJ
     */
    public Iterator<Block[]> iteratorJ() {
        return new Iterator<Block[]>() {
            int currentLine = 1 - radius;
            @Override
            public boolean hasNext() {
                return currentLine < radius;
            }
            @Override
            public Block[] next() {
                if (currentLine >= radius) {
                    throw new IndexOutOfBoundsException("No more J lines");
                }
                Block[] line;
                int index = 0; int idx = 0;
                if (currentLine > 0) {
                    // This is equals to the first loop in eliminateJ
                    line = new Block[radius * 2 - currentLine - 1];
                    index = currentLine;
                    for (int c = 1; c < radius; c++)
                    {
                        line[idx++] = blocks[index];
                        index += radius + c;
                    }
                    for (int c = 0; c < radius - currentLine; c++)
                    {
                        line[idx++] = blocks[index];
                        index += 2 * radius - c - 1;
                    }
                } else {
                    // This is equals to the second loop in eliminateJ
                    int r = -currentLine;
                    index = radius * r + r * (r - 1) / 2;
                    line = new Block[radius * 2 + currentLine - 1];
                    for (int c = 1; c < radius - r; c++){
                        line[idx++] = blocks[index];
                        index += radius + c + r;
                    }
                    for (int c = 0; c < radius; c++){
                        line[idx++] = blocks[index];
                        index += 2 * radius - c - 1;
                    }
                }
                currentLine++;
                return line;
            }
        };
    }
    /**
     * Returns an iterable over lines of blocks along the K axis.
     * Each iteration returns an array of {@link Block}s representing one line.
     * The lines are returned in order from the topmost line (smallest K) to the bottommost line (largest K).
     * <p>
     * This iterable is useful for operations that need to process or analyze blocks line by line along the K axis,
     * such as rendering, line elimination, or game logic that depends on row-based interactions.
     * <p>
     * The number of lines returned is {@code radius * 2 - 1}, with each line containing a varying number of blocks
     * depending on its position in the hexagonal grid.
     * @return an iterable over lines of blocks along the K axis
     * @since 2.0
     * @see #iteratorK()
     * @see #eliminateK
     * @see #countEliminateK
     */
    public Iterable<Block[]> lineIterableK() {
        return this::iteratorK;
    }
    /**
     * Return an iterator that iterates over lines of blocks along the K axis.
     * Each call to {@code next()} returns an array of {@link Block}s representing one line.
     * The lines are returned in order from the topmost line (smallest K) to the bottommost line (largest K).
     * <p>
     * This iterator is useful for operations that need to process or analyze blocks line by line along the K axis,
     * such as rendering, line elimination, or game logic that depends on row-based interactions.
     * <p>
     * The number of lines returned is {@code radius * 2 - 1}, with each line containing a varying number of blocks
     * depending on its position in the hexagonal grid.
     * @return an iterator over lines of blocks along the K axis
     * @since 2.0
     * @see #eliminateK
     * @see #countEliminateK
     */
    public Iterator<Block[]> iteratorK() {
        return new Iterator<Block[]>() {
            int currentLine = 0;
            final int maxLines = radius * 2 - 1;
            @Override
            public boolean hasNext() {
                return currentLine < maxLines;
            }

            @Override
            public Block[] next() {
                if (currentLine >= maxLines) {
                    throw new IndexOutOfBoundsException("No more K lines");
                }
                Block[] line;
                int index = 0;
                int idx = 0;
                if (currentLine < radius) {
                    // This is equals to the first loop in eliminateK
                    line = new Block[radius + currentLine];
                    index = currentLine;
                    for (int c = 0; c < radius - 1; c++){
                        line[idx++] = blocks[index];
                        index += radius + c;
                    }
                    for (int c = 0; c <= currentLine; c++){
                        line[idx++] = blocks[index];
                        index += 2 * radius - c - 2;
                    }
                } else {
                    // This is equals to the second loop in eliminateK
                    int r = currentLine - radius + 2;
                    index = radius * r + (r - 1) * r / 2 - 1;
                    line = new Block[radius * 2 - r];
                    for (int c = r; c < radius; c++) {
                        line[idx++] = blocks[index];
                        index += radius + c - 1;
                    }
                    for (int c = radius - 1; c >= 0; c--) {
                        line[idx++] = blocks[index];
                        index += radius + c - 1;
                    }
                }
                currentLine++;
                return line;
            }
        };
    }

    /**
     * Returns a predicate that tests whether the {@code other} grid can be added to this grid
     * at a given origin without overlap or out-of-bounds errors.
     *
     * @return a predicate that returns true if placement is valid
     * @see #checkAdd
     * @see #add
     * @since 2.0
     */
    public BiPredicate<Hex, HexGrid> canAddPredicate(){
        return this::checkAdd;
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
     * Returns a predicate that tests whether there is any valid position where {@code other} grid can be added.
     * This predicate can be used to quickly determine if addition is possible without needing to find all positions.
     *
     * @return a predicate that returns true if there is at least one valid position for addition
     * @see #checkPositions
     * @see #checkAdd
     * @since 2.0
     */
    public Predicate<HexGrid> anyAddablePredicate(){
        return other -> !checkPositions(other).isEmpty();
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
     * <p>
     * Refactored with new algorithm since version 1.3.4 to reduce time complexity from O(radius^3)
     * to O(radius^2), significantly reducing cost.
     * @return blocks eliminated
     */
    public Block[] eliminate(){
        // Eliminate according to I, J, K, then return how many blocks are being eliminated
        ArrayList<Block> eliminate = new ArrayList<Block>();
        eliminateI(eliminate); eliminateJ(eliminate); eliminateK(eliminate);
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
     * Identify blocks along I axis that can be eliminated and insert them into the input {@link ArrayList}.
     * <p>
     * For checking the possibility of eliminating a piece, use the {@link #checkEliminateI()} method instead.
     * @param eliminate the input ArrayList for insertion of elimination {@link Block} candidates.
     * @since 1.3.4
     */
    public void eliminateI(ArrayList<Block> eliminate){
        for (int i = 0; i < radius; i++){
            boolean allValid = true;
            int index = i * (radius * 2 + i - 1) / 2;
            for (int b = 0; b < radius + i; b++){
                if (!blocks[index + b].getState()){
                    allValid = false; break;
                }
            }
            if (allValid) {
                eliminate.ensureCapacity(eliminate.size() + radius + i);
                for (int b = 0; b < radius + i; b++) {
                    eliminate.add(blocks[index + b]);
                }
            }
        }
        int constTerm = radius * (radius * 3 - 1) / 2;
        for (int i = radius - 2; i >= 0; i--){
            boolean allValid = true;
            int index = constTerm + (radius - i - 2) * (radius * 3 - 1 + i) / 2;
            for (int b = 0; b < radius + i; b++){
                if (!blocks[index + b].getState()){
                    allValid = false; break;
                }
            }
            if (allValid) {
                eliminate.ensureCapacity(eliminate.size() + radius + i);
                for (int b = 0; b < radius + i; b++) {
                    eliminate.add(blocks[index + b]);
                }
            }
        }
    }
    /**
     * Identify blocks along J axis that can be eliminated and insert them into the input {@link ArrayList}.
     * <p>
     * For checking the possibility of eliminating a piece, use the {@link #checkEliminateJ()} method instead.
     * @param eliminate the input ArrayList for insertion of elimination {@link Block} candidates.
     * @since 1.3.4
     */
    public void eliminateJ(ArrayList<Block> eliminate){
        for (int r = 0; r < radius; r++){
            int index = r;
            boolean allValid = true;
            for (int c = 1; c < radius; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = 0; c < radius - r; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 1;
            }
            if (allValid){
                index = r;
                for (int c = 1; c < radius; c++)
                {
                    eliminate.add(blocks[index]);
                    index += radius + c;
                }
                for (int c = 0; c < radius - r; c++)
                {
                    eliminate.add(blocks[index]);
                    index += 2 * radius - c - 1;
                }
            }
        }
        for (int r = 1; r < radius; r++){
            int index = radius * r + r * (r - 1) / 2;
            int startIndex = index;
            boolean allValid = true;
            for (int c = 1; c < radius - r; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c + r;
            }
            for (int c = 0; c < radius; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 1;
            }
            if (allValid){
                for (int c = 1; c < radius - r; c++){
                    eliminate.add(blocks[startIndex]);
                    startIndex += radius + c + r;
                }
                for (int c = 0; c < radius; c++){
                    eliminate.add(blocks[startIndex]);
                    startIndex += 2 * radius - c - 1;
                }
            }
        }
    }
    /**
     * Identify blocks along K axis that can be eliminated and insert them into the input {@link ArrayList}.
     * <p>
     * For checking the possibility of eliminating a piece, use the {@link #checkEliminateK()} method instead.
     * @param eliminate the input ArrayList for insertion of elimination {@link Block} candidates.
     * @since 1.3.4
     */
    public void eliminateK(ArrayList<Block> eliminate){
        for (int r = 0; r < radius; r++){
            int index = r;
            boolean allValid = true;
            for (int c = 0; c < radius - 1; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = 0; c <= r; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 2;
            }
            if (allValid) {
                index = r;
                for (int c = 0; c < radius - 1; c++){
                    eliminate.add(blocks[index]);
                    index += radius + c;
                }
                for (int c = 0; c <= r; c++){
                    eliminate.add(blocks[index]);
                    index += 2 * radius - c - 2;
                }
            }
        }
        for (int r = 1; r < radius; r++){
            int index = radius * (r + 1) + r * (r + 1) / 2 - 1;
            int startIndex = index;
            boolean allValid = true;
            for (int c = r; c < radius - 1; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = radius - 1; c >= 0; c--)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c - 1;
            }
            if (allValid) {
                for (int c = r; c < radius - 1; c++)
                {
                    eliminate.add(blocks[startIndex]);
                    startIndex += radius + c;
                }
                for (int c = radius - 1; c >= 0; c--)
                {
                    eliminate.add(blocks[startIndex]);
                    startIndex += radius + c - 1;
                }
            }
        }
    }
    /**
     * Counts the number of lines or blocks that can be eliminated along I, J, or K axes.
     *
     * @param countLine if true, count the number of lines; if false, count the number of blocks
     * @return number of lines or blocks that can be eliminated
     * @see #countEliminateI
     * @see #countEliminateJ
     * @see #countEliminateK
     * @see #eliminate
     * @since 2.0
     */
    public int countEliminate(boolean countLine){
        return countEliminateI(countLine) + countEliminateJ(countLine) + countEliminateK(countLine);
    }
    /**
     * Counts the number of lines or blocks that can be eliminated along I axis.
     *
     * @param countLine if true, count the number of lines; if false, count the number of blocks
     * @return number of lines or blocks that can be eliminated along I axis
     * @see #countEliminate
     * @since 2.0
     */
    public int countEliminateI(boolean countLine){
        int counter = 0;
        for (int i = 0; i < radius; i++){
            boolean allValid = true;
            int index = i * (radius * 2 + i - 1) / 2;
            for (int b = 0; b < radius + i; b++){
                if (!blocks[index + b].getState()){
                    allValid = false; break;
                }
            }
            if (allValid) {
                if (countLine) {
                    counter++;
                } else {
                    counter += radius + i;
                }
            }
        }
        int constTerm = radius * (radius * 3 - 1) / 2;
        for (int i = radius - 2; i >= 0; i--){
            boolean allValid = true;
            int index = constTerm + (radius - i - 2) * (radius * 3 - 1 + i) / 2;
            for (int b = 0; b < radius + i; b++){
                if (!blocks[index + b].getState()){
                    allValid = false; break;
                }
            }
            if (allValid) {
                if (countLine) {
                    counter++;
                } else {
                    counter += radius + i;
                }
            }
        }
        return counter;
    }
    /**
     * Counts the number of lines or blocks that can be eliminated along J axis.
     *
     * @param countLine if true, count the number of lines; if false, count the number of blocks
     * @return number of lines or blocks that can be eliminated along J axis
     * @see #countEliminate
     * @since 2.0
     */
    public int countEliminateJ(boolean countLine){
        int counter = 0;
        for (int r = 0; r < radius; r++){
            int index = r;
            boolean allValid = true;
            for (int c = 1; c < radius; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = 0; c < radius - r; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 1;
            }
            if (allValid){
                if (countLine) {
                    counter++;
                } else {
                    counter += radius * 2 - r - 1;
                }
            }
        }
        for (int r = 1; r < radius; r++){
            int index = radius * r + r * (r - 1) / 2;
            int startIndex = index;
            boolean allValid = true;
            for (int c = 1; c < radius - r; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c + r;
            }
            for (int c = 0; c < radius; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 1;
            }
            if (allValid){
                if (countLine) {
                    counter++;
                } else {
                    counter += radius * 2 - r - 1;
                }
            }
        }
        return counter;
    }
    /**
     * Counts the number of lines or blocks that can be eliminated along K axis.
     *
     * @param countLine if true, count the number of lines; if false, count the number of blocks
     * @return number of lines or blocks that can be eliminated along K axis
     * @see #countEliminate
     * @since 2.0
     */
    public int countEliminateK(boolean countLine){
        int counter = 0;
        for (int r = 0; r < radius; r++){
            int index = r;
            boolean allValid = true;
            for (int c = 0; c < radius - 1; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = 0; c <= r; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 2;
            }
            if (allValid) {
                if (countLine) {
                    counter++;
                } else {
                    counter += radius + r;
                }
            }
        }
        for (int r = 1; r < radius; r++){
            int index = radius * (r + 1) + r * (r + 1) / 2 - 1;
            int startIndex = index;
            boolean allValid = true;
            for (int c = r; c < radius - 1; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = radius - 1; c >= 0; c--)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c - 1;
            }
            if (allValid) {
                if (countLine) {
                    counter++;
                } else {
                    counter += radius * 2 - r - 1;
                }
            }
        }
        return counter;
    }
    /**
     * Returns a {@link BooleanSupplier} that checks if any lines can be eliminated.
     * This is useful for passing as a callback or condition in other parts of the program.
     * @return a BooleanSupplier that returns true if any lines can be eliminated
     * @see #checkEliminate()
     * @since 2.0
     */
    public BooleanSupplier eliminateSupplier(){
        return this::checkEliminate;
    }
    /**
     * Checks whether any full line can be eliminated in the hex grid.
     * <p>
     * Refactored with new algorithm since version 1.3.4 to reduce time complexity from O(radius^3)
     * to O(radius^2), significantly reducing cost.
     * @return true if at least one line is full and able to be eliminated.
     * @see #checkEliminateI()
     * @see #checkEliminateJ()
     * @see #checkEliminateK()
     */
    public boolean checkEliminate(){
        return checkEliminateI() || checkEliminateJ() || checkEliminateK();
    }
    /**
     * Checks if any lines of in the direction I can be eliminated.
     * @return true if any lines are filled and part of a valid piece
     * @see Block#getLineI()
     * @see #checkEliminate()
     * @since 1.3.4
     */
    public boolean checkEliminateI(){
        for (int i = 0; i < radius; i++){
            int index = i * (radius * 2 + i - 1) / 2;
            boolean allValid = true;
            for (int b = 0; b < radius + i; b++){
                if (!blocks[index + b].getState()){
                    allValid = false; break;
                }
            }
            if (allValid) return true;
        }
        int constTerm = radius * (radius * 3 - 1) / 2;
        for (int i = radius - 2; i >= 0; i--){
            boolean allValid = true;
            int index = constTerm + (radius - i - 2) * (radius * 3 - 1 + i) / 2;
            for (int b = 0; b < radius + i; b++){
                if (!blocks[index + b].getState()){
                    allValid = false; break;
                }
            }
            if (allValid) return true;
        }
        return false;
    }
    /**
     * Checks if any lines of in the direction J can be eliminated.
     * @return true if any lines are filled and part of a valid piece
     * @see Block#getLineJ()
     * @see #checkEliminate()
     * @since 1.3.4
     */
    public boolean checkEliminateJ(){
        for (int r = 0; r < radius; r++){
            int index = r;
            boolean allValid = true;
            for (int c = 1; c < radius; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = 0; c < radius - r; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 1;
            }
            if (allValid) return true;
        }
        for (int r = 1; r < radius; r++){
            int index = radius * r + r * (r - 1) / 2;
            boolean allValid = true;
            for (int c = 1; c < radius - r; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c + r;
            }
            for (int c = 0; c < radius; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 1;
            }
            if (allValid) return true;
        }
        return false;
    }
    /**
     * Checks if any lines of in the direction K can be eliminated.
     * @return true if any lines are filled and part of a valid piece
     * @see Block#getLineK()
     * @see #checkEliminate()
     * @since 1.3.4
     */
    public boolean checkEliminateK(){
        for (int r = 0; r < radius; r++){
            int index = r;
            boolean allValid = true;
            for (int c = 0; c < radius - 1; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = 0; c <= r; c++){
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += 2 * radius - c - 2;
            }
            if (allValid) return true;
        }
        for (int r = 1; r < radius; r++){
            int index = radius * (r + 1) + r * (r + 1) / 2 - 1;
            boolean allValid = true;
            for (int c = r; c < radius - 1; c++)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c;
            }
            for (int c = radius - 1; c >= 0; c--)
            {
                if (!blocks[index].getState()){
                    allValid = false; break;
                }
                index += radius + c - 1;
            }
            if (allValid) return true;
        }
        return false;
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
     * Determine the pattern of the {@link Block}s around the given {@link Hex position} in the hexagonal grid,
     * include the block itself. This calculation ignores color of the blocks.
     * <p>
     * This method checks up to seven positions in a box bounded around the block located at coordinates (i, k).
     * This method adheres to the standard of a standard 7-{@link Block} box, returning values representing the pattern
     * inside the box, taking into account the {@link Block#getState() state} of every block in the range. The returning
     * value will between 0 and 127, inclusive.
     * <p>
     * If a neighboring position is out of range or contains a {@code null} block, it is either counted as occupied
     * or unoccupied based on the {@code includeNull} flag.
     *
     * @param i the I-line coordinate of the block in the center of the box to check for pattern.
     * @param k the K-line coordinate of the block in the center of the box to check for pattern.
     * @param includeNull whether to treat {@code null} or out-of-bounds neighbors as occupied ({@code true}) or unoccupied ({@code false}).
     * @return a number represent the pattern seen in the box around the {@code Block}. This value is in the range [0, 127].
     * @since 1.3
     */
    private int getPattern(int i, int k, boolean includeNull){
        int pattern = 0;
        if (inRange(i - 1, k - 1)){
            if (getBlock(i - 1, k - 1).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        pattern <<= 1;
        if (inRange(i - 1, k)){
            if (getBlock(i - 1, k).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        pattern <<= 1;
        if (inRange(i, k - 1)){
            if (getBlock(i, k - 1).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        pattern <<= 1;
        if (inRange(i, k )){
            if (getBlock(i, k ).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        pattern <<= 1;
        if (inRange(i, k + 1)){
            if (getBlock(i, k + 1).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        pattern <<= 1;
        if (inRange(i + 1, k)){
            if (getBlock(i + 1, k).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        pattern <<= 1;
        if (inRange(i + 1, k + 1)){
            if (getBlock(i + 1, k + 1).getState()) pattern ++;
        } else if (includeNull) pattern ++;
        return pattern;
    }
    /**
     * Returns a {@link Supplier} that provides the pattern of the specified {@link Hex} position in the grid.
     *
     * @param hex the {@link Hex} position to get the pattern for
     * @return a Supplier that returns the pattern of the specified {@link Hex} position
     * @see #getPattern(int, int, boolean)
     * @since 2.0
     */
    public Supplier<Integer> patternSupplier(Hex hex){
        return () -> getPattern(hex.getLineI(), hex.getLineK(), false);
    }
    /**
     * Computes the entropy of the hexagonal grid based on the distribution of 7-block patterns.
     * <p>
     * Entropy is calculated using the Shannon entropy formula, measuring the randomness of block arrangements
     * in the grid. Each pattern consists of a central {@link Block} and its six neighboring blocks, forming
     * a 7-block hexagonal box, as defined by the {@link #getPattern(int, int, boolean)} method. The entropy
     * reflects the diversity of these patterns, such that a grid with randomly distributed filled and empty
     * blocks has higher entropy than one with structured patterns (e.g., all blocks in a line or cluster).
     * A grid with all blocks filled or all empty has zero entropy. Inverting the grid (swapping filled and
     * empty states) results in the same entropy, as the pattern distribution remains unchanged.
     * <p>
     * The method iterates over all blocks within the grid's radius (excluding the outermost layer to ensure
     * all neighbors are in range), counts the frequency of each possible 7-block pattern (2^7 = 128 patterns),
     * and computes the entropy according to the
     * <a href="https://en.wikipedia.org/wiki/Entropy_(information_theory)">Shannon entropy formula</a> as:
     * <pre>
     * H = -Σ (p * log₂(p))
     * </pre>
     * where {@code p} is the probability of each pattern (frequency divided by total patterns counted).
     * Blocks on the grid's boundary (beyond {@code radius - 1}) are excluded to avoid incomplete patterns.
     *
     * @return the entropy of the grid in bits, a non-negative value representing the randomness of block
     *         arrangements. Returns 0.0 for a uniform grid (all filled or all empty) or if no valid patterns
     *         are counted.
     * @since 1.3
     * @see #getPattern(int, int, boolean)
     */
    public double computeEntropy(){
        double entropy = 0.0;
        int patternTotal = 0;
        int [] patternCounts = new int[128]; // 2^7 because there are 128 available positions
        for (Block block : blocks) {
            if (block.shiftJ(1).inRange(getRadius() - 1)) {
                // If it is possible to check patterns without going out of bounds
                patternTotal++;
                patternCounts[getPattern(block.getLineI(), block.getLineK(), false)]++;
            }
        }
        for (int count : patternCounts) {
            if (count > 0) {
                double p = (double) count / patternTotal;
                entropy -= p * HexEngine.log2(p); // log base 2
            }
        }
        return entropy;
    }
    /**
     * Computes an entropy-based index score for hypothetically placing another {@link HexGrid} onto this grid.
     * <p>
     * The entropy index is a value between 0 and 1 that measures the change in the grid's entropy when the
     * specified {@code other} grid is placed at the given {@code origin} coordinate. This index is derived
     * by computing the difference in Shannon entropy (as calculated by {@link #computeEntropy()}) between
     * the current grid and a hypothetical grid state after adding {@code other} and performing an elimination
     * step. The entropy difference is adjusted by a constant offset (0.21) and transformed using a sigmoid
     * function to map the result to the range [0, 1]. The sigmoid function used is:
     * <pre>{@code f(x) = 1 / (1 + e^(-3 * (x - 0.2)))}</pre>
     * where {@code x} is the adjusted entropy difference. A higher index indicates a placement that results
     * in a grid configuration with significantly different pattern diversity, which may be useful in
     * reinforcement learning or game strategy evaluation to prioritize moves that decrease or maintain
     * randomness in block arrangements.
     * <p>
     * The method operates on a cloned {@link HexEngine} to avoid modifying the current grid state. It adds
     * the {@code other} grid at the specified {@code origin}, performs the elimination, and calculates the
     * entropy difference between the two engines. This method assumes the placement is valid or an exception
     * may be thrown.
     *
     * @param origin the position in the grid where the {@code other} grid is hypothetically placed
     * @param other the {@link HexGrid} representing the piece to be evaluated for placement
     * @return a value between 0 and 1 representing the entropy-based index score, where higher values indicate
     *         a greater increase in pattern entropy after piece placement and elimination
     * @throws IllegalArgumentException if the piece placement is invalid
     * @since 1.3
     * @see #computeEntropy()
     * @see #clone()
     * @see #add(Hex, HexGrid)
     */
    public double computeEntropyIndex(Hex origin, HexGrid other) throws IllegalArgumentException{
        // Test move
        HexEngine copy = clone();
        copy.add(origin, other);
        copy.eliminate();
        double x = copy.computeEntropy() - computeEntropy() - 0.21;
        // Sigmoid: 1/(1+e^-k(x-c)) k use 3 c use 0.2
        return 1 / (1 + Math.exp(-3 * x));
    }

    /**
     * Returns a string representation of the grid color and block states.
     * @return string showing block positions and states
     * @see Block#toString()
     */
    public String toString(){
        StringBuilder str = new StringBuilder("HexEngine[blocks = {");
        if (blocks.length > 0){
            str.append(blocks[0].toBasicString());
        }
        for (int i = 1; i < blocks.length; i++) {
            Block block = blocks[i];
            str.append(", ");
            str.append(block.toBasicString());
        }
        return str + "}]";
    }
    /**
     * Returns a boolean array representation of the blocks in this {@code HexEngine}.
     * <p>
     * Empty blocks will be represented with false and filled blocks will be represented with true.
     * This method does not record color.
     * @return a boolean array representing this engine.
     * @since 1.3.3
     */
    public boolean[] toBooleans(){
        int length = blocks.length;
        boolean[] booleans = new boolean[length];
        for (int i = 0; i < length; i ++){
            if (getBlock(i).getState()) booleans[i] = true;
        }
        return booleans;
    }
    /**
     * Solve for the radius of a {@link HexEngine} based on its {@link #length}.
     * @param length the length of the {@code HexEngine} object, must be positive.
     * @return the solved radius of the engine to match the length given,
     *         -1 if the length does not match with any engine configuration or is invalid.
     * @since 1.3.3
     */
    public static int solveRadius(int length) {
        if (length <= 1) return -1;
        // y = 1 + 3 * x * (x - 1) => (y - 1) / 3 = x * (x - 1)
        if (length % 3 != 1) return -1;
        int target = (length - 1) / 3;
        for (int x = 1; x * (x - 1) <= target; x++) {
            if (x * (x - 1) == target) {
                return x;
            }
        }
        return -1;
    }
    /**
     * Construct a {@link HexEngine} piece from a boolean array data.
     * Throws {@link IllegalArgumentException} if the byte data does not match with any engine configuration.
     * Colors are set to default.
     * <p>
     * The boolean array data conversion is in accordance with the {@link #toBooleans()} method.
     * @param data the boolean array data used to create this {@code HexEngine}.
     * @throws IllegalArgumentException if the boolean array data does not represent any engine.
     * @return an engine constructed from the input data.
     * @since 1.3.3
     */
    public static HexEngine engineFromBooleans(boolean[] data) throws IllegalArgumentException{
        int length = data.length;
        int radius = solveRadius(length);
        if (radius == -1) throw new IllegalArgumentException("Data array is of invalid length");
        HexEngine engine = new HexEngine(radius);
        for (int i = 0; i < length; i ++){
            engine.setState(i, data[i]);
        }
        return engine;
    }

    /**
     * Returns an identical deep clone of this {@code HexEngine}.
     * Each {@link Block} object contained in this instance is cloned individually.
     * @return a deep copy of this {@code HexEngine} object.
     * @see Block#clone()
     */
    public HexEngine clone(){
        HexEngine newEngine;
        try{
            newEngine = (HexEngine) super.clone();
            newEngine.radius = this.radius;
        } catch (CloneNotSupportedException e) {
            newEngine = new HexEngine(this.radius);
        }
        for(int i = 0; i < this.length(); i ++){
            newEngine.blocks[i] = this.blocks[i].clone();
        }
        return newEngine;
    }
    /**
     * Compares this {@code HexEngine} to another object for equality.
     * Two {@code HexEngine} objects are considered equal if they have the same radius
     * and all corresponding {@link Block} objects are equal in state and color.
     * @param obj the object to compare with this {@code HexEngine}.
     * @return true if the specified object is a {@code HexEngine} with the same radius and identical blocks; false otherwise.
     * @since 2.0
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof HexEngine other)) return false;
        if (this.radius != other.radius) return false;
        for (int i = 0; i < this.length(); i ++){
            if (!this.blocks[i].equals(other.blocks[i])) return false;
        }
        return true;
    }
    /**
     * Compares this {@code HexEngine} to another object for equality, ignoring block colors.
     * Two {@code HexEngine} objects are considered equal if they have the same radius
     * and all corresponding {@link Block} objects are equal in state, regardless of color.
     * @param obj the object to compare with this {@code HexEngine}.
     * @return true if the specified object is a {@code HexEngine} with the same radius and identical block states; false otherwise.
     * @since 2.0
     */
    public boolean equalsIgnoreColor(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexEngine other)) return false;
        if (this.radius != other.radius) return false;
        for (int i = 0; i < this.length(); i++) {
            if (this.blocks[i].getState() != other.blocks[i].getState()) return false;
        }
        return true;
    }
}
