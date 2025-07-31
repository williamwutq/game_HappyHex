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

import hexio.HexLogger;

import java.io.IOException;

/**
 * The {@code Controller} class provides control logic for a game viewer interface.
 * It handles user actions such as advance, retreat, run, stop, and set pointer, and
 * mediates between a {@link Tracker} (which holds the state history of a game),
 * a {@link GameGUIInterface} (which displays the game state to the user), a
 * {@link InfoGUIInterface} (which displays the score and turn information), and a
 * {@link ActionGUIInterface} (which handles game animation actions).
 * <p>
 * The controller supports threaded execution of the game (via {@code run()}) with
 * adjustable playback speed, and is designed to be thread-safe and interruptible.
 * <p>
 * The Controller handles file name input and update requests via {@link FileGUIInterface}.
 * It ensures thread safety and responsiveness, especially during long I/O operations such
 * as loading a file and constructing a new {@link Tracker}.
 *
 * @author William Wu
 * @version 1.1 (HappyHex 1.4)
 * @since 1.0 (HappyHex 1.3)
 * @see Tracker
 * @see HexLogger
 * @see GameGUIInterface
 * @see FileGUIInterface
 * @see InfoGUIInterface
 * @see ActionGUIInterface
 * @see Thread
 */
public class Controller{
    private final Object trackerLock = new Object();
    private Tracker tracker;
    private GameGUIInterface gameGui;
    private FileGUIInterface fileGui;
    private InfoGUIInterface infoGui;
    private ActionGUIInterface actionGui;
    private int speed = 200;
    private Thread runnerThread;
    private final Object lock = new Object();
    private volatile boolean runningForward = false;
    private volatile boolean runningBackward = false;

    /**
     * Starts automatically advancing the game state forward at the currently set speed.
     * <p>
     * This method spawns a background daemon thread that calls {@link Tracker#advancePointer()}
     * repeatedly until the end of the history is reached or {@link #stop()} is called.
     * If already running, this method does nothing.
     */
    public void run() {
        if (tracker != null) {
            synchronized (lock) {
                if (runningForward) return; // Already running
                runningForward = true;
                runningBackward = false;
                runnerThread = new Thread(() -> {
                    try {
                        while (true) {
                            synchronized (lock) {
                                if (!runningForward) break;
                            }
                            boolean advanced;
                            synchronized (trackerLock) {
                                advanced = tracker.advancePointer();
                                if (advanced) {
                                    updateGUI();
                                } else if (actionGui != null) {
                                    actionGui.onRunStop();
                                }
                            }
                            if (!advanced) {
                                stop(); // Reached end
                                break;
                            }

                            Thread.sleep(getSpeedSafe());
                        }
                    } catch (InterruptedException e) {
                        // Thread interrupted by stop or another action
                        Thread.currentThread().interrupt(); // restore interrupt status
                    }
                });
                runnerThread.setDaemon(true);
                runnerThread.start();
            }
            synchronized (trackerLock) {
                if (actionGui != null) {
                    actionGui.onRunStart();
                }
            }
        }
    }
    /**
     * Starts automatically retreating the game state backward at the currently set speed.
     * <p>
     * This method spawns a background daemon thread that calls {@link Tracker#decrementPointer()}
     * repeatedly until the start of the history is reached or {@link #stop()} is called.
     * If already running, this method does nothing.
     */
    public void back() {
        if (tracker != null) {
            synchronized (lock) {
                if (runningBackward) return; // Already running
                runningForward = false;
                runningBackward = true;
                runnerThread = new Thread(() -> {
                    try {
                        while (true) {
                            synchronized (lock) {
                                if (!runningBackward) break;
                            }
                            boolean advanced;
                            synchronized (trackerLock) {
                                advanced = tracker.decrementPointer();
                                if (advanced) {
                                    updateGUI();
                                } else if (actionGui != null) {
                                    actionGui.onRunStop();
                                }
                            }
                            if (!advanced) {
                                stop(); // Reached end
                                break;
                            }

                            Thread.sleep(getSpeedSafe());
                        }
                    } catch (InterruptedException e) {
                        // Thread interrupted by stop or another action
                        Thread.currentThread().interrupt(); // restore interrupt status
                    }
                });
                runnerThread.setDaemon(true);
                runnerThread.start();
            }
            synchronized (trackerLock) {
                if (actionGui != null) {
                    actionGui.onRunStart();
                }
            }
        }
    }
    /**
     * Stops the automatic run mode initiated by {@link #run()}.
     * <p>
     * If no run thread is active, this method does nothing. If a thread is running,
     * it is safely interrupted and marked for cleanup.
     */
    public void stop() {
        synchronized (lock) {
            runningForward = false;
            runningBackward = false;
            if (runnerThread != null) {
                runnerThread.interrupt();
                runnerThread = null;
            }
        }
    }

