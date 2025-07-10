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

package GUI;

import javax.swing.*;
import java.awt.*;

public class PiecePanel extends JPanel{
    public PiecePanel() {
        super();
        this.setBackground(GameEssentials.gamePiecePanelColor);
        this.setLayout(null); // Use absolute
        int width = (int) Math.round(5 * HexButton.getActiveSize() * HexButton.sinOf60);
        int height = (int) Math.round(5 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        // Construct buttons
        for(int p = 0; p < GameEssentials.queue().length(); p ++) {
            for (int i = 0; i < 7; i++) {
                this.add(new PieceButton(p, i));
            }
        }
    }
    public void paint(java.awt.Graphics g) {
        int width = (int) Math.round(5 * HexButton.getActiveSize() * HexButton.sinOf60);
        int height = (int) Math.round(5 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
}
