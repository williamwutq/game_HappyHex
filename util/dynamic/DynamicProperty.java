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
 * Represents a dynamic property that can be started, stopped, reset, and queried for its current value.
 * <p>
 * A {@code DynamicProperty<T>} encapsulates behavior and state that may change over time or through explicit control.
 * It is useful for modeling timers, animations, reactive values, or mutable states that follow a lifecycle.
 * <p>
 * Implementations should ensure that the animation of such properties are thread-safe and not blocking, and access to
 * the dynamic property is readily available all the time, even when the update is not running.
 *
 * @param <T> the type of the value represented by this property
 */
public interface DynamicProperty<T> {
    /**
     * Starts or activates the property, allowing it to begin producing or updating its value.
     * <p>
     * If the property is already running, this method should have no effect.
     */
    void start();
    /**
     * Stops or deactivates the property. If the property is not started, the method should have no effect.
     * <p>
     * Once stopped, the property should not update or change its internal state until started again.
     * Calling {@link #get()} may still return the last known value.
     */
    void stop();
    /**
     * Resets the property to its initial state.
     * <p>
     * This should clear any accumulated or intermediate state, so that a subsequent {@link #start()} begins fresh.
     * This does not start or stop the property; it only resets internal state.
     */
    void reset();
    /**
     * Retrieves the current value of the dynamic property.
     *
     * @return the current value of type {@code T}, which may be stale or live depending on the state
     */
    T get();
    /**
     * Returns whether the property is currently running.
     *
     * @return {@code true} if the property is active and updating, {@code false} otherwise
     */
    boolean isRunning();
    /**
     * Stops the property and then resets it.
     * <p>
     * Equivalent to sequentially calling {@link #stop()} and {@link #reset()}.
     */
    default void stopAndReset() {
        stop();
        reset();
    }
    /**
     * Restarts the property by stopping, resetting, and then starting it again.
     * <p>
     * This ensures a full lifecycle reset and is useful for forcing fresh computation or behavior.
     */
    default void restart() {
        stop();
        reset();
        start();
    }
    /**
     * Starts the property as a new instance, stopping any ongoing activity first if necessary.
     * <p>
     * If the property is currently running, it will be stopped before resetting and starting anew.
     */
    default void startNew() {
        if (isRunning()) {
            stop();
        }
        reset();
        start();
    }
}