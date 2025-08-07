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

import GUI.InlineInfoPanel;
import Launcher.LaunchEssentials;
import Launcher.LauncherGUI;
import hexio.HexLogger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.GameTime.generateSimpleTime;

public class GamesPanel extends JPanel {
    double size;
    private JScrollPane scrollPane;
    public GamesPanel(){
        super();
        this.setOpaque(true);
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        size = 1;
        List<HexLogger> loggers = LaunchEssentials.smartFindLoggers();
        for (HexLogger logger : loggers) {
            this.add(new ListGame(logger));
        }
        scrollPane = new JScrollPane(this);
        scrollPane.setBackground(LaunchEssentials.launchBackgroundColor);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants. HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }
    public JScrollPane getScrollPane(){return scrollPane;}

    public void doLayout(){
        Dimension rawSize = this.getParent().getBounds().getSize();
        size = Math.min(rawSize.width, rawSize.height) / 15.0;
        int size5 = (int) (size*5.0);
        for (Component game : getComponents()){
            if (game instanceof ListGame listGame){
                listGame.setPreferredSize(new Dimension(rawSize.width, size5));
                listGame.setSize(new Dimension(rawSize.width, size5));
                listGame.doLayout();
            }
        }
        super.doLayout();
    }


    public void paint(Graphics g){
        doLayout();
        super.paintChildren(g);
    }

    private class ListGame extends JPanel {
        private final JLabel fileNameLabel;
        private final JButton gameStartButton, gameViewButton;
        private final GUI.InlineInfoPanel gameScorePanel, gameTurnPanel;
        public ListGame(HexLogger logger) {
            this.fileNameLabel = new JLabel(logger.getDataFileName());
            this.gameStartButton = new JButton("RESUME");
            this.gameViewButton = new JButton("VIEW");
            this.gameScorePanel = new InlineInfoPanel();
            this.gameTurnPanel = new InlineInfoPanel();
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            this.setAlignmentX(CENTER_ALIGNMENT);
            this.setAlignmentY(CENTER_ALIGNMENT);
            this.setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setOpaque(false);

            fileNameLabel.setVerticalAlignment(SwingConstants.CENTER);
            fileNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            fileNameLabel.setAlignmentX(CENTER_ALIGNMENT);
            fileNameLabel.setAlignmentY(CENTER_ALIGNMENT);
            fileNameLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.BOLD, 16));
            fileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            gameStartButton.setAlignmentX(CENTER_ALIGNMENT);
            gameStartButton.setAlignmentY(CENTER_ALIGNMENT);
            gameStartButton.setBorder(BorderFactory.createEmptyBorder());
            gameStartButton.setForeground(LaunchEssentials.launchConfirmButtonBackgroundColor);
            gameStartButton.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, 16));
            gameStartButton.addActionListener(e -> {
                LauncherGUI.startGame(logger.getDataFileName());
            });

            gameViewButton.setAlignmentX(CENTER_ALIGNMENT);
            gameViewButton.setAlignmentY(CENTER_ALIGNMENT);
            gameViewButton.setBorder(BorderFactory.createEmptyBorder());
            gameViewButton.setForeground(LaunchEssentials.launchConfirmButtonBackgroundColor);
            gameViewButton.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, 16));
            gameViewButton.addActionListener(e -> {
                runGameViewer(logger.getDataFileName());
            });

            gameScorePanel.setTitle("   Score: ");
            gameScorePanel.setInfo(logger.getScore() + "   ");
            gameTurnPanel.setTitle("   Turn: ");
            gameTurnPanel.setInfo(logger.getTurn() + "   ");

            this.add(Box.createVerticalGlue());
            this.add(fileNameLabel);
            this.add(Box.createVerticalGlue());
            this.add(gameScorePanel);
            this.add(gameTurnPanel);
            this.add(Box.createVerticalGlue());
            this.add(gameStartButton);
            this.add(Box.createVerticalGlue());
            this.add(gameViewButton);
            this.add(Box.createVerticalGlue());
            this.add(Box.createVerticalGlue());
        }

        public void doLayout() {
            int sizeInt = (int)size/2;
            gameScorePanel.setSize(sizeInt);
            gameTurnPanel.setSize(sizeInt);
            fileNameLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.BOLD, sizeInt));
            gameStartButton.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, sizeInt));
            gameViewButton.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, sizeInt));
            super.doLayout();
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int sizeInt = (int) size;
            int sizeQ = (int) (size/4.0);
            int sizeP = (int) (size/8.0);
            g2.setColor(this.getBackground());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillRoundRect(3+sizeP, 3+sizeP, getWidth()-6-sizeQ, getHeight()-6-sizeQ, sizeInt, sizeInt);
            g2.dispose();
            paintChildren(g);
        }
    }

    public static void runGameViewer(String filename) {
        File jarFile = new File("viewer/GameViewer.jar");
        // Check if JAR exists
        if (!jarFile.exists()) {
            System.err.println(generateSimpleTime() + " GameViewer Launcher: Launch failed because GameViewer jar is not found.");
            return;
        }
        // Build the command to launch the JAR
        List<String> command = new ArrayList<>();
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        command.add(javaBin);
        command.add("-jar");
        command.add(jarFile.getPath());
        // If filename is a file in data/, convert it to an absolute path
        if (filename.startsWith("data/")){
            Path filePath;
            try {
                filePath = Path.of(filename);
            } catch (InvalidPathException e) {
                System.err.println(generateSimpleTime() + " GameViewer Launcher: Launch failed because " + filename + " does not exist.");
                return;
            }
            filename = filePath.toAbsolutePath().toString();
        }
        // Add -file <filename> if filename is valid
        if (filename != null && !filename.trim().isEmpty()) {
            command.add("-file");
            command.add(filename);
        }
        try {
            new ProcessBuilder(command).start();
        } catch (IOException e) {
            System.err.println(generateSimpleTime() + " GameViewer Launcher: Launch failed because " + e.getMessage());
        }
    }
}
