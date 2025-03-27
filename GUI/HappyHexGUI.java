package GUI;

import Hex.HexEngine;
import Hex.Piece;
import Hex.Queue;

import javax.swing.*;
import java.awt.*;

public class HappyHexGUI{
    public static void play(int size, int queueSize, int delay) {
        // Legacy method
        play(size, queueSize, delay, false);
    }
    public static void play(int size, int queueSize, int delay, boolean easy) {
        // Frame
        JFrame frame = new JFrame("HappyHex Version 0.3.2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        // Engine
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
        GamePanel gamePanel = new GamePanel();
        PiecePanel piecePanel = new PiecePanel();
        JLabel scoreLabel = new JLabel("SCORE 0", SwingConstants.CENTER){
            @Override
            public void paint(java.awt.Graphics g){
                this.setText("SCORE " + GameEssentials.score);
                this.setFont(new Font("Source Code Pro", Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(gamePanel.getWidth() - (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), 5, (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        JLabel turnLabel = new JLabel("TURN 0", SwingConstants.CENTER){
            @Override
            public void paint(java.awt.Graphics g){
                this.setText("TURN " + GameEssentials.turn);
                this.setFont(new Font("Source Code Pro", Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(0, 5, (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBounds(0, 0, 1, 1);
        scoreLabel.setFont(new Font("Source Code Pro", Font.BOLD, 1));
        turnLabel.setForeground(Color.BLACK);
        turnLabel.setBounds(0, 0, 1, 1);
        turnLabel.setFont(new Font("Source Code Pro", Font.BOLD, 1));
        gamePanel.add(turnLabel);
        gamePanel.add(scoreLabel);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(piecePanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.repaint();
    }
}
