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

package viewer.GameViewer.app.Contents.app.graphics.interactive;

import viewer.graphics.interactive.SevenSegment;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * {@code GeneralIndicator} is a Swing component that visually represents a fixed length
 * alphanumeric string using seven-segment displays.
 * <p>
 * Each character in the string is rendered on a {@code SevenSegment} display.
 * String content are fixed and cannot be changed except by using the {@link #set(String)} method.
 * To obtain the displayed string, use the {@link #getString()} method.
 * <p>
 * It uses a 7-segment emulation system that can be used in games or interfaces
 * resembling digital readouts or LED displays.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see viewer.graphics.interactive.SevenSegment
 * @see JComponent
 */
public class GeneralIndicator extends JComponent {
    private static final Color charColor = new Color(85, 85, 85);
    private final viewer.graphics.interactive.SevenSegment[] sevenSegments;
    private double size;
    /**
     * Constructs a {@code NameIndicator} with a specified character length.
     *
     * @param length the number of characters this indicator can display.
     */
    public GeneralIndicator(int length){
        this.setBackground(charColor);
        this.setLayout(null);
        this.setDoubleBuffered(true);
        sevenSegments = new viewer.graphics.interactive.SevenSegment[length];
        for (int i = 0; i < length; i ++){
            sevenSegments[i] = new viewer.graphics.interactive.SevenSegment();
            this.add(sevenSegments[i]);
        }
    }
    public boolean set(String str){
        str = str.trim();
        for (int i = 0; i < length(); i++) {
            char ch = ' ';
            if (i < str.length()) ch = str.charAt(i);
            sevenSegments[i].setCharacter(ch);
        }
        return (length() <= str.length());
    }
    /**
     * Clears the current string, resetting all segments.
     *
     * @return {@code true} if the display was non-empty and is now cleared,
     *         {@code false} if it was already empty.
     */
    public boolean clear(){
        for (int i = 0; i < length(); i++) {
            sevenSegments[i].setCharacter(' ');
        }
        return true;
    }
    /**
     * Returns the length of the current string.
     *
     * @return length of the displayed string, which might be shorter than the string entered.
     */
    public int length(){
        return sevenSegments.length;
    }
    /**
     * Returns the full string currently shown on the display.
     *
     * @return the string composed of segment characters.
     */
    public String getString(){
        StringBuilder sb = new StringBuilder(length());
        for (SevenSegment sevenSegment : sevenSegments) {
            sb.append(sevenSegment.getCharacter());
        }
        return sb.toString();
    }

    /**
     * Performs layout of child components by positioning and sizing
     * the individual {@code SevenSegment} displays.
     */
    public void doLayout() {
        int halfHeight = getHeight()/2;
        int halfWidth = getWidth()/2;
        size = Math.min((getHeight() - 4) / 6.0, (getWidth() - 4) / (double)(3 * sevenSegments.length + 1));
        int sizeHalfH = (int) (size * 2.25);
        int sizeQ = (int) (size*0.25);
        int sizeW = (int) (size*2.5);
        int sizeH = (int) (size*4.5);
        for (int i = 0; i < sevenSegments.length; i++) {
            int w = (int)((i - sevenSegments.length * 0.5) * 3 * size);
            sevenSegments[i].setBounds(w + sizeQ + halfWidth, halfHeight - sizeHalfH, sizeW, sizeH);
            sevenSegments[i].setCharSize(size);
        }
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
        g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, size4, size4);
        g2.dispose();
        paintChildren(g);
    }
}
