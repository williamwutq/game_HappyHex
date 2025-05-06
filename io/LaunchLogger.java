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

package io;

import javax.json.*;
import javax.json.stream.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/**
 * The {@code LaunchLogger} class handles the reading, writing, hashing, and management of game logs.
 * <p>
 * It manages:
 * <ul>
 *   <li>Reading from and writing to JSON files with player and game statistics</li>
 *   <li>Generating secure, obfuscated hashes for identifying records</li>
 *   <li>Maintaining a list of player scores and game sessions</li>
 * </ul>
 * <p>
 * All methods and fields are static, and this class cannot be instantiated.
 * This logger is game-specific and expects the root JSON object to have a `"Game": "HappyHex"` field.
 * The logger expect to have a json file at "logs.json" in the same directory, or it may create such a file.
 *
 * @since 1.0
 * @author William Wu
 * @version 1.1
 */
public final class LaunchLogger {
    // Hashing
    /** Bit shift values used for the hashing obfuscation process. */
    private static final int[] SHIFTS = {31, 37, 41, 27, 23, 29, 33, 43};
    /** A large prime number used in the hash function for mixing bits. */
    private static final long PRIME = 0x9E4739E97F4A7C15L;
    /** A automatically generated unique identifier for the logger instance or environment. */
    private static final int ID = (int)obfuscate(PRIME);

    // JSON
    /** List storing all tracked player information. */
    private static final ArrayList<PlayerInfo> scores = new ArrayList<PlayerInfo>();
    /** List storing all game session records. */
    private static final ArrayList<GameInfo> games = new ArrayList<GameInfo>();
    /** Potential file paths for locating or saving the JSON log file. */
    private static final String[] possiblePaths = {
            "logs.json",
            "log/logs.json",
            "Game/logs.json",
            "Launcher/logs.json"
    };

    // Hashing
    /**
     * Generates a unique and obfuscated hash value from an integer input.
     * This method incorporates the current time, the static ID, and bitwise obfuscation.
     *
     * @param value The integer value to hash.
     * @return A long hash result, obfuscated for uniqueness and unpredictability.
     */
    public static long generateHash(int value) {
        long time = Instant.now().toEpochMilli(); // Get current time
        time = obfuscate(time);
        long root = ((long) ID << 32) | (value & 0xFFFFFFFFL);
        return obfuscate(time ^ root);
    }
    /**
     * Obfuscates a long integer using bit shifts and prime multiplications.
     * This function provides a layered transformation for security purposes.
     *
     * @param input The raw long value to obfuscate.
     * @return The obfuscated long result.
     */
    public static long obfuscate(long input) {
        input ^= (input << SHIFTS[0]) | (input >>> SHIFTS[1]);
        input *= PRIME;
        input ^= (input << SHIFTS[2]) | (input >>> SHIFTS[3]);
        input *= PRIME;
        input ^= (input << SHIFTS[4]) | (input >>> SHIFTS[5]);
        input *= PRIME;
        input ^= (input << SHIFTS[6]) | (input >>> SHIFTS[7]);
        return input;
    }
    /**
     * Retrieves the static identifier used by the logger.
     * This identifier is guaranteed to be the same regardless of run time evironment.
     * @return The unique logger ID.
     */
    public static int getID(){
        return ID;
    }

