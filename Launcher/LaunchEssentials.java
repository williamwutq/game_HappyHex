package Launcher;

import GUI.GameEssentials;
import Launcher.IO.GameInfo;
import Launcher.IO.LaunchLogger;
import Launcher.IO.PlayerInfo;

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
    private static PlayerInfo currentPlayerInfo;

    public static PlayerInfo currentPlayerInfo(){
        return currentPlayerInfo;
    }
    public static void setCurrentPlayer(String currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayerInfo.setPlayer(currentPlayer, currentPlayerID);
    }
    public static void updateRecent(){
        currentPlayerInfo.setRecentTurn(GameEssentials.turn);
        currentPlayerInfo.setRecentScore(GameEssentials.score);
    }
    public static void updateHighest(){
        // Update the highest turn and score
        if(currentPlayerInfo.getPlayerID() != -1) {
            // Only if the player is logged in
            for (GameInfo info : LaunchLogger.fetchGames()) {
                if (info.getPlayerID() == currentPlayerInfo.getPlayerID()) {
                    if (info.getScore() > currentPlayerInfo.getHighScore()) {
                        currentPlayerInfo.setHighScore(info.getScore());
                    }
                    if (info.getTurn() > currentPlayerInfo.getHighTurn()) {
                        currentPlayerInfo.setHighTurn(info.getScore());
                    }
                }
            }
        }
        currentPlayerInfo.updateHigh();
    }

}
