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
import achievements.GameAchievementTemplate;
import achievements.icon.AchievementIcon;
import hex.GameState;

/**
 * The {@code AccumulatedEliminationAchievement} class represents a game achievement that is achieved when the player
 * eliminates a total of 1000 blocks in the same game without quitting it. It extends the {@link StaticAchievement}
 * class and provides functionality to check if the achievement has been achieved based on the current {@link GameState}.
 * <p>
 * This class checks if the player has eliminated at least 1000 blocks in total during the game session. The achievement
 * is considered achieved if this condition is met.
 * This class is dependent on the {@link GameEssentials} class to retrieve the total number of eliminated blocks.
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
public final class AccumulatedEliminationAchievement extends StaticAchievement {
    public static void load(){
        register("Thousand Cuts", AccumulatedEliminationAchievement.class);
    }
    public AccumulatedEliminationAchievement(String n, String d, AchievementIcon i) {super(n, d, i);}
    @Override
    public boolean test(GameState state) {
        return GameEssentials.getTotalEliminatedBlocks() >= 1000;
    }
}