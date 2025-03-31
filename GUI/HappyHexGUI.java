package GUI;

import Hex.HexEngine;
import Hex.Piece;
import Hex.Queue;
import Launcher.LauncherGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HappyHexGUI{
    public static void play(int size, int queueSize, int delay, boolean easy) {
        // Frame
        JFrame frame = new JFrame("HappyHex Version " + Launcher.LaunchEssentials.currentGameVersion);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(GameEssentials.gameBackGroundColor);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(400, 400));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Custom code to execute when the window is closing
                // Log if it has score and reset
                if(GameEssentials.turn != 0) {
                    GameEssentials.logGame();
                }
                GameEssentials.resetGame();
                // Close
                frame.dispose();
            }
        });
        initialize(size, queueSize, delay, easy, frame);
        frame.add(fetchGamePanel(), BorderLayout.CENTER);
        frame.add(fetchPiecePanel(), BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.repaint();
    }
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
                this.setForeground(GameEssentials.gameDisplayFontColor);
                this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(gamePanel.getWidth() - (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), 5, (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        JLabel turnLabel = new JLabel("TURN 0", SwingConstants.CENTER){
            @Override
            public void paint(java.awt.Graphics g){
                this.setText("TURN " + GameEssentials.turn);
                this.setForeground(GameEssentials.gameDisplayFontColor);
                this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, (int)(HexButton.getActiveSize()/2)));
                this.setBounds(0, 5, (int)((GameEssentials.engine().getRadius()-0.5) * HexButton.getActiveSize()), (int)(HexButton.getActiveSize()));
                super.paintComponent(g);
            }
        };
        scoreLabel.setForeground(GameEssentials.gameDisplayFontColor);
        scoreLabel.setBounds(0, 0, 1, 1);
        scoreLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        turnLabel.setForeground(GameEssentials.gameDisplayFontColor);
        turnLabel.setBounds(0, 0, 1, 1);
        turnLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        gamePanel.add(turnLabel);
        gamePanel.add(scoreLabel);
        return gamePanel;
    }
    public static JPanel fetchPiecePanel(){
        return new PiecePanel();
    }
}