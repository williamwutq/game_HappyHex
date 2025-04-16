package Hex;

/**
 * <pre>Coordinate system: (2i, 2j, 2k)
 *    I
 *   / * (5, 4, -1)
 *  /     * (5, 7, 2)
 * o - - J
 *  \ * (0, 3, 3)
 *   \
 *    K
 * </pre>
 * <p>Coordinates: dimension along the line I, J, or K.</p>
 * <p>Line distances: distances to I or K, which are perpendicular to the coordinates.</p>
 */

public class Hex{
    private final double sinOf60 = Math.sqrt(3) / 4;
    private int x;
    private int y;

    // Basic constructors
    /**
     * Default constructor initializing the hex coordinate at (0,0).
     */
    public Hex(){
        // Basic constructor
        this.x = 0;
        this.y = 0;
    }
    /**
     * Constructs a hex at the specified (i, k) coordinates.
     *
     * @param i The i-coordinate.
     * @param k The k-coordinate.
     */
    public Hex(int i, int k){
        // Coordinate constructor
        this.x = i;
        this.y = k;
    }

    // Line constructors (static)
    // Please use those for the game instead of the old constructors
    /**
     * Creates a default hex coordinate at (0,0).
     *
     * @return A new hex instance at the origin.
     */
    public static Hex hex(){
        return new Hex();
    }
    /**
     * Creates a hex coordinate using hexagonal line indices instead of direct coordinates.
     * The hex is shifted accordingly in the coordinate system.
     *
     * @param i The I-line index in the hexagonal coordinate system.
     * @param k The K-line index in the hexagonal coordinate system.
     * @return A new hex coordinate positioned according to the given line indices.
     */
    public static Hex hex(int i, int k){
        return new Hex().shiftI(k).shiftK(i);
    }

    // Raw coordinates
    /**
     * Raw I-coordinate
     * @return The I-coordinate.
     */
    public int I(){
        return x;
    }
    /**
     * Raw J-coordinate
     * @return The J-coordinate.
     */
    public int J(){
        return x + y;
    }
    /**
     * Raw K-coordinate
     * @return The K-coordinate.
     */
    public int K(){
        return y;
    }

    // Lines
    /**
     * Computes the line index along the I-axis in the hexagonal coordinate system.
     * The I-axis represents a diagonal axis in the hexagonal coordinate system.
     *
     * @return The computed I-line index.
     * @see #getLines()
     */
    public int getLineI(){
        return (2*y+x)/3;
    }
    /**
     * Computes the line index along the J-axis in the hexagonal coordinate system.
     * The J-axis represents a diagonal axis in the hexagonal coordinate system.
     *
     * @return The computed J-line index.
     * @see #getLines()
     */
    public int getLineJ(){
        return (x-y)/3;
    }
    /**
     * Computes the line index along the K-axis in the hexagonal coordinate system.
     * The K-axis represents a diagonal axis in the hexagonal coordinate system.
     *
     * @return The computed K-line index.
     * @see #getLines()
     */
    public int getLineK(){
        return (2*x+y)/3;
    }
    /**
     * String representation of the line indices of the hex along all axes
     * <p>Format: {@code {I = i, J = j, K = k}}</p>
     *
     * @return A formatted string representing the hex coordinate indices along I, J, and K axes.
     * @see #getLineI()
     * @see #getLineJ()
     * @see #getLineK()
     */
    public String getLines(){
        return "{I = " + getLineI() + ", J = " + getLineJ() + ", K = " + getLineK() + "}";
    }

