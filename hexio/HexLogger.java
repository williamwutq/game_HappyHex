package hexio;

import hex.*;

import javax.json.*;
import javax.json.stream.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ArrayList;

public class HexLogger {
    // Hashing
    /** Bit shift values used for the hashing obfuscation process. */
    private static final int[] SHIFTS = {31, 37, 41, 27, 23, 29, 33, 43};
    /** A large prime number used in the hash function for mixing bits. */
    private static final long PRIME = 0xC96C5795D7870F3DL;
    /** A automatically generated unique identifier for the logger instance or environment. */
    private static final int ID = (int)obfuscate(PRIME);

    // Data
    private boolean completed;
    private String player;
    private long playerID;
    private HexEngine currentEngine;
    private Piece[] currentQueue;
    private ArrayList<Hex> moveOrigins;
    private ArrayList<Piece> movePieces;

    // JSON
    /** The file name this {@code HexLogger} is assigned to */
    private final String dataFile;
    /** Directory for storing game files. */
    private static final String dataDirectory = "data/";
    /** Constructs a {@code HexLogger} assigned to a specific file */
    public HexLogger(String playerName, long playerID){
        dataFile = dataDirectory + generateFileName(ID);
        currentEngine = new HexEngine(1, java.awt.Color.BLACK, java.awt.Color.WHITE);
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        movePieces = new ArrayList<Piece>();
        this.player = playerName;
        this.playerID = playerID;
        completed = false;
    }
    /** Constructs a {@code HexLogger} with a pre-assigned file name */
    private HexLogger(String fileName){
        dataFile = fileName;
        currentEngine = new HexEngine(1, java.awt.Color.BLACK, java.awt.Color.WHITE);
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        movePieces = new ArrayList<Piece>();
        player = "Guest";
        playerID = -1;
        completed = false;
    }

    /**
     * Returns the assigned filename of this {@code HexLogger}, always end in {@code .hpyhex.json}.
     * @return the assigned filename of this {@code HexLogger}
     */
    public String getDataFileName() {
        return dataFile;
    }
    // Hashing
    /**
     * Generates a string representing the current date and time in a simple format.
     * <p>
     * The format returned is: {@code yyyy-MM-dd HH:mm:ss.SSS}, where:
     * <ul>
     *   <li>{@code yyyy-MM-dd} is the current date in ISO-8601 format</li>
     *   <li>{@code HH:mm:ss.SSS} is the current time in 24-hour format with milliseconds</li>
     * </ul>
     *
     * @return a string representing the current date and time in the format {@code yyyy-MM-dd HH:mm:ss.SSS}
     */
    public static String generateSimpleTime(){
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + " " +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }
    /**
     * Generates the obfuscated current time as a long hash.
     *
     * @return A long hash result, obfuscated for uniqueness and unpredictability.
     */
    public static long generateTime() {
        return obfuscate(Instant.now().toEpochMilli()); // Get current time
    }
    /**
     * Generates a unique and obfuscated hash value from an integer input.
     * This method incorporates the current time, the static ID, and bitwise obfuscation.
     *
     * @param value The integer value to hash.
     * @return A long hash result, obfuscated for uniqueness and unpredictability.
     */
    public static long generateHash(int value) {
        long root = ((long) ID << 32) | (value & 0xFFFFFFFFL);
        return obfuscate(generateTime() ^ root);
    }
    /**
     * Obfuscates a long integer using bit shifts and prime multiplications.
     * This function provides a layered transformation for security purposes.
     * This function serve as an example and will never change.
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

    // Data
    /**
     * Returns the currently processed {@link HexEngine} in this {@code HexLogger}.
     * @return The current processed {@link HexEngine} used for logging.
     */
    public HexEngine getEngine(){
        return currentEngine;
    }
    /**
     * Returns the currently processed {@link Piece} queue in this {@code HexLogger}.
     * @return The current processed {@link Piece} queue used for logging.
     */
    public Piece[] getQueue(){
        return currentQueue;
    }
    /**
     * Returns the array of {@link Hex} coordinates of all the moves recorded in this {@code HexLogger}.
     * @return The array of coordinates of all the moves.
     */
    public Hex[] getMoveOrigins(){
        return moveOrigins.toArray(new Hex[0]);
    }
    /**
     * Returns the array of {@link Piece} involved in the moves recorded in this {@code HexLogger}.
     * @return The array of the pieces involved in the moves.
     */
    public Piece[] getMovePieces(){
        return movePieces.toArray(new Piece[0]);
    }

