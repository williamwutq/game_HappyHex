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
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        JPanel launchPanel = new JPanel();
        launchPanel.setLayout(new BoxLayout(launchPanel, BoxLayout.Y_AXIS));
        launchPanel.setBackground(new Color(0,0,0,0));
        launchPanel.add(Box.createVerticalGlue());

        JPanel gameNameLabelPanel = new JPanel();
        gameNameLabelPanel.setLayout(new BoxLayout(gameNameLabelPanel, BoxLayout.X_AXIS));
        gameNameLabelPanel.setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
        gameNameLabelPanel.add(Box.createHorizontalGlue());
        JLabel[] gameNameLabels = new JLabel[12];
        String gameNameString = "⬢HAPPY⬢⬢HEX⬢";
        int gameNameLabelIndexShift = (int)(Math.random()*12);
        for (int i = 0; i < 12; i ++){
            gameNameLabels[i] = new JLabel();
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, 80));
            gameNameLabels[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            gameNameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setText(gameNameString.substring(i, i+1));
            gameNameLabels[i].setForeground(GameEssentials.interpolate(GameEssentials.getIndexedPieceColor((gameNameLabelIndexShift+i*5)%12), LaunchEssentials.launchTitlePanelBackgroundColor, 4));
            gameNameLabelPanel.add(gameNameLabels[i]);
        }
        gameNameLabelPanel.add(Box.createHorizontalGlue());
        launchPanel.add(gameNameLabelPanel);

        JLabel launchVersionLabel = new JLabel(" Version " + LaunchEssentials.currentGameVersion + " ");
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, 40));
        launchVersionLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchVersionLabel.setBorder(new EmptyBorder(12, 0, 3, 0));
        launchPanel.add(launchVersionLabel);

        JPanel launchAuthorPanel = new JPanel();
        launchAuthorPanel.setLayout(new BoxLayout(launchAuthorPanel, BoxLayout.X_AXIS));
        launchAuthorPanel.setBorder(new EmptyBorder(5,5,5,5));
        launchAuthorPanel.setBackground(new Color(0,0,0,0));
        JLabel launchAuthorLabelA = new JLabel("  A ");
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchAuthorLabelA.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchAuthorLabelA.setHorizontalAlignment(SwingConstants.LEFT);
        launchAuthorLabelA.setVerticalAlignment(SwingConstants.CENTER);
        launchAuthorLabelA.setAlignmentY(Component.CENTER_ALIGNMENT);
        JLabel launchAuthorLabelWW = new JLabel("W.W");
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, 28));
        launchAuthorLabelWW.setForeground(LaunchEssentials.launchWWFontColor);
        launchAuthorLabelWW.setHorizontalAlignment(SwingConstants.CENTER);
        launchAuthorLabelWW.setVerticalAlignment(SwingConstants.CENTER);
        launchAuthorLabelWW.setAlignmentY(Component.CENTER_ALIGNMENT);
        JLabel launchAuthorLabelGame = new JLabel(" Game  ");
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchAuthorLabelGame.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchAuthorLabelGame.setHorizontalAlignment(SwingConstants.RIGHT);
        launchAuthorLabelGame.setVerticalAlignment(SwingConstants.CENTER);
        launchAuthorLabelGame.setAlignmentY(Component.CENTER_ALIGNMENT);
        JLabel launchCopyrightLabel = new JLabel("  ©2025 William Wu  ");
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchCopyrightLabel.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchCopyrightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        launchCopyrightLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchCopyrightLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        launchAuthorPanel.add(launchAuthorLabelA);
        launchAuthorPanel.add(launchAuthorLabelWW);
        launchAuthorPanel.add(launchAuthorLabelGame);
        launchAuthorPanel.add(Box.createHorizontalGlue());
        launchAuthorPanel.add(launchCopyrightLabel);

        launchPanel.add(Box.createVerticalGlue());
        launchPanel.add(new LaunchButton());
        launchPanel.add(Box.createVerticalGlue());
        launchPanel.add(launchAuthorPanel);

        frame.add(launchPanel);
        frame.setVisible(true);
    }
    public static void main(String[] args){
        launch();
    }
}
