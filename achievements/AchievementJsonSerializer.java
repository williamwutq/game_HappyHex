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

import achievements.abstractimpl.HiddenAchievement;

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
    static {
        // Imports: This is necessary to ensure that the built-in achievements are loaded and registered.
        achievements.impl.NumberBasedAchievement.load();
        achievements.impl.QueueBasedAchievement.load();
        achievements.impl.EliminationAchievement.load();
        achievements.abstractimpl.MarkableAchievement.load();
        achievements.abstractimpl.SerialAchievement.load();
        achievements.abstractimpl.AnyAchievement.load();
        achievements.abstractimpl.NotAchievedAchievement.load();
        achievements.abstractimpl.XorAchievement.load();
        achievements.staticimpl.StaticAchievement.load();
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
        ACHIEVEMENT_DESERIAL_NAME_MAP.put(serialName, deserializer);
    }

    private AchievementJsonSerializer() {
        // Private constructor to prevent instantiation
    }

    // Serialization of User Achievements
    /**
     * Serializes a UserAchievements instance to a JSON file.
     * The file will be named after the user's name and saved in the specified directory.
     * @param achievements the UserAchievements instance to serialize
     * @param dirPath the directory path where the JSON file will be saved
     * @throws IOException if an I/O error occurs writing to or creating the file
     * @throws IllegalArgumentException if achievements is null or if dirPath is null or blank
     */
    public static void serializeUserAchievements(UserAchievements achievements, String dirPath) throws IOException {
        if (achievements == null) {
            throw new IllegalArgumentException("Achievements cannot be null.");
        }
        if (dirPath == null || dirPath.isBlank()) {
            throw new IllegalArgumentException("Directory path cannot be null or blank.");
        }
        String userName = achievements.getUser().toString();
        String filePath = dirPath + "/" + userName + ".hpyhexua.json";
        JsonObject jsonObject = achievements.toJsonObject();
        String jsonString = createJsonString(jsonObject);
        writeFile(filePath, jsonString);
    }
    /**
     * Deserializes a UserAchievements instance from a JSON file named after the user's name in the specified directory.
     * @param user the name of the user
     * @param dirPath the directory path where the JSON file is located
     * @return the deserialized UserAchievements instance
     * @throws IOException if an I/O error occurs reading from the file
     * @throws DataSerializationException if the JSON is invalid or if deserialization fails
     * @throws IllegalArgumentException if user is null or blank, or if dirPath is null or blank
     */
    public static UserAchievements deserializeUserAchievements(String user, String dirPath) throws IOException, DataSerializationException {
        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("User cannot be null or blank.");
        }
        if (dirPath == null || dirPath.isBlank()) {
            throw new IllegalArgumentException("Directory path cannot be null or blank.");
        }
        String filePath = dirPath + "/" + user + ".hpyhexua.json";
        return deserializeUserAchievements(filePath);
    }
    /**
     * Deserializes a UserAchievements instance from a JSON file.
     * The file must contain a valid JSON representation of a UserAchievements instance.
     * @param filePath the path to the JSON file
     * @return the deserialized UserAchievements instance
     * @throws IOException if an I/O error occurs reading from the file
     * @throws DataSerializationException if the JSON is invalid or if deserialization fails
     * @throws IllegalArgumentException if filePath is null or does not end with .hpyhexua.json
     */
    public static UserAchievements deserializeUserAchievements(String filePath) throws IOException, DataSerializationException {
        if (filePath == null || !filePath.endsWith(".hpyhexua.json")) {
            throw new IllegalArgumentException("File path cannot be null and must end with .hpyhexua.json");
        }
        String fileContent = readFile(filePath);
        JsonObject jsonObject = parseJsonString(fileContent);
        return UserAchievements.fromJsonObject(jsonObject);
    }
    /**
     * Adds achievements from a UserAchievements instance to an existing UserAchievements file if it exists.
     * If the file does not exist, the original UserAchievements instance is returned.
     * @param achievements the UserAchievements instance to add
     * @param dirPath the directory path where the JSON file is located or will be saved
     * @return the updated UserAchievements instance if the file exists, otherwise the original instance
     * @throws IOException if an I/O error occurs reading from or writing to the file
     * @throws DataSerializationException if deserialization fails
     * @throws IllegalArgumentException if achievements is null or if dirPath is null or blank
     */
    public static UserAchievements addToUserAchievementsWithName(UserAchievements achievements, String dirPath) throws IOException, DataSerializationException {
        if (achievements == null) {
            throw new IllegalArgumentException("Achievements cannot be null.");
        }
        if (dirPath == null || dirPath.isBlank()) {
            throw new IllegalArgumentException("Directory path cannot be null or blank.");
        }
        String userName = achievements.getUser().toString();
        String filePath = dirPath + "/" + userName + ".hpyhexua.json";
        Path path = Path.of(filePath);
        if (Files.exists(path)) {
            UserAchievements existingAchievements = deserializeUserAchievements(filePath);
            existingAchievements.addAllAchievements(achievements.getAchievements().stream().toList());
            return existingAchievements;
        } else {
            return achievements;
        }
    }

    // Deserialization of Achievement Templates
    /**
     * Deserializes an array of GameAchievementTemplate from a JsonObject.
     * The JsonObject must contain an "Achievements" array, where each element is a JsonObject
     * representing an achievement with a "type" field indicating the achievement type.
     * @param json the JsonObject containing the "Achievements" array
     * @return an array of deserialized GameAchievementTemplate instances
     * @throws DataSerializationException if the JsonObject is invalid or if deserialization fails
     */
    public static GameAchievementTemplate[] deserializeAchievementTemplateArray(JsonObject json) throws DataSerializationException {
        if (!json.containsKey("Achievements") || !json.get("Achievements").getValueType().equals(javax.json.JsonValue.ValueType.ARRAY)) {
            throw new DataSerializationException("Invalid JSON: missing 'Achievements' array");
        }
        javax.json.JsonArray jsonArray = json.getJsonArray("Achievements");
        List<GameAchievementTemplate> achievements = new ArrayList<>();
        // Parse
        for (javax.json.JsonValue value : jsonArray) {
            if (!value.getValueType().equals(javax.json.JsonValue.ValueType.OBJECT)) {
                throw new DataSerializationException("Invalid JSON: 'achievements' array must contain JSON objects");
            }
            JsonObject obj = (JsonObject) value;
            String type;
            try {
                type = obj.getString("type");
            } catch (Exception e) {
                throw new DataSerializationException("Invalid JSON: each achievement object must contain a 'type' field", e);
            }
            if (!obj.containsKey("name") || !obj.containsKey("description")) {
                throw new DataSerializationException("Invalid JSON: each achievement object must contain 'name' and 'description' fields");
            }
            Function<JsonObject, GameAchievementTemplate> deserializer = ACHIEVEMENT_DESERIAL_NAME_MAP.get(type);
            GameAchievementTemplate template;
            if (deserializer == null) {
                throw new DataSerializationException("No deserializer registered for achievement of type " + type);
            }
            try {
                template = deserializer.apply(obj);
            } catch (Exception e) {
                throw new DataSerializationException("Failed to deserialize achievement of type " + type, e.getCause());
            }
            if (HiddenAchievement.isHidden(obj.getString("name"))) {
                template = HiddenAchievement.wrap(template);
            }
            achievements.add(template);
        }
        return achievements.toArray(new GameAchievementTemplate[0]);
    }
    /**
     * Deserializes an array of GameAchievementTemplate from a JSON file.
     * The file must contain a JSON object with an "Achievements" array, where each element is a JsonObject
     * representing an achievement with a "type" field indicating the achievement type.
     * @param filePath the path to the JSON file
     * @return an array of deserialized GameAchievementTemplate instances
     * @throws IOException if an I/O error occurs reading from the file
     * @throws DataSerializationException if the JSON is invalid or if deserialization fails
     */
    public static GameAchievementTemplate[] deserializeAchievementTemplateFile(String filePath) throws IOException, DataSerializationException {
        AchievementJsonSerializer serializer = new AchievementJsonSerializer();
        String fileContent = serializer.readFile(filePath);
        JsonObject jsonObject = serializer.parseJsonString(fileContent);
        return deserializeAchievementTemplateArray(jsonObject);
    }
    /**
     * Deserializes an array of GameAchievementTemplate from a resource file.
     * The resource file must contain a JSON object with an "Achievements" array, where each element is a JsonObject
     * representing an achievement with a "type" field indicating the achievement type.
     * @param resourcePath the path to the resource file
     * @return an array of deserialized GameAchievementTemplate instances
     * @throws IOException if an I/O error occurs reading from the resource
     * @throws DataSerializationException if the JSON is invalid or if deserialization fails
     */
    public static GameAchievementTemplate[] deserializeAchievementTemplateResource(String resourcePath) throws IOException, DataSerializationException {
        AchievementJsonSerializer serializer = new AchievementJsonSerializer();
        String resourceContent = serializer.readResource(resourcePath);
        JsonObject jsonObject = serializer.parseJsonString(resourceContent);
        return deserializeAchievementTemplateArray(jsonObject);
    }

    // JSON to and from String
    /**
     * Converts a JsonObject to a pretty-printed JSON string.
     *
     * @param jsonObject the JsonObject to convert
     * @return a pretty-printed JSON string representation of the JsonObject
     */
    public static String createJsonString(JsonObject jsonObject) {
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
    public static JsonObject parseJsonString(String jsonString) {
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
    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Path.of(filePath)));
    }
    /**
     * Writes the given content to a file at the specified path.
     *
     * @param filePath the path to the file
     * @param content  the content to write to the file
     * @throws IOException if an I/O error occurs writing to or creating the file
     */
    public static void writeFile(String filePath, String content) throws IOException {
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
    public static void appendToFile(String filePath, String content) throws IOException {
        Path path = Path.of(filePath);
        Files.writeString(path, content, java.nio.file.StandardOpenOption.APPEND, java.nio.file.StandardOpenOption.CREATE);
    }

    // Resource Loading
    /**
     * Reads the contents of a resource file and returns it as a string.
     * <p>
     * This behaves exactly like {@link #readFile(String)}, but reads from the classpath resources instead of the filesystem.
     * If run from a JAR, it reads from within the JAR.
     * <p>
     * Note: For example, if the file {@code data/file.txt} is in the jar, and a {@code data/file.txt} is in the working directory
     * then calling {@code readResource("/data/file.txt")} will read the file from within the JAR, not the working directory.
     *
     * @param resourcePath the path to the resource file
     * @return the contents of the resource file as a string
     * @throws IOException if an I/O error occurs reading from the resource or if the resource is not found
     */
    public static String readResource(String resourcePath) throws IOException {
        try (java.io.InputStream is = AchievementJsonSerializer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes());
        }
    }
}
