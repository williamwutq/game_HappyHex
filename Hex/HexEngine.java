package Hex;

class HexEngine implements HexGrid{
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
        return Block.block(i, k).inRange(radius);
    }
    public Block getBlock(int i, int k){
        if(inRange(i, k)){
            return search(i, k, 0, length()-1); // private binary search
        }else{
            return null;
        }
    }
    private Block search(int i, int k, int start, int end){
        if(start > end){return null;}
        int middleIndex = (start + end)/2;
        Block middle = blocks[middleIndex];
        if(middle.getLineI() == i && middle.getLineK() == k){
            return middle;
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
    public void add(Block origin, HexGrid other){
        // To be implemented
    }
    public String toString(){
        StringBuilder str = new StringBuilder("{HexEngine: ");
        for (Block block : blocks) {
            str.append(block.getLines());
        }
        return str + "}";
    }
    public static void main(String[] args){
        HexEngine engine = new HexEngine(7);
        System.out.println(engine);
        System.out.println(engine.inRange(8,11));
        System.out.println(engine.getBlock(8,11));
    }
}
