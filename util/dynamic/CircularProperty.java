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

package util.dynamic;

/**
 * Represents a circular or periodic dynamic property that evolves over time in a directional and cyclical manner.
 * <p>
 * A {@code CircularProperty<T>} extends the capabilities of {@link DynamicProperty} by introducing
 * support for direction, period, and phase—concepts common to oscillators, animations, rotations, or sine-wave generators.
 *
 * @param <T> the type of value represented by this property
 */
public interface CircularProperty<T> extends DynamicProperty<T> {
    /**
     * Returns the current direction of motion or progression.
     *
     * @return {@code true} if forward (e.g. clockwise, increasing), {@code false} if backward (e.g. counterclockwise, decreasing)
     */
    boolean direction();
    /**
     * Reverses the direction of the circular progression.
     * <p>
     * If the property is currently running, this method will:
     * <ul>
     *     <li>Stop the property</li>
     *     <li>Reverse its direction</li>
     *     <li>Start it again</li>
     * </ul>
     * If the property is not running, it simply reverses the direction.
     */
    default void reverseDirection() {
        boolean wasRunning = isRunning();
        if (wasRunning) stop();
        setDirection(!direction());
        if (wasRunning) start();
    }
    /**
     * Sets the current direction of the property.
     *
     * @param forward {@code true} for forward progression, {@code false} for reverse
     */
    void setDirection(boolean forward);
    /**
     * Gets the current phase of the property, usually normalized to [0.0, 1.0).
     * <p>
     * This represents the position in the cycle, where 0.0 is the start, and 1.0 wraps back to 0.
     *
     * @return the phase as a normalized double
     */
    double phase();
    /**
     * Resets the property to its default or initial phase.
     * <p>
     * Does not alter whether the property is running.
     */
    void setDefaultPhase();
    /**
     * Returns the default phase value that will be used when {@link #reset()} or {@link #setDefaultPhase()} is called.
     *
     * @return the default phase as a normalized double
     */
    double getDefaultPhase();
    /**
     * Gets the current period (duration of a full cycle), in seconds or ticks, depending on implementation.
     *
     * @return the period of the cycle
     */
    double getPeriod();
    /**
     * Sets the period (duration of a full cycle) of the property.
     * <p>
     * If the property is running, this method will:
     * <ul>
     *     <li>Stop the property</li>
     *     <li>Update the period</li>
     *     <li>Start it again</li>
     * </ul>
     * Otherwise, it will only update the internal value.
     *
     * @param period the new period (must be positive)
     * @throws IllegalArgumentException if the period is not positive
     */
    default void setPeriod(double period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be positive.");
        }
        boolean wasRunning = isRunning();
        if (wasRunning) stop();
        applyPeriod(period);
        if (wasRunning) start();
    }
    /**
     * Internal hook to actually change the period value.
     * This is called by {@link #setPeriod(double)} after stopping the property.
     *
     * @param period the new period value (guaranteed to be > 0)
     */
    void applyPeriod(double period);
    /**
     * Advances the phase manually by a certain delta.
     * <p>
     * This is useful for fast-forwarding or manually syncing phase with another system.
     * This method is safer than {@link #applyPhase(double)}.
     *
     * @param delta the amount to advance (can be negative)
     */
    default void advancePhase(double delta){
        applyPhase((phase() + delta + 1.0) % 1.0);
    }
    /**
     * Apply the phase manually to change the current phase value.
     *
     * @param phase the phase to apply to
     * @throws IllegalArgumentException if the phase is not in the correct range
     */
    void applyPhase(double phase) throws IllegalArgumentException;
}