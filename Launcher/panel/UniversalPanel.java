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
import Launcher.LaunchEssentials;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public abstract class UniversalPanel extends JPanel implements ComponentListener, Launcher.Recolorable, Launcher.Refontable {
    private final int gameNameLabelIndexShift = (int) (Math.random() * 12);
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
        JComponent[] header = fetchHeader();
        if(header != null && header.length != 0) {
            for (JComponent component: header){
                this.add(component);
            }
        }
        this.add(gameNameLabelPanel);

        launchAuthorPanel = fetchLaunchAuthorPanel();
        JComponent[] content = fetchContent();
        if(content != null && content.length != 0) {
            for (JComponent component: content){
                this.add(component);
            }
        }
        this.add(launchAuthorPanel);
        this.addComponentListener(this);
    }

    abstract protected JComponent[] fetchContent();
    abstract protected JComponent[] fetchHeader();

    private JPanel fetchGameNameLabelPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
        panel.add(Box.createHorizontalGlue());
        gameNameLabels = new JLabel[12];
        for (int i = 0; i < 12; i++) {
            gameNameLabels[i] = new JLabel();
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, 80));
            gameNameLabels[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            gameNameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setText(gameNameString.substring(i, i + 1));
            gameNameLabels[i].setForeground(GameEssentials.interpolate(
                    GameEssentials.generateColor((gameNameLabelIndexShift + i * 5) % 12),
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
        double referenceGameNameSize = Math.min(getReferenceHeight() * 1.8, getReferenceWidth());
        for (JLabel label : gameNameLabels) {
            label.setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, (int) Math.round(referenceGameNameSize / 10)));
        }
        double referenceLaunchAuthorSize = Math.min(getReferenceHeight(), getReferenceWidth() * 2.25);
        int borderSize = (int) Math.round(referenceLaunchAuthorSize / 160.0);
        launchAuthorPanel.setBorder(new EmptyBorder(borderSize, borderSize, borderSize, borderSize));
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize * 0.03)));
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
    }
    public void resetColor(){
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        gameNameLabelPanel.setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
        for (int i = 0; i < 12; i++) {
            gameNameLabels[i].setForeground(GameEssentials.interpolate(
                    GameEssentials.generateColor((gameNameLabelIndexShift + i * 5) % 12),
                    LaunchEssentials.launchTitlePanelBackgroundColor, 4));
        }
        launchAuthorLabelA.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchAuthorLabelWW.setForeground(LaunchEssentials.launchWWFontColor);
        launchAuthorLabelGame.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchCopyrightLabel.setForeground(LaunchEssentials.launchAuthorFontColor);
    }
    public void resetFont(){
        double referenceGameNameSize = Math.min(getReferenceHeight() * 1.8, getReferenceWidth());
        for (JLabel label : gameNameLabels) {
            label.setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, (int) Math.round(referenceGameNameSize / 10)));
        }
        double referenceLaunchAuthorSize = Math.min(getReferenceHeight(), getReferenceWidth() * 2.25);
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize * 0.03)));
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int) Math.round(referenceLaunchAuthorSize / 40.0)));
    }

    public void paint(Graphics g) {
        if (GameEssentials.isColorAnimated()) {
            for (int i = 0; i < 12; i++) {
                gameNameLabels[i].setForeground(GameEssentials.interpolate(
                        GameEssentials.generateColor((gameNameLabelIndexShift + i * 5) % 12),
                        LaunchEssentials.launchTitlePanelBackgroundColor, 4));
            }
        }
        super.paint(g);
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