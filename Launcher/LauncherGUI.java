package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import java.awt.*;

public class LauncherGUI {
    public static void launch(){
        JFrame frame = new JFrame("HappyHex Version " + LaunchEssentials.currentGameVersion);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        JPanel launchPanel = new JPanel();
        launchPanel.setLayout(new BoxLayout(launchPanel, BoxLayout.Y_AXIS));
        launchPanel.add(Box.createVerticalGlue());

        JPanel gameNameLabelPanel = new JPanel();
        gameNameLabelPanel.setLayout(new BoxLayout(gameNameLabelPanel, BoxLayout.X_AXIS));
        gameNameLabelPanel.add(Box.createHorizontalGlue());
        JLabel[] gameNameLabels = new JLabel[12];
        String gameNameString = "⬢HAPPY⬢⬢HEX⬢";
        for (int i = 0; i < 12; i ++){
            gameNameLabels[i] = new JLabel();
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, 80));
            gameNameLabels[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            gameNameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setText(gameNameString.substring(i, i+1));
            gameNameLabels[i].setForeground(GameEssentials.getIndexedPieceColor((i*5)%12));
            gameNameLabelPanel.add(gameNameLabels[i]);
        }
        gameNameLabelPanel.add(Box.createHorizontalGlue());
        launchPanel.add(gameNameLabelPanel);

        JLabel launchVersionLabel = new JLabel("Version " + LaunchEssentials.currentGameVersion);
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, 40));
        launchVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchPanel.add(launchVersionLabel);

        launchPanel.add(Box.createVerticalGlue());
        launchPanel.add(new LaunchButton());
        launchPanel.add(Box.createVerticalGlue());

        frame.add(launchPanel);
        frame.setVisible(true);
    }
    public static void main(String[] args){
        launch();
    }
}
