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
  This theme is part of a special date-dependent default theme of the HappyHex Game.
  It can be used by any mods outside its targeting theme if appropriate. Original
  author of HappyHex William Wu reserve all rights.
*/

package special.Christmas;

import GUI.GameEssentials;
import GUI.animation.Animation;

import java.awt.*;

public class TreeAnimation extends Animation {
    public static final double sinOf60 = Math.sqrt(3) / 2;
    private static final int[][] colorArray = {
            // R, G, B
            {220, 151, 18},
            {207, 140, 86},
            {63, 189, 139},
            {209, 104, 127},
            {162, 118, 195},
            {50, 154, 189},
            {188, 185, 38},
            {168, 107, 61},
            {163, 84, 120},
            {113, 168, 41},
            {225, 80, 59},
            {122, 121, 121}
    };
    private static final double[][] treePoints = {
            // X, Y range: (-1, 1)
            {0, -1},
            {-0.6, -0.4},
            {-0.3, -0.4},
            {-0.9, 0.2},
            {-0.6, 0.2},
            {-1, 0.7},
            {-0.2, 0.7},
            {-0.2, 1},
            {0.2, 1},
            {0.2, 0.7},
            {1, 0.7},
            {0.6, 0.2},
            {0.9, 0.2},
            {0.3, -0.4},
            {0.6, -0.4},
    };
    private hex.Hex hex;
    private final int colorIndex;
    private static Color interpretColor(int index, int alpha){
        // Custom color interpreter that is faster
        if (index == -1 || index == 13){
            // If block is empty
            return new Color(68, 68, 68, alpha);
        } else if (index < -1 || index >= colorArray.length) {
            return new Color(122, 121, 121, alpha);
        } else {
            return new Color(colorArray[index][0], colorArray[index][1], colorArray[index][2], alpha);
        }
    }
    public TreeAnimation(hex.Block block) {
        super(2000, 1);
        hex = block.thisHex();
        colorIndex = block.getColor();
    }
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
            double extended = 1.5;
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * hex.X() + (1 - extended) * size);
            int y = (int) Math.round(size * 2 * (hex.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75) + (1 - extended) * size);
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), (int) Math.round(extended * 2 * size * GameEssentials.sinOf60), (int) Math.round(extended * 2 * size));
        }
    }

    @Override
    protected void paintFrame(Graphics graphics, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double extended = 1.5;
        int cooX = (int) Math.round(size * (sinOf60 + 0.5));
        int cooY = (int) Math.round(size * extended);
        int alpha = 255 - (int)(progress * progress * 255);
        final Color treeColor = new Color(0, 128, 0, alpha);
        final Color dominantColor = interpretColor(colorIndex, alpha);
        final Color sideColor = interpretColor(11 - colorIndex, alpha);
        // Note on special values:
        // For normal colorIndex, it will return a normal color.
        // For -1 (unoccupied), it will return gray.
        // For -2 (default), it will return dark gray.
        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        int[] xPoints = new int[treePoints.length];
        int[] yPoints = new int[treePoints.length];
        for (int i = 0; i < treePoints.length; i++) {
            xPoints[i] = (int) Math.round(cooX + size * treePoints[i][0] * sinOf60);
            yPoints[i] = (int) Math.round(cooY + size * treePoints[i][1]);
        }
        g2d.setColor(treeColor);
        g2d.fillPolygon(xPoints, yPoints, treePoints.length);
    }
}
