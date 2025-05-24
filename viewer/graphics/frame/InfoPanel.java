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
import viewer.logic.InfoGUIInterface;

import javax.swing.*;
import java.awt.*;

/**
 * {@code InfoPanel} is a Swing component designed to visually display the current score and turn
 * in a game interface using two {@link GeneralIndicator} seven-segment displays.
 * <p>
 * This component implement {@code InfoGUIInterface} to enable dynamic updates from viewer logic,
 * where it will receive updated turn and score information and use for display.
 * <p>
 * Each indicator displays a label (e.g., "sc:" for score, "tn:" for turn) followed by a
 * right-aligned numeric value, padded with spaces to fit exactly seven characters. The indicators
 * are rendered side-by-side and are styled with a rounded rectangular border and custom background.
 * The indicator use a String of 10 characters to display a number of maximum 7 digits, including
 * negative sign.
 * <p>
 * This component is not opaque and relies on the {@code GeneralIndicator} for displaying
 * text using a custom seven-segment display. It supports smooth resizing and dynamic updates via
 * {@link #setStats(int, int)} and {@link InfoGUIInterface}.
 * <p><b>Limitations:</b><br>
 * If the score or turn exceeds the digit capacity of the indicator (more than 7 digits),
 * the overflow digits will not be shown. This is by design and no exceptions will be thrown;
 * it is assumed these values remain within safe bounds during normal execution.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * InfoPanel panel = new InfoPanel();
 * panel.setStats(123, 5);  // Displays "sc:    123" and "tn:      5"
 * }</pre>
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see GeneralIndicator
 * @see InfoGUIInterface
 * @see JComponent
 */
public class InfoPanel extends JComponent implements InfoGUIInterface {
    private static final Color backgroundColor = new Color(85, 85, 85);
    private final GeneralIndicator scoreIndicator, turnIndicator;
    /**
     * Creates a new {@code InfoPanel} to display game information
     */
    public InfoPanel(){
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setBackground(backgroundColor);
        this.scoreIndicator = new GeneralIndicator(10);
        this.turnIndicator = new GeneralIndicator(10);
        this.scoreIndicator.set("sc:      0");
        this.turnIndicator.set("tn:      0");
        this.add(scoreIndicator);
        this.add(turnIndicator);
    }

    /**
     * Performs layout of child components
     */
    public void doLayout() {
        double sizeH = getHeight() / 6.0;
        double sizeW = getWidth() / 6.0;
        int h = (int) (sizeH * 5.75);
        int hb = (int) (sizeH * 0.125);
        int w = (int) (sizeW * 2.75);
        int wb = (int) (sizeW * 0.125);
        scoreIndicator.setBounds(wb, hb, w, h);
        turnIndicator.setBounds(w+3*wb, hb, w, h);
    }
    /**
     * Paints the children {@link GeneralIndicator}s.
     *
     * @param g the graphics context.
     */
    public void paint(Graphics g){
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

    /**
     * {@inheritDoc}
     * This set the score indicated by the score {@link GeneralIndicator} in this {@code InfoPanel}.
     * @see #getNumString
     * @see #setStats
     */
    public void setScore(int score) {
        this.scoreIndicator.set("sc:" + getNumString(score, 7));
        repaint();
    }
    /**
     * {@inheritDoc}
     * This set the turn indicated by the turn {@link GeneralIndicator} in this {@code InfoPanel}.
     * @see #getNumString
     * @see #setStats
     */
    public void setTurn(int turn) {
        this.turnIndicator.set("tn:" + getNumString(turn, 7));
        repaint();
    }
    /**
     * {@inheritDoc}
     * This returns the game score displayed by the score {@link GeneralIndicator}.
     */
    public int getScore() {
        String str = scoreIndicator.getString().substring(3).trim();
        int result = 0;
        try{
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {}
        return result;
    }
    /**
     * {@inheritDoc}
     * This returns the game turn displayed by the turn {@link GeneralIndicator}.
     */
    public int getTurn() {
        String str = turnIndicator.getString().substring(3).trim();
        int result = 0;
        try{
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {}
        return result;
    }
}
