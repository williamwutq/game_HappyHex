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

import GUI.GameEssentials;
import Launcher.LaunchEssentials;
import achievements.GameAchievementTemplate;
import hex.GameState;

/**
 * The {@code AutoplayBaseAchievement} class represents a game achievement that is achieved when the player
 * starts to use Autoplay. It implements the {@link GameAchievementTemplate} interface and provides functionality
 * to check if the achievement has been achieved by pulling the autoplay status from {@link GameEssentials}
 * and checking if the game has started from {@link LaunchEssentials}.
 * <p>
 * This class checks if the Autoplay handler is running and if the game has started. The achievement is considered
 * achieved if both conditions are met.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed. This ensures
 * that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class does not provide serialization or deserialization methods, as it does not maintain any complex state.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @see GameEssentials
 * @see LaunchEssentials
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public final class AutoplayBaseAchievement implements GameAchievementTemplate {
    @Override
    public String name() {
        return "Go Automatic" ;
    }
    @Override
    public String description() {
        return "Start to use Autoplay";
    }
    @Override
    public boolean test(GameState state){
        if (GameEssentials.getAutoplayHandler() == null) {
            return false;
        }
        return GameEssentials.getAutoplayHandler().isRunning() && LaunchEssentials.isGameStarted();
    }
}
