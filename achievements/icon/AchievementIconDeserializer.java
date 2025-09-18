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

package achievements.icon;

import achievements.DataSerializationException;

import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Utility class for serializing and deserializing AchievementIcon objects.
 * <p>
 * This class provides static methods to convert between AchievementIcon instances
 * and their JSON representations. It handles different types of AchievementIcon,
 * including AchievementEmptyIcon, AchievementTextIcon, and AchievementShapedIcon.
 *
 * @see AchievementIcon
 * @see AchievementEmptyIcon
 * @see AchievementTextIcon
 * @see AchievementShapedIcon
 * @author William Wu
 * @version 2.0
 */
public class AchievementIconDeserializer {
    private static final AchievementEmptyIcon emptyIcon = new AchievementEmptyIcon();
    private AchievementIconDeserializer() {
        // Prevent instantiation
    }
    /**
     * Deserializes a JsonObject into an AchievementIcon.
     * <p>
     * If the JsonObject does not contain the "icon" key, an AchievementEmptyIcon is returned.
     * If the "icon" key exists but is null, an AchievementEmptyIcon is returned.
     * If the "icon" key contains a JsonObject, it is deserialized into an AchievementTextIcon.
     * If the "icon" key contains a JsonArray, it is deserialized into an AchievementShapedIcon.
     * <p>
     * If the "icon" key exists but is of an invalid type, a DataSerializationException is thrown.
     * If the icon data is invalid and therefore cannot be parsed as the type it indicates itself with,
     * a DataSerializationException is thrown with details.
     *
     * @param object The JsonObject to deserialize.
     * @return The deserialized AchievementIcon.
     * @throws IllegalArgumentException   if the input JsonObject is null.
     * @throws DataSerializationException if deserialization fails due to invalid data.
     */
    public static AchievementIcon deserialize(JsonObject object) throws DataSerializationException {
        if (object == null) {
            throw new IllegalArgumentException("Input JsonObject cannot be null.");
        }
        if (!object.containsKey("icon")) {
            return emptyIcon;
        } else {
            if (object.isNull("icon")) {
                return emptyIcon;
            }
            if (object.isEmpty()){
                return emptyIcon;
            }
            try {
                // Attempt to deserialize as Json Object first, turn into AchievementTextIcon
                JsonObject iconObj = object.getJsonObject("icon");
                return AchievementTextIcon.fromJsonObject(iconObj);
            } catch (ClassCastException ignored) {
                // If it fails, continue
            } catch (DataSerializationException e) {
                throw new DataSerializationException("Failed to deserialize AchievementIcon because " + e.getMessage(), e);
            }
            try {
                // Attempt to deserialize as Json Array, turn into AchievementShapedIcon
                return AchievementShapedIcon.fromJsonObject(object);
            } catch (ClassCastException e) {
                // An exception is thrown instead of retuning empty icon because the 'icon' key exists but is of an invalid type
                // This is considered illegal data, while a missing 'icon' key is considered valid and returns an empty icon
                throw new DataSerializationException("Failed to deserialize AchievementIcon: 'icon' is neither a JsonObject nor a JsonArray.", e);
            } catch (DataSerializationException e) {
                throw new DataSerializationException("Failed to deserialize AchievementIcon because " + e.getMessage(), e);
            }
        }
    }
    /**
     * Serializes an AchievementIcon into a JsonValue.
     * <p>
     * If the icon is null or an instance of AchievementEmptyIcon, null is returned.
     * If the icon is an instance of AchievementTextIcon, it is converted to a JsonObject.
     * If the icon is an instance of AchievementShapedIcon, it is converted to a JsonObject.
     * <p>
     * If the icon is of an unknown implementation, an IllegalArgumentException is thrown.
     *
     * @param icon The AchievementIcon to serialize.
     * @return The serialized JsonValue, or null if the icon is empty or null.
     * @throws IllegalArgumentException if the icon is of an unknown implementation.
     */
    public static JsonValue serialize(AchievementIcon icon) {
        return switch (icon) {
            case null -> null;
            case AchievementEmptyIcon achievementEmptyIcon -> null;
            case AchievementTextIcon textIcon -> textIcon.toJsonObjectBuilder().build();
            case AchievementShapedIcon shapedIcon -> shapedIcon.toJsonArrayBuilder().build();
            default ->
                    throw new IllegalArgumentException("Unknown AchievementIcon implementation: " + icon.getClass().getName());
        };
    }
}
