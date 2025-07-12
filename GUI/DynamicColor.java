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

package GUI;

import util.dynamic.ExtremalProperty;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@code DynamicColor} smoothly transitions between two colors (default and extreme)
 * over a specified duration using a sigmoid interpolation curve. The transition is animated
 * in a background thread, and optionally updates a GUI component via a provided {@link Runnable}.
 * <p>
 * This class is thread-safe. All mutable shared state is accessed under synchronization or
 * via atomic variables.
 * <p>
 * Implements {@link ExtremalProperty} with {@link Color} as the value type.
 *
 * @author William Wu
 * @version 1.4
 * @since 1.4
 */
public class DynamicColor implements ExtremalProperty<Color>, Runnable {
    private final Color defaultColor;
    private final Color extremeColor;
    private double position;
    private final AtomicBoolean forward;
    private final AtomicBoolean running;
    private int duration;
    private final Object lock; // Lock shared because access to duration is low
    private final Runnable guiUpdater;
    private Thread animationThread;
    private static final double sigmoidSteepness = 10.0; // Controls sigmoid curve steepness
    private static final long frameDelay = 6;

    /**
     * Constructs a new {@code DynamicColor} with the given colors and GUI update callback.
     * The GUI update must be non-blocking and thread-safe, and must not create recursive calls to this
     * {@link DynamicColor}, as that may create unexpected issues.
     *
     * @param defaultColor the color to start from; defaults to {@link Color#BLACK} if null
     * @param extremeColor the color to transition to; defaults to {@link Color#WHITE} if null
     * @param guiUpdater   a {@code Runnable} to run on each frame update (optional, may be null)
     */
    public DynamicColor(Color defaultColor, Color extremeColor, Runnable guiUpdater) {
        this.defaultColor = defaultColor != null ? defaultColor : Color.BLACK;
        this.extremeColor = extremeColor != null ? extremeColor : Color.WHITE;
        this.guiUpdater = guiUpdater;
        this.position = 0.0;
        this.lock = new Object();
        this.forward = new AtomicBoolean(true);
        this.running = new AtomicBoolean(false);
        this.duration = 1000; // Default to 1 second
    }
    /**
     * Constructs a {@code DynamicColor} without a GUI updater.
     *
     * @param defaultColor the base color
     * @param extremeColor the peak color
     */
    public DynamicColor(Color defaultColor, Color extremeColor) {
        this(defaultColor, extremeColor, null);
    }
    /**
     * Starts the animation from the current position in the specified direction.
     * If already running, this call has no effect.
     * Runs in a separate background thread.
     */
    @Override
    public void run() {
        if (running.get()) return;
        final long startTime = System.nanoTime();
        running.set(true);
        synchronized (lock) {
            animationThread = new Thread(() -> {
                while (running.get() && !animationThread.isInterrupted()) {
                    long currentTime = System.nanoTime();
                    double elapsedMillis = (currentTime - startTime) / 1000000.0;
                    double rawProgress = elapsedMillis / getDuration();
                    double newPosition = forward.get() ? rawProgress : 1.0 - rawProgress;
                    if (newPosition >= 1.0 || newPosition <= 0.0) {
                        newPosition = Math.max(0.0, Math.min(1.0, newPosition)); // Clamp to [0,1]
                        applyPosition(newPosition);
                        running.set(false);
                        reverseDirection();
                        break;
                    }
                    applyPosition(newPosition);
                    if (guiUpdater != null) {
                        guiUpdater.run();
                    }
                    try {
                        Thread.sleep(frameDelay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                running.set(false);
            });
            animationThread.start();
        }
    }
    /**
     * Stops the animation thread safely. If not running, this call has no effect.
     */
    @Override
    public void stop() {
        running.set(false);
        synchronized (lock) {
            if (animationThread != null) {
                animationThread.interrupt();
                try {
                    animationThread.join(); // Wait for thread to terminate
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                animationThread = null;
            }
        }
    }
    /**
     * Resets the animation state to the default color and direction (forward).
     * If a GUI updater is set, it is invoked.
     */
    @Override
    public void reset() {
        synchronized (lock) {
            position = 0.0;
            forward.set(true);
            if (guiUpdater != null) {
                guiUpdater.run();
            }
        }
    }
    /**
     * Returns the current interpolated color based on position.
     *
     * @return the color between {@code defaultColor} and {@code extremeColor}
     */
    @Override
    public Color get() {
        final double p;
        synchronized (lock){
            p = this.position;
        }
        return computeColor(this.defaultColor, this.extremeColor, p);
    }
    /** {@inheritDoc} */
    @Override
    public boolean isRunning() {
        return running.get();
    }
    /** {@inheritDoc} */
    @Override
    public boolean direction() {
        return forward.get();
    }
    /** {@inheritDoc} */
    @Override
    public void setDirection(boolean forward) {
        this.forward.set(forward);
        if (guiUpdater != null) {
            guiUpdater.run();
        }
    }
    /** {@inheritDoc} */
    @Override
    public void reverseDirection() {
        boolean wasRunning = isRunning();
        if (wasRunning) {
            stop();
        }
        setDirection(!forward.get());
        if (wasRunning) {
            start();
        }
    }
    /** {@inheritDoc} */
    @Override
    public double position() {
        synchronized (lock) {
            return position;
        }
    }
    /** {@inheritDoc} */
    @Override
    public void advancePosition(double delta) {
        double p;
        synchronized (lock) {
            p = this.position;
        }
        p = (p + delta + 2.0) % 2.0;
        if (p <= 1.0) {
            applyPosition(p);
        } else {
            reverseDirection();
            applyPosition(2.0 - p);
        }
    }
    /** {@inheritDoc} */
    @Override
    public void applyPosition(double position) {
        if (position < 0.0 || position > 1.0) {
            throw new IllegalArgumentException("Position must be between 0.0 and 1.0");
        }
        synchronized (lock) {
            this.position = position;
        }
        if (guiUpdater != null) {
            guiUpdater.run();
        }
    }
    /** {@inheritDoc} */
    @Override
    public int getDuration() {
        synchronized (lock) {
            return duration;
        }
    }
    /** {@inheritDoc} */
    @Override
    public void applyDuration(int duration) {
        synchronized (lock) {
            this.duration = duration;
        }
    }
    /** {@inheritDoc} */
    @Override
    public boolean atMiddle() {
        synchronized (lock) {
            return position != 0.0 && position != 1.0;
        }
    }
    /**
     * Interpolates between two colors using sigmoid smoothing.
     *
     * @param defaultColor the base color
     * @param extremeColor the extreme color
     * @param position     the normalized position [0.0, 1.0]
     * @return the interpolated {@code Color}
     */
    private static Color computeColor(Color defaultColor, Color extremeColor, double position) {
        double t = sigmoid(position);
        int r = interpolate(defaultColor.getRed(), extremeColor.getRed(), t);
        int g = interpolate(defaultColor.getGreen(), extremeColor.getGreen(), t);
        int b = interpolate(defaultColor.getBlue(), extremeColor.getBlue(), t);
        int a = interpolate(defaultColor.getAlpha(), extremeColor.getAlpha(), t);
        return new Color(r, g, b, a);
    }
    /**
     * Sigmoid curve centered at 0.5 for smoother transition.
     *
     * @param x position input in [0.0, 1.0]
     * @return sigmoid-interpolated value
     */
    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-sigmoidSteepness * (x - 0.5)));
    }
    /**
     * Linearly interpolates between two values with smoothing factor {@code t}.
     *
     * @param start the starting value
     * @param end   the ending value
     * @param t     the interpolation factor [0.0, 1.0]
     * @return the interpolated value
     */
    private static int interpolate(int start, int end, double t) {
        return (int) (start + (end - start) * t);
    }
}