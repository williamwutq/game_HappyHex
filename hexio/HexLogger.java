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

package hexio;

import hex.*;
import hexio.hexdata.*;

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

/**
 * The {@code HexLogger} class is designed to log and manage game data for the "HappyHex" game,
 * specifically handling the recording, storage, and retrieval of game state data in JSON and binary format.
 * It supports logging critical game elements such as the game engine, piece queue, and player moves,
 * ensuring accurate tracking of game progress. The class also provides obfuscation mechanisms for
 * generating unique identifiers and timestamps, enhancing data integrity and uniqueness.
 *
 * <h3>Purpose</h3>
 * The primary purpose of {@code HexLogger} is to serve as a robust logging utility for the HappyHex game,
 * enabling developers to save game states to JSON or binary files, read them back, and manage game data efficiently.
 * It is particularly useful for tracking player progress, debugging, and analyzing game sessions. JSON files
 * also record player information, which means data could be used for reviewing. For machine learning, the
 * logged data, especially in binary format, could be used for training, helping future developers to build
 * advanced autoplay systems. The class supports the {@code hex.basic}, {@code hex} (unnamed), {@code hex.uncolored}
 * and {@code hex.binary} data formats, with plans to support additional formats in future releases.
 *
 * <h3>Function</h3>
 * The {@code HexLogger} class provides the following key functionalities:
 * <ul>
 *     <li><b>Game State Logging:</b> Records the current state of the game, including the {@link HexEngine},
 *         piece queue, and move history, into a JSON file with the {@code .hpyhex.json} extension or
 *         into a binary file with the {@code .hpyhex} extension.</li>
 *     <li><b>Data Retrieval:</b> Reads and parses JSON and binary log files to reconstruct game states in
 *         memory, populating fields like the engine, queue, and moves.</li>
 *     <li><b>Obfuscation:</b> Generates unique, obfuscated hashes and timestamps for filenames and identifiers
 *         using bit-shifting and prime multiplication techniques to ensure uniqueness and security.</li>
 *     <li><b>File Management:</b> Manages JSON and binary files in the {@code /data/} directory, including
 *         creating, reading, writing, and deleting game log files.</li>
 *     <li><b>Player Management:</b> Tracks player information, including name and ID, with support for
 *         guest profile for invalid or non-existent inputs.</li>
 *     <li><b>Game Completion:</b> Marks games as completed to prevent further modifications, ensuring
 *         data integrity for finalized game sessions.</li>
 * </ul>
 *
 * <h3>Data Format</h3>
 * This is version 1.3 of the {@code HexLogger} class, adhering to the {@code hex.basic} format standard.
 * It currently supports reading in the {@code hex.basic}, {@code hex} (unnamed), and {@code hex.uncolored} formats,
 * and writes in the {@code hex.uncolored} format.
 * In addition to ordinary json data, it also supports reading and writing binary data in the format {@code hex.binary},
 * stored in files with suffix {@code .hpyhex}.
 * Support for additional data formats is planned for future releases. See {@link #readBasicHexData} for formats.
 *
 * <h3>Example Usage</h3>
 * Below is an example demonstrating how to use the {@code HexLogger} class to log a game session,
 * add moves, complete the game, and read the logged data:
 *
 * <pre>{@code
 * // Initialize a HexLogger for a player
 * HexLogger logger = new HexLogger("PlayerOne", 123456789L);
 *
 * // Set up the game engine and piece queue
 * HexEngine engine = new HexEngine(1);
 * logger.setEngine(engine);
 * Piece[] queue = {piece, piece};
 * logger.setQueue(queue);
 *
 * // Add a move
 * Hex origin = new Hex(0, 0);
 * boolean moveSuccess = logger.addMove(origin, piece);
 * if (moveSuccess) {
 *     System.out.println("Move added successfully.");
 * }
 *
 * // Complete the game, lock data
 * logger.completeGame();
 *
 * // Write the game data to a JSON file
 * try {
 *     logger.write();
 *     System.out.println("Game data saved to: " + logger.getDataFileName());
 * } catch (IOException e) {
 *     System.err.println("Failed to save game data: " + e.getMessage());
 * }
 *
 * // Write the game data to a binary file
 * try {
 *     logger.writeBinary();
 *     System.out.println("Game data saved to binary file");
 * } catch (IOException e) {
 *     System.err.println("Failed to save game data: " + e.getMessage());
 * }
 *
 * // Read the game data from the JSON file
 * HexLogger newLogger = new HexLogger(logger.getDataFileName());
 * try {
 *     newLogger.read();
 *     System.out.println("Game data loaded: " + newLogger.toString());
 * } catch (IOException e) {
 *     System.err.println("Failed to load game data: " + e.getMessage());
 * }
 *
 * // Read the game data from the binary file (This does not include user information)
 * HexLogger newLogger = new HexLogger(logger.getDataFileName());
 * try {
 *     newLogger.read("hex.binary");
 *     System.out.println("Game data loaded: " + newLogger.toString());
 * } catch (IOException e) {
 *     System.err.println("Failed to load game data: " + e.getMessage());
 * }
 *
 * // Delete the log file if no longer needed
 * boolean deleted = logger.deleteFile();
 * if (deleted) {
 *     System.out.println("Log file deleted successfully.");
 * }
 * }</pre>
 *
 * <h3>Notes</h3>
 * <ul>
 *     <li>This class is dependent on {@link hex} package and its JSON conversion class {@link HexConverter}.</li>
 *     <li>This class is dependent on its {@link hexio.hexdata} package and its hexadecimal conversion class {@link HexDataConverter}.</li>
 *     <li>JSON files are stored in the {@code /data/} directory with names {@link #generateFileName generated} using
 *         obfuscated hashes for uniqueness, following the format {@code HTHTHTHTHTHTHTHT.hpyhex.json}.
 *         Binary files are stored in the same directory with the same naming, but with suffix {@code .hpyhex}.</li>
 *     <li>The {@link #read()}, {@link #readBinary()}, and {@link #write(String)} methods throw {@link IOException} if file operations
 *         or JSON/binary parsing fail, so proper exception handling is required.</li>
 *     <li>Future versions will expand support for additional data formats beyond {@code hex.basic}, {@code hex}, and {@code hex.uncolored}.</li>
 * </ul>
 *
 * @author William Wu
 * @version 1.3
 * @since 1.2
 */
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
    private int turn;
    private int score;
    private String player;
    private long playerID;
    private HexEngine currentEngine;
    private Piece[] currentQueue;
    private ArrayList<Hex> moveOrigins;
    private ArrayList<Piece[]> moveQueues;
    private ArrayList<Integer> movePieces;

    // JSON
    /** The file name this {@code HexLogger} is assigned to */
    private final String dataFile;
    /** Directory for storing game files. */
    private static final String dataDirectory = "data/";
    /** Format of the data logged into the JSON file. */
    private static final String dataFormat = "hex.colored";
    /** Constructs a {@code HexLogger} assigned to a specific file */
    public HexLogger(String playerName, long playerID){
        dataFile = dataDirectory + generateFileName(ID);
        currentEngine = new HexEngine(1);
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        moveQueues = new ArrayList<Piece[]>();
        movePieces = new ArrayList<Integer>();
        this.player = playerName;
        this.playerID = playerID;
        completed = false;
        turn = 0;
        score = 0;
    }
    /** Constructs a {@code HexLogger} with a pre-assigned file name */
    public HexLogger(String fileName){
        dataFile = fileName;
        currentEngine = new HexEngine(1);
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        moveQueues = new ArrayList<Piece[]>();
        movePieces = new ArrayList<Integer>();
        player = "Guest";
        playerID = -1;
        completed = false;
        turn = 0;
        score = 0;
    }

    /**
     * Returns the assigned filename of this {@code HexLogger}, always end in {@code .hpyhex.json}.
     * @return the assigned filename of this {@code HexLogger}
     */
    public String getDataFileName() {
        return dataFile;
    }
    /**
     * Gets the name of the current player.
     * @return the player's name
     */
    public String getPlayer() {return player;}
    /**
     * Gets the ID (long) of the current player.
     * @return the player’s ID
     */
    public long getPlayerID(){return playerID;}
    /**
     * Whether this game have been completed. If it is completed, the game data cannot be further modified.
     * @return true if this game is completed
     */
    public boolean isCompleted(){return completed;}
    /**
     * Returns the total turns occurred in the game.
     * @return the total turns in the game.
     * @since 1.2.4
     */
    public int getTurn(){return turn;}
    /**
     * Returns the total score in the game earned by the player.
     * @return the total score in the game.
     * @since 1.2.4
     */
    public int getScore(){return score;}
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
     * Interleaves the bits of two 32-bit integers into a single 64-bit long.
     * It is superior to just combining two integers together.
     * <p>
     * The first integer contributes its bits to the even-numbered bit positions
     * (0, 2, 4, ...) of the result, while the second integer contributes its bits
     * to the odd-numbered bit positions (1, 3, 5, ...).
     * @param even the 32-bit integer whose bits will be placed in the even bit positions of the result
     * @param odd  the 32-bit integer whose bits will be placed in the odd bit positions of the result
     * @return a 64-bit long value with bits from {@code even} and {@code odd} interleaved
     * @since 1.3
     */
    public static long interleaveIntegers(int even, int odd) {
        long eL = even & 0xFFFFFFFFL;
        long oL = odd & 0xFFFFFFFFL;
        eL = (eL | (eL << 16)) & 0x0000FFFF0000FFFFL;
        oL = (oL | (oL << 16)) & 0x0000FFFF0000FFFFL;
        eL = (eL | (eL << 8)) & 0x00FF00FF00FF00FFL;
        oL = (oL | (oL << 8)) & 0x00FF00FF00FF00FFL;
        eL = (eL | (eL << 4)) & 0x0F0F0F0F0F0F0F0FL;
        oL = (oL | (oL << 4)) & 0x0F0F0F0F0F0F0F0FL;
        eL = (eL | (eL << 2)) & 0x3333333333333333L;
        oL = (oL | (oL << 2)) & 0x3333333333333333L;
        eL = (eL | (eL << 1)) & 0x5555555555555555L;
        oL = (oL | (oL << 1)) & 0x5555555555555555L;
        return eL ^ oL << 1;
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
     * Returns the array of {@link Piece} queues involved in the moves recorded in this {@code HexLogger}.
     * @return The array of the pieces queues involved in the moves.
     * @since 1.3
     */
    public Piece[][] getMoveQueues(){
        return moveQueues.toArray(new Piece[0][0]);
    }
    /**
     * Returns the array of {@link Piece} indexes that is used to indicate the piece in the move's piece queue
     * involved in the moves recorded in this {@code HexLogger}.
     * @return The array of the pieces indexes involved in the moves.
     * @since 1.3
     */
    public int[] getMovePieceIndexes(){
        int[] arr = new int[movePieces.size()];
        for (int i = 0; i < arr.length; i ++){
            arr[i] = movePieces.get(i);
        }
        return arr;
    }
    /**
     * Returns the array of {@link Piece} involved in the moves recorded in this {@code HexLogger}.
     * @return The array of the pieces involved in the moves.
     * @see #getMoveQueues()
     * @see #getMovePieceIndexes()
     * @since 1.3
     */
    public Piece[] getMovePieces(){
        Piece[] arr = new Piece[movePieces.size()];
        for (int i = 0; i < arr.length; i ++){
            try {
                arr[i] = moveQueues.get(i)[movePieces.get(i)];
            } catch (IndexOutOfBoundsException e){
                arr[i] = null;
            }
        }
        return arr;
    }

    /**
     * Completes the game recorded in this {@code HexLogger}. This means that this game will be registered
     * as completed and no further data changes can be made to this {@code HexLogger}.
     */
    public void completeGame(){
        completed = true;
    }
    /**
     * Set the turns played in this game. This will unconditionally accept the new number of turns.
     * @param turns the new score
     * @since 1.2.4
     */
    public void setTurn(int turns) {this.turn = turns;}
    /**
     * Set the player’s score. This will unconditionally accept the new score.
     * @param score the new score
     * @since 1.2.4
     */
    public void setScore(int score) {this.score = score;}
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
        if (!completed){
            currentEngine = engine.clone();
            return true;
        } return false;
    }
    /**
     * Set the current processed {@link Piece} queue to a copy of a new array of pieces.
     * As pieces are not supposed to be modified, this array stores reference to pre-created pieces,
     * and is generally safe to use safe.
     * <p>
     * It is necessary to update the queue after the ending of a game before logging.
     * @return Whether the queue has changed.
     */
    public boolean setQueue(Piece[] queue){
        if (!completed){
            currentQueue = queue.clone();
            return true;
        } return false;
    }
    /**
     * Update the engine and the move list by adding a move, which is composed of a {@link Hex hexagonal coordinate}
     * representing the origin of the piece placement and a valid {@link Piece}. This will attempt to replicate the
     * move with the current-holding engine, and if successful, the move would be considered valid and added to the
     * move list. This would also trigger automatic incrementation of the game score and turns with the engine logic.
     * Otherwise, the method returns false.
     * @return Whether the engine is changed and the move was successful.
     * @deprecated Since 1.2.4 for implementation of recording queue at each move.
     * @see #addMove(Hex, int, Piece[])
     */
    @Deprecated
    public boolean addMove(Hex origin, Piece piece){
        if (completed) {return false;}
        boolean success = false;
        int eliminated = 0;
        try {
            currentEngine.add(origin, piece);
            eliminated = currentEngine.eliminate().length;
            success = true;
        } catch (IllegalArgumentException e) {}
        if (success) {
            moveOrigins.add(origin);
            moveQueues.add(new Piece[]{piece});
            movePieces.add(0);
            score += eliminated * 5;
            turn++;
        }
        return success;
    }
    /**
     * Update the engine and the move list by adding a move, which is composed of a {@link Hex hexagonal coordinate}
     * representing the origin of the piece placement, and valid array of {@link Piece} representing the game queue,
     * and a valid index of the piece in the game queue. This will attempt to replicate the move of placing the indexed
     * piece with the current-holding engine, and if successful, the move would be considered valid and added to the
     * move list. This would also trigger automatic incrementation of the game score and turns with the engine logic.
     * Otherwise, the method returns false.
     * @return Whether the engine is changed and the move was successful.
     */
    public boolean addMove(Hex origin, int index, Piece[] queue){
        if (completed) {return false;}
        boolean success = false;
        int eliminated = 0;
        Piece piece;
        try {
            piece = queue[index];
            currentEngine.add(origin, piece);
            eliminated = currentEngine.eliminate().length;
            score += piece.length();
            success = true;
        } catch (IndexOutOfBoundsException | IllegalArgumentException e){}
        if (success) {
            moveOrigins.add(origin);
            moveQueues.add(queue.clone());
            movePieces.add(index);
            score += eliminated * 5;
            turn++;
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
        builder.append(", player = ");
        builder.append(player);
        builder.append(", playerID = ");
        builder.append(playerID);
        builder.append(", completed = ");
        builder.append(completed);
        builder.append(", turn = ");
        builder.append(turn);
        builder.append(", score = ");
        builder.append(score);
        builder.append(", data = HexData[engine = ");
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
            builder.append(moveQueues.get(0)[movePieces.get(0)]);
            builder.append("]");
        }
        for (int i = 1; i < moveOrigins.size(); i++) {
            builder.append(", HexMove[center = ");
            builder.append(moveOrigins.get(i));
            builder.append(", piece = ");
            builder.append(moveQueues.get(i)[movePieces.get(i)]);
            builder.append("]");
        }
        builder.append("}]]");
        return builder.toString();
    }

    // JSON

    /**
     * Scans the data directory for all files ending with ".hpyhex.json".
     *
     * @return a list of {@link Path} objects representing the .hpyhex.json files found;
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

    // Binary
    /**
     * Scans the data directory for all files ending with ".hpyhex".
     *
     * @return a list of {@link Path} objects representing the .hpyhex files found;
     *         returns an empty list if none are found or if the directory is invalid
     */
    public static ArrayList<Path> findGameBinaryFiles() {
        ArrayList<Path> result = new ArrayList<>();
        Path dir = Paths.get(dataDirectory);
        if (!Files.isDirectory(dir)) {
            return result; // Return empty list if not a directory
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{hpyhex}")) {
            for (Path path : stream) {
                if (path.getFileName().toString().endsWith(".hpyhex")) {
                    result.add(path);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(generateSimpleTime() + " HexLogger: Error occurred finding reading all game data files.");
        }
        return result;
    }
    public static ArrayList<HexLogger> generateBinaryLoggers(){
        ArrayList<Path> paths = findGameBinaryFiles();
        ArrayList<HexLogger> result = new ArrayList<>();
        for (Path path : paths){
            result.add(new HexLogger(path.toString()));
        }
        return result;
    }

    /**
     * Erase the game data in this logger by reset them. This will not reset the file it points to or the player.
     * This is almost equal to create another logger.
     */
    public void erase() {
        currentEngine = new HexEngine(1);
        currentQueue = new Piece[0];
        moveOrigins = new ArrayList<Hex>();
        movePieces = new ArrayList<Integer>();
        completed = false;
        turn = 0;
        score = 0;
    }

    /**
     * Deletes the file related to this {@code HexLogger} if exists
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
        String realJsonFile = dataFile;
        if (!realJsonFile.endsWith(".json")){
            realJsonFile += ".json";
        }
        File file = new File(realJsonFile);
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
     * {@link Piece} queue statues, and the moves in the game. Uses the default format {@code hex.uncolored}.
     * @throws IOException If JSON creation or writing fails.
     * @see #createBasicData(JsonObjectBuilder)
     * @see #createUncoloredData(JsonObjectBuilder)
     * @see #read()
     * @see #write(String)
     */
    public void write() throws IOException {
        write(dataFormat);
    }

    /**
     * Constructs a complete JSON object or hexadecimal string from the current logger information
     * and writes it to a JSON or binary file. This includes the basic game information, the current
     * {@link HexEngine} statues, the current {@link Piece} queue statues, and the moves in the game.
     * This uses the specified format passed in. If the format is {@code hex.binary}, it out put a .bin file.
     * @param format The format of the JSON log. Default to the default format {@code hex.basic} if not valid.
     * @throws IOException If JSON creation or writing fails.
     * @see #createBasicData(JsonObjectBuilder)
     * @see #createColoredData(JsonObjectBuilder)
     * @see #createUncoloredData(JsonObjectBuilder)
     * @see #read()
     * @see #write()
     */
    public void write(String format) throws IOException {
        if (format.equals("hex.binary")) {
            String jsonName = getDataFileName();
            HexDataWriter writer = HexDataFactory.createWriter(jsonName.substring(0, jsonName.length()-12), "hpyhex");
            long obfScore = obfuscate(interleaveIntegers(score * score, ID ^ turn));
            long obfTurn = obfuscate(interleaveIntegers(turn * turn, ID ^ score));
            long obfCombined = ((obfTurn << 32) | (obfScore & 0xFFFFFFFFL));
            writer.addHex("4B874B1E5A0F5A0F" + "5A964B874B5A5A87");
            writer.add(obfuscate(ID * 43L ^ obfCombined ^ obfTurn) ^ obfScore);
            writer.addHex("4A41564148584C47");
            writer.add((byte)HexIOInfo.major);
            writer.add((byte)HexIOInfo.minor);
            writer.add((byte)HexIOInfo.patch);
            writer.addDivider(1);
            writer.add(ID);
            writer.addHex("214845582D42494E");
            writer.add(turn);
            writer.add(score);
            writer.add(completed);
            writer.addDivider(2);
            writer.addHex(HexDataConverter.convertBooleanEngine(currentEngine));
            writer.addDivider(1);
            writer.add((short)currentQueue.length);
            for (Piece piece : currentQueue){
                writer.addHex(HexDataConverter.convertBooleanPiece(piece));
            }
            writer.addDivider(1);
            int totalMoves = moveOrigins.size();
            for (int i = 0; i < totalMoves; i ++) {
                writer.addHex(HexDataConverter.convertHex(moveOrigins.get(i)));
                writer.add((byte)(int)(movePieces.get(i)));
                for (Piece piece : moveQueues.get(i)) {
                    writer.addHex(HexDataConverter.convertBooleanPiece(piece));
                }
            }
            writer.addDivider(2);
            writer.add((short) (obfuscate(ID) << 5));
            HexDataFactory.write(writer);
        } else {
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

            // Write data format
            jsonObjectBuilder.add("format", dataFormat);

            // Write data
            if (format.substring(0, 3).equals("hex")) {
                if (format.equals("hex.uncolored")) {
                    createUncoloredData(jsonObjectBuilder);
                } if (format.equals("hex.colored")) {
                    createColoredData(jsonObjectBuilder);
                } else {
                    createBasicData(jsonObjectBuilder);
                }
            } else throw new IOException("Data format is not \"hex\" for this version of \"HexLogger\"");

            // Write to file
            writeJsonToFile(jsonObjectBuilder.build());
        }
    }
    /**
     * Constructs a complete JSON object from the current logger information.
     * This includes the basic game information, the current {@link HexEngine} statues, the current
     * {@link Piece} queue statues, and the moves in the game. This use the {@code hex.basic} format.
     * <p>
     * This method appends information to the {@link JsonObjectBuilder} passed in as parameter.
     * @param builder The builder to accept JSON information.
     * @throws IOException If JSON creation fails.
     * @see #write(String)
     */
    public void createBasicData(JsonObjectBuilder builder) throws IOException {
        // Write game statues
        builder.add("completed", completed);

        // Write engine
        builder.add("engine", HexConverter.convertEngine(currentEngine));

        // Write queue
        JsonArrayBuilder jsonQueueBuilder = Json.createArrayBuilder();
        for (Piece piece : currentQueue) {
            jsonQueueBuilder.add(HexConverter.convertPiece(piece));
        }
        builder.add("queue", jsonQueueBuilder);

        // Write moves
        JsonArrayBuilder jsonMoveArrayBuilder = Json.createArrayBuilder();
        int totalMoves = moveOrigins.size();
        for (int i = 0; i < totalMoves; i ++) {
            JsonObjectBuilder jsonMoveBuilder = Json.createObjectBuilder();
            try {
                jsonMoveBuilder.add("order", i);
                jsonMoveBuilder.add("center", HexConverter.convertHex(moveOrigins.get(i)));
                jsonMoveBuilder.add("piece", HexConverter.convertPiece(moveQueues.get(i)[movePieces.get(i)]));
            } catch (Exception e) {
                throw new IOException("Failed to create JSON objects for moves");
            }
            jsonMoveArrayBuilder.add(jsonMoveBuilder);
        }
        builder.add("moves", jsonMoveArrayBuilder);
    }

    /**
     * Constructs a complete JSON object from the current logger information and writes it to a file.
     * This includes the basic game information, the current {@link HexEngine} statues and its color,
     * the current {@link Piece} queue statues and piece colors, and the moves in the game.
     * This use the {@code hex.colored} format.
     * <p>
     * This method appends information to the {@link JsonObjectBuilder} passed in as parameter.
     * @param builder The builder to accept JSON information.
     * @throws IOException If JSON creation or writing fails.
     * @see #write(String)
     * @since 1.3
     */
    public void createColoredData(JsonObjectBuilder builder) throws IOException {
        // Write game statues
        builder.add("completed", completed);
        builder.add("turn", turn);
        builder.add("score", score);

        // Write engine
        builder.add("engine", HexConverter.convertIndexColoredEngine(currentEngine));

        // Write queue
        JsonArrayBuilder jsonQueueBuilder = Json.createArrayBuilder();
        for (Piece piece : currentQueue) {
            jsonQueueBuilder.add(HexConverter.convertIndexColoredPiece(piece));
        }
        builder.add("queue", jsonQueueBuilder);

        // Write moves
        JsonArrayBuilder jsonMoveArrayBuilder = Json.createArrayBuilder();
        int totalMoves = moveOrigins.size();
        for (int i = 0; i < totalMoves; i ++) {
            JsonObjectBuilder jsonMoveBuilder = Json.createObjectBuilder();
            try {
                jsonMoveBuilder.add("order", i);
                jsonMoveBuilder.add("center", HexConverter.convertHex(moveOrigins.get(i)));
                jsonMoveBuilder.add("index", movePieces.get(i));
                JsonArrayBuilder jsonMoveQueueBuilder = Json.createArrayBuilder();
                for (Piece piece : moveQueues.get(i)) {
                    jsonMoveQueueBuilder.add(HexConverter.convertIndexColoredPiece(piece));
                }
                jsonMoveBuilder.add("queue", jsonMoveQueueBuilder);
            } catch (Exception e) {
                throw new IOException("Failed to create JSON objects for moves");
            }
            jsonMoveArrayBuilder.add(jsonMoveBuilder);
        }
        builder.add("moves", jsonMoveArrayBuilder);
    }

    /**
     * Constructs a complete JSON object from the current logger information and writes it to a file.
     * This includes the basic game information, the current {@link HexEngine} statues, the current
     * {@link Piece} queue statues, and the moves in the game. This use the {@code hex.uncolored} format.
     * <p>
     * This method appends information to the {@link JsonObjectBuilder} passed in as parameter.
     * @param builder The builder to accept JSON information.
     * @throws IOException If JSON creation or writing fails.
     * @see #write(String)
     */
    public void createUncoloredData(JsonObjectBuilder builder) throws IOException {
        // Write game statues
        builder.add("completed", completed);
        builder.add("turn", turn);
        builder.add("score", score);

        // Write engine
        builder.add("engine", HexConverter.convertEngine(currentEngine));

        // Write queue
        JsonArrayBuilder jsonQueueBuilder = Json.createArrayBuilder();
        for (Piece piece : currentQueue) {
            jsonQueueBuilder.add(HexConverter.convertPiece(piece));
        }
        builder.add("queue", jsonQueueBuilder);

        // Write moves
        JsonArrayBuilder jsonMoveArrayBuilder = Json.createArrayBuilder();
        int totalMoves = moveOrigins.size();
        for (int i = 0; i < totalMoves; i ++) {
            JsonObjectBuilder jsonMoveBuilder = Json.createObjectBuilder();
            try {
                jsonMoveBuilder.add("order", i);
                jsonMoveBuilder.add("center", HexConverter.convertHex(moveOrigins.get(i)));
                jsonMoveBuilder.add("index", movePieces.get(i));
                JsonArrayBuilder jsonMoveQueueBuilder = Json.createArrayBuilder();
                for (Piece piece : moveQueues.get(i)) {
                    jsonMoveQueueBuilder.add(HexConverter.convertPiece(piece));
                }
                jsonMoveBuilder.add("queue", jsonMoveQueueBuilder);
            } catch (Exception e) {
                throw new IOException("Failed to create JSON objects for moves");
            }
            jsonMoveArrayBuilder.add(jsonMoveBuilder);
        }
        builder.add("moves", jsonMoveArrayBuilder);
    }

    /**
     * Reads a binary log file and parses it into memory.
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from binary data.
     * <p>
     * For writing binary data, use {@code write("hex.binary")}.
     * @throws IOException If reading or parsing fails, or data is corrupted.
     * @since 1.3
     */
    public void readBinary() throws IOException {
        String jsonName = getDataFileName();
        if (jsonName.endsWith(".hpyhex.json")){
            jsonName = jsonName.substring(0, jsonName.length()-12);
        } else if (jsonName.endsWith(".json")){
            jsonName = jsonName.substring(0, jsonName.length()-5);
        } else if (jsonName.endsWith(".hpyhex")){
            jsonName = jsonName.substring(0, jsonName.length()-7);
        }
        HexDataReader reader = HexDataFactory.read(jsonName, "hpyhex");
        // Format check
        if (!reader.next(32).equals("4B874B1E5A0F5A0F5A964B874B5A5A87")) {
            throw new IOException("Fail to read binary data because file header is corrupted");
        }
        long code = reader.nextLong();
        reader.advance(24); // Skip generator and version
        int id = reader.nextInt();
        if (!reader.next(16).equals("214845582D42494E")) {
            throw new IOException("Fail to read binary data because file data start header is corrupted");
        }
        int turnData = reader.nextInt();
        int scoreData = reader.nextInt();
        boolean completeBooleanData = reader.nextBoolean();
        // try encoding
        long obfScore = obfuscate(interleaveIntegers(scoreData * scoreData, id ^ turnData));
        long obfTurn = obfuscate(interleaveIntegers(turnData * turnData, id ^ scoreData));
        long obfCombined = ((obfTurn << 32) | (obfScore & 0xFFFFFFFFL));
        obfCombined = obfuscate(id * 43L ^ obfCombined ^ obfTurn) ^ obfScore;
        if (obfCombined != code){
            throw new IOException("Fail to read binary data because file data encoding is corrupted or version is not supported");
        }
        if (!reader.next(4).equals("FFFF")) {
            throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
        }
        // Read engine
        HexEngine engineData;
        try {
            int radius = reader.getShort(reader.pointer());
            int l = (3*(radius)*(radius-1))/4 + 5;
            engineData = HexDataConverter.convertEngine(reader.next(l));
        } catch (IOException e) {
            throw new IOException("Fail to read engine data in binary data");
        }
        if (!reader.next(2).equals("FF")) {
            throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
        }
        // Read queue
        Piece[] queueData;
        try {
            int length = reader.nextShort();
            queueData = new Piece[length];
            for (int i = 0; i < length; i ++){
                queueData[i] = HexDataConverter.convertPiece(reader.next(2));
            }
        } catch (IOException e) {
            throw new IOException("Fail to read queue data in binary data because " + e.getMessage());
        }
        if (!reader.next(2).equals("FF")) {
            throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
        }
        // Read moves
        ArrayList<Hex> moveOriginsData = new ArrayList<Hex>(turnData);
        ArrayList<Piece[]> moveQueuesData = new ArrayList<Piece[]>(turnData);
        ArrayList<Integer> movePiecesData = new ArrayList<Integer>(turnData);
        for (int i = 0; i < turnData; i ++){
            try{
                moveOriginsData.add(HexDataConverter.convertHex(reader.next(8)));
            } catch (IOException e) {
                throw new IOException("Fail to read coordinate data in move data in binary data at index " + i);
            }
            int index = reader.nextByte();
            if (index < 0 || index >= queueData.length) {
                throw new IOException("Fail to read piece index data in move data in binary data at index " + i);
            } else movePiecesData.add(index);
            Piece[] moveQueueData;
            try {
                moveQueueData = new Piece[queueData.length];
                for (int j = 0; j < queueData.length; j++){
                    moveQueueData[j] = HexDataConverter.convertPiece(reader.next(2));
                }
            } catch (IOException e) {
                throw new IOException("Fail to read piece queue data in move data in binary data at index " + i + " because " + e.getMessage());
            }
            moveQueuesData.add(moveQueueData.clone());
        }
        if (!reader.next(4).equals("FFFF")) {
            throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
        }
        // Final check
        if ((short) (obfuscate(id) << 5) != reader.nextShort()){
            throw new IOException("Fail to read binary data because file data encoding is corrupted or version is not supported");
        }
        // Record data to this object
        moveOrigins = moveOriginsData;
        moveQueues = moveQueuesData;
        movePieces = movePiecesData;
        currentQueue = queueData.clone();
        currentEngine = engineData;
        completed = completeBooleanData;
        turn = turnData;
        score = scoreData;
    }

    /**
     * Reads the log file and parses it into memory.
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from JSON.
     * @throws IOException If reading or parsing fails or if the game type is unsupported.
     * @see #write()
     * @see #readJsonFile()
     * @see #readBasicHexData(JsonObject)
     * @see #readAdvancedHexData(JsonObject)
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

        // Read data format (This support versions without format)
        String dataFormat;
        try {
            dataFormat = jsonObject.getString("format");
        } catch (Exception e) {
            try {
                dataFormat = jsonObject.getString("Format");
            } catch (Exception ex) {
                dataFormat = "hex";
            }
        }

        if(dataFormat.substring(0, 3).equals("hex")) {
            if(dataFormat.equals("hex")) {
                readBasicHexData(jsonObject);
            } else if (dataFormat.equals("hex.basic")) {
                readBasicHexData(jsonObject);
            } else if (dataFormat.equals("hex.uncolored")) {
                readAdvancedHexData(jsonObject);
            } else if (dataFormat.equals("hex.colored")) {
                readAdvancedHexData(jsonObject);
            } else {
                throw new IOException("Data format is not compatible for this version of \"HexLogger\"");
            }
        } else throw new IOException("Data format is not \"hex\" for this version of \"HexLogger\"");
    }

    /**
     * Reads the hexagonal grid data of a format {@code hex.uncolored} or {@code hex.uncolored} data and parse it into memory.
     * <p>
     * To reflect the fact that it can read both colored and uncolored formats, the method is renamed from
     * {@code readUncoloredHexData(JsonObject)} to {@code readAdvancedHexData(JsonObject)}.
     * <p>
     * The {@code hex.uncolored} format contains:
     * <ul>
     *     <li><b>{@code completed}:</b> Whether this game should be consider completed. If completed, the data would
     *     be non-modifiable.</li>
     *     <li><b>{@code turn}:</b> The number of turns already occurred in the game.</li>
     *     <li><b>{@code score}:</b> The score already obtained by the player in the game.</li>
     *     <li><b>{@code engine}:</b> This field representing the current game engine, containing its
     *     radius, and blocks. The block record of the engine does not contain colors.</li>
     *     <li><b>{@code queue}:</b> This field representing the current game queue, containing multiple uncolored
     *     seven-block pieces, each represented by an array of blocks. The queue is not ordered</li>
     *     <li><b>{@code moves}:</b> This field representing the past moves of the game, each instance contains a
     *     number representing the move order, a snapshot of the game queue, a hex coordinate representing the center,
     *     and an index indicate which piece is placed. The moves are ordered according to move sequence</li>
     * </ul>
     * The {@code hex.uncolored} format contains:
     * <ul>
     *     <li><b>{@code completed}:</b> Whether this game should be consider completed. If completed, the data would
     *     be non-modifiable.</li>
     *     <li><b>{@code turn}:</b> The number of turns already occurred in the game.</li>
     *     <li><b>{@code score}:</b> The score already obtained by the player in the game.</li>
     *     <li><b>{@code engine}:</b> This field representing the current game engine, containing its default colors
     *     radius, and blocks. The block record of the engine does contain colors.</li>
     *     <li><b>{@code queue}:</b> This field representing the current game queue, containing multiple colored
     *     seven-block pieces, each represented by an array of blocks. The queue is colored and not ordered</li>
     *     <li><b>{@code moves}:</b> This field representing the past moves of the game, each instance contains a
     *     number representing the move order, a snapshot of the game queue, a hex coordinate representing the center,
     *     and an index indicate which piece is placed. The moves are ordered according to move sequence</li>
     * </ul>
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from JSON.
     * @throws IOException If reading or parsing fails or if the game type is unsupported.
     * @see #read()
     * @since 1.3
     */
    public void readAdvancedHexData(JsonObject jsonObject) throws IOException {
        // Read completed
        try{
            completed = jsonObject.getBoolean("completed");
        } catch (Exception e){
            try{
                completed = jsonObject.getBoolean("Completed");
            } catch (Exception ex){
                throw new IOException("Fail to read game completion status");
            }
        }

        // Read score and turn
        try{
            score = jsonObject.getInt("score");
        } catch (Exception e){
            try{
                score = jsonObject.getInt("Score");
            } catch (Exception ex){
                throw new IOException("Fail to read game score");
            }
        }
        try{
            turn = jsonObject.getInt("turn");
        } catch (Exception e){
            try{
                turn = jsonObject.getInt("Turn");
            } catch (Exception ex){
                throw new IOException("Fail to read game turn");
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
        int queueSize;
        try{
            JsonArray queueJson = jsonObject.getJsonArray("queue");
            queueSize = queueJson.size();
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
            // Check move number matching
            if (turn != movesSize){
                throw new IOException("Fail to read game moves because move array size does not match \"turn\"");
            }
            moveOrigins = new ArrayList<>(movesSize);
            movePieces = new ArrayList<>(movesSize);
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
                    // Record move queue
                    Piece[] moveQueue;
                    try{
                        JsonArray moveQueueJson = moveJson.getJsonArray("queue");
                        int moveQueueSize = moveQueueJson.size();
                        // Check queue size
                        if (moveQueueSize != queueSize){
                            throw new IOException("Fail to read move at index " + i + " because move's queue array size does not match global game queue size");
                        }
                        moveQueue = new Piece[moveQueueSize];
                        // Populate queue
                        for (int j = 0; j < moveQueueSize; j++){
                            try {
                                JsonArray pieceJson = moveQueueJson.getJsonArray(j);
                                moveQueue[j] = HexConverter.convertPiece(pieceJson);
                            } catch (Exception e) {
                                throw new IOException("Fail to read move at index \" + i + \" in game moves because queue cannot be read at index " + j);
                            }
                        }
                    } catch (Exception e) {
                        if (e instanceof IOException) throw e;
                        throw new IOException("Fail to read game queue");
                    }
                    // Record move center
                    Hex moveOrigin;
                    try {
                        JsonObject moveOriginJson = moveJson.getJsonObject("center");
                        moveOrigin = HexConverter.convertHex(moveOriginJson);
                    } catch (Exception e) {
                        throw new IOException("Fail to read move at index " + i + " in game moves due to failed center conversion");
                    }
                    // Record move
                    int moveIndex = -1;
                    try {
                        moveIndex = moveJson.getInt("index");
                    } catch (Exception e) {}
                    if (moveIndex < 0 || moveIndex >= queueSize) {
                        throw new IOException("Fail to read move at index " + i + " in game moves because move index is out of bounds");
                    }
                    // Addition
                    moveQueues.add(moveQueue);
                    movePieces.add(moveIndex);
                    moveOrigins.add(moveOrigin);
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

    /**
     * Reads the hexagonal grid data of a format {@code hex} or {@code hex.basic} data and parse it into memory.
     * <p>
     * The {@code hex} format contains:
     * <ul>
     *     <li><b>{@code engine}:</b> This field representing the current game engine, containing its default colors
     *     radius, and blocks. The block record of the engine does not contain colors.</li>
     *     <li><b>{@code queue}:</b> This field representing the current game queue, containing multiple uncolored
     *     seven-block pieces, each represented by an array of blocks. The queue is not ordered</li>
     *     <li><b>{@code moves}:</b> This field representing the past moves of the game, each instance contains a hex
     *     coordinate representing the center, and the piece that is placed. The moves are automatically ordered.</li>
     * </ul>
     * <p>
     * The {@code hex.simple} format contains:
     * <ul>
     *     <li><b>{@code completed}:</b> Whether this game should be consider completed. If completed, the data would
     *     be non-modifiable.</li>
     *     <li><b>{@code engine}:</b> This field representing the current game engine, containing its default colors
     *     radius, and blocks. The block record of the engine does not contain colors.</li>
     *     <li><b>{@code engine}:</b> This field representing the current game engine, containing its default colors
     *     radius, and blocks. The block record of the engine does not contain colors.</li>
     *     <li><b>{@code queue}:</b> This field representing the current game queue, containing multiple uncolored
     *     seven-block pieces, each represented by an array of blocks. The queue is not ordered</li>
     *     <li><b>{@code moves}:</b> This field representing the past moves of the game, each instance contains a
     *     number representing the move order, a hex coordinate representing the center, and the piece that is placed,
     *     The moves are ordered according to move sequence</li>
     * </ul>
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from JSON.
     * @throws IOException If reading or parsing fails or if the game type is unsupported.
     * @see #read()
     */
    public void readBasicHexData(JsonObject jsonObject) throws IOException {
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
            moveOrigins = new ArrayList<>(movesSize);
            movePieces = new ArrayList<>(movesSize);
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
                    movePieces.add(0);
                    moveQueues.add(new Piece[]{movePiece});
                } catch (Exception e) {
                    if (e instanceof IOException) throw e;
                    throw new IOException("Fail to read move at index " + i + " in game moves");
                }
            }
        } catch (Exception e) {
            if (e instanceof IOException) throw e;
            throw new IOException("Fail to read game moves");
        }
        turn = 0;
        score = 0;
    }
}