    /**
     * Completes the game recorded in this {@code HexLogger}. This means that this game will be registered
     * as completed and no further data changes can be made to this {@code HexLogger}.
     */
    public void completeGame(){
        completed = true;
    }
    /**
     * Assigns a player name and corresponding player ID.
     * Fallbacks to "Guest" (ID = {@code -1}) if input is not present or invalid.
     *
     * @param player the player name
     * @param ID     the player ID
     */
    public void setPlayer(String player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = "Guest";
            this.playerID = -1;
        } else {
            this.player = player;
            this.playerID = ID;
        }
    }
    /**
     * Set the current processed {@link HexEngine} to a copy of a new engine.
     * This is a deep copy and will not receive updates from the game.
     * <p>
     * Normally, this method is not used except during initialization, instead,
     * {@link #addMove(Hex, Piece)} is used for adding moves, which automatically change the engine.
     * @return Whether the engine is changed.
     */
    public boolean setEngine(HexEngine engine){
        if (completed) {return false;}
        boolean success = false;
        try {
            currentEngine = (HexEngine) engine.clone();
            success = true;
        } catch (CloneNotSupportedException e) {}
        return success;
    }
    /**
     * Set the current processed {@link Piece} queue to a copy of a new array of pieces.
     * As pieces are not supposed to be modified, this array stores reference to pre-created pieces,
     * and is generally safe to use safe.
     * <p>
     * It is necessary to update the queue after the ending of a game before logging.
     */
    public void setQueue(Piece[] queue){
        if (!completed){
            currentQueue = queue.clone();
        }
    }
    /**
     * Update the engine and the move list by adding a move, which is composed of a {@link Hex hexagonal coordinate}
     * representing the origin of the piece placement and a valid {@link Piece}. This will attempt to replicate the
     * move with the current-holding engine, and if successful, the move would be considered valid and added to the
     * move list. Otherwise, the method returns false.
     * @return Whether the engine is changed and the move was successful.
     */
    public boolean addMove(Hex origin, Piece piece){
        if (completed) {return false;}
        boolean success = false;
        try {
            currentEngine.add(origin, piece);
            currentEngine.eliminate();
            success = true;
        } catch (IllegalArgumentException e) {}
        if (success) {
            moveOrigins.add(origin);
            movePieces.add(piece);
        }
        return success;
    }
    /**
     * Returns a String representation of the {@code HexLogger}, containing all its essential information.
     * <p>
     * This String representation contains the following elements:
     * <ul>
     *     <li>The path to the file that the logger is pointing to and operating for</li>
     *     <li>The player of the recorded game and its ID</li>
     *     <li>The complete status of the game contained in the logger and the file</li>
     *     <li>The current {@link HexEngine} representing the recorded game field</li>
     *     <li>The current {@link Piece} queue in the recorded game</li>
     *     <li>The moves, containing centers {@link Hex} coordinates and {@link Piece} placed, in the recorded game</li>
     * </ul>
     * This method use the {@link Object#toString toString} method in the contained objects to generate string representations.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("GameLogger[type = HexLogger, path = ");
        builder.append(dataFile);
        builder.append(", data = HexData[player = ");
        builder.append(player);
        builder.append(", playerID = ");
        builder.append(playerID);
        builder.append(", engine = ");
        builder.append(currentEngine);
        builder.append(", queue = {");
        if (currentQueue.length > 0){
            builder.append(currentQueue[0]);
        }
        for (int i = 1; i < currentQueue.length; i++) {
            builder.append(", ");
            builder.append(currentQueue[i]);
        }
        builder.append("}, moves = {");
        if (!movePieces.isEmpty() && !moveOrigins.isEmpty()){
            builder.append("HexMove[center = ");
            builder.append(moveOrigins.get(0));
            builder.append(", piece = ");
            builder.append(movePieces.get(0));
            builder.append("]");
        }
        for (int i = 1; i < moveOrigins.size(); i++) {
            builder.append(", HexMove[center = ");
            builder.append(moveOrigins.get(i));
            builder.append(", piece = ");
            builder.append(movePieces.get(i));
            builder.append("]");
        }
        builder.append("}]]");
        return builder.toString();
    }

    // JSON
    /**
     * Scans the data directory for all files ending with ".game.json".
     *
     * @return a list of {@link Path} objects representing the .game.json files found;
     *         returns an empty list if none are found or if the directory is invalid
     */
    public static ArrayList<Path> findGameJsonFiles() {
        ArrayList<Path> result = new ArrayList<>();
        Path dir = Paths.get(dataDirectory);
        if (!Files.isDirectory(dir)) {
            return result; // Return empty list if not a directory
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{hpyhex.json}")) {
            for (Path path : stream) {
                if (path.getFileName().toString().endsWith(".hpyhex.json")) {
                    result.add(path);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(generateSimpleTime() + " HexLogger: Error occurred finding reading all game data files.");
        }
        return result;
    }
    public static ArrayList<HexLogger> generateJsonLoggers(){
        ArrayList<Path> paths = findGameJsonFiles();
        ArrayList<HexLogger> result = new ArrayList<>();
        for (Path path : paths){
            result.add(new HexLogger(path.toString()));
        }
        return result;
    }

    /**
     * Deletes the file related to this {@code HexLogger} if exists
     *
     * @return true if the file is deleted, false otherwise
     */
    public boolean deleteFile() {
        boolean success = false;
        Path filePath = Paths.get(dataFile);
        try {
            Files.deleteIfExists(filePath);
            System.out.println(generateSimpleTime() + " HexLogger: JSON data file " + dataFile + " deleted successfully.");
            success = true;
        } catch (IOException e) {}
        return success;
    }

    /**
     * Attempts to read JSON log files from {@link #dataDirectory}.
     * @return The raw JSON content as a string, or {@code null} if not found.
     */
    private String readJsonFile(){
        File file = new File(dataFile);
        if (file.exists()) {
            String result;
            try{
                result = new String(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                return null;
            }
            return result;
        } else return null;
    }

    /**
     * Create a unique filename with the naming format {@code HTHTHTHTHTHTHTHT.hpyhex.json}.
     * <p>
     * H and T represent hex bits in the long number. The bits of H(passed in {@code hash}) and
     * T(generated via {@link #generateTime()} are mixed together to maximize uniqueness
     * @param hash the int hash passed in for generate file name
     * @return the filename generated based on hash and time
     */
    private String generateFileName(int hash){
        long LongHash = generateHash(hash);
        long time = generateTime();
        StringBuilder stringBuilder = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            // Alternate between LongHash and time, high nibble to low
            int shift = (15 - i) * 4;
            if (i % 2 == 0) {
                stringBuilder.append(Integer.toHexString((int) ((LongHash >> shift) & 0xF)).toUpperCase());
            } else {
                stringBuilder.append(Integer.toHexString((int) ((time >> shift) & 0xF)).toUpperCase());
            }
        }
        return stringBuilder.toString() + ".hpyhex.json";
    }

    /**
     * Writes the provided JSON object to the appropriate log file.
     * If an existing file is found, it writes to it; otherwise, it creates a new one.
     * It writes important messages to console.
     * @param jsonObject The JSON object to write.
     */
    private void writeJsonToFile(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        JsonWriterFactory writerFactory = Json.createWriterFactory(
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));

        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.write(jsonObject);
        }

        String jsonString = stringWriter.toString();
        Path filePath = Paths.get(dataFile);
        try {
            Files.write(filePath, jsonString.getBytes());
            System.out.println(generateSimpleTime() + " HexLogger: JSON data written to " + dataFile + " successfully.");
        } catch (IOException e) {
            System.err.println(generateSimpleTime() + " HexLogger: Log game JSON data failed.");
        }
    }

    /**
     * Constructs a complete JSON object from the current logger information and writes it to a file.
     * This includes the basic game information, the current {@link HexEngine} statues, the current
     * {@link Piece} queue statues, and the moves in the game.
     * @throws IOException If JSON creation or writing fails.
     */
    public void write() throws IOException {
        // Create JSON Object
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        // Write basics
        jsonObjectBuilder.add("Game", HexIOInfo.gameName);
        jsonObjectBuilder.add("Environment", HexIOInfo.gameEnvironment);
        jsonObjectBuilder.add("Generator", "HexLogger");
        jsonObjectBuilder.add("GeneratorID", ID);

        // Write version
        JsonObjectBuilder versionBuilder = Json.createObjectBuilder();
        versionBuilder.add("Major", HexIOInfo.major);
        versionBuilder.add("Minor", HexIOInfo.minor);
        versionBuilder.add("Patch", HexIOInfo.patch);
        jsonObjectBuilder.add("Version", versionBuilder);

        // Write player
        jsonObjectBuilder.add("Player", player);
        jsonObjectBuilder.add("PlayerID", Long.toHexString(playerID));

        // Write game statues
        jsonObjectBuilder.add("Completed", completed);

        // Write engine
        jsonObjectBuilder.add("engine", HexConverter.convertEngine(currentEngine));

        // Write queue
        JsonArrayBuilder jsonQueueBuilder = Json.createArrayBuilder();
        for (Piece piece : currentQueue) {
            jsonQueueBuilder.add(HexConverter.convertPiece(piece));
        }
        jsonObjectBuilder.add("queue", jsonQueueBuilder);

        // Write moves
        JsonArrayBuilder jsonMoveArrayBuilder = Json.createArrayBuilder();
        int totalMoves = moveOrigins.size();
        for (int i = 0; i < totalMoves; i ++) {
            JsonObjectBuilder jsonMoveBuilder = Json.createObjectBuilder();
            try {
                jsonMoveBuilder.add("order", i);
                jsonMoveBuilder.add("center", HexConverter.convertHex(moveOrigins.get(i)));
                jsonMoveBuilder.add("piece", HexConverter.convertPiece(movePieces.get(i)));
            } catch (Exception e) {
                throw new IOException("Failed to create JSON objects for moves");
            }
            jsonMoveArrayBuilder.add(jsonMoveBuilder);
        }
        jsonObjectBuilder.add("moves", jsonMoveArrayBuilder);

        // write
        JsonObject resultingObject = jsonObjectBuilder.build();
        writeJsonToFile(resultingObject);
    }

    /**
     * Reads the log file and parses it into memory.
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from JSON.
     * @throws IOException If reading or parsing fails or if the game type is unsupported.
     */
    public void read() throws IOException {
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

        // Read player
        try{
            playerID = Long.parseUnsignedLong(jsonObject.getString("PlayerID"), 16);
        } catch (Exception e){
            throw new IOException("Fail to read Player ID");
        }
        try{
            player = jsonObject.getString("Player");
        } catch (Exception e){
            throw new IOException("Fail to read Player");
        }

        // Read completed (This support versions without completed)
        try{
            completed = jsonObject.getBoolean("completed");
        } catch (Exception e){
            try{
                completed = jsonObject.getBoolean("Completed");
            } catch (Exception ex){
                completed = true;
            }
        }

        // Read engine
        try{
            JsonObject engineJson = jsonObject.getJsonObject("engine");
            currentEngine = HexConverter.convertEngine(engineJson);
        } catch (Exception e) {
            throw new IOException("Fail to read game engine");
        }

        // Read queue
        try{
            JsonArray queueJson = jsonObject.getJsonArray("queue");
            int queueSize = queueJson.size();
            currentQueue = new Piece[queueSize];
            // Populate queue
            for (int i = 0; i < queueSize; i ++){
                try {
                    JsonArray pieceJson = queueJson.getJsonArray(i);
                    currentQueue[i] = HexConverter.convertPiece(pieceJson);
                } catch (Exception e) {
                    throw new IOException("Fail to read piece at index " + i + " in game queue");
                }
            }
        } catch (Exception e) {
            if (e instanceof IOException) throw e;
            throw new IOException("Fail to read game queue");
        }

        // Read moves
        try{
            JsonArray movesJson = jsonObject.getJsonArray("moves");
            int movesSize = movesJson.size();
            // Populate moves
            for (int i = 0; i < movesSize; i ++){
                try {
                    JsonObject moveJson = movesJson.getJsonObject(i);
                    // Check move order
                    int moveNumber = -1;
                    try {
                        moveNumber = moveJson.getInt("order");
                    } catch (Exception e) {}
                    if (moveNumber != i) {
                        throw new IOException("Fail to read move at index " + i + " in game moves because move order does not match");
                    }
                    // Record move
                    Hex moveOrigin; Piece movePiece;
                    try {
                        JsonObject moveOriginJson = moveJson.getJsonObject("center");
                        moveOrigin = HexConverter.convertHex(moveOriginJson);
                    } catch (Exception e) {
                        throw new IOException("Fail to read move at index " + i + " in game moves due to failed center conversion");
                    }
                    try {
                        JsonArray movePieceJson = moveJson.getJsonArray("piece");
                        movePiece = HexConverter.convertPiece(movePieceJson);
                    } catch (Exception e) {
                        throw new IOException("Fail to read move at index " + i + " in game moves due to failed piece conversion");
                    }
                    moveOrigins.add(moveOrigin);
                    movePieces.add(movePiece);
                } catch (Exception e) {
                    if (e instanceof IOException) throw e;
                    throw new IOException("Fail to read move at index " + i + " in game moves");
                }
            }
        } catch (Exception e) {
            if (e instanceof IOException) throw e;
            throw new IOException("Fail to read game moves");
        }
    }
}
