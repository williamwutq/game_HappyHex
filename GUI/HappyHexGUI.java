package GUI;

import Hex.HexEngine;
import Hex.Piece;
import Hex.Queue;
import Launcher.LaunchEssentials;

import javax.swing.*;
import java.awt.*;

public class HappyHexGUI{
    public static void initialize(int size, int queueSize, int delay, boolean easy, JFrame frame){
        if(easy) {
            Piece.setEasy();
        }
        HexEngine engine = new HexEngine(size);
        Queue queue = new Queue(queueSize);
        GameEssentials.setEngine(engine);
        GameEssentials.setQueue(queue);
        GameEssentials.setWindow(frame);
        GameEssentials.setDelay(delay);
        GameEssentials.calculateButtonSize();
    }
    public static JPanel fetchGamePanel(){
        GamePanel gamePanel = new GamePanel();
        JLabel scoreLabel = new JLabel("SCORE 0", SwingConstants.CENTER){
            @Override
            public void paint(java.awt.Graphics g){
                this.setText("SCORE " + GameEssentials.score);
                this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(gamePanel.getWidth() - (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), 5, (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        JLabel turnLabel = new JLabel("TURN 0", SwingConstants.CENTER){
            @Override
            public void paint(java.awt.Graphics g){
                this.setText("TURN " + GameEssentials.turn);
                this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(0, 5, (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        JLabel playerLabel = new JLabel(LaunchEssentials.getCurrentPlayer(), SwingConstants.CENTER){
            @Override
            public void paint(java.awt.Graphics g){
                this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(gamePanel.getWidth() - (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), gamePanel.getHeight() - (int)Math.round(1.5 * HexButton.getActiveSize()), (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        scoreLabel.setForeground(GameEssentials.gameDisplayFontColor);
        scoreLabel.setBounds(0, 0, 1, 1);
        scoreLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        turnLabel.setForeground(GameEssentials.gameDisplayFontColor);
        turnLabel.setBounds(0, 0, 1, 1);
        turnLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        playerLabel.setForeground(GameEssentials.gameDisplayFontColor);
        playerLabel.setBounds(0, 0, 1, 1);
        playerLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        gamePanel.add(turnLabel);
        gamePanel.add(scoreLabel);
        gamePanel.add(playerLabel);
        return gamePanel;
    }
    public static JPanel fetchPiecePanel(){
        return new PiecePanel();
    }
}