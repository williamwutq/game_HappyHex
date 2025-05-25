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
 * The {@code InfoGUIInterface} defines the contract for a graphical user interface (GUI)
 * that visualizes the score and turns of a game. It allows the {@link Controller} or any game
 * controller to update and retrieve the game score and turn to be displayed in graphics.
 * <p>
 * This interface is intended to be implemented by any GUI class that displays and interacts
 * with a {@link Tracker}-driven game simulation or replay system.
 *
 * @see Controller
 * @see Tracker
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 */
public interface InfoGUIInterface {
    /**
     * Updates the display to show the current game score of the game.
     * @param score the new game score
     */
    void setScore(int score);
    /**
     * Updates the display to show the current game turn of the game.
     * @param turn the new game turn
     */
    void setTurn(int turn);
    /**
     * Returns the currently displayed game score.
     * @return the current game score as displayed in the GUI
     */
    int getScore();
    /**
     * Returns the currently displayed game turn.
     * @return the current game turn as displayed in the GUI
     */
    int getTurn();
}
