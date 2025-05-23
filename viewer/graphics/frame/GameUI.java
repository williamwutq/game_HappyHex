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

import hex.HexEngine;
import hex.Piece;
import viewer.graphics.interactive.HexButton;
import viewer.graphics.interactive.SpeedSlider;
import viewer.logic.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;

public class GameUI extends JComponent {
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private static final double root2 = Math.sqrt(2);
    private final HexButton forwardButton, backwardButton, advanceButton, retreatButton;
    private final GamePanel gamePanel;
    private Controller controller;
    private final SpeedSlider slider;
    private boolean forward, backward;

    public GameUI(Controller controller){
        this.forward = true;
        this.backward = true;
        this.controller = controller;
        this.slider = new SpeedSlider();
        this.gamePanel = new GamePanel(new HexEngine(5), new Piece[]{});
        controller.bindGameGUI(gamePanel);
        controller.onFileChosen("data/39BBE0F249322343.hpyhex");

        forwardButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                if (forward){
                    backward = true;
                    backwardButton.updateCachedCustomPath();
                    controller.run();
                } else controller.stop();
                forward = !forward;
                updateCachedCustomPath();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2;
                Path2D.Double path = new Path2D.Double();
                if (forward) {
                    path.moveTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy + radius);
                    path.lineTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy - radius);
                    path.lineTo(cx + radius / sinOf60, cy);
                } else {
                    radius /= root2;
                    path.moveTo(cx + radius, cy + radius);
                    path.lineTo(cx - radius, cy + radius);
                    path.lineTo(cx - radius, cy - radius);
                    path.lineTo(cx + radius, cy - radius);
                }
                path.closePath();
                return path;
            }
        };
        backwardButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                if (backward){
                    forward = true;
                    forwardButton.updateCachedCustomPath();
                    controller.back();
                } else controller.stop();
                backward = !backward;
                updateCachedCustomPath();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2;
                Path2D.Double path = new Path2D.Double();
                if (backward) {
                    path.moveTo(cx + radius * (sinOf60 * 2 - 1 / sinOf60), cy + radius);
                    path.lineTo(cx + radius * (sinOf60 * 2 - 1 / sinOf60), cy - radius);
                    path.lineTo(cx - radius / sinOf60, cy);
                } else {
                    radius /= root2;
                    path.moveTo(cx + radius, cy + radius);
                    path.lineTo(cx - radius, cy + radius);
                    path.lineTo(cx - radius, cy - radius);
                    path.lineTo(cx + radius, cy - radius);
                }
                path.closePath();
                return path;
            }
        };
        advanceButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                controller.advance();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2 * sinOf60;
                double width = radius / 4;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(cx - width, cy + radius);
                path.lineTo(cx + width * 2, cy);
                path.lineTo(cx - width, cy - radius);
                path.lineTo(cx + width, cy - radius);
                path.lineTo(cx + width * 4, cy);
                path.lineTo(cx + width, cy + radius);
                path.closePath();
                return path;
            }
        };
        retreatButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                controller.retreat();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2 * sinOf60;
                double width = radius / 4;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(cx + width, cy + radius);
                path.lineTo(cx - width * 2, cy);
                path.lineTo(cx + width, cy - radius);
                path.lineTo(cx - width, cy - radius);
                path.lineTo(cx - width * 4, cy);
                path.lineTo(cx - width, cy + radius);
                path.closePath();
                return path;
            }
        };
        slider.setSpeedChangeListener(new SpeedSlider.SpeedChangeListener() {
            public void onSpeedChanged(double newSpeed) {
                newSpeed = 1-newSpeed;
                int base = 1024;
                int interval = (int)Math.pow(base, newSpeed);
                controller.setSpeed(interval);
            }
        });
        slider.setKnobPosition(0.5);
        controller.setSpeed(32);

        this.add(gamePanel);
        this.add(forwardButton);
        this.add(backwardButton);
        this.add(advanceButton);
        this.add(retreatButton);
        this.add(slider);
    }
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        int er = gamePanel.getEngine().getRadius();

        double halfHeight = h/2.0-3;
        double halfWidth = w/2.0-3;
        int length = er * 2 - 1;
        double vertical = er * 1.5 + 2;
        double s = (Math.min(halfHeight / vertical, halfWidth / sinOf60 / length));
        gamePanel.setBounds(3, 3, w, h - (int)(s));
        s = gamePanel.getActiveSize(); // update size

        int vs = (int)((er * 1.5 - 0.25) * s); // vertical shift
        int bb = h/2 + (int)((er * 1.5 - 4) * s) + 3; // button bound
        int tb = h/2 - (int)((er * 1.5 + 2.5) * s) + 3; // top bound
        int sb = h/2 + (int)((er * 1.5 + 1.5) * s) + 3; // slider bound
        int hs = (int)((er * 2 * sinOf60 - sinOf60) * s); // horizontal shift
        int lb = w/2 - hs; // left bound
        int rb = w/2 + hs; // right bound
        int r = Math.min(vs * 3 / 8, hs * 3 / 8); // button radius
        slider.setBounds(lb, sb, hs * 2, r / 3);
        backwardButton.setBounds(lb, bb - r, r, r);
        forwardButton.setBounds(rb - r, bb - r, r, r);
        retreatButton.setBounds(lb, tb, r, r);
        advanceButton.setBounds(rb - r, tb, r, r);
    }
    public void paint(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        gamePanel.paint(g.create(gamePanel.getX(), gamePanel.getY(), gamePanel.getWidth(), gamePanel.getHeight()));
        forwardButton.paint(g.create(forwardButton.getX(), forwardButton.getY(), forwardButton.getWidth(), forwardButton.getHeight()));
        backwardButton.paint(g.create(backwardButton.getX(), backwardButton.getY(), backwardButton.getWidth(), backwardButton.getHeight()));
        advanceButton.paint(g.create(advanceButton.getX(), advanceButton.getY(), advanceButton.getWidth(), advanceButton.getHeight()));
        retreatButton.paint(g.create(retreatButton.getX(), retreatButton.getY(), retreatButton.getWidth(), retreatButton.getHeight()));
        slider.paint(g.create(slider.getX(), slider.getY(), slider.getWidth(), slider.getHeight()));
    }

    public static void main(String[] args){
        viewer.Viewer.test(new GameUI(new Controller()));
    }
}
