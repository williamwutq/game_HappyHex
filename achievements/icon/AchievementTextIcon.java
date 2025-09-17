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
import util.tuple.Pair;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class AchievementTextIcon implements AchievementIcon, JsonConvertible {
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 1);
    private final String text;
    private final Color color;
    /**
     * Constructs an AchievementTextIcon with specified text and color.
     *
     * @param text  The text to be displayed in the icon.
     * @param color The color of the text.
     * @throws IllegalArgumentException if text is null or empty, or if color is null.
     */
    public AchievementTextIcon(String text, Color color) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null.");
        }
        this.text = text;
        this.color = color;
    }
    /**
     * {@inheritDoc}
     * Returns an iterable of a single pair containing the color and shape of the text.
     * The shape is generated using the default font and the provided text.
     *
     * @return An iterable of a single pair where the pair contains the Color and Shape of the text.
     */
    @Override
    public Iterable<Pair<Color, Shape>> normalizedParts() {
        return () -> new Iterator<Pair<Color, Shape>>() {
            boolean available = true;
            @Override
            public boolean hasNext() {
                return available;
            }
            @Override
            public Pair<Color, Shape> next() {
                available = false;

                // Create text outline
                FontRenderContext frc = new FontRenderContext(null, true, true);
                Shape outline = DEFAULT_FONT.createGlyphVector(frc, text).getOutline();

                // Get bounds of text
                Rectangle2D bounds = outline.getBounds2D();

                // Translate so text center is at (0,0)
                double centerX = bounds.getCenterX();
                double centerY = bounds.getCenterY();
                AffineTransform tx = AffineTransform.getTranslateInstance(-centerX, -centerY);
                Shape centered = tx.createTransformedShape(outline);

                // Compute uniform scale to fit into [-1,1] × [-1,1]
                double scaleX = 2.0 / bounds.getWidth();
                double scaleY = 2.0 / bounds.getHeight();
                double scale = Math.min(scaleX, scaleY);  // uniform scale (preserve aspect ratio)

                // Apply scale
                AffineTransform scaleTx = AffineTransform.getScaleInstance(scale, scale);
                Shape normalized = scaleTx.createTransformedShape(centered);

                return new Pair<>(color, normalized);
            }
        };
    }
    /**
     * {@inheritDoc}
     * Converts the icon to a JSON object builder.
     * The JSON object contains the text and its color as an array.
     *
     * @return A JsonObjectBuilder representing the icon.
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("color", JsonColorConverter.colorToJsonArray(color));
        builder.add("text", text);
        return builder;
    }
    /**
     * Constructs an AchievementTextIcon from a JSON object.
     * The JSON object must contain the keys "color" and "text".
     *
     * @param obj The JsonObject to convert.
     * @return An AchievementTextIcon constructed from the JSON data.
     * @throws DataSerializationException if the JSON object is missing required keys or contains invalid data.
     */
    public static AchievementTextIcon fromJsonObject(JsonObject obj) throws DataSerializationException {
        if (!obj.containsKey("color") || !obj.containsKey("text")) {
            throw new DataSerializationException("JSON object must contain keys 'color' and 'text'");
        }
        Color color;
        try {
            color = JsonColorConverter.jsonArrayToColor(obj.getJsonArray("color"));
        } catch (IllegalArgumentException e) {
            throw new DataSerializationException("Invalid color data", e);
        }
        String text = obj.getString("text");
        return new AchievementTextIcon(text, color);
    }
}
