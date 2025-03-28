package Launcher.IO;

import javax.json.*;
import javax.json.stream.*;
import java.io.*;
import java.lang.management.PlatformLoggingMXBean;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public final class LaunchLogger {
    // Debug
    private static final boolean IODebug = false;

    // Hashing
    private static final int[] SHIFTS = {31, 37, 41, 27, 23, 29, 33, 43};
    private static final long PRIME = 0x9E4739E97F4A7C15L;
    private static final int ID = (int)obfuscate(PRIME);

    // JSON
    private static ArrayList<PlayerInfo> scores = new ArrayList<PlayerInfo>();
    private static ArrayList<GameInfo> games = new ArrayList<GameInfo>();

    // Hashing
    public static long generateHash(int value) {
        long time = Instant.now().toEpochMilli(); // Get current time
        time = obfuscate(time);
        long root = ((long) ID << 32) | (value & 0xFFFFFFFFL);
        return obfuscate(time ^ root);
    }
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
    public static int getID(){
        return ID;
    }

    // Try to read json log
    private static String readJsonFile() throws IOException {
        String[] possiblePaths = {
                "logs.json",
                "log/logs.json",
                "Game/logs.json",
                "Launcher/logs.json"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                return new String(Files.readAllBytes(file.toPath()));
            }
        }
        throw new IOException("No valid logs.json file is found");
    }

    private static void writeJsonToFile(JsonObject jsonObject) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriterFactory writerFactory = Json.createWriterFactory(
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));

        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.write(jsonObject);
        }

        String jsonString = stringWriter.toString();
        String logsPath = IODebug? "templogs.json" : "logs.json";
        Files.write(Paths.get(logsPath), jsonString.getBytes());
        System.out.println("JSON data written to " + logsPath + " successfully.");
    }

    public static void resetLoggerInfo(){
        scores.clear();
        games.clear();
    }

    public static void read() throws IOException {
        String jsonString;
        try {
            jsonString = readJsonFile(); // Read JSON
        } catch (Exception e) {
            throw new IOException("Failed to read JSON file", e);
        }
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
                        new GameTime(
                                score.getJsonObject("Time").getString("Date"),
                                score.getJsonObject("Time").getString("Time"),
                                score.getJsonObject("Time").getString("Zone")
                        ),
                        score.getString("PlayerID"),
                        score.getString("Player")
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
                        gameObj.getString("Player"),
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

    public static void write() throws IOException {
        // Create JSON Object
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        // Write basics
        jsonObjectBuilder.add("Game", Launcher.LaunchEssentials.currentGameName);
        jsonObjectBuilder.add("Environment", Launcher.LaunchEssentials.currentEnvironment);
        jsonObjectBuilder.add("Generator", "LaunchLogger");
        jsonObjectBuilder.add("GeneratorID", ID);

        // Write version
        jsonObjectBuilder.add("Version", Launcher.LaunchEssentials.currentGameVersion.toJsonObject());

        // Write scores
        JsonArrayBuilder scoresJsonArray = Json.createArrayBuilder();
        if(IODebug){
            PlayerInfo debugTest = new PlayerInfo(139, 2862, 70, 1429);
            scoresJsonArray.add(debugTest.toJsonObject());
        }
        for (PlayerInfo info : scores) {
            scoresJsonArray.add(info.toJsonObject());
        }
        jsonObjectBuilder.add("Scores", scoresJsonArray);

        // Write games
        JsonArrayBuilder gamesJsonArray = Json.createArrayBuilder();
        if(IODebug){
            GameInfo debugTest = new GameInfo(139, 2862, GameMode.Small, new GameVersion(0,3,1));
            gamesJsonArray.add(debugTest.toJsonObject());
        }
        for (GameInfo info : games) {
            gamesJsonArray.add(info.toJsonObject());
        }
        jsonObjectBuilder.add("Games", gamesJsonArray);
        JsonObject resultingObject = jsonObjectBuilder.build();
        writeJsonToFile(resultingObject);
    }

    public static PlayerInfo[] fetchPlayerStats(){
        return new PlayerInfo[0]; // Placeholder
    }

    public static void addGame(GameInfo gameInfo){
        games.add(gameInfo);
        if(gameInfo.getPlayerID() != -1 && !gameInfo.getPlayer().equals("Guest")){
            scores.add(new PlayerInfo(gameInfo.getTurn(), gameInfo.getScore(), gameInfo.getTurn(), gameInfo.getScore()));
        } else for (PlayerInfo info : scores){
            if(info.getPlayerID() != -1 && !info.getPlayer().equals("Guest")){
                // Skip guests
                if(info.getPlayerID() == gameInfo.getPlayerID()){
                    info.setRecentTurn(gameInfo.getTurn());
                    info.setRecentScore(gameInfo.getScore());
                    info.updateHigh();
                    return;
                }
            }
        }
        // If not found
        scores.add(new PlayerInfo(gameInfo.getTurn(), gameInfo.getScore(), gameInfo.getTurn(), gameInfo.getScore(), gameInfo.getPlayerID(), gameInfo.getPlayer()));
    }
    public static void addPlayer(PlayerInfo playerInfo){
        if(playerInfo.getPlayerID() != -1 && !playerInfo.getPlayer().equals("Guest")){
            scores.add(playerInfo);
        } else for (PlayerInfo info : scores){
            if(info.getPlayerID() != -1 && !info.getPlayer().equals("Guest")){
                // Skip guests
                if(info.getPlayerID() == playerInfo.getPlayerID()){
                    info.setRecentTurn(playerInfo.getRecentTurn());
                    info.setRecentScore(playerInfo.getRecentScore());
                    info.updateHigh();
                    return;
                }
            }
        }
        // If not found
        scores.add(playerInfo);
    }

    // Getters

    public static void main(String[] args){
        try {
            read();
            write();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
