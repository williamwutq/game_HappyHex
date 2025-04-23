package hexio;

import hex.*;
import javax.json.*;
import javax.json.stream.*;
import java.io.IOException;
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
    public HexLogger(){
        dataFile = dataDirectory + generateFileName(ID);
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        movePieces = new ArrayList<Piece>();
    }
    /** Constructs a {@code HexLogger} with a pre-assigned file name */
    private HexLogger(String fileName){
        dataFile = fileName;
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        movePieces = new ArrayList<Piece>();
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
     * Set the current processed {@link HexEngine} to a copy of a new engine.
     * This is a deep copy and will not receive updates from the game.
     * <p>
     * Normally, this method is not used except during initialization, instead,
     * {@link #addMove(Hex, Piece)} is used for adding moves, which automatically change the engine.
     * @return Whether the engine is changed.
     */
    public boolean setEngine(HexEngine engine){
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
        currentQueue = queue.clone();
    }
    /**
     * Update the engine and the move list by adding a move, which is composed of a {@link Hex hexagonal coordinate}
     * representing the origin of the piece placement and a valid {@link Piece}. This will attempt to replicate the
     * move with the current-holding engine, and if successful, the move would be considered valid and added to the
     * move list. Otherwise, the method returns false.
     * @return Whether the engine is changed and the move was successful.
     */
    public boolean addMove(Hex origin, Piece piece){
        boolean success = false;
        try {
            currentEngine.add(origin, piece);
            success = true;
        } catch (IllegalArgumentException e) {}
        if (success) {
            moveOrigins.add(origin);
            movePieces.add(piece);
        }
        return success;
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
        return null;
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

    public static void main(String[] args){

    }
}
