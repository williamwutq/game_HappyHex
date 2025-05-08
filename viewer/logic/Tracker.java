/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package viewer.logic;

import hex.Block;
import hex.Hex;
import hex.HexEngine;
import hex.Piece;
import hexio.HexLogger;
import java.util.Arrays;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code Tracker} class represents a comprehensive state tracker for the HappyHex game involving a
 * game {@link HexEngine board}, a queue of {@link Piece} objects, game scores, and placement coordinates
 * on a hexagonal grid ({@link Hex}).
 * <p>
 * This tracker is initialized using a {@link HexLogger}, which must contain a complete and valid sequence
 * of game data, including the initial game state and all subsequent moves. This means that the logger must
 * have read a valid file by either calling {@link HexLogger#read()} or {@link HexLogger#readBinary()}.
 * Upon construction, the {@code Tracker} internally simulates all moves in sequence to reconstruct each
 * full game state, capturing the evolution of the board, queue states, placement positions, and score
 * progressions over time. It defaults all {@link Block} colors to default colors but highlight recent changes.
 * <p>
 * Unlike {@link HexLogger}, which stores dynamic game data using {@code ArrayLists} primarily for I/O purposes,
 * this class contains fixed, immutable data. While duplicating {@code HexLogger} instances is not recommended
 * due to potential file access conflicts, it is safe and efficient to create multiple identical {@code Tracker}
 * instances from the same game data from a single {@code HexLogger}. Furthermore, {@code HexLogger} optimizes
 * for storage by recording only delta (change; game move) values, whereas {@code Tracker} maintains the full
 * set of data. This makes {@code Tracker} slower to initialize but significantly more efficient when accessing
 * specific game states in the past, thanks to its internal caching mechanism.
 * <p>
 * The tracker maintains the following data for each state:
 * <ul>
 *     <li>The state of the {@link HexEngine} board.</li>
 *     <li>The queue of {@link Piece} objects (with color index annotations for added and eliminated pieces).</li>
 *     <li>The origin {@link Hex} coordinate of each piece placement (null for the initial setup).</li>
 *     <li>The cumulative score after each turn, calculated based on the length of placed pieces and eliminations.</li>
 * </ul>
 * <p>
 * This class allows for retrieving data in full (e.g., all game boards via {@link #engines()}) or at specific
 * points in time (e.g., board at a given move via {@link #engineAt(int)}). A {@link #getPointer() pointer} is also
 * included with convenient methods to {@link #advancePointer() advance} and {@link #decrementPointer() decrement}
 * it. Build-in methods (e.g., board at pointer move via {@link #engine()}) to get data at the game state pointed
 * by the pointer will not cause {@link IndexOutOfBoundsException}. The total number of states is always one more
 * than the number of moves to include the initial game state.
 * <p>
 * This class also applies color indices for visual or UI highlighting:
 * <ul>
 *     <li>Newly added or active {@link Piece} objects are assigned color index {@code 0}.</li>
 *     <li>Eliminated blocks are assigned color index {@code 1}.</li>
 * </ul>
 * <p>
 * Attempting to construct a {@code Tracker} with invalid logger data will result in an {@link IllegalArgumentException}.
 * Index-based methods validate bounds and throw {@link IndexOutOfBoundsException} if accessed improperly.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see HexEngine
 * @see HexLogger
 * @see Piece
 * @see Hex
 */
public class Tracker {
    private final HexEngine[] engines;
    private final Hex[] origins;
    private final Piece[][] queues;
    private final int[] scores;
    private final int length;
    private int pointer;

    // Constructor
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
     * <p>
     * Recent changes, such as selected {@link Piece} in the piece queue, placed piece in the
     * {@link HexEngine engine}, or eliminated changes, will be applied separate color for
     * highlighting. Specifically, added or to be added pieces will receive color index 0 and
     * eliminated blocks will receive color index 1.
     *
     * @param logger the logger with valid game data stored inside (This means you need to call
     *               {@link HexLogger#read} or {@link HexLogger#readBinary} in advance).
     * @throws IllegalArgumentException if reading game data from logger failed
     */
    public Tracker(HexLogger logger) throws IllegalArgumentException{
        this.pointer = 0;
        this.length = logger.getTurn() + 1;
        this.engines = new HexEngine[length];;
        this.queues = new Piece[length][logger.getQueue().length];
        this.scores = new int[length];
        this.origins = new Hex[length];
        // Populate
        scores[0] = 0;
        engines[0] = new HexEngine(logger.getEngine().getRadius());
        queues[length - 1] = logger.getQueue();
        Piece[][] loggerQueues = logger.getMoveQueues();
        int[] loggerIndexes = logger.getMovePieceIndexes();
        for (int turn = 0; turn < length - 1; turn ++){
            Piece[] queue = loggerQueues[turn];
            for (int i = 0; i < queue.length; i++) {
                if (loggerIndexes[turn] == i){
                    queue[i].setColor(0);
                } else {
                    queue[i].setColor(-2);
                }
            }
            queues[turn] = queue;
        }
        System.arraycopy(logger.getMoveOrigins(), 0, origins, 1, length - 1);
        for (int turn = 1; turn < length; turn ++){
            HexEngine existingEngine = engines[turn - 1];
            HexEngine duplicatedEngine = new HexEngine(existingEngine.getRadius());
            for(int i = 0; i < duplicatedEngine.length(); i ++){
                duplicatedEngine.setState(i, existingEngine.getBlock(i).getState());
            }
            try{
                Piece piece = logger.getMovePieces()[turn-1];
                piece.setColor(0);
                duplicatedEngine.add(origins[turn], piece);
                Block[] eliminated = duplicatedEngine.eliminate();
                for (Block block : eliminated){
                    duplicatedEngine.getBlock(block.getLineI(), block.getLineK()).setColor(1);
                }
                scores[turn] += piece.length();
                scores[turn] += 5 * eliminated.length;
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

    // Getters
    /**
     * Returns game {@link HexEngine board} array stored in the {@code Tracker}, containing completed game board data
     * across all game states by chronological order, starting from the initial state, where the {@code board}
     * will be {@link HexEngine#HexEngine(int) empty}.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@link HexEngine} representing the game board across all game states
     * @see #engine
     * @see #engineAt
     */
    public final HexEngine[] engines() {
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
     * @see #origin
     * @see #originAt
     */
    public final Hex[] origins() {
        return origins.clone();
    }
    /**
     * Returns {@link Piece} queue array stored in the {@code Tracker}, containing {@code Piece} queues
     * across all game states by chronological order, starting from the initial state.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@code Piece} arrays representing the game queue across all game states
     * @see #queue
     * @see #queueAt
     */
    public final Piece[][] queues() {
        return queues.clone();
    }
    /**
     * Returns score array stored in the {@code Tracker}, containing scores across all game states
     * by chronological order, starting from the initial state, where score is 0.
     * <p>
     * The length of the array returned will be {@link #length}.
     *
     * @return an array of {@code int} representing the game score across all game states
     * @see #score
     * @see #scoreAt
     */
    public final int[] scores() {
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
     * @throws IndexOutOfBoundsException if the index is not in range (0, length)
     * @see #origins()
     */
    public Hex originAt(int index) {
        if (index == 0) throw new ArrayIndexOutOfBoundsException("Index 0 out of bounds because move at game state 0 does not exist");
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
    public int scoreAt(int index) {
        checkIndex(index);
        return scores[index];
    }
    /**
     * Returns the game {@link HexEngine board} at the game state represented by the {@link #getPointer() pointer}.
     *
     * @return a {@link HexEngine} representing the game board in the game state represented by the pointer.
     * @see #engines()
     */
    public final HexEngine engine(){
        return engines[pointer];
    }
    /**
     * Returns the game {@link Piece} placement {@link Hex coordinate} at
     * the game state represented by the {@link #getPointer() pointer}.
     *
     * @return a {@link Hex} coordinate representing the game piece placement in the game state represented by the pointer.
     * @see #origins()
     */
    public final Hex origin() {
        return origins[pointer];
    }
    /**
     * Returns the game {@link Piece} queue at the game state represented by the {@link #getPointer() pointer}.
     *
     * @return an array of {@link Piece} representing the game {@code Piece} queue in the game state represented by the pointer.
     * @see #queues()
     */
    public final Piece[] queue() {
        return queues[pointer];
    }
    /**
     * Returns the game score at the game state represented by the {@link #getPointer() pointer}.
     *
     * @return an integer representing the game score queue in the game state represented by the pointer.
     * @see #scores()
     */
    public final int score() {
        return scores[pointer];
    }

    // Pointer
    /**
     * Returns the current pointer pointing to a specific game state in the {@code Tracker}.
     *
     * @return the current pointer pointing to a specific game state in the {@code Tracker}
     */
    public final int getPointer(){
        return pointer;
    }
    /**
     * Advances the current pointer pointing to a specific game state in the {@code Tracker}.
     * If the pointer cannot advance due to reaching its end, it will stay at {@link #length} - 1.
     *
     * @return whether the pointer is advanced
     */
    public final boolean advancePointer(){
        if(pointer < length - 1){
            pointer++;
            return true;
        } else return false;
    }
    /**
     * Decrement the current pointer pointing to a specific game state in the {@code Tracker}.
     * If the pointer cannot advance due to reaching its end, it will stay at 0.
     *
     * @return whether the pointer is decremented
     */
    public final boolean decrementPointer(){
        if(pointer > 0){
            pointer--;
            return true;
        } else return false;
    }
    /**
     * Sets the current pointer pointing to a specific game state in the {@code Tracker}.
     *
     * @param pointer the new pointer pointing to the desired game state in the {@code Tracker}, which is a valid index
     * @throws IndexOutOfBoundsException if the index is not in range [0, length)
     */
    public final void setPointer(int pointer){
        checkIndex(pointer);
        this.pointer = pointer;
    }

    // Equality
    /**
     * Returns true if the other object represent the same game as this {@link Tracker}.
     * The equality concerns about the values in the game {@link #engines}, {@link #queues}, piece placement
     * {@link #origins positions}, and {@link #scores} for each turn. Pointer position does not matter.
     *
     * @param object the reference object with which to compare.
     * @return true if the other object represent the exact same game as this {@code Tracker}, false otherwise
     */
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Tracker tracker)) return false;
        return length == tracker.length
                && Objects.deepEquals(engines, tracker.engines) && Objects.deepEquals(origins, tracker.origins)
                && Objects.deepEquals(queues, tracker.queues) && Objects.deepEquals(scores, tracker.scores);
    }
    /**
     * Returns a hash code value for this {@code Tracker} object.
     * The value concerns about the values in the game {@link #engines}, {@link #queues}, piece placement
     * {@link #origins positions}, and {@link #scores} for each turn.
     *
     * @return the generated hash code value for this {@code Tracker}
     */
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(engines), Arrays.hashCode(origins), Arrays.deepHashCode(queues), Arrays.hashCode(scores));
    }

    public static void main(String[] args) throws IOException {
        int index = 0;
        try{
            index = Integer.parseInt(args[0]);
        } catch (Exception e) {}
        // Test main
        HexLogger logger = HexLogger.generateBinaryLoggers().get(index);
        logger.readBinary();
        Tracker tracker = new Tracker(logger);
        viewer.graphics.frame.GamePanel panel = new viewer.graphics.frame.GamePanel(tracker.engineAt(0), tracker.queueAt(0));
        viewer.Viewer.test(panel);
        for (int i = 0; i < tracker.length(); i ++){
            try{
                Thread.sleep(400);
            } catch (InterruptedException e){}
            tracker.advancePointer();
            panel.setEngine(tracker.engine());
            panel.setQueue(tracker.queue());
        }
    }
}
