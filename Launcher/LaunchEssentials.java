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

package Launcher;

import GUI.GameEssentials;
import hexio.HexLogger;
import io.*;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The {@link LaunchEssentials} class provides essential launcher utilities.
 * This class is final and cannot be extended.
 */
public final class LaunchEssentials {
    // Program info
    public static final GameVersion currentGameVersion = new GameVersion(1, 3, 0);
    public static final String currentGameName = "HappyHex";
    public static final String currentEnvironment = "java";

    // Random
    private static final java.util.Random randomGenerator = new java.util.Random();

    // Game info
    private static PlayerInfo currentPlayerInfo = new PlayerInfo(0, 0, 0, 0, 0, 0, -1, Username.getUsername("Guest"));
    private static GameInfo currentGameInfo;
    private static boolean gameStarted = false;
    private static boolean restartGame = true; // Whether to restart previously ended game

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
    public static Color launchLoginFieldBackgroundColor = GameEssentials.processColor(new Color(247, 248, 238), "launchLoginFieldBackgroundColor");
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
    public static Color launchSlidingButtonEmptyColor = GameEssentials.processColor(Color.WHITE, "LaunchSlidingButtonEmptyColor");
    public static final String launchAuthorFont = "Helvetica";
    public static final String launchWWFont = "Georgia";
    public static String launchTitleFont = GameEssentials.processFont("Courier", "TitleFont");
    public static String launchVersionFont = GameEssentials.processFont("Comic Sans MS", "VersionFont");
    public static String launchButtonFont = GameEssentials.processFont("Times New Roman", "ButtonFont");
    public static String launchEnterUsernameFont = GameEssentials.processFont("Courier", "MonoFont");
    public static String launchSettingsFont = GameEssentials.processFont("Courier", "MonoFont");
    public static String launchSettingsSlidingButtonFont = GameEssentials.processFont("Helvetica", "SlidingButtonFont");

    public static int getRandomIndex(int length){
        return randomGenerator.nextInt(length);
    }
    public static void setTheme(int featureIndex){
        themeIndex = featureIndex;
        GameEssentials.changeColorProcessor(special.FeatureFactory.createFeature(Color.class.getName(), featureIndex + ""));
        recolorAll();
        LauncherGUI.resetColor();
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
        launchLoginFieldBackgroundColor = GameEssentials.processColor(new Color(247, 248, 238), "launchLoginFieldBackgroundColor");
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
        launchSlidingButtonEmptyColor = GameEssentials.processColor(Color.WHITE, "LaunchSlidingButtonEmptyColor");
    }
    public static void refontAll(){
        launchTitleFont = GameEssentials.processFont("Courier", "TitleFont");
        launchVersionFont = GameEssentials.processFont("Comic Sans MS", "VersionFont");
        launchButtonFont = GameEssentials.processFont("Times New Roman", "ButtonFont");
        launchEnterUsernameFont = GameEssentials.processFont("Courier", "MonoFont");
        launchSettingsFont = GameEssentials.processFont("Courier", "MonoFont");
        launchSettingsSlidingButtonFont = GameEssentials.processFont("Helvetica", "SlidingButtonFont");
    }