    // Line booleans
    /**
     * Determines whether this hex is in the given I-line.
     * The I-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param line The I-line value to check.
     * @return True if the hex is in the specified I-line, otherwise false.
     * @see #inLineI(Hex)
     */
    public boolean inLineI(int line){
        return getLineI() == line;
    }
    /**
     * Determines whether this hex is in the given J-line.
     * The J-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param line The J-line value to check.
     * @return True if the hex is in the specified J-line, otherwise false.
     * @see #inLineJ(Hex)
     */
    public boolean inLineJ(int line){
        return getLineJ() == line;
    }
    /**
     * Determines whether this hex is in the given K-line.
     * The K-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param line The K-line value to check.
     * @return True if the hex is in the specified K-line, otherwise false.
     * @see #inLineK(Hex)
     */
    public boolean inLineK(int line){
        return getLineK() == line;
    }
    /**
     * Determines whether this hex coordinate is in the same I-line as the other hex coordinate.
     * The I-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param other The other hex coordinate to compare with.
     * @return True if this hex coordinate is in the same I-line as the other hex coordinate.
     * @see #inLineI(int)
     */
    public boolean inLineI(Hex other){
        return this.getLineI() == other.getLineI();
    }
    /**
     * Determines whether this hex coordinate is in the same J-line as the other hex coordinate.
     * The J-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param other The other hex coordinate to compare with.
     * @return True if this hex coordinate is in the same K-line as the other hex coordinate.
     * @see #inLineJ(int)
     */
    public boolean inLineJ(Hex other){
        return this.getLineJ() == other.getLineJ();
    }
    /**
     * Determines whether this hex coordinate is in the same K-line as the other hex coordinate.
     * The K-line represents a diagonal axis in the hexagonal coordinate system.
     *
     * @param other The other hex coordinate to compare with.
     * @return True if this hex coordinate is in the same K-line as the other hex coordinate.
     * @see #inLineK(int)
     */
    public boolean inLineK(Hex other){
        return this.getLineK() == other.getLineK();
    }
    /**
     * Determines whether this hex coordinate is adjacent to another hex.
     * Two hex coordinates are considered adjacent if they share an edge in the hexagonal grid.
     * Use front and back for more precise finding.
     *
     * @param other The other hex to compare with.
     * @return True if the hex coordinates are adjacent, otherwise false.
     * @see #front(Hex)
     * @see #back(Hex)
     */
    public boolean adjacent(Hex other){
        return front(other) || back(other);
    }
    /**
     * Determines if this hex coordinate is in front of another hex coordinate.
     * A hex coordinate is considered "in the front" if it is positioned one step higher in any of the three coordinate axes.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit higher in I, J, or K, otherwise false.
     * @see #adjacent(Hex)
     * @see #frontI(Hex)
     * @see #frontJ(Hex)
     * @see #frontK(Hex)
     */
    public boolean front(Hex other){
        // adjacent, this is one higher in I, J, or K
        return frontI(other) || frontJ(other) || frontK(other);
    }
    /**
     * Determines if this hex coordinate is behind another hex coordinate.
     * A hex coordinate is considered "behind" if it is positioned one step lower in any of the three coordinate axes.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit lower in I, J, or K, otherwise false.
     * @see #adjacent(Hex)
     * @see #backI(Hex)
     * @see #backJ(Hex)
     * @see #backK(Hex)
     */
    public boolean back(Hex other){
        // adjacent, this is one lower in I, J, or K
        return backI(other) || backJ(other) || backK(other);
    }
    /**
     * Determines if this hex coordinate is in front of another hex coordinate on the I-axis.
     * A hex coordinate is considered "in the front" if it is positioned one step higher.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit higher on the I-axis.
     * @see #front(Hex)
     */
    public boolean frontI(Hex other){
        return this.x == other.x + 2 && this.y == other.y - 1;
    }
    /**
     * Determines if this hex coordinate is in front of another hex coordinate on the J-axis.
     * A hex coordinate is considered "in the front" if it is positioned one step higher.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit higher on the J-axis.
     * @see #front(Hex)
     */
    public boolean frontJ(Hex other){
        return this.x == other.x + 1 && this.y == other.y + 1;
    }
    /**
     * Determines if this hex coordinate is in front of another hex coordinate on the K-axis.
     * A hex coordinate is considered "in the front" if it is positioned one step higher.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit higher on the K-axis.
     * @see #front(Hex)
     */
    public boolean frontK(Hex other){
        return this.x == other.x - 1 && this.y == other.y + 2;
    }
    /**
     * Determines if this hex coordinate is behind another hex coordinate on the I-axis
     * A hex coordinate is considered "behind" if it is positioned one step lower.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit lower on the I-axis.
     * @see #back(Hex)
     */
    public boolean backI(Hex other){
        return this.x == other.x - 2 && this.y == other.y + 1;
    }
    /**
     * Determines if this hex coordinate is behind another hex coordinate on the J-axis
     * A hex coordinate is considered "behind" if it is positioned one step lower.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit lower on the J-axis.
     * @see #back(Hex)
     */
    public boolean backJ(Hex other){
        return this.x == other.x - 1 && this.y == other.y - 1;
    }
    /**
     * Determines if this hex coordinate is behind another hex coordinate on the K-axis
     * A hex coordinate is considered "behind" if it is positioned one step lower.
     *
     * @param other The other hex to compare with.
     * @return True if this hex coordinate is one unit lower on the K-axis.
     * @see #back(Hex)
     */
    public boolean backK(Hex other){
        return this.x == other.x + 1 && this.y == other.y - 2;
    }
    /**
     * Checks if this hex is equal to another hex.
     * Two hex coordinates are considered equal if they have the same coordinates in all axes.
     *
     * @param other The other hex to compare.
     * @return True if both hex coordinates have the same coordinates, otherwise false.
     */
    public boolean equals(Hex other) {
        return this.x == other.x && this.y == other.y;
    }
    /**
     * Checks if this hex coordinate is within a given radius from the origin, as specified in the grid.
     * The radius is determined using the hexagonal distance metric.
     *
     * @param radius The radius to check.
     * @return True if the hex is within the radius, otherwise false.
     */
    public boolean inRange(int radius){
        return 0 <= getLineI() && getLineI() < radius*2 - 1 &&
                -radius < getLineJ() && getLineJ() < radius &&
                0 <= getLineK() && getLineK() < radius*2 - 1;
    }

