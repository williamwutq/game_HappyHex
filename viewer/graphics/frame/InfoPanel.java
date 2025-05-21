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

import viewer.graphics.interactive.GeneralIndicator;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JComponent {
    private static final Color backgroundColor = new Color(136, 136, 136);
    private final GeneralIndicator scoreIndicator, turnIndicator;
    private double size;
    /**
     * Creates a new {@code InfoPanel} to display game information
     */
    public InfoPanel(){
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setBackground(backgroundColor);
        this.scoreIndicator = new GeneralIndicator(10);
        this.turnIndicator = new GeneralIndicator(10);
        this.scoreIndicator.set("sc:        0");
        this.turnIndicator.set("tn:        0");
        this.add(scoreIndicator);
        this.add(turnIndicator);
    }

    /**
     * Performs layout of child components
     */
    public void doLayout() {
        double sizeH = (getHeight() - 6) / 6.0;
        double sizeW = (getWidth() - 6) / 6.0;
        size = Math.min(sizeH, sizeW);
        int h = (int) (sizeH * 5.75);
        int hb = (int) (sizeH * 0.125);
        int w = (int) (sizeW * 2.75);
        int wb = (int) (sizeW * 0.125);
        scoreIndicator.setBounds(3+wb, 3+hb, w, h);
        turnIndicator.setBounds(3+w+3*wb, 3+hb, w, h);
    }
    /**
     * Paints the background and then children components.
     *
     * @param g the graphics context.
     */
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        int sizeH = (int) (size * 0.5);
        g2.setColor(this.getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke((int) (size / 12.0)));
        g2.drawRoundRect(3, 3, getWidth()-6, getHeight()-6, sizeH, sizeH);
        g2.setColor(this.getParent().getBackground());
        g2.dispose();
        paintChildren(g);
    }

    /**
     * Set the score and turn indicated by the two {@link GeneralIndicator}s in this {@code InfoPanel}.
     * This will override the current indicated score and turn.
     *
     * @param score the new score to be indicated by the score indicator on the left
     * @param turn the new turn to be indicated by the turn indicator on the right
     * @see #getNumString
     * @see GeneralIndicator
     */
    public void setStats(int score, int turn){
        this.scoreIndicator.set("sc:" + getNumString(score, 7));
        this.turnIndicator.set("tn:" + getNumString(turn, 7));
        repaint();
    }
    /**
     * Returns a right-aligned string representation of a number,
     * padded with spaces to fit the specified total length.
     * <p>
     * For negative numbers, one space less is used for padding
     * due to the minus sign being part of the digit count.
     *
     * @param num the integer to convert to a string
     * @param len the total length of the resulting string, including padding
     * @return a string representation of the number, right-aligned and space-padded to the given length
     */
    private static String getNumString(int num, int len){
        StringBuilder str = new StringBuilder();
        int digits = countDigits(num);
        if (num < 0){
            str.append(" ".repeat(Math.max(0, len - digits - 1)));
            str.append(num);
        } else {
            str.append(" ".repeat(Math.max(0, len - digits)));
            str.append(num);
        } return str.toString();
    }
    /**
     * Recursively counts the number of digits in the absolute value of an integer.
     *
     * @param num the integer whose digits are to be counted
     * @return the number of digits in the absolute value of {@code num}
     */
    private static int countDigits(int num) {
        if (num < 0) {
            return countDigits(-num);
        } else if (num < 10) return 1;
        return 1 + countDigits(num / 10);
    }

    public static void main(String[] args){
        InfoPanel panel = new InfoPanel();
        viewer.Viewer.test(panel);
        for (int i = 0; i < 1000000; i ++){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {}
            panel.setStats(i*1000, -i*80);
        }
    }
}
