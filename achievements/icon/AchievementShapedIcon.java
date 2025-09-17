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
import io.JsonConvertible;
import util.geom.CurvedShape;
import util.tuple.Pair;

import javax.json.*;
import java.awt.*;
import java.util.Iterator;

/**
 * Represents an achievement icon composed of multiple colored shapes.
 * Each shape is represented by a {@link CurvedShape} and has an associated {@link Color}.
 * The icon can be converted to and from a JSON representation for serialization purposes.
 * <p>
 * This class implements the {@link AchievementIcon} interface, providing the necessary methods
 * to retrieve the icon parts and convert them to JSON format.
 *
 * @see AchievementIcon
 * @see CurvedShape
 * @see Color
 * @see JsonConvertible
 * @author William Wu
 * @version 2.0
 */
public class AchievementShapedIcon implements AchievementIcon, JsonConvertible {
    private final CurvedShape[] iconShapes;
    private final Color[] iconColors;
    /**
     * Constructs an AchievementShapedIcon with specified shapes and colors.
     *
     * @param iconShapes An array of CurvedShape objects representing the shapes of the icon.
     * @param iconColors An array of Color objects representing the colors of the icon.
     *                   The length of iconColors must match the length of iconShapes.
     * @throws IllegalArgumentException if iconShapes or iconColors is null, or if their lengths do not match.
     */
    public AchievementShapedIcon(CurvedShape[] iconShapes, Color[] iconColors) {
        if (iconShapes == null || iconColors == null) {
            throw new IllegalArgumentException("Icon shapes and colors cannot be null.");
        }
        if (iconShapes.length != iconColors.length) {
            throw new IllegalArgumentException("Icon shapes and colors arrays must have the same length.");
        }
        this.iconShapes = iconShapes;
        this.iconColors = iconColors;
    }

    /**
     * {@inheritDoc}
     * Returns an iterable of pairs of colors and shapes that make up the icon.
     * Each pair consists of a Color and a Shape, representing a part of the icon.
     *
     * @return An iterable of pairs where each pair contains a Color and a Shape.
     */
    @Override
    public Iterable<Pair<Color, Shape>> normalizedParts() {
        return () -> new Iterator<Pair<Color, Shape>>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < iconShapes.length;
            }
            @Override
            public Pair<Color, Shape> next() {
                Pair<Color, Shape> part = new Pair<>(iconColors[index], iconShapes[index].toShape());
                index++;
                return part;
            }
        };
    }
    /**
     * {@inheritDoc}
     * Converts the icon to a JSON object builder.
     * The JSON object contains an array of parts, where each part has a color and a shape.
     *
     * @return A JsonObjectBuilder representing the icon.
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonArrayBuilder shapesArray = Json.createArrayBuilder();
        JsonObjectBuilder rootObj = Json.createObjectBuilder();
        for (int i = 0; i < iconShapes.length; i++) {
            JsonObjectBuilder shapeObj = Json.createObjectBuilder();
            shapeObj.add("color", JsonColorConverter.colorToJsonArray(iconColors[i]));
            shapeObj.add("shape", iconShapes[i].toJsonArrayBuilder());
            shapesArray.add(shapeObj);
        }
        rootObj.add("icon", shapesArray);
        return rootObj;
    }
    /**
     * Constructs an AchievementShapedIcon from a JSON object.
     * The JSON object must contain an array of parts, where each part has a color and a shape.
     *
     * @param obj The JsonObject to convert.
     * @return An AchievementShapedIcon constructed from the JSON data.
     * @throws DataSerializationException if the JSON object is malformed or missing required data.
     */
    public static AchievementShapedIcon fromJsonObject(JsonObject obj) throws DataSerializationException {
        if (!obj.containsKey("icon")) {
            throw new DataSerializationException("JSON object must contain key 'icon'");
        }
        JsonArray shapesArray = obj.getJsonArray("icon");
        CurvedShape[] shapes = new CurvedShape[shapesArray.size()];
        Color[] colors = new Color[shapesArray.size()];
        for (int i = 0; i < shapesArray.size(); i++) {
            JsonObject shapeObj = shapesArray.getJsonObject(i);
            if (!shapeObj.containsKey("color") || !shapeObj.containsKey("shape")) {
                throw new DataSerializationException("Missing 'color' or 'shape' key in shape object at index " + i);
            }
            JsonArray colorArray = shapeObj.getJsonArray("color");
            JsonArray shapeArray = shapeObj.getJsonArray("shape");
            try {
                colors[i] = JsonColorConverter.jsonArrayToColor(colorArray);
                shapes[i] = CurvedShape.fromJsonArray(shapeArray);
            } catch (IllegalArgumentException e) {
                throw new DataSerializationException("Invalid color or shape data at index " + i, e);
            }
        }
        return new AchievementShapedIcon(shapes, colors);
    }
}
