package Launcher.panel;

import GUI.GameQuitButton;
import Launcher.LaunchEssentials;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverPanel extends JPanel implements ComponentListener {
    private JPanel gameNameLabelPanel;
    private String gameNameString = "GAME OVER";
    private JLabel[] gameNameLabels;
    private GameQuitButton gameQuitButton;

    public GameOverPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GUI.GameEssentials.gameBackGroundColor);
        gameNameLabelPanel = fetchGameNameLabelPanel();
        this.add(gameNameLabelPanel);
        gameQuitButton = fetchGameQuitButton();
        this.add(Box.createVerticalGlue());
        this.add(new JLabel("677"));
        this.add(gameQuitButton);
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
    private GameQuitButton fetchGameQuitButton(){
        return new GameQuitButton();
    }

    public void recalculate() {
        double referenceGameNameSize = Math.min(getReferenceHeight() * 2.5, getReferenceWidth());
        for (JLabel label : gameNameLabels) {
            label.setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, (int) Math.round(referenceGameNameSize / 10)));
        }
        Dimension size = new Dimension(1000, (int) Math.round(referenceGameNameSize / 10));
        System.out.println(size);
        gameQuitButton.setPreferredSize(size);
        //gameQuitButton.setBounds(new Rectangle((int) Math.round(referenceGameNameSize / 10), (int) Math.round(referenceGameNameSize / 10), (int) Math.round(referenceGameNameSize / 10), (int) Math.round(referenceGameNameSize / 10)));
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
