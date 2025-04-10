package Launcher.panel;

import GUI.GameEssentials;
import GUI.SimpleButton;
import Launcher.LaunchEssentials;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverPanel extends JPanel implements ComponentListener {
    private JPanel gameNameLabelPanel;
    private String gameNameString = "GAME OVER";
    private JLabel[] gameNameLabels;
    private JLabel[] spacers;
    private JLabel settingTitle;
    private JLabel scoreTitle;
    private JLabel settingEasyMode;
    private JLabel settingGameSize;
    private JLabel scoreUser;
    private JLabel scoreTurns;
    private JLabel scorePoints;
    private JLabel highestTurns;
    private JLabel highestPoints;
    private SimpleButton gameQuitButton;
    private SimpleButton gameNextButton;

    public GameOverPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GUI.GameEssentials.gameBackGroundColor);
        gameNameLabelPanel = fetchGameNameLabelPanel();
        spacers = new JLabel[6];
        for (int i = 0; i < spacers.length; i++) {
            spacers[i] = new JLabel("  ");
            spacers[i].setFont(new Font(GameEssentials.gameDisplayFont, Font.PLAIN, 16));
            spacers[i].setForeground(GameEssentials.gameDisplayFontColor);
            spacers[i].setForeground(GameEssentials.gameDisplayFontColor);
            spacers[i].setHorizontalAlignment(SwingConstants.CENTER);
            spacers[i].setVerticalAlignment(SwingConstants.CENTER);
            spacers[i].setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        settingTitle = new JLabel("SETTING");
        settingTitle.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.BOLD, 32));
        settingTitle.setForeground(GUI.GameEssentials.gameDisplayFontColor);
        settingTitle.setHorizontalAlignment(SwingConstants.CENTER);
        settingTitle.setVerticalAlignment(SwingConstants.CENTER);
        settingTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreTitle = new JLabel("RESULT");
        scoreTitle.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.BOLD, 32));
        scoreTitle.setForeground(GUI.GameEssentials.gameDisplayFontColor);
        scoreTitle.setHorizontalAlignment(SwingConstants.CENTER);
        scoreTitle.setVerticalAlignment(SwingConstants.CENTER);
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingEasyMode = new JLabel("EASY MODE:    " + (LaunchEssentials.isEasyMode()? " ON" : "OFF"));
        settingEasyMode.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, 32));
        settingEasyMode.setForeground(GUI.GameEssentials.gameDisplayFontColor);
        settingEasyMode.setHorizontalAlignment(SwingConstants.CENTER);
        settingEasyMode.setVerticalAlignment(SwingConstants.CENTER);
        settingEasyMode.setAlignmentX(Component.CENTER_ALIGNMENT);
        String gameSizeString = "GAME SIZE: ";
        if (LaunchEssentials.isSmallMode()){
            gameSizeString += " SMALL";
        } else if (LaunchEssentials.isMediumMode()){
            gameSizeString += "MEDIUM";
        } else if (LaunchEssentials.isLargeMode()){
            gameSizeString += " LARGE";
        } else gameSizeString += "CUSTOM";
        settingGameSize = new JLabel(gameSizeString);
        settingGameSize.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, 32));
        settingGameSize.setForeground(GUI.GameEssentials.gameDisplayFontColor);
        settingGameSize.setHorizontalAlignment(SwingConstants.CENTER);
        settingGameSize.setVerticalAlignment(SwingConstants.CENTER);
        settingGameSize.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreUser = new JLabel("USER");
        scoreTurns = new JLabel("TURNS");
        scorePoints = new JLabel("POINTS");
        highestTurns = new JLabel("TURNS");
        highestPoints = new JLabel("POINTS");

        gameQuitButton = new GUI.GameQuitButton();
        gameNextButton = new Launcher.interactive.NextGameButton();
        // Add components
        this.add(gameNameLabelPanel);
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.add(settingTitle);
        this.add(spacers[0]);
        this.add(settingEasyMode);
        this.add(settingGameSize);
        this.add(Box.createVerticalGlue());
        this.add(spacers[1]);
        this.add(scoreTitle);
        this.add(spacers[2]);
        this.add(scoreUser);
        this.add(spacers[3]);
        this.add(scoreTurns);
        this.add(scorePoints);
        this.add(spacers[4]);
        this.add(highestTurns);
        this.add(highestPoints);
        this.add(spacers[5]);
        this.add(Box.createVerticalGlue());
        this.add(gameNextButton);
        this.add(gameQuitButton);
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.addComponentListener(this);
    }

    private JPanel fetchGameNameLabelPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(GUI.GameEssentials.whitenColor(GUI.GameEssentials.darkenColor(GUI.GameEssentials.gameBackGroundColor)));
        panel.add(Box.createHorizontalGlue());
        gameNameLabels = new JLabel[9];
        int gameNameLabelIndexShift = (int) (Math.random() * 9);
        for (int i = 0; i < 9; i++) {
            gameNameLabels[i] = new JLabel();
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, 80));
            gameNameLabels[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            gameNameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setText(gameNameString.substring(i, i + 1));
            gameNameLabels[i].setForeground(GUI.GameEssentials.interpolate(
                    GUI.GameEssentials.getIndexedPieceColor((gameNameLabelIndexShift + i * 5) % 12),
                    panel.getBackground(), 4));
            panel.add(gameNameLabels[i]);
        }
        panel.add(Box.createHorizontalGlue());
        return panel;
    }

    public void recalculate() {
        double referenceGameNameSize = Math.min(getReferenceHeight() * 1.5, getReferenceWidth());
        for (JLabel label : gameNameLabels) {
            label.setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, (int) Math.round(referenceGameNameSize / 10)));
        }
        double buttonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        int fontSize = (int) Math.round(buttonSize / 24);
        GUI.SimpleButton.setSize(fontSize * 2);
        Dimension size = new Dimension((int) Math.round(buttonSize / 4), (int) Math.round(buttonSize / 8));
        gameNextButton.setPreferredSize(size);
        gameQuitButton.setPreferredSize(size);
        for (JLabel spacer : spacers) {
            spacer.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize / 2));
        }
        settingTitle.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.BOLD, fontSize));
        scoreTitle.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.BOLD, fontSize));
        settingEasyMode.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        settingGameSize.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        scoreUser.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        scoreTurns.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        scorePoints.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        highestTurns.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        highestPoints.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize));
        gameNextButton.resetSize();
        gameQuitButton.resetSize();
    }

    private double getReferenceHeight() {
        return this.getSize().getHeight();
    }
    private double getReferenceWidth() {
        return this.getSize().getWidth();
    }

    public final void componentResized(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentMoved(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentShown(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentHidden(ComponentEvent e) {}
}