    public static boolean isGameStarted(){
        return gameStarted;
    }
    public static boolean isRestartGame(){
        return restartGame;
    }
    public static void startGame(){
        gameStarted = true;
        int delay = 250;
        if (currentGameInfo.getGameMode() == GameMode.Small){
            GameEssentials.initialize(5, 3, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(5, 3));
        } else if (currentGameInfo.getGameMode() == GameMode.Medium){
            GameEssentials.initialize(8, 5, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(8, 5));
        } else if (currentGameInfo.getGameMode() == GameMode.Large){
            GameEssentials.initialize(11, 7, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(11, 7));
        } else if (currentGameInfo.getGameMode() == GameMode.SmallEasy){
            GameEssentials.initialize(5, 3, delay, true, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(5, 3));
        } else if (currentGameInfo.getGameMode() == GameMode.MediumEasy){
            GameEssentials.initialize(8, 5, delay, true, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(8, 5));
        } else if (currentGameInfo.getGameMode() == GameMode.LargeEasy){
            GameEssentials.initialize(11, 7, delay, true, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(11, 7));
        } else if(currentGameInfo.getGameMode() == GameMode.Unspecified){
            System.err.println(GameTime.generateSimpleTime() + " GameEssentials: Legacy GameMode.Unspecified GameMode unsupported since Version 0.4.1");
            GameEssentials.initialize(5, 3, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(5, 3));
        } else {
            System.err.println(GameTime.generateSimpleTime() + " GameEssentials: Unknown GameMode detected.");
            GameEssentials.initialize(5, 3, delay, false, LauncherGUI.getMainFrame(), getCurrentPlayer(), smartCreateLogger(5, 3));
        }
    }
    public static void endGame(){
        gameStarted = false;
    }
    public static void setRestartGame(boolean restartGame){LaunchEssentials.restartGame = restartGame;}
    public static void setCurrentPlayer(Username currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayerInfo.setPlayer(currentPlayer, currentPlayerID);
        LaunchEssentials.currentGameInfo.setPlayer(currentPlayer, currentPlayerID);
    }
    public static String getCurrentPlayer(){
        return currentGameInfo.getPlayer().toString();
    }
    public static long getCurrentPlayerID(){
        return currentGameInfo.getPlayerID();
    }
    public static hexio.HexLogger smartCreateLogger(int size, int queueSize){
        hexio.HexLogger logger = new hexio.HexLogger(getCurrentPlayer(), getCurrentPlayerID()); // Default logger
        java.util.ArrayList<hexio.HexLogger> loggers = hexio.HexLogger.generateJsonLoggers();
        // Search for incomplete games
        if(restartGame && !loggers.isEmpty() && currentGameInfo.getPlayerID() != -1 && !currentGameInfo.getPlayer().equals("Guest")){
            for (hexio.HexLogger generatedLogger : loggers){
                try {
                    generatedLogger.readBinary();
                    generatedLogger.read();
                } catch (IOException e) {continue;}
                if (!generatedLogger.isCompleted() && generatedLogger.getEngine().getRadius() == size
                        && generatedLogger.getQueue().length == queueSize
                        && generatedLogger.getPlayerID() == currentGameInfo.getPlayerID()){
                    System.out.println(GameTime.generateSimpleTime() + " HexLogger: Unfinished game found at file " + generatedLogger.getDataFileName() + ".");
                    logger = generatedLogger;
                }
            }
        }
        return logger;
    }
    public static java.util.ArrayList<hexio.HexLogger> smartFindLoggers(){
        if (currentGameInfo.getPlayerID() == -1 || currentGameInfo.getPlayer().equals("Guest")){
            return new ArrayList<hexio.HexLogger>();
        }
        java.util.ArrayList<hexio.HexLogger> loggers = hexio.HexLogger.generateJsonLoggers();
        int radius = 5;
        int queueSize = 3;
        if (GameMode.getChar(currentGameInfo.getGameMode()) == 'M'){
            radius = 8; queueSize = 5;
        } else if (GameMode.getChar(currentGameInfo.getGameMode()) == 'L'){
            radius = 11; queueSize = 7;
        }
        // Search for incomplete games
        if(!loggers.isEmpty()){
            int i = 0;
            while (i < loggers.size()) {
                HexLogger generatedLogger = loggers.get(i);
                try {
                    generatedLogger.readBinary();
                    generatedLogger.read();
                } catch (IOException e) {
                    loggers.remove(i);
                }
                if (!generatedLogger.isCompleted() && generatedLogger.getEngine().getRadius() == radius
                        && generatedLogger.getQueue().length == queueSize
                        && generatedLogger.getPlayerID() == currentGameInfo.getPlayerID()) {
                    i++;
                } else loggers.remove(i);
            }
        }
        return loggers;
    }
    public static void initialize(){
        currentGameInfo = new GameInfo(GameMode.Small, currentGameVersion);
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

    public static void log(int turn, int score){
        // Print to console
        System.out.println(GameTime.generateSimpleTime() +
                " GameEssentials: This game lasted for " + turn +
                " turn" + (turn == 1 ? "" : "s") + ", resulting in a total score of " + score +
                " point" + (score == 1 ? "" : "s") + ".");
        // Try to read previous logs
        new Thread(() -> {
            try {
                LaunchLogger.read();
            } catch (IOException e) {
                System.err.println(GameTime.generateSimpleTime() + " LaunchLogger: " + e.getMessage());
            }
            // Update current information
            currentPlayerInfo.eraseStats();
            currentGameInfo = new GameInfo(turn, score, Long.toHexString(currentGameInfo.getPlayerID()), currentGameInfo.getPlayer(),
                    new GameTime(), Long.toHexString(currentGameInfo.getGameID()), currentGameInfo.getGameMode(), currentGameVersion);
            LaunchLogger.addGame(currentGameInfo);
            updateRecent();
            updateHighest();
            updateOnGame();
            try {
                LaunchLogger.write(currentGameName, currentEnvironment, currentGameVersion);
                LaunchLogger.resetLoggerInfo();
            } catch (IOException e) {
                System.err.println(GameTime.generateSimpleTime() + " LaunchLogger: " + e.getMessage());
                LaunchLogger.resetLoggerInfo();
            }
        }).start();
    }
}
