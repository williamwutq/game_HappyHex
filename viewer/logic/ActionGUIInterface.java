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

package viewer.logic;

/**
 * The {@code ActionGUIInterface} is a GUI interface that handles user actions
 * related to game moves and animation control in the HappyHex game viewer.
 * It provides methods for updating the GUI when the game move is incremented,
 * decremented, or when the animation speed is changed, started, or stopped.
 * <p>
 * This interface is intended to be implemented by any GUI class that needs to
 * respond to these specific game actions.
 *
 * @author William Wu
 * @version 1.1 (HappyHex 1.4)
 * @since 1.1 (HappyHex 1.4)
 */
public interface ActionGUIInterface {
    /**
     * Called by the controller to update the GUI when the game move is incremented.
     */
    void onIncrement();
    /**
     * Called by the controller to update the GUI when the game move is decremented.
     */
    void onDecrement();
    /**
     * Called by the controller to update the GUI when the game animation speed is changed.
     */
    void onSpeedChanged(int delay);
    /**
     * Called by the controller to update the GUI when the game animation is started.
     */
    void onRunStart();
    /**
     * Called by the controller to update the GUI when the game animation is stopped.
     */
    void onRunStop();
}
