package GUI;

import Hex.*;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public GamePanel() {
        super();
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        // Construct buttons
        for (int i = 0; i < GameEssentials.engine().length(); i++) {
            this.add(new EngineButton(i));
        }
    }
    public void paint(java.awt.Graphics g) {
        recalculate();
        // print component and children
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
    public void recalculate(){
        // Calculate minimum size
        double horizontalCount = GameEssentials.engine().getRadius() * 4 - 2;
        double verticalCount = GameEssentials.engine().getRadius() * 3 - 1;
        double minSize = Math.min((this.getHeight()-5) / verticalCount, (this.getWidth()-5) / horizontalCount / GameEssentials.sinOf60 / 2);
        HexButton.setSize(minSize);
    }
    // Test: autoplay
    public static void autoplay(int size, int queueSize, int delay){
        // Frame
        JFrame frame = new JFrame("Test: GamePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setAlwaysOnTop(true);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        // Engine
        HexEngine engine = new HexEngine(size);
        Queue queue = new Queue(queueSize);
        GameEssentials.setEngine(engine);
        GameEssentials.setQueue(queue);
        GameEssentials.setWindow(frame);
        GameEssentials.setDelay(delay);
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        // Autoplay
        int turns = 0;
        int score = 0;
        while (true) {
            Piece piece = queue.getFirst();
            // Check positions
            try {
                engine.add(engine.checkPositions(piece).get((int) (Math.random() * engine.checkPositions(piece).size())), piece);
                queue.next();
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                int index = 1;
                while(index < queueSize) {
                    piece = queue.get(index);
                    try {
                        engine.add(engine.checkPositions(piece).get((int) (Math.random() * engine.checkPositions(piece).size())), piece);
                        queue.fetch(index);
                        break;
                    } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                        index ++;
                    }
                }
                if(index >= queueSize){
                    // No possible solutions
                    break;
                }
            }
            score += piece.length();
            gamePanel.repaint();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
            score += engine.eliminate() * 3;
            gamePanel.repaint();
            turns++;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
        }
        engine.reset();
        gamePanel.repaint();
        System.out.println("Auto Play: Game Over");
        System.out.println("This game lasted for " + turns + " turns.");
        System.out.println("The total score is " + score + " points.");
    }
    public static boolean checkEnd(){
        for(int i = 0; i < GameEssentials.queue().length(); i ++){
            if(!GameEssentials.engine().checkPositions(GameEssentials.queue().get(i)).isEmpty()){
                return false;
            }
        }
        return true;
    }
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
        // Calculate minimum size
        double horizontalCount = GameEssentials.engine().getRadius() * 4 - 2;
        double verticalCount = GameEssentials.engine().getRadius() * 3 - 7;
        double minSize = Math.min((frame.getHeight()-28-5) / verticalCount, (frame.getWidth()-5) / horizontalCount / GameEssentials.sinOf60 / 2);
        // Set size
        HexButton.setSize(minSize);
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
