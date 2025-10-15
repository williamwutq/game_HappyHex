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

import GUI.GameEssentials;
import achievements.AchievementJsonSerializer;
import achievements.DataSerializationException;
import achievements.GameAchievementTemplate;
import achievements.icon.AchievementIcon;
import achievements.icon.AchievementIconSerialHelper;
import hex.GameState;
import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Objects;

/**
 * The {@code EliminationAchievement} class represents a game achievement that is achieved when the player
 * eliminates a specified minimum number of blocks, lines, and directions in a single move. It implements the
 * {@link GameAchievementTemplate} interface and provides functionality to check if the achievement has been
 * achieved based on the current {@link GameState}.
 * <p>
 * This class checks if the player has eliminated at least the required minimum number of blocks, lines, and
 * directions in a single move. The achievement is considered achieved if all these conditions are met.
 * This class is dependent on the {@link GameEssentials} class to retrieve the current elimination counts.
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
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class EliminationAchievement implements GameAchievementTemplate, JsonConvertible {
    private final int requiredMinEliminationBlock;
    private final int requiredMinEliminationLine;
    private final int requiredMinEliminationDirection;
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    public static void load()  {
        AchievementJsonSerializer.registerAchievementClass("EliminationAchievement", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
    /**
     * Creates a new EliminationAchievement with the specified requirements.
     * The achievement is achieved if the player eliminates at least the specified number of blocks,
     * lines, and directions in a single move.
     * @param requiredMinEliminationBlock the minimum number of blocks that must be eliminated
     * @param requiredMinEliminationLine the minimum number of lines that must be eliminated
     * @param requiredMinEliminationDirection the minimum number of directions that must be eliminated
     * @param name the name of the achievement
     * @param description the description of the achievement
     */
    public EliminationAchievement(int requiredMinEliminationBlock, int requiredMinEliminationLine, int requiredMinEliminationDirection, String name, String description, AchievementIcon icon){
        this.requiredMinEliminationBlock = requiredMinEliminationBlock;
        this.requiredMinEliminationLine = requiredMinEliminationLine;
        this.requiredMinEliminationDirection = requiredMinEliminationDirection;
        this.name = name;
        this.description = description;
        this.icon = icon;
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
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof EliminationAchievement other)) return false;
        return this.requiredMinEliminationBlock == other.requiredMinEliminationBlock &&
                this.requiredMinEliminationLine == other.requiredMinEliminationLine &&
                this.requiredMinEliminationDirection == other.requiredMinEliminationDirection &&
                this.name.equals(other.name) &&
                this.description.equals(other.description);
    }
    /**
     * {@inheritDoc}
     * @return the hash code of the achievement
     */
    @Override
    public int hashCode(){
        return Objects.hash(requiredMinEliminationBlock, requiredMinEliminationLine, requiredMinEliminationDirection, name, description);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method checks if the achievement has been achieved. It is dependent on the {@link GameEssentials} class
     * to retrieve the current elimination counts. The achievement is considered achieved if the player has eliminated
     * at least the required minimum number of blocks, lines, and directions in a single move. This make this template
     * a dependent template.
     * @param state the input argument
     * @return
     */
    @Override
    public boolean test(GameState state) {
        return GameEssentials.getEliminateBlockCount() >= requiredMinEliminationBlock &&
                GameEssentials.getEliminateLineCount() >= requiredMinEliminationLine &&
                GameEssentials.getEliminateDirectionCount() >= requiredMinEliminationDirection;
    }
    /**
     * {@inheritDoc}
     * <p>
     * This method converts the achievement to a JSON object builder. It includes all the fields of the achievement.
     * @return a JsonObjectBuilder representing the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        return Json.createObjectBuilder()
                .add("type", "EliminationAchievement")
                .add("requiredMinEliminationBlock", requiredMinEliminationBlock)
                .add("requiredMinEliminationLine", requiredMinEliminationLine)
                .add("requiredMinEliminationDirection", requiredMinEliminationDirection)
                .add("icon", AchievementIconSerialHelper.serialize(icon))
                .add("name", name)
                .add("description", description);
    }
    /**
     * Deserializes a JSON object to create an instance of EliminationAchievement.
     * @param jsonObject the JSON object to deserialize
     * @return an instance of EliminationAchievement
     * @throws DataSerializationException if the JSON data is invalid or missing required fields
     */
    public static EliminationAchievement fromJsonObject(JsonObject jsonObject) throws DataSerializationException {
        try {
            int requiredMinEliminationBlock = jsonObject.getInt("requiredMinEliminationBlock");
            int requiredMinEliminationLine = jsonObject.getInt("requiredMinEliminationLine");
            int requiredMinEliminationDirection = jsonObject.getInt("requiredMinEliminationDirection");
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");
            AchievementIcon icon = AchievementIconSerialHelper.deserialize(jsonObject);
            return new EliminationAchievement(requiredMinEliminationBlock, requiredMinEliminationLine, requiredMinEliminationDirection, name, description, icon);
        } catch (NullPointerException | ClassCastException e) {
            throw new DataSerializationException("Invalid JSON data for EliminationAchievement", e);
        }
    }
}
