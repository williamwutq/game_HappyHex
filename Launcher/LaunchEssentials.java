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
    public static final GameVersion currentGameVersion = new GameVersion(1, 1, 2);
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Game info
    private static PlayerInfo currentPlayerInfo = new PlayerInfo(0, 0, 0, 0, 0, 0, -1, Username.getUsername("Guest"));
    private static GameInfo currentGameInfo;
    private static boolean gameStarted = false;

    // Special
    private static special.SpecialFeature fontStyle = special.FeatureFactory.createFeature(Font.class.getName());

    // Graphics Theme
    private static int themeIndex = 2;

    //GUI
    // Launcher
    public static Color launchBackgroundColor = GameEssentials.processColor(new Color(241, 243, 213), "LaunchBackgroundColor");
    public static Color launchTitlePanelBackgroundColor = GameEssentials.processColor(new Color(219, 223, 151), "LaunchTitlePanelBackgroundColor");
    public static Color launchAuthorFontColor = GameEssentials.processColor(new Color(0, 73, 54), "LaunchAuthorFontColor");
    public static Color launchWWFontColor = GameEssentials.processColor(Color.BLACK, "LaunchWWFontColor");
    public static Color launchVersionFontColor = GameEssentials.processColor(Color.BLACK, "LaunchVersionFontColor");
    public static Color launchHintFontColor = GameEssentials.processColor(Color.GRAY, "LaunchHintFontColor");
    public static Color launchPlayerNameFontColor = GameEssentials.processColor(new Color(136, 136, 0), "LaunchPlayerNameFontColor");
    public static Color launchPlayerPromptFontColor = GameEssentials.processColor(new Color(0, 136, 0), "LaunchPlayerPromptFontColor");
    public static Color launchPlayerErrorFontColor = GameEssentials.processColor(new Color(136, 0, 0), "LaunchPlayerErrorFontColor");
    public static Color launchPlayerSpecialFontColor = GameEssentials.processColor(new Color(0, 136, 136), "LaunchPlayerSpecialFontColor");
    public static Color launchStartButtonBackgroundColor = GameEssentials.processColor(Color.BLACK, "LaunchStartButtonBackgroundColor");
    public static Color launchQuitButtonBackgroundColor = GameEssentials.processColor(Color.RED, "LaunchQuitButtonBackgroundColor");
    public static Color launchNewButtonBackgroundColor = GameEssentials.processColor(new Color(0, 193, 211), "LaunchNewButtonBackgroundColor");
    public static Color launchConfirmButtonBackgroundColor = GameEssentials.processColor(new Color(0, 223, 39), "LaunchConfirmButtonBackgroundColor");
    public static Color launchSlidingButtonOnColor = GameEssentials.processColor(Color.GREEN, "LaunchSlidingButtonOnColor");
    public static Color launchSlidingButtonOffColor = GameEssentials.processColor(Color.RED, "LaunchSlidingButtonOffColor");
    public static String launchTitleFont = (String) fontStyle.process(new Object[]{"Courier", "TitleFont"})[0];
    public static String launchVersionFont = (String) fontStyle.process(new Object[]{"Comic Sans MS", "VersionFont"})[0];
    public static String launchAuthorFont = "Helvetica";
    public static String launchWWFont = "Georgia";
    public static String launchButtonFont = (String) fontStyle.process(new Object[]{"Times New Roman", "ButtonFont"})[0];
    public static String launchEnterUsernameFont = (String) fontStyle.process(new Object[]{"Courier", "MonoFont"})[0];
    public static String launchSettingsFont = (String) fontStyle.process(new Object[]{"Courier", "MonoFont"})[0];
    public static String launchSettingsSlidingButtonFont = (String) fontStyle.process(new Object[]{"Helvetica", "SlidingButtonFont"})[0];

    public static void setTheme(int featureIndex){
        themeIndex = featureIndex;
    }
    public static int getTheme(){
        return themeIndex;
    }
    public static void recolorAll(){
        launchBackgroundColor = GameEssentials.processColor(new Color(241, 243, 213), "LaunchBackgroundColor");
        launchTitlePanelBackgroundColor = GameEssentials.processColor(new Color(219, 223, 151), "LaunchTitlePanelBackgroundColor");
        launchAuthorFontColor = GameEssentials.processColor(new Color(0, 73, 54), "LaunchAuthorFontColor");
        launchWWFontColor = GameEssentials.processColor(Color.BLACK, "LaunchWWFontColor");
        launchVersionFontColor = GameEssentials.processColor(Color.BLACK, "LaunchVersionFontColor");
        launchHintFontColor = GameEssentials.processColor(Color.GRAY, "LaunchHintFontColor");
        launchPlayerNameFontColor = GameEssentials.processColor(new Color(136, 136, 0), "LaunchPlayerNameFontColor");
        launchPlayerPromptFontColor = GameEssentials.processColor(new Color(0, 136, 0), "LaunchPlayerPromptFontColor");
        launchPlayerErrorFontColor = GameEssentials.processColor(new Color(136, 0, 0), "LaunchPlayerErrorFontColor");
        launchPlayerSpecialFontColor = GameEssentials.processColor(new Color(0, 136, 136), "LaunchPlayerSpecialFontColor");
        launchStartButtonBackgroundColor = GameEssentials.processColor(Color.BLACK, "LaunchStartButtonBackgroundColor");
        launchQuitButtonBackgroundColor = GameEssentials.processColor(Color.RED, "LaunchQuitButtonBackgroundColor");
        launchNewButtonBackgroundColor = GameEssentials.processColor(new Color(0, 193, 211), "LaunchNewButtonBackgroundColor");
        launchConfirmButtonBackgroundColor = GameEssentials.processColor(new Color(0, 223, 39), "LaunchConfirmButtonBackgroundColor");
        launchSlidingButtonOnColor = GameEssentials.processColor(Color.GREEN, "LaunchSlidingButtonOnColor");
        launchSlidingButtonOffColor = GameEssentials.processColor(Color.RED, "LaunchSlidingButtonOffColor");
    }

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
        System.out.println(GameTime.generateSimpleTime() + " LaunchLogger: You are playing HappyHex Version " + currentGameVersion + ". Good Luck!");
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
        } else {
            currentPlayerInfo.incrementGameNumber();
            currentPlayerInfo.addTotalTurn(currentGameInfo.getTurn());
            currentPlayerInfo.addTotalScore(currentGameInfo.getScore());
            currentPlayerInfo.setHighTurn(currentGameInfo.getTurn());
            currentPlayerInfo.setHighScore(currentGameInfo.getScore());
        }
    }
    public static void updateOnGame(){
        // Update the average turn and score
        if(currentPlayerInfo.getPlayerID() != -1) {
            // Only if the player is logged in
            for (GameInfo info : LaunchLogger.fetchGameStats()) {
                if (info.getPlayerID() == currentPlayerInfo.getPlayerID()) {
                    int turn = info.getTurn();
                    int score = info.getScore();
                    currentPlayerInfo.incrementGameNumber();
                    currentPlayerInfo.addTotalTurn(turn);
                    currentPlayerInfo.addTotalScore(score);
                    if (score > currentPlayerInfo.getHighScore()) {
                        currentPlayerInfo.setHighScore(score);
                    }
                    if (turn > currentPlayerInfo.getHighTurn()) {
                        currentPlayerInfo.setHighTurn(turn);
                    }
                }
            }
        }
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
            System.err.println(GameTime.generateSimpleTime() + " LaunchLogger: " + e.getMessage());
        }
        currentPlayerInfo.eraseStats();
        LaunchLogger.addGame(currentGameInfo);
        updateRecent();
        updateHighest();
        updateOnGame();
        try {
            LaunchLogger.write();
            LaunchLogger.resetLoggerInfo();
            return true;
        } catch (IOException e) {
            System.err.println(GameTime.generateSimpleTime() + " LaunchLogger: " + e.getMessage());
            LaunchLogger.resetLoggerInfo();
            return false;
        }
    }
}
