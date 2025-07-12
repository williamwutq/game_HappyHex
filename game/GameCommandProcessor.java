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

package game;

import comm.CommandProcessor;
import hex.HexEngine;
import hex.Piece;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@code GameCommandProcessor} is a command execution engine that mediates between a game GUI
 * and an external command source (callback processor). It supports an "autoplay" mode during
 * which it continuously queries the game state and relays it in a standardized command format
 * to a callback processor at fixed intervals.
 * <p>
 * This class is designed to be:
 * <ul>
 *     <li>Thread-safe: Critical sections are protected using appropriate locks.</li>
 *     <li>Non-reentrant: The querying process is serialized using internal state flags and timing checks.</li>
 *     <li>Lifecycle-aware: It supports controlled startup and shutdown via {@link #run()} and {@link #close()}.</li>
 * </ul>
 * <p>
 * Autoplay Query Loop:
 * <ul>
 *     <li>Once {@link #run()} is invoked, the processor sets a flag to indicate autoplay is active.</li>
 *     <li>Subsequent calls to {@link #query()} will poll the {@link GameGUIInterface} for game state,
 *     encode it into a move command, and dispatch it to the callback processor.</li>
 *     <li>If the previous query is still running or the delay interval has not passed,
 *     it reschedules itself after a small delay ({@code checkDelay}).</li>
 *     <li>Query execution is re-triggered after each completed move command (via {@link #execute}).</li>
 * </ul>
 * <p>
 * Thread Safety:
 * <ul>
 *     <li>{@code callbackProcessorLock}: synchronizes access to the callback command processor.</li>
 *     <li>{@code queryLock}: synchronizes query control flags to prevent overlapping or premature queries.</li>
 *     <li>{@code isAutoplayRunning}: an atomic flag guarding all autoplay operations and lifecycle state.</li>
 *     <li>{@code gameGUI} access is also synchronized to ensure UI interactions remain thread-safe.</li>
 * </ul>
 * <p>
 * Usage Warning:
 * <ul>
 *     <li>{@link #setCallBackProcessor(CommandProcessor)} must be called before {@link #query()} is invoked,
 *     or a runtime exception will be thrown.</li>
 *     <li>This class should not be cloned or passed by value — always pass it by reference.</li>
 * </ul>
 * <p>
 * Usage example:
 * This example delegate the work of placing the pieces to an external resource wrapped in a delegate processor,
 * and the GameCommandProcessor is used to facilitate communication between the two.
 * <pre>{@code
 * // Create GUI interface
 * GameGUIInterface gui = ...;
 *
 * // Create an instance of the GameCommandProcessor
 * GameCommandProcessor processor = new GameCommandProcessor(gui);
 *
 * // Create a processor delegate work to other resource, executor, or process
 * CommandProcessor delegate = ...;
 *
 * // Set callback as each other
 * processor.setCallBackProcessor(delegate);
 * delegate.setCallBackProcessor(processor);
 *
 * // Start autoplay
 * processor.run();
 *
 * // Optionally, call query() manually or rely on automatic rescheduling in the main method
 * processor.query();
 * ... // Some other code
 *
 * // Make sure to shutdown autoplay when finished
 * processor.close();
 * }</pre>
 *
 * @version 1.4
 * @author William Wu
 * @since 1.4
 */
public class GameCommandProcessor implements CommandProcessor, Runnable, AutoCloseable {
    private CommandProcessor callBackProcessor;
    private final GameGUIInterface gameGUI;
    private ScheduledExecutorService scheduler;
    private boolean isQueryCompleted = true;
    private final AtomicBoolean isAutoplayRunning = new AtomicBoolean(false);
    private final Object callbackProcessorLock = new Object();
    private final Object queryLock = new Object();
    private long lastQueryTime = 0;
    private static long queryDelay = 250; // Delay between queries in milliseconds
    private static final long checkDelay = 10; // Delay to check statues again when last query did not complete

    /**
     * Constructs a new GameCommandProcessor instance.
     *
     * @param gameGUI the game GUI interface used to query engine state and perform moves.
     */
    public GameCommandProcessor(GameGUIInterface gameGUI){
        scheduler = Executors.newSingleThreadScheduledExecutor();
        callBackProcessor = null;
        this.gameGUI = gameGUI;
    }
    /**
     * Returns the current callback {@link CommandProcessor} used to receive move commands.
     *
     * @return the current callback processor
     */
    @Override
    public CommandProcessor getCallBackProcessor(){
        synchronized (callbackProcessorLock){
            return callBackProcessor;
        }
    }
    /**
     * Sets the callback processor to which move commands will be delegated.
     *
     * @param processor the processor to set as callback
     * @throws IllegalArgumentException if the processor is the same as this instance
     * @throws UnsupportedOperationException never thrown in this implementation
     */
    @Override
    public void setCallBackProcessor(CommandProcessor processor) throws IllegalArgumentException, UnsupportedOperationException {
        if (this == processor){
            throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        } else synchronized (callbackProcessorLock) {
            callBackProcessor = processor;
        }
    }
    /**
     * Initiates a query operation.
     * <p>
     * This method does the following:
     * <ul>
     *     <li>Checks if autoplay is running. If not, the method returns immediately.</li>
     *     <li>Within {@code queryLock}, checks whether the previous query is complete and whether enough time has elapsed.</li>
     *     <li>If ready, builds a query string from the current game state and sends it to the callback processor via {@link #execute}.</li>
     *     <li>If not ready, schedules another attempt after {@code checkDelay} ms.</li>
     * </ul>
     *
     * @throws InterruptedException if the query thread is interrupted
     * @throws IllegalStateException if the callback processor is not yet initialized
     */
    public void query() throws InterruptedException {
        if (!isAutoplayRunning.get()) {
            // Do not query if autoplay is closed.
            return;
        }
        synchronized (queryLock) {
            if (!isQueryCompleted || System.currentTimeMillis() - lastQueryTime < queryDelay) {
                // Schedule a new query if previous query is still processing or delay has not passed.
                scheduler.schedule(() -> {
                    try {
                        query();
                    } catch (InterruptedException e) {
                        close();
                    }
                }, checkDelay, TimeUnit.MILLISECONDS);
            } else {
                // Start a new query
                isQueryCompleted = false;
                lastQueryTime = System.currentTimeMillis();
            }
        }
        String queryString;
        synchronized (gameGUI){
            HexEngine engine = gameGUI.getEngine();
            Piece[] queue = gameGUI.getQueue();
            queryString = "move " + getEngineString(engine) + " " + getQueueString(queue);
        }
        synchronized (callbackProcessorLock){
            if (callBackProcessor == null) {
                throw new IllegalStateException("Callback processor is not properly initialized");
            } else {
                callBackProcessor.execute(queryString);
            }
        }
    }

    /**
     * Starts the autoplay lifecycle.
     * <p>
     * This method prepares the internal {@link ScheduledExecutorService} scheduler
     * and sets the autoplay running flag. Once this method is called, the system becomes
     * responsive to {@link #query()} calls and accepts incoming move commands.</p>
     */
    public void run(){
        scheduler = Executors.newSingleThreadScheduledExecutor();
        isAutoplayRunning.set(true);
    }
    /**
     * Stops the autoplay lifecycle.
     * <p>
     * This method shuts down the scheduler (if active), interrupts all pending tasks,
     * and clears the autoplay flag. After this method is called, no new queries or commands
     * will be processed until {@link #run()} is called again.</p>
     */
    public void close() {
        if (isAutoplayRunning.getAndSet(false)) {
            scheduler.shutdownNow();
        }
    }
    /**
     * Change the query delay to a new value and return whether the change has been performed.
     *
     * @param millis the new query delay in milliseconds, must exceed double the amount of the internal checking delay
     * @return whether the change is successful, {@code true} if after the operation the internal queryDelay
     *         is equal to set value, {@code false} if the operation is not successful.
     */
    public boolean changeQueryDelay(int millis){
        if (millis > checkDelay * 2) {
            synchronized (queryLock) {
                queryDelay = millis;
            }
            return true;
        } else return false;
    }

    /**
     * Converts a {@link HexEngine} state into a string representation.
     *
     * @param engine the {@link HexEngine to convert
     * @return a string representing the state of each block (as "X" or "O"), or an empty string if the engine is null or empty
     */
    public static String getEngineString(HexEngine engine) {
        if (engine == null || engine.length() == 0){
            return "";
        } else {
            StringBuilder builder = new StringBuilder(engine.getBlock(0).getState() ? "X" : "O");
            for (int i = 1; i < engine.length(); i ++){
                builder.append(engine.getBlock(i).getState() ? "X" : "O");
            }
            return builder.toString();
        }
    }
    /**
     * Converts an array of {@link Piece} objects into a space-separated string representation using {@link Piece#toByte()}.
     *
     * @param pieces the pieces to convert
     * @return a string representing each piece's byte encoding, or an empty string if there are no piece present
     */
    public static String getQueueString(Piece[] pieces) {
        if (pieces == null || pieces.length == 0){
            return "";
        } else {
            StringBuilder builder = new StringBuilder(getPieceString(pieces[0]));
            for (int i = 1; i < pieces.length; i ++){
                builder.append(" ").append(getPieceString(pieces[i]));
            }
            return builder.toString();
        }
    }
    /**
     * Converts a single {@link Piece} into its byte string representation.
     *
     * @param piece the piece to convert
     * @return the byte value of the piece as a string
     */
    private static String getPieceString(Piece piece) {
        return Byte.toString(piece.toByte());
    }
    /**
     * Executes a command received from the callback processor to change the GUI.
     *
     * <p>This method supports the following commands:</p>
     * <ul>
     *     <li><b>"move i k index"</b>: Place the piece at the given index into the engine block identified by hex coordinates (i, k).</li>
     *     <li><b>"interrupt"</b> or <b>"kill"</b>: Immediately shuts down autoplay mode.</li>
     * </ul>
     * <p>
     * Only executes if {@link #isAutoplayRunning} is {@code true}. Otherwise, the command is ignored.
     *
     * @param command the command name
     * @param args the command arguments
     * @throws IllegalArgumentException if the command or arguments are invalid
     * @throws InterruptedException if the thread is interrupted during post-execution scheduling
     */
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        if (!isAutoplayRunning.get()) {
            return; // Ignore commands if autoplay is closed
        }
        if (command.equals("move") && args.length == 3) {
            // Parse
            int i, k, index;
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Command move is invalid because I-line coordinate of move is not integer");
            }
            try {
                k = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Command move is invalid because K-line coordinate of move is not integer");
            }
            try {
                index = Integer.parseUnsignedInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Command move is invalid because piece index is not unsigned integer");
            }
            // Move only if autoplay is running
            synchronized (gameGUI){
                gameGUI.move(gameGUI.getEngine().getBlockIndex(i, k), index);
            }
        } else if (command.equals("interrupt") || command.equals("kill")) {
            close();
        } else {
            throw new IllegalArgumentException("Illegal command for this GameCommandProcessor");
        }

        // Mark query as completed and schedule next query
        synchronized (queryLock){
            isQueryCompleted = true;
        }
        if (isAutoplayRunning.get()) {
            scheduler.schedule(() -> {
                try {
                    query();
                } catch (InterruptedException e) {
                    close();
                }
            }, queryDelay, TimeUnit.MILLISECONDS);
        }
    }
}
