package Launcher;

import GUI.GameEssentials;
import Launcher.IO.*;

import java.awt.*;
import java.io.IOException;

/**
 * The {@link LaunchEssentials} class provides essential launcher utilities.
 * This class is final and cannot be extended.
 */
public final class LaunchEssentials {
    // Program info
    public static final GameVersion currentGameVersion = new GameVersion(0, 3, 3);
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Game info
    private static PlayerInfo currentPlayerInfo = new PlayerInfo(0, 0, 0, 0);
    private static GameInfo currentGameInfo;
    private static boolean gameStarted = false;

    //GUI
    // Launcher
    public static final Color launchBackgroundColor = GameEssentials.gameBackGroundColor;
    public static final Color launchTitleFontColor = new Color(0, 73, 54);
    public static final Color launchAuthorFontColor = new Color(0, 73, 54);
    public static final Color launchStartButtonFontColor = Color.WHITE;
    public static final Color launchStartButtonBackgroundColor = Color.BLACK;
    public static final String launchTitleFont = "Courier";
    public static final String launchVersionFont = "Times New Roman";
    public static final String launchAuthorFont = "Times New Roman";
    public static final String launchStartButtonFont = "Times New Roman";

    public static boolean isGameStarted(){
        return gameStarted;
    }
    public static void startGame(){
        gameStarted = true;
        // Do game starting
    }
    public static void endGame(){
        gameStarted = false;
    }
    public static PlayerInfo currentPlayerInfo(){
        return currentPlayerInfo;
    }
    public static void setCurrentPlayer(String currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayerInfo.setPlayer(currentPlayer, currentPlayerID);
        LaunchEssentials.currentGameInfo.setPlayer(currentPlayer, currentPlayerID);
    }
    public static void initializeCurrentGame(GameMode mode){
        currentGameInfo = new GameInfo(mode);
        gameStarted = false;
    }
    public static void fetchGameInfo(){
        currentGameInfo = new GameInfo(GameEssentials.turn, GameEssentials.score, Long.toHexString(currentGameInfo.getPlayerID()), currentGameInfo.getPlayer(), new GameTime(), Long.toHexString(currentGameInfo.getGameID()), currentGameInfo.getGameMode(), currentGameVersion);
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
