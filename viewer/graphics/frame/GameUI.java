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

import hexio.HexLogger;
import viewer.graphics.interactive.HexButton;
import viewer.logic.Tracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.io.IOException;

public class GameUI extends JComponent {
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private Tracker tracker;
    private final HexButton startButton;
    private final GamePanel gamePanel;

    public GameUI(HexLogger logger){
        tracker = new Tracker(logger);
        gamePanel = new GamePanel(tracker.engineAt(0), tracker.queueAt(0));
        startButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy + radius);
                path.lineTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy - radius);
                path.lineTo(cx + radius / sinOf60, cy);
                path.closePath();
                return path;
            }
        };
        this.add(gamePanel);
        this.add(startButton);
    }
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        gamePanel.setBounds(0, 0, w, h);
        double s = gamePanel.getActiveSize();
        double move = (gamePanel.getEngine().getRadius() - 1) * 3 * s * sinOf60;
        System.out.println(move);
        startButton.setBounds(0, 60, w / 5, h / 5);
    }
    public void paint(Graphics g){
        gamePanel.paint(g);
        startButton.paint(g);
    }

    public static void main(String[] args) throws IOException {
        HexLogger logger = HexLogger.generateBinaryLoggers().get(0);
        logger.readBinary();
        viewer.Viewer.test(new GameUI(logger));
    }
}
