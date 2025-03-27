package Hex;

import GUI.GameEssentials;
import java.awt.*;

public class Piece implements HexGrid{
    private static boolean easy = false;
    private Block[] blocks;
    private Color color;

    // Static
    public static boolean isEasy(){return easy;}
    public static void setEasy(){easy = true;}
    public static void setHard(){easy = false;}
    // Constructor
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
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece triangle3A() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece triangle3B() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        return p;
    }
    public static Piece line3I() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    public static Piece line3J() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece line3K() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece corner3Il() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece corner3Jl() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        return p;
    }
    public static Piece corner3Kl() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    public static Piece corner3Ir() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece corner3Jr() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece corner3Kr() {
        Piece p = new Piece(3, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece fan4A() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece fan4B() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece rhombus4I() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece rhombus4J() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        return p;
    }
    public static Piece rhombus4K() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece corner4Ir() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece corner4Il() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        p.add(Block.block(0, 1));
        p.add(Block.block(-1, 0));
        return p;
    }
    public static Piece corner4Jr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece corner4Jl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece corner4Kr() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 1));
        return p;
    }
    public static Piece corner4Kl() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 1));
        return p;
    }
    public static Piece asymmetrical4Ia() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    public static Piece asymmetrical4Ib() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece asymmetrical4Ic() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        return p;
    }
    public static Piece asymmetrical4Id() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece asymmetrical4Ja() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece asymmetrical4Jb() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece asymmetrical4Jc() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece asymmetrical4Jd() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece asymmetrical4Ka() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(0, 1));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece asymmetrical4Kb() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, -1));
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece asymmetrical4Kc() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        p.add(Block.block(1, 1));
        return p;
    }
    public static Piece asymmetrical4Kd() {
        Piece p = new Piece(4, GameEssentials.generateColor());
        p.add(Block.block(-1, 0));
        p.add(Block.block(0, -1));
        p.add(Block.block(0, 0));
        p.add(Block.block(1, 0));
        return p;
    }
    public static Piece generatePiece() {
        int i = (int) (Math.random() * 86);
        if(easy) {
            // Easier generation
            if (between(i, 0, 8)) {
                return triangle3A();
            } else if (between(i, 8, 16)) {
                return triangle3B();
            } else if (between(i, 16, 22)) {
                return line3I();
            } else if (between(i, 22, 28)) {
                return line3J();
            } else if (between(i, 28, 34)) {
                return line3K();
            } else if (between(i, 34, 37)) {
                return corner3Ir();
            } else if (between(i, 37, 40)) {
                return corner3Jr();
            } else if (between(i, 40, 43)) {
                return corner3Kr();
            } else if (between(i, 43, 46)) {
                return corner3Il();
            } else if (between(i, 46, 49)) {
                return corner3Jl();
            } else if (between(i, 49, 52)) {
                return corner3Kl();
            } else if (between(i, 52, 56)) {
                return rhombus4I();
            } else if (between(i, 56, 60)) {
                return rhombus4J();
            } else if (between(i, 60, 64)) {
                return rhombus4K();
            } else if (between(i, 64, 66)) {
                return fan4A();
            } else if (between(i, 66, 68)) {
                return fan4B();
            } else if (i == 68) {
                return corner4Il();
            } else if (i == 69) {
                return corner4Ir();
            } else if (i == 70) {
                return corner4Jl();
            } else if (i == 71) {
                return corner4Jr();
            } else if (i == 72) {
                return corner4Kl();
            } else if (i == 73) {
                return corner4Kr();
            } else if (i == 74) {
                return asymmetrical4Ia();
            } else if (i == 75) {
                return asymmetrical4Ib();
            } else if (i == 76) {
                return asymmetrical4Ic();
            } else if (i == 77) {
                return asymmetrical4Id();
            } else if (i == 78) {
                return asymmetrical4Ja();
            } else if (i == 79) {
                return asymmetrical4Jb();
            } else if (i == 80) {
                return asymmetrical4Jc();
            } else if (i == 81) {
                return asymmetrical4Jd();
            } else if (i == 82) {
                return asymmetrical4Ka();
            } else if (i == 83) {
                return asymmetrical4Kb();
            } else if (i == 84) {
                return asymmetrical4Kc();
            } else if (i == 85) {
                return asymmetrical4Kd();
            } else return bigBlock(); // Should never reach
        } else {
            if (between(i, 0, 6)) {
                return triangle3A();
            } else if (between(i, 6, 12)) {
                return triangle3B();
            } else if (between(i, 12, 16)) {
                return line3I();
            } else if (between(i, 16, 20)) {
                return line3J();
            } else if (between(i, 20, 24)) {
                return line3K();
            } else if (between(i, 24, 26)) {
                return corner3Ir();
            } else if (between(i, 26, 28)) {
                return corner3Jr();
            } else if (between(i, 28, 30)) {
                return corner3Kr();
            } else if (between(i, 30, 32)) {
                return corner3Il();
            } else if (between(i, 32, 34)) {
                return corner3Jl();
            } else if (between(i, 34, 36)) {
                return corner3Kl();
            } else if (between(i, 36, 40)) {
                return rhombus4I();
            } else if (between(i, 40, 44)) {
                return rhombus4J();
            } else if (between(i, 44, 48)) {
                return rhombus4K();
            } else if (between(i, 48, 54)) {
                return fan4A();
            } else if (between(i, 54, 60)) {
                return fan4B();
            } else if (between(i, 60, 62)) {
                return corner4Il();
            } else if (between(i, 62, 64)) {
                return corner4Ir();
            } else if (between(i, 64, 66)) {
                return corner4Jl();
            } else if (between(i, 66, 68)) {
                return corner4Jr();
            } else if (between(i, 68, 70)) {
                return corner4Kl();
            } else if (between(i, 70, 72)) {
                return corner4Kr();
            } else if (i == 72) {
                return asymmetrical4Ia();
            } else if (i == 73) {
                return asymmetrical4Ib();
            } else if (i == 74) {
                return asymmetrical4Ic();
            } else if (i == 75) {
                return asymmetrical4Id();
            } else if (i == 76) {
                return asymmetrical4Ja();
            } else if (i == 77) {
                return asymmetrical4Jb();
            } else if (i == 78) {
                return asymmetrical4Jc();
            } else if (i == 79) {
                return asymmetrical4Jd();
            } else if (i == 80) {
                return asymmetrical4Ka();
            } else if (i == 81) {
                return asymmetrical4Kb();
            } else if (i == 82) {
                return asymmetrical4Kc();
            } else if (i == 83) {
                return asymmetrical4Kd();
            } else return bigBlock();
        }
    }
    private static boolean between(int value, int start, int end) {
        return value >= start && value < end;
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
