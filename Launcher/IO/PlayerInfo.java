package Launcher.IO;

import javax.json.*;

/**
 * The {@code PlayerInfo} class represents persistent statistics about a player.
 * This class is immutable in part and serializable via the {@link JsonConvertible} interface.
 * <p>
 * It stores references to various key player information such as:
 * <ul>
 *     <li>{@link Username} - the player name</li>
 *     <li>{@code playerID} - the player ID</li>
 *     <li>{@code highTurn} - the highest turns the player has played in a game</li>
 *     <li>{@code highScore} - the highest score the player has obtained in a game</li>
 *     <li>{@code recentTurn} - the total turns in the most recent game</li>
 *     <li>{@code recentScore} - the total score as a result of the player's most recent game</li>
 *     <li>{@code totalTurn} - the total turns in all games of the player, not stored in JSON</li>
 *     <li>{@code totalScore} - the total score in all games of the player, not stored in JSON</li>
 *     <li>{@code numberOfGames} - the number of games the player has played, not stored in JSON</li>
 *     <li>{@link GameTime time} - the timestamp of the player's last game</li>
 * </ul>
 * <p>
 * {@link #toJsonObjectBuilder()} convert it to an {@code JsonObjectBuilder};
 *
 * @see JsonObjectBuilder
 * @see JsonConvertible
 * @see java.time.LocalDateTime
 * @see Username
 * @see GameTime
 */
public final class PlayerInfo implements JsonConvertible{
    private Username player;
    private int highTurn;
    private int highScore;
    private int recentTurn;
    private int recentScore;
    private int totalTurn;
    private int totalScore;
    private int numberOfGames;
    private long playerID;
    private GameTime time;

