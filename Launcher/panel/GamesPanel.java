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

package Launcher.panel;

import GUI.GameEssentials;
import Launcher.LaunchEssentials;

import javax.swing.*;
import java.awt.*;

public class GamesPanel extends JPanel {
    double size;
    public GamesPanel(){
        super();
        this.setOpaque(false);
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        size = 1;
    }

    public void doLayout(){
        size = Math.min(this.getBounds().width, this.getBounds().height) / 12.0;
    }


    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        int sizeInt = (int) size;
        int sizeHalf = (int) (size * 0.5);
        int sizeOneHalf = (int) (size * 1.5);
        int sizeThreeThird = (int) (size * 0.75);
        g2.setColor(LaunchEssentials.launchTitlePanelBackgroundColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(3+sizeHalf, 3+sizeHalf, getWidth()-6-sizeInt, getHeight()-6-sizeInt, sizeInt, sizeInt);
        g2.setColor(this.getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(3+sizeThreeThird, 3+sizeThreeThird, getWidth()-6-sizeOneHalf, getHeight()-6-sizeOneHalf, sizeHalf, sizeHalf);
        g2.dispose();
        paintChildren(g);
    }
}
