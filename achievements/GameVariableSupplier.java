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
import hex.HexEngine;
import hex.Piece;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Returns a predefined GameVariableSupplier based on the given name.
     * Recognized names (case-insensitive) include:
     * <ul>
     *     <li><b>zero or 0</b> - returns a supplier that always returns 0</li>
     *     <li><b>one or 1</b> - returns a supplier that always returns 1</li>
     *     <li><b>pi or π</b> - returns a supplier that always returns the mathematical constant π (pi)</li>
     *     <li><b>hex or 6</b> - returns a supplier that always returns 6, representing the number of sides on a hexagon</li>
     *     <li><b>length or len</b> - returns a supplier that provides the length of the engine</li>
     *     <li><b>radius or r</b> - returns a supplier that provides the radius of the engine</li>
     *     <li><b>lines or l</b> - returns a supplier that provides the number of lines in the engine</li>
     *     <li><b>size or s</b> - returns a supplier that provides the size of the piece queue</li>
     *     <li><b>first</b> - returns a supplier that provides the first piece in the piece queue</li>
     *     <li><b>last</b> - returns a supplier that provides the last piece in the piece queue</li>
     *     <li><b>score</b> - returns a supplier that provides the current score of the game</li>
     *     <li><b>turn or turns</b> - returns a supplier that provides the current turn number of the game</li>
     *     <li><b>fill, filled, percentfilled, percent_filled, or percent-filled</b> - returns a supplier
     *         that provides the percentage of the engine that is filled</li>
     *     <li><b>entropy</b> - returns a supplier that provides the entropy of the engine</li>
     *     <li>Any valid integer string (e.g., "42") - returns a supplier that always returns that integer</li>
     *     <li>Any valid double string (e.g., "3.14") - returns a supplier that always returns that double</li>
     * </ul>
     * If the name is not recognized, an IllegalArgumentException is thrown.
     * @param name the name of the predefined GameVariableSupplier
     * @return the corresponding GameVariableSupplier
     * @throws IllegalArgumentException if the name is not recognized
     */
    static GameVariableSupplier<?> of(String name) {
        return switch (name.trim().toLowerCase()) {
            case "zero", "0" -> ZERO;
            case "one", "1" -> ONE;
            case "pi", "π" -> PI;
            case "hex", "6" -> HEX;
            case "length", "len" -> LENGTH;
            case "radius", "r" -> RADIUS;
            case "lines", "l" -> LINES;
            case "size", "s" -> SIZE;
            case "first" -> FIRST;
            case "last" -> LAST;
            case "score" -> SCORE;
            case "turn", "turns" -> TURN;
            case "fill", "filled", "percentfilled", "percent_filled", "percent-filled" -> FILL;
            case "entropy" -> ENTROPY;
            default -> {
                // Is this an integer?
                try {
                    int val = Integer.parseInt(name);
                    yield constant(val);
                } catch (NumberFormatException ignored) {}
                // Is this a double?
                try {
                    double val = Double.parseDouble(name);
                    yield constant(val);
                } catch (NumberFormatException ignored) {}
                throw new IllegalArgumentException("Unknown GameVariableSupplier: " + name);
            }
        };
    }
    /**
     * Casts a GameVariableSupplier of a Number type to a GameVariableSupplier of Integer.
     * If the input supplier is null, null is returned.
     * The resulting supplier will convert the Number to an Integer using intValue().
     * @param supplier the GameVariableSupplier to cast
     * @return a GameVariableSupplier<Integer> that converts the result of the input supplier to Integer, or null if the input is null
     * @param <N> the type of Number supplied by the input supplier
     */
    static <N extends Number> GameVariableSupplier<Integer> castInt(GameVariableSupplier<N> supplier){
        return (GameVariableSupplier<Integer>) Optional.ofNullable(supplier).map(s -> s.andThen(Number::intValue)).orElse(null);
    }
    /**
     * Casts a GameVariableSupplier of an unknown type to a GameVariableSupplier of Integer.
     * The resulting supplier will attempt to convert the result of the input supplier to an Integer.
     * If the result is a Number, it will use intValue().
     * If the result is a Piece, it will use the byte value of the Piece.
     * If the result is null or cannot be converted, null is returned.
     * @param supplier the GameVariableSupplier to cast
     * @return a GameVariableSupplier<Integer> that converts the result of the input supplier to Integer, or null if conversion is not possible
     */
    static GameVariableSupplier<Integer> castIntUnknown(GameVariableSupplier<?> supplier){
        return t -> {
            Object result = supplier.apply(t);
            if (result instanceof Number num){
                return num.intValue();
            } else if (result instanceof Piece p) {
                return (int) p.toByte();
            } else return null;
        };
    }
    /**
     * Casts a GameVariableSupplier of a Number type to a GameVariableSupplier of Double.
     * If the input supplier is null, null is returned.
     * The resulting supplier will convert the Number to a Double using doubleValue().
     * @param supplier the GameVariableSupplier to cast
     * @return a GameVariableSupplier<Double> that converts the result of the input supplier to Double, or null if the input is null
     * @param <N> the type of Number supplied by the input supplier
     */
    static <N extends Number> GameVariableSupplier<Double> castDouble(GameVariableSupplier<N> supplier){
        return (GameVariableSupplier<Double>) Optional.ofNullable(supplier).map(s -> s.andThen(Number::doubleValue)).orElse(null);
    }
    /**
     * Casts a GameVariableSupplier of unknown type to a GameVariableSupplier of Double.
     * If the input supplier is null, null is returned.
     * The resulting supplier will convert the result to a Double if it is a Number or a Piece.
     * If the result is a Number, it uses doubleValue(); if it's a Piece, it uses toByte().
     * If the result is neither, it returns null.
     * @param supplier the GameVariableSupplier to cast
     * @return a GameVariableSupplier<Double> that converts the result of the input supplier to Double, or null if the input is null
     */
    static GameVariableSupplier<Double> castDoubleUnknown(GameVariableSupplier<?> supplier){
        return t -> {
            Object result = supplier.apply(t);
            if (result instanceof Number num){
                return num.doubleValue();
            } else if (result instanceof Piece p) {
                return (double) p.toByte();
            } else return null;
        };
    }
    /**
     * Applies a unary operation to the result of a GameVariableSupplier<Integer>.
     * Supported operations (case-insensitive) include:
     * <ul>
     *     <li><b>-</b>, <b>neg</b>, <b>negate</b>, <b>negative</b> - negation</li>
     *     <li><b>abs</b>, <b>absolute</b> - absolute value</li>
     *     <li><b>sq</b>, <b>sqr</b>, <b>square</b>, <b>squared</b> - square the value</li>
     *     <li><b>sqrt</b>, <b>squareroot</b>, <b>square_root</b>, <b>square-root</b> - square root of the value</li>
     *     <li><b>bool</b>, <b>boolean</b> - converts the integer to a boolean (0 becomes 0, non-zero becomes 1)</li>
     * </ul>
     * If the operation is not recognized, an IllegalArgumentException is thrown.
     * @param x the GameVariableSupplier<Integer> to apply the operation to
     * @param name the name of the operation
     * @return a new GameVariableSupplier<Integer> that applies the operation
     * @throws IllegalArgumentException if the operation is not recognized
     */
    static GameVariableSupplier<Integer> integerOperation(GameVariableSupplier<Integer> x, String name){
        return switch (name) {
            case "-", "neg", "negate", "negative" -> s -> -x.apply(s);
            case "abs", "absolute" -> s -> Math.abs(x.apply(s));
            case "sq", "sqr", "square", "squared" -> s -> {
                Integer val = x.apply(s);
                return val == null ? null : val * val;
            };
            case "sqrt", "squareroot", "square_root", "square-root" -> s -> {
                Integer val = x.apply(s);
                return val == null ? null : (int) Math.sqrt(val);
            };
            case "bool", "boolean" -> s -> {
                Integer val = x.apply(s);
                return (val == null) ? null : (val != 0 ? 1 : 0);
            };
            case "not", "!" -> s -> {
                Integer val = x.apply(s);
                return (val == null) ? null : (val == 0 ? 1 : 0);
            };
            default -> throw new IllegalArgumentException("Unknown operation: " + name);
        };
    }
    /**
     * Applies a unary operation to the result of a GameVariableSupplier<Double>.
     * Supported operations (case-insensitive) include:
     * <ul>
     *     <li><b>-</b>, <b>neg</b>, <b>negate</b>, <b>negative</b> - negation</li>
     *     <li><b>abs</b>, <b>absolute</b> - absolute value</li>
     *     <li><b>sq</b>, <b>sqr</b>, <b>square</b>, <b>squared</b> - square the value</li>
     *     <li><b>sqrt</b>, <b>squareroot</b>, <b>square_root</b>, <b>square-root</b> - square root of the value</li>
     * </ul>
     * If the operation is not recognized, an IllegalArgumentException is thrown.
     * @param x the GameVariableSupplier<Double> to apply the operation to
     * @param name the name of the operation
     * @return a new GameVariableSupplier<Double> that applies the operation
     * @throws IllegalArgumentException if the operation is not recognized
     */
    static GameVariableSupplier<Double> doubleOperation(GameVariableSupplier<Double> x, String name){
        return switch (name) {
            case "-", "neg", "negate", "negative" -> s -> -x.apply(s);
            case "abs", "absolute" -> s -> Math.abs(x.apply(s));
            case "sq", "sqr", "square", "squared" -> s -> {
                Double val = x.apply(s);
                return val == null ? null : val * val;
            };
            case "sqrt", "squareroot", "square_root", "square-root" -> s -> {
                Double val = x.apply(s);
                return val == null ? null : Math.sqrt(val);
            };
            default -> throw new IllegalArgumentException("Unknown operation: " + name);
        };
    }
    /**
     * Applies a binary operation to the results of two GameVariableSupplier<Integer> instances.
     * Supported operations (case-insensitive) include:
     * <ul>
     *     <li><b>+</b>, <b>adds</b>, <b>add</b>, <b>plus</b>, <b>addition</b> - addition</li>
     *     <li><b>-</b>, <b>subtracts</b>, <b>subtract</b>, <b>minus</b>, <b>subtraction</b> - subtraction</li>
     *     <li><b>*</b>, <b>multiplies</b>, <b>multiply</b>, <b>times</b>, <b>time</b>, <b>multiplication</b> - multiplication</li>
     *     <li><b>/</b>, <b>divides</b>, <b>divide</b>, <b>division</b> - division (returns null if dividing by zero)</li>
     *     <li><b>%</b>, <b>mod</b>, <b>modulo</b>, <b>modulos</b>, <b>remainder</b> - modulo (returns null if modulo by zero)</li>
     *     <li><b>^</b>, <b>pow</b>, <b>power</b>, <b>exp</b>, <b>exponent</b> - exponentiation (val1 raised to the power of val2)</li>
     *     <li><b>max</b>, <b>maximum</b> - maximum of the two values</li>
     *     <li><b>min</b>, <b>minimum</b> - minimum of the two values</li>
     *     <li><b>avg</b>, <b>average</b>, <b>mean</b> - average of the two values (integer division)</li>
     *     <li><b>equals</b>, <b>equal</b>, <b>==</b>, <b>is</b>, <b>same</b> - equality check (returns 1 if equal, 0 otherwise)</li>
     *     <li><b>not_equals</b>, <b>not_equal</b>, <b>!=</b>, <b>not</b>, <b>is_not</b>, <b>not_same</b> - inequality check
     *         (returns 1 if not equal, 0 otherwise)</li>
     * </ul>
     * If the operation is not recognized, an IllegalArgumentException is thrown.
     * @param v1 the first GameVariableSupplier<Integer>
     * @param v2 the second GameVariableSupplier<Integer>
     * @param name the name of the operation
     * @return a new GameVariableSupplier<Integer> that applies the operation
     * @throws IllegalArgumentException if the operation is not recognized
     */
    static GameVariableSupplier<Integer> integerOperation(GameVariableSupplier<Integer> v1, GameVariableSupplier<Integer> v2, String name) {
        return switch (name) {
            case "+", "adds", "add", "plus", "addition" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return val1 + val2;
            };
            case "-", "subtracts", "subtract", "minus", "subtraction" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return val1 - val2;
            };
            case "*", "multiplies ","multiply", "times", "time", "multiplication" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return val1 * val2;
            };
            case "/", "divides", "divide", "division" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null || val2 == 0) return null;
                return val1 / val2;
            };
            case "%", "mod", "modulo", "modulos", "remainder" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null || val2 == 0) return null;
                return val1 % val2;
            };
            case "^", "pow", "power", "exp", "exponent" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (int) Math.pow(val1, val2);
            };
            case "max", "maximum" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return Math.max(val1, val2);
            };
            case "min", "minimum" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return Math.min(val1, val2);
            };
            case "avg", "average", "mean" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (val1 + val2) / 2;
            };
            case "equals", "equal", "==", "is", "same" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (val1.equals(val2)) ? 1 : 0;
            };
            case "not_equals", "not_equal", "!=", "not", "is_not", "not_same" -> s -> {
                Integer val1 = v1.apply(s);
                Integer val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (!val1.equals(val2)) ? 1 : 0;
            };
            default -> throw new IllegalArgumentException("Unknown operation: " + name);
        };
    }
    /**
     * Applies a binary operation to the results of two GameVariableSupplier<Double> instances.
     * Supported operations (case-insensitive) include:
     * <ul>
     *     <li><b>+</b>, <b>adds</b>, <b>add</b>, <b>plus</b>, <b>addition</b> - addition</li>
     *     <li><b>-</b>, <b>subtracts</b>, <b>subtract</b>, <b>minus</b>, <b>subtraction</b> - subtraction</li>
     *     <li><b>*</b>, <b>multiplies</b>, <b>multiply</b>, <b>times</b>, <b>time</b>, <b>multiplication</b> - multiplication</li>
     *     <li><b>/</b>, <b>divides</b>, <b>divide</b>, <b>division</b> - division (returns null if dividing by zero)</li>
     *     <li><b>%</b>, <b>mod</b>, <b>modulo</b>, <b>modulos</b>, <b>remainder</b> - modulo (returns null if modulo by zero)</li>
     *     <li><b>^</b>, <b>pow</b>, <b>power</b>, <b>exp</b>, <b>exponent</b> - exponentiation (val1 raised to the power of val2)</li>
     *     <li><b>max</b>, <b>maximum</b> - maximum of the two values</li>
     *     <li><b>min</b>, <b>minimum</b> - minimum of the two values</li>
     *     <li><b>avg</b>, <b>average</b>, <b>mean</b> - average of the two values (integer division)</li>
     *     <li><b>equals</b>, <b>equal</b>, <b>==</b>, <b>is</b>, <b>same</b> - equality check (returns 1 if equal, 0 otherwise)</li>
     *     <li><b>equals_exact</b>, <b>equal_exact</b>, <b>===</b>, <b>is_exact</b>, <b>same_exact</b> - exact equality check
     *         (returns 1 if exactly equal, 0 otherwise)</li>
     *     <li><b>not_equals</b>, <b>not_equal</b>, <b>!=</b>, <b>not</b>, <b>is_not</b>, <b>not_same</b> - inequality check
     *         (returns 1 if not equal, 0 otherwise)</li>
     * </ul>
     * If the operation is not recognized, an IllegalArgumentException is thrown.
     * @param v1 the first GameVariableSupplier<Double>
     * @param v2 the second GameVariableSupplier<Double>
     * @param name the name of the operation
     * @return a new GameVariableSupplier<Double> that applies the operation
     * @throws IllegalArgumentException if the operation is not recognized
     */
    static GameVariableSupplier<Double> doubleOperation(GameVariableSupplier<Double> v1, GameVariableSupplier<Double> v2, String name) {
        return switch (name) {
            case "+", "adds", "add", "plus", "addition" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return val1 + val2;
            };
            case "-", "subtracts", "subtract", "minus", "subtraction" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return val1 - val2;
            };
            case "*", "multiplies ","multiply", "times", "time", "multiplication" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return val1 * val2;
            };
            case "/", "divides", "divide", "division" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null || val2 == 0) return null;
                return val1 / val2;
            };
            case "%", "mod", "modulo", "modulos", "remainder" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null || val2 == 0) return null;
                return val1 % val2;
            };
            case "^", "pow", "power", "exp", "exponent" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return Math.pow(val1, val2);
            };
            case "max", "maximum" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return Math.max(val1, val2);
            };
            case "min", "minimum" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return Math.min(val1, val2);
            };
            case "avg", "average", "mean" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (val1 + val2) / 2;
            };
            case "equals", "equal", "==", "is", "same" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (Math.abs(val1 - val2) < Math.ulp(Math.max(Math.abs(val1), Math.abs(val2)))) ? 1.0 : 0;
            };
            case "equals_exact", "equal_exact", "===", "is_exact", "same_exact" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (val1.equals(val2)) ? 1.0 : 0;
            };
            case "not_equals", "not_equal", "!=", "not", "is_not", "not_same" -> s -> {
                Double val1 = v1.apply(s);
                Double val2 = v2.apply(s);
                if (val1 == null || val2 == null) return null;
                return (Math.abs(val1 - val2) >= Math.ulp(Math.max(Math.abs(val1), Math.abs(val2)))) ? 1.0 : 0;
            };
            default -> throw new IllegalArgumentException("Unknown operation: " + name);
        };
    }
    /**
     * Converts a GameVariableSupplier that supplies Piece objects into one that supplies their corresponding pattern integers.
     * If the input supplier returns null, the resulting supplier will return -1.
     * @param pieceSupplier the GameVariableSupplier that supplies Piece objects
     * @return a GameVariableSupplier<Integer> that supplies the pattern integer of the Piece, or -1 if the Piece is null
     */
    static GameVariableSupplier<Integer> patternOf(GameVariableSupplier<Piece> pieceSupplier) {
        return t -> Optional.ofNullable(pieceSupplier.apply(t))
                .map(p -> (int)p.toByte())
                .orElse(-1);
    }
    /**
     * Converts a GameVariableSupplier that supplies pattern integers into one that supplies their corresponding Piece objects.
     * If the input supplier returns a value outside the range 0-127, the resulting supplier will return null to prevent exception being thrown.
     * @param patternSupplier the GameVariableSupplier that supplies pattern integers
     * @return a GameVariableSupplier<Piece> that supplies the Piece corresponding to the pattern integer, or null if out of range
     */
    static GameVariableSupplier<Piece> pieceOf(GameVariableSupplier<Integer> patternSupplier) {
        return t -> Optional.ofNullable(patternSupplier.apply(t))
                .filter(n -> n >= 0 && n <= 127)
                .map(n -> Piece.pieceFromByte((byte)(int)n, -2))
                .orElse(null);
    }
    /**
     * Creates a GameVariableSupplier that always returns the given constant value.
     * @param value the constant value to return
     * @return a GameVariableSupplier that always returns the given value
     * @param <T> the type of the value supplied
     */
    static <T> GameVariableSupplier<T> constant(T value) { return s -> value; }

    public static GameVariableSupplier<?> parse(String str) {
        return s -> {
            try {
                return parseRec(autoFormat(autoParen(str))).apply(s);
            } catch (Exception e) {
                return null;
            }
        };
    }
    private static GameVariableSupplier<?> parseRec(String str) {
        // Trim
        str = str.trim().toLowerCase();
        // Is this a predefined supplier?
        try {
            return of(str);
        } catch (IllegalArgumentException ignored) {}
        // Is this casting?
        if (str.startsWith("int")) {
            try {
                GameVariableSupplier<?> var = parseRec(str.substring(3));
                return castIntUnknown(var);
            } catch (IllegalArgumentException | ClassCastException ex) {
                throw new IllegalArgumentException("Failed to cast int because of " + ex.getMessage());
            }
        } else if (str.startsWith("double")) {
            try {
                GameVariableSupplier<?> var = parseRec(str.substring(6));
                return castDoubleUnknown(var);
            } catch (IllegalArgumentException | ClassCastException ex) {
                throw new IllegalArgumentException("Failed to cast double because of " + ex.getMessage());
            }
        } else if (str.startsWith("patternof")) {
            try {
                GameVariableSupplier<Piece> pieceVar = (GameVariableSupplier<Piece>) parseRec(str.substring(9));
                return patternOf(pieceVar);
            } catch (IllegalArgumentException | ClassCastException ex) {
                throw new IllegalArgumentException("Failed to get patternOf because of " + ex.getMessage());
            }
        } else if (str.startsWith("pattern")) {
            try {
                GameVariableSupplier<Piece> pieceVar = (GameVariableSupplier<Piece>) parseRec(str.substring(7));
                return patternOf(pieceVar);
            } catch (IllegalArgumentException | ClassCastException ex) {
                throw new IllegalArgumentException("Failed to get patternOf because of " + ex.getMessage());
            }
        } else if (str.startsWith("pieceof")) {
            try {
                GameVariableSupplier<Integer> patternVar = (GameVariableSupplier<Integer>) parseRec(str.substring(7));
                return pieceOf(patternVar);
            } catch (IllegalArgumentException | ClassCastException ex) {
                throw new IllegalArgumentException("Failed to get pieceOf because of " + ex.getMessage());
            }
        } else if (str.startsWith("piece")) {
            try {
                GameVariableSupplier<Integer> patternVar = (GameVariableSupplier<Integer>) parseRec(str.substring(5));
                return pieceOf(patternVar);
            } catch (IllegalArgumentException | ClassCastException  ex) {
                throw new IllegalArgumentException("Failed to get pieceOf because of " + ex.getMessage());
            }
        }
        // Is this a unary operation?
        String[] parts = split(str, 2);
        if (parts.length == 2) {
            try {
                GameVariableSupplier<Integer> intVar = (GameVariableSupplier<Integer>) parseRec(parts[1]);
                return integerOperation(intVar, parts[0]);
            } catch (IllegalArgumentException | ClassCastException ignored) {}
            try {
                GameVariableSupplier<Double> doubleVar = (GameVariableSupplier<Double>) parseRec(parts[1]);
                return doubleOperation(doubleVar, parts[0]);
            } catch (IllegalArgumentException | ClassCastException ignored) {}
        }
        // Is this a binary operation?
        parts = split(str, 3);
        if (parts.length == 3) {
            try {
                GameVariableSupplier<Integer> intVar1 = (GameVariableSupplier<Integer>) parseRec(parts[0]);
                GameVariableSupplier<Integer> intVar2 = (GameVariableSupplier<Integer>) parseRec(parts[2]);
                return integerOperation(intVar1, intVar2, parts[1]);
            } catch (IllegalArgumentException | ClassCastException ignored) {}
            try {
                GameVariableSupplier<Integer> intVar1 = (GameVariableSupplier<Integer>) parseRec(parts[0]);
                GameVariableSupplier<Double> intVar2 = (GameVariableSupplier<Double>) parseRec(parts[2]);
                return doubleOperation(castDoubleUnknown(intVar1), intVar2, parts[1]);
            } catch (IllegalArgumentException | ClassCastException ignored) {}
            try {
                GameVariableSupplier<Double> intVar1 = (GameVariableSupplier<Double>) parseRec(parts[0]);
                GameVariableSupplier<Integer> intVar2 = (GameVariableSupplier<Integer>) parseRec(parts[2]);
                return doubleOperation(intVar1, castDoubleUnknown(intVar2), parts[1]);
            } catch (IllegalArgumentException | ClassCastException ignored) {}
            try {
                GameVariableSupplier<Double> doubleVar1 = (GameVariableSupplier<Double>) parseRec(parts[0]);
                GameVariableSupplier<Double> doubleVar2 = (GameVariableSupplier<Double>) parseRec(parts[2]);
                return doubleOperation(doubleVar1, doubleVar2, parts[1]);
            } catch (IllegalArgumentException | ClassCastException ignored) {}
        }
        // Try strip parentheses
        if (str.startsWith("(") && str.endsWith(")")) {
            try {
                return parseRec(str.substring(1, str.length() - 1));
            } catch (IllegalArgumentException ignored) {}
        }
        // Nothing worked
        throw new IllegalArgumentException("Could not parse GameVariableSupplier: " + str);
    }
    /**
     * Splits the input string into tokens based on whitespace, ignoring whitespace within parentheses.
     * The splitting respects nested parentheses and only splits at the top level.
     * If a limit is provided and reached, the rest of the string is included in the last token.
     * @param input the input string to split
     * @param limit the maximum number of tokens to return; if 0 or negative, no limit is applied
     * @return an array of tokens
     */
    static String[] split(String input, int limit) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') {
                if (limit > 0 && result.size() == limit - 1 || depth > 0) {
                    token.append(c);
                }
                depth++;
            } else if (c == ')') {
                depth--;
                if (limit > 0 && result.size() == limit - 1 || depth > 0) {
                    token.append(c);
                }
            } else if (Character.isWhitespace(c) && depth == 0) {
                if (!token.isEmpty()) {
                    // Check if adding this token would hit the limit
                    if (limit > 0 && result.size() == limit - 1) {
                        // Put the rest of the string into the last token
                        token.append(input.substring(i));
                        result.add(token.toString().trim());
                        return result.toArray(new String[0]);
                    }
                    result.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(c);
            }
        }
        if (!token.isEmpty()) {
            result.add(token.toString());
        }
        return result.toArray(new String[0]);
    }
    /**
     * Automatically formats the input string by adding spaces around operators and removing extra spaces.
     * This helps in parsing expressions by ensuring that operators are clearly separated from operands.
     * Supported operators include: +, -, *, /, %, ^, (, and ).
     * @param input the input string to format
     * @return the formatted string with spaces around operators and no extra spaces
     */
    static String autoFormat(String input) {
        // Add spaces around operators
        String spaced = input.replaceAll("([+\\-*/%^()])", " $1 ");
        // Add space in between numbers and strings
        spaced = spaced.replaceAll("(\\d)([a-zA-Z])", "$1 $2");
        spaced = spaced.replaceAll("([a-zA-Z])(\\d)", "$1 $2");
        // Replace multiple spaces with a single space
        return spaced.replaceAll("\\s+", " ").trim();
    }
    /**
     * Automatically adds parentheses to an expression string based on operator precedence.
     * This helps in ensuring that the expression is evaluated in the correct order.
     * Supported operators include:
     * <ul>
     *     <li>Unary casting operators: int, double, patternof, pattern, pieceof, piece</li>
     *     <li>Binary operators with precedence (from highest to lowest):
     *         <ul>
     *             <li>^, pow, power, exp, exponent</li>
     *             <li>*, multiplies, multiply, times, time, multiplication,
     *                 /, divides, divide, division,
     *                 %, mod, modulo, modulos, remainder</li>
     *             <li>+, adds, add, plus, addition,
     *                 -, subtracts, subtract, minus, subtraction</li>
     *         </ul>
     *     </li>
     * </ul>
     * Parentheses are added to ensure that operations are grouped correctly according to their precedence.
     * @param str the input expression string
     * @return the expression string with added parentheses based on operator precedence
     */
    static String autoParen(String str) {
        final Map<String, Integer> PRECEDENCE = new HashMap<>();
        for (String op : new String[]{"^", "pow", "power", "exp", "exponent"}) PRECEDENCE.put(op, 3);
        for (String op : new String[]{"*", "multiplies", "multiply", "times", "time", "multiplication",
                "/", "divides", "divide", "division",
                "%", "mod", "modulo", "modulos", "remainder"})
            PRECEDENCE.put(op, 2);
        for (String op : new String[]{"+", "adds", "add", "plus", "addition",
                "-", "subtracts", "subtract", "minus", "subtraction"})
            PRECEDENCE.put(op, 1);
        final Set<String> CAST_OPS = new HashSet<>(Arrays.asList(
                "int", "double", "patternof", "pattern", "pieceof", "piece"
        ));
        List<String> tokens = new ArrayList<>();
        Stack<String> values = new Stack<>();
        Stack<String> ops = new Stack<>();
        Pattern p = Pattern.compile(
                "\\d+\\.\\d+|\\d+|[A-Za-z_][A-Za-z_0-9]*|[()+\\-*/%^]"
        );
        Matcher m = p.matcher(str);
        while (m.find()) {
            tokens.add(m.group());
        }
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (CAST_OPS.contains(t)) {
                if (i + 1 < tokens.size()) {
                    String next = tokens.get(++i);
                    values.push("(" + t + " " + next + ")");
                }
            } else if (PRECEDENCE.containsKey(t)) {
                while (!ops.isEmpty() && PRECEDENCE.getOrDefault(ops.peek(), 0) >= PRECEDENCE.get(t)) {
                    String op = ops.pop();
                    String b = values.pop();
                    String a = values.pop();
                    values.push("(" + a + " " + op + " " + b + ")");
                }
                ops.push(t);
            } else if (t.equals("(")) {
                ops.push(t);
            } else if (t.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    String op = ops.pop();
                    String b = values.pop();
                    String a = values.pop();
                    values.push("(" + a + " " + op + " " + b + ")");
                }
                if (!ops.isEmpty() && ops.peek().equals("(")) ops.pop();
            } else {
                values.push(t);
            }
        }
        while (!ops.isEmpty()) {
            String op = ops.pop();
            String b = values.pop();
            String a = values.pop();
            values.push("(" + a + " " + op + " " + b + ")");
        }
        return values.isEmpty() ? "" : values.pop();
    }


    /** A constant supplier that always returns 0. */
    GameVariableSupplier<Integer> ZERO = constant(0);
    /** A constant supplier that always returns 1. */
    GameVariableSupplier<Integer> ONE = constant(1);
    /** A constant supplier that always returns the mathematical constant π (pi). */
    GameVariableSupplier<Double> PI = constant(Math.PI);
    /** A constant supplier that always returns the number 6, representing the number of sides on a hexagon. */
    GameVariableSupplier<Integer> HEX = constant(6);
    /** A supplier that returns the length of the engine, which is the total number of blocks in the engine. */
    GameVariableSupplier<Integer> LENGTH = s -> (s == null || s.getEngine() == null) ? 0 : s.getEngine().length();
    /** A supplier that returns the radius of the engine. */
    GameVariableSupplier<Integer> RADIUS = s -> (s == null || s.getEngine() == null) ? 0 : s.getEngine().getRadius();
    /** A supplier that returns the number of lines in the engine, which is radius * 2 - 1. */
    GameVariableSupplier<Integer> LINES = s -> (s == null || s.getEngine() == null) ? 0 : s.getEngine().getRadius() * 2 - 1;
    /** A supplier that returns the size of the piece queue. */
    GameVariableSupplier<Integer> SIZE = s -> (s == null || s.getQueue() == null) ? 0 : s.getQueue().length;
    /** A supplier that returns the first piece in the piece queue, or null if the queue is empty. */
    GameVariableSupplier<Piece> FIRST = s -> (s == null || s.getQueue() == null || s.getQueue().length == 0) ? null : s.getQueue()[0];
    /** A supplier that returns the last piece in the piece queue, or null if the queue is empty. */
    GameVariableSupplier<Piece> LAST = s -> (s == null || s.getQueue() == null || s.getQueue().length == 0) ? null : s.getQueue()[s.getQueue().length - 1];
    /** A supplier that returns the current score of the game. */
    GameVariableSupplier<Integer> SCORE = s -> (s == null) ? 0 : s.getScore();
    /** A supplier that returns the current turn number of the game. */
    GameVariableSupplier<Integer> TURN = s -> (s == null) ? 0 : s.getTurn();
    /** A supplier that returns the percentage of the engine that is filled, from 0.0 to 1.0. */
    GameVariableSupplier<Double> FILL = s -> (s == null || s.getEngine() == null) ? 0.0 : s.getEngine().getPercentFilled();
    /** A supplier that returns the entropy of the engine, which is a measure of how disordered the blocks are. */
    GameVariableSupplier<Double> ENTROPY = s -> (s == null || s.getEngine() == null) ? 0.0 : s.getEngine().computeEntropy();
}
