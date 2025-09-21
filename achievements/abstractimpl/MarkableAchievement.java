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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code MarkableAchievement} class represents a game achievement that can be manually marked as achieved.
 * It implements the {@link GameAchievementTemplate} interface and provides functionality to check if the achievement
 * has been achieved based on an internal flag that can be set by calling the {@link #mark()} method, or reset by calling
 * the {@link #reset()} method. To access an instance of this class, use the static {@link #mark(String)}
 * and {@link #reset(String)} methods with the unique name of the achievement. {@link #resetAll()} can be used to
 * reset all registered achievements.
 * <p>
 * This class allows for dynamic marking of achievements, making it suitable for scenarios where achievements
 * are not tied to specific game state conditions but rather to external events or actions taken by the player.
 * <p>
 * The class is designed to be thread-safe, allowing multiple threads to safely mark the achievement as achieved
 * without causing inconsistent states. The internal flag is managed using an {@link AtomicBoolean}. This allows this
 * achievement to be triggered outside the Achievement Update Thread (AUT), such as in response to immediate user inputs,
 * network events, or other asynchronous actions, then synced back onto the achievement system whenever the {@code AUT}
 * is available to process it. For example, this can be run on AWT's Event Dispatch Thread (EDT) to respond to GUI events.
 * <p>
 * Instances of this class are identified by a unique name and description. The name must be unique across all
 * instances of {@code MarkableAchievement} to prevent conflicts. Attempting to create an achievement with a
 * duplicate name will result in an {@link IllegalArgumentException}.
 * <p>
 * The class provides methods for JSON serialization and deserialization, allowing achievements to be easily
 * saved and loaded as part of a game's data management system.
 * <p>
 * Note: It is important to call the {@link #unload()} method when the achievement is no longer needed to prevent
 * memory leaks, as instances are stored in a static registry.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @see JsonConvertible
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class MarkableAchievement implements GameAchievementTemplate, JsonConvertible {
    private static final Map<String, MarkableAchievement> globalRegistry = new java.util.HashMap<>();
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    private final AtomicBoolean flag;
    public static void load()  {
        AchievementJsonSerializer.registerAchievementClass("markable", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException("Failed to deserialize markable.", e);
            }
        });
    }
    /**
     * Constructs a new {@code MarkableAchievement} with the specified name, description, and icon.
     * The name must be unique among all instances of {@code MarkableAchievement}. If an achievement
     * with the same name already exists, an {@link IllegalArgumentException} is thrown.
     * <p>
     * The achievement is initially not marked as achieved. It can be marked as achieved by calling
     * the {@link #mark()} method.
     * <p>
     * This constructor registers the achievement in a global registry to ensure uniqueness of names.
     * It is very important to call the {@link #unload()} method when the achievement is no longer needed
     * to prevent memory leaks.
     *
     * @param name        the unique name of the achievement
     * @param description a brief description of the achievement
     * @throws IllegalArgumentException if an achievement with the same name already exists
     */
    public MarkableAchievement(String name, String description, AchievementIcon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.flag = new AtomicBoolean(false);
        synchronized (globalRegistry) {
            if (globalRegistry.containsKey(name)) {
                throw new IllegalArgumentException("An achievement with the name '" + name + "' already exists.");
            }
            globalRegistry.put(name, this);
        }
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
     * @return the hash code of the achievement
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description) + 0xC59BB2; // Offset to reduce collision with other classes since only name and description are used
    }
    /**
     * {@inheritDoc}
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return name.equals(((MarkableAchievement) obj).name); // This is possible because the constructor ensures unique names
    }
    /**
     * {@inheritDoc}
     * <p>
     * Tests whether the achievement has been achieved. This implementation checks the internal flag
     * that indicates whether the achievement has been marked as achieved.
     * @param state the current game state (not used in this implementation)
     * @return {@code true} if the achievement has been marked as achieved, {@code false} otherwise
     */
    @Override
    public boolean test(GameState state) {
        return flag.get();
    }
    /**
     * Marks the achievement as achieved by setting the internal flag to {@code true}.
     * This method is thread-safe and can be called from multiple threads without causing
     * inconsistent states.
     */
    public void mark() {
        flag.set(true);
    }
    /**
     * Resets the achievement to not achieved state by setting the internal flag to {@code false}.
     * This method is thread-safe and can be called from multiple threads without causing
     * inconsistent states.
     */
    public void reset() {
        flag.set(false);
    }
    /**
     * Marks the achievement with the specified name as achieved.
     * This static method looks up the achievement in the global registry and calls its
     * {@link #mark()} method to set it as achieved.
     * <p>
     * Note: This method is synchronized to ensure thread safety when accessing the global registry.
     * @param name the name of the achievement to mark as achieved
     * @throws IllegalArgumentException if no achievement with the specified name exists
     */
    public static void mark(String name) {
        synchronized (globalRegistry) {
            MarkableAchievement achievement = globalRegistry.get(name);
            if (achievement != null) {
                achievement.mark();
            } else {
                throw new IllegalArgumentException("No achievement found with the name '" + name + "'.");
            }
        }
    }
    /**
     * Resets the achievement with the specified name to not achieved state.
     * This static method looks up the achievement in the global registry and calls its
     * {@link #reset()} method to set it as not achieved.
     * <p>
     * Note: This method is synchronized to ensure thread safety when accessing the global registry.
     * @param name the name of the achievement to reset
     * @throws IllegalArgumentException if no achievement with the specified name exists
     */
    public static void reset(String name) {
        synchronized (globalRegistry) {
            MarkableAchievement achievement = globalRegistry.get(name);
            if (achievement != null) {
                achievement.reset();
            } else {
                throw new IllegalArgumentException("No achievement found with the name '" + name + "'.");
            }
        }
    }
    /**
     * Marks the achievement with the specified name as achieved if it exists.
     * This static method looks up the achievement in the global registry and calls its
     * {@link #mark()} method to set it as achieved if found.
     * <p>
     * Note: This method is synchronized to ensure thread safety when accessing the global registry.
     * @param name the name of the achievement to mark as achieved
     */
    public static void markIfExists(String name){
        synchronized (globalRegistry) {
            MarkableAchievement achievement = globalRegistry.get(name);
            if (achievement != null) {
                achievement.mark();
            }
        }
    }
    /**
     * Resets the achievement with the specified name to not achieved state if it exists.
     * This static method looks up the achievement in the global registry and calls its
     * {@link #reset()} method to set it as not achieved if found.
     * <p>
     * Note: This method is synchronized to ensure thread safety when accessing the global registry.
     * @param name the name of the achievement to reset
     */
    public static void resetIfExists(String name){
        synchronized (globalRegistry) {
            MarkableAchievement achievement = globalRegistry.get(name);
            if (achievement != null) {
                achievement.reset();
            }
        }
    }
    /**
     * Resets all registered achievements to not achieved state.
     * This static method iterates through all achievements in the global registry
     * and calls their {@link #reset()} method to set them as not achieved.
     * <p>
     * Note: This method is synchronized to ensure thread safety when accessing the global registry.
     */
    public static void resetAll() {
        synchronized (globalRegistry) {
            for (MarkableAchievement achievement : globalRegistry.values()) {
                achievement.reset();
            }
        }
    }

    /**
     * Unloads the achievement from the global registry.
     * This method removes the achievement from the static map that tracks all instances.
     * It is useful for cleaning up resources when the achievement is no longer needed.
     * <p>
     * Note: This method is synchronized to ensure thread safety when modifying the global registry.
     * It is very important to call this method when the achievement is no longer needed to prevent
     * memory leaks.
     */
    public void unload() {
        synchronized (globalRegistry) {
            globalRegistry.remove(name);
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * Converts the achievement to a JSON object builder. This implementation includes the name
     * and description of the achievement in the JSON representation.
     * @return a {@link JsonObjectBuilder} representing the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("name", name);
        builder.add("description", description);
        builder.add("icon", AchievementIconSerialHelper.serialize(icon));
        return builder;
    }
    /**
     * Deserializes a {@link MarkableAchievement} from a JSON object.
     * <p>
     * The JSON object must contain the "name" and "description" fields. If an achievement
     * with the same name already exists in the global registry, it will be returned instead
     * of creating a new instance.
     * <p>
     * Note: This method is synchronized to ensure thread safety when accessing the global registry.
     * @param jsonObject the JSON object to deserialize from
     * @return a {@link MarkableAchievement} instance
     * @throws DataSerializationException if the JSON object is invalid or missing required fields
     */
    public static MarkableAchievement fromJsonObject(JsonObject jsonObject) throws DataSerializationException {
        if (!jsonObject.containsKey("name") || !jsonObject.containsKey("description")) {
            throw new DataSerializationException("Invalid JSON object for MarkableAchievement: missing required fields.");
        }
        String name = jsonObject.getString("name");
        String description = jsonObject.getString("description");
        AchievementIcon icon = AchievementIconSerialHelper.deserialize(jsonObject);
        synchronized (globalRegistry) {
            if (globalRegistry.containsKey(name)) {
                return globalRegistry.get(name);
            } else {
                return new MarkableAchievement(name, description, icon); // Constructor handles registration
            }
        }
    }
}
