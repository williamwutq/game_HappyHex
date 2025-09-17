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

package achievements.abstractimpl;

import achievements.GameAchievementTemplate;
import hex.GameState;

/**
 * A decorator for {@link GameAchievementTemplate} that marks an achievement as hidden.
 * Hidden achievements have their names prefixed with "_HIDDEN_" to indicate their status.
 * This class delegates all functionality to the wrapped template, except for the name,
 * which is modified to include the hidden prefix.
 * <p>
 * This is useful for achievements that should not be visible to players,
 * used for record keeping purposes for game mechanics.
 * <p>
 * Example usage:
 * <pre>
 *     GameAchievementTemplate hiddenAchievement = new HiddenAchievement(existingAchievement);
 * </pre>
 * <p>
 * Note: The description and test logic remain unchanged and are directly delegated to the
 * wrapped template.
 * <p>
 * A few utility static methods are provided to check if an achievement is hidden,
 * to hide an achievement name, and to reveal the original name of a hidden achievement.
 *
 * @see GameAchievementTemplate
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class HiddenAchievement implements GameAchievementTemplate {
    private final GameAchievementTemplate template;
    /**
     * Constructs a new {@code HiddenAchievement} that wraps the given template.
     * <p>
     * The name of the achievement will be prefixed with "_HIDDEN_" to indicate its hidden status.
     * @param template the achievement template to wrap
     */
    public HiddenAchievement(GameAchievementTemplate template) {
        this.template = template;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement prefixed with "_HIDDEN_"
     */
    @Override
    public String name() {
        return hidedName(template.name());
    }
    /**
     * {@inheritDoc}
     * @return the description of the achievement
     */
    @Override
    public String description() {
        return template.description();
    }

    /**
     * {@inheritDoc}
     * An object is considered equal to this {@code HiddenAchievement} if it is also a {@code HiddenAchievement}
     * and their wrapped templates are equal. It is noticeable the hidden achievement is not equal to its template.
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof HiddenAchievement) {
                return template.equals(((HiddenAchievement) obj).template);
        } else return false;
    }
    /**
     * {@inheritDoc}
     * The hash code is computed based on the wrapped template's hash code.
     * @return the hash code of the achievement
     */
    @Override
    public int hashCode() {
        return template.hashCode() + 0x4bf56b8; // constant to differentiate from template's hash code
    }
    /**
     * {@inheritDoc}
     * Delegates the test to the wrapped template.
     * @param state the current game state
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     */
    @Override
    public boolean test(GameState state) {
        return template.test(state);
    }
    /**
     * Creates a new {@code HiddenAchievement} from the given template.
     * @param template the achievement template to wrap
     * @return a new {@code HiddenAchievement} instance
     */
    public static GameAchievementTemplate of(GameAchievementTemplate template) {
        return new HiddenAchievement(template);
    }
    /**
     * Wraps the given achievement template in a {@code HiddenAchievement} if it is not already hidden.
     * If the template is already a {@code HiddenAchievement}, it is returned unchanged.
     * @param template the achievement template to wrap
     * @return a {@code HiddenAchievement} instance or the original template if it is already hidden
     */
    public static GameAchievementTemplate wrap(GameAchievementTemplate template) {
        if (isHidden(template)) {
            return template; // already hidden
        }
        return new HiddenAchievement(template);
    }
    /**
     * Checks if the given achievement template is a hidden achievement.
     * @param template the achievement template to check
     * @return {@code true} if the template is a {@code HiddenAchievement}, {@code false} otherwise
     */
    public static boolean isHidden(GameAchievementTemplate template) {
        return template instanceof HiddenAchievement;
    }
    /**
     * Checks if the given achievement name indicates a hidden achievement.
     * @param name the name of the achievement to check
     * @return {@code true} if the name starts with "_HIDDEN_", {@code false} otherwise
     */
    public static boolean isHidden(String name) {
        return name.startsWith("_HIDDEN_");
    }
    /**
     * Returns the hidden version of the given achievement name.
     * If the name is already hidden, it is returned unchanged.
     * @param name the name of the achievement
     * @return the hidden version of the name
     */
    public static String hidedName(String name) {
        if (isHidden(name)) {
            return name; // already hidden
        }
        return "_HIDDEN_" + name;
    }
    /**
     * Reveals the original name of a hidden achievement by removing the "_HIDDEN_" prefix.
     * If the name is not hidden, it is returned unchanged.
     * @param name the name of the achievement
     * @return the original name without the hidden prefix
     */
    public static String revealName(String name) {
        if (isHidden(name)) {
            return name.substring(8); // remove "_HIDDEN_"
        }
        return name; // not hidden
    }
}
