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
import hex.Hex;
import hex.HexEngine;
import hex.Piece;

/**
 * The {@code EnginePerfectFitAchievement} class represents a game achievement that is achieved when the player
 * has a piece in the queue that perfectly fits an empty slot in the game board. It implements the
 * {@link GameAchievementTemplate} interface and provides functionality to check if the achievement has been
 * achieved based on the current {@link GameState}.
 * <p>
 * This class checks if there is at least one piece in the queue that can fit perfectly into any empty slot on the game board.
 * The achievement is considered achieved if this condition is met.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed. This ensures
 * that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class does not provide serialization or deserialization methods, as it does not maintain any complex state.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class EnginePerfectFitAchievement implements GameAchievementTemplate {
    @Override
    public String name() {
        return "Perfect Fit";
    }
    @Override
    public String description() {
        return "Have a piece in queue that perfectly fits an empty slot in the game board";
    }
    @Override
    public boolean test(GameState state) {
        HexEngine engine = state.getEngine();
        Piece[] queue = state.getQueue();
        for (Hex position : engine.blocks()) {
            for (Piece piece : queue) {
                if (engine.computeDenseIndex(position, piece) == 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
