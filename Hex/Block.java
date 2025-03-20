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
    private int x;
    private int y;

    public Block(){
        // Basic constructor
        this.x = 0;
        this.y = 0;
        this.color = Color.BLACK;
    }
    public Block(int i, int k){
        // Coordinate constructor
        this.x = i;
        this.y = k;
        this.color = Color.BLACK;
    }
    public Block(int i, int k, Color color){
        // Complete constructor
        this.x = i;
        this.y = k;
        this.color = color;
    }

    // Getters
    public Color color(){
        return color;
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
                "}; X,Y = {" + X() + ", "+ Y() + "};}";
    }

    // Setters
    public void setColor(Color color){
        this.color = color;
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

    // Add and subtract
    public Block add(Block other){
        return new Block(this.x + other.x, this.y + other.y, this.color);
    }
    public Block subtract(Block other){
        return new Block(this.x - other.x, this.y - other.y, this.color);
    }

    // Test main
    public static void main(String[] args){
        // Move up 1, left 1, down 1
        Block b1 = new Block();
        System.out.println(b1);
        b1.moveI(1);
        System.out.println(b1);
        b1.moveJ(-1);
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
    }
}
