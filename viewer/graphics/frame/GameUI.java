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
import viewer.logic.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;

public class GameUI extends JComponent {
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private static final double root2 = Math.sqrt(2);
    private final HexButton startButton, endButton, advanceButton, retreatButton;
    private final GamePanel gamePanel;
    private final Controller controller;

    public GameUI(){
        controller = new Controller();
        gamePanel = new GamePanel(new HexEngine(5), new Piece[]{});
        controller.bindGUI(gamePanel);
        controller.onFileChosen("data/39BBE0F249322343.hpyhex");

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
        endButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= root2 * 2;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(cx + radius, cy + radius);
                path.lineTo(cx - radius, cy + radius);
                path.lineTo(cx - radius, cy - radius);
                path.lineTo(cx + radius, cy - radius);
                path.closePath();
                return path;
            }
        };
        advanceButton = new HexButton(){
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
        retreatButton = new HexButton(){
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
        this.add(endButton);
        this.add(advanceButton);
        this.add(retreatButton);
    }
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        gamePanel.setBounds(0, 0, w, h);
        double s = gamePanel.getActiveSize();
        double move = (gamePanel.getEngine().getRadius() - 1) * 3 * s * sinOf60;
        startButton.setBounds(0, (int)move, w / 5, h / 5);
        endButton.setBounds(w * 4 / 5, (int)move, w / 5, h / 5);
        advanceButton.setBounds(0, 0, w / 5, h / 5);
        retreatButton.setBounds(w * 4 / 5, 0, w / 5, h / 5);
    }
    public void paint(Graphics g){
        gamePanel.paint(g);
        startButton.paint(g.create(startButton.getX(), startButton.getY(), startButton.getWidth(), startButton.getHeight()));
        endButton.paint(g.create(endButton.getX(), endButton.getY(), endButton.getWidth(), endButton.getHeight()));
        advanceButton.paint(g.create(advanceButton.getX(), advanceButton.getY(), advanceButton.getWidth(), advanceButton.getHeight()));
        retreatButton.paint(g.create(retreatButton.getX(), retreatButton.getY(), retreatButton.getWidth(), retreatButton.getHeight()));
    }

    public static void main(String[] args){
        viewer.Viewer.test(new GameUI());
    }
}