    /**
     * Constructs a {@code PlayerInfo} object using numerical ID and {@link Username}.
     *
     * @param highTurn    the highest number of turns achieved
     * @param highScore   the highest score achieved
     * @param recentTurn  the number of turns in the most recent game
     * @param recentScore the score in the most recent game
     * @param totalTurn     the total number of turns of all the games of the player
     * @param totalScore    the total game score of all the games of the player
     * @param playerID    the player's ID (long form)
     * @param player      the player's username
     * @see Username#getUsername(String)
     */
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore, int totalTurn, int totalScore, long playerID, Username player){
        this.setPlayer(player, playerID);
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.totalTurn = totalTurn;
        this.totalScore = totalScore;
        this.numberOfGames = 0;
        this.time = new GameTime();
    }
    /**
     * Constructs a {@code PlayerInfo} object from string-formatted player ID and {@link GameTime}.
     *
     * @param highTurn    the highest number of turns achieved
     * @param highScore   the highest score achieved
     * @param recentTurn  the number of turns in the most recent game
     * @param recentScore the score in the most recent game
     * @param totalTurn     the total number of turns of all the games of the player
     * @param totalScore    the total game score of all the games of the player
     * @param time        the time metadata
     * @param playerID    the player's ID in hex string format
     * @param player      the player's username
     * @see Long#parseUnsignedLong(String, int)
     */
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore, int totalTurn, int totalScore, GameTime time, String playerID, Username player){
        long ID = Long.parseUnsignedLong(playerID, 16);
        this.setPlayer(player, ID);
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.totalTurn = totalTurn;
        this.totalScore = totalScore;
        this.numberOfGames = 0;
        this.time = time;
    }

    /**
     * Gets the {@link Username} of the player.
     * @return the player’s {@code Username}
     */
    public Username getPlayer() {return player;}
    /**
     * Returns the player ID (long) matching the player.
     * @return the player’s ID
     */
    public long getPlayerID(){return playerID;}

    /**
     * Assigns a {@link Username} and corresponding player ID.
     * Fallbacks to "Guest" (ID = {@code -1}) if input is not present or invalid.
     *
     * @param player the player name
     * @param ID     the player ID
     */
    public void setPlayer(Username player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = Username.getUsername("Guest");
            this.playerID = -1;
        } else {
            this.player = player;
            this.playerID = ID;
        }
    }

    /** @return the highest number of turns achieved by the player*/
    public int getHighTurn() {return highTurn;}
    /** @return the highest score achieved by the player*/
    public int getHighScore() {return highScore;}
    /** @return the number of turns in the most recent game of the player*/
    public int getRecentTurn() {return recentTurn;}
    /** @return the final score in the most recent game of the player*/
    public int getRecentScore() {return recentScore;}
    /** @return the average number of turns of the player*/
    public double getAvgTurn() {
        if(numberOfGames == 0)return totalTurn;
        return (double) totalTurn / numberOfGames;
    }
    /** @return the average score of the player*/
    public double getAvgScore() {
        if(numberOfGames == 0)return totalScore;
        return (double) totalScore / numberOfGames;
    }
    /** @return the number of games played by player*/
    public int getNumberOfGames() {return numberOfGames;}

    /** @param turns set the highest number of turns */
    public void setHighTurn(int turns) {this.highTurn = turns;}
    /** @param score set the highest score */
    public void setHighScore(int score) {this.highScore = score;}
    /** @param turns set the most recent game's turns */
    public void setRecentTurn(int turns) {this.recentTurn = turns;}
    /** @param score set the most recent game's score */
    public void setRecentScore(int score) {this.recentScore = score;}
    /** @param turns add the average game turns */
    public void addTotalTurn(int turns) {this.totalTurn += turns;}
    /** @param score add the average game score */
    public void addTotalScore(int score) {this.totalScore += score;}
    /** Increment the number of games */
    public void incrementGameNumber() {this.numberOfGames++;}
    /** Erase all score date of this player */
    public void eraseStats(){
        this.totalTurn = 0;
        this.totalScore = 0;
        this.highTurn = 0;
        this.highScore = 0;
        this.recentTurn = 0;
        this.recentScore = 0;
        this.numberOfGames = 0;
    }

    /**
     * Updates the high score and high turn, number of games, and average stats based on recent results.
     * This method is intended to be called after a game session ends.
     * This method will automatically update the timestamp.
     */
    public void update(){
        if(highTurn < recentTurn){
            highTurn = recentTurn;
        }
        if(highScore < recentScore){
            highScore = recentScore;
        }
        numberOfGames ++;
        totalTurn += recentTurn;
        totalScore += recentScore;
        this.time = new GameTime();
    }

    /**
     * Returns a human-readable summary of the player’s statistics.
     *
     * @return string representation of this object
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "PlayerInfo[Player = " + player +
                ", Highest Turn = " + highTurn +
                ", Highest Score = " + highScore +
                ", Recent Turn = " + recentTurn +
                ", Recent Score = " + recentScore +
                ", Total Turn = " + totalTurn +
                ", Total Score = " + totalScore +
                ", Number of Games = " + numberOfGames +
                ", Player ID = " + playerID +
                ", Time = " + time + "]";
    }

    /**
     * Converts this {@code PlayerInfo} object into a {@link JsonObjectBuilder},
     * including all player information and nested scoring objects.
     * <p>
     * The resulting JSON object includes:
     * <ul>
     *     <li>{@code Player}</li>
     *     <li>{@code PlayerID}</li>
     *     <li>{@code Time}</li>
     *     <li>{@code Highest} - a nested object with {@code highestScore} and {@code highestTurn}</li>
     *     <li>{@code Recent} - a nested object with {@code recentScore} and {@code recentTurn}</li>
     * </ul>
     * Average turns and scores are not recorded.
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
        builder.add("Player", player.toString());
        builder.add("PlayerID", Long.toHexString(playerID));
        builder.add("Time", time.toJsonObject());
        JsonObject scoreElement = Json.createObjectBuilder().add("Score", highScore).add("Turn", highTurn).build();
        builder.add("Highest", scoreElement);
        scoreElement = Json.createObjectBuilder().add("Score", recentScore).add("Turn", recentTurn).build();
        builder.add("Recent", scoreElement);
        return builder;
    }
}
