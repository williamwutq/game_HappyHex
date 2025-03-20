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


class Block extends Hex{
    private final double sinOf60 = Math.sqrt(3) / 2;
    private Color color;
    private boolean state;

    // Basic constructors
    /**
     * Default constructor initializing the block at (0,0) with black color and unoccupied state.
     */
    public Block(){
        // Basic constructor
        super();
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
        super(i, k);
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
        super(i, k);
        this.state = false;
        this.color = color;
    }
    /**
     * Constructs a block at the specified hex coordinates and unoccupied state.
     *
     * @param hex the coordinate
     */
    public Block(Hex hex){
        // Coordinate constructor
        super();
        super.set(hex);
        this.state = false;
        this.color = Color.BLACK;
    }
    /**
     * Constructs a block at the specified hex coordinates with a specified color and unoccupied state.
     *
     * @param hex the coordinate
     * @param color The color of the block.
     */
    public Block(Hex hex, Color color){
        // Complete constructor
        super();
        super.set(hex);
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
     * String representation of the block used for debugging
     * <p>Format: {@code {Color = {r, g, b}; I,J,K = {i, j, k}; Line I,J,K = {i, j, k}; X,Y = {x, y}; State = state;}}</p>
     * @return A string representation of the block, including color, coordinates, and state.
     */
    public String toString(){
        return "{Color = {" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue()
                + "}; I,J,K = {" + I() + ", " + J() + ", " + K() +
                "}; Line I,J,K = {" + getLineI() + ", " + getLineJ() + ", " + getLineK() +
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

    /**
     * Creates a new hex coordinate shifted along the I-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the I-axis.
     */
    public Block shiftI(int unit){
        Block temp = this;
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
        Block temp = this;
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
        Block temp = this;
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
        Block temp = this;
        temp.add(other);
        return temp;
    }
    /**
     * Subtracts another hex coordinate from this hex coordinate and returns a new hex coordinate.
     *
     * @param other The hex coordinate to subtract.
     * @return A new hex coordinate with the subtracted coordinates.
     */
    public Block subtract(Hex other){
        Block temp = this;
        temp.subtract(other);
        return temp;
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
                if(nb.inRange(radius)){
                    System.out.println(nb.getLines());
                }
            }
            System.out.println();
        }
    }
}
