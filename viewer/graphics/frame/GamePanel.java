package viewer.graphics.frame;

import hex.Block;
import hex.HexEngine;

import java.awt.*;

public class GamePanel extends Panel {
    /** Constant value representing sin(60°), used in hexagon geometry calculations. */
    public static final double sinOf60 = Math.sqrt(3) / 2;
    private HexEngine engine;
    private double size;
    private double widthExtension;
    private double heightExtension;
    public GamePanel(HexEngine engine){
        this.engine = engine;
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
        widthExtension = width * 0.5 - length * size * sinOf60;
        heightExtension = height * 0.5 - 3.5 * size;
    }
    /**
     * Paints this engine panel containing all its engine components
     * This calls to {@link #resetSize()} to ensure the size of components are correct
     * @param g the Graphics context to use for painting
     */
    public void paint(Graphics g){
        resetSize();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0, getWidth(), getHeight());
        for (int i = 0; i < engine.length(); i ++){
            paintHexagon(g.create(), engine.getBlock(i));
        }
    }
    /**
     * Paints a simple hexagon based the state and position of a {@link Block}.
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
            xPoints[i] = 3 + (int) Math.round(widthExtension + size * (x + sinOf60 + Math.sin(angle) * 0.9));
            yPoints[i] = 3 + (int) Math.round(heightExtension + size * (y + 1.0 + Math.cos(angle) * 0.9));
        }
        g.fillPolygon(xPoints, yPoints, 6);
    }
    public static void main(String[] args){
        viewer.Viewer.test(new GamePanel(new HexEngine(9)));
    }
}
