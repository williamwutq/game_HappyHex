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
import io.JsonConvertible;

/**
 * The {@code UnknownAchievement} class represents an achievement that is not recognized by the system.
 * It implements the {@link GameAchievementTemplate} interface. This achievement should not be phantom.
 * <p>
 * This class is used to handle achievements that are not defined in the current implementation.
 * It stores the name of the unknown achievement but does not provide any functionality
 * to test or update the achievement status.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed.
 * This ensures that the unknown achievement remains consistent throughout its lifecycle.
 * <p>
 * The class cannot be serialized to a real template, as it represents an unknown achievement.
 * <p>
 * The class does not provide any specific achievement logic, as it is meant to represent an unknown
 * achievement that should be preserved without modification during serialization and deserialization processes.
 *
 * @see GameAchievementTemplate
 * @see JsonConvertible
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public final class UnknownAchievement implements GameAchievementTemplate {
    private final String name;
    /**
     * Constructs an UnknownAchievement with the given name and data.
     * @param name the name of the unknown achievement
     */
    public UnknownAchievement(String name) {
        this.name = name;
    }
    /**
     * {@inheritDoc}
     * Returns the name of the unknown achievement.
     * @return name
     */
    @Override
    public String name() {
        return name;
    }
    /**
     * {@inheritDoc}
     * Always returns "Unknown Achievement".
     * @return description
     */
    @Override
    public String description() {
        return "Unknown Achievement";
    }
    /**
     * {@inheritDoc}
     * Always returns false to prevent modification.
     * @param state current game state
     * @return false
     */
    @Override
    public boolean test(GameState state) {
        return false;
    }
    /**
     * {@inheritDoc}
     * Equality based on name.
     * @param obj other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UnknownAchievement other)) return false;
        return name.equals(other.name);
    }
    /**
     * {@inheritDoc}
     * Hash code based on name.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
