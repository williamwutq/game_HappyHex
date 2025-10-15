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

import achievements.icon.AchievementIcon;
import hex.Block;
import hex.GameState;
import hex.HexEngine;

/**
 * The {@code EngineMiddleUnoAchievement} class represents a game achievement that is achieved when the player
 * has only one block remaining in the game engine, and that block is located in the middle of the engine.
 * It extends the {@link StaticAchievement} class and provides functionality to check if the achievement has been
 * achieved based on the current {@link GameState}.
 * <p>
 * This class checks if there is exactly one block left in the game engine and if that block is positioned
 * at the center of the engine. The achievement is considered achieved if both conditions are met.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed. This ensures
 * that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class does not provide serialization or deserialization methods, as it does not maintain any complex state.
 *
 * @see GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public final class EngineMiddleUnoAchievement extends StaticAchievement {
    public static void load(){
        register("Bullseye", EngineMiddleUnoAchievement.class);
    }
    public EngineMiddleUnoAchievement(String n, String d, AchievementIcon i) {super(n, d, i);}
    @Override
    public boolean test(GameState state) {
        HexEngine engine = state.getEngine();
        if (engine == null) return false;
        int count = 0; boolean find = false; int r = engine.getRadius();
        for (Block b : engine.blocks()) {
            if (b.getState()) {
                count++;
                if (b.J() == r && b.getLineJ() == 0) find = true;
            }
        }
        if (count != 1) return false;
        return find;
    }
}
