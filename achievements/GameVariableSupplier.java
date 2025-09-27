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

package achievements;

import hex.GameState;
import hex.Piece;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A functional interface that represents a supplier of game-related variables based on the current GameState.
 * This interface extends Function<GameState, T>, allowing it to take a GameState as input and produce a value of type T.
 * It includes several predefined suppliers for common game variables such as score, turn, engine length, etc.
 * <p>
 * This is a functional auxiliary for {@link GameState}.
 * @param <T> the type of the value supplied
 * @see GameState
 * @see Function
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public interface GameVariableSupplier<T> extends Function<GameState, T> {
    /**
     * Converts a Function to a GameVariableSupplier.
     * @param func the function to convert
     * @return a GameVariableSupplier that applies the function to the GameState
     * @param <T> the type of the value supplied
     */
    static <T> GameVariableSupplier<T> of(Function<GameState, T> func) {return func::apply;}
    /**
     * Converts a Supplier to a GameVariableSupplier that ignores the GameState parameter.
     * @param supplier the supplier to convert
     * @return a GameVariableSupplier that always returns the value from the supplier
     * @param <T> the type of the value supplied
     */
    static <T> GameVariableSupplier<T> of(Supplier<T> supplier) { return s -> supplier.get(); }
    /**
     * Creates a GameVariableSupplier that always returns the given constant value.
     * @param value the constant value to return
     * @return a GameVariableSupplier that always returns the given value
     * @param <T> the type of the value supplied
     */
    static <T> GameVariableSupplier<T> constant(T value) { return s -> value; }
    /** A constant supplier that always returns 0. */
    GameVariableSupplier<Integer> ZERO = constant(0);
    /** A constant supplier that always returns 1. */
    GameVariableSupplier<Integer> ONE = constant(1);
    /** A supplier that returns the length of the engine, which is the total number of blocks in the engine. */
    GameVariableSupplier<Integer> LENGTH = s -> (s == null || s.getEngine() == null) ? 0 : s.getEngine().length();
    /** A supplier that returns the radius of the engine. */
    GameVariableSupplier<Integer> RADIUS = s -> (s == null || s.getEngine() == null) ? 0 : s.getEngine().getRadius();
    /** A supplier that returns the number of lines in the engine, which is radius * 2 - 1. */
    GameVariableSupplier<Integer> LINES = s -> (s == null || s.getEngine() == null) ? 0 : s.getEngine().getRadius() * 2 - 1;
    /** A supplier that returns the size of the piece queue. */
    GameVariableSupplier<Integer> SIZE = s -> (s == null || s.getQueue() == null) ? 0 : s.getQueue().length;
    /** A supplier that returns the first piece in the piece queue, or null if the queue is empty. */
    GameVariableSupplier<Piece> FIRST = s -> (s == null || s.getQueue() == null) ? null : s.getQueue()[0];
    /** A supplier that returns the last piece in the piece queue, or null if the queue is empty. */
    GameVariableSupplier<Piece> LAST = s -> (s == null || s.getQueue() == null) ? null : s.getQueue()[s.getQueue().length - 1];
    /** A supplier that returns the current score of the game. */
    GameVariableSupplier<Integer> SCORE = s -> (s == null) ? 0 : s.getScore();
    /** A supplier that returns the current turn number of the game. */
    GameVariableSupplier<Integer> TURN = s -> (s == null) ? 0 : s.getTurn();
    /** A supplier that returns the percentage of the engine that is filled, from 0.0 to 1.0. */
    GameVariableSupplier<Double> FILL = s -> (s == null || s.getEngine() == null) ? 0.0 : s.getEngine().getPercentFilled();
    /** A supplier that returns the entropy of the engine, which is a measure of how disordered the blocks are. */
    GameVariableSupplier<Double> ENTROPY = s -> (s == null || s.getEngine() == null) ? 0.0 : s.getEngine().computeEntropy();
}
