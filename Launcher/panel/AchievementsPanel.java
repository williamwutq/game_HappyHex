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

import Launcher.LaunchEssentials;
import Launcher.LauncherGUI;

import javax.swing.*;
import java.awt.*;

public class AchievementsPanel extends UniversalPanel {
    private JLabel titleLabel;
    private SimpleCloseButton closeButton;
    private JPanel wrapperTopPanel;
    public AchievementsPanel() {
        super();
    }
    @Override
    protected JComponent[] fetchContent() {
        titleLabel = new JLabel("Achievements");
        titleLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, 24));
        titleLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        closeButton = new SimpleCloseButton();
        wrapperTopPanel = new JPanel();
        wrapperTopPanel.setLayout(new BorderLayout());
        wrapperTopPanel.setOpaque(false);
        wrapperTopPanel.add(titleLabel, BorderLayout.CENTER);
        wrapperTopPanel.add(closeButton, BorderLayout.EAST);
        return new JComponent[]{wrapperTopPanel};
    }

    @Override
    protected JComponent[] fetchHeader() {
        return new JComponent[0];
    }

    @Override
    public void recalculate() {
        super.recalculate();
        int topNameSize = (int)(Math.min(getReferenceHeight() * 1.8, getReferenceWidth()) / 30);
        wrapperTopPanel.setMaximumSize(new Dimension((int) getReferenceWidth(), topNameSize));
        closeButton.setMaximumSize(new Dimension(topNameSize, topNameSize));
        closeButton.setPreferredSize(new Dimension(topNameSize, topNameSize));
        titleLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, topNameSize));
    }

    private class SimpleCloseButton extends JButton {
        public SimpleCloseButton() {
            this.setBackground(AchievementsPanel.this.getBackground());
            this.setForeground(LaunchEssentials.launchQuitButtonBackgroundColor);
            this.setFocusPainted(false);
            this.setBorderPainted(false);
            this.setContentAreaFilled(false);
            this.setOpaque(true);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.addActionListener(e -> LauncherGUI.returnHome());
        }
        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            // Draw an "X"
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int a = (w + h) / 2;
            g2.setStroke(new BasicStroke((float) a / 6));
            g2.setColor(this.getForeground());
            int padding = a / 4;
            g2.drawLine(padding, padding, getWidth() - padding, getHeight() - padding);
            g2.drawLine(getWidth() - padding, padding, padding, getHeight() - padding);
        }
    }
}
