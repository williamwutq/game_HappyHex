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

    public Block(){
        // Basic constructor
        this.x = 0;
        this.y = 0;
        this.state = false;
        this.color = Color.BLACK;
    }
    public Block(int i, int k){
        // Coordinate constructor
        this.x = i;
        this.y = k;
        this.state = false;
        this.color = Color.BLACK;
    }
    public Block(int i, int k, Color color){
        // Complete constructor
        this.x = i;
        this.y = k;
        this.state = false;
        this.color = color;
    }

    // Getters
    public Color color(){
        return color;
    }
    public boolean getState(){
        return state;
    }
    public int I(){
        return x;
    }
    public int J(){
        return x + y;
    }
    public int K(){
        return y;
    }

    // Lines
    public int getLineI(){
        return (2*y+x)/3;
    }
    public int getLineJ(){
        return (x-y)/3;
    }
    public int getLineK(){
        return (2*x+y)/3;
    }
    public String getLines(){
        return "{I = " + getLineI() + ", J = " + getLineJ() + ", K = " + getLineK() + "}";
    }
 
    // Line booleans
    public boolean inLineI(int line){
        return getLineI() == line;
    }
    public boolean inLineJ(int line){
        return getLineJ() == line;
    }
    public boolean inLineK(int line){
        return getLineK() == line;
    }
    public boolean inLineI(Block other){
        return this.getLineI() == other.getLineI();
    }
    public boolean inLineJ(Block other){
        return this.getLineJ() == other.getLineJ();
    }
    public boolean inLineK(Block other){
        return this.getLineK() == other.getLineK();
    }
    public boolean adjacent(Block other){
        return front(other) || back(other);
    }
    public boolean front(Block other){
        // adjacent, this is one higher in I, J, or K
        return frontI(other) || frontJ(other) || frontK(other);
    }
    public boolean back(Block other){
        // adjacent, this is one lower in I, J, or K
        return backI(other) || backJ(other) || backK(other);
    }
    public boolean frontI(Block other){
        return this.x == other.x + 2 && this.y == other.y - 1;
    }
    public boolean frontJ(Block other){
        return this.x == other.x + 1 && this.y == other.y + 1;
    }
    public boolean frontK(Block other){
        return this.x == other.x - 1 && this.y == other.y + 2;
    }
    public boolean backI(Block other){
        return this.x == other.x - 2 && this.y == other.y + 1;
    }
    public boolean backJ(Block other){
        return this.x == other.x - 1 && this.y == other.y - 1;
    }
    public boolean backK(Block other){
        return this.x == other.x + 1 && this.y == other.y - 2;
    }
    public boolean equals(Block other) {
        return this.x == other.x && this.y == other.y && this.state == other.state;
    }
    public boolean inRange(int radius){
        return 0 <= getLineI() && getLineI() < radius*2 - 1 &&
               -radius < getLineJ() && getLineJ() < radius &&
               0 <= getLineK() && getLineK() < radius*2 - 1;
    }

    // convert to rectangular
    public double X(){
        return (x+y)/2.0;
    }
    public double Y(){
        return sinOf60 * 0.5 * (x-y);
    }
    public String toString(){
        return "{Color = {" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue()
                + "}; I,J,K = {" + I() + ", " + J() + ", " + K() +
                "}; X,Y = {" + X() + ", "+ Y() + "}; State = " + state + ";}";
    }

    // Setters
    public void setColor(Color color){
        this.color = color;
    }
    public void setState(boolean state){
        this.state = state;
    }
    public void changeState(){
        this.state = !this.state;
    }
 
    // Coordinate manipulation
    public void moveI(int unit){
        this.x += 2 * unit;
        this.y -= unit;
    }
    public void moveJ(int unit){
        this.x += unit;
        this.y += unit;
    }
    public void moveK(int unit){
        this.x -= unit;
        this.y += 2 * unit;
    }
    public Block shiftI(int unit){
        return new Block (this.x + 2 * unit, this.y - unit);
    }
    public Block shiftJ(int unit){
        return new Block (this.x + unit, this.y + unit);
    }
    public Block shiftK(int unit){
        return new Block (this.x - unit, this.y + 2 * unit);
    }

    // Add and subtract
    public Block add(Block other){
        return new Block(this.x + other.x, this.y + other.y, this.color);
    }
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
