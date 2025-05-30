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

package special.Valentine;

import GUI.GameEssentials;
import GUI.animation.Animation;

import java.awt.*;

public class HeartAnimation extends Animation {
    private hex.Hex hex;
    private Color color;
    public HeartAnimation(hex.Block block){
        super(2000, 1);
        this.hex = block.thisHex();
        this.color = new Color(255, 102, 153);
        resetSize();
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
    protected void paintFrame(Graphics g, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double extended = 1.5;
        progress = 1 - progress;
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(progress*255)));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);


        double radius = size * 0.8;

        // Formula: x^2+y^2 <= k*x^{4/9}*y*(ln(r))^2+r^2
        int coordinateX = (int) Math.round(size * (extended + GameEssentials.sinOf60 - 1));
        int coordinateY = (int) Math.round(size * (extended + 0.5));
        int limitX = (int) Math.round(radius * 1.6);
        int limitY = (int) Math.round(radius * 2.025);
        for (int x = 0; x <= limitX; x++) {
            for (int y = -limitY; y <= limitY; y++) {
                double leftSide = x * x + y * y;
                double rightSide = Math.pow(x, 4.0 / 9) * y * Math.pow(Math.log(radius), 2) + radius * radius;
                if (leftSide <= rightSide) {
                    if (x == 0){
                        graphics.fillRect(coordinateX, coordinateY - y, 1, 1);
                    } else {
                        // Use symmetry over the y-axis
                        graphics.fillRect(coordinateX + x, coordinateY - y, 1, 1);
                        graphics.fillRect(coordinateX - x, coordinateY - y, 1, 1);
                    }
                }
            }
        }
    }
}
