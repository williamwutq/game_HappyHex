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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class AchievementJsonSerializer {
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
