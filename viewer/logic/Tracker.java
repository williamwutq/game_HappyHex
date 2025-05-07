package viewer.logic;

import hex.Hex;
import hex.HexEngine;
import hex.Piece;
import hexio.HexLogger;
import java.util.Arrays;
import java.io.IOException;

public class Tracker {
    HexEngine[] engines;
    Hex[] origins;
    Piece[][] queues;
    int[] scores;
    int length;
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
    public static void main(String[] args) throws IOException {
        HexLogger logger = HexLogger.generateBinaryLoggers().getFirst();
        logger.readBinary();
        Tracker tracker = new Tracker(logger);
        System.out.println(tracker);
    }
}
