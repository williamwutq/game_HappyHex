package Hex;

import java.awt.*;
/*
 Coordinate system: (2i, 2j, 2k)
    i
   + * (5, 4, -1)
  +     * (5, 7, 2)
 + + + j
  + * (0, 3, 3)
   +
    k
 */


class Block{
    private final double sinOf60 = Math.sqrt(3) / 2;
    private Color color;
    private boolean state;
    private int x;
    private int y;

    // Basic constructors
    /**
     * Default constructor initializing the block at (0,0) with black color and unoccupied state.
     */
    public Block(){
        // Basic constructor
        this.x = 0;
        this.y = 0;
        this.state = false;
        this.color = Color.BLACK;
    }
    /**
     * Constructs a block at the specified (i, k) coordinates and unoccupied state.
     *
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     */
    public Block(int i, int k){
        // Coordinate constructor
        this.x = i;
        this.y = k;
        this.state = false;
        this.color = Color.BLACK;
    }
    /**
     * Constructs a block at the specified (i, k) coordinates with a specified color and unoccupied state.
     *
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     * @param color The color of the block.
     */
    public Block(int i, int k, Color color){
        // Complete constructor
        this.x = i;
        this.y = k;
        this.state = false;
        this.color = color;
    }

    // Line constructors (static)
    // Please use those for the game instead of the old constructors
    /**
     * Creates a default block at (0,0) with black color and unoccupied state.
     *
     * @return A new block instance at the origin.
     */
    public static Block block(){
        return new Block();
    }
    /**
     * Creates a block using hexagonal line indices instead of direct coordinates.
     * The block is shifted accordingly in the coordinate system.
     *
     * @param i The I-line index in the hexagonal coordinate system.
     * @param k The K-line index in the hexagonal coordinate system.
     * @return A new block positioned according to the given line indices.
     */
    public static Block block(int i, int k){
        return new Block().shiftI(i).shiftK(k);
    }
    /**
     * Creates a block using hexagonal line indices and assigns it a specific color.
     * The block is shifted accordingly in the coordinate system.
     *
     * @param i The I-line index in the hexagonal coordinate system.
     * @param k The K-line index in the hexagonal coordinate system.
     * @param color The color of the block.
     * @return A new block positioned according to the given line indices with the specified color.
     */
    public static Block block(int i, int k, Color color){
        return new Block(0,0, color).shiftI(i).shiftK(k);
    }

