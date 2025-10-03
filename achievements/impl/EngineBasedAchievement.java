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

import achievements.AchievementJsonSerializer;
import achievements.DataSerializationException;
import achievements.GameAchievementTemplate;
import achievements.GameVariableSupplier;
import achievements.icon.AchievementIcon;
import achievements.icon.AchievementIconSerialHelper;
import hex.*;
import io.JsonConvertible;
import util.function.TriFunction;
import util.function.TriPredicate;
import util.tuple.Pair;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * An achievement that is based on predicates evaluated against the game engine state.
 * This achievement can have variables that are updated from the game state and used in the predicates.
 * The main predicate is an {@link EnginePredicate} that determines if the achievement is completed.
 * <p>
 * The achievement can be serialized to and from JSON format using the {@link #toJsonObjectBuilder()} and {@link #fromJsonObject(JsonObject)} methods.
 * The class must be registered with the {@link AchievementJsonSerializer} using the {@link #load()} method before deserialization can occur.
 * <p>
 * The predicates support a variety of operations and can be combined using logical operators.
 * See the documentation of the predicate methods for details on the supported operations and their syntax.
 * <p>
 * The implementation of this class is entirely functional, which enables easy linking of predicates and variables.
 * This also allows for easy extension of the predicate system by adding new functional interfaces and methods.
 * As of this version, type safety is not guaranteed due to the dynamic nature of the predicates and variables, but
 * in tests, type errors are rare and usually indicate a mistake in the achievement definition instead of a runtime error.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @see JsonConvertible
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class EngineBasedAchievement implements GameAchievementTemplate, JsonConvertible {
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    private final Map<String, VariableAchievement.AchievementVariable<?>> variables;
    private String mainPredicateSymbol; // For serialization
    private EnginePredicate mainPredicate;

    public static void load() {
        AchievementJsonSerializer.registerAchievementClass("EngineBasedAchievement", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
    /**
     * Constructs an EngineBasedAchievement with the given name, description, icon. Predicate is set to null and should be set later.
     * Initializes an empty map for variables.
     * @param name the name of the achievement
     * @param description the description of the achievement
     * @param icon the icon of the achievement
     */
    public EngineBasedAchievement(String name, String description, AchievementIcon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.variables = new HashMap<>();
        this.mainPredicateSymbol = null;
        this.mainPredicate = null;
    }
    /**
     * Sets the main predicate of the achievement based on the given symbol.
     * This method compiles the symbol into an EnginePredicate using the {@link #compile(String)} method.
     * <p>
     * If the symbol cannot be compiled into a valid EnginePredicate, an IllegalArgumentException is thrown.
     * It is recommended to preserve the message of the exception for debugging purposes.
     *
     * @param symbol the symbol representing the main predicate
     * @throws IllegalArgumentException if the symbol cannot be compiled into a valid EnginePredicate
     */
    public void setPredicate(String symbol) throws IllegalArgumentException {
        this.mainPredicateSymbol = symbol;
        this.mainPredicate = compile(symbol);
    }

    /**
     * {@inheritDoc}
     * @return the name of the achievement
     */
    @Override
    public String name() {
        return name;
    }
    /**
     * {@inheritDoc}
     * @return the description of the achievement
     */
    @Override
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

    /**
     * Checks if the achievement is completed based on the given GameState.
     * Updates all variables before evaluating the main predicate.
     * @param state the input argument to the predicate
     * @return true if the achievement is completed, false otherwise
     */
    @Override
    public boolean test(GameState state) {
        // Update variables
        for (Map.Entry<String, VariableAchievement.AchievementVariable<?>> entry : variables.entrySet()) {
            entry.getValue().update(state);
        }
        HexEngine engine = state.getEngine();
        return engine != null && mainPredicate != null && mainPredicate.test(state, engine);
    }
    /**
     * Adds a variable to the achievement.
     * @param name the name of the variable
     * @param var the variable to add
     */
    public void addVariable(String name, VariableAchievement.AchievementVariable<?> var) {
        variables.put(name, var);
    }
    /**
     * Converts the achievement to a JSON object builder.
     * @return a {@link JsonObjectBuilder} representing the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "EngineBasedAchievement");
        builder.add("name", name());
        builder.add("description", description());
        builder.add("icon", AchievementIconSerialHelper.serialize(icon));
        // Variables
        JsonArrayBuilder varBuilder = Json.createArrayBuilder();
        for (Map.Entry<String, VariableAchievement.AchievementVariable<?>> entry : variables.entrySet()) {
            String varName = entry.getKey();
            VariableAchievement.AchievementVariable<?> var = entry.getValue();
            String varSymbol = var.getSymbol();
            varBuilder.add(Json.createObjectBuilder()
                    .add("name", varName)
                    .add("symbol", varSymbol)
                    .add("type", var.type().getSimpleName())
            );
        }
        builder.add("variables", varBuilder);
        // Main predicate
        builder.add("mainPredicate", mainPredicateSymbol);
        return builder;
    }

    /**
     * Converts a JSON object to an EngineBasedAchievement.
     * @param obj the JSON object to convert
     * @return an EngineBasedAchievement represented by the JSON object
     * @throws DataSerializationException if the JSON object is invalid or cannot be converted
     */
    public static EngineBasedAchievement fromJsonObject(JsonObject obj) throws DataSerializationException {
        // Name, description, icon
        if (!obj.containsKey("name") || !obj.containsKey("description") || !obj.containsKey("icon") || !obj.containsKey("variables") || !obj.containsKey("mainPredicate")) {
            throw new IllegalArgumentException("JSON object does not contain required fields for EngineBasedAchievement");
        }
        String name = obj.getString("name");
        String description = obj.getString("description");
        AchievementIcon icon = AchievementIconSerialHelper.deserialize(obj);
        String mainPredicateSymbol = obj.getString("mainPredicate");
        EngineBasedAchievement achievement = new EngineBasedAchievement(name, description, icon);
        // Variables
        for (var varObj : obj.getJsonArray("variables")) {
            if (!(varObj instanceof JsonObject varJson) || !varJson.containsKey("name") || !varJson.containsKey("symbol") || !varJson.containsKey("type")) {
                throw new DataSerializationException("Invalid variable object in JSON array");
            }
            String varName = varJson.getString("name");
            String varSymbol = varJson.getString("symbol");
            String varType = varJson.getString("type");
            VariableAchievement.AchievementVariable<?> var;
            switch (varType) {
                case "Integer" -> {
                    try {
                        GameVariableSupplier<Integer> iv = (GameVariableSupplier<Integer>) GameVariableSupplier.parse(varSymbol);
                        var = new VariableAchievement.AchievementVariable<>(iv, Integer.class, varSymbol);
                    } catch (IllegalArgumentException e) {
                        throw new DataSerializationException("Invalid Integer variable symbol " + varSymbol + " in variables", e);
                    } catch (ClassCastException e) {
                        throw new DataSerializationException("Variable symbol " + varSymbol + " does not resolve to Integer type in variables", e); // This should never happen
                    }
                }
                case "Double" -> {
                    try {
                        GameVariableSupplier<Double> dv = (GameVariableSupplier<Double>) GameVariableSupplier.parse(varSymbol);
                        var = new VariableAchievement.AchievementVariable<>(dv, Double.class, varSymbol);
                    } catch (IllegalArgumentException e) {
                        throw new DataSerializationException("Invalid Double variable symbol " + varSymbol + " in variables", e);
                    } catch (ClassCastException e) {
                        throw new DataSerializationException("Variable symbol " + varSymbol + " does not resolve to Double type in variables", e); // This should never happen
                    }
                }
                case "Piece" -> {
                    try {
                        GameVariableSupplier<Piece> pv = (GameVariableSupplier<Piece>) GameVariableSupplier.parse(varSymbol);
                        var = new VariableAchievement.AchievementVariable<>(pv, Piece.class, varSymbol);
                    } catch (IllegalArgumentException e) {
                        throw new DataSerializationException("Invalid Piece variable symbol " + varSymbol + " in variables", e);
                    } catch (ClassCastException e) {
                        throw new DataSerializationException("Variable symbol " + varSymbol + " does not resolve to Piece type in variables", e); // This should never happen
                    }
                }
                default -> throw new DataSerializationException("Unsupported variable type " + varType);
            }
            achievement.addVariable(varName, var);
        }
        // Main predicate
        try {
            achievement.setPredicate(mainPredicateSymbol);
        } catch (IllegalArgumentException e) {
            throw new DataSerializationException("Invalid main predicate symbol " + mainPredicateSymbol, e);
        }
        return achievement;
    }

    // Vars
    /**
     * Gets an IntegerProvider from a String representation of a constant integer.
     * @param str the String representation of the integer
     * @return an IntegerProvider that returns an integer constant, or null if the string is not a valid integer
     */
    IntegerProvider getIntegerConstant(String str) {
        try {
            int value = Integer.parseInt(str);
            return IntegerProvider.constant(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Gets an IntegerProvider for the given variable name.
     * @param varName the name of the variable
     * @return an IntegerProvider that retrieves an Integer from the variable, or null if the variable does not exist or is not a Number
     */
    IntegerProvider getIntegerProvider(String varName) {
        // Search in variables
        VariableAchievement.AchievementVariable<?> var = variables.get(varName);
        if (var != null) {
            try {
                // Try to cast to Number. This might not actually reflect the underlying type, but it's the best we can do.
                // If there are internal errors, we simply return null in the provider
                VariableAchievement.AchievementVariable<Number> casted = (VariableAchievement.AchievementVariable<Number>) var;
                return s -> {
                    Object obj = casted.get(s);
                    if (obj instanceof Number n) {
                        return n.intValue();
                    } else return null;
                };
            } catch (ClassCastException e) {
                return null;
            }
        } else {
            // If not found, try to parse as constant integer
            try {
                int value = Integer.parseInt(varName);
                return IntegerProvider.constant(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
    /**
     * Gets a DoubleProvider from a String representation of a constant double.
     * @param str the String representation of the double
     * @return a DoubleProvider that returns a double constant, or null if the string is not a valid double
     */
    DoubleProvider getDoubleConstant(String str) {
        try {
            double value = Double.parseDouble(str);
            return DoubleProvider.constant(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Gets a DoubleProvider for the given variable name.
     * @param varName the name of the variable
     * @return a DoubleProvider that retrieves a Double from the variable, or null if the variable does not exist or is not a Number
     */
    DoubleProvider getDoubleProvider(String varName) {
        // Search in variables
        VariableAchievement.AchievementVariable<?> var = variables.get(varName);
        if (var != null) {
            try {
                // Try to cast to Number. This might not actually reflect the underlying type, but it's the best we can do.
                // If there are internal errors, we simply return null in the provider
                VariableAchievement.AchievementVariable<Number> casted = (VariableAchievement.AchievementVariable<Number>) var;
                return s -> {
                    Object obj = casted.get(s);
                    if (obj instanceof Number n) {
                        return n.doubleValue();
                    } else return null;
                };
            } catch (ClassCastException e) {
                return null;
            }
        } else {
            // If not found, try to parse as constant double
            try {
                double value = Double.parseDouble(varName);
                return DoubleProvider.constant(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
    /**
     * Gets a PieceProvider from a String representation of a constant Piece. Syntax: {@code <shape>p<color>} or {@code <byte>p}.
     * @param str the String representation of the Piece
     * @return a PieceProvider that returns a Piece constant, or null if the string is not a valid representation
     */
    PieceProvider getPieceConstant(String str) {
        if (str.endsWith("p")) {
            str = str.substring(0, str.length() - 1);
            try {
                byte byteRepr = Byte.parseByte(str);
                Piece p = Piece.pieceFromByte(byteRepr, -2);
                return PieceProvider.constant(p);
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else if (str.contains("p")){
            // Get index of P and split into two parts
            int pIndex = str.indexOf('p');
            String shapePart = str.substring(0, pIndex);
            String colorPart = str.substring(pIndex + 1);
            try {
                byte shape = Byte.parseByte(shapePart);
                int color = Integer.parseInt(colorPart);
                Piece p = Piece.pieceFromByte(shape, color);
                return PieceProvider.constant(p);
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else {
            return null;
        }
    }
    /**
     * Gets a PieceProvider for the given variable name.
     * @param varName the name of the variable
     * @return a PieceProvider that retrieves a Piece from the variable, or null if the variable does not exist or is not a Piece
     */
    PieceProvider getPieceProvider(String varName) {
        // Search in variables
        VariableAchievement.AchievementVariable<?> var = variables.get(varName);
        if (var != null) {
            try {
                // Try to cast to Piece. This might not actually reflect the underlying type, but it's the best we can do.
                // If there are internal errors, we simply return null in the provider
                VariableAchievement.AchievementVariable<Piece> casted = (VariableAchievement.AchievementVariable<Piece>) var;
                return s -> {
                    Object obj = casted.get(s);
                    if (obj instanceof Piece p) {
                        return p;
                    } else return null;
                };
            } catch (ClassCastException e) {
                return null;
            }
        } else {
            // If not found, try to parse as constant piece
            if (varName.endsWith("p")) {
                varName = varName.substring(0, varName.length() - 1);
                try {
                    byte byteRepr = Byte.parseByte(varName);
                    Piece p = Piece.pieceFromByte(byteRepr, -2);
                    return PieceProvider.constant(p);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else if (varName.contains("p")){
                // Get index of P and split into two parts
                int pIndex = varName.indexOf('p');
                String shapePart = varName.substring(0, pIndex);
                String colorPart = varName.substring(pIndex + 1);
                try {
                    byte shape = Byte.parseByte(shapePart);
                    int color = Integer.parseInt(colorPart);
                    Piece p = Piece.pieceFromByte(shape, color);
                    return PieceProvider.constant(p);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    /**
     * Gets a BlockProvider from a String representation. Syntax: {@code <i>|<k>b<color>}, state is inferred from color.
     * @param str the String representation of the Block
     * @return a BlockProvider that returns a Block constant
     */
    BlockProvider getBlockProvider(String str){
        String[] parts = str.split("b");
        if (parts.length != 2) return null;
        String[] coords = parts[0].split("\\|");
        if (coords.length != 2) return null;
        try {
            int i = Integer.parseInt(coords[0]);
            int k = Integer.parseInt(coords[1]);
            int color = Integer.parseInt(parts[1]);
            boolean state = color != -1; // If color is -1, block is empty. This is the converse of the logic of the coloring system, which might not be true, but we treat it as such here.
            Block b = new Block(i, k, color, state);
            return BlockProvider.constant(b);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Gets a BlockProvider from a String representation. Syntax: {@code <binary>e}, state is inferred from color.
     * @param str the String representation of the Block
     * @return a BlockProvider that returns a Block constant
     */
    EngineProvider getEngineProvider(String str){
        if (str.endsWith("e")) {
            String binary = str.substring(0, str.length() - 1);
            try {
                // Construct boolean array from binary string
                boolean[] binaryArr = new boolean[binary.length()];
                for (int i = 0; i < binary.length(); i++) {
                    char c = binary.charAt(i);
                    if (c == '0' || c == 'O' || c == 'o' || c == '-' || c == 'F' || c == 'f') {
                        binaryArr[i] = false;
                    } else if (c == '1' || c == 'X' || c == 'x' || c == '+' || c == 'T' || c == 't') {
                        binaryArr[i] = true;
                    } else {
                        throw new IllegalArgumentException("Invalid character in binary string: " + c);
                    }
                }
                HexEngine engine = HexEngine.engineFromBooleans(binaryArr);
                return EngineProvider.constant(engine);
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else {
            return null;
        }
    }
    // We provide no function for Block because AchievementVariable does not support it, so we don't need to support it here either
    /**
     * A functional interface for supplying integer values from a GameState.
     * This wraps a {@link Function<>} that takes in a {@link GameState} and returns an {@link Integer}
     * to provide more readable code.
     */
    private interface IntegerProvider extends Function<GameState, Integer> {
        /**
         * Creates an IntegerProvider that always returns the given constant value.
         * @param value the constant value to return
         * @return an IntegerProvider that always returns the given constant value
         */
        static IntegerProvider constant(int value) {
            return state -> value;
        }
        /**
         * Creates an IntegerProvider that retrieves a value from a GameVariableSupplier.
         * If the value is a Number, it returns its integer value; otherwise, it returns null.
         * @param var the GameVariableSupplier to retrieve the value from
         * @return an IntegerProvider that retrieves a value from the given GameVariableSupplier
         */
        static IntegerProvider fromVariable(GameVariableSupplier<?> var){
            return state -> {
                Object obj = var.apply(state);
                if (obj instanceof Number n) {
                    return n.intValue();
                } else return null;
            };
        }
    }
    /**
     * A functional interface for supplying double values from a GameState.
     * This wraps a {@link Function<>} that takes in a {@link GameState} and returns a {@link Double}
     * to provide more readable code.
     */
    private interface DoubleProvider extends Function<GameState, Double> {
        /**
         * Creates a DoubleProvider that always returns the given constant value.
         * @param value the constant value to return
         * @return a DoubleProvider that always returns the given constant value
         */
        static DoubleProvider constant(double value) {
            return state -> value;
        }
        /**
         * Creates a DoubleProvider that retrieves a value from a GameVariableSupplier.
         * If the value is a Number, it returns its double value; otherwise, it returns null.
         * @param var the GameVariableSupplier to retrieve the value from
         * @return a DoubleProvider that retrieves a value from the given GameVariableSupplier
         */
        static DoubleProvider fromVariable(GameVariableSupplier<?> var){
            return state -> {
                Object obj = var.apply(state);
                if (obj instanceof Number n) {
                    return n.doubleValue();
                } else return null;
            };
        }
    }
    /**
     * A functional interface for supplying Block values from a GameState.
     * This wraps a {@link Function<>} that takes in a {@link GameState} and returns a {@link Block} to provider more readable code.
     */
    private interface BlockProvider extends Function<GameState, Block> {
        /**
         * Creates a BlockProvider that always returns the given constant Block.
         * @param block the constant Block to return
         * @return a BlockProvider that always returns the given constant Block
         */
        static BlockProvider constant(Block block) {
            return state -> block;
        }
        /**
         * Creates a BlockProvider that constructs a Block based on the given IntegerProviders for line I, line K, color, and block state.
         * If any of the line I, line K, or color providers return null, this provider returns null.
         * The block state provider determines if the block is occupied (non-zero) or empty (zero).
         * @param lineI the IntegerProvider for the line I coordinate
         * @param lineK the IntegerProvider for the line K coordinate
         * @param color the IntegerProvider for the color index
         * @param blockState the IntegerProvider for the block state (0 for empty, non-zero for occupied)
         * @return a BlockProvider that constructs a Block based on the provided IntegerProviders
         */
        static BlockProvider fromInteger(IntegerProvider lineI, IntegerProvider lineK, IntegerProvider color, IntegerProvider blockState) {
            return state -> {
                Integer i = lineI.apply(state);
                Integer k = lineK.apply(state);
                Integer c = color.apply(state);
                Integer s = blockState.apply(state);
                boolean occupied = s != null && s != 0;
                if (i == null || k == null || c == null) return null;
                return new Block(i, k, c, occupied);
            };
        }
        /**
         * Creates a BlockProvider that finds a Block in the current HexEngine based on the position of a Block provided by another BlockProvider.
         * If the provided BlockProvider returns null or if there is no HexEngine in the GameState, this provider returns null.
         * @param hexProvider the BlockProvider to get the target Block's position from
         * @return a BlockProvider that finds a Block in the current HexEngine based on the position of the provided Block
         */
        static BlockProvider findInEngine(BlockProvider hexProvider) {
            return state -> {
                Block target = hexProvider.apply(state);
                if (target == null) return null;
                HexEngine engine = state.getEngine();
                if (engine == null) return null;
                return engine.getBlock(target.thisHex());
            };
        }
    }
    /**
     * A functional interface for supplying Piece values from a GameState.
     * This wraps a {@link Function<>} that takes in a {@link GameState} and returns a {@link Piece} to provide more readable code.
     */
    private interface PieceProvider extends Function<GameState, Piece> {
        /**
         * Creates a PieceProvider that always returns the given constant Piece.
         * @param piece the constant Piece to return
         * @return a PieceProvider that always returns the given constant Piece
         */
        static PieceProvider constant(Piece piece) {
            return state -> piece;
        }
        /**
         * Creates a PieceProvider that retrieves a Piece from a GameVariableSupplier.
         * If the value is a Piece, it returns it;
         * if the value is a Number, it treats it as an index into the game's piece queue and returns the corresponding Piece;
         * otherwise, it returns null.
         * @param var the GameVariableSupplier to retrieve the value from
         * @return a PieceProvider that retrieves a Piece from the given GameVariableSupplier
         */
        static PieceProvider fromVariable(GameVariableSupplier<?> var){
            return state -> {
                Object obj = var.apply(state);
                if (obj instanceof Piece p) {
                    return p;
                } else if (obj instanceof Number n){
                    if (state == null) return null;
                    try {
                        return state.getQueue()[n.intValue()];
                    } catch (IndexOutOfBoundsException e){
                        return null;
                    }
                } else return null;
            };
        }
    }
    /**
     * A functional interface for supplying HexEngine values from a GameState.
     * This wraps a {@link Function<>} that takes in a {@link GameState} and returns a {@link HexEngine} to provide more readable code.
     */
    private interface EngineProvider extends Function<GameState, HexEngine> {
        /**
         * Creates an EngineProvider that always returns the given constant HexEngine.
         * @param engine the constant HexEngine to return
         * @return an EngineProvider that always returns the given constant HexEngine
         */
        static EngineProvider constant(HexEngine engine) {
            return state -> engine;
        }
    }

    // Predicates
    /**
     * A functional interface for block predicates.
     * The first argument is {@link GameState} context, the next is the Block to test.
     * This wraps a {@link BiPredicate<>} of {@link Block} to provide more readable code.
     */
    private interface BlockPredicate extends BiPredicate<GameState, Block>{}
    /**
     * A functional interface for block comparators.
     * The first argument is {@link GameState} context, the next two are the Blocks to compare.
     * This wraps a {@link TriPredicate<>} of {@link Block} to provide more readable code.
     */
    private interface BlockComparator extends TriPredicate<GameState, Block, Block> {}
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
                return (c, b) -> false;
            }
            case "true" -> {
                return (c, b) -> true;
            }
            case "state" -> {
                return (c, b) -> b.getState();
            }
            case "is" -> {
                if (args.length == 1 && args[0] instanceof BlockProvider bp) {
                    return (c, b) -> {
                        Block other = bp.apply(c);
                        return other != null && b.equals(other) && b.getColor() == other.getColor() && b.getState() == other.getState();
                    };
                }
            }
            case "color" -> {
                if (args.length == 1 && args[0] instanceof IntegerProvider ip) {
                    return (c, b) -> {
                        Integer color = ip.apply(c);
                        return color != null && b.getColor() == color;
                    };
                }
            }
            case "at" -> {
                if (args.length == 2 && args[0] instanceof IntegerProvider ip && args[1] instanceof IntegerProvider kp) {
                    return (c, b) -> {
                        Integer i = ip.apply(c);
                        Integer k = kp.apply(c);
                        return i != null && k != null && b.getLineI() == i && b.getLineK() == k;
                    };
                }
            }
            case "or" -> {
                if (args.length == 2 && args[0] instanceof BlockPredicate p1 && args[1] instanceof BlockPredicate p2) {
                    return (c, b) -> p1.test(c, b) || p2.test(c, b);
                }
            }
            case "and" -> {
                if (args.length == 2 && args[0] instanceof BlockPredicate p1 && args[1] instanceof BlockPredicate p2) {
                    return (c, b) -> p1.test(c, b) && p2.test(c, b);
                }
            }
            case "not" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p) {
                    return (c, b) -> !p.test(c, b);
                }
            }
        }
        return null;
    }
    /**
     * Creates a BlockComparator based on the given operation.
     * The supported operations are:
     * <ul>
     *     <li>"overlap": checks if two blocks occupy the same position</li>
     *     <li>"is": checks if two blocks are identical in state and color</li>
     *     <li>"not": checks if two blocks differ in state or color</li>
     *     <li>"analogous": checks if two blocks have the same state</li>
     *     <li>"divergent": checks if two blocks have different states</li>
     *     <li>"color": checks if two blocks have the same color</li>
     *     <li>"varied": checks if two blocks have different colors</li>
     *     <li>"separate": checks if two blocks occupy different positions</li>
     *     <li>"i-line": checks if two blocks are in the same I line</li>
     *     <li>"j-line": checks if two blocks are in the same J line</li>
     *     <li>"k-line": checks if two blocks are in the same K line</li>
     *     <li>"i-adjacent": checks if two blocks are adjacent along the I axis</li>
     *     <li>"j-adjacent": checks if two blocks are adjacent along the J axis</li>
     *     <li>"k-adjacent": checks if two blocks are adjacent along the K axis</li>
     *     <li>"adjacent": checks if two blocks are adjacent in any direction</li>
     *     <li>"front": checks if one block is in front of another (lower I, J, or K value)</li>
     *     <li>"back": checks if one block is behind another (higher I, J, or K value)</li>
     * </ul>
     * @param op the operation to perform
     * @return a BlockComparator based on the operation, or null if the operation is not recognized
     */
    private static BlockComparator blockComparator(String op){
        return switch (op) {
            case "overlap" -> (c, b1, b2) -> b1.equals(b2.thisHex()); // Note this only check position
            case "is" -> (c, b1, b2) -> b1.getState() == b2.getState() && b1.getColor() == b2.getColor();
            case "not" -> (c, b1, b2) -> b1.getState() != b2.getState() || b1.getColor() != b2.getColor();
            case "analogous" -> (c, b1, b2) -> b1.getState() == b2.getState();
            case "divergent" -> (c, b1, b2) -> b1.getState() != b2.getState();
            case "color" -> (c, b1, b2) -> b1.getColor() == b2.getColor();
            case "separate" -> (c, b1, b2) -> !b1.equals(b2.thisHex()); // Note this only check position
            case "varied" -> (c, b1, b2) -> b1.getColor() != b2.getColor();
            case "i-line" -> (c, b1, b2) -> b1.inLineI(b2);
            case "j-line" -> (c, b1, b2) -> b1.inLineJ(b2);
            case "k-line" -> (c, b1, b2) -> b1.inLineK(b2);
            case "i-adjacent" -> (c, b1, b2) -> b1.adjacentI(b2);
            case "j-adjacent" -> (c, b1, b2) -> b1.adjacentJ(b2);
            case "k-adjacent" -> (c, b1, b2) -> b1.adjacentK(b2);
            case "adjacent" -> (c, b1, b2) -> b1.adjacent(b2);
            case "front" -> (c, b1, b2) -> b1.front(b2);
            case "back" -> (c, b1, b2) -> b1.back(b2);
            default -> null;
        };
    }
    /**
     * A functional interface for line predicates.
     * The first argument is {@link GameState} context, the next is the Block array (line) to test.
     * This wraps a {@link BiPredicate<>} of {@link Block} arrays to provide more readable code.
     */
    private interface LinePredicate extends BiPredicate<GameState, Block[]>{}
    /**
     * Creates a LinePredicate based on the given operation and arguments.
     * The supported operations are:
     * <ul>
     *     <li>"false": always returns false</li>
     *     <li>"true": always returns true</li>
     *     <li>"length": checks if the line length is within the given bounds (args[0], args[1])</li>
     *     <li>"any": checks if any block in the line satisfies the given block predicate (args[0])</li>
     *     <li>"none": checks if no blocks in the line satisfy the given block predicate (args[0])</li>
     *     <li>"all": checks if all blocks in the line satisfy the given block predicate (args[0])</li>
     *     <li>"ratio": checks if the ratio of blocks satisfying the given block predicate (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li>"count": checks if the count of blocks satisfying the given block predicate (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li>"sequence": checks if there is a sequence of at least a given length (args[1]) of blocks satisfying the given block predicate (args[0])</li>
     *     <li>"checker": checks if blocks in even positions satisfy one block predicate (args[0]) and blocks in odd positions satisfy another block predicate (args[1])</li>
     *     <li>"anypair": checks if any adjacent pair of blocks in the line satisfy the given block comparator (args[0])</li>
     *     <li>"nopair": checks if no adjacent pairs of blocks in the line satisfy the given block comparator (args[0])</li>
     *     <li>"allpairs": checks if all adjacent pairs of blocks in the line satisfy the given block comparator (args[0])</li>
     *     <li>"parts": checks if the ratio of adjacent pairs of blocks satisfying the given block comparator (args[0]) is within the given bounds (args[1], args[2])</li>
     *     <li>"pairs": checks if there is a sequence of at least a given length (args[1]) of adjacent pairs of blocks satisfying the given block comparator (args[0])</li>
     *     <li>"xor": logical XOR of two line predicates (args[0], args[1])</li>
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
                return (s, l) -> false;
            }
            case "true" -> {
                return (s, l) -> true;
            }
            case "length" -> {
                if (args.length == 2 && args[0] instanceof IntegerProvider lower && args[1] instanceof IntegerProvider upper){
                    return (s, l) -> {
                        Integer lo = lower.apply(s);
                        Integer up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        int len = l.length;
                        return lo <= len && len <= up;
                    };
                }
            }
            case "any" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p){
                    return (s, l) -> {
                        for (Block b : l){
                            if (p.test(s, b)) return true;
                        }
                        return false;
                    };
                }
            }
            case "none" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p){
                    return (s, l) -> {
                        for (Block b : l){
                            if (p.test(s, b)) return false;
                        }
                        return true;
                    };
                }
            }
            case "all" -> {
                if (args.length == 1 && args[0] instanceof BlockPredicate p){
                    return (s, l) -> {
                        for (Block b : l){
                            if (!p.test(s, b)) return false;
                        }
                        return true;
                    };
                }
            }
            case "ratio" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate p && args[1] instanceof DoubleProvider lower && args[2] instanceof DoubleProvider upper){
                    return (s, l) -> {
                        int c = 0; double r;
                        for (Block b : l){
                            if (p.test(s, b)) c++;
                        }
                        if (l.length > 0){
                            r = (double) c / l.length;
                        } else {
                            r = 0.0;
                        }
                        Double lo = lower.apply(s);
                        Double up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        return lo <= r && r <= up;
                    };
                }
            }
            case "count" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate p && args[1] instanceof IntegerProvider lower && args[2] instanceof IntegerProvider upper){
                    return (s, l) -> {
                        int c = 0;
                        for (Block b : l){
                            if (p.test(s, b)) c++;
                        }
                        Integer lo = lower.apply(s);
                        Integer up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        return lo <= c && c <= up;
                    };
                }
            }
            case "sequence" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate p && args[1] instanceof IntegerProvider len){
                    return (s, l) -> {
                        Integer lenVal = len.apply(s);
                        if (lenVal == null) return false;
                        if (l.length >= lenVal) {
                            int c = 0;
                            for (Block b : l){
                                if (p.test(s, b)) {
                                    c++;
                                    if (c > lenVal) return true;
                                } else c = 0;
                            }
                        }
                        return false;
                    };
                }
            }
            case "checker" -> {
                if (args.length == 3 && args[0] instanceof BlockPredicate e && args[1] instanceof BlockPredicate o){
                    return (s, l) -> {
                        for (int i = 0; i < l.length; i++) {
                            Block b = l[i];
                            if (i % 2 == 0){
                                if (!e.test(s, b)) return false;
                            } else if (!o.test(s, b)) return false;
                        }
                        return true;
                    };
                }
            }
            case "anypair" -> {
                if (args.length == 2 && args[0] instanceof BlockComparator c){
                    return (s, l) -> {
                        for (int i = 0; i < l.length - 1; i++){
                            if (c.test(s, l[i], l[i+1])) return true;
                        }
                        return false;
                    };
                }
            }
            case "nopair" -> {
                if (args.length == 2 && args[0] instanceof BlockComparator c){
                    return (s, l) -> {
                        for (int i = 0; i < l.length - 1; i++){
                            if (c.test(s, l[i], l[i+1])) return false;
                        }
                        return true;
                    };
                }
            }
            case "allpairs" -> {
                if (args.length == 2 && args[0] instanceof BlockComparator c){
                    return (s, l) -> {
                        for (int i = 0; i < l.length - 1; i++){
                            if (!c.test(s, l[i], l[i+1])) return false;
                        }
                        return true;
                    };
                }
            }
            case "parts" ->{
                if (args.length == 3 && args[0] instanceof BlockComparator p && args[1] instanceof DoubleProvider lower && args[2] instanceof DoubleProvider upper) {
                    return (s, l) -> {
                        int c = 0; double r;
                        for (int i = 0; i < l.length - 1; i++){
                            if (p.test(s, l[i], l[i+1])) c++;
                        }
                        if (l.length > 1){
                            r = (double) c / (l.length - 1);
                        } else {
                            r = 0.0;
                        }
                        Double lo = lower.apply(s);
                        Double up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        return lo <= r && r <= up;
                    };
                }
            }
            case "pairs" -> {
                if (args.length == 3 && args[0] instanceof BlockComparator c && args[1] instanceof IntegerProvider len){
                    return (s, l) -> {
                        Integer lenVal = len.apply(s);
                        if (lenVal == null) return false;
                        if (l.length >= lenVal) {
                            int count = 0;
                            for (int i = 0; i < l.length - 1; i++){
                                if (c.test(s, l[i], l[i+1])) {
                                    count++;
                                    if (count >= lenVal) return true;
                                } else count = 0;
                            }
                        }
                        return false;
                    };
                }
            }
            case "xor" -> {
                if (args.length == 2 && args[0] instanceof LinePredicate p1 && args[1] instanceof LinePredicate p2) {
                    return (s, l) -> p1.test(s, l) ^ p2.test(s, l);
                }
            }
            case "not" -> {
                if (args.length == 1 && args[0] instanceof LinePredicate p) {
                    return (s, l) -> !p.test(s, l);
                }
            }
            case "or" -> {
                if (args.length == 2 && args[0] instanceof LinePredicate p1 && args[1] instanceof LinePredicate p2) {
                    return (s, l) -> p1.test(s, l) || p2.test(s, l);
                }
            }
            case "and" -> {
                if (args.length == 2 && args[0] instanceof LinePredicate p1 && args[1] instanceof LinePredicate p2) {
                    return (s, l) -> p1.test(s, l) && p2.test(s, l);
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
     * The first argument is {@link GameState} context, the next is the {@link HexEngine engine} to test.
     * This wraps a {@link BiPredicate<>} of {@link HexEngine engine} to provide more readable code.
     */
    private interface EnginePredicate extends BiPredicate<GameState, HexEngine>{}
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
     *     <li><b>equals</b>: checks whether argument variables equals to each other. This is not recommended, and {@link VariableAchievement} should
     *     be used instead of this {@link EngineBasedAchievement}</li>
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
                    return (s, e) -> {
                        for (Block[] line : bis.apply(e)) {
                            if (!predicate.test(s, line)) return false;
                        }
                        return true;
                    };
                }
            }
            case "none" -> {
                if (args.length == 2 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate) {
                    return (s, e) -> {
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(s, line)) return false;
                        }
                        return true;
                    };
                }
            }
            case "any" -> {
                if (args.length == 2 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate) {
                    return (s, e) -> {
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(s, line)) return true;
                        }
                        return false;
                    };
                }
            }
            case "ratio" -> {
                if (args.length == 4 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate && args[2] instanceof DoubleProvider upper && args[3] instanceof DoubleProvider lower) {
                    return (s, e) -> {
                        int c = 0; int t = 0;
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(s, line)){
                                c++;
                            }
                            t++;
                        }
                        double r = (t == 0) ? 0.0 : (double) c / t;
                        Double lo = lower.apply(s);
                        Double up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        return lo <= r && r <= up;
                    };
                }
            }
            case "sequence" -> {
                if (args.length == 3 && args[0] instanceof BlocksIterableSupplier bis && args[1] instanceof LinePredicate predicate && args[2] instanceof IntegerProvider len) {
                    return (s, e) -> {
                        Integer lenVal = len.apply(s);
                        if (lenVal == null) return false;
                        int c = 0;
                        for (Block[] line : bis.apply(e)) {
                            if (predicate.test(s, line)){
                                c++;
                                if (c >= lenVal) return true;
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
                    return (s, e) -> {
                        for (Block[] line : bis.apply(e)) {
                            for (int i = 0; i < line.length; i++) {
                                Block b = line[i];
                                if (i % 2 == 0) {
                                    if (!even.test(s, b)) return false;
                                } else {
                                    if (!odd.test(s, b)) return false;
                                }
                            }
                        }
                        return true;
                    };
                }
            }
            // Native
            case "filled" -> {
                if (args.length == 2 && args[0] instanceof DoubleProvider lower && args[1] instanceof DoubleProvider upper) {
                    return (s, e) -> {
                        double filled = e.getPercentFilled();
                        Double lo = lower.apply(s);
                        Double up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        return lo <= filled && filled <= up;
                    };
                }
            }
            case "entropy" -> {
                if (args.length == 2 && args[0] instanceof DoubleProvider lower && args[1] instanceof DoubleProvider upper) {
                    return (s, e) -> {
                        double ent = e.computeEntropy(); // We absolutely need to cache this costly operation
                        Double lo = lower.apply(s);
                        Double up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        return lo <= ent && ent <= up;
                    };
                }
            }
            case "length" -> {
                if (args.length == 2 && args[0] instanceof IntegerProvider lower && args[1] instanceof IntegerProvider upper) {
                    return (s, e) -> {
                        Integer lo = lower.apply(s);
                        Integer up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        int len = e.length();
                        return lo <= len && len <= up;
                    };
                }
            }
            case "radius" -> {
                if (args.length == 2 && args[0] instanceof IntegerProvider lower && args[1] instanceof IntegerProvider upper) {
                    return (s, e) -> {
                        Integer lo = lower.apply(s);
                        Integer up = upper.apply(s);
                        if (lo == null || up == null) return false;
                        int radius = e.getRadius();
                        return lo <= radius && radius <= up;
                    };
                }
            }
            case "density-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> densityIndexSupplier = constructIndexSupplier(HexEngine::computeDenseIndex, "avg");
                    if (densityIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = densityIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "densest-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> densestIndexSupplier = constructIndexSupplier(HexEngine::computeDenseIndex, "max");
                    if (densestIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = densestIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "sparsest-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> sparsestIndexSupplier = constructIndexSupplier(HexEngine::computeDenseIndex, "min");
                    if (sparsestIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = sparsestIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "entropy-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> entropyIndexSupplier = constructIndexSupplier(HexEngine::computeEntropyIndex, "avg");
                    if (entropyIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = entropyIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "most-entropic-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> mostEntropicIndexSupplier = constructIndexSupplier(HexEngine::computeEntropyIndex, "max");
                    if (mostEntropicIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = mostEntropicIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "least-entropic-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> leastEntropicIndexSupplier = constructIndexSupplier(HexEngine::computeEntropyIndex, "min");
                    if (leastEntropicIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = leastEntropicIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "eliminate-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> eliminateIndexSupplier = constructIndexSupplier(EngineBasedAchievement::eliminateIndex, "max");
                    if (eliminateIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = eliminateIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "reduction-index" -> {
                if (args.length == 3 && args[0] instanceof PieceProvider pp && args[1] instanceof DoubleProvider lp && args[2] instanceof DoubleProvider up) {
                    final BiFunction<HexEngine, HexGrid, Double> eliminateIndexSupplier = constructIndexSupplier(EngineBasedAchievement::eliminateIndex, "avg");
                    if (eliminateIndexSupplier == null) return null;
                    return (s, e) -> {
                        Piece piece = pp.apply(s); Double lower = lp.apply(s); Double upper = up.apply(s);
                        if (piece == null || lower == null || upper == null) return false;
                        double index = eliminateIndexSupplier.apply(e, piece);
                        return lower <= index && index <= upper;
                    };
                }
            }
            case "is" -> {
                if (args.length == 1 && args[0] instanceof EngineProvider ep) {
                    return (s, e) -> {
                        HexEngine other = ep.apply(s);
                        return e.equals(other);
                    };
                }
            }
            case "matches" -> {
                if (args.length == 1 && args[0] instanceof EngineProvider ep) {
                    return (s, e) -> {
                        HexEngine other = ep.apply(s);
                        return e.equalsIgnoreColor(other);
                    };
                }
            }
            case "appears" -> {
                if (args.length == 1 && args[0] instanceof IntegerProvider pp) {
                    return (s, e) -> {
                        Integer pattern = pp.apply(s);
                        if (pattern == null) return false;
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
                if (args.length == 1 && args[0] instanceof IntegerProvider pp) {
                    return (s, e) -> {
                        Integer pattern = pp.apply(s);
                        if (pattern == null) return false;
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
                    return (s, e) -> !p.test(s, e);
                }
            }
            case "or" -> {
                if (args.length == 2 && args[0] instanceof EnginePredicate p1 && args[1] instanceof EnginePredicate p2) {
                    return (s, e) -> p1.test(s, e) || p2.test(s, e);
                }
            }
            case "and" -> {
                if (args.length == 2 && args[0] instanceof EnginePredicate p1 && args[1] instanceof EnginePredicate p2) {
                    return (s, e) -> p1.test(s, e) && p2.test(s, e);
                }
            }
            case "xor" -> {
                if (args.length == 2 && args[0] instanceof EnginePredicate p1 && args[1] instanceof EnginePredicate p2) {
                    return (s, e) -> p1.test(s, e) ^ p2.test(s, e);
                }
            }
            // Variable equals
            case "equals" -> {
                if (args.length == 2) {
                    if (args[0] instanceof DoubleProvider d1 && args[1] instanceof DoubleProvider d2) {
                        return (s, e) -> {
                            Double v1 = d1.apply(s);
                            Double v2 = d2.apply(s);
                            if (v1 == null || v2 == null) return false;
                            return v1.equals(v2);
                        };
                    } else if (args[0] instanceof IntegerProvider i1 && args[1] instanceof IntegerProvider i2) {
                        return (s, e) -> {
                            Integer v1 = i1.apply(s);
                            Integer v2 = i2.apply(s);
                            if (v1 == null || v2 == null) return false;
                            return v1.equals(v2);
                        };
                    } else if (args[0] instanceof PieceProvider p1 && args[1] instanceof PieceProvider p2) {
                        return (s, e) -> {
                            Piece v1 = p1.apply(s);
                            Piece v2 = p2.apply(s);
                            if (v1 == null || v2 == null) return false;
                            return v1.equals(v2);
                        };
                    } else if (args[0] instanceof EngineProvider ep1 && args[1] instanceof EngineProvider ep2) {
                        return (s, e) -> {
                            HexEngine v1 = ep1.apply(s);
                            HexEngine v2 = ep2.apply(s);
                            if (v1 == null || v2 == null) return false;
                            return v1.equals(v2);
                        };
                    }
                }
            }
        }
        return null;
    }

    // Compiler
    // Syntax: use spaces, use () to group, always starts with engine() (unless it is equals), everything is treated as function. If something take no arguments, () can be omitted.
    // Constants: #{value}. Value can be integer, double, piece as byte representation followed by the symbol P, engine as booleanString followed by the symbol E
    // Variables: $name, where name is the variable name, or anonymously using ${type: expr}, where type is the type of the expression, expr is an expression that evaluates to a number or piece.
    // See the official documentation in GameVariable for expression syntax.
    // Example: engine( and (ratio (#{0.4}, #{0.5}), any (lines, pairs (${radius - 1}, color))))
    // The example means: in the engine, the fill ratio is between 40% and 50%, and there exists at least one line with at least (engine radius - 1) pairs of same-color adjacent blocks.
    /**
     * Compiles a string expression into an EnginePredicate.
     * The expression language supports functions, constants, and variables as described in the class documentation.
     * The top-level expression must be either an "equals" function or an "engine" function.
     * @param expr the string expression to compile
     * @return an EnginePredicate representing the compiled expression
     * @throws IllegalArgumentException if the expression is invalid or cannot be compiled
     */
    private EnginePredicate compile(String expr) {
        expr = expr.toLowerCase().trim();
        // Check () balance
        int balance = 0;
        char[] charArray = expr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '(') balance++;
            else if (c == ')') balance--;
            if (balance < 0)
                throw new IllegalArgumentException("Unmatched parentheses in expression " + expr + " at position " + i);
        }
        if (balance != 0)
            throw new IllegalArgumentException("Unmatched parentheses in expression " + expr + " at end of expression");
        // Check that there are no {} inside {}, and for each { immediately there is either # or $
        boolean inCurly = false;
        char prevChar = 0;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '{') {
                if (inCurly)
                    throw new IllegalArgumentException("Nested curly braces in expression " + expr + " at position " + i);
                inCurly = true;
                if (prevChar != '#' && prevChar != '$') return null; // Not preceded by # or $
            } else if (c == '}') {
                if (!inCurly) return null; // Unmatched }
                inCurly = false;
            }
            prevChar = c;
        }
        // If this starts with equals function, parse it directly
        if (expr.startsWith("equals")) {
            String iexpr = expr.substring(6).trim();
            if (iexpr.startsWith("(") && iexpr.endsWith(")")) {
                iexpr = expr.substring(1, iexpr.length() - 1).trim();
            } else {
                throw new IllegalArgumentException("Top level expression is not a legal function in expression " + expr);
            }
            // Find the comma that separates the two arguments
            int commaIndex = iexpr.indexOf(',');
            if (commaIndex == -1)
                throw new IllegalArgumentException("Function 'equals' requires two arguments in expression " + expr);
            String arg1 = iexpr.substring(0, commaIndex).trim();
            String arg2 = iexpr.substring(commaIndex + 1).trim();
            // Check arg1 type (constant, named variable, or expression variable)
            Pair<Object, Class<?>> parsed1 = parseVariable(arg1);
            Pair<Object, Class<?>> parsed2 = parseVariable(arg2);
            if (!parsed1.getLast().equals(parsed2.getLast())) {
                throw new IllegalArgumentException("Function 'equals' requires both arguments to be of the same type in expression " + expr);
            } else if (parsed1.getLast().equals(Integer.class)) {
                return enginePredicate("equals", new Object[]{(IntegerProvider) parsed1.getFirst(), (IntegerProvider) parsed2.getFirst()});
            } else if (parsed1.getLast().equals(Double.class)) {
                return enginePredicate("equals", new Object[]{(DoubleProvider) parsed1.getFirst(), (DoubleProvider) parsed2.getFirst()});
            } else if (parsed1.getLast().equals(Piece.class)) {
                return enginePredicate("equals", new Object[]{(PieceProvider) parsed1.getFirst(), (PieceProvider) parsed2.getFirst()});
            } else if (parsed1.getLast().equals(HexEngine.class)) {
                return enginePredicate("equals", new Object[]{(EngineProvider) parsed1.getFirst(), (EngineProvider) parsed2.getFirst()});
            } else {
                throw new IllegalArgumentException("Function 'equals' does not support arguments of type " + parsed1.getLast().getSimpleName() + " in expression " + expr);
            }
        } else if (expr.startsWith("engine")) {
            // Remove the leading "engine" and () and parse the rest recursively
            String iexpr = expr.substring(6).trim();
            if (iexpr.startsWith("(") && iexpr.endsWith(")")) {
                iexpr = iexpr.substring(1, iexpr.length() - 1).trim();
                Object result = compilerRecursive(iexpr);
                try {
                    return (EnginePredicate) result;
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Function " + expr +  " does evaluates to engine predicate, but got " + result.getClass().getInterfaces()[0].getSimpleName() + " instead");
                }
            } else {
                throw new IllegalArgumentException("Top level expression is not a legal function in " + expr);
            }
        } else {
            throw new IllegalArgumentException("Top level expression is not a legal function in " + expr);
        }
    }
    /**
     * Recursively compiles a sub-expression into the appropriate predicate or provider.
     * This method handles function parsing, argument splitting, and recursive compilation of arguments.
     * <p>
     * The function name is determined by the substring before the first opening parenthesis.
     * The arguments are extracted from within the parentheses and split by commas, ignoring commas inside nested
     * parentheses, brackets, braces, or quotes.
     * Each argument is then compiled recursively.
     * <p>
     * The compiled function is then matched against known predicates and providers to construct the appropriate object.
     * If the compiler cannot recognize the function name or the arguments do not match any known signature,
     * an IllegalArgumentException is thrown with a very detailed message to aid debugging.
     * @see #compile(String)
     * @param expr the sub-expression to compile
     * @return an Object representing the compiled predicate or provider
     * @throws IllegalArgumentException if the sub-expression is invalid or cannot be compiled
     */
    private Object compilerRecursive(String expr){
        expr = expr.trim();
        if (expr.isEmpty())
            throw new IllegalArgumentException("Empty expression");
        // If this is a variable or constant, parse it directly
        if ((expr.startsWith("#{") && expr.endsWith("}")) ||
            (expr.startsWith("$")) ||
            (expr.startsWith("${") && expr.endsWith("}"))) {
            return parseVariable(expr).getFirst();
        }
        int firstParen = expr.indexOf('(');
        String funcName; String argsStr;
        if (firstParen == -1) {
            // No parentheses, so this is a function with no arguments
            funcName = expr.trim(); String[] emptyArgs = new String[0];
            Object result = linePredicates(funcName, emptyArgs); // We can skip engine predicates as they all require arguments
            if (result != null) {
                return result;
            } else if (blockComparator(funcName) != null) {
                return blockComparator(funcName);
            } else if (blocksIterable(funcName) != null) {
                return blocksIterable(funcName);
            } else throw new IllegalArgumentException("Unknown function " + funcName + "() in expression " + expr);
        } else {
            funcName = expr.substring(0, firstParen).trim();
            argsStr = expr.substring(firstParen).trim();
            if (!argsStr.startsWith("(") || !argsStr.endsWith(")"))
                throw new IllegalArgumentException("Malformed function " + funcName + " in expression " + expr);
            argsStr = argsStr.substring(1, argsStr.length() - 1);
            // Split argsStr by commas, but ignore commas inside parentheses
            String[] argStrings = splitArgs(argsStr);
            Object[] args = new Object[argStrings.length];
            for (int i = 0; i < argStrings.length; i++) {
                args[i] = compilerRecursive(argStrings[i]);
            }
            // Now we have funcName and args, construct the appropriate predicate or provider
            Object result = enginePredicate(funcName, args);
            if (result != null) {
                return result;
            } else {
                result = linePredicates(funcName, args);
            }
            if (result != null) {
                return result;
            } else {
                result = blockPredicates(funcName, args);
            }
            if (result != null) {
                return result;
            } else if (argStrings.length == 0){
                result = blockComparator(funcName); // Only attempt if there are no arguments, as comparators take no arguments
            }
            if (result != null) {
                return result;
            } else if (argStrings.length == 0) {
                result = blocksIterable(funcName); // Only attempt if there are no arguments, as iterable for block take no arguments
            }
            if (result != null) {
                return result;
            } else {
                StringBuilder exceptionStr = new StringBuilder("Unknown function " + funcName);
                // Add types of args to the exception string. This is to help debugging because most errors are due to wrong signature
                if (args.length > 0) {
                    exceptionStr.append("(");
                    for (int i = 0; i < args.length; i++) {
                        Object arg = args[i];
                        switch (arg) {
                            case EnginePredicate enginePredicate -> exceptionStr.append("EnginePredicate");
                            case LinePredicate linePredicate -> exceptionStr.append("LinePredicate");
                            case BlockPredicate blockPredicate -> exceptionStr.append("BlockPredicate");
                            case BlockComparator blockComparator -> exceptionStr.append("BlockComparator");
                            case BlocksIterableSupplier blocksIterableSupplier -> exceptionStr.append("BlocksIterableSupplier");
                            case IntegerProvider integerProvider -> exceptionStr.append("IntegerProvider");
                            case DoubleProvider doubleProvider -> exceptionStr.append("DoubleProvider");
                            case PieceProvider pieceProvider -> exceptionStr.append("PieceProvider");
                            case EngineProvider engineProvider -> exceptionStr.append("EngineProvider");
                            case Pair<?, ?> p -> exceptionStr.append("Pair<").append(p.getLast().getClass().getSimpleName()).append(">");
                            case null -> exceptionStr.append("null");
                            default -> exceptionStr.append(arg.getClass().getInterfaces()[0].getSimpleName());
                        }
                        if (i < args.length - 1) exceptionStr.append(", ");
                    }
                    exceptionStr.append(") in expression ").append(expr);
                }
                throw new IllegalArgumentException(exceptionStr.toString());
            }
        }
    }
    /**
     * Splits a string of arguments separated by commas, ignoring commas inside parentheses, brackets, braces, or quotes.
     * This method handles nested structures and quoted strings correctly.
     * @param argString the string of arguments to split
     * @return an array of argument strings
     */
    private static String[] splitArgs(String argString) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depthParen = 0, depthBracket = 0, depthBrace = 0, depthSingleQuote = 0, depthDoubleQuote = 0;

        for (int i = 0; i < argString.length(); i++) {
            char c = argString.charAt(i);
            switch (c) {
                case '(' -> {
                    depthParen++;
                    current.append(c);
                }
                case ')' -> {
                    depthParen--;
                    current.append(c);
                }
                case '[' -> {
                    depthBracket++;
                    current.append(c);
                }
                case ']' -> {
                    depthBracket--;
                    current.append(c);
                }
                case '{' -> {
                    depthBrace++;
                    current.append(c);
                }
                case '}' -> {
                    depthBrace--;
                    current.append(c);
                }
                case '\'' -> {
                    depthSingleQuote = 1 - depthSingleQuote; // Toggle
                    current.append(c);
                }
                case '"' -> {
                    depthDoubleQuote = 1 - depthDoubleQuote; // Toggle
                    current.append(c);
                }
                case ',' -> {
                    if (depthParen == 0 && depthBracket == 0 && depthBrace == 0 && depthSingleQuote == 0 && depthDoubleQuote == 0) {
                        String val = current.toString().trim();
                        if (!val.isEmpty()) result.add(val);
                        current.setLength(0);
                        continue; // skip appending the comma
                    } else {
                        current.append(c);
                    }
                }
                default -> current.append(c);
            }
        }
        // add last piece
        String val = current.toString().trim();
        if (!val.isEmpty()) result.add(val);
        return result.toArray(new String[0]);
    }

    /**
     * Parses a variable argument which can be a constant (#{...}), a named variable ($name), or an expression variable (${type: expr}).
     *
     * @param arg the argument string to parse
     * @return a Pair containing the parsed provider (IntegerProvider, DoubleProvider, PieceProvider) and its corresponding Class type (Integer.class, Double.class, Piece.class)
     * @throws IllegalArgumentException if the argument is invalid or the variable is undefined
     */
    private Pair<Object, Class<?>> parseVariable(String arg) {
        Object provider; Class<?> clazz;
        if (arg.startsWith("#{") && arg.endsWith("}")) {
            String constStr = arg.substring(2, arg.length() - 1).trim();
            provider = getIntegerConstant(constStr);
            if (provider == null) {
                provider = getDoubleConstant(constStr);
                if (provider == null) {
                    provider = getPieceConstant(constStr);
                    if (provider == null) {
                        provider = getEngineProvider(constStr);
                        if (provider == null) {
                            provider = getBlockProvider(constStr);
                            if (provider == null) {
                                throw new IllegalArgumentException("Invalid constant " + constStr + " expression");
                            } else clazz = Block.class;
                        } else clazz = HexEngine.class;
                    } else clazz = Piece.class;
                } else clazz = Double.class;
            } else clazz = Integer.class;
        } else if (arg.startsWith("${") && arg.endsWith("}")) {
            String varExpr = arg.substring(2, arg.length() - 1).trim();
            // Get type hint before the first colon
            int colonIndex = varExpr.indexOf(':');
            if (colonIndex == -1)
                throw new IllegalArgumentException("Invalid variable " + varExpr + " expression");
            String typeHint = varExpr.substring(0, colonIndex).trim().toLowerCase();
            varExpr = varExpr.substring(colonIndex + 1).trim();
            // Parse the variable expression
            GameVariableSupplier<?> supplier;
            try {
                supplier = GameVariableSupplier.parse(varExpr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid variable " + varExpr + " expression");
            }
            provider = switch (typeHint) {
                case "int", "integer" -> (IntegerProvider) (s) -> {
                    Object val = supplier.apply(s);
                    if (val instanceof Integer i) return i;
                    if (val instanceof Double d) return d.intValue();
                    return null;
                };
                case "double", "float" -> (DoubleProvider) (s) -> {
                    Object val = supplier.apply(s);
                    if (val instanceof Double d) return d;
                    if (val instanceof Integer i) return i.doubleValue();
                    return null;
                };
                case "piece" -> (PieceProvider) (s) -> {
                    Object val = supplier.apply(s);
                    if (val instanceof Piece p) return p;
                    return null;
                };
                default ->
                        throw new IllegalArgumentException("Invalid type hint " + typeHint + " in variable " + varExpr + " expression");
            };
            clazz = switch (typeHint) {
                case "int", "integer" -> Integer.class;
                case "double", "float" -> Double.class;
                case "piece" -> Piece.class;
                default -> throw new IllegalArgumentException("Invalid type hint " + typeHint + " in variable " + varExpr + " expression");
            };
        } else if (arg.startsWith("$")) {
            String varName = arg.substring(1).trim();
            VariableAchievement.AchievementVariable<?> achievementVariable = variables.get(varName);
            if (achievementVariable == null)
                throw new IllegalArgumentException("Undefined variable " + varName);
            // Check type. Here they are named, so we can directly use the cached version
            clazz = achievementVariable.type();
            if (clazz.equals(Integer.class)) {
                provider = (IntegerProvider) (s) -> (Integer) achievementVariable.get();
            } else if (clazz.equals(Double.class)) {
                provider = (DoubleProvider) (s) -> (Double) achievementVariable.get();
            } else if (clazz.equals(Piece.class)) {
                provider = (PieceProvider) (s) -> (Piece) achievementVariable.get();
            } else {
                throw new IllegalArgumentException("Unsupported variable type " + achievementVariable.type().getSimpleName() + " for variable " + varName);
            }
        } else {
            throw new IllegalArgumentException("Invalid argument");
        }
        return new Pair<>(provider, clazz);
    }
}