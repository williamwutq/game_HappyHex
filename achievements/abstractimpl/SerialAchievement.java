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

package achievements.abstractimpl;

import achievements.*;
import achievements.icon.AchievementIcon;
import achievements.icon.AchievementIconSerialHelper;
import achievements.icon.AchievementTextIcon;
import hex.GameState;
import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * The {@code SerialAchievement} class represents a game achievement that is based on a series of
 * other achievements that must be achieved in order. It implements the {@link GameAchievementTemplate}
 * interface and provides functionality to check if the achievement has been achieved based on the current
 * {@link GameState}.
 * <p>
 * This class allows for specifying an array of required achievements.
 * The achievement is considered achieved if all required achievements have been achieved by the player.
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
public class SerialAchievement implements GameAchievementTemplate, JsonConvertible {
    private final GameAchievementTemplate[] requirements;
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    public static void load()  {
        AchievementJsonSerializer.registerAchievementClass("serial", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
    /**
     * Creates a new SerialAchievement with the specified requirements, name, and description.
     * The achievement is achieved if the player has achieved all the specified achievements in the given order.
     * <p>
     * It is assumed that there are no duplicates in the requirements array. If there are duplicates,
     * the achievement will still function correctly, but it may be less efficient.
     * <p>
     * It is assumed that there are no circular dependencies in the requirements array. If there are circular dependencies,
     * the achievement will never be achieved.
     * @throws IllegalArgumentException if the requirements array is empty, or if the name or description is null or blank
     * @param requirements the achievements that need to be achieved in order
     * @param name the name of the achievement
     * @param description the description of the achievement
     */
    public SerialAchievement(GameAchievementTemplate[] requirements, String name, String description, AchievementIcon icon) {
        if (requirements.length == 0) {
            throw new IllegalArgumentException("Requirements cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        this.icon = icon == null ? new AchievementTextIcon("LINK", Color.WHITE) : icon;
        this.requirements = requirements.clone();
        this.name = name;
        this.description = description;
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
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SerialAchievement other)) return false;
        if (!name.equals(other.name) || !description.equals(other.description)) return false;
        if (requirements.length != other.requirements.length) return false;
        for (int i = 0; i < requirements.length; i++) {
            if (!requirements[i].equals(other.requirements[i])) return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     * @return the hash code of the achievement
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(requirements), name, description);
    }
    /**
     * {@inheritDoc}
     * <p>
     * Tests whether the achievement has been achieved based on the provided game state.
     * The achievement is considered achieved if all the required achievements have been achieved.
     * <p>
     * This method relies on the current user management by the {@link GameAchievement} class, which
     * should be automatic. However, this make this template a dependent template.
     * @param state the current game state
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     */
    @Override
    public boolean test(GameState state) {
        // Get achievements of the user, which should be a snapshot
        UserAchievements achievements;
        try {
            achievements = GameAchievement.getActiveUserAchievements().get();
        } catch (InterruptedException | ExecutionException e) {
            return false; // ExecutionException is never thrown, but if interrupted, return false
        }
        // Convert achieved templates into a Set for O(1) lookups
        Set<GameAchievementTemplate> achievedTemplates = achievements.getAchievements()
                .stream()
                .map(GameAchievement::getTemplate)
                .collect(Collectors.toSet());
        // Check if all requirements are satisfied
        for (GameAchievementTemplate requirement : requirements) {
            if (!achievedTemplates.contains(requirement)) {
                return false;
            }
        }
        return true; // All requirements met
    }

    /**
     * Converts this SerialAchievement to a JsonObjectBuilder.
     * The JSON representation includes the type, name, description, and requirements.
     * @return a JsonObjectBuilder representing this SerialAchievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "serial");
        builder.add("name", name);
        builder.add("icon", AchievementIconSerialHelper.serialize(icon));
        builder.add("description", description);
        // Add requirements as an array of JSON objects
        var reqArrayBuilder = Json.createArrayBuilder();
        for (GameAchievementTemplate req : requirements) {
            reqArrayBuilder.add(req.name()); // This is possible because names are unique
        }
        builder.add("requirements", reqArrayBuilder);
        return builder;
    }
    /**
     * Creates a SerialAchievement from a JsonObject.
     * The JSON object must contain the type, name, description, and requirements.
     * @param jsonObject the JsonObject to convert
     * @return a SerialAchievement represented by the JsonObject
     * @throws DataSerializationException if the JSON object is invalid or missing required fields
     */
    public static SerialAchievement fromJsonObject(javax.json.JsonObject jsonObject) throws DataSerializationException {
        if (!jsonObject.containsKey("type") || !jsonObject.getString("type").equals("serial")) {
            throw new DataSerializationException("Invalid JSON object for SerialAchievement");
        }
        if (!jsonObject.containsKey("name") || !jsonObject.containsKey("description") || !jsonObject.containsKey("requirements")) {
            throw new DataSerializationException("Missing required fields in JSON object for SerialAchievement");
        }
        String name = jsonObject.getString("name");
        String description = jsonObject.getString("description");
        AchievementIcon icon = AchievementIconSerialHelper.deserialize(jsonObject);
        JsonArray reqArray = jsonObject.getJsonArray("requirements");
        GameAchievementTemplate[] requirements = new GameAchievementTemplate[reqArray.size()];
        Map<String, GameAchievementTemplate> templateMap = GameAchievement.getTemplates().stream()
                .collect(Collectors.toMap(GameAchievementTemplate::name, t -> t));
        for (int i = 0; i < reqArray.size(); i++) {
            String reqName = reqArray.getString(i);
            if (!templateMap.containsKey(reqName)) {
                throw new DataSerializationException("Unknown requirement: " + reqName + ", templates may not have been loaded; " +
                        "If this is the initialization, serialize independent templates first");
            }
            requirements[i] = templateMap.get(reqName);
        }
        return new SerialAchievement(requirements, name, description, icon);
    }
}
