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

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * The {@code HexLogger} class is designed to log and manage game data for the "HappyHex" game,
 * specifically handling the recording, storage, and retrieval of game state data in binary formats.
 * It supports logging critical game elements such as the game engine, piece queue, and player moves,
 * ensuring accurate tracking of game progress. The class also provides obfuscation mechanisms for
 * generating unique identifiers and timestamps, enhancing data integrity and uniqueness.
 *
 * <h3>Purpose</h3>
 * The primary purpose of {@code HexLogger} is to serve as a robust logging utility for the HappyHex game,
 * enabling developers to save game states to binary files, read them back, and manage game data efficiently.
 * It is particularly useful for tracking player progress, debugging, and analyzing game sessions. The
 * logged data, especially in binary format, could be used for training, helping future developers to build
 * advanced autoplay systems. The class currently only supports the {@code hex.binary} data formats, with
 * plans to support additional binary formats in future releases.
 *
 * <h3>Function</h3>
 * The {@code HexLogger} class provides the following key functionalities:
 * <ul>
 *     <li><b>Game State Logging:</b> Records the current state of the game, including the {@link HexEngine},
 *         piece queue, and move history, into a binary file with the {@code .hpyhex} extension.</li>
 *     <li><b>Data Retrieval:</b> Reads and parses binary log files to reconstruct game states in
 *         memory, populating fields like the engine, queue, and moves.</li>
 *     <li><b>Obfuscation:</b> Generates unique, obfuscated hashes and timestamps for filenames and identifiers
 *         using bit-shifting and prime multiplication techniques to ensure uniqueness and security.</li>
 *     <li><b>File Management:</b> Manages binary files in the {@code /data/} directory, including
 *         creating, reading, writing, and deleting game log files.</li>
 *     <li><b>Player Management:</b> Tracks player information, including name and ID, with support for
 *         guest profile for invalid or non-existent inputs.</li>
 *     <li><b>Game Completion:</b> Marks games as completed to prevent further modifications, ensuring
 *         data integrity for finalized game sessions.</li>
 *     <li><b>Game Record</b> Implements {@link GameState}, can serve as a mutable game state.</li>
 * </ul>
 *
 * <h3>Data Format</h3>
 * This is version 1.3 of the {@code HexLogger} class, adhering to the {@code hex.binary} format standard.
 * It currently only supports reading and writing n the {@code hex.binary} format, stored in files with suffix {@code .hpyhex}.
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
 * // Write the game data to a binary file
 * try {
 *     logger.write();
 *     System.out.println("Game data saved to binary file");
 * } catch (IOException e) {
 *     System.err.println("Failed to save game data: " + e.getMessage());
 * }
 *
 * // Read the game data from the binary file (This currently does not include user information)
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
 *     <li>This class is dependent on {@link hex} package.</li>
 *     <li>This class is dependent on its {@link hexio.hexdata} package and its hexadecimal conversion class {@link HexDataConverter}.</li>
 *     <li>Binary files are stored in the {@code /data/} directory with names {@link #generateFileName generated} using
 *         obfuscated hashes for uniqueness, following the format {@code HTHTHTHTHTHTHTHT.hpyhex}.</li>
 *     <li>The {@link #read()}, and {@link #write(String)} methods throw {@link IOException} if file operations
 *         or binary parsing fail, so proper exception handling is required.</li>
 *     <li>Future versions will expand support for additional data formats beyond {@code hex.binary} and provide colored support.</li>
 * </ul>
 *
 * @author William Wu
 * @version 2.0
 * @since 1.2
 */
public class HexLogger implements GameState {
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

    /** The file name this {@code HexLogger} is assigned to */
    private final String dataFile;
    /** Directory for storing game files. */
    private static final String dataDirectory = "data/";
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
        if (fileName.endsWith(".hpyhex.json")){
            dataFile = fileName.substring(0, fileName.length()-12);
        } else if (fileName.endsWith(".json")){
            dataFile = fileName.substring(0, fileName.length()-5);
        } else if (fileName.endsWith(".hpyhex")){
            dataFile = fileName.substring(0, fileName.length()-7);
        } else {
            dataFile = fileName;
        }
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
     * Returns the assigned filename of this {@code HexLogger}, always end in {@code .hpyhex}.
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
     * Ensures the string is exactly the given length.
     * If the string is longer, it is truncated.
     * If the string is shorter, it is padded with spaces.
     *
     * @param input The original string.
     * @param length The desired length of the result.
     * @return The formatted string.
     * @since 1.3.3
     */
    public static String formatStringToLength(String input, int length) {
        if (input == null) input = "";
        if (input.length() > length) {
            return input.substring(0, length);
        } else if (input.length() < length) {
            return String.format("%-" + length + "s", input);
        } else {
            return input;
        }
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
     * {@link #addMove(Hex, int, Piece[])} is used for adding moves, which automatically change the engine.
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
     * representing the origin of the piece placement, and valid array of {@link Piece} representing the game queue,
     * and a valid index of the piece in the game queue. This will attempt to replicate the move of placing the indexed
     * piece with the current-holding engine, and if successful, the move would be considered valid and added to the
     * move list. This would also trigger automatic incrementation of the game score and turns with the engine logic.
     * Otherwise, the method returns false.
     * @return Whether the engine is changed and the move was successful.
     */
    public boolean addMove(Hex origin, int index, Piece[] queue){
        if (completed) {return false;}
        try {
            Piece piece = queue[index];
            currentEngine.add(origin, piece);
            moveOrigins.add(origin);
            moveQueues.add(queue.clone());
            movePieces.add(index);
            score += currentEngine.eliminate().length * 5;
            score += piece.length();
            turn ++;
            return true;
        } catch (IndexOutOfBoundsException | IllegalArgumentException e){
            return false;
        }
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
        StringBuilder builder = new StringBuilder("GameLogger[type = HexLogger, file = ");
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

    /**
     * Creates and returns a deep clone of this {@code HexLogger} instance.
     * <p>
     * The method first attempts to use {@link Object#clone()} via {@code super.clone()} to create a shallow copy of
     * the object. If cloning is not supported, it manually constructs a new {@code HexLogger} instance by duplicating
     * key internal state fields then cloning the mutable fields. The clone operations may take some time for loggers
     * containing significant data.
     * <p>
     * The method copy the mutable fields, including:
     * <ul>
     *     <li>{@code movePieces} is copied shallowly as it contains {@code Integer} objects, which are immutable.</li>
     *     <li>{@code moveOrigins} is deep copied by cloning each {@link Hex} object individually.</li>
     *     <li>{@code moveQueues} is shallowly copied at the array level, because {@link Piece} instances are passed by reference.</li>
     *     <li>{@code currentEngine} and {@code currentQueue} are deep cloned using their respective {@code clone()} methods.</li>
     * </ul>
     * <p>
     * If the original game was marked as complete (via {@code completeGame()}), this state is also replicated in the clone.
     *
     * @return a deep copied new {@code HexLogger} instance contain the exact same information as this one
     * @since 1.3
     */
    public HexLogger clone(){
        // Setup
        HexLogger newLogger;
        try {
            newLogger = (HexLogger)super.clone();
        } catch (CloneNotSupportedException e) {
            newLogger = new HexLogger(getDataFileName());
            newLogger.setPlayer(getPlayer(), getPlayerID());
            newLogger.setTurn(getTurn());
            newLogger.setScore(getScore());
        }
        // Copy ArrayLists
        newLogger.movePieces.addAll(this.movePieces); // Integer, shallow is ok
        for (Hex hex : this.moveOrigins){
            newLogger.moveOrigins.add(hex.clone());
        }
        for (Piece[] queues : this.moveQueues){
            newLogger.moveQueues.add(queues.clone());
        }

        // Deep engine and queue information
        newLogger.currentEngine = this.currentEngine.clone();
        newLogger.currentQueue = this.currentQueue.clone();
        // Score and complete lock
        if (completed) newLogger.completeGame();
        return newLogger;
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
    /**
     * Generate loggers for every game logged in binary format the data directory.
     *
     * @return a list of {@code HexLogger}s representing the .hpyhex files found in the directory;
     *         returns an empty list if none are found or if the directory is invalid
     * @see #findGameBinaryFiles()
     */
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
        moveQueues = new ArrayList<Piece[]>();
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
        Path filePath = Paths.get(dataFile + ".hpyhex");
        try {
            Files.deleteIfExists(filePath);
            System.out.println(generateSimpleTime() + " HexLogger: Data file " + dataFile + " deleted successfully.");
            success = true;
        } catch (IOException e) {}
        return success;
    }

    /**
     * Create a unique filename with the naming format {@code HTHTHTHTHTHTHTHT}.
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
            int shift = (15 - i) * 4;
            if (i % 2 == 0) {
                stringBuilder.append(Integer.toHexString((int) ((LongHash >> shift) & 0xF)).toUpperCase());
            } else {
                stringBuilder.append(Integer.toHexString((int) ((time >> shift) & 0xF)).toUpperCase());
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Writes to a binary file using the {@code hex.binary} format.
     * This includes the basic game information, the current {@link HexEngine} statues, the current
     * {@link Piece} queue statues, and the moves in the game.
     * @throws IOException If binary file writing fails.
     * @see #read()
     * @see #write(String)
     */
    public void write() throws IOException {
        write("hex.binary");
    }

    /**
     * Constructs a hexadecimal string from the current logger information and write to a binary file.
     * This includes the basic game information, the current {@link HexEngine} statues, the current
     * {@link Piece} queue statues, and the moves in the game. This uses the specified format passed in.
     * If the format is {@code hex.binary} or {@code hex.coloredbinary}, it out put a .hpyhex binary file.
     * @param format The format of the game data log. If not valid, writing will not occur.
     * @throws IOException If writing fails.
     * @see #read()
     * @see #write()
     */
    public void write(String format) throws IOException {
        if (format.equals("hex.binary") || format.equals("hex.coloredbinary")) {
            boolean colored = format.equals("hex.coloredbinary");
            HexDataWriter writer = HexDataFactory.createWriter(getDataFileName(), "hpyhex");
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
            if (colored) {
                writer.add(playerID);
                writer.add(formatStringToLength(player, 24));
            }
            writer.addDivider(2);
            writer.addHex(colored ? HexDataConverter.convertColoredEngine(currentEngine) : HexDataConverter.convertBooleanEngine(currentEngine));
            writer.addDivider(1);
            writer.add((short)currentQueue.length);
            for (Piece piece : currentQueue){
                writer.addHex(colored ? HexDataConverter.convertColoredPiece(piece) : HexDataConverter.convertBooleanPiece(piece));
            }
            writer.addDivider(1);
            int totalMoves = moveOrigins.size();
            for (int i = 0; i < totalMoves; i ++) {
                writer.addHex(HexDataConverter.convertHex(moveOrigins.get(i)));
                writer.add((byte)(int)(movePieces.get(i)));
                for (Piece piece : moveQueues.get(i)) {
                    writer.addHex(colored ? HexDataConverter.convertColoredPiece(piece) : HexDataConverter.convertBooleanPiece(piece));
                }
            }
            writer.addDivider(2);
            writer.add((short) (obfuscate(ID) << 5));
            HexDataFactory.write(writer);
        } else {
            throw new IOException("Unsupported format used for writing");
        }
    }

    /**
     * Reads a binary log file and parses it into memory.
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from binary data.
     * <p>
     * For writing binary data, use {@code write("hex.binary")}.
     * @throws IOException If reading or parsing fails, or data is corrupted.
     * @deprecated Use {@link #read()} directly instead.
     * @since 1.3
     */
    @Deprecated
    public void readBinary() throws IOException {read();}

    /**
     * Reads a log file and parses it into memory. Reads both "hex.binary" and "hex.coloredbinary".
     * This populates the {@code engine}, {@code queue}, {@code moves}, and other game data from binary data.
     * <p>
     * For writing binary data, use {@link #write(String)}.
     * <p>
     * Since Version 1.4, this method delegates to {@link #readAndCheck} with all requirements being null.
     * @throws IOException If reading or parsing fails, or data is corrupted.
     * @since 1.3
     */
    public void read() throws IOException {
        readAndCheck(null, null, null, null, null, null, null, null);
    }
    /**
     * Reads a log file and parses it into memory, checking values against provided parameters as soon as they are read.
     * Supports both "hex.binary" and "hex.coloredbinary" formats. Populates {@code engine}, {@code queue}, {@code moves},
     * and other game data from binary data. Terminates early by throwing IOException if any required parameter does not
     * match or if the format does not match the required format.
     * <p>
     * For writing binary data, use {@link #write(String)}.
     *
     * @param formatRequired           The required file format ("hex.binary" or "hex.coloredbinary"). If non-null, parsing fails if the format does not match.
     * @param completionStatusRequired The required completion status. If non-null, parsing fails if the status does not match.
     * @param turnRequired             The required turn count. If non-null, parsing fails if the turn count does not match.
     * @param scoreRequired            The required score. If non-null, parsing fails if the score does not match.
     * @param playerIDRequired         The required player ID. If non-null, parsing fails if the player ID does not match.
     * @param playerNameRequired       The required player name. If non-null, parsing fails if the player name does not match.
     * @param engineRadiusRequired     The required engine radius. If non-null, parsing fails if the radius does not match.
     * @param queueSizeRequired        The required queue size. If non-null, parsing fails if the queue size does not match.
     * @throws IOException If reading or parsing fails, data is corrupted, or any required parameter does not match.
     * @since 1.4
     */
    public void readAndCheck(String formatRequired, Boolean completionStatusRequired,
                             Integer turnRequired, Integer scoreRequired,
                             Long playerIDRequired, String playerNameRequired,
                             Integer engineRadiusRequired, Integer queueSizeRequired)
            throws IOException {
        if (formatRequired != null && !formatRequired.equals("hex.binary") && !formatRequired.equals("hex.coloredbinary")) {
            throw new IOException("Unsupported format used for reading");
        }
        HexDataReader reader = HexDataFactory.read(getDataFileName(), "hpyhex");
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
        if (turnRequired != null && turnRequired != turnData){
            throw new IOException("Turn count does not match required value: expected " + turnRequired + ", found " + turnData);
        }
        int scoreData = reader.nextInt();
        if (scoreRequired != null && scoreRequired != scoreData){
            throw new IOException("Score does not match required value: expected " + scoreRequired + ", found " + scoreData);
        }
        boolean completeBooleanData = reader.nextBoolean();
        if (completionStatusRequired != null && completionStatusRequired != completeBooleanData) {
            throw new IOException("Completion status does not match required value: expected " + completionStatusRequired + ", found " + completeBooleanData);
        }
        // try encoding
        long obfScore = obfuscate(interleaveIntegers(scoreData * scoreData, id ^ turnData));
        long obfTurn = obfuscate(interleaveIntegers(turnData * turnData, id ^ scoreData));
        long obfCombined = ((obfTurn << 32) | (obfScore & 0xFFFFFFFFL));
        obfCombined = obfuscate(id * 43L ^ obfCombined ^ obfTurn) ^ obfScore;
        if (obfCombined != code){
            throw new IOException("Fail to read binary data because file data encoding is corrupted or version is not supported");
        }
        long playerIDData;
        String playerNameData;
        if (!reader.get(reader.pointer(), 4).equals("FFFF")) {
            // If is colored binary including player info
            playerIDData = reader.nextLong();
            if (playerIDRequired != null && playerIDRequired != playerIDData) {
                throw new IOException("Player ID does not match required value: expected " + playerIDRequired + ", found " + playerIDData);
            }
            playerNameData = reader.nextString(24).trim();
            if (playerNameRequired != null && !playerNameRequired.equals(playerNameData)) {
                throw new IOException("Player name does not match required value: expected " + playerNameRequired + ", found " + playerNameData);
            }
            if (!reader.next(4).equals("FFFF")) {
                throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
            }
        } else {
            if (playerIDRequired != null) {
                throw new IOException("Player ID does not match required value: expected " + playerIDRequired + ", not found");
            } else if (playerNameRequired != null) {
                throw new IOException("Player name does not match required value: expected " + playerNameRequired + ", not found");
            }
            playerIDData = this.playerID;
            playerNameData = this.player;
            reader.advance(4);
        }
        int startingDataPointerPosition = reader.pointer();
        HexDataReader clonedReader = HexDataFactory.cloneReader(reader);
        HexEngine engineData;
        Piece[] queueData;
        ArrayList<Hex> moveOriginsData = new ArrayList<Hex>(turnData);
        ArrayList<Piece[]> moveQueuesData = new ArrayList<Piece[]>(turnData);
        ArrayList<Integer> movePiecesData = new ArrayList<Integer>(turnData);
        // Attempt uncolored
        try {
            if (formatRequired != null && !formatRequired.equals("hex.binary")){
                throw new IOException("Format binary is skipped in reading attempt");
            }
            clonedReader.advance(startingDataPointerPosition);
            // Read engine
            try {
                int radius = clonedReader.getShort(clonedReader.pointer());
                if (engineRadiusRequired != null && engineRadiusRequired != radius) {
                    throw new IOException("Engine radius does not match required value: expected " + engineRadiusRequired + ", found " + radius);
                }
                int l = (3 * (radius) * (radius - 1)) / 4 + 5;
                engineData = HexDataConverter.convertEngine(clonedReader.next(l));
            } catch (IOException e) {
                throw new IOException("Fail to read engine data in binary data because " + e.getMessage());
            }
            if (!clonedReader.next(2).equals("FF")) {
                throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
            }
            // Read queue
            try {
                int length = clonedReader.nextShort();
                if (queueSizeRequired != null && queueSizeRequired != length) {
                    throw new IOException("Queue size does not match required value: expected " + queueSizeRequired + ", found " + length);
                }
                queueData = new Piece[length];
                for (int i = 0; i < length; i++) {
                    queueData[i] = HexDataConverter.convertPiece(clonedReader.next(2));
                }
            } catch (IOException e) {
                throw new IOException("Fail to read queue data in binary data because " + e.getMessage());
            }
            if (!clonedReader.next(2).equals("FF")) {
                throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
            }
            // Read moves
            for (int i = 0; i < turnData; i++) {
                try {
                    moveOriginsData.add(HexDataConverter.convertHex(clonedReader.next(8)));
                } catch (IOException e) {
                    throw new IOException("Fail to read coordinate data in move data in binary data at index " + i);
                }
                int index = clonedReader.nextByte();
                if (index < 0 || index >= queueData.length) {
                    throw new IOException("Fail to read piece index data in move data in binary data at index " + i);
                } else movePiecesData.add(index);
                Piece[] moveQueueData;
                try {
                    moveQueueData = new Piece[queueData.length];
                    for (int j = 0; j < queueData.length; j++) {
                        moveQueueData[j] = HexDataConverter.convertPiece(clonedReader.next(2));
                    }
                } catch (IOException e) {
                    throw new IOException("Fail to read piece queue data in move data in binary data at index " + i + " because " + e.getMessage());
                }
                moveQueuesData.add(moveQueueData.clone());
            }
            if (!clonedReader.next(4).equals("FFFF")) {
                throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
            }
        } catch (Exception exceptionReadingUncolored) {
            if (formatRequired != null && !formatRequired.equals("hex.coloredbinary")){
                throw new IOException("Format coloredbinary is skipped in reading attempt");
            }
            // If fails, also attempt colored
            clonedReader = HexDataFactory.cloneReader(reader);
            clonedReader.advance(startingDataPointerPosition);
            moveOriginsData = new ArrayList<Hex>(turnData);
            moveQueuesData = new ArrayList<Piece[]>(turnData);
            movePiecesData = new ArrayList<Integer>(turnData);
            try {
                // Read engine
                try {
                    int radius = clonedReader.getShort(clonedReader.pointer());
                    if (engineRadiusRequired != null && engineRadiusRequired != radius) {
                        throw new IOException("Engine radius does not match required value: expected " + engineRadiusRequired + ", found " + radius);
                    }
                    int c = 3 * (radius) * (radius - 1);
                    int l = c + c / 4 + 6;
                    engineData = HexDataConverter.convertEngine(clonedReader.next(l));
                } catch (IOException e) {
                    throw new IOException("Fail to read engine data in binary data because " + e.getMessage());
                }
                if (!clonedReader.next(2).equals("FF")) {
                    throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
                }
                // Read queue
                try {
                    int length = clonedReader.nextShort();
                    if (queueSizeRequired != null && queueSizeRequired != length) {
                        throw new IOException("Queue size does not match required value: expected " + queueSizeRequired + ", found " + length);
                    }
                    queueData = new Piece[length];
                    for (int i = 0; i < length; i++) {
                        queueData[i] = HexDataConverter.convertPiece(clonedReader.next(3));
                    }
                } catch (IOException e) {
                    throw new IOException("Fail to read queue data in binary data because " + e.getMessage());
                }
                if (!clonedReader.next(2).equals("FF")) {
                    throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
                }
                // Read moves
                for (int i = 0; i < turnData; i++) {
                    try {
                        moveOriginsData.add(HexDataConverter.convertHex(clonedReader.next(8)));
                    } catch (IOException e) {
                        throw new IOException("Fail to read coordinate data in move data in binary data at index " + i);
                    }
                    int index = clonedReader.nextByte();
                    if (index < 0 || index >= queueData.length) {
                        throw new IOException("Fail to read piece index data in move data in binary data at index " + i);
                    } else movePiecesData.add(index);
                    Piece[] moveQueueData;
                    try {
                        moveQueueData = new Piece[queueData.length];
                        for (int j = 0; j < queueData.length; j++) {
                            moveQueueData[j] = HexDataConverter.convertPiece(clonedReader.next(3));
                        }
                    } catch (IOException e) {
                        throw new IOException("Fail to read piece queue data in move data in binary data at index " + i + " because " + e.getMessage());
                    }
                    moveQueuesData.add(moveQueueData.clone());
                }
                if (!clonedReader.next(4).equals("FFFF")) {
                    throw new IOException("Fail to read binary data because file data divider cannot be found at the correct position");
                }
            } catch (Exception exceptionReadingColored) {
                throw new IOException("Fail to read binary data in both uncolored and colored format because in uncolored format, "
                        + exceptionReadingUncolored.getMessage() + "; in colored format, " + exceptionReadingColored.getMessage());
            }
        } finally {
            reader.advance(clonedReader.pointer() - startingDataPointerPosition);
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
        playerID = playerIDData;
        player = playerNameData;
    }
}
