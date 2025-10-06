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
import Launcher.panel.*;
import achievements.GameAchievement;
import util.fgui.GraphicsDisplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class LauncherGUI implements GraphicsDisplayer {
    private static JFrame mainFrame;
    private static JLayeredPane layeredPane;
    private static Component popUpPanel = null;
    public static final LauncherGUI INSTANCE = new LauncherGUI();
    private LauncherGUI(){}
    public static void launch(){
        setupMainFrame();
        LaunchEssentials.initialize();
        layeredPane.add(fetchLaunchPanel(), BorderLayout.CENTER);
        mainFrame.validate();
        mainFrame.setVisible(true);
        mainFrame.setSize(new Dimension(800, 800));
        mainFrame.repaint();
    }
    public static String getGameHint(){
        String[] hints = new String[]{
                "Try InfinityHex",
                "Consider InfinityHex?",
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
                "Try log in and then log out",
                "Try to change theme color",
                "Which theme is your favorite?",
                "Dislike the color? No problem",
                "Toggle theme in themes",
                "Dark theme best theme",
                "I want to be forever young",
                "Choose white if you like it",
                "Choose dark if you like it",
                "Choose a theme you like",
                "Want to resume a game?",
                "Wonder if my game are saved",
                "Colors are saved in above v1.3",
                "Games are saved since v1.2",
                "What does resume button do?",
                "Resume Resume Resume Resume",
                "What if HappyHex... was dark",
                "What if HappyHex... was white",
                "How do I turn resume game off",
                "Everyday I play this game",
                "Everyday my life is better",
                "Everyday is a blessing",
                "I tried, I said",
                "Are you better than the bot?",
                "Sure, tough guy, try autoplay",
                "Turn autoplay on to enjoy",
                "Playing without a brain",
                "Interesting things it is doing",
                "Let computer do it",
                "Try autoplay, I said",
                "Games are no-brainer",
                "What is the coolest feature?",
        };
        return hints[LaunchEssentials.getRandomIndex(hints.length)];
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
    private static JPanel fetchResumePanel(){
        return new ResumePanel();
    }
    private static JPanel fetchLoginPanel(){
        return new LoginPanel();
    }
    private static JPanel fetchSettingPanel(){
        return new SettingPanel();
    }
    private static JPanel fetchAchievementPanel(){
        return new AchievementsPanel();
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
        mainFrame = new JFrame("HappyHex Version " + Launcher.LaunchEssentials.currentGameVersion){
            @Override
            public void doLayout() {
                if (popUpPanel != null) {
                    // Set to half the width and height of the frame
                    popUpPanel.setSize(getWidth() / 2, getHeight() / 2);
                    popUpPanel.setLocation((getWidth() - popUpPanel.getWidth()) / 2, (getHeight() - popUpPanel.getHeight()) / 2);
                    popUpPanel.validate();
                }
                super.doLayout();
            }
        };
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setBackground(LaunchEssentials.launchBackgroundColor);
        mainFrame.setIconImage(fetchIconImage());
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        mainFrame.setContentPane(layeredPane);
        Taskbar.getTaskbar().setIconImage(fetchIconImage());
        mainFrame.setSize(new Dimension(400, 400));
        mainFrame.setMinimumSize(new Dimension(400, 400));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Custom code to execute when the window is closing
                if(LaunchEssentials.isGameStarted()) {
                    // Log if it has score and reset
                    if (GameEssentials.getTurn() != 0) {
                        System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game ends by force quitting.");
                        GameEssentials.logGame();
                    }
                    GameEssentials.resetGame();
                }
                // Close
                GameEssentials.terminateAutoplay();
                GameEssentials.closeAnimator();
                System.out.println(io.GameTime.generateSimpleTime() + " LaunchLogger: Application quits.");
                mainFrame.dispose();
                try {
                    GameAchievement.serializeActiveUserAchievements();
                } catch (IOException ex) {
                    System.err.println(io.GameTime.generateSimpleTime() + " Achievement: Failed to serialize achievements because " + ex.getMessage());
                }
                GameAchievement.shutdownAchievementSystem();
                System.exit(0);
            }
        });
    }
    public static void removeAllFromFrame(){
        layeredPane.removeAll();
        layeredPane.setLayout(new BorderLayout());
        layeredPane.revalidate();
    }
    public static void resetColor(){
        for (Component component : layeredPane.getComponents()){
            if (component instanceof Recolorable){
                ((Recolorable) component).resetColor();
            }
        }
    }
    public static void setBackgroundColor(Color color){
        mainFrame.setBackground(color);
    }
    public static void startGame(String fileName){
        LaunchEssentials.startGame(fileName);

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        layeredPane.add(GameEssentials.fetchGamePanel(), BorderLayout.CENTER);
        layeredPane.add(GameEssentials.fetchPiecePanel(), BorderLayout.SOUTH);
    }
    public static void toResumeGame(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(LaunchEssentials.launchBackgroundColor);
        layeredPane.add(fetchResumePanel(), BorderLayout.CENTER);
    }
    public static void toLogInPage(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(LaunchEssentials.launchBackgroundColor);
        layeredPane.add(fetchLoginPanel(), BorderLayout.CENTER);
    }
    public static void toSettings(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(LaunchEssentials.launchBackgroundColor);
        layeredPane.add(fetchSettingPanel(), BorderLayout.CENTER, JLayeredPane.DEFAULT_LAYER);
    }
    public static void toAchievements(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(LaunchEssentials.launchBackgroundColor);
        layeredPane.add(fetchAchievementPanel(), BorderLayout.CENTER, JLayeredPane.DEFAULT_LAYER);
    }
    public static void toThemes(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(LaunchEssentials.launchBackgroundColor);
        layeredPane.add(fetchThemePanel(), BorderLayout.CENTER, JLayeredPane.DEFAULT_LAYER);
    }
    public static void returnHome(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(LaunchEssentials.launchBackgroundColor);
        layeredPane.add(fetchLaunchPanel(), BorderLayout.CENTER, JLayeredPane.DEFAULT_LAYER);
    }
    public static void toGameOver(){
        LaunchEssentials.endGame();

        // Initialization
        removeAllFromFrame();
        setBackgroundColor(GameEssentials.gameBackgroundColor);
        layeredPane.add(fetchGameOverPanel(), BorderLayout.CENTER, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }
    public static void showPopUp(Component panel){
        popUpPanel = panel;
        layeredPane.add(popUpPanel, JLayeredPane.POPUP_LAYER);
        layeredPane.revalidate();
        mainFrame.repaint();
    }
    public static void closePopUp() {
        layeredPane.remove(popUpPanel);
        layeredPane.revalidate();
        popUpPanel = null;
        mainFrame.repaint();
    }

    @Override
    public void display(Component component) {
        if (component == null) return;
        remove();
        showPopUp(component);
    }
    @Override
    public void remove() {
        if (popUpPanel != null){
            layeredPane.remove(popUpPanel);
            layeredPane.revalidate();
            popUpPanel = null;
            mainFrame.repaint();
        }
    }
    @Override
    public Component current() {
        return popUpPanel;
    }
}
