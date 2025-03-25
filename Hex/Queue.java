public class Queue{
    private Piece[] pieces;
    public Queue(int size){
        if (size < 1){
            size = 1;
        }
        pieces = new Piece[size];
        // Generate Pieces
        for (int i = 0; i < size; i ++){
            pieces[i] = generate();
        }
    }
    public Piece next(){
        Piece next = getFirst();
        for(int i = 1; i < pieces.length; i ++){
            pieces[i - 1] = pieces[i];
        }
        pieces[pieces.length - 1] = generate();
        return next;
    }
    public Piece fetch(int index){
        if(index == -1){
            return fetch(pieces.length - 1);
        } else if(index < -1 || index >= pieces.length){
            throw new IndexOutOfBoundsException("Index " + index + " out of bound for length " + pieces.length + ".");
        } else {
            Piece fetch = pieces[index];
            for(int i = index + 1; i < pieces.length; i ++){
                pieces[i - 1] = pieces[i];
            }
            pieces[pieces.length - 1] = generate();
            return fetch;
        }
    }
    protected Piece generate(){
        return Piece.generatePiece();
    }
    public Piece getFirst(){
        return get(0);
    }
    public Piece getLast(){
        return get(pieces.length - 1);
    }
    public Piece get(int index){
        if(index == -1){
            return getLast();
        } else if(index < -1 || index >= pieces.length){
            throw new IndexOutOfBoundsException("Index " + index + " out of bound for length " + pieces.length + ".");
        } else {
            return pieces[index];
        }
    }
    public int length() {
        return pieces.length;
    }
}
