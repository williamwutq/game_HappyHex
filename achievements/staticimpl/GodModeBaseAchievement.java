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

package achievements.staticimpl;

import achievements.GameAchievementTemplate;
import game.Queue;
import hex.GameState;

/**
 * The {@code GodModeBaseAchievement} class represents a game achievement that is achieved when the player
 * activates God Mode. It implements the {@link GameAchievementTemplate} interface and provides functionality
 * to check if the achievement has been achieved by directly querying the piece processor ID from the {@link Queue}.
 * <p>
 * This class checks if the current piece processor ID corresponds to God Mode (ID 5). The achievement is considered
 * achieved if this condition is met.
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
public final class GodModeBaseAchievement implements GameAchievementTemplate {
    @Override
    public String name() {
        return "God Mode";
    }
    @Override
    public String description() {
        return "Activate God Mode";
    }
    @Override
    public boolean test(GameState state){
        int id = Queue.getPieceProcessorID();
        // ID for the Devil Mode piece processor is 2
        return id == 5;
    }
}
