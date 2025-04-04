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
    public static final GameVersion currentGameVersion = new GameVersion(0, 4, 1);
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Game info
    private static PlayerInfo currentPlayerInfo = new PlayerInfo(0, 0, 0, 0);
    private static GameInfo currentGameInfo;
    private static boolean gameStarted = false;

    //GUI
    // Launcher
    public static final Color launchBackgroundColor = new Color(241, 243, 213);
    public static final Color launchTitlePanelBackgroundColor = new Color(219, 223, 151);
    public static final Color launchAuthorFontColor = new Color(0, 73, 54);
    public static final Color launchWWFontColor = Color.BLACK;
    public static final Color launchVersionFontColor = Color.BLACK;
    public static final Color launchStartButtonBackgroundColor = Color.BLACK;
    public static final Color launchQuitButtonBackgroundColor = Color.RED;
    public static final Color launchConfirmButtonBackgroundColor = new Color(0, 223, 39);
    public static final String launchTitleFont = "Courier";
    public static final String launchVersionFont = "Comic Sans MS";
    public static final String launchAuthorFont = "Helvetica";
    public static final String launchWWFont = "Georgia";
    public static final String launchButtonFont = "Times New Roman";
    public static final String launchEnterUsernameFont = "Courier";
    public static final String launchSettingsFont = "Courier";
    public static final String launchSettingsSlidingButtonFont = "Helvetica";

    public static boolean isGameStarted(){
        return gameStarted;
    }
    public static void startGame(){
        gameStarted = true;
        if (currentGameInfo.getGameMode() == GameMode.Small){
            GameEssentials.initialize(5, 3, 100, false, LauncherGUI.mainFrame);
        } else if (currentGameInfo.getGameMode() == GameMode.Medium){
            GameEssentials.initialize(8, 5, 100, false, LauncherGUI.mainFrame);
        } else if (currentGameInfo.getGameMode() == GameMode.Large){
            GameEssentials.initialize(11, 7, 100, false, LauncherGUI.mainFrame);
        } else if (currentGameInfo.getGameMode() == GameMode.SmallEasy){
            GameEssentials.initialize(5, 3, 100, true, LauncherGUI.mainFrame);
        } else if (currentGameInfo.getGameMode() == GameMode.MediumEasy){
            GameEssentials.initialize(8, 5, 100, true, LauncherGUI.mainFrame);
        } else if (currentGameInfo.getGameMode() == GameMode.LargeEasy){
            GameEssentials.initialize(11, 7, 100, true, LauncherGUI.mainFrame);
        } else if(currentGameInfo.getGameMode() == GameMode.Unspecified){
            System.err.println("Legacy GameMode.Unspecified GameMode unsupported since Version 0.4.1");
            GameEssentials.initialize(5, 3, 100, false, LauncherGUI.mainFrame);
        } else {
            System.err.println("Unknown GameMode detected.");
            GameEssentials.initialize(5, 3, 100, false, LauncherGUI.mainFrame);
        }
    }
    public static void endGame(){
        gameStarted = false;
    }
    public static void setCurrentPlayer(Username currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayerInfo.setPlayer(currentPlayer, currentPlayerID);
        LaunchEssentials.currentGameInfo.setPlayer(currentPlayer, currentPlayerID);
    }
    public static String getCurrentPlayer(){
        return currentGameInfo.getPlayer().toString();
    }
    public static void initialize(){
        currentGameInfo = new GameInfo(GameMode.Small);
        gameStarted = false;
    }
    public static void setEasyMode(){
        if (currentGameInfo.getGameMode() == GameMode.Small){
            currentGameInfo.setGameMode(GameMode.SmallEasy);
        } else if (currentGameInfo.getGameMode() == GameMode.Medium){
            currentGameInfo.setGameMode(GameMode.MediumEasy);
        } else if (currentGameInfo.getGameMode() == GameMode.Large){
            currentGameInfo.setGameMode(GameMode.LargeEasy);
        }
    }
    public static void setNormalMode(){
        if (currentGameInfo.getGameMode() == GameMode.SmallEasy){
            currentGameInfo.setGameMode(GameMode.Small);
        } else if (currentGameInfo.getGameMode() == GameMode.MediumEasy){
            currentGameInfo.setGameMode(GameMode.Medium);
        } else if (currentGameInfo.getGameMode() == GameMode.LargeEasy){
            currentGameInfo.setGameMode(GameMode.Large);
        }
    }
    public static void setSmallMode(){
        GameMode mode = currentGameInfo.getGameMode();
        if (mode == GameMode.SmallEasy || mode == GameMode.MediumEasy || mode == GameMode.LargeEasy){
            currentGameInfo.setGameMode(GameMode.SmallEasy);
        } else currentGameInfo.setGameMode(GameMode.Small);
    }
    public static void setMediumMode(){
        GameMode mode = currentGameInfo.getGameMode();
        if (mode == GameMode.SmallEasy || mode == GameMode.MediumEasy || mode == GameMode.LargeEasy){
            currentGameInfo.setGameMode(GameMode.MediumEasy);
        } else currentGameInfo.setGameMode(GameMode.Medium);
    }
    public static void setLargeMode(){
        GameMode mode = currentGameInfo.getGameMode();
        if (mode == GameMode.SmallEasy || mode == GameMode.MediumEasy || mode == GameMode.LargeEasy){
            currentGameInfo.setGameMode(GameMode.LargeEasy);
        } else currentGameInfo.setGameMode(GameMode.Large);
    }
    public static boolean isEasyMode(){
        return GameMode.isEasy(currentGameInfo.getGameMode());
    }
    public static boolean isSmallMode(){
        return currentGameInfo.getGameMode() == GameMode.Small || currentGameInfo.getGameMode() == GameMode.SmallEasy;
    }
    public static boolean isMediumMode(){
        return currentGameInfo.getGameMode() == GameMode.Medium || currentGameInfo.getGameMode() == GameMode.MediumEasy;
    }
    public static boolean isLargeMode(){
        return currentGameInfo.getGameMode() == GameMode.Large || currentGameInfo.getGameMode() == GameMode.LargeEasy;
    }
    public static void fetchGameInfo(){
        currentGameInfo = new GameInfo(GameEssentials.getTurn(), GameEssentials.getScore(), Long.toHexString(currentGameInfo.getPlayerID()), currentGameInfo.getPlayer(), new GameTime(), Long.toHexString(currentGameInfo.getGameID()), currentGameInfo.getGameMode(), currentGameVersion);
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
        try {
            LaunchLogger.read();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        updateRecent();
        updateHighest();
        LaunchLogger.addGame(currentGameInfo);
        try {
            LaunchLogger.write();
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
