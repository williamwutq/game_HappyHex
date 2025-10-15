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
import hex.GameState;
import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Objects;

/**
 * The {@code VariableAchievement} class represents a game achievement that is achieved when a specified variable
 * reaches zero. It implements the {@link GameAchievementTemplate} interface and provides functionality to check
 * if the achievement has been achieved based on the current {@link GameState}.
 * <p>
 * This class uses a {@link GameVariableSupplier} to compute the value of the variable based on the game state.
 * The achievement is considered achieved when the variable's value is zero.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed. This ensures
 * that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class provides serialization and deserialization methods to convert the achievement to and from JSON format.
 * This allows for easy storage and retrieval of achievement data.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @see JsonConvertible
 * @see GameVariableSupplier
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class VariableAchievement implements GameAchievementTemplate, JsonConvertible {
    /**
     * A wrapper class for achievement variables.
     * <p>
     * Warning: Do not use equals or hashCode on this class
     *
     * @param <T> the type of the variable
     */
    public static class AchievementVariable<T> {
        private final String symbol;
        private final GameVariableSupplier<T> supplier;
        private final Class<T> type;
        private T value;
        /**
         * Constructs an AchievementVariable with the given supplier and symbol.
         * The initial value is set to null and will be updated when get(GameState) is called.
         * @param supplier the supplier function to compute the variable's value based on the game state
         * @param type the class type of the variable
         * @param symbol the symbol representing the variable
         */
        public AchievementVariable(GameVariableSupplier<T> supplier, Class<T> type, String symbol) {
            this.supplier = supplier;
            this.type = type;
            this.symbol = symbol;
            this.value = null;
        }
        /**
         * Computes and updates the variable's value based on the given game state.
         * @param s the current game state
         * @return the updated value of the variable
         */
        public T get(GameState s) {
            this.value = supplier.apply(s);
            return value;
        }
        /**
         * Returns the current cached value of the variable.
         * If {@link #get(GameState)} has not been called yet, this will return null.
         * @return the current value of the variable
         */
        public T get() {
            return value;
        }
        /**
         * Updates the variable's value based on the given game state without returning it.
         * @param s the current game state
         */
        public void update(GameState s) {
            this.value = supplier.apply(s);
        }
        /**
         * Returns the symbol representing the variable.
         * @return the symbol of the variable
         */
        public String getSymbol() {
            return symbol;
        }
        /**
         * Returns the type of the variable.
         * @return the class type of the variable
         */
        public Class<T> type() {
            return type;
        }
    }


    public static void load() {
        AchievementJsonSerializer.registerAchievementClass("VarZero", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    private final AchievementVariable<?> variable;
    /**
     * Constructs a VariableAchievement with the given name, description, icon, and variable supplier.
     * @param name the name of the achievement
     * @param description the description of the achievement
     * @param icon the icon representing the achievement
     * @param symbol the symbol representing the variable
     * @param variableSupplier the supplier function to compute the achievement's variable based on the game state
     */
    public VariableAchievement(String name, String description, AchievementIcon icon, String symbol, GameVariableSupplier<Number> variableSupplier) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.variable = new AchievementVariable<Number>(variableSupplier, Number.class, symbol);
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
     * {@inheritDoc}
     * Updates the variable based on the current game state and checks if it is zero.
     * If the variable is null, the achievement is not completed.
     * <p>
     * The variable should be constructed such that it reaches zero when the achievement is completed.
     * For example, if the achievement is a piece being a certain color, the variable could be {@code color - targetColor}.
     * When the piece is the target color, the variable will be zero, indicating the achievement is completed.
     *
     * @param state the input argument
     * @return
     */
    @Override
    public boolean test(GameState state) {
        // Update the variable based on the current game state and check if it is zero
        Object val = variable.get(state);
        if (val instanceof Number) {
            return ((Number) val).intValue() == 0;
        }
        return false;
    }
    /**
     * Returns a string representation of the VariableAchievement.
     * @return a string representation of the VariableAchievement
     */
    @Override
    public String toString() {
        return "VariableAchievement{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon=" + icon +
                ", variable=" + variable.get() +
                '}';
    }
    /**
     * Compares this VariableAchievement to another object for equality.
     * Two VariableAchievements are considered equal if they have the same name, description, icon, and variable.
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariableAchievement other)) return false;
        return this.name.equals(other.name) &&
                this.description.equals(other.description) &&
                this.icon.equals(other.icon) &&
                this.variable.symbol.equals(other.variable.symbol);
    }
    /**
     * Returns the hash code of the VariableAchievement.
     * The hash code is computed based on the name, description, icon, and variable.
     * @return the hash code of the VariableAchievement
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description, icon, variable.symbol) ^ 0x47E82F;
    }
    /**
     * Deserializes a VariableAchievement from a JSON object.
     * @param json the JSON object to deserialize from
     * @return the deserialized VariableAchievement
     * @throws DataSerializationException if the JSON object is invalid or missing required fields
     */
    public static VariableAchievement fromJsonObject(JsonObject json) throws DataSerializationException {
        if (!json.containsKey("name") || !json.containsKey("description") || !json.containsKey("icon") || !json.containsKey("variable")) {
            throw new DataSerializationException("Missing required fields for VariableAchievement.");
        }
        String name = json.getString("name");
        String description = json.getString("description");
        AchievementIcon icon = AchievementIconSerialHelper.deserialize(json);
        String varSymbol = json.getString("variable");
        GameVariableSupplier<Number> supplier;
        try {
            supplier = (GameVariableSupplier<Number>)(GameVariableSupplier.parse(varSymbol));
        } catch (IllegalArgumentException e) {
            throw new DataSerializationException("Invalid variable symbol " + varSymbol + " in variable in VariableAchievement", e);
        } catch (ClassCastException e) {
            throw new DataSerializationException("Variable symbol " + varSymbol + " does not correspond to a Number variable in VariableAchievement", e);
        }
        return new VariableAchievement(name, description, icon, varSymbol, supplier);
    }
    /**
     * Serializes the VariableAchievement to a JSON object builder.
     * @return the JSON object builder representing the VariableAchievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        return Json.createObjectBuilder()
                .add("type", "VarZero")
                .add("name", name)
                .add("description", description)
                .add("icon", AchievementIconSerialHelper.serialize(icon))
                .add("variable", GameVariableSupplier.autoParen(variable.getSymbol()));
        // Auto-paren to preserve precedence, so if parsed by another parser it will still be correct
    }
}
