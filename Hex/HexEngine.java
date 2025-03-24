package Hex;

import java.awt.Color;
import java.util.ArrayList;

public class HexEngine implements HexGrid{
    private int radius;
    private Block[] blocks;
    public HexEngine(int radius){
        this.radius = radius;
        // Calculate array size
        // Recursive Formula Ak = A(k-1) + 6 * (k-1)
        // General Formula: Ak = 1 + 3 * (k-1)*(k)
        this.blocks = new Block[1 + 3*(radius)*(radius-1)];
        // Add into array to generate the grid
        int i = 0;
        for(int a = 0; a <= radius*2-1; a++){
            for(int b = 0; b <= radius*2-1; b++){
                Block nb = new Block();
                nb.moveI(b);
                nb.moveK(a);
                if(nb.inRange(radius)){
                    blocks[i] = nb;
                    i ++;
                }
            }
        }
        // Already sorted by first I then K
    }
    public void reset(){
        // Set all to empty and default color
        for (Block block : blocks){
            block.setColor(Color.BLACK);
            block.setState(false);
        }
    }
    public int getRadius(){
        return radius;
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
        // Use line
        return Hex.hex(i, k).inRange(radius);
    }
    public Block getBlock(int i, int k){
        if(inRange(i, k)){
            int index = search(i, k, 0, length()-1);
            if (index >= 0) {
                return getBlock(index); // private binary search
            }
        }
        return null;
    }
    public Block getBlock(int index){
        return blocks[index];
    }
    public void setBlock(int i, int k, Block block){
        if(inRange(i, k)){
            int index = search(i, k, 0, length()-1);
            if (index >= 0) {
                blocks[index] = block;
            }
        }
    }
    private int search(int i, int k, int start, int end){
        if(start > end){return -1;}
        int middleIndex = (start + end)/2;
        Block middle = blocks[middleIndex];
        if(middle.getLineI() == i && middle.getLineK() == k){
            return middleIndex;
        } else if (middle.getLineI() < i){
            // second half
            return search(i, k, middleIndex+1, end);
        } else if (middle.getLineI() > i){
            // first half
            return search(i, k, start, middleIndex-1);
        } else if (middle.getLineK() < k) {
            // second half
            return search(i, k, middleIndex+1, end);
        } else {
            // first half
            return search(i, k, start, middleIndex-1);
        }
    }
    public boolean checkAdd(Hex origin, HexGrid other){
        // Iterate through other
        Block[] otherBlocks = other.blocks();
        for(int i = 0; i < other.length(); i ++){
            Block current = otherBlocks[i];
            // Null check and state check
            if (current != null && current.getState()){
                current = current.add(origin); // placement
                // Check for this HexGrid
                Block selfTarget = this.getBlock(current.getLineI(), current.getLineK());
                if (selfTarget == null || selfTarget.getState()){
                    return false;
                }
            }
        }
        return true;
    }
    public void add(Hex origin, HexGrid other) throws IllegalArgumentException{
        // Iterate through other
        Block[] otherBlocks = other.blocks();
        for(int i = 0; i < other.length(); i ++){
            Block current = otherBlocks[i];
            // Null check and state check
            if (current != null && current.getState()){
                current = current.add(origin); // placement
                // Check for this HexGrid
                Block selfTarget = this.getBlock(current.getLineI(), current.getLineK());
                if (selfTarget == null){
                    // If it cannot be found, it must be out of range
                    throw new IllegalArgumentException(new IndexOutOfBoundsException("Block out of grid when adding"));
                } else if (selfTarget.getState()){
                    // If this position is already occupied, it can't be added neither
                    throw new IllegalArgumentException("Cannot add into existing block");
                } else {
                    // If all checks, proceed to add block
                    setBlock(current.getLineI(), current.getLineK(), current);
                }
            }
        }
    }
    public ArrayList<Hex> checkPositions(HexGrid other){
        ArrayList<Hex> positions = new ArrayList<Hex>();
        // Try to find positions by checking all available space
        for (Block block : blocks){
            Hex hex = block.thisHex();
            // Use blocks as hex
            if(checkAdd(hex, other)){
                // If it is possible to add, record this position
                positions.add(hex);
            }
        }
        // Return
        return positions;
    }
    public int eliminate(){
        // Eliminate according to I, J, K, then return how many blocks are being eliminated
        ArrayList<Block> eliminate = new ArrayList<Block>();
        // Check I
        for(int i = 0; i < radius*2 - 1; i ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineI() == i){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            eliminate.addAll(line);
        }
        // Check J
        for(int j = 1 - radius; j < radius; j ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineJ() == j){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            eliminate.addAll(line);
        }
        // Check K
        for(int k = 0; k < radius*2 - 1; k ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineK() == k){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            eliminate.addAll(line);
        }
        // Eliminate
        for(Block block : eliminate){
            block.setColor(Color.BLACK);
            block.setState(false);
            setBlock(block.getLineI(), block.getLineK(), block);
        }
        return eliminate.size(); // Number of blocks being eliminated
    }
    public boolean checkEliminate(){
        // Check I
        for(int i = 0; i < radius*2 - 1; i ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineI() == i){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            if(!line.isEmpty()) return true; // If one line is to be eliminated
        }
        // Check J
        for(int j = 1 - radius; j < radius; j ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineJ() == j){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            if(!line.isEmpty()) return true; // If one line is to be eliminated
        }
        // Check K
        for(int k = 0; k < radius*2 - 1; k ++){
            ArrayList<Block> line = new ArrayList<Block>();
            for(int index = 0; index < length(); index ++){
                if(blocks[index].getLineK() == k){
                    // Found block
                    if(blocks[index].getState()){
                        line.add(blocks[index]);
                    } else {
                        // Else this line does not satisfy, clean up line and break out of the for loop
                        line.clear();
                        break;
                    }
                }
            }
            if(!line.isEmpty()) return true; // If one line is to be eliminated
        }
        return false;
    }

