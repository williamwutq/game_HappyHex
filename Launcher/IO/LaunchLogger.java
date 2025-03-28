package Launcher.IO;

import GUI.GameEssentials;

import javax.json.*;
import javax.json.stream.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class LaunchLogger {
    // Debug
    private static final boolean IODebug = true;

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
        String jsonString = readJsonFile(); // Read JSON
        if (jsonString != null) {
            // Parse the JSON string
            JsonObject jsonObject;
            try {
                JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
                jsonObject = jsonReader.readObject();
                jsonReader.close();
            } catch (Exception e) {
                throw new IOException("Failed to parse JSON file");
            }

            // Game Check
            String game;
            try {
                game = jsonObject.getString("Game");
            } catch (Exception e) {
                throw new IOException("Game type is not found");
            }
            if (game == null || !game.equals("HappyHex")){
                throw new IOException("Game type is not HappyHex");
            }

            // Reading Scores Array
            JsonArray scoresJson = jsonObject.getJsonArray("Scores");
            for (JsonObject score : scoresJson.getValuesAs(JsonObject.class)) {
                String player = score.getString("Player");
                String playerID = score.getString("PlayerID");

                JsonObject highest = score.getJsonObject("Highest");
                int highestScore = highest.getInt("Score");
                int highestTurn = highest.getInt("Turn");

                JsonObject recent = score.getJsonObject("Recent");
                int recentScore = recent.getInt("Score");
                int recentTurn = recent.getInt("Turn");

                JsonObject time = score.getJsonObject("Time");
                String date = time.getString("Date");
                String timeStr = time.getString("Time");
                String zone = time.getString("Zone");
                GameTime individualTime = new GameTime(date, timeStr, zone);

                PlayerInfo info = new PlayerInfo(highestTurn, highestScore, recentTurn, recentScore, individualTime, playerID, player);
                scores.add(info);
            }

            // Reading Games Array
            JsonArray gamesJson = jsonObject.getJsonArray("Games");
            for (JsonObject gameObj : gamesJson.getValuesAs(JsonObject.class)) {
                String player = gameObj.getString("Player");
                String playerID = gameObj.getString("PlayerID");
                String gameID = gameObj.getString("GameID");
                GameMode gameMode;
                boolean GameEasyMode = gameObj.getBoolean("EasyMode");
                String GamePreset = gameObj.getString("Preset");
                if(GameEasyMode){
                    if(GamePreset.equals("S")){
                        gameMode = GameMode.SmallEasy;
                    } else if (GamePreset.equals("L")){
                        gameMode = GameMode.LargeEasy;
                    } else {
                        gameMode = GameMode.MediumEasy;
                    }
                } else {
                    if(GamePreset.equals("S")){
                        gameMode = GameMode.Small;
                    } else if (GamePreset.equals("L")){
                        gameMode = GameMode.Large;
                    } else {
                        gameMode = GameMode.Medium;
                    }
                }

                JsonObject gameVersion = gameObj.getJsonObject("Version");
                int gameMajor = gameVersion.getInt("Major");
                int gameMinor = gameVersion.getInt("Minor");
                int gamePatch = gameVersion.getInt("Patch");
                GameVersion individualGameVersion = new GameVersion(gameMajor, gameMinor, gamePatch);

                JsonObject gameTime = gameObj.getJsonObject("Time");
                String gameDate = gameTime.getString("Date");
                String gameTimeStr = gameTime.getString("Time");
                String gameZone = gameTime.getString("Zone");
                GameTime individualGameTime = new GameTime(gameDate, gameTimeStr, gameZone);

                JsonObject result = gameObj.getJsonObject("Result");
                int gameScore = result.getInt("Score");
                int gameTurn = result.getInt("Turn");

                GameInfo info = new GameInfo(gameTurn, gameScore, playerID, player, individualGameTime, gameID, gameMode, individualGameVersion);
                games.add(info);
            }
        }
    }

    public static void write() throws IOException {
        // Create JSON Object
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        // Write basics
        jsonObjectBuilder.add("Game", "HappyHex");
        jsonObjectBuilder.add("Environment", "java");
        jsonObjectBuilder.add("Generator", "LaunchLogger");
        jsonObjectBuilder.add("GeneratorID", ID);

        // Write version
        jsonObjectBuilder.add("Version", GameEssentials.version.toJsonObject());

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

    public static GameInfo[] fetchGames(){
        return new GameInfo[0]; // Placeholder
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
