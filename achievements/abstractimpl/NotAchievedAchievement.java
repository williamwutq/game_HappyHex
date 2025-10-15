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
 * A wrapper for {@link GameAchievementTemplate} that marks an achievement as not achieved.
 * Not achieved achievements have their test logic inverted to indicate their status.
 * This class delegates all functionality to the wrapped template, except for the test method,
 * which is modified to return true when the original template's test method returns false.
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
public class NotAchievedAchievement implements PhantomAchievementTemplate, JsonConvertible {
    private final GameAchievementTemplate template;
    /**
     * Constructs a new {@code NotAchievedAchievement} that wraps the given template.
     * <p>
     * The name of the achievement will be prefixed with "_PHANTOM_" to indicate its phantom status.
     * @param template the achievement template to wrap
     */
    public NotAchievedAchievement(GameAchievementTemplate template) {
        this.template = template;
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement prefixed with "_PHANTOM_NOT_"
     */
    @Override
    public String realName() {
        return "NOT_" + template.name();
    }
    /**
     * {@inheritDoc}
     * @return true if the user has not achieved this achievement, false otherwise
     */
    @Override
    public boolean test(GameState state) {
        return !template.test(state);
    }

    /**
     * {@inheritDoc}
     * Two NotAchievedAchievement are considered equal if their wrapped templates are equal.
     * This ensures that the equality check is based on the actual achievement logic rather than
     * the wrapper itself. Note that the template does not equal to the NotAchievedAchievement wrapping it.
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NotAchievedAchievement)) return false;
        NotAchievedAchievement other = (NotAchievedAchievement) obj;
        return template.equals(other.template);
    }
    /**
     * The hash code is the bitwise NOT of the wrapped template's hash code.
     * This ensures that the hash code is different from the original template,
     * while still being consistent with equality.
     * @return the hash code of this achievement
     */
    @Override
    public int hashCode() {
        return ~template.hashCode();
    }
    /**
     * {@inheritDoc}
     * Delegates to the wrapped template's name.
     * @return the name of the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("type", "not");
        job.add("name", name());
        job.add("description", description());
        job.add("template", template.name());
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
        if (!jsonObject.getString("type").equals("not")) {
            throw new IllegalArgumentException("Invalid type: " + jsonObject.getString("type"));
        }
        if (!jsonObject.containsKey("name") || !jsonObject.containsKey("description") || !jsonObject.containsKey("template")) {
            throw new DataSerializationException("Missing required fields in JSON object for NotAchievedAchievement");
        }
        String templateName = jsonObject.getString("template");
        Map<String, GameAchievementTemplate> templateMap = GameAchievement.getTemplates().stream()
                .collect(Collectors.toMap(GameAchievementTemplate::name, t -> t));
        if (!templateMap.containsKey(templateName)) {
            throw new DataSerializationException("Unknown requirement: " + templateName + ", templates may not have been loaded; " +
                    "If this is the initialization, serialize independent templates first");
        } else {
            GameAchievementTemplate template = templateMap.get(templateName);
            return new NotAchievedAchievement(template);
        }
    }
    public static void load()  {
        AchievementJsonSerializer.registerAchievementClass("not", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
}
