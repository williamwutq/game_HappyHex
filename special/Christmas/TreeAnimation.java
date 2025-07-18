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
    private hex.Hex hex;
    public TreeAnimation(hex.Block block) {
        super(2000, 1);
        hex = block.thisHex();
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
    }
}
