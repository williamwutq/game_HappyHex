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
 * A wrapper for {@link GameAchievementTemplate} that is archived when exactly one of the two wrapped achievements
 * is achieved. This class delegates all functionality to the wrapped templates, except for the test method,
 * which is modified to return true when exactly one of the original templates' test methods returns true,
 * indicating an exclusive or (XOR) condition.
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
public class XorAchievement implements PhantomAchievementTemplate {
    private final GameAchievementTemplate template1;
    private final GameAchievementTemplate template2;
    /**
     * Constructs a new {@code XorAchievement} that wraps the given template.
     * <p>
     * The name of the achievement will be prefixed with "_PHANTOM_" to indicate its phantom status.
     * @param template1 the achievement template to wrap
     */
    public XorAchievement(GameAchievementTemplate template1, GameAchievementTemplate template2) {
        this.template1 = template1;
        this.template2 = template2;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement prefixed with "_PHANTOM_"
     */
    @Override
    public String realName() {
        return template1.name() + "_XOR_" + template2.name();
    }
    /**
     * {@inheritDoc}
     * @return true if the user has achieved exactly one of the two achievements, false otherwise
     */
    @Override
    public boolean test(GameState state) {
        return template1.test(state) ^ template2.test(state);
    }
    /**
     * {@inheritDoc}
     * Two XorAchievement are considered equal if their wrapped templates are equal.
     * This ensures that the equality check is based on the actual achievement logic rather than
     * the wrapper itself. Note that the template does not equal to the XorAchievement wrapping it.
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof XorAchievement)) return false;
        XorAchievement other = (XorAchievement) obj;
        return (template1.equals(other.template1) && template2.equals(other.template2)) ||
                (template1.equals(other.template2) && template2.equals(other.template1));
    }
    /**
     * The hash code is the bitwise XOR of the wrapped templates' hash codes.
     * This ensures that the hash code is different from the original templates,
     * while still being consistent with equality.
     * @return the hash code of this achievement
     */
    @Override
    public int hashCode() {
        return template1.hashCode() ^ template2.hashCode();
    }
}
