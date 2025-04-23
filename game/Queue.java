package game;

import hex.Piece;

/**
 * Represents a simple fixed-length queue of {@code Piece} objects.
 * <p>
 * This queue handles an "insignificant" form of queuing — that is,
 * it always maintains a fixed-size array of elements, and whenever
 * an element is removed (via {@link #next()} or {@link #fetch(int)}),
 * a new element is immediately generated to fill the gap.
 * <p>
 * {@link Piece} objects are always used as reference to static pieces,
 * so practically the {@code Queue} only store an array of references.
 * There is no differentiation between deep and shallow copies.
 * <p>
 * It supports peeking and consuming elements from the front or
 * at specific indices (with shifting), but does not allow adding
 * externally-defined elements. New pieces are created via an
 * internal {@link #generate()} method.
 * @since 0.6
 * @author William Wu
 * @version 1.2
 */
public class Queue{
    /** Internal fixed-size array of Piece elements. */
    private Piece[] pieces;

    // Special
    /**
     * The current {@link special.SpecialFeature} instance responsible for modifying the piece generation logic.
     * The default feature will allow piece to be generated directly via the {@link #generate()} method.
     * This can be replaced dynamically at runtime to alter how pieces are generated in the system.
     * @see special.SpecialFeature
     * @see special.FeatureFactory
     * @since 1.1
     */
    private static special.SpecialFeature pieceProcessor = special.FeatureFactory.createFeature();
    /**
     * Replaces the current piece generation logic handler with a new {@code SpecialFeature}.
     * <p>
     * This method allows dynamic modification of how pieces are generated or processed
     * during the application's execution by injecting a new implementation of {@code SpecialFeature}.
     *
     * @param feature the new {@link special.SpecialFeature} to use for piece generation logic
     * @since 1.1
     */
    public static void changePieceProcessor(special.SpecialFeature feature){
        pieceProcessor = feature;
    }
    /**
     * Retrieves the unique identifier of the currently active piece generation feature.
     * This identifier can be used to distinguish between different {@code SpecialFeature} implementations or configurations at runtime.
     *
     * @return the feature ID of the current {@code pieceProcessor}
     * @see special.SpecialFeature
     * @since 1.1
     */
    public static int getPieceProcessorID(){
        return pieceProcessor.getFeatureID();
    }

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
        Piece generated = generate(next);
        for(int i = 1; i < pieces.length; i ++){
            pieces[i - 1] = pieces[i];
        }
        pieces[pieces.length - 1] = generated;
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
            Piece generated = generate(fetch);
            for(int i = index + 1; i < pieces.length; i ++){
                pieces[i - 1] = pieces[i];
            }
            pieces[pieces.length - 1] = generated;
            return fetch;
        }
    }
    /**
     * Generates a new {@link Piece} according to piece generation logic.
     * @return a newly generated {@code Piece}.
     * @see PieceFactory#generatePiece()
     * @see #generate(Piece)
     */
    protected Piece generate(){
        return (Piece) pieceProcessor.process(new Object[]{PieceFactory.generatePiece(), Launcher.LaunchEssentials.isEasyMode(), GUI.GameEssentials.engine()})[0];
    }
    /**
     * Generates a new {@link Piece} according to piece generation logic.
     * @param dequeued the {@code Piece} that is dequeued from the queue.
     * @return a newly generated {@code Piece}.
     * @see PieceFactory#generatePiece()
     * @see #generate()
     * @since 1.1
     */
    protected Piece generate(Piece dequeued){
        return (Piece) pieceProcessor.process(new Object[]{PieceFactory.generatePiece(), Launcher.LaunchEssentials.isEasyMode(), GUI.GameEssentials.engine(), dequeued})[0];
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
    /**
     * Returns an identical clone of this {@code Queue}.
     * Each {@link Piece} object contained in this instance is copied by reference.
     * Since {@code Queue} change its pieces by modifying references, this does not affect any operations.
     * @return a copy of this {@code Queue} object.
     * @since 1.1
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
