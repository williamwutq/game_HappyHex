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
 * @see JComponent
 */
public final class NameIndicator extends JComponent{
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
        sevenSegments[0].setCharacter('-');
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
        sevenSegments[0].setCharacter('-');
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
                sevenSegments[pointer].setCharacter('-');
            } else if (pointer == 1) {
                hidden = ' ';
                pointer --;
                sevenSegments[pointer].setCharacter('-');
            } else {
                pointer--;
                sevenSegments[pointer].setCharacter(' ');
                if(pointer == cursor) sevenSegments[pointer].setCharacter('-');
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
                hidden = sevenSegments[cursor - 1].character;
                for (int i = cursor; i < pointer; i++) {
                    sevenSegments[i - 1].setCharacter(sevenSegments[i].character);
                }
                if (pointer - 1 < sevenSegments.length) sevenSegments[pointer - 1].setCharacter(' ');
                pointer--;
                cursor--;
                sevenSegments[cursor].setCharacter('-');
                return true;
            } else if (cursor > 0 && cursor == pointer) {
                sevenSegments[cursor].setCharacter(' ');
                pointer--;
                cursor--;
                hidden = ' ';
                sevenSegments[cursor].setCharacter('-');
                return true;
            } else if (cursor == 0 && pointer > 0) {
                hidden = sevenSegments[cursor + 1].character;
                for (int i = cursor + 1; i < pointer; i++) {
                    sevenSegments[i - 1].setCharacter(sevenSegments[i].character);
                }
                if (pointer - 1 < sevenSegments.length) sevenSegments[pointer - 1].setCharacter(' ');
                pointer--;
                sevenSegments[cursor].setCharacter('-');
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
                    sevenSegments[i + 1].setCharacter(sevenSegments[i].character);
                }
                sevenSegments[cursor].setCharacter(c);
                cursor++;
                pointer++;
                if (cursor < sevenSegments.length) sevenSegments[cursor].setCharacter('-');
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
            hidden = sevenSegments[cursor].character;
            sevenSegments[cursor].setCharacter('-');
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
            hidden = sevenSegments[cursor].character;
            sevenSegments[cursor].setCharacter('-');
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
            if (sevenSegment.character == '-'){
                sb.append(hidden);
            } else sb.append(sevenSegment.character);
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
    /**
     * Internal component representing a single 7-segment digit.
     */
    private final class SevenSegment extends JComponent{
        private static final boolean[][] data = {
                {true, true, true, false, true, true, true},
                {false, false, true, false, false, true, false},
                {true, false, true, true, true, false, true},
                {true, false, true, true, false, true, true},
                {false, true, true, true, false, true, false},
                {true, true, false, true, false, true, true},
                {true, true, false, true, true, true, true},
                {true, false, true, false, false, true, false},
                {true, true, true, true, true, true, true},
                {true, true, true, true, false, true, true},
                {true, true, true, true, true, true, false},
                {false, true, false, true, true, true, true},
                {true, true, false, false, true, false, true},
                {false, false, true, true, true, true, true},
                {true, true, false, true, true, false, true},
                {true, true, false, true, true, false, false}
        };
        private static final double[][] refPosX = {
                {0.5, 0.25, 0.5, 2, 2.25, 2},
                {0, 0.25, 0.5, 0.5, 0.25, 0},
                {2, 2.25, 2.5, 2.5, 2.25, 2},
                {0.5, 0.25, 0.5, 2, 2.25, 2},
                {0, 0.25, 0.5, 0.5, 0.25, 0},
                {2, 2.25, 2.5, 2.5, 2.25, 2},
                {0.5, 0.25, 0.5, 2, 2.25, 2}
        };
        private static final double[][] refPosY = {
                {0, 0.25, 0.5, 0.5, 0.25, 0},
                {0.5, 0.25, 0.5, 2, 2.25, 2},
                {0.5, 0.25, 0.5, 2, 2.25, 2},
                {2, 2.25, 2.5, 2.5, 2.25, 2},
                {2.5, 2.25, 2.5, 4, 4.25, 4},
                {2.5, 2.25, 2.5, 4, 4.25, 4},
                {4, 4.25, 4.5, 4.5, 4.25, 4}
        };
        private char character;
        private boolean[] states;
        /** Creates an empty seven-segment display component. */
        private SevenSegment(){
            this.character = ' ';
            states = new boolean[7];
        }
        /**
         * Sets the displayed character and updates the segment states.
         *
         * @param character character to display (0-9, A-F, a-f, or '-').
         */
        private void setCharacter(char character){
            this.character = character;
            states = SevenSegment.getStates(character);
        }
        /**
         * Maps a character to its segment states.
         *
         * @param c character to map.
         * @return boolean array representing which segments to light.
         */
        private static boolean[] getStates(char c){
            if (c == '-'){
                boolean[] result = new boolean[7];
                result[3] = true;
                return result;
            } else if ('0' <= c && c <= '9'){
                return data[c - '0'];
            } else if ('A' <= c && c <= 'F') {
                return data[c - 55];
            } else if ('a' <= c && c <= 'f') {
                return data[c - 87];
            } else return new boolean[7];
        }
        /**
         * Paints the current character using white-filled polygon segments.
         *
         * @param g the graphics context.
         */
        public void paint(Graphics g){
            // Draw key background
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            int[] positionX = new int[6];
            int[] positionY = new int[6];
            for (int k = 0; k < 7; k ++){
                if (states[k]){
                    for (int i = 0; i < 6; i++){
                        positionX[i] = (int) (size * refPosX[k][i]);
                        positionY[i] = (int) (size * refPosY[k][i]);
                    }
                    g2.fillPolygon(positionX, positionY, 6);
                }
            }
        }
    }
}
