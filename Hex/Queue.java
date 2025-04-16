package Hex;

/**
 * Represents a simple fixed-length queue of {@code Piece} objects.
 * <p>
 * This queue handles an "insignificant" form of queuing — that is,
 * it always maintains a fixed-size array of elements, and whenever
 * an element is removed (via {@link #next()} or {@link #fetch(int)}),
 * a new element is immediately generated to fill the gap.
 * <p>
 * It supports peeking and consuming elements from the front or
 * at specific indices (with shifting), but does not allow adding
 * externally-defined elements. New pieces are created via an
 * internal {@link #generate()} method.
 */
public class Queue{
    /** Internal fixed-size array of Piece elements. */
    private Piece[] pieces;
    /**
     * Constructs a new {@code Queue} with a given size. If the size is less than 1, it defaults to 1.
     * @param size the number of elements to hold in the queue.
     */
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
    /**
     * Regenerates all {@code Piece} objects in the queue. This resets the queue without changing its size.
     * @see #generate()
     */
    public void reset(){
        for (int i = 0; i < pieces.length; i ++){
            pieces[i] = generate();
        }
    }
    /**
     * Removes and returns the first {@link Piece} in the queue.
     * The remaining elements shift left by one, and a new {@code Piece} is generated and added to the end.
     * @return the first {@link Piece} in the queue.
     * @see #generate()
     */
    public Piece next(){
        Piece next = getFirst();
        for(int i = 1; i < pieces.length; i ++){
            pieces[i - 1] = pieces[i];
        }
        pieces[pieces.length - 1] = generate();
        return next;
    }
    /**
     * Removes and returns the {@link Piece} at the specified index.
     * Elements after the index shift left, and a new {@code Piece} is generated and appended at the end.
     *
     * @param index the position of the {@code Piece} to fetch; -1 refers to the last element.
     * @return the {@link Piece} at the specified index.
     * @throws IndexOutOfBoundsException if index is less than -1 or greater than or equal to the queue {@link #length()}.
     * @see #generate()
     */
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
    /**
     * Generates a new {@link Piece} according to piece generation logic.
     * @return a newly generated {@code Piece}.
     * @see Piece#generatePiece()
     */
    protected Piece generate(){
        return Piece.generatePiece();
    }
    /**
     * Returns the first {@link Piece} in the queue without removing it.
     * @return the first {@code Piece}.
     * @see #get(int)
     */
    public Piece getFirst(){
        return get(0);
    }
    /**
     * Returns the last {@link Piece} in the queue without removing it.
     * @return the last {@code Piece}.
     * @see #get(int)
     */
    public Piece getLast(){
        return get(pieces.length - 1);
    }
    /**
     * Returns the {@code Piece} at the specified index without removing it.
     * @param index the index of the piece to retrieve; -1 refers to the last element.
     * @return the {@code Piece} at the given index.
     * @throws IndexOutOfBoundsException if index is less than -1 or greater than or equal to the queue {@link #length()}.
     */
    public Piece get(int index){
        if(index == -1){
            return getLast();
        } else if(index < -1 || index >= pieces.length){
            throw new IndexOutOfBoundsException("Index " + index + " out of bound for length " + pieces.length + ".");
        } else {
            return pieces[index];
        }
    }
    /**
     * Returns the fixed length of the queue, which does not change over the lifetime of the object.
     * @return the number of pieces in the queue.
     * @see #fetch(int)
     * @see #get(int)
     */
    public int length() {
        return pieces.length;
    }
    /**
     * Returns a string representation of the queue.
     * @return a string in the format {@code Queue[Pieces]}.
     */
    public String toString(){
        StringBuilder result = new StringBuilder("Queue[");
        for (int i = 0; i < length(); i ++){
            result.append(pieces[i].toString());
        }
        return result + "]";
    }
}
