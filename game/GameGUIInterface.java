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

package game;

import hex.HexEngine;
import hex.Piece;

/**
 * The {@code GameGUIInterface} defines the contract for a graphical user interface (GUI)
 * that visualizes the state of a game. It allows the game to update and retrieve
 * key visual components such as the game engine state and action queue.
 *
 * @see HexEngine
 * @see Piece
 * @author William Wu
 * @version 1.4
 * @since 1.4
 */
public interface GameGUIInterface {
    /**
     * Updates the display to show the current state of the game engine.
     * @param engine the {@link HexEngine} representing the current game engine
     */
    void setEngine(HexEngine engine);
    /**
     * Updates the display to show the current {@code Piece} queue.
     * @param queue an array of {@link Piece} objects representing the piece queue
     */
    void setQueue(Piece[] queue);
    /**
     * Returns the currently displayed game engine state.
     * @return the current {@link HexEngine} as displayed in the GUI
     */
    HexEngine getEngine();
    /**
     * Returns the currently displayed {@code Piece} queue.
     * @return an array of {@link Piece} objects representing the displayed piece queue
     */
    Piece[] getQueue();
}
