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

package viewer.graphics.interactive;

import javax.swing.*;
import java.awt.*;

/**
 * {@code NameIndicator} is a Swing component that visually represents a user-defined
 * alphanumeric string using seven-segment displays.
 * <p>
 * Each character (0-9, A-F, a-f, or '-') is rendered on a {@code SevenSegment} display.
 * Characters are displayed left-to-right and a cursor marks the current insertion index.
 * The component supports adding, inserting, removing, and clearing characters.
 * <p>
 * It uses a 7-segment emulation system that can be used in games or interfaces
 * resembling digital readouts or LED displays.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see SevenSegment
 * @see JComponent
 * @see JButton
 */
public final class NameIndicator extends JButton {
    private static final Color charColor = new Color(85, 85, 85);
    private final SevenSegment[] sevenSegments;
    private int pointer;
    private int cursor;
    private boolean locked;
    private char hidden;
    private double size;
    /**
     * Constructs a {@code NameIndicator} with a specified character length.
     *
     * @param length the number of characters this indicator can display.
     */
    public NameIndicator(int length){
        this.setBackground(charColor);
        this.setLayout(null);
        this.setDoubleBuffered(true);
        locked = false;
        pointer = 0;
        cursor = 0;
        hidden = ' ';
        sevenSegments = new SevenSegment[length];
        for (int i = 0; i < length; i ++){
            sevenSegments[i] = new SevenSegment();
            this.add(sevenSegments[i]);
        }
        sevenSegments[0].setCharacter('_');
    }
    /**
     * Clears the current string, resetting all segments.
     *
     * @return {@code true} if the display was non-empty and is now cleared,
     *         {@code false} if it was already empty.
     */
    public boolean clear(){
        if (pointer == 0) return false;
        locked = false;
        pointer = 0;
        cursor = 0;
        hidden = ' ';
        sevenSegments[0].setCharacter('_');
        for (int i = 1; i < sevenSegments.length; i++) {
            sevenSegments[i].setCharacter(' ');
        }
        return true;
    }
    /**
     * Removes the character at the end of the display.
     *
     * @return {@code true} if a character was removed,
     *         {@code false} if the display was empty.
     */
    public boolean removeEnd(){
        if (pointer > 0 && !locked) {
            if (pointer < sevenSegments.length) sevenSegments[pointer].setCharacter(' ');
            if (pointer == cursor) {
                cursor--;
                hidden = ' ';
                pointer --;
                sevenSegments[pointer].setCharacter('_');
            } else if (pointer == 1) {
                hidden = ' ';
                pointer --;
                sevenSegments[pointer].setCharacter('_');
            } else {
                pointer--;
                sevenSegments[pointer].setCharacter(' ');
                if(pointer == cursor) sevenSegments[pointer].setCharacter('_');
            }
            return true;
        } else return false;
    }
    /**
     * Removes the character at cursor from the display, or if the cursor is at end of
     * the character array, remove the last character
     *
     * @return {@code true} if a character was removed,
     *         {@code false} if the display was empty.
     */
    public boolean removeChar(){
        if (!locked){
            if (cursor > 0 && cursor < pointer) {
                hidden = sevenSegments[cursor - 1].getCharacter();
                for (int i = cursor; i < pointer; i++) {
                    sevenSegments[i - 1].setCharacter(sevenSegments[i].getCharacter());
                }
                if (pointer - 1 < sevenSegments.length) sevenSegments[pointer - 1].setCharacter(' ');
                pointer--;
                cursor--;
                sevenSegments[cursor].setCharacter('_');
                return true;
            } else if (cursor > 0 && cursor == pointer) {
                sevenSegments[cursor].setCharacter(' ');
                pointer--;
                cursor--;
                hidden = ' ';
                sevenSegments[cursor].setCharacter('_');
                return true;
            } else if (cursor == 0 && pointer > 0) {
                hidden = sevenSegments[cursor + 1].getCharacter();
                for (int i = cursor + 1; i < pointer; i++) {
                    sevenSegments[i - 1].setCharacter(sevenSegments[i].getCharacter());
                }
                if (pointer - 1 < sevenSegments.length) sevenSegments[pointer - 1].setCharacter(' ');
                pointer--;
                sevenSegments[cursor].setCharacter('_');
                return true;
            }
        } return false;
    }
    /**
     * Adds a character to the position of cursor.
     * Only hexadecimal characters (0-9, A-F, a-f) are accepted.
     *
     * @param c the character to add.
     * @return {@code true} if the character was added,
     *         {@code false} if the display is full or character is invalid.
     */
    public boolean addChar(char c){
        if (!locked && (('0' <= c && c <= '9') || ('A' <= c && c <= 'F') || ('a' <= c && c <= 'f'))){
            if (cursor == sevenSegments.length-1 && pointer < sevenSegments.length) {
                hidden = c;
                pointer ++;
            } else if (cursor <= pointer && pointer < sevenSegments.length) {
                for (int i = pointer - 1; i >= cursor; i--) {
                    sevenSegments[i + 1].setCharacter(sevenSegments[i].getCharacter());
                }
                sevenSegments[cursor].setCharacter(c);
                cursor++;
                pointer++;
                if (cursor < sevenSegments.length) sevenSegments[cursor].setCharacter('_');
                return true;
            }
        } return false;
    }
    /**
     * Retrieves the cursor position character.
     *
     * @return the character at cursor position, or space if not yet entered.
     */
    public char getChar(){
        return hidden;
    }

    /**
     * Increment the cursor position.
     *
     * @return {@code true} if the cursor was incremented,
     *         {@code false} if the cursor cannot be incremented because it reached the end.
     */
    public boolean incrementCursor(){
        if (cursor < pointer && cursor < sevenSegments.length-1 && !locked){
            sevenSegments[cursor].setCharacter(hidden);
            cursor ++;
            hidden = sevenSegments[cursor].getCharacter();
            sevenSegments[cursor].setCharacter('_');
            return true;
        } else return false;
    }
    /**
     * Decrement the cursor position.
     *
     * @return {@code true} if the cursor was decremented,
     *         {@code false} if the cursor cannot be decremented because it was at the start.
     */
    public boolean decrementCursor(){
        if (cursor > 0 && !locked){
            sevenSegments[cursor].setCharacter(hidden);
            cursor --;
            hidden = sevenSegments[cursor].getCharacter();
            sevenSegments[cursor].setCharacter('_');
            return true;
        } else return false;
    }
    /**
     * Lock this {@code NameIndicator}.
     *
     * @return {@code true} if the indicator is filled and locked,
     *         {@code false} if the cursor cannot be locked because it is not fill.
     */
    public boolean lock(){
        if (!locked && pointer == sevenSegments.length){
            sevenSegments[cursor].setCharacter(hidden);
            hidden = ' ';
            locked = true;
            return true;
        } else return false;
    }
    /**
     * Returns the current length of the entered string.
     *
     * @return number of characters currently displayed (excluding dash, which is used to indicating entering position).
     */
    public int getStringLength(){
        return pointer;
    }
    /**
     * Returns the full string currently shown on the display.
     *
     * @return the string composed of segment characters.
     */
    public String getString(){
        StringBuilder sb = new StringBuilder(sevenSegments.length);
        for (SevenSegment sevenSegment : sevenSegments) {
            if (sevenSegment.getCharacter() == '_'){
                sb.append(hidden);
            } else sb.append(sevenSegment.getCharacter());
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
        size = Math.min((getHeight() - 6) / 6.0, (getWidth() - 6) / (double)(3 * sevenSegments.length + 1));
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
        g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, size4, size4);
        g2.dispose();
        paintChildren(g);
    }
}
