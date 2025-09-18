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
import java.awt.image.BufferedImage;

/**
 * An AchievementIcon that applies a gradient background to another AchievementIcon.
 * The gradient is defined by four corner colors and can be applied in various directions.
 * <p>
 * This class implements the {@link AchievementIcon} interface and provides methods to
 * paint the icon with a gradient background onto a graphics context at a specified size.
 * It also utilizes caching to avoid redundant rendering for the same size.
 * <p>
 * The gradient can be defined using either a direction (VERTICAL, HORIZONTAL, DIAGONAL_DOWN, DIAGONAL_UP, MIXING)
 * with start and end colors, or by specifying the colors at each of the four corners (c00, c10, c01, c11).
 *
 * @see AchievementIcon
 * @author William Wu
 * @version 2.0
 */
public class AchievementGradientIcon implements AchievementIcon {
    // Direction constants
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;
    public static final int DIAGONAL_DOWN = 3;
    public static final int DIAGONAL_UP = 4;
    public static final int MIXING = 5;
    // Static variables
    protected static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    // Instance variables
    private final AchievementIcon baseIcon;
    private BufferedImage cachedImage = null;
    private final Color c00; // top-left
    private final Color c10; // top-right
    private final Color c01; // bottom-left
    private final Color c11; // bottom-right
    // Color and Graphics helpers
    /**
     * Interpolates between two colors based on the parameter t.
     * @param c1 the first color
     * @param c2 the second color
     * @param t the interpolation factor (0.0 to 1.0)
     * @return the interpolated color
     */
    protected static Color interpolate(Color c1, Color c2, double t) {
        int r = (int) (c1.getRed() * (1 - t) + c2.getRed() * t);
        int g = (int) (c1.getGreen() * (1 - t) + c2.getGreen() * t);
        int b = (int) (c1.getBlue() * (1 - t) + c2.getBlue() * t);
        int a = (int) (c1.getAlpha() * (1 - t) + c2.getAlpha() * t);
        return new Color(r, g, b, a);
    }
    /**
     * Paints the icon onto the provided Graphics context at the specified size.
     * The icon is scaled to fit within a square of the given size.
     * @param g2d The Graphics context to paint on.
     * @param size The size to scale the icon to.
     */
    protected void paintComponent(Graphics2D g2d, double size){
        for (Pair<Color, Shape> part : scaledParts(size)){
            g2d.setColor(part.getFirst());
            g2d.fill(part.getSecond());
        }
    }
    /**
     * Determines if a point (x, y) is within the rounded corner area of a square of given size and corner radius.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param size The size of the square.
     * @param cornerRadius The radius of the rounded corners.
     * @return true if the point is in the rounded corner area, false otherwise.
     */
    protected boolean isInRoundCorner(int x, int y, double size, double cornerRadius) {
        if (cornerRadius <= 0) return false;
        if (cornerRadius >= size / 2) return true;
        if (x < cornerRadius && y < cornerRadius) {
            double dx = cornerRadius - x;
            double dy = cornerRadius - y;
            return dx * dx + dy * dy > cornerRadius * cornerRadius;
        } else if (x > size - cornerRadius && y < cornerRadius) {
            double dx = x - (size - cornerRadius);
            double dy = cornerRadius - y;
            return dx * dx + dy * dy > cornerRadius * cornerRadius;
        } else if (x < cornerRadius && y > size - cornerRadius) {
            double dx = cornerRadius - x;
            double dy = y - (size - cornerRadius);
            return dx * dx + dy * dy > cornerRadius * cornerRadius;
        } else if (x > size - cornerRadius && y > size - cornerRadius) {
            double dx = x - (size - cornerRadius);
            double dy = y - (size - cornerRadius);
            return dx * dx + dy * dy > cornerRadius * cornerRadius;
        }
        return false;
    }

