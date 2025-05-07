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
    private double engineWidthExtension;
    private double queueWidthExtension;
    private double engineHeightExtension;
    private double queueHeightExtension;
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
        int height = getHeight()-6;
        int width = getWidth()-6;
        double horizontalCount = engine.getRadius() * 4 - 2;
        double verticalCount = engine.getRadius() * 3 + 4;
        size = (Math.min(height / verticalCount, width / horizontalCount / sinOf60));
        int length = engine.getRadius() * 2 - 1;
        engineWidthExtension = width * 0.5 - length * size * sinOf60;
        queueWidthExtension = width * 0.5 - queue.length * 3 * size * sinOf60;
        engineHeightExtension = height * 0.5 - 3.5 * size;
        queueHeightExtension = height - 3.5 * size;
    }
    /**
     * Paints this engine panel containing all its engine components
     * This calls to {@link #resetSize()} to ensure the size of components are correct
     * @param g the Graphics context to use for painting
     */
    public void paint(Graphics g){
        resetSize();
        Graphics g2 = g.create();
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0, getWidth(), getHeight());
        // Paint queue
        for (int i = 0; i < queue.length; i ++){
            for (int j = 0; j < queue[i].length(); j ++){
                if (queue[i].getBlock(j)!= null) {
                    paintHexagon(g.create(), queue[i].getBlock(j), i);
                }
            }
        }
        // Paint engine
        for (int i = 0; i < engine.length(); i ++){
            paintHexagon(g.create(), engine.getBlock(i));
        }
    }
    /**
     * Paints a simple hexagon based the state and position of a {@link Block} in the game engine.
     * This hexagon does not contain any highlight.
     * @param g the Graphics context to use for painting
     * @param block the block to be painted
     */
    public final void paintHexagon(java.awt.Graphics g, Block block) {
        // Paint Basic Polygon
        if (block.getState()) {
            g.setColor(Color.LIGHT_GRAY);
        } else {
            g.setColor(Color.GRAY);
        }
        double x = block.X() * 2;
        double y = block.Y() * 2;
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = 3 + (int) Math.round(engineWidthExtension + size * (x + xReferencePoints[i]));
            yPoints[i] = 3 + (int) Math.round(engineHeightExtension + size * (y + yReferencePoints[i]));
        }
        g.fillPolygon(xPoints, yPoints, 6);
        g.dispose();
    }
    /**
     * Paints a simple hexagon based the state and position of a {@link Block} in the game queue.
     * This hexagon does not contain any highlight.
     * @param g the Graphics context to use for painting
     * @param block the block to be painted
     * @param index the index of the piece in the queue
     */
    public final void paintHexagon(java.awt.Graphics g, Block block, int index) {
        // Paint Basic Polygon
        if (block.getState()) {
            g.setColor(Color.LIGHT_GRAY);
        } else {
            g.setColor(Color.GRAY);
        }
        double x = block.X() * 2 + (index * 6) * sinOf60;
        double y = block.Y() * 2;
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        for (int i = 0; i < 6; i++) {
            xPoints[i] = 3 + (int) Math.round(queueWidthExtension + size * (x + 2 * sinOf60 + xReferencePoints[i]));
            yPoints[i] = 3 + (int) Math.round(queueHeightExtension + size * (y + yReferencePoints[i]));
        }
        g.fillPolygon(xPoints, yPoints, 6);
        g.dispose();
    }
    public final void paintHexagon(int widthExtension, int heightExtension, double x, double y) {
        path.moveTo(widthExtension + size * (x + xReferencePoints[0]), heightExtension + size * (y + yReferencePoints[0]));
        for (int i = 1; i < 6; i++) {
            path.lineTo(widthExtension + size * (x + xReferencePoints[i]), heightExtension + size * (y + yReferencePoints[i]));
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
        viewer.Viewer.test(new GamePanel(new HexEngine(9), queue));
    }
}