    // JSON
    /**
     * Attempts to read the JSON log file from one of the defined possible paths.
     * @return The raw JSON content as a string, or {@code null} if not found.
     */
    private static String readJsonFile(){
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                String result;
                try{
                    result = new String(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    continue;
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Writes the provided JSON object to an appropriate log file.
     * If an existing file is found, it writes to it; otherwise, it creates a new one.
     * It writes important messages to console.
     * @param jsonObject The JSON object to write.
     */
    private static void writeJsonToFile(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        JsonWriterFactory writerFactory = Json.createWriterFactory(
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));

        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.write(jsonObject);
        }

        String jsonString = stringWriter.toString();
        boolean success = false;

        for (String path : possiblePaths) {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                try {
                    Files.write(filePath, jsonString.getBytes());
                    System.out.println(GameTime.generateSimpleTime() + " LaunchLogger: JSON data written to " + path + " successfully.");
                    success = true;
                    break;
                } catch (IOException e) {
                    // Continue trying
                }
            }
        }

        if (!success) {
            String fallbackPath = possiblePaths[0];
            System.out.println(GameTime.generateSimpleTime() + " LaunchLogger: JSON game log not found, " + fallbackPath + " created successfully.");
            try {
                Files.write(Paths.get(fallbackPath), jsonString.getBytes());
            } catch (IOException e) {
                System.err.println(GameTime.generateSimpleTime() + " LaunchLogger: JSON data written to " + fallbackPath + " failed.");
                System.err.println(GameTime.generateSimpleTime() + " LaunchLogger: Logging failed");
            }
            System.out.println(GameTime.generateSimpleTime() + " LaunchLogger: JSON data written to " + fallbackPath + " successfully.");
        }
    }

    /** Clears all stored player and game data. */
    public static void resetLoggerInfo(){
        scores.clear();
        games.clear();
    }