    /**
     * Creates a new AchievementGradientIcon with the specified base icon, start and end colors, and direction.
     * <p>
     * The gradient is applied over the base icon according to the specified direction.
     * <ul>
     *     <li>VERTICAL: Gradient from top (startColor) to bottom (endColor)</li>
     *     <li>HORIZONTAL: Gradient from left (startColor) to right (endColor)</li>
     *     <li>DIAGONAL_DOWN: Gradient from top-left (startColor) to bottom-right (endColor)</li>
     *     <li>DIAGONAL_UP: Gradient from bottom-left (startColor) to top-right (endColor)</li>
     *     <li>MIXING: Gradient mixing startColor and endColor in a checkerboard pattern</li>
     * </ul>
     *
     * @param icon        The base AchievementIcon to apply the gradient to.
     * @param startColor  The starting color of the gradient.
     * @param endColor    The ending color of the gradient.
     * @param direction   The direction of the gradient. Must be one of VERTICAL, HORIZONTAL, DIAGONAL_DOWN, DIAGONAL_UP, or MIXING.
     * @throws IllegalArgumentException if icon is null or of the same type, if startColor or endColor is null, or if direction is invalid.
     */
    public AchievementGradientIcon(AchievementIcon icon, Color startColor, Color endColor, int direction){
        if (icon == null) {
            throw new IllegalArgumentException("Icon cannot be null.");
        }
        if (icon instanceof AchievementGradientIcon) {
            throw new IllegalArgumentException("Cannot wrap an AchievementGradientIcon inside another AchievementGradientIcon.");
        }
        if (startColor == null || endColor == null) {
            throw new IllegalArgumentException("Colors cannot be null.");
        }
        switch (direction) {
            case VERTICAL -> {
                this.c00 = startColor;
                this.c10 = startColor;
                this.c01 = endColor;
                this.c11 = endColor;
            }
            case HORIZONTAL -> {
                this.c00 = startColor;
                this.c10 = endColor;
                this.c01 = startColor;
                this.c11 = endColor;
            }
            case DIAGONAL_DOWN -> {
                this.c00 = startColor;
                this.c10 = interpolate(startColor, endColor, 0.5);
                this.c01 = interpolate(startColor, endColor, 0.5);
                this.c11 = endColor;
            }
            case DIAGONAL_UP -> {
                this.c00 = interpolate(startColor, endColor, 0.5);
                this.c10 = endColor;
                this.c01 = startColor;
                this.c11 = interpolate(startColor, endColor, 0.5);
            }
            case MIXING -> {
                this.c00 = startColor;
                this.c10 = endColor;
                this.c01 = endColor;
                this.c11 = startColor;
            }
            default -> throw new IllegalArgumentException("Invalid direction. Must be one of VERTICAL, HORIZONTAL, DIAGONAL_DOWN, DIAGONAL_UP.");
        }
        this.baseIcon = icon;
    }
    /**
     * Creates a new AchievementGradientIcon with the specified base icon and corner colors.
     * <p>
     * The gradient is defined by the four corner colors:
     * <ul>
     *     <li>c00: Top-left color</li>
     *     <li>c10: Top-right color</li>
     *     <li>c01: Bottom-left color</li>
     *     <li>c11: Bottom-right color</li>
     * </ul>
     *
     * @param icon The base AchievementIcon to apply the gradient to.
     * @param c00  The color in the top-left corner.
     * @param c10  The color in the top-right corner.
     * @param c01  The color in the bottom-left corner.
     * @param c11  The color in the bottom-right corner.
     * @throws IllegalArgumentException if icon is null or of the same type, or if any of the colors are null.
     */
    public AchievementGradientIcon(AchievementIcon icon, Color c00, Color c10, Color c01, Color c11){
        if (icon == null) {
            throw new IllegalArgumentException("Icon cannot be null.");
        }
        if (icon instanceof AchievementGradientIcon) {
            throw new IllegalArgumentException("Cannot wrap an AchievementGradientIcon inside another AchievementGradientIcon.");
        }
        if (c00 == null || c10 == null || c01 == null || c11 == null) {
            throw new IllegalArgumentException("Colors cannot be null.");
        }
        this.c00 = c00;
        this.c10 = c10;
        this.c01 = c01;
        this.c11 = c11;
        this.baseIcon = icon;
    }
    /**
     * {@inheritDoc}
     * Delegates to the base icon's normalizedParts method.
     *
     * @return An iterable of pairs where each pair contains a Color and a Shape normalized to the standard size.
     */
    @Override
    public Iterable<Pair<Color, Shape>> normalizedParts() {
        return baseIcon.normalizedParts();
    }
    /**
     * {@inheritDoc}
     * Delegates to the base icon's scaledParts method.
     *
     * @param size The desired size to scale the icon parts to.
     * @return An iterable of pairs where each pair contains a Color and a Shape scaled to the specified size.
     */
    @Override
    public Iterable<Pair<Color, Shape>> scaledParts(double size) {
        return baseIcon.scaledParts(size);
    }
    /**
     * {@inheritDoc}
     * <p>
     * Paints the icon with a gradient background onto the provided Graphics context at the specified size.
     * Utilizes caching to avoid redundant rendering for the same size.
     *
     * @param g    The Graphics context to paint on.
     * @param size The size to scale the icon to.
     */
    @Override
    public void paint(Graphics g, double size) {
        int s = (int) Math.round(size);
        if (cachedImage != null && cachedImage.getWidth() == s && cachedImage.getHeight() == s) {
            g.drawImage(cachedImage, 0, 0, null);
        } else {
            BufferedImage image = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            paintBackground(image, size);
            paintComponent(g2d, size);
            g2d.dispose();
            cachedImage = image;
            g.drawImage(image, 0, 0, null);
        }
    }
    /**
     * Paints the gradient background onto the provided BufferedImage at the specified size.
     * The gradient is applied according to the corner colors c00, c10, c01, and c11.
     * @param img The BufferedImage to paint on.
     * @param size The size of the image (assumed to be square).
     */
    public void paintBackground(BufferedImage img, double size){
        for (int y = 0; y < size; y++) {
            double ty = y / (size - 1);
            Color leftColor = interpolate(c00, c01, ty);
            Color rightColor = interpolate(c10, c11, ty);
            for (int x = 0; x < size; x++) {
                double tx = x / (size - 1);
                Color pixelColor = interpolate(leftColor, rightColor, tx);
                if (isInRoundCorner(x, y, size, size * 0.1)) {
                    pixelColor = TRANSPARENT;
                } else {
                    img.setRGB(x, y, pixelColor.getRGB());
                }
            }
        }
    }
}
