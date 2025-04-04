package GUI;

import Hex.HexEngine;
import Hex.Piece;
import Hex.Queue;

import javax.swing.*;

public class HappyHexGUI{
    public static void initialize(int size, int queueSize, int delay, boolean easy, JFrame frame){
        if(easy) {
            Piece.setEasy();
        }
        GameEssentials.setEngine(new HexEngine(size));
        GameEssentials.setQueue(new Queue(queueSize));
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