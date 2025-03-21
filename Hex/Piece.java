package Hex;

import java.awt.*;

class Piece implements HexGrid{
    private Block[] blocks;
    private Color color;
    public Piece(){
        this.blocks = new Block[1];
        this.color = Color.BLACK;
        this.blocks[0] = new Block(0, 0, color);
    }
    public Piece(int length, Color color){
        if(length < 1){
            length = 1;
        }
        this.blocks = new Block[length];
        this.color = color;
    }

    // Color
    public void setColor(Color color){
        this.color = color;
        // write to all
        for(int i = 0; i < length(); i ++){
            if(blocks[i] != null){
                blocks[i].setColor(color); // Set all color to color
            }
        }
    }
    public Color getColor(){
        return color;
    }

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
    public int length(){
        if(blocks == null){
            return 0;
        }else{
            return blocks.length;
        }
    }
    public Block[] blocks(){
        // Remove null
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
    public boolean inRange(int i, int k){
        // Attempt to find it
        return this.getBlock(i, k) != null;
    }
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
    public void add(Block origin, HexGrid other) throws IllegalArgumentException{
        throw new IllegalArgumentException("Adding Grid to piece prohibited. Please add block by block.");
    }

    public static void main(String[] args){
        Piece p1 = new Piece(5, Color.BLUE);
        System.out.println(p1.add(Block.block(0,0)));// Should be true
        System.out.println(p1.add(Block.block(1,0)));// Should be true
        System.out.println(p1.add(Block.block(0,2)));// Should be true
        System.out.println(p1.add(Block.block(0,1)));// Should be true
        System.out.println(p1.add(Block.block(1,1)));// Should be true
        System.out.println(p1.add(Block.block(2,0)));// Should be false
        System.out.println();
        System.out.println(p1.inRange(2,0));// Should be false
        System.out.println(p1.inRange(0,2));// Should be true
        System.out.println(p1.inRange(1,1));// Should be true
        System.out.println();
        System.out.println(p1.getBlock(0,1));// Should have Color (0, 0, 255)
    }
}
