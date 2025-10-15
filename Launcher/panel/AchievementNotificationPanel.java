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
import achievements.AchievementNotification;
import achievements.GameAchievementTemplate;
import io.Username;
import util.fgui.GraphicsProvider;
import util.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class AchievementNotificationPanel extends JPanel implements GraphicsProvider {
    private final GameAchievementTemplate achievement;
    private final String username;

    public static void register(){
        AchievementNotification.hookNotifier(AchievementNotificationPanel::fetch);
    }

    public AchievementNotificationPanel(GameAchievementTemplate achievement, String username) {
        this.achievement = achievement;
        this.username = username;
        this.setOpaque(false);
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setPreferredSize(new Dimension(400, 200));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Paint round rectangle background
        g2.setColor(LauncherGUI.getMainFrame().getBackground().darker());
        g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), this.getHeight() / 5, this.getHeight() / 5);
        // Call paint to draw the icon no matter what
        int iconSize = this.getHeight() / 2;
        // Size g to 0.1 - 0.9 of the panel size
        int iconGap = (int) (iconSize * 0.1);
        g2.translate(iconGap, (int) (iconSize * 0.4));
        achievement.icon().paint(g2, iconSize * 0.8);
        g2.dispose();
        g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Also draw the name and description if infoDisplayed is true
        g2.setColor(LaunchEssentials.launchVersionFontColor);
        // Get the base font
        // Get font every single time because it may be changed by font processors
        Font baseFont = new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, 1);
        AffineTransform sAt = AffineTransform.getScaleInstance(iconSize * 0.075, iconSize * 0.075);
        AffineTransform at = AffineTransform.getScaleInstance(iconSize * 0.15, iconSize * 0.15);
        AffineTransform dAt = AffineTransform.getScaleInstance(iconSize * 0.2, iconSize * 0.2);
        Font smallerFont = baseFont.deriveFont(sAt);
        Font mediumFont = baseFont.deriveFont(at);
        Font biggerFont = baseFont.deriveFont(dAt);
        g2.setColor(LaunchEssentials.launchPlayerSpecialFontColor);
        g2.setFont(smallerFont);
        FontMetrics fmS = g2.getFontMetrics();
        String info = "Achievement Unlocked to " + username;
        g2.drawString(info, (float) ((getWidth() - fmS.stringWidth(info)) * 0.5), (float) (iconSize * 0.1));
        g2.setColor(LaunchEssentials.launchVersionFontColor);
        g2.setFont(biggerFont);
        String name = achievement.name();
        FontMetrics fmName = g2.getFontMetrics();
        g2.drawString(name, (float) ((getWidth() - fmName.stringWidth(name)) * 0.5), (float) (iconSize * 0.3));
        g2.setFont(mediumFont);
        // Multiple line description if too long
        String det = achievement.details();
        FontMetrics fm = g2.getFontMetrics();
        int availableWidth = this.getWidth() - (int) (iconSize * 1.2);
        StringBuilder newDet = new StringBuilder();
        if (availableWidth < 0) return;
        String[] words = det.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            String testLine = currentLine + (currentLine.length() == 0 ? "" : " ") + word;
            int testWidth = fm.stringWidth(testLine);
            if (testWidth > availableWidth) {
                if (currentLine.isEmpty()) {
                    // Single word too long, need to truncate
                    String truncatedWord = word;
                    while (fm.stringWidth(truncatedWord + "...") > availableWidth && !truncatedWord.isEmpty()) {
                        truncatedWord = truncatedWord.substring(0, truncatedWord.length() - 1);
                    }
                    newDet.append(truncatedWord).append("...\n");
                } else {
                    newDet.append(currentLine).append("\n");
                    currentLine = new StringBuilder(word);
                }
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        if (!currentLine.isEmpty()) {
            newDet.append(currentLine);
        }
        String[] lines = newDet.toString().split("\n");
        for (int i = 0; i < lines.length; i++) {
            g2.drawString(lines[i], (float) (iconSize), (float) (iconSize * 0.25 + (i + 1) * fm.getHeight()));
        }
        g2.dispose();
        // Paint close button on top
        super.paint(g);
    }
    public static void fetch() {
        if (AchievementNotification.isUpdated()) {
            if (AchievementNotification.hasNext()) {
                Pair<Username, GameAchievementTemplate> notification = AchievementNotification.popNotification();
                AchievementNotificationPanel panel = new AchievementNotificationPanel(notification.getLast(), notification.getFirst().toString());
                System.out.println("Displaying achievement notification for " + notification.getFirst() + ": " + notification.getLast().name());
                LauncherGUI.INSTANCE.accept(panel);
            }
        }
    }
    public static void next() {
        LauncherGUI.INSTANCE.remove();
        if (AchievementNotification.hasNext()) {
            Pair<Username, GameAchievementTemplate> notification = AchievementNotification.popNotification();
            AchievementNotificationPanel panel = new AchievementNotificationPanel(notification.getLast(), notification.getFirst().toString());
            LauncherGUI.INSTANCE.accept(panel);
        }
    }
    @Override
    public Component get() {
        return this;
    }
}