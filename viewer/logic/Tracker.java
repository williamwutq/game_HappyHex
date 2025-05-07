package viewer.logic;

import hex.Hex;
import hex.HexEngine;
import hex.Piece;
import hexio.HexLogger;
import java.util.Arrays;
import java.io.IOException;

public class Tracker {
    private final HexEngine[] engines;
    private final Hex[] origins;
    private final Piece[][] queues;
    private final int[] scores;
    private final int length;
    /**
     * Create a {@code Tracker} object to track the progression of a game by reading game
     * information from a {@link HexLogger}. This logger need to contain valid game information,
     * otherwise the object will not be created and instead a {@link IllegalArgumentException}
     * will be thrown.
     * <p>
     * This tracker will track all game components, including the game {@link HexEngine board},
     * {@link Piece} queue, {@link Hex coordinate} of piece placements, and scores.
     * It records {@code length + 1} individual game states, because
     * the initial state is also included in additions to moves in the game.
     * <p>
     * All the recorded game information will be stored in instance variables in
     * this {@code Tracker}, which can be accessed through getter methods.
     *
     * @param logger the logger with valid game data stored inside (This means you need to call
     *               {@link HexLogger#read} or {@link HexLogger#readBinary} in advance).
     * @throws IllegalArgumentException if reading game data from logger failed
     */
    public Tracker(HexLogger logger) throws IllegalArgumentException{
        this.length = logger.getTurn() + 1;
        this.engines = new HexEngine[length];;
        this.queues = new Piece[length][logger.getQueue().length];
        this.scores = new int[length];
        this.origins = new Hex[length];
        // Populate
        scores[0] = 0;
        engines[0] = new HexEngine(logger.getEngine().getRadius());
        queues[length - 1] = logger.getQueue();
        System.arraycopy(logger.getMoveQueues(), 0, queues, 0, length - 1);
        System.arraycopy(logger.getMoveOrigins(), 0, origins, 1, length - 1);
        for (int turn = 1; turn < length; turn ++){
            HexEngine duplicatedEngine = engines[turn-1].clone();
            try{
                Piece piece = logger.getMovePieces()[turn-1];
                duplicatedEngine.add(origins[turn], piece);
                scores[turn] += piece.length();
                scores[turn] += 5 * duplicatedEngine.eliminate().length;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tracker cannot be created because error occurred reading game data, ", e.getCause());
            }
            engines[turn] = duplicatedEngine;
        }
    }
    /**
     * Returns a string representation of this {@code Tracker}, including the number of states tracked,
     * and a detailed breakdown of each game state including the score, engine, queue, and origin.
     * The format is:
     * <pre>Tracker[length = X, states = {{score = A, engine = ..., queue = [...], origin = ...}, ...}]</pre>
     * @return a string summarizing the tracker's recorded game states
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tracker[length = ").append(length).append(", states = {");

        for (int i = 0; i < length; i++) {
            sb.append("{score = ").append(scores[i]);
            sb.append(", engine = ").append(engines[i]);
            sb.append(", queue = ").append(Arrays.toString(queues[i]));
            if (i > 0) {
                sb.append(", origin = ").append(origins[i - 1]);
            } else {
                sb.append(", origin = null");
            }
            sb.append("}");
            if (i < length - 1) {
                sb.append(", ");
            }
        }

        sb.append("}]");
        return sb.toString();
    }
    /**
     * Returns game {@link HexEngine board} array stored in the {@code Tracker}, containing completed game board data
     * across all game states by chronological order, starting from the initial state, where the {@code board}
     * will be {@link HexEngine#HexEngine(int) empty}.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@link HexEngine} representing the game board across all game states
     */
    public HexEngine[] engines() {
        return engines.clone();
    }
    /**
     * Returns {@link Hex coordinate} array stored in the {@code Tracker}, containing {@link Piece} placement
     * {@code coordinates} in the {@link HexEngine} across all game states by chronological order, starting
     * from the initial state, where the placement coordinate will be {@code null}.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@link Hex coordinates} representing the game piece placement across all game states
     */
    public Hex[] origins() {
        return origins.clone();
    }
    /**
     * Returns {@link Piece} queue array stored in the {@code Tracker}, containing {@code Piece} queues
     * across all game states by chronological order, starting from the initial state.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@code Piece} arrays representing the game queue across all game states
     */
    public Piece[][] queues() {
        return queues.clone();
    }
    /**
     * Returns score array stored in the {@code Tracker}, containing scores across all game states
     * by chronological order, starting from the initial state, where score is 0.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@code int} representing the game score across all game states
     */
    public int[] scores() {
        return scores.clone();
    }
    /**
     * Returns the total length of game states stored in the {@code Tracker}, including the initial game state.
     * This value will be one greater than the total game moves.
     *
     * @return the total length of all game states, which is the length of all arrays in this {@code Tracker}
     */
    public int length() {
        return length;
    }
    /**
     * Returns the game {@link HexEngine board} at a specific indexed game state stored in the {@code Tracker}.
     *
     * @param index the index of the game state, with 0 representing the initial state and {@link #length}-1
     *              representing the final game state. Index must be valid.
     * @return a {@link HexEngine} representing the game board in the indexed game state
     * @throws IndexOutOfBoundsException if the index is not in range [0, length)
     * @see #engines()
     */
    public HexEngine engineAt(int index) throws IndexOutOfBoundsException{
        checkIndex(index);
        return engines[index];
    }
    /**
     * Returns the game {@link Piece} placement {@link Hex coordinate} at
     * a specific indexed game state stored in the {@code Tracker}.
     *
     * @param index the index of the game state, with 0 representing the initial state and {@link #length}-1
     *              representing the final game state. Index must be valid
     * @return a {@link Hex} coordinate representing the game piece placement in the indexed game state
     * @throws IndexOutOfBoundsException if the index is not in range [0, length)
     * @see #origins()
     */
    public Hex originAt(int index) {
        checkIndex(index);
        return origins[index];
    }
    /**
     * Returns the game {@link Piece} queue at a specific indexed game state stored in the {@code Tracker}.
     *
     * @param index the index of the game state, with 0 representing the initial state and {@link #length}-1
     *              representing the final game state. Index must be valid
     * @return an array of {@link Piece} representing the game {@code Piece} queue in the indexed game state
     * @throws IndexOutOfBoundsException if the index is not in range [0, length)
     * @see #queues()
     */
    public Piece[] queueAt(int index) {
        checkIndex(index);
        return queues[index];
    }
    /**
     * Returns the game score at a specific indexed game state stored in the {@code Tracker}.
     *
     * @param index the index of the game state, with 0 representing the initial state and {@link #length}-1
     *              representing the final game state. Index must be valid
     * @return an integer representing the game score queue in the indexed game state
     * @throws IndexOutOfBoundsException if the index is not in range [0, length)
     * @see #scores()
     */
    public int scores(int index) {
        checkIndex(index);
        return scores[index];
    }
    /**
     * Check whether the index passed in for accessing elements in this game {@code Tracker} is valid.
     * If the passed in index is not valid, it will throw an {@link IndexOutOfBoundsException}. This method is intended
     * to catch out-of-bounds index early to avoid further problems.
     *
     * @param index the index to check
     * @throws IndexOutOfBoundsException if the index is not in range [0, length)
     */
    private void checkIndex(int index) throws IndexOutOfBoundsException{
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds for length " + length);
        }
    }
    public static void main(String[] args) throws IOException {
        HexLogger logger = HexLogger.generateBinaryLoggers().getFirst();
        logger.readBinary();
        Tracker tracker = new Tracker(logger);
        System.out.println(tracker);
    }
}
