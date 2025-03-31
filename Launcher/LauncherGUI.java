package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LauncherGUI {
    public static JFrame mainFrame;
    public static void launch(){
        setupMainFrame();
        mainFrame.add(fetchLaunchPanel());
        mainFrame.validate();
        mainFrame.setVisible(true);
        mainFrame.setSize(new Dimension(800, 800));
        mainFrame.repaint();
    }
    public static JPanel fetchLaunchPanel(){
        return new LaunchPanel();
    }
    private static void setupMainFrame(){
        mainFrame = new JFrame("HappyHex Version " + Launcher.LaunchEssentials.currentGameVersion);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(LaunchEssentials.launchBackgroundColor);
        mainFrame.setSize(new Dimension(400, 400));
        mainFrame.setMinimumSize(new Dimension(400, 400));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Custom code to execute when the window is closing
                if(LaunchEssentials.isGameStarted()) {
                    // Log if it has score and reset
                    if (GameEssentials.turn != 0) {
                        GameEssentials.logGame();
                    }
                    GameEssentials.resetGame();
                }
                // Close
                mainFrame.dispose();
            }
        });
    }
    public static void removeAllFromFrame(){
        mainFrame.getContentPane().removeAll();
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().revalidate();
    }
    public static void setBackgroundColor(Color color){
        mainFrame.setBackground(color);
    }
    public static void main(String[] args){
        launch();
    }
}
