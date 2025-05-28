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
import java.util.ArrayList;

public class GamesPanel extends JPanel {
    double size;
    private JScrollPane scrollPane;
    public GamesPanel(){
        super();
        this.setOpaque(true);
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        size = 1;
        ArrayList<HexLogger> loggers = LaunchEssentials.smartFindLoggers();
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
        size = Math.min(rawSize.width, rawSize.height) / 12.0;
        int size5 = (int) (size*5.0);
        for (Component game : getComponents()){
            if (game instanceof ListGame listGame){
                listGame.setPreferredSize(new Dimension(rawSize.width, size5));
                listGame.setSize(new Dimension(rawSize.width, size5));
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
        private final JButton gameStartButton;
        private final GUI.InlineInfoPanel gameScorePanel, gameTurnPanel;
        public ListGame(HexLogger logger) {
            final String fullname = logger.getDataFileName();
            this.fileNameLabel = new JLabel(fullname.substring(0, fullname.length() - 12));
            this.gameStartButton = new JButton("RESUME");
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
                LauncherGUI.startGame(fullname);
            });

            gameScorePanel.setTitle("Score: ");
            gameScorePanel.setInfo(logger.getScore() + "");
            gameTurnPanel.setTitle("Turn: ");
            gameTurnPanel.setInfo(logger.getTurn() + "");

            this.add(Box.createVerticalGlue());
            this.add(fileNameLabel);
            this.add(gameScorePanel);
            this.add(gameTurnPanel);
            this.add(Box.createVerticalGlue());
            this.add(gameStartButton);
            this.add(Box.createVerticalGlue());
        }

        public void doLayout() {
            int sizeInt = (int)size;
            gameScorePanel.setSize(sizeInt);
            gameTurnPanel.setSize(sizeInt);
            fileNameLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.BOLD, sizeInt));
            gameStartButton.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, sizeInt));
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
}
