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

import viewer.graphics.interactive.SevenSegment;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A utility class to help with keyboard input handling in a JFrame.
 * <p>
 * This class provides a method to attach a key listener to a JFrame that listens for
 * various key presses and notifies an ActionListener with the corresponding action command.
 * It supports alphanumeric keys, arrow keys, and several special keys.
 * </p>
 *
 * @author William Wu
 * @version 1.1 (HappyHex 1.4)
 * @since 1.1 (HappyHex 1.4)
 */
public class KeyboardHelper {
    /**
     * Attaches a key listener to the given JFrame that listens for key presses
     * and notifies the provided ActionListener with the corresponding action command.
     * <p>
     * This method binds keys a-z, A-Z, 0-9, arrow keys, and several special keys
     * (like ENTER, DELETE, BACK_SPACE, TAB, END, HOME, CLEAR) to the ActionListener.
     *
     * @param frame the JFrame to attach the listener to
     * @param listener the ActionListener to notify on key presses
     */
    public static void attachListener(JFrame frame, ActionListener listener) {
        JRootPane rootPane = frame.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // Bind a-z keys
        for (char c = 'a'; c <= 'z'; c++) {
            bindKey(frame, inputMap, actionMap, listener, c + "", false);
        }
        // Bind A-Z keys
        for (char c = 'A'; c <= 'Z'; c++) {
            bindKey(frame, inputMap, actionMap, listener, c + "", true);
        }
        // Bind 0-9 keys
        for (char c = '0'; c <= '9'; c++) {
            bindKey(frame, inputMap, actionMap, listener, c + "", false);
        }
        // Bind Arrow keys
        bindKey(frame, inputMap, actionMap, listener, "LEFT", false);
        bindKey(frame, inputMap, actionMap, listener, "RIGHT", false);
        bindKey(frame, inputMap, actionMap, listener, "UP", false);
        bindKey(frame, inputMap, actionMap, listener, "DOWN", false);
        // Bind Special keys
        bindSpecialKey(frame, inputMap, actionMap, listener, "ENTER", "ENT");
        bindSpecialKey(frame, inputMap, actionMap, listener, "DELETE", "DEL");
        bindSpecialKey(frame, inputMap, actionMap, listener, "BACK_SPACE", "-");
        bindSpecialKey(frame, inputMap, actionMap, listener, "TAB", "+");
        bindSpecialKey(frame, inputMap, actionMap, listener, "END", "END");
        bindSpecialKey(frame, inputMap, actionMap, listener, "HOME", "STT");
        bindSpecialKey(frame, inputMap, actionMap, listener, "CLEAR", "CLR");
        // Bind special keys for SevenSegment
        for (char character : SevenSegment.getSupportedSpecialCharacters()){
            if (character == '-' || character == '+' || character == '<' || character == '>') {
                bindSpecialKey(frame, inputMap, actionMap, listener, character, "Literal" + character);
            } else {
                bindSpecialKey(frame, inputMap, actionMap, listener, character, String.valueOf(character));
            }
        }
        // ESC key
        bindSpecialKey(frame, inputMap, actionMap, listener, "ESCAPE", "ESC");
    }
    /**
     * Binds a key to an action in the given frame.
     * @param frame the JFrame to bind the key to
     * @param inputMap the InputMap to use for key bindings
     * @param actionMap the ActionMap to use for action bindings
     * @param listener the ActionListener to notify when the key is pressed
     * @param keyName a String representing the name of the key to bind (e.g., "A", "B", "1")
     * @param shiftPressed whether the key is pressed with Shift (for uppercase letters)
     */
    private static void bindKey(JFrame frame, InputMap inputMap, ActionMap actionMap, ActionListener listener, String keyName, boolean shiftPressed){
        String actionName = "press_" + keyName;
        String strokeName = shiftPressed ? "shift " + keyName : keyName.toUpperCase();
        inputMap.put(KeyStroke.getKeyStroke(strokeName), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "press " + keyName));
            }
        });
    }
    /**
     * Binds a special key to an action in the given frame.
     * @param frame the JFrame to bind the key to
     * @param inputMap the InputMap to use for key bindings
     * @param actionMap the ActionMap to use for action bindings
     * @param listener the ActionListener to notify when the key is pressed
     * @param keyName a String representing the name of the key to bind (e.g., "ENTER", "DELETE")
     * @param keyNameAs a String representing the name to use in the action command (e.g., "ENT", "DEL")
     */
    private static void bindSpecialKey(JFrame frame, InputMap inputMap, ActionMap actionMap, ActionListener listener, String keyName, String keyNameAs){
        String actionName = "press_" + keyNameAs;
        inputMap.put(KeyStroke.getKeyStroke(keyName), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "press " + keyNameAs));
            }
        });
    }
    /**
     * Binds a special key to an action in the given frame.
     * @param frame the JFrame to bind the key to
     * @param inputMap the InputMap to use for key bindings
     * @param actionMap the ActionMap to use for action bindings
     * @param listener the ActionListener to notify when the key is pressed
     * @param keyName a character representing the name of the key to bind (e.g., '-', '+')
     * @param keyNameAs a String representing the name to use in the action command (e.g., "MINUS", "PLUS")
     */
    private static void bindSpecialKey(JFrame frame, InputMap inputMap, ActionMap actionMap, ActionListener listener, char keyName, String keyNameAs){
        String actionName = "press_" + keyNameAs;
        inputMap.put(KeyStroke.getKeyStroke(keyName), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "press " + keyNameAs));
            }
        });
    }
}