    /**
     * Advances the game state by one step using {@link Tracker#advancePointer()}.
     * If the advance succeeds, the GUI is updated. If the end has been reached,
     * this method has no effect.
     * <p>
     * If a run is active, it is stopped first.
     */
    public void advance() {
        stop();
        if (tracker != null) synchronized (trackerLock) {
            if (tracker.advancePointer()) {
                updateGUI();
                if (actionGui != null) {
                    actionGui.onIncrement();
                }
            }
        }
    }
    /**
     * Moves the game state one step backward using {@link Tracker#decrementPointer()}.
     * If the retreat succeeds, the GUI is updated. If the beginning has been reached,
     * this method has no effect.
     * <p>
     * If a run is active, it is stopped first.
     */
    public void retreat() {
        stop();
        if (tracker != null) synchronized (trackerLock) {
            if (tracker.decrementPointer()) {
                updateGUI();
                if (actionGui != null) {
                    actionGui.onDecrement();
                }
            }
        }
    }

    /**
     * Sets the internal pointer of the tracker to a specific index using {@link Tracker#setPointer(int)}.
     * If the index is out of bounds, the exception is caught silently and no update occurs.
     * <p>
     * If a run is active, it is stopped first.
     *
     * @param index the new pointer index to set
     */
    public void setPointer(int index) {
        stop();
        if (tracker != null) synchronized (trackerLock) {
            try {
                tracker.setPointer(index);
                updateGUI();
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }

    /**
     * Binds a {@link GameGUIInterface} to the controller.
     * The game GUI will receive all visual updates based on tracker state changes.
     *
     * @param gui the GUI interface to bind
     */
    public void bindGameGUI(GameGUIInterface gui) {
        this.gameGui = gui;
    }
    /**
     * Binds a {@link InfoGUIInterface} to the controller.
     * The game GUI will receive all game point updates based on tracker state changes.
     *
     * @param gui the GUI interface to bind
     */
    public void bindInfoGUI(InfoGUIInterface gui) {
        this.infoGui = gui;
    }
    /**
     * Binds the file GUI interface for handling file selection and I/O updates.
     *
     * @param fileGui the {@link FileGUIInterface} to bind
     */
    public void bindFileGUI(FileGUIInterface fileGui) {
        this.fileGui = fileGui;
        fileGui.setNameChangeListener(this::onFileChosen);
    }
    /**
     * Binds an {@link ActionGUIInterface} to the controller.
     * The action GUI will receive updates for game actions such as run, stop, increment, and decrement.
     *
     * @since 1.1 (HappyHex 1.4)
     * @param actionGui the {@link ActionGUIInterface} to bind
     */
    public void bindActionGUI(ActionGUIInterface actionGui) {
        this.actionGui = actionGui;
    }
    /**
     * Updates the GUI display for game state and game information if tracker exists.
     * <p>
     * The method invokes {@link GameGUIInterface#setEngine} and
     * {@link GameGUIInterface#setQueue} with the current state from the tracker.
     * <p>
     * The method also invokes {@link InfoGUIInterface#setScore} and
     * {@link InfoGUIInterface#setTurn} with the current game points from the tracker.
     */
    private void updateGUI() {
        if (tracker != null) {
            if (gameGui != null) {
                gameGui.setEngine(tracker.engine());
                gameGui.setQueue(tracker.queue());
            }
            if (infoGui != null) {
                infoGui.setScore(tracker.score());
                infoGui.setTurn(tracker.getPointer());
            }
        }
    }
    /**
     * Sets the delay in milliseconds between automatic steps during {@link #run()} mode.
     *
     * @param milliseconds the delay between steps; must be positive
     */
    public void setSpeed(int milliseconds) {
        if (milliseconds > 0) {
            synchronized (lock) {
                this.speed = milliseconds;
            }
            synchronized (trackerLock) {
                if (actionGui != null) {
                    actionGui.onSpeedChanged(milliseconds);
                }
            }
        }
    }
    /**
     * Safely retrieves the current speed setting inside a synchronized block.
     *
     * @return the speed in milliseconds
     */
    private int getSpeedSafe() {
        synchronized (lock) {
            return speed;
        }
    }

    /**
     * Callback triggered by {@link FileGUIInterface} when the user selects a new file.
     * This method starts a background thread to load the file and build a new {@link Tracker}.
     *
     * @param filename the selected filename
     */
    public void onFileChosen(String filename) {
        // Manipulate name
        String filePath = filename + ".hpyhex";

        new Thread(() -> {
            HexLogger logger = new HexLogger(filePath);
            boolean loaded = false;
            try {
                logger.read();
                loaded = true;
            } catch (IOException ignored) {}

            if (loaded) {
                if (fileGui != null) {
                    fileGui.setFilename(filename);
                }
                try {
                    Tracker newTracker = new Tracker(logger); // create tracker takes time
                    synchronized (lock) {
                        this.tracker = newTracker;
                        updateGUI();
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }
}

