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

/*
  This animation is part of a special date-dependent default theme of the HappyHex Game.
  It can be used by any mods outside its targeting theme if appropriate. Original
  author of HappyHex William Wu reserve all rights.
*/

package special.WinterSuki;

import GUI.GameEssentials;
import GUI.animation.Animation;

import java.awt.*;

/**
 * An animation that pops up an ice cream cone centered on a {@link hex.Block},
 * replacing the standard {@link GUI.animation.CenteringEffect}.
 * <p>
 * The bottom scoop is colored using the block's game color; the top scoop is
 * white cream. The cone is a waffle tan with horizontal ruled lines. The whole
 * image fades in quickly, then slowly floats upward while fading out, giving
 * a light celebratory feel when a block is placed. The animation runs for
 * 2000 frames at 1 ms per frame (2 seconds), matching CenteringEffect's duration.
 * @since 1.2
 * @author William Wu
 * @version 1.2
 */
public class IceCreamAnimation extends Animation {
    private static final Color CONE_COLOR   = new Color(210, 170,  90);
    private static final Color WAFFLE_COLOR = new Color(170, 130,  50);
    private static final Color CREAM_COLOR  = new Color(245, 245, 250);
    private static final Color CHERRY_COLOR = new Color(220,  40,  60);
    private static final Color STEM_COLOR   = new Color( 60, 140,  60);

    private hex.Block block;
    private Color scoopColor;

    public IceCreamAnimation(hex.Block block) {
        super(2000, 1);
        this.block = block;
        this.scoopColor = GameEssentials.generateColor(block.getColor());
        resetSize();
    }

    public final void resetSize() {
        if (block == null) {
            Dimension d = new Dimension(1, 1);
            this.setSize(d); this.setMinimumSize(d); this.setMaximumSize(d); this.setPreferredSize(d);
            this.setBounds(new Rectangle(d));
        } else {
            double size = GUI.HexButton.getActiveSize();
            double extended = 2.5;
            int width  = (int) Math.round(extended * 2 * size * GameEssentials.sinOf60);
            int height = (int) Math.round(extended * 2 * size);
            Dimension d = new Dimension(width, height);
            this.setSize(d); this.setMinimumSize(d); this.setMaximumSize(d); this.setPreferredSize(d);
            int x = (int) Math.round(size * 2 * block.X() + (1 - extended) * size);
            int y = (int) Math.round(size * 2 * (block.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75) + (1 - extended) * size);
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), width, height);
        }
    }

    @Override
    protected void paintFrame(Graphics g, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double extended = 2.5;
        double cx = size * (extended + GameEssentials.sinOf60 - 1);
        double cy = size * extended;

        // Fade in over first 30%, then float up while fading out
        int alpha;
        double yOff;
        if (progress < 0.3) {
            double t = progress / 0.3;
            alpha = (int)(t * 255);
            yOff  = 0;
        } else {
            double t = (progress - 0.3) / 0.7;
            alpha = (int)((1 - t) * 255);
            yOff  = -size * 0.75 * t;
        }
        if (alpha <= 0) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        double scoopR    = size * 0.35;
        double coneHalfW = size * 0.30;
        double coneH     = size * 0.60;

        // Vertical anchor: cone rim sits at cy (block center), scoops extend upward
        double coneRimY  = cy + yOff;
        double coneTipY  = coneRimY + coneH;
        double botCY     = coneRimY - scoopR * 0.60;
        double topCY     = botCY    - scoopR * 1.55;
        double topR      = scoopR * 0.88;

        // --- Cone ---
        int[] coneXs = {(int)(cx - coneHalfW), (int)(cx + coneHalfW), (int)cx};
        int[] coneYs = {(int)coneRimY,          (int)coneRimY,         (int)coneTipY};
        g2d.setColor(new Color(CONE_COLOR.getRed(), CONE_COLOR.getGreen(), CONE_COLOR.getBlue(), alpha));
        g2d.fillPolygon(coneXs, coneYs, 3);

        // Waffle lines (clipped to cone triangle)
        g2d.setClip(new Polygon(coneXs, coneYs, 3));
        g2d.setColor(new Color(WAFFLE_COLOR.getRed(), WAFFLE_COLOR.getGreen(), WAFFLE_COLOR.getBlue(), alpha));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int line = 1; line <= 3; line++) {
            double lineY = coneRimY + coneH * line / 4.0;
            double t     = (lineY - coneRimY) / coneH;
            double hw    = coneHalfW * (1 - t) * 1.1;
            g2d.drawLine((int)(cx - hw), (int)lineY, (int)(cx + hw), (int)lineY);
        }
        g2d.setClip(null);

        // --- Bottom scoop (block's color) ---
        g2d.setColor(new Color(scoopColor.getRed(), scoopColor.getGreen(), scoopColor.getBlue(), alpha));
        g2d.fillOval((int)(cx - scoopR), (int)(botCY - scoopR), (int)(2 * scoopR), (int)(2 * scoopR));
        g2d.setColor(new Color(255, 255, 255, (int)(alpha * 0.35)));
        g2d.fillOval((int)(cx + scoopR * 0.1), (int)(botCY - scoopR * 0.65), (int)(scoopR * 0.28), (int)(scoopR * 0.28));

        // --- Top scoop (cream) ---
        g2d.setColor(new Color(CREAM_COLOR.getRed(), CREAM_COLOR.getGreen(), CREAM_COLOR.getBlue(), alpha));
        g2d.fillOval((int)(cx - topR), (int)(topCY - topR), (int)(2 * topR), (int)(2 * topR));
        g2d.setColor(new Color(255, 255, 255, (int)(alpha * 0.70)));
        g2d.fillOval((int)(cx + topR * 0.1), (int)(topCY - topR * 0.65), (int)(topR * 0.28), (int)(topR * 0.28));

        // --- Cherry on top ---
        double cherryR  = scoopR * 0.22;
        double cherryCY = topCY - topR - cherryR * 0.6;
        // Stem
        g2d.setColor(new Color(STEM_COLOR.getRed(), STEM_COLOR.getGreen(), STEM_COLOR.getBlue(), alpha));
        g2d.setStroke(new BasicStroke(Math.max(1f, (float)(size * 0.03))));
        g2d.drawLine((int)cx, (int)(topCY - topR), (int)(cx + cherryR * 0.4), (int)(cherryCY + cherryR));
        // Cherry
        g2d.setColor(new Color(CHERRY_COLOR.getRed(), CHERRY_COLOR.getGreen(), CHERRY_COLOR.getBlue(), alpha));
        g2d.fillOval((int)(cx + cherryR * 0.4 - cherryR), (int)(cherryCY - cherryR + cherryR), (int)(2 * cherryR), (int)(2 * cherryR));
        // Cherry highlight
        g2d.setColor(new Color(255, 200, 200, (int)(alpha * 0.6)));
        g2d.fillOval((int)(cx + cherryR * 0.4 - cherryR * 0.3), (int)(cherryCY - cherryR * 0.3 + cherryR), (int)(cherryR * 0.4), (int)(cherryR * 0.4));

        g2d.dispose();
    }
}
