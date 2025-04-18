package Launcher;

import GUI.GameEssentials;
import Launcher.panel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LauncherGUI {
    private static JFrame mainFrame;
    public static void launch(){
        setupMainFrame();
        LaunchEssentials.initialize();
        mainFrame.add(fetchLaunchPanel(), BorderLayout.CENTER);
        mainFrame.validate();
        mainFrame.setVisible(true);
        mainFrame.setSize(new Dimension(800, 800));
        mainFrame.repaint();
    }
    public static String getGameHint(){
        String[] hints = new String[]{
                "Try log in as Player",
                "Try log in as God",
                "Try log in as Devil",
                "Complain game is HARD",
                "Complain game is EVIL",
                "Login to preserve score",
                "Toggle easy mode in settings",
                "Set board size in settings",
                "Whatever this is hard",
                "How can I get up to 3000?",
                "Turns and turns it ends",
                "Keep easy pieces in queue",
                "Keep weird pieces in queue",
                "Search for gray blocks",
                "Try hover over blocks",
                "Click on piece to select",
                "Try click on piece again",
                "What is this version?",
                "Don't know what to put here",
                "What if HappyHex... was sad",
                "What if HappyHex... was bad",
                "What if HappyHex... was hard",
                "What if HappyHex... was old",
                "There are some special things",
                "Why is there a queue there?",
                "What's your favorite piece?",
                "Color means nothing to me",
                "What is done can't be undone",
                "Color potential eliminations",
                "Eliminate in any direction",
                "Eliminate horizontal lines",
                "Eliminate diagonal lines",
                "Don't blame the developer",
                "I made this... at what cost",
        };
        return hints[(int)(Math.random() * hints.length)];
    }
    public static JFrame getMainFrame(){
        if(mainFrame == null){
            setupMainFrame();
        }
        return mainFrame;
    }
    private static JPanel fetchLaunchPanel(){
        return new LaunchPanel();
    }
    private static JPanel fetchLoginPanel(){
        return new LoginPanel();
    }
    private static JPanel fetchSettingPanel(){
        return new SettingPanel();
    }
    private static JPanel fetchGameOverPanel(){
        return new GameOverPanel();
    }
    public static JPanel fetchThemePanel(){
        return new ThemePanel();
    }
    private static Image fetchIconImage(){
        String path = "icon_512.png";
        ImageIcon icon = new ImageIcon(LauncherGUI.class.getResource(path));
        return icon.getImage();
    }
    private static void setupMainFrame(){
        mainFrame = new JFrame("HappyHex Version " + Launcher.LaunchEssentials.currentGameVersion);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(LaunchEssentials.launchBackgroundColor);
        mainFrame.setIconImage(fetchIconImage());
        Taskbar.getTaskbar().setIconImage(fetchIconImage());
        mainFrame.setSize(new Dimension(400, 400));
        mainFrame.setMinimumSize(new Dimension(400, 400));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Custom code to execute when the window is closing
                if(LaunchEssentials.isGameStarted()) {
                    // Log if it has score and reset
                    if (GameEssentials.getTurn() != 0) {
                        System.out.println(Launcher.IO.GameTime.generateSimpleTime() + " GameEssentials: Game ends by force quitting.");
                        GameEssentials.logGame();
                    }
                    GameEssentials.resetGame();
                }
                // Close
                System.out.println(Launcher.IO.GameTime.generateSimpleTime() + " LaunchLogger: Application quits.");
                mainFrame.dispose();
            }
        });
    }
    public static void removeAllFromFrame(){
        mainFrame.getContentPane().removeAll();
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().revalidate();
    }
    public static void resetColor(){
        for (Component component : mainFrame.getContentPane().getComponents()){
            if (component instanceof GUI.Recolorable){
                ((GUI.Recolorable) component).resetColor();
            }
        }
    }
    public static void setBackgroundColor(Color color){
        mainFrame.setBackground(color);
    }
    public static void startGame(){
        LaunchEssentials.startGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        mainFrame.add(GameEssentials.fetchGamePanel(), BorderLayout.CENTER);
        mainFrame.add(GameEssentials.fetchPiecePanel(), BorderLayout.SOUTH);
    }
    public static void toLogInPage(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        mainFrame.add(fetchLoginPanel(), BorderLayout.CENTER);
    }
    public static void toSettings(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        mainFrame.add(fetchSettingPanel(), BorderLayout.CENTER);
    }
    public static void toThemes(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        mainFrame.add(fetchThemePanel(), BorderLayout.CENTER);
    }
    public static void returnHome(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        mainFrame.add(fetchLaunchPanel(), BorderLayout.CENTER);
    }
    public static void toGameOver(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        mainFrame.add(fetchGameOverPanel(), BorderLayout.CENTER);
    }
}
