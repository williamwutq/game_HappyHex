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

import viewer.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A user input component combining a {@link Keyboard} and a {@link NameIndicator}.
 * This component allows numerical and alphabetical input, displays the current state
 * of the input buffer, and provides editing functionality.
 *
 * <p>Only the following buttons are currently functional:</p>
 * <ul>
 *   <li>0–9, A–F: Appends the corresponding character</li>
 *   <li>+: Repeats the last character</li>
 *   <li>-: Removes the last character</li>
 *   <li>DEL: Deletes the last character</li>
 *   <li>CLR: Clears the entire buffer</li>
 * </ul>
 *
 * <p>The buttons <b>HOM</b>, <b>&lt;</b>, <b>&gt;</b>, <b>CNF</b>, and <b>ENT</b> are present
 * but currently disabled.</p>
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see Keyboard
 * @see NameIndicator
 */
public final class EnterField extends JComponent {

    private final NameIndicator indicator;
    private final Keyboard keyboard;
    private final int maxLength;

    /**
     * Constructs a new {@code EnterField} with a specified input length.
     *
     * @param length the maximum number of characters accepted
     */
    public EnterField(int length) {
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setBackground(Color.DARK_GRAY);

        this.maxLength = length;

        this.indicator = new NameIndicator(length);
        this.keyboard = new Keyboard(this::onKeyPress);

        add(indicator);
        add(keyboard);
    }

    /**
     * Handles key press events from the embedded {@link Keyboard}.
     *
     * @param e the action event representing a key press
     */
    private void onKeyPress(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (!cmd.startsWith("press ")) return;
        String key = cmd.substring(6);
        if (key.equals("DEL") || key.equals("-")) {
            indicator.removeChar();
        } else if (key.equals("CLR")) {
            indicator.clear();
        } else if (key.equals("+")) {
            indicator.addChar(indicator.getChar());
        } else if (key.equals("END")) {
            boolean possible = indicator.incrementCursor();
            while(possible){
                possible = indicator.incrementCursor();
            }
        } else if (key.equals("STT")) {
            boolean possible = indicator.decrementCursor();
            while(possible){
                possible = indicator.decrementCursor();
            }
        } else if (key.equals(">")) {
            indicator.incrementCursor();
        } else if (key.equals("<")) {
            indicator.decrementCursor();
        } else if (key.equals("ENT")) {
            // To be implemented
        } else {
            indicator.addChar(key.charAt(0));
        }
        this.repaint();
    }

    /**
     * Sets the layout of the subcomponents.
     * The {@link NameIndicator} is placed above the {@link Keyboard}.
     */
    public void doLayout() {
        int h = getHeight();
        int w = getWidth();

        int indicatorHeight = h * 2 / 15;
        int keyboardHeight = h - indicatorHeight;

        indicator.setBounds(0, 0, w, indicatorHeight);
        keyboard.setBounds(0, indicatorHeight, w, keyboardHeight);
    }

    public static void main(String[] args){
        Viewer.test(new EnterField(16));
    }
}

