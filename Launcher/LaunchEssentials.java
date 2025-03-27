package Launcher;

import GUI.GameEssentials;
import Launcher.IO.LaunchLogger;

/**
 * The {@link LaunchEssentials} class provides essential launcher utilities.
 * This class is final and cannot be extended.
 */
public final class LaunchEssentials {
    // Program info
    public static final int currentVersionMajor = 0;
    public static final int currentVersionMinor = 3;
    public static final int currentVersionPatch = 1;
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Game info
    private static String currentPlayer = "Guest";
    private static long currentPlayerID = -1;
    private static int highestTurn = 0;
    private static int highestScore = 0;
    private static int recentTurn = 0;
    private static int recentScore = 0;

    public static String getCurrentPlayer(){
        return currentPlayer;
    }
    public static long getCurrentPlayerID(){
        return currentPlayerID;
    }
    public static int getHighestTurn(){
        updateHighest();
        return highestTurn;
    }
    public static int getHighestScore(){
        updateHighest();
        return highestScore;
    }
    public static int getRecentTurn(){
        return recentTurn;
    }
    public static int getRecentScore(){
        return recentScore;
    }
    public static void setCurrentPlayer(String currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayer = currentPlayer;
        LaunchEssentials.currentPlayerID = currentPlayerID;
    }
    public static void updateRecent(){
        recentTurn = GameEssentials.turn;
        recentScore = GameEssentials.score;
    }
    public static void updateHighest(){
        // Update the highest turn and score
        if(currentPlayerID != -1) {
            // Only if the player is logged in
            for (GameInfo info : LaunchLogger.fetchGames()) {
                if (info.getPlayerID() == currentPlayerID) {
                    if (info.getScore() > highestScore) {
                        highestScore = info.getScore();
                    }
                    if (info.getTurn() > highestTurn) {
                        highestTurn = info.getTurn();
                    }
                }
            }
        }
        if(recentScore > highestScore){
            highestScore = recentScore;
        }
        if(recentTurn > highestTurn){
            highestTurn = recentTurn;
        }
    }

}
