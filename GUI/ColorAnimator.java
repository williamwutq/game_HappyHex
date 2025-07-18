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

/* Authors of mods should put their license and below. */

/* Authors of mods should put their license and above. */

package GUI;

import util.dynamic.CircularProperty;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@code ColorAnimator} is a circular dynamic property that animates a transition between multiple {@link Color} values over time.
 * <p>
 * It evolves its state based on a defined period and phase, looping through a list of colors in a continuous manner.
 * It supports direction reversal, customizable default phase, and manual control over lifecycle events such as start, stop, and reset.
 * <p>
 * This class is thread-safe and uses a background thread to update the animation. Color interpolation is linear and alpha-aware.
 * GUI updates can be triggered on each animation frame via a user-supplied {@link Runnable}.
 *
 * @author William Wu
 * @version 1.4.1
 * @since 1.4.1
 */
public class ColorAnimator implements CircularProperty<Color>{
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isForward;
    private final Object phaseLock;
    private final Object colorLock;
    private Color[] colors;
    private final AtomicInteger period;
    private double phase;
    private double defaultPhase;
    private final Runnable guiUpdater;
    private Thread animationThread;

    /**
     * Constructs a {@code ColorAnimator} with a given color sequence, animation period, and GUI update callback.
     *
     * @param colors     an array of {@link Color} objects to interpolate between; must contain at least two colors
     * @param period     the duration (in milliseconds) of a full cycle through all colors; must be positive
     * @param guiUpdater a {@link Runnable} to be invoked whenever the color is updated, typically for UI repainting
     * @throws IllegalArgumentException if the colors array is null or has fewer than two entries, or if period is non-positive
     */
    public ColorAnimator(Color[] colors, int period, Runnable guiUpdater) {
        if (colors == null || colors.length < 2) {
            throw new IllegalArgumentException("At least two colors are required.");
        }
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be positive.");
        }
        this.isRunning = new AtomicBoolean(false);
        this.isForward = new AtomicBoolean(true);
        this.phaseLock = new Object();
        this.colorLock = new Object();
        this.colors = Arrays.copyOf(colors, colors.length);
        this.period = new AtomicInteger(period);
        this.phase = 0.0;
        this.defaultPhase = 0.0;
        this.guiUpdater = guiUpdater;
    }

    /** {@inheritDoc} */
    @Override
    public boolean direction() {
        return isForward.get();
    }
    /** {@inheritDoc} */
    @Override
    public void setDirection(boolean forward) {
        isForward.set(forward);
    }
    /** {@inheritDoc} */
    @Override
    public double phase() {
        synchronized (phaseLock) {
            return phase;
        }
    }
    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the phase is not within the range [0.0, 1.0)
     */
    @Override
    public void applyDefaultPhase(double phase) {
        if (phase < 0 || phase >= 1) throw new IllegalArgumentException("Phase must be between 0 and 1");
        synchronized (phaseLock) {
            this.defaultPhase = phase;
        }
    }
    /** {@inheritDoc} */
    @Override
    public double getDefaultPhase() {
        synchronized (phaseLock){
            return defaultPhase;
        }
    }
    /** {@inheritDoc} */
    @Override
    public int getPeriod() {
        return period.get();
    }
    /**
     * {@inheritDoc}
     * This method is the internal hook to actually change the stored period.
     * It assumes validation has already been performed.
     */
    @Override
    public void applyPeriod(int period) {
        this.period.set(period);
    }
    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the phase is not within the range [0.0, 1.0)
     */
    @Override
    public void applyPhase(double phase) throws IllegalArgumentException {
        if (phase < 0 || phase >= 1) throw new IllegalArgumentException("Phase must be between 0 and 1");
        synchronized (phaseLock) {
            this.phase = phase;
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * Starts the animation in a background thread, updating phase and invoking the GUI updater periodically.
     */
    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            animationThread = new Thread(this::animate);
            animationThread.setDaemon(true);
            animationThread.start();
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * Stops the animation thread if running and waits for its termination.
     */
    @Override
    public void stop() {
        isRunning.set(false);
        if (animationThread != null) {
            animationThread.interrupt();
            try {
                animationThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            animationThread = null;
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * Resets the current phase to the {@link #getDefaultPhase() default phase} and triggers the GUI update task, if provided.
     */
    @Override
    public void reset() {
        synchronized (phaseLock){
            phase = defaultPhase;
        }
        if (guiUpdater != null) {
            guiUpdater.run();
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * Returns the current interpolated {@link Color} based on the internal phase value.
     * @return the current interpolated {@link Color} at this moment
     */
    @Override
    public Color get() {
        return get(0.0);
    }
    /** {@inheritDoc} */
    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
    /**
     * Returns the current interpolated {@link Color} with a phase shift applied.
     * <p>
     * This allows sampling a color at a position offset from the current animation phase.
     *
     * @param phaseShift a value in the range [-1.0, 1.0] representing phase offset; values are normalized
     * @return the interpolated color at the shifted phase
     */
    public Color get(double phaseShift) {
        double f;
        synchronized (phaseLock){
            f = (phase + phaseShift + 1.0) % 1.0;
        }
        return interpolateColor(f);
    }
    /**
     * Updates the color sequence used by the animator.
     * <p>
     * This change takes effect immediately and resets interpolation accordingly.
     * Triggers GUI update if a GUI updater was provided.
     *
     * @param colors the new array of colors; must contain at least two colors
     * @throws IllegalArgumentException if {@code colors} is {@code null} or has fewer than two elements
     */
    public void setColors(Color[] colors) {
        if (colors == null || colors.length < 2) {
            throw new IllegalArgumentException("At least two colors are required.");
        }
        synchronized (colorLock){
            this.colors = Arrays.copyOf(colors, colors.length);
        }
        if (guiUpdater != null) {
            guiUpdater.run();
        }
    }
    /**
     * Internal animation loop executed in a background thread while the animator is running.
     * <p>
     * It updates the phase based on elapsed time and direction, and invokes the GUI update task.
     * This method sleeps briefly between frames to limit CPU usage.
     */
    private void animate() {
        long lastUpdate = System.currentTimeMillis();
        while (isRunning.get()) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastUpdate;
            lastUpdate = currentTime;
            double delta = (double) elapsed / period.get();
            if (!isForward.get()){
                delta = -delta;
            }
            synchronized (phaseLock){
                phase = (phase + delta + 1.0) % 1.0;
            }
            if (guiUpdater != null) {
                guiUpdater.run();
            }
            try {
                Thread.sleep(6);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    /**
     * Computes the interpolated {@link Color} for a given normalized phase value.
     * <p>
     * Performs linear interpolation between two adjacent colors in the color array, wrapping cyclically.
     * Includes alpha channel interpolation.
     *
     * @param phase the normalized phase value in [0.0, 1.0)
     * @return the interpolated color
     */
    private Color interpolateColor(double phase) {
        final Color[] localColors;
        synchronized (colorLock) {
            localColors = Arrays.copyOf(colors, colors.length);
        }
        int numColors = localColors.length;
        double scaledPhase = phase * numColors;
        int index1 = (int) scaledPhase;
        int index2 = (index1 + 1) % numColors;
        double fraction = scaledPhase - index1;
        Color c1 = localColors[index1];
        Color c2 = localColors[index2];
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
        int a = (int) (c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * fraction);
        return new Color(r, g, b, a);
    }
}