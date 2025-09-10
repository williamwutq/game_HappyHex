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

package achievements;

import io.JsonConvertible;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class AchievementJsonSerializer {
    // We do not, in fact, have a serialization map. We only have a deserialization map.
    private static final Map<String, Function<JsonObject, GameAchievementTemplate>> ACHIEVEMENT_DESERIAL_NAME_MAP = new HashMap<>();
    private static final Set<GameAchievementTemplate> BUILT_IN_ACHIEVEMENT_INSTANCE = new HashSet<>();
    static {
        // Add default deserializers for built-in achievements
        ACHIEVEMENT_DESERIAL_NAME_MAP.put("JavaBuildIn", json -> {
            try{
                return deserializeBuiltInAchievement(json);
            } catch (DataSerializationException e){
                throw new RuntimeException("Failed to deserialize built-in achievement.", e);
            }
        });
    }
    /**
     * Registers a built-in achievement class.
     * For internal use only. Does not have any checks.
     * @param clazz the class to register
     */
    static void registerBuildInClass(Class<? extends GameAchievementTemplate> clazz){
        // Create an instance of the class using reflection
        GameAchievementTemplate instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to instantiate class: " + clazz.getName(), e);
        }
        BUILT_IN_ACHIEVEMENT_INSTANCE.add(instance);
    }
    /**
     * Registers a custom achievement class with a deserializer function.
     * @param serialName the unique name for the achievement class
     * @param deserializer the function to deserialize a JsonObject into an instance of the achievement class
     * @throws IllegalArgumentException if the serialName is already registered or if deserializer is null
     */
    public static void registerAchievementClass(String serialName, Function<JsonObject, GameAchievementTemplate> deserializer) {
        if (deserializer == null) {
            throw new IllegalArgumentException("Deserializer function cannot be null.");
        }
        if (serialName == null || serialName.isBlank()) {
            throw new IllegalArgumentException("Serial name cannot be null or blank.");
        }
        if (ACHIEVEMENT_DESERIAL_NAME_MAP.containsKey(serialName)) {
            throw new IllegalArgumentException("An achievement with the name " + serialName + " is already registered.");
        }
    }
    /**
     * Deserializes a GameAchievementTemplate from a JsonObject for built-in achievements.
     * @param json the JsonObject to deserialize
     * @return the deserialized GameAchievementTemplate
     * @throws DataSerializationException if the JsonObject is invalid or if deserialization fails
     */
    private static GameAchievementTemplate deserializeBuiltInAchievement(JsonObject json) throws DataSerializationException {
        // Get name, description
        String type, name, description;
        try {
            type = json.getString("type");
            name = json.getString("name");
            description = json.getString("description");
        } catch (Exception e){
            throw new DataSerializationException("Failed to parse built-in achievement: missing or invalid 'type', 'name', or 'description' fields.", e);
        }
        if (type == null || !type.equals("JavaBuildIn") || name == null || name.isBlank() || description == null || description.isBlank()) {
            throw new DataSerializationException("Failed to parse built-in achievement: 'type' must be 'JavaBuildIn', 'name', and 'description' fields cannot be null or blank.");
        }
        // Search for the built-in achievement with the given name
        for (GameAchievementTemplate achievement : BUILT_IN_ACHIEVEMENT_INSTANCE) {
            if (achievement.name().equals(name) && achievement.description().equals(description)) {
                return achievement;
            }
        }
        throw new DataSerializationException("No built-in achievement found with name: " + name);
    }

    private AchievementJsonSerializer() {
        // Private constructor to prevent instantiation
    }

    // JSON to and from String
    /**
     * Converts a JsonObject to a pretty-printed JSON string.
     *
     * @param jsonObject the JsonObject to convert
     * @return a pretty-printed JSON string representation of the JsonObject
     */
    public String createJsonString(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        JsonWriterFactory writerFactory = Json.createWriterFactory(
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));

        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.write(jsonObject);
        }
        return stringWriter.toString();
    }
    /**
     * Parses a JSON string and returns it as a JsonObject.
     *
     * @param jsonString the JSON string to parse
     * @return the parsed JsonObject
     */
    public JsonObject parseJsonString(String jsonString) {
        return Json.createReader(new java.io.StringReader(jsonString)).readObject();
    }

    // File Operations
    /**
     * Reads the contents of a file and returns it as a string.
     *
     * @param filePath the path to the file
     * @return the contents of the file as a string
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    public String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Path.of(filePath)));
    }
    /**
     * Writes the given content to a file at the specified path.
     *
     * @param filePath the path to the file
     * @param content  the content to write to the file
     * @throws IOException if an I/O error occurs writing to or creating the file
     */
    public void writeFile(String filePath, String content) throws IOException {
        Path path = Path.of(filePath);
        Files.writeString(path, content);
    }
    /**
     * Appends the given content to a file at the specified path. If the file does not exist, it will be created.
     *
     * @param filePath the path to the file
     * @param content  the content to append to the file
     * @throws IOException if an I/O error occurs writing to or creating the file
     */
    public void appendToFile(String filePath, String content) throws IOException {
        Path path = Path.of(filePath);
        Files.writeString(path, content, java.nio.file.StandardOpenOption.APPEND, java.nio.file.StandardOpenOption.CREATE);
    }
}
