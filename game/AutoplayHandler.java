package game;

import python.PythonCommandProcessor;

import java.io.IOException;

/**
 * Handles lifecycle management and coordination between a {@link GameCommandProcessor}
 * and a {@link PythonCommandProcessor} in an autoplay setup.
 * <p>
 * This class serves as the orchestrator for connecting a GUI-facing game logic engine
 * and a Python-based command processor. Upon creation, it links the two processors through
 * their callback interfaces, enabling two-way command delegation. Specifically:
 * <ul>
 *   <li>{@code GameCommandProcessor} handles communication with the GUI, maintains a controlled
 *       query loop to poll game state, and relays commands.</li>
 *   <li>{@code PythonCommandProcessor} launches a Python subprocess, to which it sends serialized
 *       commands, and handles asynchronous responses by redirecting them to the game processor.</li>
 * </ul>
 * <p>
 * AutoplayHandler initializes these components, sets up mutual callback links, and provides
 * methods to run and shut them down safely in a lifecycle-aware manner.
 * <p>
 * AutoplayHandler provides three levels of shutdown:
 * <ul>
 *   <li>{@link #hardClose()} — Fully shuts down both the Python subprocess and the game command processor,
 *       severing their callback links. This is used when a full reset is needed, or when the program is fully closing.
 *       It guarantees all resources and subprocesses are released.</li>
 *   <li>{@link #softClose()} — Gracefully shuts down only the game processor while leaving the Python
 *       process running. This is useful for brief pauses or temporary game suspension, where restarting
 *       the Python backend would be unnecessary and expensive.</li>
 *   <li>{@link #genericClose()} — Dynamically chooses between a hard or soft close depending on how much
 *       time has passed since the last close operation. If the previous close was recent (within
 *       {@code hardCloseDelay}, which is 10 minutes), it opts for a full shutdown. Otherwise, it performs
 *       a lightweight soft close. This adaptive behavior prevents rapid repeated restarts while still
 *       ensuring cleanup after prolonged inactivity. <b>This is the preferred method for most implementations</b></li>
 * </ul>
 * <p>
 * Thread Usage: The {@link #run()} method should be called in a separate thread, as it is
 * blocking and enters the main autoplay query loop provided by the game processor. Any interruption
 * of that loop will trigger {@code genericClose()} to ensure safe recovery.
 * <p>
 * Callback Safety: Both processors are properly linked via {@code setCallBackProcessor()}
 * before any query or execution begins. This is enforced by both implementations.
 * <p>
 * Failure Handling: If initialization of the Python backend fails (e.g., due to misconfigured
 * paths or system limitations), an {@link IllegalStateException} is thrown to signal startup failure.
 * This failure should never happen.
 * <p>
 * Example Usage
 * <ul>
 * <li>Direct execution on the main thread:
 * <pre>{@code
 * GameGUIInterface gui = ...;
 * AutoplayHandler handler = new AutoplayHandler(gui);
 *
 * // Run autoplay loop (blocking call)
 * handler.run();
 * }</pre></li>
 *
 * <li>Execution on a separate thread (recommended for GUI applications):
 * <pre>{@code
 * GameGUIInterface gui = ...;
 * AutoplayHandler handler = new AutoplayHandler(gui);
 *
 * Thread autoplayThread = new Thread(handler);
 * autoplayThread.start();
 *
 * // Later, when shutting down:
 * handler.genericClose(); // or handler.hardClose() if desirable to fully close.
 * }</pre></li>
 * </ul>
 *
 * @see GameCommandProcessor
 * @see PythonCommandProcessor
 * @author William Wu
 * @version 1.4
 * @since 1.4
 */
public class AutoplayHandler implements Runnable, AutoCloseable{
    private PythonCommandProcessor pythonProcessor;
    private final GameCommandProcessor gameProcessor;
    private static final long hardCloseDelay = 600000;
    private long lastCloseTime;
    /**
     * Constructs a new AutoplayHandler with the given GUI interface.
     *
     * @param gameGUI The game GUI interface to be passed into the GameCommandProcessor.
     * @throws IllegalStateException if the Python backend fails to initialize.
     */
    public AutoplayHandler(GameGUIInterface gameGUI){
        lastCloseTime = System.currentTimeMillis();
        this.gameProcessor = new GameCommandProcessor(gameGUI);
        setupPython();
    }
    /**
     * Initializes the Python command processor and links both command processors
     * as each other's callbacks. Throws an exception if initialization fails.
     *
     * @throws IllegalStateException If the Python process fails to start.
     */
    private void setupPython() throws IllegalStateException {
        try {
            this.pythonProcessor = new PythonCommandProcessor("python/comm.py");
        } catch (IOException e) {
            throw new IllegalStateException("Python initialization failed");
        }
        gameProcessor.setCallBackProcessor(pythonProcessor);
        pythonProcessor.setCallBackProcessor(gameProcessor);
    }
    /**
     * Starts the command processing. If the Python processor is not initialized,
     * it attempts to reinitialize. If interrupted, it invokes a generic close operation.
     * This method is blocking and should be run on a separate thread as {@link Runnable}.
     */
    public void run() {
        if (pythonProcessor == null) setupPython();
        try {
            gameProcessor.run();
            gameProcessor.query();
        } catch (InterruptedException e) {
            genericClose();
        }
    }
    /**
     * Invokes a {@link #hardClose()} of the processors.
     * This method is part of the {@link AutoCloseable} interface.
     */
    public void close(){
        hardClose();
    }
    /**
     * Performs a full shutdown of both the Python and game processors,
     * including severing the callback link and nullifying the Python processor.
     * Also updates the lastCloseTime to current time.
     */
    public void hardClose() {
        if (pythonProcessor != null) {
            pythonProcessor.close();
            pythonProcessor = null;
        }
        if (gameProcessor != null) {
            gameProcessor.close();
            gameProcessor.setCallBackProcessor(null);
        }
        lastCloseTime = System.currentTimeMillis();
    }
    /**
     * Performs a minimal shutdown, closing only the game processor.
     * The Python process remains active. Used when a quick reset is needed.
     */
    public void softClose() {
        if (gameProcessor != null) {
            gameProcessor.close();
        }
        lastCloseTime = System.currentTimeMillis();
    }
    /**
     * Determines whether to do a {@link #softClose()} or {@link #hardClose()} depending on the
     * elapsed time since the last close. If the interval is less than
     * {@code hardCloseDelay}, performs a hard close; otherwise, performs a soft close.
     */
    public void genericClose(){
        if (System.currentTimeMillis() - lastCloseTime < hardCloseDelay){
            hardClose();
        } else {
            softClose();
        }
    }
    /**
     * Change the query delay of the game processor and return whether the change has been performed.
     *
     * @param millis the new query delay in milliseconds
     * @return whether the change is successful
     * @see GameCommandProcessor#changeQueryDelay
     */
    public boolean changeDelay(int millis){
        if (gameProcessor != null) {
            return gameProcessor.changeQueryDelay(millis);
        } else return false;
    }
}
