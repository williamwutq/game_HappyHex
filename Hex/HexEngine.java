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
                nb.moveI(a);
                nb.moveK(b);
                if(nb.inRange(radius)){
                    blocks[i] = nb;
                    i ++;
                }
            }
        }
        // To be implemented further
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
            // Implement logic
            return blocks[0]; // Placeholder
        }else{
            return null;
        }
    }
    public void add(Block origin, HexGrid other){
        // To be implemented
    }
    public String toString(){
        StringBuilder str = new StringBuilder("{HexEngine: ");
        for (Block block : blocks) {
            str.append(block);
        }
        return str + "}";
    }
    public static void main(String[] args){
        HexEngine engine = new HexEngine(7);
        System.out.println(engine);
    }
}