    // Getters
    /**
     * Color of the block
     * @return The color of the block.
     */
    public Color color(){
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
     * Raw I-coordinate
     * @return The I-coordinate of the block in the hexagonal grid.
     */
    public int I(){
        return x;
    }
    /**
     * Raw J-coordinate
     * @return The J-coordinate of the block in the hexagonal grid.
     */
    public int J(){
        return x + y;
    }
    /**
     * Raw K-coordinate
     * @return The K-coordinate of the block in the hexagonal grid.
     */
    public int K(){
        return y;
    }

    // Lines
    /**
     * Computes the line index along the I-axis in the hexagonal coordinate system.
     * The I-axis represents a diagonal axis in the hexagonal coordinate system.
     *
     * @return The computed I-line index of the block.
     * @see #getLines()
     */
    public int getLineI(){
        return (2*y+x)/3;
    }
    /**
     * Computes the line index along the J-axis in the hexagonal coordinate system.
     * The J-axis represents a diagonal axis in the hexagonal coordinate system.
     *
     * @return The computed J-line index of the block.
     * @see #getLines()
     */
    public int getLineJ(){
        return (x-y)/3;
    }
    /**
     * Computes the line index along the K-axis in the hexagonal coordinate system.
     * The K-axis represents a diagonal axis in the hexagonal coordinate system.
     *
     * @return The computed K-line index of the block.
     * @see #getLines()
     */
    public int getLineK(){
        return (2*x+y)/3;
    }
    /**
     * String representation of the line indices of the block along all axes
     * <p>Format: {@code {I = i, J = j, K = k}}</p>
     *
     * @return A formatted string representing the block's line indices along I, J, and K axes.
     * @see #getLineI()
     * @see #getLineJ()
     * @see #getLineK()
     */
    public String getLines(){
        return "{I = " + getLineI() + ", J = " + getLineJ() + ", K = " + getLineK() + "}";
    }

    // Line booleans
    /**
     * Determines whether this block is in the given I-line.
     * The I-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param line The I-line value to check.
     * @return True if the block is in the specified I-line, otherwise false.
     * @see #inLineI(Block)
     */
    public boolean inLineI(int line){
        return getLineI() == line;
    }
    /**
     * Determines whether this block is in the given J-line.
     * The J-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param line The J-line value to check.
     * @return True if the block is in the specified J-line, otherwise false.
     * @see #inLineJ(Block)
     */
    public boolean inLineJ(int line){
        return getLineJ() == line;
    }
    /**
     * Determines whether this block is in the given K-line.
     * The K-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param line The K-line value to check.
     * @return True if the block is in the specified K-line, otherwise false.
     * @see #inLineK(Block)
     */
    public boolean inLineK(int line){
        return getLineK() == line;
    }
    /**
     * Determines whether this block is in the same I-line as the other block.
     * The I-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param other The other block to compare with.
     * @return True if this block is in the same I-line as the other block.
     * @see #inLineI(int)
     */
    public boolean inLineI(Block other){
        return this.getLineI() == other.getLineI();
    }
    /**
     * Determines whether this block is in the same J-line as the other block.
     * The J-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param other The other block to compare with.
     * @return True if this block is in the same K-line as the other block.
     * @see #inLineJ(int)
     */
    public boolean inLineJ(Block other){
        return this.getLineJ() == other.getLineJ();
    }
    /**
     * Determines whether this block is in the same K-line as the other block.
     * The K-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param other The other block to compare with.
     * @return True if this block is in the same K-line as the other block.
     * @see #inLineK(int)
     */
    public boolean inLineK(Block other){
        return this.getLineK() == other.getLineK();
    }
    /**
     * Determines whether this block is adjacent to another block.
     * Two blocks are considered adjacent if they share an edge in the hexagonal grid.
     * Use front and back for more precise finding.
     *
     * @param other The other block to compare with.
     * @return True if the blocks are adjacent, otherwise false.
     * @see #front(Block)
     * @see #back(Block)
     */
    public boolean adjacent(Block other){
        return front(other) || back(other);
    }
    /**
     * Determines if this block is in front of another block.
     * A block is considered "in the front" if it is positioned one step higher in any of the three coordinate axes.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit higher in I, J, or K, otherwise false.
     * @see #adjacent(Block)
     * @see #frontI(Block)
     * @see #frontJ(Block)
     * @see #frontK(Block)
     */
    public boolean front(Block other){
        // adjacent, this is one higher in I, J, or K
        return frontI(other) || frontJ(other) || frontK(other);
    }
    /**
     * Determines if this block is behind another block.
     * A block is considered "behind" if it is positioned one step lower in any of the three coordinate axes.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit lower in I, J, or K, otherwise false.
     * @see #adjacent(Block)
     * @see #backI(Block)
     * @see #backJ(Block)
     * @see #backK(Block)
     */
    public boolean back(Block other){
        // adjacent, this is one lower in I, J, or K
        return backI(other) || backJ(other) || backK(other);
    }
    /**
     * Determines if this block is in front of another block on the I-axis.
     * A block is considered "in the front" if it is positioned one step higher.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit higher on the I-axis.
     * @see #front(Block)
     */
    public boolean frontI(Block other){
        return this.x == other.x + 2 && this.y == other.y - 1;
    }
    /**
     * Determines if this block is in front of another block on the J-axis.
     * A block is considered "in the front" if it is positioned one step higher.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit higher on the J-axis.
     * @see #front(Block)
     */
    public boolean frontJ(Block other){
        return this.x == other.x + 1 && this.y == other.y + 1;
    }
    /**
     * Determines if this block is in front of another block on the K-axis.
     * A block is considered "in the front" if it is positioned one step higher.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit higher on the K-axis.
     * @see #front(Block)
     */
    public boolean frontK(Block other){
        return this.x == other.x - 1 && this.y == other.y + 2;
    }
    /**
     * Determines if this block is behind another block on the I-axis
     * A block is considered "behind" if it is positioned one step lower.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit lower on the I-axis.
     * @see #back(Block)
     */
    public boolean backI(Block other){
        return this.x == other.x - 2 && this.y == other.y + 1;
    }
    /**
     * Determines if this block is behind another block on the J-axis
     * A block is considered "behind" if it is positioned one step lower.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit lower on the J-axis.
     * @see #back(Block)
     */
    public boolean backJ(Block other){
        return this.x == other.x - 1 && this.y == other.y - 1;
    }
    /**
     * Determines if this block is behind another block on the K-axis
     * A block is considered "behind" if it is positioned one step lower.
     *
     * @param other The other block to compare with.
     * @return True if this block is one unit lower on the K-axis.
     * @see #back(Block)
     */
    public boolean backK(Block other){
        return this.x == other.x + 1 && this.y == other.y - 2;
    }
    /**
     * Checks if this block is equal to another block.
     * Two blocks are considered equal if they have the same coordinates and state.
     *
     * @param other The other block to compare.
     * @return True if both blocks have the same coordinates and state, otherwise false.
     */
    public boolean equals(Block other) {
        return this.x == other.x && this.y == other.y && this.state == other.state;
    }
    /**
     * Checks if this block is within a given radius from the origin.
     * The radius is determined using the hexagonal distance metric.
     *
     * @param radius The radius to check.
     * @return True if the block is within the radius, otherwise false.
     */
    public boolean inRange(int radius){
        return 0 <= getLineI() && getLineI() < radius*2 - 1 &&
                -radius < getLineJ() && getLineJ() < radius &&
                0 <= getLineK() && getLineK() < radius*2 - 1;
    }

    // convert to rectangular
    /**
     * Converts the hexagonal coordinates of the block to a rectangular X coordinate.
     * This transformation is based on the hexagonal grid layout, where the X-coordinate
     * is computed using the sine of 30 degrees to account for the hexagonal tiling pattern.
     *
     * @return The X-coordinate in rectangular space.
     */
    public double X(){
        return (x+y)/2.0;
    }
    /**
     * Converts the hexagonal coordinates of the block to a rectangular Y coordinate.
     * This transformation is based on the hexagonal grid layout, where the Y-coordinate
     * is computed using the sine of 60 degrees to account for the hexagonal tiling pattern.
     *
     * @return The Y-coordinate in rectangular space.
     */
    public double Y(){
        return sinOf60 * 0.5 * (x-y);
    }
    /**
     * String representation of the block used for debugging
     * <p>Format: {@code {Color = {r, g, b}; I,J,K = {i, j, k}; X,Y = {x, y}; State = state;}}</p>
     * @return A string representation of the block, including color, coordinates, and state.
     */
    public String toString(){
        return "{Color = {" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue()
                + "}; I,J,K = {" + I() + ", " + J() + ", " + K() +
                "}; X,Y = {" + X() + ", "+ Y() + "}; State = " + state + ";}";
    }

    // Setters
    /**
     * Sets the color of the block.
     *
     * @param color The new color of the block.
     */
    public void setColor(Color color){
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

    // Coordinate manipulation
    /**
     * Moves the block along the I-axis.
     *
     * @param unit The number of units to move.
     */
    public void moveI(int unit){
        this.x += 2 * unit;
        this.y -= unit;
    }
    /**
     * Moves the block along the J-axis.
     *
     * @param unit The number of units to move.
     */
    public void moveJ(int unit){
        this.x += unit;
        this.y += unit;
    }
    /**
     * Moves the block along the K-axis.
     *
     * @param unit The number of units to move.
     */
    public void moveK(int unit){
        this.x -= unit;
        this.y += 2 * unit;
    }
    /**
     * Creates a new block shifted along the I-axis.
     *
     * @param unit The number of units to shift.
     * @return A new block shifted along the I-axis.
     */
    public Block shiftI(int unit){
        return new Block (this.x + 2 * unit, this.y - unit);
    }
    /**
     * Creates a new block shifted along the J-axis.
     *
     * @param unit The number of units to shift.
     * @return A new block shifted along the J-axis.
     */
    public Block shiftJ(int unit){
        return new Block (this.x + unit, this.y + unit);
    }
    /**
     * Creates a new block shifted along the K-axis.
     *
     * @param unit The number of units to shift.
     * @return A new block shifted along the K-axis.
     */
    public Block shiftK(int unit){
        return new Block (this.x - unit, this.y + 2 * unit);
    }

    // Add and subtract
    /**
     * Adds the coordinates of another block to this block and returns a new block.
     *
     * @param other The block to add.
     * @return A new block with the summed coordinates.
     */
    public Block add(Block other){
        return new Block(this.x + other.x, this.y + other.y, this.color);
    }
    /**
     * Subtracts the coordinates of another block from this block and returns a new block.
     *
     * @param other The block to subtract.
     * @return A new block with the subtracted coordinates.
     */
    public Block subtract(Block other){
        return new Block(this.x - other.x, this.y - other.y, this.color);
    }

    // Test main
    public static void main(String[] args){
        // Move up 1, left 1, down 1, state testing
        Block b1 = new Block();
        System.out.println(b1);
        b1.moveI(1);
        System.out.println(b1);
        b1.moveJ(-1);
        b1.changeState();
        System.out.println(b1);
        b1.moveK(1);
        System.out.println(b1);
        // Lines tests
        Block b2 = new Block();
        Block b3 = new Block();
        b2.moveI(5);
        System.out.print(b2.getLineJ() + " "); // should be 5
        System.out.println(b2.getLineK()); // should be -5
        b3.moveJ(4);
        b3.moveK(1);
        System.out.print(b3.getLineI() + " "); // should be 5
        System.out.print(b3.getLineJ() + " "); // should be -1
        System.out.println(b3.getLineK()); // should be 4

        int radius = 5;
        for(int a = 0; a <= radius * 2; a++){
            for(int b = 0; b <= radius * 2; b++){
                Block nb = new Block();
                nb.moveI(a);
                nb.moveK(b);
                //System.out.print(nb.getLineI());
                if(nb.inRange(radius)){
                    System.out.println(nb.getLines());
                }
            }
            System.out.println();
        }
    }
}
