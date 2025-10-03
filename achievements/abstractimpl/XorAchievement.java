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
import achievements.GameAchievement;
import achievements.GameAchievementTemplate;
import hex.GameState;
import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A wrapper for {@link GameAchievementTemplate} that is archived when exactly one of the two wrapped achievements
 * is achieved. This class delegates all functionality to the wrapped templates, except for the test method,
 * which is modified to return true when exactly one of the original templates' test methods returns true,
 * indicating an exclusive or (XOR) condition.
 * <p>
 * Note: The name and description remain unchanged and are directly delegated to the
 * wrapped template.
 * <p>
 * This is marked phantom to prevent display in achievement lists and to enable constant checking regardless
 * of whether the achievement has been achieved.
 *
 * @see GameAchievementTemplate
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class XorAchievement implements PhantomAchievementTemplate, JsonConvertible {
    private final GameAchievementTemplate template1;
    private final GameAchievementTemplate template2;
    /**
     * Constructs a new {@code XorAchievement} that wraps the given template.
     * <p>
     * The name of the achievement will be prefixed with "_PHANTOM_" to indicate its phantom status.
     * @param template1 the achievement template to wrap
     */
    public XorAchievement(GameAchievementTemplate template1, GameAchievementTemplate template2) {
        this.template1 = template1;
        this.template2 = template2;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement prefixed with "_PHANTOM_"
     */
    @Override
    public String realName() {
        return template1.name() + "_XOR_" + template2.name();
    }
    /**
     * {@inheritDoc}
     * @return true if the user has achieved exactly one of the two achievements, false otherwise
     */
    @Override
    public boolean test(GameState state) {
        return template1.test(state) ^ template2.test(state);
    }
    /**
     * {@inheritDoc}
     * Two XorAchievement are considered equal if their wrapped templates are equal.
     * This ensures that the equality check is based on the actual achievement logic rather than
     * the wrapper itself. Note that the template does not equal to the XorAchievement wrapping it.
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof XorAchievement)) return false;
        XorAchievement other = (XorAchievement) obj;
        return (template1.equals(other.template1) && template2.equals(other.template2)) ||
                (template1.equals(other.template2) && template2.equals(other.template1));
    }
    /**
     * The hash code is the bitwise XOR of the wrapped templates' hash codes.
     * This ensures that the hash code is different from the original templates,
     * while still being consistent with equality.
     * @return the hash code of this achievement
     */
    @Override
    public int hashCode() {
        return template1.hashCode() ^ template2.hashCode();
    }
    /**
     * {@inheritDoc}
     * Delegates to the wrapped template's name.
     * @return the name of the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("type", "xor");
        job.add("name", name());
        job.add("description", description());
        job.add("template1", template1.name());
        job.add("template2", template2.name());
        return job;
    }
    /**
     * Deserializes a {@code XorAchievement} from a JSON object.
     * <p>
     * The JSON object must contain the following fields:
     * <ul>
     *     <li>"type": must be "xor"</li>
     *     <li>"name": the name of the achievement, disregarded</li>
     *     <li>"description": the description of the achievement, disregarded</li>
     *     <li>"template1": the name of the first wrapped achievement template</li>
     *     <li>"template2": the name of the second wrapped achievement template</li>
     * </ul>
     * If any of these fields are missing or if the type is incorrect, a {@code DataSerializationException} is thrown.
     * <p>
     * Note: This method assumes that all independent achievement templates have already been loaded
     * into {@link GameAchievement#getTemplates()}. If this is not the case, an exception will be thrown.
     *
     * @param jsonObject the JSON object to deserialize from
     * @return a new {@code XorAchievement} instance
     * @throws DataSerializationException if required fields are missing or if referenced templates are unknown
     */
    public static PhantomAchievementTemplate fromJsonObject(JsonObject jsonObject) throws DataSerializationException {
        if (!jsonObject.getString("type").equals("xor")) {
            throw new IllegalArgumentException("Invalid type: " + jsonObject.getString("type"));
        }
        if (!jsonObject.containsKey("name") || !jsonObject.containsKey("description") || !jsonObject.containsKey("template1") || !jsonObject.containsKey("template2")) {
            throw new DataSerializationException("Missing required fields in JSON object for XorAchievement");
        }
        String template1Name = jsonObject.getString("template1");
        String template2Name = jsonObject.getString("template2");
        Map<String, GameAchievementTemplate> templateMap = GameAchievement.getTemplates().stream()
                .collect(Collectors.toMap(GameAchievementTemplate::name, t -> t));
        if (!templateMap.containsKey(template1Name)) {
            throw new DataSerializationException("Unknown requirement: " + template1Name + ", templates may not have been loaded; " +
                    "If this is the initialization, serialize independent templates first");
        } else if (!templateMap.containsKey(template2Name)) {
            throw new DataSerializationException("Unknown requirement: " + template2Name + ", templates may not have been loaded; " +
                    "If this is the initialization, serialize independent templates first");
        }
        GameAchievementTemplate template1 = templateMap.get(template1Name);
        GameAchievementTemplate template2 = templateMap.get(template2Name);
        return new XorAchievement(template1, template2);
    }
    public static void load()  {
        AchievementJsonSerializer.registerAchievementClass("xor", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
}
