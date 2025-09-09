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

package achievements;

import hex.GameState;
import hex.Piece;

/**
 * The {@code IdenticalQueueAchievement} class represents a game achievement that is achieved when all pieces
 * in the current piece queue are identical. It implements the {@link GameAchievementTemplate} interface and
 * provides functionality to check if the achievement has been achieved based on the current {@link GameState}.
 * <p>
 * This class checks if all pieces in the queue are the same instance, meaning they must be identical in type
 * and reference. The achievement is considered achieved if this condition is met.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed.
 * This ensures that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class does not provide serialization or deserialization methods, as it does not maintain any complex state.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class IdenticalQueueAchievement implements GameAchievementTemplate{
    /**
     * Creates a new IdenticalQueueAchievement.
     * The achievement is achieved if all pieces in the current piece queue are identical.
     */
    public IdenticalQueueAchievement() {}
    @Override
    public String name() {
        return "Identity, Destiny";
    }
    @Override
    public String description() {
        return "Have all pieces in the queue be the exact same piece";
    }
    @Override
    public boolean test(GameState state) {
        Piece[] queue = state.getQueue();
        if (queue.length == 0) {
            return false;
        }
        Piece first = queue[0];
        for (Piece p : queue) {
            if (p != first) {
                return false;
            }
        }
        return true;
    }
}
