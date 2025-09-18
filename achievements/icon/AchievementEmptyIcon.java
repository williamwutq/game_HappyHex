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

package achievements.icon;

import util.tuple.Pair;

import java.awt.*;
import java.util.Iterator;

/**
 * Represents an empty achievement icon with no parts.
 * This icon will render as empty and has no shapes or colors.
 * It implements the {@link AchievementIcon} interface.
 * <p>
 * The {@code AchievementEmptyIcon} class provides a simple implementation of the
 * {@link AchievementIcon} interface, returning an empty iterable for
 * the icon parts.
 *
 * @see AchievementIcon
 * @author William Wu
 * @version 2.0
 */
public class AchievementEmptyIcon implements AchievementIcon {
    /**
     * Constructs an AchievementEmptyIcon.
     * This icon has no parts and will render as empty.
     */
    public AchievementEmptyIcon() {
        // No initialization needed for an empty icon
    }
    /**
     * {@inheritDoc}
     * Returns an empty iterable as the icon has no parts.
     */
    @Override
    public Iterable<Pair<Color, Shape>> normalizedParts() {
        return () -> new Iterator<Pair<Color, Shape>>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            @Override
            public Pair<Color, Shape> next() {
                throw new UnsupportedOperationException("No parts in AchievementEmptyIcon");
            }
        };
    }
}
