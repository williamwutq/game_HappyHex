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
        return new GamePanel();
    }
    public static JPanel fetchPiecePanel(){
        return new PiecePanel();
    }
}