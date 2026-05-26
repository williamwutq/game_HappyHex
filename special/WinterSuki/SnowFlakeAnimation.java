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
 * An animation that renders a snowflake dissolving piece by piece into nothing.
 * <p>
 * The snowflake has 6 arms radiating from the block center, each with symmetrical
 * branch pairs. Every arm segment and branch is assigned a random dissolve threshold
 * and fades out independently when the animation progress crosses that threshold,
 * producing a slow, natural-looking dissolution in a snowflake pattern.
 * The whole animation runs for 3000 frames at 1 ms per frame (3 seconds).
 * @since 1.2
 * @author William Wu
 * @version 1.2
 */
public class SnowFlakeAnimation extends Animation {
    private static final int ARM_COUNT = 6;
    private static final int SEG_PER_ARM = 10;
    private static final int BRANCH_PAIRS = 3;
    private static final int TOTAL_SEGMENTS = ARM_COUNT * (SEG_PER_ARM + BRANCH_PAIRS * 2);
    private static final Color ICY = new Color(200, 230, 255);

    private hex.Hex hex;
    private final double[] dissolveThresholds;

    public SnowFlakeAnimation(hex.Block block) {
        super(3000, 1);
        this.hex = block.thisHex();
        dissolveThresholds = new double[TOTAL_SEGMENTS];
        for (int i = 0; i < TOTAL_SEGMENTS; i++) {
            dissolveThresholds[i] = Math.random();
        }
        resetSize();
    }

    public final void resetSize() {
        if (hex == null) {
            Dimension d = new Dimension(1, 1);
            this.setSize(d); this.setMinimumSize(d); this.setMaximumSize(d); this.setPreferredSize(d);
            this.setBounds(new Rectangle(d));
        } else {
            double size = GUI.HexButton.getActiveSize();
            double extended = 2.0;
            int width  = (int) Math.round(extended * 2 * size * GameEssentials.sinOf60);
            int height = (int) Math.round(extended * 2 * size);
            Dimension d = new Dimension(width, height);
            this.setSize(d); this.setMinimumSize(d); this.setMaximumSize(d); this.setPreferredSize(d);
            int x = (int) Math.round(size * 2 * hex.X() + (1 - extended) * size * GameEssentials.sinOf60);
            int y = (int) Math.round(size * 2 * (hex.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75) + (1 - extended) * size);
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), width, height);
        }
    }

    private int segmentAlpha(int segIndex, double progress) {
        double threshold = dissolveThresholds[segIndex];
        if (progress >= threshold) return 0;
        double remaining = threshold - progress;
        double alpha = remaining < 0.12 ? remaining / 0.12 : 1.0;
        return (int)(alpha * 255);
    }

    private void fillBar(Graphics2D g, double x0, double y0, double x1, double y1, double hw, double angle) {
        double px = Math.cos(angle + Math.PI / 2) * hw;
        double py = Math.sin(angle + Math.PI / 2) * hw;
        int[] xs = {(int)(x0 + px), (int)(x1 + px), (int)(x1 - px), (int)(x0 - px)};
        int[] ys = {(int)(y0 + py), (int)(y1 + py), (int)(y1 - py), (int)(y0 - py)};
        g.fillPolygon(xs, ys, 4);
    }

    @Override
    protected void paintFrame(Graphics g, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double extended = 2.0;
        double cx = size * extended * GameEssentials.sinOf60;
        double cy = size * extended;
        double maxR = size * 0.85;
        double armW = Math.max(2.0, size * 0.045);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int segIdx = 0;
        for (int arm = 0; arm < ARM_COUNT; arm++) {
            double armAngle = Math.toRadians(30 + 60 * arm);

            // Main arm segments
            for (int seg = 0; seg < SEG_PER_ARM; seg++) {
                int alpha = segmentAlpha(segIdx++, progress);
                if (alpha <= 0) continue;
                g2d.setColor(new Color(ICY.getRed(), ICY.getGreen(), ICY.getBlue(), alpha));
                double r0 = (double) seg / SEG_PER_ARM * maxR;
                double r1 = (double)(seg + 1) / SEG_PER_ARM * maxR;
                fillBar(g2d,
                        cx + Math.cos(armAngle) * r0, cy + Math.sin(armAngle) * r0,
                        cx + Math.cos(armAngle) * r1, cy + Math.sin(armAngle) * r1,
                        armW, armAngle);
            }

            // Branch pairs: evenly spaced along the arm, shrinking toward the tip
            for (int bp = 0; bp < BRANCH_PAIRS; bp++) {
                double t = (double)(bp + 1) / (BRANCH_PAIRS + 1);
                double bR  = t * maxR;
                double bLen = maxR * 0.28 * (1.0 - t * 0.5);
                double bx0 = cx + Math.cos(armAngle) * bR;
                double by0 = cy + Math.sin(armAngle) * bR;

                // Left branch
                int alphaL = segmentAlpha(segIdx++, progress);
                if (alphaL > 0) {
                    g2d.setColor(new Color(ICY.getRed(), ICY.getGreen(), ICY.getBlue(), alphaL));
                    double baL = armAngle - Math.PI / 3;
                    fillBar(g2d, bx0, by0,
                            bx0 + Math.cos(baL) * bLen, by0 + Math.sin(baL) * bLen,
                            armW * 0.55, baL);
                }

                // Right branch
                int alphaR = segmentAlpha(segIdx++, progress);
                if (alphaR > 0) {
                    g2d.setColor(new Color(ICY.getRed(), ICY.getGreen(), ICY.getBlue(), alphaR));
                    double baR = armAngle + Math.PI / 3;
                    fillBar(g2d, bx0, by0,
                            bx0 + Math.cos(baR) * bLen, by0 + Math.sin(baR) * bLen,
                            armW * 0.55, baR);
                }
            }
        }
        g2d.dispose();
    }
}
