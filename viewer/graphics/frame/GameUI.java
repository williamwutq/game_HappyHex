package viewer.graphics.frame;

import hexio.HexLogger;
import viewer.graphics.interactive.HexButton;
import viewer.logic.Tracker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.io.IOException;

public class GameUI extends JComponent {
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private Tracker tracker;
    private final HexButton startButton;
    private final GamePanel gamePanel;

    public GameUI(HexLogger logger){
        tracker = new Tracker(logger);
        gamePanel = new GamePanel(tracker.engineAt(0), tracker.queueAt(0));
        startButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy + radius);
                path.lineTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy - radius);
                path.lineTo(cx + radius / sinOf60, cy);
                path.closePath();
                return path;
            }
        };
        this.add(gamePanel);
    }
    public static void main(String[] args) throws IOException {
        HexLogger logger = HexLogger.generateBinaryLoggers().get(0);
        logger.readBinary();
        viewer.Viewer.test(new GameUI(logger));
    }
}
