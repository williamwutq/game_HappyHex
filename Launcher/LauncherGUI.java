package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LauncherGUI {
    public static void launch(){
        JFrame frame = new JFrame("HappyHex Version " + LaunchEssentials.currentGameVersion);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(LaunchEssentials.launchBackgroundColor);
        frame.setSize(new Dimension(400, 400));
        frame.setMinimumSize(new Dimension(400, 400));

        JPanel launchPanel = new LaunchPanel();

        frame.add(launchPanel);
        frame.validate();
        frame.setVisible(true);
        frame.setSize(new Dimension(800, 800));
        frame.repaint();
    }
    public static void main(String[] args){
        launch();
    }
}
