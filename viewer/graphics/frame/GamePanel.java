package viewer.graphics.frame;

import hex.Block;
import hex.HexEngine;
import hex.Piece;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class GamePanel extends Panel {
    /** Constant value representing sin(60°), used in hexagon geometry calculations. */
    public static final double sinOf60 = Math.sqrt(3) / 2;
    private HexEngine engine;
    private Piece[] queue;
    private double size;
    private int halfHeight;
    private int halfWidth;
    private GeneralPath path;
    private final double[] xReferencePoints = {sinOf60, sinOf60 * 1.9, sinOf60 * 1.9, sinOf60, sinOf60 * 0.1, sinOf60 * 0.1};
    private final double[] yReferencePoints = {1.9, 1.45, 0.55, 0.1, 0.55, 1.45};
    public GamePanel(HexEngine engine, Piece[] queue){
        this.engine = engine;
        this.queue = queue;
        path = new GeneralPath();
        resetSize();
    }
    /**
     * Reset the size of individual hexagons in the panel by to match the maximum size allowed in this panel.
     */
    public void resetSize(){
        // Calculate size
        halfHeight = getHeight()/2-3;
        halfWidth = getWidth()/2-3;
        int radius = engine.getRadius();
        int length = radius * 2 - 1;
        double vertical = radius * 1.5 + 2;
        size = (Math.min(halfHeight / vertical, halfWidth / sinOf60 / length));
    }
    /**
     * Paints this engine panel containing all its engine components
     * This calls to {@link #resetSize()} to ensure the size of components are correct
     * @param g the Graphics context to use for painting
     */
    public void paint(Graphics g){
        resetSize();
        path = new GeneralPath();
        Graphics g2 = g.create();
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0, getWidth(), getHeight());
        Block block;
        // Paint queue
        double move = (engine.getRadius() - 1) * 0.75;
        for (int i = 0; i < queue.length; i ++){
            double x = (i - queue.length * 0.5) * 3 * sinOf60;
            for (int j = 0; j < queue[i].length(); j ++){
                block = queue[i].getBlock(j);
                if (block != null) {
                    paintHexagon(0, 0, block.X() + x, block.Y() + move);
                }
            }
        }
        // Paint engine
        move = engine.getRadius() * sinOf60 - sinOf60 * 0.5;
        for (int i = 0; i < engine.length(); i ++){
            block = engine.getBlock(i);
            paintHexagon(0, 0, block.X() - move, block.Y() - 1.75);
        }
        Graphics2D g3 = (Graphics2D) g.create();
        g3.fill(path);
    }
    public final void paintHexagon(double widthExtension, double heightExtension, double x, double y) {
        path.moveTo(halfWidth + widthExtension + size * (2 * x + xReferencePoints[0]), halfHeight + heightExtension + size * (2 * y + yReferencePoints[0]));
        for (int i = 1; i < 6; i++) {
            path.lineTo(halfWidth + widthExtension + size * (2 * x + xReferencePoints[i]), halfHeight + heightExtension + size * (2 * y + yReferencePoints[i]));
        }
        path.closePath();
    }
    public static void main(String[] args){
        Piece piece1 = new Piece(4, 4);
        piece1.add(-1, -1);
        piece1.add(0, -1);
        piece1.add(-1, 0);
        piece1.add(0, 1);
        Piece piece2 = new Piece(4, 4);
        piece2.add(0, -1);
        piece2.add(0, 1);
        piece2.add(1, 0);
        piece2.add(1, 1);
        Piece piece3 = new Piece(3, 4);
        piece3.add(0, -1);
        piece3.add(0, 0);
        piece3.add(0, 1);
        Piece[] queue = new Piece[]{piece1, piece2, piece3};
        viewer.Viewer.test(new GamePanel(new HexEngine(13), queue));
    }
}
