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
    private Color color;
    private int i;
    private int j;
    private int k;
    
    public Block(){
        // Basic constructor
        this.i = 0;
        this.j = 0;
        this.k = 0;
        this.color = Color.BLACK;
    }
    public Block(int i, int k){
        // Coordinate constructor
        this.i = 0;
        this.j = i + k;
        this.k = 0;
        this.color = Color.BLACK;
    }
    public Block(int i, int k, Color color){
        // Complete constructor
        this.i = 0;
        this.j = i + k;
        this.k = 0;
        this.color = color;
    }

    // Test main
    public static void main(String[] args){
        // Do nothing currently
    }
}