    public String toString(){
        StringBuilder str = new StringBuilder("{HexEngine: ");
        for (Block block : blocks) {
            str.append(block.getLines());
            str.append(",");
            str.append(block.getState());
            str.append("; ");
        }
        return str + "}";
    }
    public static void main(String[] args){
        /*
Running log example:
{HexEngine: {I = 0, J = 0, K = 0},false; {I = 0, J = 1, K = 1},false; {I = 0, J = 2, K = 2},false; {I = 1, J = -1, K = 0},false; {I = 1, J = 0, K = 1},false; {I = 1, J = 1, K = 2},false; {I = 1, J = 2, K = 3},false; {I = 2, J = -2, K = 0},false; {I = 2, J = -1, K = 1},false; {I = 2, J = 0, K = 2},false; {I = 2, J = 1, K = 3},false; {I = 2, J = 2, K = 4},false; {I = 3, J = -2, K = 1},false; {I = 3, J = -1, K = 2},false; {I = 3, J = 0, K = 3},false; {I = 3, J = 1, K = 4},false; {I = 4, J = -2, K = 2},false; {I = 4, J = -1, K = 3},false; {I = 4, J = 0, K = 4},false; }
{Piece: {I = 0, J = 1, K = 1}{I = 0, J = 0, K = 0}{I = 1, J = -1, K = 0}}
{HexEngine: {I = 0, J = 0, K = 0},false; {I = 0, J = 1, K = 1},true; {I = 0, J = 2, K = 2},true; {I = 1, J = -1, K = 0},false; {I = 1, J = 0, K = 1},true; {I = 1, J = 1, K = 2},false; {I = 1, J = 2, K = 3},false; {I = 2, J = -2, K = 0},false; {I = 2, J = -1, K = 1},false; {I = 2, J = 0, K = 2},false; {I = 2, J = 1, K = 3},false; {I = 2, J = 2, K = 4},false; {I = 3, J = -2, K = 1},false; {I = 3, J = -1, K = 2},false; {I = 3, J = 0, K = 3},false; {I = 3, J = 1, K = 4},false; {I = 4, J = -2, K = 2},false; {I = 4, J = -1, K = 3},false; {I = 4, J = 0, K = 4},false; }

{Piece: {I = 0, J = 0, K = 0}{I = 0, J = 1, K = 1}{I = 1, J = 0, K = 1}}
8 out of 19 positions available.
{HexEngine: {I = 0, J = 0, K = 0},false; {I = 0, J = 1, K = 1},true; {I = 0, J = 2, K = 2},true; {I = 1, J = -1, K = 0},false; {I = 1, J = 0, K = 1},true; {I = 1, J = 1, K = 2},true; {I = 1, J = 2, K = 3},true; {I = 2, J = -2, K = 0},false; {I = 2, J = -1, K = 1},false; {I = 2, J = 0, K = 2},false; {I = 2, J = 1, K = 3},true; {I = 2, J = 2, K = 4},false; {I = 3, J = -2, K = 1},false; {I = 3, J = -1, K = 2},false; {I = 3, J = 0, K = 3},false; {I = 3, J = 1, K = 4},false; {I = 4, J = -2, K = 2},false; {I = 4, J = -1, K = 3},false; {I = 4, J = 0, K = 4},false; }

{Piece: {I = 0, J = 1, K = 1}{I = 0, J = 0, K = 0}{I = 1, J = -1, K = 0}}
3 out of 19 positions available.
{HexEngine: {I = 0, J = 0, K = 0},false; {I = 0, J = 1, K = 1},true; {I = 0, J = 2, K = 2},true; {I = 1, J = -1, K = 0},false; {I = 1, J = 0, K = 1},true; {I = 1, J = 1, K = 2},true; {I = 1, J = 2, K = 3},true; {I = 2, J = -2, K = 0},false; {I = 2, J = -1, K = 1},true; {I = 2, J = 0, K = 2},true; {I = 2, J = 1, K = 3},true; {I = 2, J = 2, K = 4},false; {I = 3, J = -2, K = 1},true; {I = 3, J = -1, K = 2},false; {I = 3, J = 0, K = 3},false; {I = 3, J = 1, K = 4},false; {I = 4, J = -2, K = 2},false; {I = 4, J = -1, K = 3},false; {I = 4, J = 0, K = 4},false; }
After elimination
4
{HexEngine: {I = 0, J = 0, K = 0},false; {I = 0, J = 1, K = 1},false; {I = 0, J = 2, K = 2},true; {I = 1, J = -1, K = 0},false; {I = 1, J = 0, K = 1},false; {I = 1, J = 1, K = 2},true; {I = 1, J = 2, K = 3},true; {I = 2, J = -2, K = 0},false; {I = 2, J = -1, K = 1},false; {I = 2, J = 0, K = 2},true; {I = 2, J = 1, K = 3},true; {I = 2, J = 2, K = 4},false; {I = 3, J = -2, K = 1},false; {I = 3, J = -1, K = 2},false; {I = 3, J = 0, K = 3},false; {I = 3, J = 1, K = 4},false; {I = 4, J = -2, K = 2},false; {I = 4, J = -1, K = 3},false; {I = 4, J = 0, K = 4},false; }
         */
        HexEngine engine = new HexEngine(3);
        System.out.println(engine);
        Piece piece = Piece.generatePiece();
        engine.add(engine.checkPositions(piece).get(1), piece);
        System.out.println(piece);
        System.out.println(engine);
        Piece piece2 = Piece.generatePiece();
        System.out.println();
        System.out.println(piece2);
        System.out.println(engine.checkPositions(piece2).size() + " out of " + engine.length() + " positions available.");
        engine.add(engine.checkPositions(piece2).get(0), piece2);
        System.out.println(engine);
        Piece piece3 = Piece.generatePiece();
        System.out.println();
        System.out.println(piece3);
        System.out.println(engine.checkPositions(piece3).size() + " out of " + engine.length() + " positions available.");
        engine.add(engine.checkPositions(piece3).get(0), piece3);
        System.out.println(engine);
        System.out.println("After elimination");
        System.out.println(engine.eliminate());
        System.out.println(engine);
    }
}
