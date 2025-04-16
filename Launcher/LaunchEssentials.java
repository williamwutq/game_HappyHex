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
    public static final GameVersion currentGameVersion = new GameVersion(1, 1, 1);
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Game info
    private static PlayerInfo currentPlayerInfo = new PlayerInfo(0, 0, 0, 0, 0, 0, -1, Username.getUsername("Guest"));
    private static GameInfo currentGameInfo;
    private static boolean gameStarted = false;

    // Special
    private static final special.SpecialFeature fontStyle = special.FeatureFactory.createFeature(Font.class.getName());

    //GUI
    // Launcher
    public static final Color launchBackgroundColor = GameEssentials.processColor(new Color(241, 243, 213));
    public static final Color launchTitlePanelBackgroundColor = GameEssentials.processColor(new Color(219, 223, 151));
    public static final Color launchAuthorFontColor = GameEssentials.processColor(new Color(0, 73, 54));
    public static final Color launchWWFontColor = GameEssentials.processColor(Color.BLACK);
    public static final Color launchVersionFontColor = GameEssentials.processColor(Color.BLACK);
    public static final Color launchPlayerNameFontColor = GameEssentials.processColor(new Color(136, 136, 0));
    public static final Color launchPlayerPromptFontColor = GameEssentials.processColor(new Color(0, 136, 0));
    public static final Color launchPlayerErrorFontColor = GameEssentials.processColor(new Color(136, 0, 0));
    public static final Color launchPlayerSpecialFontColor = GameEssentials.processColor(new Color(0, 136, 136));
    public static final Color launchStartButtonBackgroundColor = GameEssentials.processColor(Color.BLACK);
    public static final Color launchQuitButtonBackgroundColor = GameEssentials.processColor(Color.RED);
    public static final Color launchNewButtonBackgroundColor = GameEssentials.processColor(new Color(0, 193, 211));
    public static final Color launchConfirmButtonBackgroundColor = GameEssentials.processColor(new Color(0, 223, 39));
    public static final Color launchSlidingButtonOnColor = GameEssentials.processColor(Color.GREEN);
    public static final Color launchSlidingButtonOffColor = GameEssentials.processColor(Color.RED);
    public static final String launchTitleFont = (String) fontStyle.process(new Object[]{"Courier", "TitleFont"})[0];
    public static final String launchVersionFont = (String) fontStyle.process(new Object[]{"Comic Sans MS", "VersionFont"})[0];
    public static final String launchAuthorFont = "Helvetica";
    public static final String launchWWFont = "Georgia";
    public static final String launchButtonFont = (String) fontStyle.process(new Object[]{"Times New Roman", "ButtonFont"})[0];
    public static final String launchEnterUsernameFont = (String) fontStyle.process(new Object[]{"Courier", "MonoFont"})[0];
    public static final String launchSettingsFont = (String) fontStyle.process(new Object[]{"Courier", "MonoFont"})[0];
    public static final String launchSettingsSlidingButtonFont = (String) fontStyle.process(new Object[]{"Helvetica", "SlidingButtonFont"})[0];

    public static boolean isGameStarted(){
        return gameStarted;
    }
    public static void startGame(){
        gameStarted = true;
        int delay = 250;
        if (currentGameInfo.getGameMode() == GameMode.Small){
            GameEssentials.initialize(5, 3, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else if (currentGameInfo.getGameMode() == GameMode.Medium){
            GameEssentials.initialize(8, 5, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else if (currentGameInfo.getGameMode() == GameMode.Large){
            GameEssentials.initialize(11, 7, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else if (currentGameInfo.getGameMode() == GameMode.SmallEasy){
            GameEssentials.initialize(5, 3, delay, true, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else if (currentGameInfo.getGameMode() == GameMode.MediumEasy){
            GameEssentials.initialize(8, 5, delay, true, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else if (currentGameInfo.getGameMode() == GameMode.LargeEasy){
            GameEssentials.initialize(11, 7, delay, true, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else if(currentGameInfo.getGameMode() == GameMode.Unspecified){
            System.err.println("Legacy GameMode.Unspecified GameMode unsupported since Version 0.4.1");
            GameEssentials.initialize(5, 3, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer());
        } else {
            System.err.println("Unknown GameMode detected.");
            GameEssentials.initialize(5, 3, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer());
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
                        currentPlayerInfo.setHighTurn(info.getHighTurn());
                    }
                }
            }
        }
        currentPlayerInfo.updateHigh();
    }
    public static int getLastScore(){
        return currentGameInfo.getScore();
    }
    public static int getLastTurn(){
        return currentGameInfo.getTurn();
    }
    public static int getHighestScore(){
        return currentPlayerInfo.getHighScore();
    }
    public static int getHighestTurn(){
        return currentPlayerInfo.getHighTurn();
    }
    public static int getAverageScore(){
        return (int) Math.round(currentPlayerInfo.getAvgScore());
    }
    public static int getAverageTurn(){
        return (int) Math.round(currentPlayerInfo.getAvgTurn());
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
