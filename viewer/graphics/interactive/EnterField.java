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
import java.awt.event.ActionListener;

/**
 * A user input component combining a {@link Keyboard} and a {@link NameIndicator}.
 * This component allows numerical and alphabetical input, displays the current state
 * of the input buffer, and provides editing functionality.
 * <p>
 * The input contains a cursor represented by "-", and its position can be adjusted with keys.
 * The display can be clicked upon, which will toggle the keyboard. Toggle the keyboard off
 * will automatically confirm the input if a full input is made and not confirmed.
 *
 * <p>The following buttons below serve the described functions:</p>
 * <ul>
 *   <li>0–9, A–F: Appends the corresponding character</li>
 *   <li>>: Move cursor to the right</li>
 *   <li><: Move cursor to the left</li>
 *   <li>END: Move cursor to the end of the buffer</li>
 *   <li>STT: Move cursor to the start of the buffer</li>
 *   <li>+: Repeats the cursor position character</li>
 *   <li>-: Removes the last character</li>
 *   <li>DEL: Deletes the cursor position character</li>
 *   <li>CLR: Clears the entire buffer</li>
 *   <li>ENT: Enter the current buffer, if full, keep it</li>
 * </ul>
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
    private boolean keyboardShown;

    /**
     * Constructs a new {@code EnterField} with a specified input length.
     *
     * @param length the maximum number of characters accepted
     */
    public EnterField(int length) {
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setBackground(Color.DARK_GRAY);

        this.indicator = new NameIndicator(length);
        this.indicator.addActionListener(this::onIndicatorClick);
        this.keyboardShown = true;
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
        if (key.equals("DEL")) {
            indicator.removeChar();
        } else if (key.equals("CLR")) {
            indicator.clear();
        } else if (key.equals("-")) {
            indicator.removeEnd();
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
            indicator.lock();
        } else {
            indicator.addChar(key.charAt(0));
        }
        this.repaint();
    }
    /**
     * Handles button events from the embedded {@link NameIndicator}.
     * Clicking on the indicator toggle whether the keyboard will be shown.
     *
     * @param e the action event representing a key press
     */
    private void onIndicatorClick(ActionEvent e) {
        if (keyboardShown) {
            indicator.lock();
            this.remove(keyboard);
        } else {
            this.add(keyboard);
        }
        keyboardShown = !keyboardShown;
        this.revalidate();
        this.repaint();
    }

    /**
     * Sets the layout of the subcomponents.
     * If the {@link Keyboard} is shown, the {@link NameIndicator} is placed above the {@code Keyboard}.
     * Otherwise, the {@code NameIndicator} will be made to full size.
     */
    public void doLayout() {
        int h = getHeight();
        int w = getWidth();

        if (keyboardShown) {
            int indicatorHeight = h * 2 / 15;
            int keyboardHeight = h - indicatorHeight;

            indicator.setBounds(0, 0, w, indicatorHeight);
            keyboard.setBounds(0, indicatorHeight, w, keyboardHeight);
        } else {
            indicator.setBounds(0, 0, w, h);
        }
    }

    /**
     * Whether the keyboard is shown in this component.
     * @return true of the keyboard is currently shown, false otherwise.
     */
    public boolean isKeyboardShown(){
        return keyboardShown;
    }
}

