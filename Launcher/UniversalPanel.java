package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public abstract class UniversalPanel extends JPanel implements ComponentListener {
    private JPanel gameNameLabelPanel;
    private String gameNameString = "⬢HAPPY⬢⬢HEX⬢";
    private JLabel[] gameNameLabels;
    private JPanel launchAuthorPanel;
    private JLabel launchAuthorLabelA;
    private JLabel launchAuthorLabelWW;
    private JLabel launchAuthorLabelGame;
    private JLabel launchCopyrightLabel;

    public UniversalPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        gameNameLabelPanel = fetchGameNameLabelPanel();
        JPanel headerPanel = fetchHeaderPanel();
        if(headerPanel != null) {
            this.add(headerPanel);
        }
        this.add(gameNameLabelPanel);

        launchAuthorPanel = fetchLaunchAuthorPanel();
        this.add(Box.createVerticalGlue());
        JPanel contentPanel = fetchContentPanel();
        if(contentPanel != null) {
            this.add(contentPanel);
        }
        this.add(Box.createVerticalGlue());
        this.add(launchAuthorPanel);
        this.addComponentListener(this);
    }

    abstract protected JPanel fetchContentPanel();
    abstract protected JPanel fetchHeaderPanel();

    private JPanel fetchGameNameLabelPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
        panel.add(Box.createHorizontalGlue());
        gameNameLabels = new JLabel[12];
        int gameNameLabelIndexShift = (int) (Math.random() * 12);
        for (int i = 0; i < 12; i++) {
            gameNameLabels[i] = new JLabel();
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, 80));
            gameNameLabels[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            gameNameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setText(gameNameString.substring(i, i + 1));
            gameNameLabels[i].setForeground(GameEssentials.interpolate(
                    GameEssentials.getIndexedPieceColor((gameNameLabelIndexShift + i * 5) % 12),
                    LaunchEssentials.launchTitlePanelBackgroundColor, 4));
            panel.add(gameNameLabels[i]);
        }
        panel.add(Box.createHorizontalGlue());
        return panel;
    }

    private JPanel fetchLaunchAuthorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(0, 0, 0, 0));
        launchAuthorLabelA = new JLabel("  A ");
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchAuthorLabelA.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchAuthorLabelWW = new JLabel("W.W");
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, 28));
        launchAuthorLabelWW.setForeground(LaunchEssentials.launchWWFontColor);
        launchAuthorLabelGame = new JLabel(" Game  ");
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchAuthorLabelGame.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchCopyrightLabel = new JLabel("  ©2025 William Wu  ");
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchCopyrightLabel.setForeground(LaunchEssentials.launchAuthorFontColor);
        panel.add(launchAuthorLabelA);
        panel.add(launchAuthorLabelWW);
        panel.add(launchAuthorLabelGame);
        panel.add(Box.createHorizontalGlue());
        panel.add(launchCopyrightLabel);
        return panel;
    }

    public void recalculate() {
        double referenceGameNameSize = Math.min(getReferenceHeight() * 2.5, getReferenceWidth());
        for (JLabel label : gameNameLabels) {
            label.setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, (int) Math.round(referenceGameNameSize / 10)));
        }
        double referenceStartButtonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        LaunchButton.setSizeConstant((int) Math.round(referenceStartButtonSize * 0.0100));
        double referenceLaunchAuthorSize = Math.min(getReferenceHeight(), getReferenceWidth() * 2.25);
        int borderSize = (int) Math.round(referenceLaunchAuthorSize / 160.0);
        launchAuthorPanel.setBorder(new EmptyBorder(borderSize, borderSize, borderSize, borderSize));
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize * 0.03)));
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
    }

    public double getReferenceHeight() {
        return this.getSize().getHeight();
    }
    public double getReferenceWidth() {
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