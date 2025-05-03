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

package GUI.animation;

import GUI.GameEssentials;

import java.awt.*;

/**
 * An animation that creates a wave-like, shrinking, and fading effect centered on a hexagonal block.
 * <p>
 * This class extends {@link Animation} to produce a dynamic visual effect where a polygon, initially centered
 * on a {@link hex.Hex} coordinate from a {@link hex.Block}, shrinks and fades over 2000 frames with a 1ms delay
 * per frame. The polygon's shape resembles a wave, with crests and troughs that oscillate, creating a pattern where
 * crests periodically transform into troughs and vice versa. This effect is ideal for visually indicating the
 * disappearance or dissolution of hexagonal blocks in the game.
 */
public class DisappearEffect extends Animation {
    private hex.Hex hex;
    private Color color;
    public DisappearEffect(hex.Block block){
        super(2000, 1);
        this.hex = block.thisHex();
        this.color = new Color(block.color().getRed(), block.color().getGreen(), block.color().getBlue());
        resetSize();
    }
    /**
     * Resets the size and position of the animation component based on the hexagonal coordinates.
     * <p>
     * This method calculates the dimensions and bounds of the animation using the active hexagon size from
     * {@link GUI.HexButton#getActiveSize()} and the coordinates from the {@link hex.Block} object. The
     * component is sized to fit a hexagon and positioned at the block's center, adjusted by offsets from
     * {@link GameEssentials#getGamePanelWidthExtension()} and
     * {@code GameEssentials#getGamePanelHeightExtension()}
     * to align correctly within the game panel.
     */
    public final void resetSize(){
        if(hex == null) {
            Dimension minDimension = new Dimension(1,1);
            this.setSize(minDimension);
            this.setMinimumSize(minDimension);
            this.setMaximumSize(minDimension);
            this.setPreferredSize(minDimension);
            this.setBounds(new Rectangle(minDimension));
        } else {
            double size = GUI.HexButton.getActiveSize();
            int width = (int) Math.round(2 * size * GameEssentials.sinOf60);
            int height = (int) Math.round(2 * size);
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * hex.X());
            int y = (int) Math.round(size * 2 * (hex.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75));
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), (int) Math.round(2 * size * GameEssentials.sinOf60), (int) Math.round(2 * size));
        }
    }
    /**
     * Renders a single frame of the animation, drawing a wave-like polygon that shrinks and fades.
     * <p>
     * This method draws a 40-point {@link Polygon} in a polar coordinate system, where the radius of each point varies
     * based on the animation's progress and a {@link Math#sin sinusoidal} function. The radius formula creates a
     * wave-like pattern with oscillating crests and troughs, which shift as crests become troughs and vice versa,
     * driven by the interplay of two sine terms offset by the progress. The polygon's size scales down as progress
     * decreases, and its opacity fades linearly from fully opaque to transparent. The color is derived from the block,
     * with alpha value adjusted by progress, and the polygon is centered on the hexagonal block's coordinates.
     * <p>
     * This method is not at all related to hexagon painting and only use the coordinate information from the block.
     *
     * @param g the {@code Graphics} context to draw on.
     * @param progress a value between 0 and 1 indicating the animation's progress.
     */
    @Override
    protected void paintFrame(Graphics g, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double ratio = 0.13;
        progress = 1 - progress;
        int[] xPoints = new int[40];
        int[] yPoints = new int[40];
        for (int i = 0; i < 40; i++) {
            double angle = Math.toRadians(9 * i);
            double radius = ratio * size * progress * (8 + Math.sin(20 * (angle - progress)) - Math.sin(20 * (angle + progress)));
            xPoints[i] = (int) Math.round(Math.cos(angle) * radius + size * GameEssentials.sinOf60);
            yPoints[i] = (int) Math.round(Math.sin(angle) * radius + size);
        }
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(progress*255)));
        g.fillPolygon(xPoints, yPoints, 40);
    }
}
