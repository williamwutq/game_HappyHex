package Launcher;

import GUI.GameEssentials;
import GUI.HappyHexGUI;
import Launcher.IO.*;

import java.awt.*;
import java.io.IOException;

/**
 * The {@link LaunchEssentials} class provides essential launcher utilities.
 * This class is final and cannot be extended.
 */
public final class LaunchEssentials {
    // Program info
    public static final GameVersion currentGameVersion = new GameVersion(0, 4, 0);
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

    public static boolean isGameStarted(){
        return gameStarted;
    }
    public static void startGame(){
        gameStarted = true;

        // Initialization
        LauncherGUI.removeAllFromFrame();
        LauncherGUI.setBackgroundColor(GameEssentials.gameBackGroundColor);
        HappyHexGUI.initialize(5, 3, 100, false, LauncherGUI.mainFrame);
        LaunchEssentials.initializeCurrentGame(GameMode.Unspecified);

        // Add to frame and repaint
        LauncherGUI.mainFrame.add(HappyHexGUI.fetchGamePanel(), BorderLayout.CENTER);
        LauncherGUI.mainFrame.add(HappyHexGUI.fetchPiecePanel(), BorderLayout.SOUTH);
        LauncherGUI.mainFrame.repaint();
    }
    public static void toLogInPage(){
        gameStarted = false;

        // Initialization
        LauncherGUI.removeAllFromFrame();
        LauncherGUI.setBackgroundColor(GameEssentials.gameBackGroundColor);

        LauncherGUI.mainFrame.add(LauncherGUI.fetchLoginPanel(), BorderLayout.CENTER);
    }
    public static void toLSettings(){
        gameStarted = false;

        // Initialization
        LauncherGUI.removeAllFromFrame();
        LauncherGUI.setBackgroundColor(GameEssentials.gameBackGroundColor);

        LauncherGUI.mainFrame.add(LauncherGUI.fetchSettingPanel(), BorderLayout.CENTER);
    }
    public static void endGame(){
        gameStarted = false;
    }
    public static PlayerInfo currentPlayerInfo(){
        return currentPlayerInfo;
    }
    public static void setCurrentPlayer(Username currentPlayer, long currentPlayerID) {
        LaunchEssentials.currentPlayerInfo.setPlayer(currentPlayer, currentPlayerID);
        LaunchEssentials.currentGameInfo.setPlayer(currentPlayer, currentPlayerID);
    }
    public static void initializeCurrentGame(GameMode mode){
        currentGameInfo = new GameInfo(mode);
        gameStarted = true;
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
