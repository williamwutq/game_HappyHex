package Hex;

import GUI.GameEssentials;
import java.awt.*;

public class Piece implements HexGrid{
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
    public Block getBlock(int index){
        return blocks[index];
    }
    public void add(Hex origin, HexGrid other) throws IllegalArgumentException{
        throw new IllegalArgumentException("Adding Grid to piece prohibited. Please add block by block.");
    }

    // Static pieces
    public static Piece bigBlock() {
        Piece p = new Piece(7, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(1,0));
        p.add(Block.block(1,1));
        p.add(Block.block(1,2));
        p.add(Block.block(2,1));
        p.add(Block.block(2,2));
        return p;
    }
    public static Piece triangle3A() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(1,1));
        return p;
    }
    public static Piece triangle3B() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,0));
        p.add(Block.block(1,1));
        return p;
    }
    public static Piece line3I() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(0,2));
        return p;
    }
    public static Piece line3J() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        p.add(Block.block(2,2));
        return p;
    }
    public static Piece line3K() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,0));
        p.add(Block.block(2,0));
        return p;
    }
    public static Piece corner3I() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1,0));
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        return p;
    }
    public static Piece corner3J() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0,1));
        p.add(Block.block(0,0));
        p.add(Block.block(1,0));
        return p;
    }
    public static Piece corner3K() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(1,1));
        p.add(Block.block(0,0));
        p.add(Block.block(0,-1));
        return p;
    }
    public static Piece generatePiece(){
        return switch((int)(Math.random() * 19)){
            case 1, 2, 3 -> triangle3A();
            case 4, 5, 6 -> triangle3B();
            case 7, 8 -> line3I();
            case 9, 10 -> line3J();
            case 11, 12 -> line3K();
            case 13, 14 -> corner3I();
            case 15, 16 -> corner3J();
            case 17, 18 -> corner3K();
            default -> bigBlock();
        };
    }

    public String toString(){
        StringBuilder str = new StringBuilder("{Piece: ");
        for (Block block : blocks) {
            str.append(block.getLines());
        }
        return str + "}";
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
        System.out.println();
        System.out.println(p1); // Print out this
        System.out.println(generatePiece()); // Print out random
        System.out.println(generatePiece()); // Print out random
        System.out.println(generatePiece()); // Print out random
    }
}