    // convert to rectangular
    /**
     * Converts the hexagonal coordinates to a rectangular X coordinate.
     * This transformation is based on the hexagonal grid layout, where the X-coordinate
     * is computed using the sine of 60 degrees to account for the hexagonal tiling pattern.
     *
     * @return The X-coordinate in rectangular space.
     */
    public double X(){
        return sinOf60 * (x+y);
    }
    /**
     * Converts the hexagonal coordinates to a rectangular Y coordinate.
     * This transformation is based on the hexagonal grid layout, where the Y-coordinate
     * is computed using the sine of 30 degrees to account for the hexagonal tiling pattern.
     *
     * @return The Y-coordinate in rectangular space.
     */
    public double Y(){
        return (x-y)/4.0;
    }
    /**
     * String representation of the hex coordinate used for debugging
     * <p>Format: {@code {Hex I,J,K = {i, j, k}, Line I,J,K = {i, j, k}, Rect X,Y = {x, y}}}</p>
     * @return A string representation of the hex coordinate, including its rectangular conversion.
     */
    public String toString(){
        return "{Hex I,J,K = {" + I() + ", " + J() + ", " + K() +
                "}, Line I,J,K = {" + getLineI() + ", " + getLineJ() + ", " + getLineK() +
                "}, Rect X,Y = {" + X() + ", "+ Y() + "}}";
    }

    // Coordinate manipulation
    /**
     * Moves the hex coordinate along the I-axis.
     *
     * @param unit The number of units to move.
     */
    public void moveI(int unit){
        this.x += 2 * unit;
        this.y -= unit;
    }
    /**
     * Moves the hex coordinate along the J-axis.
     *
     * @param unit The number of units to move.
     */
    public void moveJ(int unit){
        this.x += unit;
        this.y += unit;
    }
    /**
     * Moves the hex coordinate along the K-axis.
     *
     * @param unit The number of units to move.
     */
    public void moveK(int unit){
        this.x -= unit;
        this.y += 2 * unit;
    }
    /**
     * Creates a new hex coordinate shifted along the I-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the I-axis.
     */
    public Hex shiftI(int unit){
        return new Hex (this.x + 2 * unit, this.y - unit);
    }
    /**
     * Creates a new hex coordinate shifted along the J-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the J-axis.
     */
    public Hex shiftJ(int unit){
        return new Hex (this.x + unit, this.y + unit);
    }
    /**
     * Creates a new hex coordinate shifted along the K-axis.
     *
     * @param unit The number of units to shift.
     * @return A new hex coordinate shifted along the K-axis.
     */
    public Hex shiftK(int unit){
        return new Hex (this.x - unit, this.y + 2 * unit);
    }

    // Add and subtract
    /**
     * Adds another hex to this hex coordinate and returns a new hex coordinate.
     *
     * @param other The hex coordinate to add.
     * @return A new hex coordinate with the summed coordinates.
     */
    public Hex add(Hex other){
        return new Hex(this.x + other.x, this.y + other.y);
    }
    /**
     * Subtracts another hex coordinate from this hex coordinate and returns a new hex coordinate.
     *
     * @param other The hex coordinate to subtract.
     * @return A new hex coordinate with the subtracted coordinates.
     */
    public Hex subtract(Hex other){
        return new Hex(this.x - other.x, this.y - other.y);
    }
    // Set
    /**
     * Adds set hex to another hex coordinate.
     *
     * @param other The target hex coordinate.
     */
    public void set(Hex other){
        this.x = other.x;
        this.y = other.y;
    }
    // Get
    /**
     * This hex coordinate
     *
     * @return this hex coordinate object
     */
    public Hex thisHex(){
        return Hex.hex(this.getLineI(), this.getLineK());
    }
}