    /**
     * Reads the log file and parses it into memory.
     * This populates the {@code scores} and {@code games} lists from JSON.
     * @throws IOException If reading or parsing fails or if the game type is unsupported.
     */
    public static void read() throws IOException {
        String jsonString = readJsonFile();
        if (jsonString == null) {
            throw new IOException("Failed to read JSON file because it is null");
        }

        JsonObject jsonObject;
        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))) {
            jsonObject = jsonReader.readObject();
        } catch (Exception e) {
            throw new IOException("Failed to parse JSON file", e);
        }

        // Game Check
        String game;
        try {
            game = jsonObject.getString("Game");
        } catch (Exception e) {
            throw new IOException("Game type is not found", e);
        }
        if (!"HappyHex".equals(game)) {
            throw new IOException("Game type is not HappyHex");
        }

        try {
            // Reading Scores Array
            JsonArray scoresJson = jsonObject.getJsonArray("Scores");
            for (JsonObject score : scoresJson.getValuesAs(JsonObject.class)) {
                PlayerInfo info = new PlayerInfo(
                        score.getJsonObject("Highest").getInt("Turn"),
                        score.getJsonObject("Highest").getInt("Score"),
                        score.getJsonObject("Recent").getInt("Turn"),
                        score.getJsonObject("Recent").getInt("Score"),
                        0,
                        0,
                        new GameTime(
                                score.getJsonObject("Time").getString("Date"),
                                score.getJsonObject("Time").getString("Time"),
                                score.getJsonObject("Time").getString("Zone")
                        ),
                        score.getString("PlayerID"),
                        Username.getUsername(score.getString("Player"))
                );
                scores.add(info);
            }

            // Reading Games Array
            JsonArray gamesJson = jsonObject.getJsonArray("Games");
            for (JsonObject gameObj : gamesJson.getValuesAs(JsonObject.class)) {
                GameMode gameMode = GameMode.determineGameMode(
                        gameObj.getBoolean("EasyMode"),
                        gameObj.getString("Preset")
                );

                GameInfo info = new GameInfo(
                        gameObj.getJsonObject("Result").getInt("Turn"),
                        gameObj.getJsonObject("Result").getInt("Score"),
                        gameObj.getString("PlayerID"),
                        Username.getUsername(gameObj.getString("Player")),
                        new GameTime(
                                gameObj.getJsonObject("Time").getString("Date"),
                                gameObj.getJsonObject("Time").getString("Time"),
                                gameObj.getJsonObject("Time").getString("Zone")
                        ),
                        gameObj.getString("GameID"),
                        gameMode,
                        new GameVersion(
                                gameObj.getJsonObject("Version").getInt("Major"),
                                gameObj.getJsonObject("Version").getInt("Minor"),
                                gameObj.getJsonObject("Version").getInt("Patch")
                        )
                );
                games.add(info);
            }
        } catch (Exception e) {
            throw new IOException("Error processing JSON data", e);
        }
    }

    /**
     * Constructs a complete JSON object from the current logger state and writes it to a file.
     * @throws IOException If writing fails for all file paths.
     * @see JsonConvertible
     */
    public static void write(String gameName, String gameEvironment, GameVersion gameVersion) throws IOException {
        // Create JSON Object
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        // Write basics
        jsonObjectBuilder.add("Game", gameName);
        jsonObjectBuilder.add("Environment", gameEvironment);
        jsonObjectBuilder.add("Generator", "LaunchLogger");
        jsonObjectBuilder.add("GeneratorID", ID);

        // Write version
        jsonObjectBuilder.add("Version", gameVersion.toJsonObject());

        // Write scores
        JsonArrayBuilder scoresJsonArray = Json.createArrayBuilder();
        for (PlayerInfo info : scores) {
            scoresJsonArray.add(info.toJsonObject());
        }
        jsonObjectBuilder.add("Scores", scoresJsonArray);

        // Write games
        JsonArrayBuilder gamesJsonArray = Json.createArrayBuilder();
        for (GameInfo info : games) {
            gamesJsonArray.add(info.toJsonObject());
        }
        jsonObjectBuilder.add("Games", gamesJsonArray);
        JsonObject resultingObject = jsonObjectBuilder.build();
        writeJsonToFile(resultingObject);
    }

    // DATA
    /**
     * Returns an array of all current player statistics.
     * @return An array of {@link PlayerInfo} objects.
     */
    public static PlayerInfo[] fetchPlayerStats(){
        return scores.toArray(new PlayerInfo[0]);
    }
    /**
     * Returns an array of all current game statistics.
     * @return An array of {@link GameInfo} objects.
     */
    public static GameInfo[] fetchGameStats(){
        return games.toArray(new GameInfo[0]);
    }

    /**
     * Adds a new game record and updates the associated player's recent stats.
     * If the player is new, a new {@link PlayerInfo} record is created.
     * @param gameInfo The new game information to add.
     */
    public static void addGame(GameInfo gameInfo){
        games.add(gameInfo);
        if(gameInfo.getPlayerID() != -1 && !gameInfo.getPlayer().equals("Guest")){
            for (PlayerInfo info : scores) {
                if (info.getPlayerID() != -1 && !info.getPlayer().equals("Guest")) {
                    // Skip guests
                    if (info.getPlayerID() == gameInfo.getPlayerID()) {
                        info.setRecentTurn(gameInfo.getTurn());
                        info.setRecentScore(gameInfo.getScore());
                        info.update();
                        return;
                    }
                }
            }
        }
        // If not found
        scores.add(new PlayerInfo(gameInfo.getTurn(), gameInfo.getScore(), gameInfo.getTurn(), gameInfo.getScore(), gameInfo.getTurn(), gameInfo.getScore(), gameInfo.getPlayerID(), gameInfo.getPlayer()));
    }
    /**
     * Adds or updates a player's score record.
     * @param playerInfo The player information to add or update.
     */
    public static void addPlayer(PlayerInfo playerInfo){
        if(playerInfo.getPlayerID() != -1 && !playerInfo.getPlayer().equals("Guest")){
            for (PlayerInfo info : scores){
                if(info.getPlayerID() != -1 && !info.getPlayer().equals("Guest")){
                    // Skip guests
                    if(info.getPlayerID() == playerInfo.getPlayerID()) {
                        info.setRecentTurn(playerInfo.getRecentTurn());
                        info.setRecentScore(playerInfo.getRecentScore());
                        info.update();
                        return;
                    }
                }
            }
        }
        // If not found
        scores.add(playerInfo);
    }
}