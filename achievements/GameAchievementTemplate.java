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

import achievements.icon.AchievementEmptyIcon;
import achievements.icon.AchievementIcon;
import hex.GameState;

import java.awt.*;
import java.util.function.Predicate;

/**
 * The {@code GameAchievementTemplate} interface defines the contract for creating game achievements.
 * It extends the {@link Predicate} interface to allow checking if an achievement has been achieved
 * based on the current {@link GameState}.
 * <p>
 * Implementing classes should provide a unique name and description for the achievement, as well as
 * the logic to determine if the achievement has been achieved. Implementing classes can optionally
 * provide an icon from the achievement, or a default icon will be provided. All templates should be immutable.
 * <p>
 * This interface is intended to be used in conjunction with game state management systems to track
 * and reward player accomplishments.
 * <p>
 * The achievement logic should be stateless, relying solely on the provided {@link GameState} to determine
 * if the achievement criteria have been met.
 * <p>
 * The template itself is not guaranteed to be serializable, but implementations may choose to implement
 * serialization and deserialization as needed.
 *
 * @see GameState
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public interface GameAchievementTemplate extends Predicate<GameState> {
    /**
     * The name of the achievement. This should be unique among all achievements.
     * @return the name of the achievement
     */
    String name();
    /**
     * The description of the achievement. This should provide a brief summary of what the achievement is about.
     * @return the description of the achievement
     */
    String description();
    /**
     * The icon representing the achievement. This icon will be displayed in the game's UI.
     * @implNote This default implementation generates a simple icon based on the name of the achievement.
     * If the name starts with digits, those digits are used to create a text icon.
     * If the name starts with a letter, that letter is used to create a text icon.
     * If the name is null or empty, or does not start with a digit or letter, an empty icon is returned.
     * The color of the text icon is generated based on the hash code of the name to ensure uniqueness.
     * @implSpec Implementing classes can override this method to provide a custom icon.
     * However, if a custom icon is provided, the class must provide ways to serialize and deserialize the icon
     * if the achievement itself is serialized.
     * @see achievements.icon
     * @return the icon of the achievement
     */
    default AchievementIcon icon(){
        String name = name();
        if (name != null && !name.isEmpty()) {
            // Check if the first few characters of the name are digits, if so, use them to create a text icon
            int i = 0;
            while (i < name.length() && Character.isDigit(name.charAt(i))) {
                i++;
            }
            if (i > 0) {
                String numberPart = name.substring(0, i);
                return new achievements.icon.AchievementTextIcon(numberPart, randomColor(name));
            }
            // Check if the first digit is a letter, if so, use it to create a text icon
            if (Character.isLetter(name.charAt(0))) {
                String letterPart = name.substring(0, 1).toUpperCase();
                return new achievements.icon.AchievementTextIcon(letterPart, randomColor(name));
            }
            // Note in this implementation, we always use the name of the achievement to generate a color,
            // as it is guaranteed to be unique among all achievements and longer strings have better, well-distributed hash codes.
        }
        return AchievementEmptyIcon.INSTANCE;
    }
    /**
     * Tests whether the achievement has been achieved based on the provided game state.
     * @param state the current game state
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     */
    boolean test(GameState state);
    /**
     * Generates a random color based on the hash code of the provided object.
     * <p>
     * Object provided should ideally be non-null and have a well-distributed hash code to ensure
     * a good variety of colors. If the object is null, the method returns {@link Color#GRAY}.
     *
     * @param object the object to generate a color from
     * @return a Color object representing the generated color
     */
    private static Color randomColor(Object object){
        final int dh = 360; final int ds = 50;
        if (object == null) return Color.GRAY;
        // Get hash
        int hash = object.hashCode();
        // Use hash to generate color
        float hue = (hash % dh) / 360f;
        float saturation = 0.5f + (((float) hash / dh) % ds) / 100f;
        float brightness = 0.7f + (((float) hash / dh / ds) % 30) / 100f;
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
