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

package Launcher.panel;

import GUI.GameEssentials;
import GUI.SimpleButton;
import GUI.InlineInfoPanel;
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
    private InlineInfoPanel settingEasyMode;
    private InlineInfoPanel settingGameSize;
    private InlineInfoPanel scoreUser;
    private InlineInfoPanel scoreTurns;
    private InlineInfoPanel scorePoints;
    private InlineInfoPanel highestTurns;
    private InlineInfoPanel highestPoints;
    private InlineInfoPanel averageTurns;
    private InlineInfoPanel averagePoints;
    private SimpleButton gameQuitButton;
    private SimpleButton gameNextButton;

    public GameOverPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GUI.GameEssentials.gameBackgroundColor);
        gameNameLabelPanel = fetchGameNameLabelPanel();
        spacers = new JLabel[8];
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

        String gameSizeString = " ";
        if (LaunchEssentials.isSmallMode()){
            gameSizeString += "SMALL";
        } else if (LaunchEssentials.isMediumMode()){
            gameSizeString += "MEDIUM";
        } else if (LaunchEssentials.isLargeMode()){
            gameSizeString += "LARGE";
        } else gameSizeString += "CUSTOM";

        settingEasyMode = new InlineInfoPanel();
        settingEasyMode.setTitle("    EASY MODE ");
        settingEasyMode.setInfo((LaunchEssentials.isEasyMode()? " ON" : " OFF") + "    ");
        settingGameSize = new InlineInfoPanel();
        settingGameSize.setTitle("    GAME SIZE ");
        settingGameSize.setInfo(gameSizeString + "    ");

        scoreUser = new InlineInfoPanel();
        scoreTurns = new InlineInfoPanel();
        scorePoints = new InlineInfoPanel();
        highestTurns = new InlineInfoPanel();
        highestPoints = new InlineInfoPanel();
        averageTurns = new InlineInfoPanel();
        averagePoints = new InlineInfoPanel();

        scoreUser.setTitle("    PLAYER ");
        scoreUser.setInfo(" " + LaunchEssentials.getCurrentPlayer() + "    ");
        scoreTurns.setTitle("    LAST TURNS ");
        scoreTurns.setInfo(" " + LaunchEssentials.getLastTurn() + "    ");
        scorePoints.setTitle("    LAST SCORE ");
        scorePoints.setInfo(" " + LaunchEssentials.getLastScore() + "    ");
        highestTurns.setTitle("    HIGHEST TURNS ");
        highestTurns.setInfo(" " + LaunchEssentials.getHighestTurn() + "    ");
        highestPoints.setTitle("    HIGHEST SCORE ");
        highestPoints.setInfo(" " + LaunchEssentials.getHighestScore() + "    ");
        averageTurns.setTitle("    AVERAGE TURNS ");
        averageTurns.setInfo(" " + LaunchEssentials.getAverageTurn() + "    ");
        averagePoints.setTitle("    AVERAGE SCORE ");
        averagePoints.setInfo(" " + LaunchEssentials.getAverageScore() + "    ");

        gameQuitButton = new GUI.GameQuitButton();
        gameNextButton = new Launcher.interactive.NextGameButton();
        // Add components
        this.add(gameNameLabelPanel);
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.add(spacers[0]);
        this.add(settingTitle);
        this.add(spacers[1]);
        this.add(settingEasyMode);
        this.add(settingGameSize);
        this.add(Box.createVerticalGlue());
        this.add(spacers[2]);
        this.add(scoreTitle);
        this.add(spacers[3]);
        this.add(scoreUser);
        this.add(spacers[4]);
        this.add(scoreTurns);
        this.add(scorePoints);
        this.add(spacers[5]);
        this.add(highestTurns);
        this.add(highestPoints);
        this.add(spacers[6]);
        this.add(averageTurns);
        this.add(averagePoints);
        this.add(spacers[7]);
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
        panel.setBackground(GUI.GameEssentials.gameOverBackgroundColor);
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
        int fontSize = (int) Math.round(buttonSize / 30);
        GUI.SimpleButton.setSize(fontSize * 2);
        Dimension size = new Dimension((int) Math.round(buttonSize / 4), (int) Math.round(buttonSize / 8));
        gameNextButton.setPreferredSize(size);
        gameQuitButton.setPreferredSize(size);
        for (JLabel spacer : spacers) {
            spacer.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.PLAIN, fontSize / 2));
        }
        settingTitle.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.BOLD, fontSize));
        scoreTitle.setFont(new Font(GUI.GameEssentials.gameDisplayFont, Font.BOLD, fontSize));
        settingEasyMode.setSize(fontSize);
        settingGameSize.setSize(fontSize);
        scoreUser.setSize(fontSize);
        scoreTurns.setSize(fontSize);
        scorePoints.setSize(fontSize);
        highestTurns.setSize(fontSize);
        highestPoints.setSize(fontSize);
        averageTurns.setSize(fontSize);
        averagePoints.setSize(fontSize);
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
