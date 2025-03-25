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
        sort();
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
        p.add(Block.block(0,-1));
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        return p;
    }
    public static Piece fan4A() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1,-1));
        p.add(Block.block(0,0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece fan4B() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1,0));
        p.add(Block.block(0,-1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece rhombus4I() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(1,1));
        p.add(Block.block(1, 2));
        return p;
    }
    public static Piece rhombus4J() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1,1));
        return p;
    }
    public static Piece rhombus4K() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,0));
        p.add(Block.block(1,1));
        p.add(Block.block(2, 1));
        return p;
    }
    public static Piece corner4Ir() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(1,0));
        p.add(Block.block(2,1));
        return p;
    }
    public static Piece corner4Il() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,-1));
        p.add(Block.block(0,0));
        p.add(Block.block(-1,0));
        p.add(Block.block(-2,-1));
        return p;
    }
    public static Piece corner4Jr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1,0));
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        p.add(Block.block(1,2));
        return p;
    }
    public static Piece corner4Jl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,-1));
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        p.add(Block.block(2,1));
        return p;
    }
    public static Piece corner4Kr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(1,0));
        p.add(Block.block(1,2));
        return p;
    }
    public static Piece corner4Kl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,-1));
        p.add(Block.block(0,0));
        p.add(Block.block(-1,-2));
        p.add(Block.block(-1,0));
        return p;
    }
    public static Piece asymmetrical4Il() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1,0));
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(0,2));
        return p;
    }
    public static Piece asymmetrical4Ir() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(0,1));
        p.add(Block.block(0,2));
        p.add(Block.block(1,1));
        return p;
    }
    public static Piece asymmetrical4Jl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        p.add(Block.block(2,1));
        p.add(Block.block(2,2));
        return p;
    }
    public static Piece asymmetrical4Jr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,1));
        p.add(Block.block(1,2));
        p.add(Block.block(2,2));
        return p;
    }
    public static Piece asymmetrical4Kl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,0));
        p.add(Block.block(1,0));
        p.add(Block.block(1,1));
        p.add(Block.block(2,0));
        return p;
    }
    public static Piece asymmetrical4Kr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0,-1));
        p.add(Block.block(0,0));
        p.add(Block.block(1,0));
        p.add(Block.block(2,0));
        return p;
    }

    public static Piece generatePiece(){
        int i = (int) (Math.random() * 47);
        if (i == 1 || i == 2 || i == 3) {
            return triangle3A();
        } else if (i == 4 || i == 5 || i == 6) {
            return triangle3B();
        } else if (i == 7 || i == 8) {
            return line3I();
        } else if (i == 9 || i == 10) {
            return line3J();
        } else if (i == 11 || i == 12) {
            return line3K();
        } else if (i == 13 || i == 14) {
            return corner3I();
        } else if (i == 15 || i == 16) {
            return corner3J();
        } else if (i == 17 || i == 18) {
            return corner3K();
        } else if (i == 19 || i == 20) {
            return rhombus4I();
        } else if (i == 21 || i == 22) {
            return rhombus4J();
        } else if (i == 23 || i == 24) {
            return rhombus4K();
        } else if (i == 25 || i == 26) {
            return line4I();
        } else if (i == 27 || i == 28) {
            return line4J();
        } else if (i == 29 || i == 30) {
            return line4K();
        } else if (i == 31) {
            return corner4Il();
        } else if (i == 32) {
            return corner4Ir();
        } else if (i == 33) {
            return corner4Jl();
        } else if (i == 34) {
            return corner4Jr();
        } else if (i == 35) {
            return corner4Kl();
        } else if (i == 36) {
            return corner4Kr();
        } else if (i == 37) {
            return asymmetrical4Il();
        } else if (i == 38) {
            return asymmetrical4Ir();
        } else if (i == 39) {
            return asymmetrical4Jl();
        } else if (i == 40) {
            return asymmetrical4Jr();
        } else if (i == 41) {
            return asymmetrical4Kl();
        } else if (i == 42) {
            return asymmetrical4Kr();
        } else if (i == 43 || i == 44){
            return fan4A();
        } else if (i == 45 || i == 46){
            return fan4B();
        }else return bigBlock();
    }
    
    private void sort() {
        int n = blocks.length;
        for (int i = 1; i < n; i++) {
            Block key = blocks[i];
            int j = i - 1;
            // Sort by getLineI(), then getLineK() if equal
            while (j >= 0 && (blocks[j].getLineI() > key.getLineI() || (blocks[j].getLineI() == key.getLineI() && blocks[j].getLineK() > key.getLineK()))) {
                blocks[j + 1] = blocks[j];
                j--;
            }
            blocks[j + 1] = key;
        }
    }
    public String toString(){
        StringBuilder str = new StringBuilder("{Piece: ");
        for (Block block : blocks) {
            str.append(block.getLines());
        }
        return str + "}";
    }
    public boolean equals(Piece piece) {
        if(piece.length() != this.length()) {return false;}
        this.sort();
        piece.sort();
        for(int i = 0; i < this.length(); i ++){
            if(!this.blocks[i].equals(piece.blocks[i])){
                return false;
            }
        }
        return true;
    }
}
