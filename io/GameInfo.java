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

/**
 * Represents game session information such as player identity, score, turn, mode, and versioning.
 * This class is immutable in part and serializable via the {@link JsonConvertible} interface.
 * This class is completely thread safe and can be used across different threads.
 * <p>
 * {@code GameInfo} objects can either be {@link #GameInfo(GameMode, GameVersion) created with default values}
 * as if it was an unspecified guest player, or via the fully populated
 * {@link #GameInfo(int, int, String, Username, GameTime, String, GameMode, GameVersion) constructor}.
 * <p>
 * It stores references to various key components such as:
 * <ul>
 *     <li>{@link Username} - the player name</li>
 *     <li>{@code playerID} - the player ID</li>
 *     <li>{@code turn} - the total turns the game lasted</li>
 *     <li>{@code score} - the total score of this game session</li>
 *     <li>{@link GameTime} - the timestamp associated with the game session</li>
 *     <li>{@link GameMode} - the gameplay mode in use</li>
 *     <li>{@link GameVersion} - the version of the game client in use</li>
 *     <li>{@code GameID} - the ID number of the game</li>
 * </ul>
 * <p>
 * {@link #toJsonObjectBuilder()} convert it to an {@code JsonObjectBuilder};
 *
 * @see JsonConvertible
 * @see JsonObjectBuilder
 * @since 1.0
 * @author William Wu
 * @version 1.4
 */
public final class GameInfo implements JsonConvertible{
    private Username player;
    private int turn;
    private int score;
    private long playerID;
    private final GameTime time;
    private final long gameID;
    private GameMode gameMode;
    private final GameVersion gameVersion;
    private final Object lock = new Object();

    /**
     * Create a new {@code GameInfo} with default player and a specified {@link GameMode}.
     * This constructor initializes the player as {@code "Guest"} (ID = {@code -1}), generates a new {@code GameID},
     * and sets the game version.
     *
     * @param mode the {@link GameMode} to initialize with
     * @param version    the {@link GameVersion} in use
     */
    public GameInfo(GameMode mode, GameVersion version) {
        this.turn = 0;
        this.score = 0;
        this.playerID = -1;
        this.player = Username.getUsername("Guest");
        this.time = new GameTime();
        this.gameID = LaunchLogger.generateHash(0);
        this.gameMode = mode;
        this.gameVersion = version;
    }
    /**
     * Constructs a fully populated {@code GameInfo} object, typically used for deserialization.
     *
     * @param turn       the total number of turns
     * @param score      the player’s total score
     * @param playerID   the hex-encoded player ID in String format
     * @param player     the player's {@link Username}
     * @param time       the {@link GameTime} of the game
     * @param gameID     the hex-encoded game session ID in String format
     * @param mode       the {@link GameMode} in use
     * @param version    the {@link GameVersion} in use
     */
    public GameInfo(int turn, int score, String playerID, Username player, GameTime time, String gameID, GameMode mode, GameVersion version){
        long ID = Long.parseUnsignedLong(playerID, 16);
        this.setPlayer(player, ID);
        this.turn = turn;
        this.score = score;
        this.time = time;
        this.gameID = Long.parseUnsignedLong(gameID, 16);
        this.gameMode = mode;
        this.gameVersion = version;
    }

    /**
     * Gets the {@link Username} of the current player.
     * @return the player’s {@code Username}
     */
    public Username getPlayer() {
        synchronized (lock){
            return player;
        }
    }
    /**
     * Returns the player ID (long) matching the player.
     * @return the player’s ID
     */
    public long getPlayerID(){
        synchronized (lock) {
            return playerID;
        }
    }
    /**
     * Returns the game session ID (long).
     * @return the game session ID
     */
    public long getGameID(){
        synchronized (lock) {
            return gameID;
        }
    }
    /**
     * Gets the {@link GameMode} currently in use.
     * @return the active game mode
     * @see GameMode
     */
    public GameMode getGameMode(){
        synchronized (lock) {
            return gameMode;
        }
    }

    /**
     * Assigns a {@link Username} and corresponding player ID.
     * Fallbacks to "Guest" (ID = {@code -1}) if input is not present or invalid.
     *
     * @param player the player name
     * @param ID     the player ID
     */
    public void setPlayer(Username player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            synchronized (lock) {
                this.player = Username.getUsername("Guest");
                this.playerID = -1;
            }
        } else {
            synchronized (lock) {
                this.player = player;
                this.playerID = ID;
            }
        }
    }
    /**
     * Sets the {@link GameMode} for this game.
     * @param gameMode the mode to set
     * @see GameMode
     */
    public void setGameMode(GameMode gameMode){
        synchronized (lock) {
            this.gameMode = gameMode;
        }
    }

    /**
     * Returns the current total turn number.
     * @return the total turn number
     */
    public int getTurn() {
        synchronized (lock) {
            return turn;
        }
    }
    /**
     * Returns the player’s total score.
     * @return the total score
     */
    public int getScore() {
        synchronized (lock) {
            return score;
        }
    }

    /**
     * Set the turns played in this game. This will unconditionally accept the new number of turns.
     * @param turns the new score
     */
    public void setTurn(int turns) {
        synchronized (lock){
            this.turn = turns;
        }
    }
    /**
     * Set the player’s score. This will unconditionally accept the new score.
     * @param score the new score
     */
    public void setScore(int score) {
        synchronized (lock) {
            this.score = score;
        }
    }

    /**
     * Returns a human-readable String representation of this game.
     *
     * @return string representation of this object
     * @see Object#toString()
     */
    @Override
    public String toString() {
        synchronized (lock){
            return "GameInfo[Player = " + player +
                ", Player ID = " + playerID +
                ", Turn = " + turn +
                ", Score = " + score +
                ", Game ID = " + gameID +
                ", Game Mode = " + gameMode.toString() +
                ", Game Version = " + gameVersion.toString() +
                ", Time = " + time + "]";
        }
    }

    /**
     * Converts this {@code GameInfo} object into a {@link JsonObjectBuilder},
     * including all game metadata and nested objects.
     * <p>
     * The resulting JSON object includes:
     * <ul>
     *     <li>{@code Player}</li>
     *     <li>{@code PlayerID}</li>
     *     <li>{@code GameID}</li>
     *     <li>{@code EasyMode}</li>
     *     <li>{@code Preset}</li>
     *     <li>{@code Version} (from {@link GameVersion#toJsonObject()})</li>
     *     <li>{@code Time} (from {@link GameTime#toJsonObject()})</li>
     *     <li>{@code Result} - a nested object with {@code Score} and {@code Turn}</li>
     * </ul>
     *
     * @return a {@code JsonObjectBuilder} for further chaining or building
     * @see JsonObject
     * @see JsonObjectBuilder
     * @see JsonConvertible
     * @see GameVersion#toJsonObject()
     * @see GameTime#toJsonObject()
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        synchronized (lock) {
            builder.add("Player", player.toString());
            builder.add("PlayerID", Long.toHexString(playerID));
            builder.add("GameID", Long.toHexString(gameID));
            builder.add("EasyMode", GameMode.isEasy(gameMode));
            builder.add("Preset", GameMode.getChar(gameMode) + "");
            builder.add("Version", gameVersion.toJsonObject());
            builder.add("Time", time.toJsonObject());
            JsonObject scoreElement = Json.createObjectBuilder().add("Score", score).add("Turn", turn).build();
            builder.add("Result", scoreElement);
        }
        return builder;
    }
}
