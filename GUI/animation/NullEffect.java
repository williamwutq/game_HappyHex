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

package GUI.animation;

import java.awt.*;

/**
 * A no-op animation that performs no visual effect and can terminate a series of linked animations.
 * <p>
 * This class extends {@link Animation} to provide an empty animation with zero frames,
 * effectively doing nothing when started. It is useful as a placeholder or default animation
 * when no visual effect is desired, ensuring compatibility with systems expecting an
 * {@code Animation} instance without rendering any graphics. Due to the logic in the
 * {@code Animation} class's {@code start} method no total frames ensures that the
 * {@link #paintFrame} method is never called, as the animation cannot start.
 * <p>
 * Additionally, {@code NullEffect} can be used to terminate a sequence of animations linked
 * via the {@link #getStartListener} and {@link #setEndListener} system. When included in a
 * chain where one animation's {@code endListener} triggers another's {@code startListener},
 * this instance will immediately complete without rendering, halting a series of animations
 * at a specific point, allowing subsequent actions or cleanup to occur without additional
 * visual effects. That said, be careful injecting this animation to a linked series of animations.
 */
public final class NullEffect extends Animation {
    /**
     * Constructs a {@code NullEffect} with zero frames and a 1ms frame delay.
     * <p>
     * The animation is initialized with no duration, preventing it from starting and
     * ensuring that no frames are rendered, as the {@code start} method in {@code Animation}
     * requires a non-zero {@code totalFrames} to proceed. This also allows the animation to
     * serve as a terminal point in a chain of animations linked by listeners.
     */
    public NullEffect() {
        super(0, 1);
    }
    /**
     * Renders an empty frame, performing no drawing operations.
     * <p>
     * This method is intentionally empty and is never called, as the animation's zero
     * frame count prevents the {@code start} method from initiating any frame rendering
     *
     * @param graphics the {@code Graphics} context to draw on.
     * @param progress a value between 0 and 1 indicating the animation's progress.
     */
    protected void paintFrame(Graphics graphics, double progress) {
    }
}
