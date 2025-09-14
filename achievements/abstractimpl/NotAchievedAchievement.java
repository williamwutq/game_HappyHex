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
 * A wrapper for {@link GameAchievementTemplate} that marks an achievement as not achieved.
 * Not achieved achievements have their test logic inverted to indicate their status.
 * This class delegates all functionality to the wrapped template, except for the test method,
 * which is modified to return true when the original template's test method returns false.
 * <p>
 * Note: The name and description remain unchanged and are directly delegated to the
 * wrapped template.
 * <p>
 * This is marked phantom to prevent display in achievement lists and to enable constant checking regardless
 * of whether the achievement has been achieved.
 *
 * @see GameAchievementTemplate
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class NotAchievedAchievement implements PhantomAchievementTemplate {
    private final GameAchievementTemplate template;
    /**
     * Constructs a new {@code NotAchievedAchievement} that wraps the given template.
     * <p>
     * The name of the achievement will be prefixed with "_PHANTOM_" to indicate its phantom status.
     * @param template the achievement template to wrap
     */
    public NotAchievedAchievement(GameAchievementTemplate template) {
        this.template = template;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement prefixed with "_PHANTOM_"
     */
    @Override
    public String realName() {
        return template.name();
    }
    /**
     * {@inheritDoc}
     * @return true if the user has not achieved this achievement, false otherwise
     */
    @Override
    public boolean test(GameState state) {
        return !template.test(state);
    }

    /**
     * {@inheritDoc}
     * Two NotAchievedAchievement are considered equal if their wrapped templates are equal.
     * This ensures that the equality check is based on the actual achievement logic rather than
     * the wrapper itself. Note that the template does not equal to the NotAchievedAchievement wrapping it.
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NotAchievedAchievement)) return false;
        NotAchievedAchievement other = (NotAchievedAchievement) obj;
        return template.equals(other.template);
    }
    /**
     * The hash code is the bitwise NOT of the wrapped template's hash code.
     * This ensures that the hash code is different from the original template,
     * while still being consistent with equality.
     * @return the hash code of this achievement
     */
    @Override
    public int hashCode() {
        return ~template.hashCode();
    }
}
