package Hex;

public class Queue{
    private Piece[] pieces;
    // Special
    private static special.SpecialFeature pieceProcessor = special.FeatureFactory.createFeature();
    public static void changePieceProcessor(special.SpecialFeature feature){
        pieceProcessor = feature;
    }
    public static int getPieceProcessorID(){
        return pieceProcessor.getFeatureID();
    }

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
    public void reset(){
        // Regenerate all pieces
        for (int i = 0; i < pieces.length; i ++){
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
        return (Piece) pieceProcessor.process(new Object[]{Piece.generatePiece(), Launcher.LaunchEssentials.isEasyMode(), GUI.GameEssentials.engine()})[0];
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
    public String toString(){
        StringBuilder result = new StringBuilder("Queue[");
        for (int i = 0; i < length(); i ++){
            result.append(pieces[i].toString());
        }
        return result + "]";
    }
}
