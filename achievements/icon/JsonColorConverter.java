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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.awt.*;

/**
 * Utility class for converting Color objects to and from JSON representations.
 * Provides the following methods:
 * <ul>
 *   <li>{@link #colorToJsonArray(Color color)}: Converts a Color object to a JsonArray with RGBA components.</li>
 *   <li>{@link #jsonArrayToColor(JsonArray array)}: Converts a JsonArray with RGBA components to a Color object.</li>
 *   <li>{@link #colorToJsonObject(Color color)}: Converts a Color object to a JsonObject with RGBA components.</li>
 *   <li>{@link #jsonObjectToColor(JsonObject obj)}: Converts a JsonObject with RGBA components to a Color object.</li>
 *   <li>{@link #colorToString(Color color)}: Converts a Color object to a string in the format #RRGGBBAA.</li>
 *   <li>{@link #stringToColor(String str)}: Converts a string in the format #RRGGBB or #RRGGBBAA to a Color object.</li>
 *   <li>{@link #deserializeColor(Object obj)}: Deserializes a Color from a JsonArray, JsonObject, or String representation.</li>
 *   <li>{@link #deserializeColorField(JsonObject obj, String key)}: Deserializes a Color from a field in a JsonObject that can be a JsonArray, JsonObject, or String.</li>
 * </ul>
 * <p>
 * All color representations contain four components: red, green, blue, and alpha (opacity).
 * Each component is an integer in the range [0, 255].
 * <p>
 * It is recommended to use the JsonArray representation for compactness, when possible.
 * <p>
 * This class is not instantiable and only provides static utility methods.
 * @author William Wu
 * @version 2.0
 */
public class JsonColorConverter {
    private JsonColorConverter() {}
    /**
     * Converts the icon to a JSON object builder.
     * The JSON object contains an array of parts, where each part has a color and a shape.
     *
     * @return A JsonObjectBuilder representing the icon.
     */
    public static JsonArray colorToJsonArray(Color color){
        return Json.createArrayBuilder().add(color.getRed()).add(color.getGreen()).add(color.getBlue()).add(color.getAlpha()).build();
    }
    /**
     * Converts a JSON array to a Color object.
     * The JSON array must have exactly 4 elements representing the RGBA components of the color.
     *
     * @param array The JsonArray to convert.
     * @return A Color object representing the color.
     * @throws IllegalArgumentException if the array does not have exactly 4 elements, or if the elements are not valid integers in the range [0, 255].
     */
    public static Color jsonArrayToColor(JsonArray array){
        if (array.size() != 4) throw new IllegalArgumentException("Color array must have exactly 4 elements (r, g, b, a)");
        return new Color(array.getInt(0), array.getInt(1), array.getInt(2), array.getInt(3));
    }
    /**
     * Converts a Color object to a JSON object.
     * The JSON object contains the RGBA components of the color.
     *
     * @param color The Color object to convert.
     * @return A JsonObject representing the color.
     */
    public static JsonObject colorToJsonObject(Color color){
        return Json.createObjectBuilder()
                .add("r", color.getRed())
                .add("g", color.getGreen())
                .add("b", color.getBlue())
                .add("a", color.getAlpha())
                .build();
    }
    /**
     * Converts a JSON object to a Color object.
     * The JSON object must contain the keys "r", "g", "b", and "a" representing the RGBA components of the color.
     *
     * @param obj The JsonObject to convert.
     * @return A Color object representing the color.
     * @throws IllegalArgumentException if any of the required keys are missing, or if the values are not valid integers in the range [0, 255].
     */
    public static Color jsonObjectToColor(JsonObject obj){
        if (!obj.containsKey("r") || !obj.containsKey("g") || !obj.containsKey("b") || !obj.containsKey("a"))
            throw new IllegalArgumentException("Color object must contain keys 'r', 'g', 'b', and 'a'");
        return new Color(obj.getInt("r"), obj.getInt("g"), obj.getInt("b"), obj.getInt("a"));
    }
    /**
     * Converts a Color object to a string representation in the format #RRGGBBAA.
     * Each component (red, green, blue, alpha) is represented as a two-digit hexadecimal number.
     *
     * @param color The Color object to convert.
     * @return A string representing the color in #RRGGBBAA format.
     */
    public static String colorToString(Color color){
        // In #RRGGBBAA format
        return String.format("#%02X%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    /**
     * Converts a string representation of a color in the format #RRGGBB or #RRGGBBAA to a Color object.
     * The string must start with '#' followed by 6 or 8 hexadecimal digits.
     * If 6 digits are provided, the alpha component is assumed to be 255 (fully opaque).
     *
     * @param str The string to convert.
     * @return A Color object representing the color.
     * @throws IllegalArgumentException if the string is not in the correct format.
     */
    public static Color stringToColor(String str){
        if (!str.startsWith("#") || (str.length() != 7 && str.length() != 9))
            throw new IllegalArgumentException("Color string must be in format #RRGGBB or #RRGGBBAA");
        int r = Integer.parseInt(str.substring(1, 3), 16);
        int g = Integer.parseInt(str.substring(3, 5), 16);
        int b = Integer.parseInt(str.substring(5, 7), 16);
        int a = (str.length() == 9) ? Integer.parseInt(str.substring(7, 9), 16) : 255;
        return new Color(r, g, b, a);
    }
    /**
     * Deserializes a color from various representations: JsonArray, JsonObject, or String.
     * The method automatically detects the type of the input object and converts it to a Color.
     *
     * @param obj The object to deserialize (JsonArray, JsonObject, or String).
     * @return A Color object representing the color.
     * @throws IllegalArgumentException if the input object is of an unsupported type or contains invalid data.
     */
    public static Color deserializeColor(Object obj) throws IllegalArgumentException {
        return switch (obj) {
            case JsonArray jsonArray -> jsonArrayToColor(jsonArray);
            case JsonObject jsonObject -> jsonObjectToColor(jsonObject);
            case String str -> stringToColor(str);
            case null, default -> throw new IllegalArgumentException("Unsupported color representation");
        };
    }
    /**
     * Deserializes a color from a JSON object field that can be represented as a JsonArray, JsonObject, or String.
     * The method checks the type of the value associated with the specified key and converts it to a Color.
     *
     * @param obj The JsonObject containing the color field.
     * @param key The key of the color field in the JsonObject.
     * @return A Color object representing the color.
     * @throws IllegalArgumentException if the key is missing or if the value is of an unsupported type or contains invalid data.
     */
    public static Color deserializeColorField(JsonObject obj, String key) throws IllegalArgumentException {
        if (!obj.containsKey(key)) throw new IllegalArgumentException("JSON object does not contain key '" + key + "'");
        // Explicitly try to get as each type
        try {
            return jsonArrayToColor(obj.getJsonArray(key));
        } catch (ClassCastException | IllegalArgumentException ignored) {}
        try {
            return jsonObjectToColor(obj.getJsonObject(key));
        } catch (ClassCastException | IllegalArgumentException ignored) {}
        try {
            return stringToColor(obj.getString(key));
        } catch (ClassCastException | IllegalArgumentException ignored) {}
        throw new IllegalArgumentException("Unsupported color representation for key '" + key + "'");
    }
}
