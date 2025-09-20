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
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;

/**
 * The {@code AchievementIcon} interface defines methods for creating and rendering achievement icons.
 * An achievement icon is composed of multiple colored shapes, which can be normalized and scaled.
 * The interface provides methods to retrieve the icon parts, scale them to a desired size, and
 * paint them onto a graphics context.
 * <p>
 * Implementing classes should provide the logic for generating the normalized icon parts as an
 * iterable of pairs, where each pair consists of a color and a shape, and can optionally override
 * the background color, which will be used to fill the background before painting the icon parts.
 * <p>
 * The default methods handle scaling and painting the icon parts.
 */
public interface AchievementIcon {
    /**
     * Get the normalized icon parts as an iterable of pairs of color and shape.
     * The parts are normalized to the standard size [-1, 1] on both x and y axes.
     * @return An iterable of pairs where each pair contains a Color and a Shape.
     */
    Iterable<Pair<Color, Shape>> normalizedParts();
    /**
     * Get the background color of the icon.
     * This color is used to fill the background before painting the icon parts.
     * @implNote The default implementation returns Color.BLACK.
     * @return The background color.
     */
    default Color getBackgroundColor(){
        return Color.BLACK;
    }
    /**
     * Get the icon parts scaled to the specified size.
     * @param size The desired size to scale the icon parts to.
     * @return An iterable of pairs where each pair contains a Color and a Shape.
     */
    default Iterable<Pair<Color, Shape>> scaledParts(double size){
        Iterator<Pair<Color, Shape>> normalizedParts = normalizedParts().iterator();
        return new Iterable<Pair<Color, Shape>>() {
            @Override
            public Iterator<Pair<Color, Shape>> iterator() {
                return new Iterator<Pair<Color, Shape>>() {
                    @Override
                    public boolean hasNext() {
                        return normalizedParts.hasNext();
                    }
                    @Override
                    public Pair<Color, Shape> next() {
                        return new Pair<>(normalizedParts.next().getFirst(), scaleShape(normalizedParts.next().getSecond(), size));
                    }
                };
            }
        };
    }
    /**
     * Paint the icon onto the provided Graphics context at the specified size.
     * <p>
     * The icon is painted on a rounded square background.
     * @param g The Graphics context to paint on.
     * @param size The size to scale the icon to.
     */
    default void paint(Graphics g, double size){
        Graphics2D g2d = (Graphics2D) g;
        double r = size * 0.1;
        g2d.fill(new RoundRectangle2D.Double(0.0, 0.0, size, size, r, r));
        for (Pair<Color, Shape> part : scaledParts(size)){
            g2d.setColor(part.getFirst());
            g2d.fill(part.getSecond());
        }
    }

    /**
     * Scale a shape from the normalized coordinate system [-1, 1] to [0, size].
     * @param shape The shape to scale.
     * @param size The target size to scale the shape to.
     * @return The scaled shape.
     */
    static Shape scaleShape(Shape shape, double size) {
        // Transform from [-1, 1] to [0, size]
        AffineTransform transform = new AffineTransform();
        transform.scale(size / 2.0, size / 2.0);
        transform.translate(size / 2.0, size / 2.0);
        return transform.createTransformedShape(shape);
    }
    /**
     * Get the type of the AchievementIcon as a string.
     * @param icon The AchievementIcon instance.
     * @return The type of the icon as a string.
     */
    static String typeOf(AchievementIcon icon){
        return icon.getClass().getSimpleName();
    }
}
