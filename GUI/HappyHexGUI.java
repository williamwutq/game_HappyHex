package GUI;

import Hex.HexEngine;
import Hex.Queue;

import javax.swing.*;
import java.awt.*;

class HappyHexGUI{
    public static void play(int size, int queueSize, int delay) {
        // Frame
        JFrame frame = new JFrame("Test: GamePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        // Engine
        HexEngine engine = new HexEngine(size);
        Queue queue = new Queue(queueSize);
        GameEssentials.setEngine(engine);
        GameEssentials.setQueue(queue);
        GameEssentials.setWindow(frame);
        GameEssentials.setDelay(delay);
        GameEssentials.calculateButtonSize();
        GamePanel gamePanel = new GamePanel();
        PiecePanel piecePanel = new PiecePanel();
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(piecePanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.repaint();
    }
    public static void main(String[] args) {
        play(9, 6, 200);
    }
}
