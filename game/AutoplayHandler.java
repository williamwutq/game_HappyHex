package game;

import io.GameTime;
import python.PythonCommandProcessor;
import python.PythonEnvsChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles lifecycle management and coordination between a {@link GameCommandProcessor}
 * and a {@link PythonCommandProcessor} in an autoplay setup. The upgraded version of this
 * handler support automatic detection and usage of machine learning models if available.
 * The model availability is checked through the {@link PythonEnvsChecker} class.
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
 * The {@link #closeML()} method provides a convenient way to switch from using a machine learning model
 * back to a default non-ML setup by performing a hard close and reinitializing without ML. It does not close the handler.
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
 * Machine Learning Integration: If machine learning models are available and compatible with
 * the current game settings, the handler will automatically configure the Python processor to use
 * the first suitable model it finds. This is done through the {@link PythonEnvsChecker} class,
 * which manages model discovery and compatibility checks. If no models are found or if ML
 * support is unavailable, the handler falls back to a default non-ML setup. To switch back
 * to a non-ML setup, call {@link #closeML()} which performs a hard close and reinitializes without ML.
 * To attempt to use ML if available, call {@link #useMLIfAvailable()}.
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
 * @version 2.0
 * @since 1.4
 */
public class AutoplayHandler implements Runnable, AutoCloseable{
    private PythonCommandProcessor pythonProcessor;
    private final GameCommandProcessor gameProcessor;
    private final GameGUIInterface gameGUI;
    private final List<Supplier<Boolean>> mlPreconditions;
    private boolean usingML = false;
    private static final long hardCloseDelay = 600000;
    private static final String defaultPath = "python/comm.py";
    private static final String tensorflowPath = "python/comm_tf.py";
    private static final String torchPath = "python/comm_pt.py";
    private long lastCloseTime;
    /**
     * Constructs a new AutoplayHandler with the given GUI interface.
     *
     * @param gameGUI The game GUI interface to be passed into the GameCommandProcessor.
     * @throws IllegalStateException if the Python backend fails to initialize.
     */
    public AutoplayHandler(GameGUIInterface gameGUI){
        this.gameGUI = gameGUI;
        this.lastCloseTime = System.currentTimeMillis();
        this.gameProcessor = new GameCommandProcessor(gameGUI);
        smartSetupPython();
        this.mlPreconditions = new ArrayList<>();
        synchronized (mlPreconditions) {
            this.mlPreconditions.add(PythonEnvsChecker::isMLAvailable);
            this.mlPreconditions.add(() -> {
                PythonEnvsChecker.updateAvailableModels();
                int radius = gameGUI.getEngine().getRadius();
                int size = gameGUI.getQueue().length;
                return PythonEnvsChecker.canRunModel(radius, size);
            });
        }
    }
    /**
     * Attempts to set up the Python command processor with a machine learning model
     * if available and compatible with the current game settings. If no suitable model
     * is found or if ML is not available, falls back to the default setup without ML.
     * Throws an exception if initialization fails. The method is built with the help
     * of the {@link PythonEnvsChecker} class to determine model availability and compatibility.
     *
     * @throws IllegalStateException If the Python process fails to start.
     */
    public void smartSetupPython() throws IllegalStateException {
        if (shouldUseML()) {
            // Get the engine and queue from gameGUI
            int radius = gameGUI.getEngine().getRadius();
            int size = gameGUI.getQueue().length;
            String[] models = PythonEnvsChecker.runnableModels(radius, size);
            if (models.length > 0) {
                // Use the first available model
                String model = models[0];
                String framework = PythonEnvsChecker.getModelFramework(model);
                String modelPath = PythonEnvsChecker.getModelPath(model);
                if (framework == null || modelPath == null) {
                    System.out.println(GameTime.generateSimpleTime() + " Hpyhexml (Python): No compatible ML model found for " + radius + "-" + size);
                    setupPython();
                } else if (framework.equals("tf")) {
                    setupPython(tensorflowPath, new String[]{modelPath});
                    System.out.println(GameTime.generateSimpleTime() + " Hpyhexml (Python): Using TensorFlow model " + model);
                    usingML = true;
                } else if (framework.equals("torch")) {
                    setupPython(torchPath, new String[]{modelPath});
                    System.out.println(GameTime.generateSimpleTime() + " Hpyhexml (Python): Using PyTorch model " + model);
                    usingML = true;
                }
                return;
            }
        }
        setupPython();
    }
    /**
     * If ML is available, attempts to set up the Python command processor with a compatible ML model
     * based on the current game settings. If no suitable model is found or if ML
     * is not available, does nothing. Throws an exception if initialization fails.
     * <p>
     * Compare to call {@link #hardClose()} then {@link #setupPython()}, this method does not always
     * force a reinitialization. It only does so if a compatible ML model is found. This is beneficial
     * because it avoids expensive process restart if no ML is to be used.
     * <p>
     * The method uses the {@link PythonEnvsChecker} class to determine model availability and compatibility.
     *
     * @throws IllegalStateException If the Python process fails to start.
     */
    public void useMLIfAvailable(){
        if (shouldUseML()) {
            // Get the engine and queue from gameGUI
            int radius = gameGUI.getEngine().getRadius();
            int size = gameGUI.getQueue().length;
            String[] models = PythonEnvsChecker.runnableModels(radius, size);
            if (models.length > 0) {
                // Use the first available model
                String model = models[0];
                String framework = PythonEnvsChecker.getModelFramework(model);
                String modelPath = PythonEnvsChecker.getModelPath(model);
                if (framework != null && framework.equals("tf")) {
                    hardClose();
                    setupPython(tensorflowPath, new String[]{modelPath});
                    System.out.println(GameTime.generateSimpleTime() + " Hpyhexml (Python): Using TensorFlow model " + model);
                    usingML = true;
                } else if (framework != null && framework.equals("torch")) {
                    hardClose();
                    setupPython(torchPath, new String[]{modelPath});
                    System.out.println(GameTime.generateSimpleTime() + " Hpyhexml (Python): Using PyTorch model " + model);
                    usingML = true;
                }
            }
        }
    }
    /**
     * Initializes the Python command processor with default path and links both command processors
     * as each other's callbacks. Throws an exception if initialization fails.
     *
     * @throws IllegalStateException If the Python process fails to start.
     */
    private void setupPython() throws IllegalStateException {
        try {
            this.pythonProcessor = new PythonCommandProcessor(defaultPath);
        } catch (IOException e) {
            throw new IllegalStateException("Python initialization failed");
        }
        gameProcessor.setCallBackProcessor(pythonProcessor);
        pythonProcessor.setCallBackProcessor(gameProcessor);
    }
    /**
     * Initializes the Python command processor with a specified path and arguments,
     * linking both command processors as each other's callbacks. Throws an exception
     * if initialization fails.
     *
     * @param path The path to the Python script.
     * @param args The arguments to pass to the Python script.
     * @throws IllegalStateException If the Python process fails to start.
     */
    private void setupPython(String path, String[] args) throws IllegalStateException {
        try {
            this.pythonProcessor = new PythonCommandProcessor(path, args);
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
        if (pythonProcessor == null) smartSetupPython();
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
     * If currently using a machine learning model, performs a hard close and
     * reinitializes the Python processor without ML. If not using ML, does nothing.
     */
    public void closeML(){
        if (usingML){
            hardClose();
            usingML = false;
            setupPython();
        }
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
    /**
     * Returns whether the handler is currently using a machine learning model.
     *
     * @return true if using ML, false otherwise
     */
    public boolean isUsingML(){ return usingML;}
    /**
     * Evaluates all preconditions for using machine learning models.
     * If all preconditions return true, indicates that ML can be used.
     * If there are no preconditions, returns true by default.
     *
     * @return true if all ML preconditions are met, false otherwise
     */
    public boolean shouldUseML() {
        if (mlPreconditions == null) return false;
        if (mlPreconditions.isEmpty()) return true;
        synchronized (mlPreconditions) {
            for (Supplier<Boolean> precondition : mlPreconditions) {
                if (!precondition.get()) return false;
            }
            return true;
        }
    }
    /**
     * Adds a new precondition for using machine learning models.
     * The precondition is a Supplier that returns a boolean indicating
     * whether the condition is met. All preconditions must return true
     * for ML to be considered usable.
     * <p>
     * The precondition to add must not be null and cannot already exist.
     *
     * @param precondition the precondition to add
     */
    public void addMLPrecondition(Supplier<Boolean> precondition){
        if (precondition == null) return;
        synchronized (mlPreconditions) {
            if (mlPreconditions.contains(precondition)) return;
            mlPreconditions.add(precondition);
        }
    }
    /**
     * Removes an existing precondition for using machine learning models.
     * If the precondition is found and removed, returns true; otherwise, returns false.
     * <p>
     * The precondition to remove must not be null and cannot be the default
     * ML availability check.
     *
     * @param precondition the precondition to remove
     * @return true if the precondition was found and removed, false otherwise
     */
    public boolean removeMLPrecondition(Supplier<Boolean> precondition){
        if (precondition == null || mlPreconditions.isEmpty()) return false;
        if (precondition.equals((Supplier<Boolean>) PythonEnvsChecker::isMLAvailable)) return false;
        synchronized (mlPreconditions) {
            return mlPreconditions.remove(precondition);
        }
    }
}
