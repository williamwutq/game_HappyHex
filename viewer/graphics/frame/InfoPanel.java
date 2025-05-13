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

package viewer.graphics.frame;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JComponent {
    private static final Color backgroundColor = new Color(136, 136, 136);
    private double size;
    /**
     * Creates a new {@code InfoPanel} to display game information
     */
    public InfoPanel(){
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setBackground(backgroundColor);
    }

    /**
     * Performs layout of child components by positioning and sizing
     * the individual {@code SevenSegment} displays.
     */
    public void doLayout() {
        int halfHeight = getHeight()/2;
        int halfWidth = getWidth()/2;
        size = Math.min((getHeight() - 6) / 6.0, (getWidth() - 6) / 6.0);
    }
    /**
     * Paints the background and then children components.
     *
     * @param g the graphics context.
     */
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        int size4 = (int) (size * 3);
        g2.setColor(this.getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, size4, size4);
        g2.dispose();
        paintChildren(g);
    }

    public static void main(String[] args){
        viewer.Viewer.test(new InfoPanel());
    }
}
