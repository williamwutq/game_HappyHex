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
 * The {@code Block} class extends {@link Hex} and represents a colored block with an occupancy state
 * within the hexagonal grid system.
 * <p>
 * Block inherits the coordinate system from {@link Hex}. See {@link Hex} for details on the hexagonal coordinate
 * system, including both raw and line-based coordinates.
 * <p>
 * In addition to the coordinate functionality provided by {@link Hex}, each {@code Block} instance encapsulates:
 * <ul>
 *   <li>A color index indicating the block's color. -1 represent is the typical empty color for the block,
 *       -2 is the default filled color for the block, 0-n represent the real colors generated.</li>
 *   <li>A boolean state representing whether the block is occupied (true) or unoccupied (false).</li>
 * </ul>
 * <p>
 * The class provides various constructors and static factory methods for creating blocks using either
 * standard (i, k) coordinates or line indices. It also includes methods for moving, shifting, adding, and
 * subtracting coordinates, as well as modifying and retrieving the block's state and color.
 *
 * @see Hex
 * @since 0.6
 * @author William Wu
 * @version 1.3
 */
public class Block extends Hex{
    private int color;
    private boolean state;

    // Basic constructors
    /**
     * Constructs a block at the specified (i, k) coordinates with an unoccupied state.
     *
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     * @since 1.3
     */
    public Block(int i, int k){
        // Complete constructor
        super(i, k);
        this.state = false;
        this.color = -1;
    }
    /**
     * Constructs a block at the specified (i, k) coordinates with a specified color and unoccupied state.
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     * @param color The color of the block.
     */
    public Block(int i, int k, int color){
        // Complete constructor
        super(i, k);
        this.state = false;
        this.color = color;
    }
    /**
     * Constructs a block at the specified (i, k) coordinates with a specified state.
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     * @param state The state of the block.
     * @since 1.3
     */
    public Block(int i, int k, boolean state){
        // Complete constructor
        super(i, k);
        if (state){
            this.color = -2;
        } else this.color = -1;
        this.state = state;
    }
    /**
     * Constructs a block at the specified (i, k) coordinates with a specified color and state.
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     * @param color The color of the block.
     * @param state The state of the block.
     */
    public Block(int i, int k, int color, boolean state){
        // Complete constructor
        super(i, k);
        this.color = color;
        this.state = state;
    }
    /**
     * Constructs a block at the specified hex coordinates with unoccupied color and state
     * @param hex the coordinate.
     * @since 1.3
     */
    public Block(Hex hex){
        // Complete constructor
        super();
        super.set(hex);
        this.state = false;
        this.color = -1;
    }
    /**
     * Constructs a block at the specified hex coordinates with a specified color and unoccupied state.
     * @param hex the coordinate.
     * @param color The color of the block.
     */
    public Block(Hex hex, int color){
        // Complete constructor
        super();
        super.set(hex);
        this.state = false;
        this.color = color;
    }
    /**
     * Constructs a block at the specified hex coordinates with a specified color and state.
     *
     * @param hex the coordinate.
     * @param color The color of the block.
     * @param state The state of the block.
     */
    public Block(Hex hex, int color, boolean state){
        // Complete constructor
        super();
        super.set(hex);
        this.color = color;
        this.state = state;
    }

    // Line constructors (static)
    // Please use those for the game instead of the old constructors
    /**
     * Creates a block using hexagonal line indices and assigns it a specific color.
     * The block is shifted accordingly in the coordinate system.
     *
     * @param i The I-line index in the hexagonal coordinate system.
     * @param k The K-line index in the hexagonal coordinate system.
     * @param color The color of the block.
     * @return A new block positioned according to the given line indices with the specified color.
     */
    public static Block block(int i, int k, int color){
        return new Block(0,0, color).shiftI(k).shiftK(i);
    }

    // Getters
    /**
     * Color of the block
     * @return The color of the block.
     */
    public int getColor(){
        return color;
    }
    /**
     * The state of the block, namely whether it is occupied
     * @return The state of the block (occupied = true).
     */
    public boolean getState(){
        return state;
    }

    /**
     * String representation of the block used for debugging. This use line coordinates.
     * <p>Format: {@code Block[color = c, coordinates = {i, j, k}, State = state]}</p>
     * @return A string representation of the block, including color, coordinates, and state.
     */
    public String toString(){
        return "Block[color = " + color + ", coordinates = {" + getLineI() + ", " + getLineJ() + ", " + getLineK() +
                "}, state = " + state + "]";
    }
    /**
     * String representation of the block used for debugging with less information. This use line coordinates.
     * <p>Format: {@code {i, j, k, state}}</p>
     * @return A string representation of the block, including only coordinates and state.
     */
    public String toBasicString(){
        return "{" + getLineI() + ", " + getLineJ() + ", " + getLineK() + ", " + state + "}";
    }
    /**
     * {@inheritDoc}
     * In addition, it also copies the state and color of this {@code Block}.
     * @return a clone of the {@code Block}.
     * @since 1.1
     */
    public Block clone(){
        Block block;
        try{
            block = (Block) super.clone();
            block.setColor(this.color);
        } catch (CloneNotSupportedException e) {
            block = new Block(this.thisHex(), this.color);
        }
        block.state = this.state;
        return block;
    }

    // Setters
    /**
     * Sets the color index of the block.
     *
     * @param color The new color index of the block.
     */
    public void setColor(int color){
        this.color = color;
    }
    /**
     * Sets the state of the block.
     *
     * @param state The new state of the block (true = occupied).
     */
    public void setState(boolean state){
        this.state = state;
    }
    /**
     * Toggles the state of the block.
     */
    public void changeState(){
        this.state = !this.state;
    }

    /**
     * Creates a new hex coordinate shifted along the I-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the I-axis.
     */
    public Block shiftI(int unit){
        Block temp = this.clone();
        temp.moveI(unit);
        return temp;
    }
    /**
     * Creates a new hex coordinate shifted along the J-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the J-axis.
     */
    public Block shiftJ(int unit){
        Block temp = this.clone();
        temp.moveJ(unit);
        return temp;
    }
    /**
     * Creates a new hex coordinate shifted along the K-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the K-axis.
     */
    public Block shiftK(int unit){
        Block temp = this.clone();
        temp.moveK(unit);
        return temp;
    }

    // Add and subtract
    /**
     * Adds another hex to this hex coordinate and returns a new hex coordinate.
     *
     * @param other The hex coordinate to add.
     * @return A new hex coordinate with the summed coordinates.
     */
    public Block add(Hex other){
        return new Block(thisHex().add(other), this.color, this.state);
    }
    /**
     * Subtracts another hex coordinate from this hex coordinate and returns a new hex coordinate.
     *
     * @param other The hex coordinate to subtract.
     * @return A new hex coordinate with the subtracted coordinates.
     */
    public Block subtract(Hex other){
        return new Block(thisHex().subtract(other), this.color, this.state);
    }
}
