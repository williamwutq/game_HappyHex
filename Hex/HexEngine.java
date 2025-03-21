package Hex;

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
        HexEngine engine = new HexEngine(7);
        System.out.println(engine);
        System.out.println(engine.inRange(8,11));
        System.out.println(engine.getBlock(8,11));
        Piece piece = Piece.generatePiece();
        engine.add(Hex.hex(3, 2), piece);
        System.out.println(piece);
        System.out.println(engine);
    }
}
