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
import achievements.GameAchievement;
import achievements.GameAchievementTemplate;
import achievements.UserAchievements;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.concurrent.ExecutionException;

public class AchievementsPanel extends UniversalPanel {
    private JLabel titleLabel;
    private SimpleCloseButton closeButton;
    private JPanel wrapperTopPanel;
    private GameAchievementTemplate[] achievementsCache;
    private int pageStartIndex = 0;
    public AchievementsPanel() {
        super();
        achievementsCache = fetchAchievement();
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
        return new JComponent[]{wrapperTopPanel, new InnerPanel()};
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
        closeButton.setMaximumSize(new Dimension(topNameSize * 2, topNameSize));
        closeButton.setPreferredSize(new Dimension(topNameSize * 2, topNameSize));
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
            // Draw an "X"
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int a = Math.min(w, h);
            g2.setStroke(new BasicStroke((float) a / 6));
            g2.setColor(this.getForeground());
            int p = a / 4; int wp = (w - a / 2) / 2; int hp = (h - a / 2) / 2;
            g2.drawLine(wp, hp, w - wp, h - hp);
            g2.drawLine(w - wp, hp, wp, h - hp);
        }
    }

    private class InnerPanel extends JPanel {
        public InnerPanel() {
            this.setOpaque(false);
            this.setLayout(null);
        }
        @Override
        public void doLayout() {
            // Manual grid layout, starting from pageStartIndex
            // Computer how many items can fit in the panel based on width = width and optimal computed height
            int w = this.getWidth();
            int h = this.getHeight();
            int offset = (int) (h * 0.05);
            h -= offset * 2; // Leave some space at top and bottom
            if (w <= 0 || h <= 0) return; // Avoid division by zero
            int optimalItemHeight = Math.min(h / 3, w / AchievementItemPanel.OPTIMAL_RATIO); // At least 3 rows, and not too tall
            if (optimalItemHeight <= 0) return; // Avoid division by zero
            int numRows = Math.max(1, h / optimalItemHeight);
            // Grab from achievementsCache starting from pageStartIndex
            int numItems = Math.min(achievementsCache.length - pageStartIndex, numRows);
            if (numItems <= 0) return; // Nothing to display
            this.removeAll();
            for (int i = 0; i < numItems; i++) {
                AchievementItemPanel itemPanel = new AchievementItemPanel(achievementsCache[pageStartIndex + i]);
                itemPanel.setBounds(0, offset + i * optimalItemHeight, w, optimalItemHeight);
                this.add(itemPanel);
            }
        }
    }

    private class AchievementItemPanel extends JPanel {
        static int OPTIMAL_RATIO = 6; // Width to height ratio to display info
        private GameAchievementTemplate achievement;
        private double iconSize = 1.0;
        private boolean infoDisplayed = false;
        public AchievementItemPanel(GameAchievementTemplate achievement) {
            this.achievement = achievement;
            this.setOpaque(false);
            this.setLayout(null);
        }
        @Override
        public void doLayout() {
            // Layout the achievement item panel
            // Get size of the panel
            int w = this.getWidth();
            int h = this.getHeight();
            // If we are wide enough, display info
            if (w > h * 3) {
                infoDisplayed = true;
                iconSize = h;
            } else {
                infoDisplayed = false;
                iconSize = Math.min(w, h);
            }
        }
        @Override
        public void paint(Graphics g) {
            // Call paint to draw the icon no matter what
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Size g to 0.1 - 0.9 of the panel size
            int iconX = (int) (iconSize * 0.1);
            int iconY = (int) ((this.getHeight() - iconSize) / 2);
            g2.translate(iconX, iconY);
            achievement.icon().paint(g2, iconSize * 0.8);
            // Also draw the name and description if infoDisplayed is true
            if (infoDisplayed) {
                g2.setColor(LaunchEssentials.launchVersionFontColor);
                // Get the base font
                // Get font every single time because it may be changed by font processors
                Font baseFont = new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, 1);
                AffineTransform at = AffineTransform.getScaleInstance(iconSize * 0.15, iconSize * 0.15);
                AffineTransform dAt = AffineTransform.getScaleInstance(iconSize * 0.3, iconSize * 0.3);
                Font smallerFont = baseFont.deriveFont(at); Font biggerFont = baseFont.deriveFont(dAt);
                g2.setFont(biggerFont);
                g2.drawString(achievement.name(), (float) (iconSize), (float) (iconSize * 0.25));
                g2.setFont(smallerFont);
                // Replace with ... if too long
                String desc = achievement.description();
                FontMetrics fm = g2.getFontMetrics();
                int availableWidth = this.getWidth() - (int) (iconSize * 1.2);
                if (fm.stringWidth(desc) > availableWidth) {
                    while (fm.stringWidth(desc + "...") > availableWidth && desc.length() > 0) {
                        desc = desc.substring(0, desc.length() - 1);
                    }
                    desc += "...";
                }
                g2.drawString(desc, (int) (iconSize), (int) (iconSize * 0.65));
                g2.dispose();
            }
        }
    }

    /**
     * Fetch achievements of the user.
     * @return An array of GameAchievementTemplate representing achieved achievements.
     */
    public static GameAchievementTemplate[] fetchAchievement() {
        // Get achievements of the user, which should be a snapshot
        UserAchievements achievements;
        try {
            achievements = GameAchievement.getActiveUserAchievements().get();
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            // Expect InterruptedException and ExecutionException: achievement system is shutdown
            // Expect NullPointerException: no user logged in
            // In these cases, return an empty array
            return new GameAchievementTemplate[0];
        }
        if (achievements == null) {
            // If achievements is null, return an empty array
            return new GameAchievementTemplate[0];
        }
        // Get achievements from the UserAchievements object and:
        // 1. Filter out the ones that are not achieved
        // 2. Map to GameAchievementTemplate
        // 3. Collect to array
        return achievements.getAchievements()
                .stream()
                .filter(GameAchievement::isAchieved)
                .map(GameAchievement::getTemplate)
                .toArray(GameAchievementTemplate[]::new);
    }
}
