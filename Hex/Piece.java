package Hex;

import java.awt.*;

class Piece implements HexGrid{
    private Block[] blocks;
    public Piece(){
        // To be implemented
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
        return this.blocks;
    }
    public boolean inRange(int i, int k){
        return true; // Placeholder
    }
    public Block getBlock(int i, int k){
        if(inRange(i, k)){
            // Implement logic
            return blocks[0]; // Placeholder
        }else{
            return null;
        }
    }
    public void add(Block origin, HexGrid other){
        // To be implemented
    }
}
