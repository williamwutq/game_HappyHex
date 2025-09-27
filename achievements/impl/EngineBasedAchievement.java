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

package achievements.impl;

import achievements.GameAchievementTemplate;
import achievements.icon.AchievementIcon;
import hex.*;
import util.function.TriFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class EngineBasedAchievement implements GameAchievementTemplate {
    private final String name;
    private final String description;
    private final AchievementIcon icon;

    public EngineBasedAchievement(String name, String description, AchievementIcon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    /**
     * {@inheritDoc}
     * @return the name of the achievement
     */
    public String name() {
        return name;
    }
    /**
     * {@inheritDoc}
     * @return the description of the achievement
     */
    public String description() {
        return description;
    }
    /**
     * {@inheritDoc}
     * @return the icon of the achievement
     */
    @Override
    public AchievementIcon icon() {
        return icon;
    }

    @Override
    public boolean test(GameState state) {
        HexEngine engine = state.getEngine();
        if (engine == null) {
            return false;
        }
        return false;
    }


    // Predicates
    /**
     * A functional interface for block predicates.
     * This wraps a {@link Predicate<Block>} to provide more readable code.
     */
    private interface BlockPredicate extends Predicate<Block>{}
    /**
     * Creates a BlockPredicate based on the given operation and arguments.
     * The supported operations are:
     * <ul>
     *     <li>"false": always returns false</li>
     *     <li>"true": always returns true</li>
     *     <li>"state": returns the state of the block (non-empty)</li
     *     <li>"is": checks if the block is equal to the given block (args[0])</li>
     *     <li>"color": checks if the block's color index matches the given color index (args[0])</li>
     *     <li>"at": checks if the block is at the given line and index (args[0], args[1])</li>
     *     <li>"or": logical OR of two block predicates (args[0], args[1])</li>
     *     <li>"and": logical AND of two block predicates (args[0], args[1])</li>
     *     <li>"not": logical NOT of a block predicate (args[0])</li>
     * </ul>
     * @param op the operation to perform
     * @param args the arguments for the operation. The expected types and number of arguments depend on the operation.
     * @return a BlockPredicate based on the operation and arguments, or null if the operation is not recognized or arguments are invalid
     */
    private static BlockPredicate blockPredicates(String op, Object[] args){
        switch (op) {
            case "false" -> {
                return b -> false;
            }
            case "true" -> {
                return b -> true;
            }
            case "state" -> {
                return Block::getState;
            }
            case "is" -> {
                if (args.length == 1) {
                    return b -> b.equals(args[0]);
                }
            }
            case "color" -> {
                if (args.length == 1 && args[0] instanceof Integer) {
                    return b -> b.getColor() == (int) args[0];
                }
            }
            case "at" -> {
                if (args.length == 2 && args[0] instanceof Integer && args[1] instanceof Integer) {
                    return b -> b.getLineI() == (int) args[0] && b.getLineK() == (int) args[1];
                }
            }
            case "or" -> {
                if (args.length == 2 && args[0] instanceof BlockPredicate p1 && args[1] instanceof BlockPredicate p2) {
                    return b -> p1.test(b) || p2.test(b);
                }
            }
            case "and" -> {
                if (args.length == 2 && args[0] instanceof BlockPredicate p1 && args[1] instanceof BlockPredicate p2) {
                    return b -> p1.test(b) && p2.test(b);
                }
            }
            case "not" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p) {
                    return b -> !p.test(b);
                }
            }
        }
        return null;
    }
    /**
     * A functional interface for line predicates.
     * This wraps a {@link Predicate<>} of {@link Block} arrays to provide more readable code.
     */
    private interface LinePredicate extends Predicate<Block[]>{}
    /**
     * Creates a LinePredicate based on the given operation and arguments.
     * The supported operations are:
     * <ul>
     *     <li>"false": always returns false</li>
     *     <li>"true": always returns true</li>
     *     <li>"any": checks if any block in the line satisfies the given block predicate (args[0])</li>
     *     <li>"none": checks if no blocks in the line satisfy the given block predicate (args[0])</li>
     *     <li>"all": checks if all blocks in the line satisfy the given block predicate (args[0])</li>
     *     <li>"ratio": checks if the ratio of blocks satisfying the given block predicate (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li>"sequence": checks if there is a sequence of at least a given length (args[1]) of blocks satisfying the given block predicate (args[0])</li>
     *     <li>"checker": checks if blocks in even positions satisfy one block predicate (args[0]) and blocks in odd positions satisfy another block predicate (args[1])</li>
     *     <li>"or": logical OR of two line predicates (args[0], args[1])</li>
     *     <li>"and": logical AND of two line predicates (args[0], args[1])</li>
     *     <li>"not": logical NOT of a line predicate (args[0])</li>
     * </ul>
     * @param op the operation to perform
     * @param args the arguments for the operation. The expected types and number of arguments depend on the operation.
     * @return a LinePredicate based on the operation and arguments, or null if the operation is not recognized or arguments are invalid
     */
    private static LinePredicate linePredicates(String op, Object[] args){
        switch (op) {
            case "false" -> {
                return l -> false;
            }
            case "true" -> {
                return l -> true;
            }
            case "any" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p){
                    return l -> {
                        for (Block b : l){
                            if (p.test(b)) return true;
                        }
                        return false;
                    };
                }
            }
            case "none" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p){
                    return l -> {
                        for (Block b : l){
                            if (p.test(b)) return false;
                        }
                        return true;
                    };
                }
            }
            case "all" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p){
                    return l -> {
                        for (Block b : l){
                            if (!p.test(b)) return false;
                        }
                        return true;
                    };
                }
            }
            case "ratio" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate p && args[1] instanceof Double lower && args[2] instanceof Double upper){
                    return l -> {
                        int c = 0; double r;
                        for (Block b : l){
                            if (p.test(b)) c++;
                        }
                        if (l.length > 0){
                            r = (double) c / l.length;
                        }
                        return lower <= c && c <= upper;
                    };
                }
            }
            case "sequence" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate p && args[1] instanceof Integer len){
                    return l -> {
                        if (l.length >= len) {
                            int c = 0;
                            for (Block b : l){
                                if (p.test(b)) {
                                    c++;
                                    if (c > len) return true;
                                } else c = 0;
                            }
                        }
                        return false;
                    };
                }
            }
            case "checker" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate e && args[1] instanceof BlockPredicate o){
                    return l -> {
                        for (int i = 0; i < l.length; i++) {
                            Block b = l[i];
                            if (i % 2 == 0){
                                if (!e.test(b)) return false;
                            } else if (!o.test(b)) return false;
                        }
                        return true;
                    };
                }
            }
            case "not" -> {
                if (args.length == 1 && args[0] instanceof LinePredicate p) {
                    return l -> !p.test(l);
                }
            }
            case "or" -> {
                if (args.length == 2 && args[0] instanceof LinePredicate p1 && args[1] instanceof LinePredicate p2) {
                    return l -> p1.test(l) || p2.test(l);
                }
            }
            case "and" -> {
                if (args.length == 2 && args[0] instanceof LinePredicate p1 && args[1] instanceof LinePredicate p2) {
                    return l -> p1.test(l) && p2.test(l);
                }
            }
        }
        return null;
    }
    /**
     * A functional interface for supplying iterables of block arrays from a HexEngine.
     * This wraps a {@link Function<>} that takes in an {@link HexEngine engine} and returns an {@link Iterable}
     * of {@link Block} array to provide more readable code.
     */
    private interface BlocksIterableSupplier extends Function<HexEngine, Iterable<Block[]>> {}
    /**
     * Creates a BlocksIterableSupplier based on the given iteration strategy.
     * The supported strategies are:
     * <ul>
     *     <li>"array": returns all blocks in a single array</li>
     *     <li>"random": returns blocks in random order, one at a time</li>
     *     <li>"back": returns blocks in reverse order, one at a time</li>
     *     <li>"i-line": returns blocks line by line along the I axis</li>
     *     <li>"j-line": returns blocks line by line along the J axis</li>
     *     <li>"k-line": returns blocks line by line along the K axis</li>
     *     <li>"lines": returns blocks line by line along all three axes (I, J, K)</li>
     *     <li>"i-random": returns lines along the I axis in random order</li>
     *     <li>"j-random": returns lines along the J axis in random order</li>
     *     <li>"k-random": returns lines along the K axis in random order</li>
     *     <li>"lines-random": returns lines along all three axes (I, J, K) in random order</li>
     * </ul>
     * @param it the iteration strategy
     * @return a BlocksIterableSupplier based on the iteration strategy, or null if the strategy is not recognized
     */
    private static BlocksIterableSupplier blocksIterable(String it){
        final Random random = new Random();
        return switch (it){
            case "array" -> e -> () -> new Iterator<>() {
                private boolean hasNext = true;
                @Override
                public boolean hasNext() {
                    return hasNext;
                }
                @Override
                public Block[] next() {
                    hasNext = false;
                    return e.blocks();
                }
            };
            case "random" -> e -> {
                Block[] blocks = e.blocks();
                int l = blocks.length;
                // Do a Fisher-Yates shuffle
                for (int i = l - 1; i > 0; i--) {
                    int j = random.nextInt(i + 1);
                    Block temp = blocks[i];
                    blocks[i] = blocks[j];
                    blocks[j] = temp;
                }
                return () -> new Iterator<>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < l;
                    }

                    @Override
                    public Block[] next() {
                        if (index >= l) throw new NoSuchElementException();
                        return new Block[]{blocks[index++]};
                    }
                };
            };
            case "back" -> e -> () -> new Iterator<>() {
                private final Block[] blocks = e.blocks();
                private int index = blocks.length - 1;
                @Override
                public boolean hasNext() {
                    return index >= 0;
                }
                @Override
                public Block[] next() {
                    return new Block[]{blocks[index--]};
                }
            };
            case "i-line" -> e -> e::iteratorI;
            case "j-line" -> e -> e::iteratorJ;
            case "k-line" -> e -> e::iteratorK;
            case "lines" -> e -> () -> new Iterator<Block[]>() {
                private final Iterator<Block[]> it = e.iteratorI();
                private final Iterator<Block[]> jt = e.iteratorJ();
                private final Iterator<Block[]> kt = e.iteratorK();
                @Override
                public boolean hasNext() {
                    // If I is depleted, check J, if J is depleted, check K
                    return it.hasNext() || jt.hasNext() || kt.hasNext();
                }
                @Override
                public Block[] next() {
                    // Return from I if possible, else J, else K
                    if (it.hasNext()) return it.next();
                    if (jt.hasNext()) return jt.next();
                    if (kt.hasNext()) return kt.next();
                    throw new NoSuchElementException();
                }
            };
            case "i-random" -> e -> {
                // Collect all lines into an array
                Block[][] lines = new Block[e.getRadius() * 2 - 1][];
                int li = 0;
                for (Block[] line : e.lineIterableI()) {
                    lines[li] = line;
                    li++;
                }
                return () -> shuffledBlocksIterator(random, lines);
            };
            case "j-random" -> e -> {
                // Collect all lines into an array
                Block[][] lines = new Block[e.getRadius() * 2 - 1][];
                int li = 0;
                for (Block[] line : e.lineIterableJ()) {
                    lines[li] = line;
                    li++;
                }
                return () -> shuffledBlocksIterator(random, lines);
            };
            case "k-random" -> e -> {
                // Collect all lines into an array
                Block[][] lines = new Block[e.getRadius() * 2 - 1][];
                int li = 0;
                for (Block[] line : e.lineIterableK()) {
                    lines[li] = line;
                    li++;
                }
                return () -> shuffledBlocksIterator(random, lines);
            };
            case "lines-random" -> e -> {
                // Collect all lines into an array
                Block[][] lines = new Block[e.getRadius() * 6 - 3][];
                int li = 0;
                for (Block[] line : e.lineIterableI()) {
                    lines[li] = line;
                    li++;
                }
                for (Block[] line : e.lineIterableJ()) {
                    lines[li] = line;
                    li++;
                }
                for (Block[] line : e.lineIterableK()) {
                    lines[li] = line;
                    li++;
                }
                return () -> shuffledBlocksIterator(random, lines);
            };
            default -> null;
        };
    }
    /**
     * Returns an iterator that yields the given lines in a random order.
     * This method performs a Fisher-Yates shuffle on the array of lines and returns an iterator over the shuffled array.
     * @param random a Random instance to use for shuffling
     * @param lines the array of Block arrays (lines) to shuffle
     * @return an Iterator that yields the lines in random order
     */
    private static Iterator<Block[]> shuffledBlocksIterator(Random random, Block[][] lines){
        // Do a Fisher-Yates shuffle
        int l = lines.length;
        for (int i = l - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Block[] temp = lines[i];
            lines[i] = lines[j];
            lines[j] = temp;
        }
        return new Iterator<>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < l;
            }
            @Override
            public Block[] next() {
                if (index >= l) throw new NoSuchElementException();
                return lines[index++];
            }
        };
    }
    /**
     * Constructs a BiFunction that computes an index value for a HexEngine based on the given function and option.
     * The supported options are:
     * <ul>
     *     <li>"max": returns the maximum value of the function applied to all blocks in the engine</li>
     *     <li>"min": returns the minimum value of the function applied to all blocks in the engine</li>
     *     <li>"avg": returns the average value of the function applied to all blocks in the engine</li>
     *     <li>"sum": returns the sum of the values of the function applied to all blocks in the engine</li>
     *     <li>a numeric string: returns a constant value parsed from the string</li>
     * </ul>
     * If the option is null or unrecognized, this method returns null.
     * @param function a TriFunction that takes in a HexEngine, a Hex, and a HexGrid, and returns a Double value
     * @param option a String specifying how to aggregate the values computed by the function
     * @return a BiFunction that takes in a HexEngine and a HexGrid, and returns a Double value based on the specified option, or null if the option is null or unrecognized
     */
    private static BiFunction<HexEngine, HexGrid, Double> constructIndexSupplier(TriFunction<HexEngine, Hex, HexGrid, Double> function, String option){
        if (option == null) {
            return null;
        } else if (option.equals("max")) {
            return (e, g) -> {
                double max = Double.NEGATIVE_INFINITY;
                for (Hex hex : e.blocks()){
                    double val = function.apply(e, hex, g);
                    if (val > max) max = val;
                }
                return max;
            };
        } else if (option.equals("min")) {
            return (e, g) -> {
                double min = Double.POSITIVE_INFINITY;
                for (Hex hex : e.blocks()){
                    double val = function.apply(e, hex, g);
                    if (val < min) min = val;
                }
                return min;
            };
        } else if (option.equals("avg")) {
            return (e, g) -> {
                double sum = 0.0;
                int count = 0;
                for (Hex hex : e.blocks()){
                    sum += function.apply(e, hex, g);
                    count++;
                }
                return count == 0 ? 0.0 : sum / count;
            };
        } else if (option.equals("sum")) {
            return (e, g) -> {
                double sum = 0.0;
                for (Hex hex : e.blocks()){
                    sum += function.apply(e, hex, g);
                }
                return sum;
            };
        } else {
            // Try to parse as a constant
            try {
                double constant = Double.parseDouble(option);
                return (e, g) -> constant;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
    /**
     * A functional interface for engine predicates.
     * This wraps a {@link Predicate<>} of {@link HexEngine engine} to provide more readable code.
     */
    private interface EnginePredicate extends Predicate<HexEngine>{}
    /**
     * Simulates adding a piece to the engine at the given position and counts the number of blocks that would be eliminated.
     * <p>
     * This method clones the engine, attempts to add the piece at the specified position, and if successful,
     * counts the number of blocks that would be eliminated as a result.
     * If the position is invalid (e.g., out of bounds or occupied), this method returns 0.
     * <p>
     * To be honest, this method should be optimized as a part of HexEngine, but it is not provided there, so we added this suboptimal version here.
     * We return a double to satisfy the TriFunction signature, but the value is always an integer.
     * @see HexEngine#add(Hex, HexGrid)
     * @see HexEngine#countEliminate(boolean)
     * @param engine the HexEngine to simulate adding the piece to
     * @param position the Hex position to add the piece at
     * @param grid the HexGrid representing the game board
     * @return the number of blocks that would be eliminated if the piece were added at the specified position, or 0 if the position is invalid
     */
    private static double eliminateIndex(HexEngine engine, Hex position, HexGrid grid){
        HexEngine cloned = engine.clone(); // Clone engine
        try {
            cloned.add(position, grid); // Add piece at position
        } catch (IllegalArgumentException e) {
            return 0; // Invalid position
        }
        return cloned.countEliminate(false); // Count eliminated blocks
    }
    /**
     * Creates an EnginePredicate based on the given operation and arguments.
     * The supported operations are:
     * <ul>
     *     <li><b>all</b>: checks if all lines from a given BlocksIterableSupplier (args[0]) satisfy a given LinePredicate (args[1])</li>
     *     <li><b>none</b>: checks if no lines from a given BlocksIterableSupplier (args[0]) satisfy a given LinePredicate (args[1])</li>
     *     <li><b>any</b>: checks if any line from a given BlocksIterableSupplier (args[0]) satisfies a given LinePredicate (args[1])</li>
     *     <li><b>ratio</b>: checks if the ratio of lines from a given BlocksIterableSupplier (args[0]) that satisfy
     *         a given LinePredicate (args[1]) is within the given bounds (args[2], args[3])</li>
     *     <li><b>sequence</b>: checks if there is a sequence of at least a given length (args[2]) of lines from a given BlocksIterableSupplier (args[0])
     *         that satisfy a given LinePredicate (args[1])</li>
     *     <li><b>checker</b>: checks if blocks in even positions of lines from a given BlocksIterableSupplier (args[0]) satisfy one BlockPredicate (args[1])
     *         and blocks in odd positions satisfy another BlockPredicate (args[2])</li>
     *     <li><b>filled</b>: checks if the engine's filled percentage is within the given bounds (args[0], args[1])</li>
     *     <li><b>entropy</b>: checks if the engine's entropy is within the given bounds (args[0], args[1])</li>
     *     <li><b>length</b>: checks if the engine's length is within the given bounds (args[0], args[1])</li>
     *     <li><b>radius</b>: checks if the engine's radius is within the given bounds (args[0], args[1])</li>
     *     <li><b>density-index</b>: checks if the engine's mean density index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>densest-index</b>: checks if the engine's highest density index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>sparsest-index</b>: checks if the engine's lowest density index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>entropy-index</b>: checks if the engine's mean entropy index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>most-entropic-index</b>: checks if the engine's highest entropy index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>least-entropic-index</b>: checks if the engine's lowest entropy index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>eliminate-index</b>: checks if the engine's elimination index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>reduction-index</b>: checks if the engine's mean elimination index for a given piece (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li><b>is</b>: checks if the engine is equal to another engine (args[0])</li>
     *     <li><b>matches</b>: checks if the engine matches another engine ignoring color (args[0])</li>
     *     <li><b>appears</b>: checks if a block with the given pattern (args[0]) appears in the engine</li>
     *     <li><b>lacks</b>: checks if a block with the given pattern (args[0]) does not appear in the engine</li>
     *     <li><b>not</b>: logical NOT of an engine predicate (args[0])</li>
     *     <li><b>or</b>: logical OR of two engine predicates (args[0], args[1])</li>
     *     <li><b>and</b>: logical AND of two engine predicates (args[0], args[1])</li>
     *     <li><b>xor</b>: logical XOR of two engine predicates (args[0], args[1])</li>
     * </ul>
     * @param op the operation to perform
     * @param args the arguments for the operation. The expected types and number of arguments depend on the operation.
     * @return an EnginePredicate based on the operation and arguments, or null if the operation is not recognized or arguments are invalid
     */
    private static EnginePredicate enginePredicate(String op, Object[] args){
        switch (op) {
            // For
            case "all" -> {
                if (args.length == 2 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate) {
                    return e -> {
                        for (Block[] line : bis.apply(e)) {
                            if (!predicate.test(line)) return false;
                        }
                        return true;
                    };
                }
            }
            case "none" -> {
                if (args.length == 2 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate) {
                    return e -> {
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(line)) return false;
                        }
                        return true;
                    };
                }
            }
            case "any" -> {
                if (args.length == 2 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate) {
                    return e -> {
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(line)) return true;
                        }
                        return false;
                    };
                }
            }
            case "ratio" -> {
                if (args.length == 4 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate && args[2] instanceof Double upper && args[3] instanceof Double lower) {
                    return e -> {
                        int c = 0; int t = 0;
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(line)){
                                c++;
                            }
                            t++;
                        }
                        double r = (t == 0) ? 0.0 : (double) c / t;
                        return lower <= r && r <= upper;
                    };
                }
            }
            case "sequence" -> {
                if (args.length == 3 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate && args[2] instanceof Integer len) {
                    return e -> {
                        int c = 0;
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(line)){
                                c++;
                                if (c >= len) return true;
                            } else {
                                c = 0;
                            }
                        }
                        return false;
                    };
                }
            }
            case "checker" -> {
                if (args.length == 3 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof BlockPredicate even && args[2] instanceof BlockPredicate odd) {
                    return e -> {
                        for (Block[] line : bis.apply(e)) {
                            for (int i = 0; i < line.length; i++) {
                                Block b = line[i];
                                if (i % 2 == 0) {
                                    if (!even.test(b)) return false;
                                } else {
                                    if (!odd.test(b)) return false;
                                }
                            }
                        }
                        return true;
                    };
                }
            }
            // Native
            case "filled" -> {
                if (args.length == 2 && args[0] instanceof Double lower && args[1] instanceof Double upper) {
                    return e -> {
                        double filled = e.getPercentFilled();
                        return lower <= filled && filled <= upper;
                    };
                }
            }
            case "entropy" -> {
                if (args.length == 2 && args[0] instanceof Double lower && args[1] instanceof Double upper) {
                    return e -> {
                        double ent = e.computeEntropy(); // We absolutely need to cache this costly operation
                        return lower <= ent && ent <= upper;
                    };
                }
            }
            case "length" -> {
                if (args.length == 2 && args[0] instanceof Integer lower && args[1] instanceof Integer upper) {
                    return e -> lower <= e.length() && e.length() <= upper;
                }
            }
            case "radius" -> {
                if (args.length == 2 && args[0] instanceof Integer lower && args[1] instanceof Integer upper) {
                    return e -> lower <= e.getRadius() && e.getRadius() <= upper;
                }
            }
            case "density-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> densityIndexSupplier = constructIndexSupplier(HexEngine::computeDenseIndex, "avg");
                    if (densityIndexSupplier == null) return null;
                    return (e) -> {
                        double index = densityIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "densest-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> densestIndexSupplier = constructIndexSupplier(HexEngine::computeDenseIndex, "max");
                    if (densestIndexSupplier == null) return null;
                    return (e) -> {
                        double index = densestIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "sparsest-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> sparsestIndexSupplier = constructIndexSupplier(HexEngine::computeDenseIndex, "min");
                    if (sparsestIndexSupplier == null) return null;
                    return (e) -> {
                        double index = sparsestIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "entropy-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> entropyIndexSupplier = constructIndexSupplier(HexEngine::computeEntropyIndex, "avg");
                    if (entropyIndexSupplier == null) return null;
                    return (e) -> {
                        double index = entropyIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "most-entropic-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> mostEntropicIndexSupplier = constructIndexSupplier(HexEngine::computeEntropyIndex, "max");
                    if (mostEntropicIndexSupplier == null) return null;
                    return (e) -> {
                        double index = mostEntropicIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "least-entropic-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> leastEntropicIndexSupplier = constructIndexSupplier(HexEngine::computeEntropyIndex, "min");
                    if (leastEntropicIndexSupplier == null) return null;
                    return (e) -> {
                        double index = leastEntropicIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "eliminate-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> eliminateIndexSupplier = constructIndexSupplier(EngineBasedAchievement::eliminateIndex, "max");
                    if (eliminateIndexSupplier == null) return null;
                    return (e) -> {
                        double index = eliminateIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "reduction-index" -> {
                if (args.length == 3 && args[0] instanceof Piece piece && args[1] instanceof Double lower && args[2] instanceof Double upper) {
                    final BiFunction<HexEngine, HexGrid, Double> eliminateIndexSupplier = constructIndexSupplier(EngineBasedAchievement::eliminateIndex, "avg");
                    if (eliminateIndexSupplier == null) return null;
                    return (e) -> {
                        double index = eliminateIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "is" -> {
                if (args.length == 1 && args[0] instanceof HexEngine other) {
                    return e -> e.equals(other);
                }
            }
            case "matches" -> {
                if (args.length == 1 && args[0] instanceof HexEngine other) {
                    return e -> e.equalsIgnoreColor(other);
                }
            }
            case "appears" -> {
                if (args.length == 1 && args[0] instanceof Integer pattern) {
                    return e -> {
                        for (Block b : e.blocks()) {
                            if ((int)pattern == e.patternSupplier(b).get()) {
                                return true;
                            }
                        }
                        return false;
                    };
                }
            }
            case "lacks" -> {
                if (args.length == 1 && args[0] instanceof Integer pattern) {
                    return e -> {
                        for (Block b : e.blocks()) {
                            if ((int)pattern == e.patternSupplier(b).get()) {
                                return false;
                            }
                        }
                        return true;
                    };
                }
            }
            // Logical operations on engine predicates
            case "not" -> {
                if (args.length == 1 && args[0] instanceof EnginePredicate p) {
                    return e -> !p.test(e);
                }
            }
            case "or" -> {
                if (args.length == 2 && args[0] instanceof EnginePredicate p1 && args[1] instanceof EnginePredicate p2) {
                    return e -> p1.test(e) || p2.test(e);
                }
            }
            case "and" -> {
                if (args.length == 2 && args[0] instanceof EnginePredicate p1 && args[1] instanceof EnginePredicate p2) {
                    return e -> p1.test(e) && p2.test(e);
                }
            }
            case "xor" -> {
                if (args.length == 2 && args[0] instanceof EnginePredicate p1 && args[1] instanceof EnginePredicate p2) {
                    return e -> p1.test(e) ^ p2.test(e);
                }
            }
        }
        return null;
    }
}
