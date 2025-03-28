package Launcher;

import GUI.GameEssentials;
import Launcher.IO.*;

import java.io.IOException;

/**
 * The {@link LaunchEssentials} class provides essential launcher utilities.
 * This class is final and cannot be extended.
 */
public final class LaunchEssentials {
    // Program info
    public static final GameVersion currentGameVersion = new GameVersion(0, 3, 2);
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Game info
    private static PlayerInfo currentPlayerInfo = new PlayerInfo(0, 0, 0, 0);
    private static GameInfo currentGameInfo = new GameInfo(GameMode.Medium);

    public static PlayerInfo currentPlayerInfo(){
        return currentPlayerInfo;
    }
    public static void setCurrentPlayer(String currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayerInfo.setPlayer(currentPlayer, currentPlayerID);
    }
    public static void fetchGameInfo(){
        currentGameInfo = new GameInfo(GameEssentials.turn, GameEssentials.score, GameMode.Medium, currentGameVersion);
    }
    public static void updateRecent(){
        currentPlayerInfo.setRecentTurn(currentGameInfo.getTurn());
        currentPlayerInfo.setRecentScore(currentGameInfo.getScore());
    }
    public static void updateHighest(){
        // Update the highest turn and score
        if(currentPlayerInfo.getPlayerID() != -1) {
            // Only if the player is logged in
            for (PlayerInfo info : LaunchLogger.fetchPlayerStats()) {
                if (info.getPlayerID() == currentPlayerInfo.getPlayerID()) {
                    if (info.getHighScore() > currentPlayerInfo.getHighScore()) {
                        currentPlayerInfo.setHighScore(info.getHighScore());
                    }
                    if (info.getHighTurn() > currentPlayerInfo.getHighTurn()) {
                        currentPlayerInfo.setHighTurn(info.getHighScore());
                    }
                }
            }
        }
        currentPlayerInfo.updateHigh();
    }

    public static boolean log(){
        try{
            LaunchLogger.read();
            updateRecent();
            updateHighest();
            LaunchLogger.addGame(currentGameInfo);
            LaunchLogger.write();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
