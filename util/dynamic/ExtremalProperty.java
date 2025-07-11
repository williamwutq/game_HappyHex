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
 * Represents a dynamic property that transitions between two extremes—such as from a default state to a peak state—and stops at the end.
 * <p>
 * Subsequent runs reverse direction automatically unless explicitly set otherwise.
 *
 * @param <T> the type of value produced or represented by the property
 */
public interface ExtremalProperty<T> extends DynamicProperty<T> {
    /**
     * Returns the current direction of progression.
     * <p>
     * {@code true} = forward (toward the extreme), {@code false} = backward (toward default).
     */
    boolean direction();
    /**
     * Sets the direction explicitly.
     *
     * @param forward {@code true} to move toward the extreme value, {@code false} to move toward the default value
     */
    void setDirection(boolean forward);
    /**
     * Reverses the current direction.
     * <p>
     * If the property is running, it stops, reverses direction, and starts again.
     * If stopped, it simply toggles the direction flag.
     */
    default void reverseDirection() {
        boolean wasRunning = isRunning();
        if (wasRunning) stop();
        setDirection(!direction());
        if (wasRunning) start();
    }
    /**
     * Returns the current position of the property within its extremal range.
     * <p>
     * Typically, normalized: {@code 0.0} = default value, {@code 1.0} = extreme value.
     *
     * @return the progress through the range
     */
    double position();
    /**
     * Advances the current position manually.
     * <p>
     * This is useful for synchronizing or forcing a certain state without running the animation.
     * This method is safer than {@link #applyPosition(double)}.
     *
     * @param delta amount to advance or retreat (can be negative)
     */
    default void advancePosition(double delta){
        double p = (position() + delta + 2.0) % 2.0;
        if (p <= 1.0){
            applyPosition(p);
        } else {
            reverseDirection();
            applyPosition(2.0 - p);
        }
    }
    /**
     * Apply the position manually to change the current phase value.
     *
     * @param position the position to apply to
     * @throws IllegalArgumentException if the position is not in the correct range
     */
    void applyPosition(double position) throws IllegalArgumentException;
    /**
     * Returns the total duration required to go from one end to the other.
     *
     * @return the run duration in seconds or ticks
     */
    double getDuration();
    /**
     * Sets the total duration for a full transition between extremes.
     * <p>
     * If currently running, this will restart the transition using the new timing.
     *
     * @param duration the new duration value (must be positive)
     * @throws IllegalArgumentException if duration is not positive
     */
    default void setDuration(double duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        boolean wasRunning = isRunning();
        if (wasRunning) stop();
        applyDuration(duration);
        if (wasRunning) start();
    }
    /**
     * Internal hook to update the internal duration.
     * Called by {@link #setDuration(double)}.
     *
     * @param duration the validated duration value
     */
    void applyDuration(double duration);
    /**
     * Resets the property to its default extreme and set direction to positive.
     * <p>
     * This should clear any accumulated or intermediate state, so that a subsequent {@link #start()} begins fresh.
     * This does not start or stop the property; it only resets internal state.
     */
    void reset();
    /**
     * Starts the property in its current direction.
     * <p>
     * If it's currently not at an extreme (i.e., mid-way), direction is reversed before starting.
     */
    @Override
    default void start() {
        if (isRunning()) stop();
        if (atMiddle()) {
            reverseDirection();
        }
        run(); // hook to real start logic
    }
    /**
     * True if position is not exactly 0.0 or 1.0 (i.e., not at either extreme).
     *
     * @return whether this is in-between extremes
     */
    default boolean atMiddle(){
        return position() != 1.0 && position() != 0.0;
    }
    /**
     * Triggers actual run logic after external decision logic.
     */
    void run();
}