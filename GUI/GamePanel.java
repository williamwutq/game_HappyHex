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

package GUI;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private static GameInfoPanel turnLabel;
    private static GameInfoPanel scoreLabel;
    private static GameInfoPanel playerLabel;
    private static GameQuitButton quitButton;
    public GamePanel(int engineLength, int turn, int score, String player) {
        super();
        this.setBackground(GameEssentials.gameBackgroundColor);
        this.setLayout(null);
        // Construct buttons
        for (int i = 0; i < engineLength; i++) {
            this.add(new EngineButton(i));
        }
        // Construct labels
        turnLabel = new GameInfoPanel();
        scoreLabel = new GameInfoPanel();
        playerLabel = new GameInfoPanel();
        quitButton = new GameQuitButton();
        turnLabel.setTitle("TURN");
        scoreLabel.setTitle("SCORE");
        playerLabel.setTitle("PLAYER");
        turnLabel.setInfo(turn + "");
        scoreLabel.setInfo(score + "");
        playerLabel.setInfo(player);
        turnLabel.setBounds(0, 0, 100, 100);
        scoreLabel.setBounds(300, 0, 100, 100);
        playerLabel.setBounds(300, 300, 100, 100);
        this.add(turnLabel);
        this.add(scoreLabel);
        this.add(playerLabel);
        this.add(quitButton);
    }
    public void paint(java.awt.Graphics g) {
        // print component and children
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
    public void doLayout(){
        GameEssentials.calculateButtonSize();
        int height = GameEssentials.window().getHeight();
        int width = GameEssentials.window().getWidth();
        int gamePanelExtension = GameEssentials.getGamePanelWidthExtension();

        double minSize = Math.min(height - 33, width - 5);
        int labelWidth = (int) Math.round(minSize / 6.0);
        int quitWidth = (int) Math.round(minSize / 8.0);
        int labelHeight = (int) Math.round(minSize / 12.0);
        Dimension dimension = new Dimension(labelWidth, labelHeight);
        Dimension quitDimension = new Dimension(quitWidth, labelHeight);
        SimpleButton.setSize((int)Math.round(labelHeight*0.6));

        int margin = 3;
        int piecePanelSize = (int) Math.round(5 * HexButton.getActiveSize());
        int bottomOffset = labelHeight + 28 + margin;

        int right = width - gamePanelExtension - labelWidth - margin;
        int bottom = height - piecePanelSize - bottomOffset;

        turnLabel.setPreferredSize(dimension);
        scoreLabel.setPreferredSize(dimension);
        playerLabel.setPreferredSize(dimension);
        quitButton.setPreferredSize(dimension);
        quitButton.resetSize();

        turnLabel.setBounds(new Rectangle(new Point(margin + gamePanelExtension, margin), dimension));
        scoreLabel.setBounds(new Rectangle(new Point(right, margin), dimension));
        playerLabel.setBounds(new Rectangle(new Point(right, bottom), dimension));
        quitButton.setBounds(new Rectangle(new Point(right + labelWidth - quitWidth, bottom - labelHeight), quitDimension));
    }
    public void updateDisplayedInfo(int turn, int score){
        turnLabel.setInfo(turn + "");
        scoreLabel.setInfo(score + "");
    }
}
