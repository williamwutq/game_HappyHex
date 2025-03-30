package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import java.awt.*;

public class LauncherGUI {
    public static void launch(){
        JFrame frame = new JFrame("HappyHex Version " + LaunchEssentials.currentGameVersion);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        JPanel launchPanel = new JPanel();
        launchPanel.setLayout(new BoxLayout(launchPanel, BoxLayout.Y_AXIS));

        JLabel gameNameLabel = new JLabel("HappyHex");
        gameNameLabel.setFont(new Font(GameEssentials.gameTitleFont, Font.BOLD, 20));
        gameNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameNameLabel.setForeground(Color.WHITE);

        frame.add(launchPanel);
        frame.setVisible(true);
    }
    public static void main(String[] args){
        launch();
    }
}
