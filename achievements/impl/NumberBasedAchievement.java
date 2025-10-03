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
import achievements.icon.AchievementIcon;
import achievements.icon.AchievementIconSerialHelper;
import hex.GameState;
import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * The {@code NumberBasedAchievement} class represents a game achievement that is based on numerical
 * requirements such as turn number and score. It implements the {@link GameAchievementTemplate} interface
 * and provides functionality to check if the achievement has been achieved based on the current
 * {@link GameState}.
 * <p>
 * This class supports multiple engine sizes, with specific turn and score requirements for each size.
 * The supported engine sizes are defined in the {@link #ENGINE_RADII} array.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed.
 * This ensures that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class also provides methods for serialization and deserialization to and from JSON format,
 * allowing for easy storage and retrieval of achievement data.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @see JsonConvertible
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class NumberBasedAchievement implements GameAchievementTemplate, JsonConvertible {
    /**
     * The supported engine sizes for the game. Each size corresponds to specific turn and score requirements.
     * The sizes are represented by their radii in the game engine.
     * @value 5, 8, 11
     */
    public static final int[] ENGINE_RADII = {5, 8, 11}; // Engine sizes supported by the game
    private final int[] turnRequirement;
    private final int[] scoreRequirement;
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    public static void load() {
        AchievementJsonSerializer.registerAchievementClass("NumberBasedAchievement", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }

    /**
     * Creates a new NumberBasedAchievement with the specified requirements, name, and description.
     * The requirements are applied uniformly across all engine sizes defined in ENGINE_RADII.
     * @param turnRequirement the turn requirement for the achievement
     * @param scoreRequirement the score requirement for the achievement
     * @param name the name of the achievement
     * @param description the description of the achievement
     */
    public NumberBasedAchievement(int turnRequirement, int scoreRequirement, String name, String description, AchievementIcon icon) {
        int l = ENGINE_RADII.length;
        this.turnRequirement = new int[l];
        this.scoreRequirement = new int[l];
        for (int i = 0; i < l; i++) {
            this.turnRequirement[i] = turnRequirement;
            this.scoreRequirement[i] = scoreRequirement;
        }
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    /**
     * Creates a new NumberBasedAchievement with the specified requirements, name, and description.
     * The length of the requirement arrays must match the length of ENGINE_RADII.
     * @param turnRequirement the turn requirement for the achievement
     * @param scoreRequirement the score requirement for the achievement
     * @param name the name of the achievement
     * @param description the description of the achievement
     * @throws IllegalArgumentException if the lengths of the requirement arrays do not match ENGINE_RADII
     */
    public NumberBasedAchievement(int[] turnRequirement, int[] scoreRequirement, String name, String description, AchievementIcon icon) {
        if (turnRequirement.length != ENGINE_RADII.length || scoreRequirement.length != ENGINE_RADII.length) {
            throw new IllegalArgumentException("Requirement arrays must match the length of ENGINE_RADII");
        }
        this.turnRequirement = turnRequirement.clone();
        this.scoreRequirement = scoreRequirement.clone();
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

    /**
     * {@inheritDoc}
     * Test whether the achievement has been achieved based on the provided game state.
     * The achievement is considered achieved if the current turn and score meet or exceed the requirements
     * for the engine radius of the current game state.
     * @param state the input current game state
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     * @throws IllegalArgumentException if the engine radius of the game state is not supported
     */
    public boolean test(GameState state) {
        int radius = state.getEngine().getRadius();
        int index = -1;
        for (int i = 0; i < ENGINE_RADII.length; i++) {
            if (ENGINE_RADII[i] == radius) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("Unsupported engine radius: " + radius);
        }
        return state.getTurn() >= turnRequirement[index] && state.getScore() >= scoreRequirement[index];
    }
    /**
     * Returns a string representation of the achievement, including its name, description, and requirements.
     * @return a string representation of the achievement
     */
    public String toString(){
        return "Achievement[" + name + ": " + description + "\n Requirements: Require turn exceeding " + java.util.Arrays.toString(turnRequirement)
                + ", and score exceeding " + java.util.Arrays.toString(scoreRequirement) + " for game engine size" + java.util.Arrays.toString(ENGINE_RADII) + "]";
    }
    /**
     * {@inheritDoc}
     * Compares this achievement to another object for equality.
     * Two achievements are considered equal if they have the same name, description,
     * turn requirements, and score requirements.
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof NumberBasedAchievement other)) return false;
        return name.equals(other.name)
                && java.util.Arrays.equals(turnRequirement, other.turnRequirement)
                && java.util.Arrays.equals(scoreRequirement, other.scoreRequirement);
    }
    /**
     * {@inheritDoc}
     * Returns a hash code value for the achievement.
     * The hash code is computed based on the name, description, turn requirements, and score requirements.
     * @return a hash code value for the achievement
     */
    public int hashCode(){
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + java.util.Arrays.hashCode(turnRequirement);
        result = 31 * result + java.util.Arrays.hashCode(scoreRequirement);
        return result;
    }
    /**
     * {@inheritDoc}
     * Converts the achievement to a JSON object builder.
     * @return a {@link JsonObjectBuilder} representing the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "NumberBasedAchievement");
        builder.add("name", name());
        builder.add("description", description());
        builder.add("icon", AchievementIconSerialHelper.serialize(icon));
        builder.add("turnRequirement", Json.createArrayBuilder(java.util.Arrays.stream(turnRequirement).boxed().toList()));
        builder.add("scoreRequirement", Json.createArrayBuilder(java.util.Arrays.stream(scoreRequirement).boxed().toList()));
        return builder;
    }
    /**
     * Creates a NumberBasedAchievement from a JSON object.
     * The JSON object must contain the fields "name", "description", "turnRequirement", and "scoreRequirement".
     * @param obj the JSON object representing the achievement
     * @return a NumberBasedAchievement instance
     * @throws DataSerializationException if the JSON object is invalid or missing required fields
     */
    public static NumberBasedAchievement fromJsonObject(JsonObject obj) throws DataSerializationException {
        try {
            String name = obj.getString("name");
            String description = obj.getString("description");
            JsonArray turnArray = obj.getJsonArray("turnRequirement");
            JsonArray scoreArray = obj.getJsonArray("scoreRequirement");
            AchievementIcon icon = AchievementIconSerialHelper.deserialize(obj);
            int[] turnRequirement = turnArray.stream().mapToInt(v -> ((javax.json.JsonNumber) v).intValue()).toArray();
            int[] scoreRequirement = scoreArray.stream().mapToInt(v -> ((javax.json.JsonNumber) v).intValue()).toArray();
            return new NumberBasedAchievement(turnRequirement, scoreRequirement, name, description, icon);
        } catch (Exception e){
            throw new DataSerializationException("Invalid JSON object for NumberBasedAchievement", e);
        }
    }
}
