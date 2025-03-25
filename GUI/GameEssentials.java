package GUI;

import Hex.HexEngine;
import Hex.Queue;

import javax.swing.*;
import java.awt.*;

/**
 * The {@link GameEssentials} class provides essential game utilities, including {@link #generateColor() color generation}
 * and {@link #paintHexagon(Graphics, Color, double, double, double, double) methods} for efficiently rendering hexagons.
 * <p>
 * This class is final and cannot be extended.
 */
public final class GameEssentials {
    /** The sine of 60 degrees, used for hexagonal calculations. For scaling, use {@code GameEssentials.sinOf60 * 2}. */
    public static final double sinOf60 = Math.sqrt(3) / 4;
    /** A scaling factor used for filling hexagons, ranging between 0.0 and 1.0. */
    private static double fill = 0.9;
    /** The delay to a typical action of the game, in ms*/
    private static int actionDelay = 80;
    /** The main game engine object. */
    private static HexEngine engine;
    /** The main game queue of pieces, which contain a number of {@link Hex.Piece}. */
    private static Queue queue;
    /** The main window of the game. */
    private static JFrame window;

    private static int selectedPieceIndex = -1;
    private static int selectedBlockIndex = -1;

    public static int turn = 0;
    public static int score = 0;

    /**
     * Sets the fill ratio for hexagons, ensuring it remains within the valid range (0.0, 1.0].
     *
     * @param newFill the new fill ratio to set; must be between 0.0 (exclusive) and 1.0 (inclusive).
     */
    public static void setFill(double newFill) {
        if (newFill <= 1.0 && newFill > 0.0) {
            fill = newFill;
        }
    }
    /**
     * Sets the typical action delay of the game in ms, must be an integer between 10 and 100000.
     *
     * @param delay the new delay of a typical action in the game; must be between 10 (inclusive) and 100000 (inclusive).
     */
    public static void setDelay(int delay) {
        if (delay <= 100000 && delay >= 10) {
            GameEssentials.actionDelay = delay;
        }
    }
    /**
     * Get the typical action delay of the game in ms
     *
     * @return the delay of a typical action in the game.
     */
    public static int getDelay() {
        return actionDelay;
    }
    /**
     * Generates a random color from a predefined set of 12 distinct colors.
     *
     * @return a randomly selected {@code Color} object.
     */
    public static Color generateColor() {
        Color[] colors = new Color[12];
        colors[0] = new Color(0, 0, 240);
        colors[1] = new Color(0, 100, 190);
        colors[2] = new Color(0, 180, 180);
        colors[3] = new Color(0, 180, 120);
        colors[4] = new Color(0, 210, 0);
        colors[5] = new Color(100, 180, 0);
        colors[6] = new Color(180, 180, 0);
        colors[7] = new Color(200, 90, 0);
        colors[8] = new Color(210, 0, 0);
        colors[9] = new Color(200, 0, 120);
        colors[10] = new Color(180, 0, 180);
        colors[11] = new Color(100, 0, 200);
        return colors[(int) (Math.random() * 12)];
    }
    /**
     * Paints a hexagon at the origin (0,0) with a specified color and size.
     *
     * @param g     the {@code Graphics} context used for rendering.
     * @param color the {@code Color} of the hexagon.
     * @param size  the size (radius) of the hexagon.
     * @see #paintHexagon(Graphics, Color, double, double, double, double) Full version
     */
    public static void paintHexagon(Graphics g, Color color, double size) {
        paintHexagon(g, color, 0, 0, size, GameEssentials.fill);
    }
    /**
     * Paints a hexagon at a specified (x, y) position with a given color and size.
     *
     * @param g     the {@code Graphics} context used for rendering.
     * @param color the {@code Color} of the hexagon.
     * @param x     the x-coordinate of the hexagon's center.
     * @param y     the y-coordinate of the hexagon's center.
     * @param size  the size (radius) of the hexagon.
     * @see #paintHexagon(Graphics, Color, double, double, double, double) Full version
     */
    public static void paintHexagon(Graphics g, Color color, double x, double y, double size) {
        paintHexagon(g, color, x, y, size, GameEssentials.fill);
    }
    /**
     * Paints a hexagon at a specified (x, y) position with a given color, size, and fill ratio.
     *
     * @param g     the {@code Graphics} context used for rendering.
     * @param color the {@code Color} of the hexagon.
     * @param x     the x-coordinate of the hexagon's center.
     * @param y     the y-coordinate of the hexagon's center.
     * @param size  the size (radius) of the hexagon.
     * @param fill  the fill ratio, affecting how much of the hexagon is drawn.
     *              It is usually between 0.0 (exclusive) and 1.0 (inclusive),
     *              but greater values may also be used in special cases.
     * @see #paintHexagon(Graphics, Color, double) Basic version
     * @see #paintHexagon(Graphics, Color, double, double, double) Simplified version
     */
    public static void paintHexagon(Graphics g, Color color, double x, double y, double size, double fill) {
        // Create Polygon
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = (int) Math.round(size * (x * 2 + sinOf60 * 2 + Math.sin(angle) * fill));
            yPoints[i] = (int) Math.round(size * (y * 2 + 1.0 + Math.cos(angle) * fill));
        }
        // Paint
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 6);
    }
    // Resizing
    public static void calculateButtonSize(){
        if(engine == null || window == null){
            throw new NullPointerException("Critical game components cannot be null");
        }
        // Calculate minimum size
        double horizontalCount = engine().getRadius() * 4 - 2;
        double verticalCount = engine().getRadius() * 3 + 4;
        double minSize = Math.min((window().getHeight()-33) / verticalCount, (window().getWidth()-5) / horizontalCount / GameEssentials.sinOf60 / 2);
        HexButton.setSize(minSize);
    }
    // End checking
    public static boolean checkEnd(){
        for(int i = 0; i < GameEssentials.queue().length(); i ++) {
            if(GameEssentials.engine().checkPositions(GameEssentials.queue().get(i)).size() != 0){
                return false;
            }
        }
        return true;
    }

    // Setters
    public static void setEngine(HexEngine engine){
        GameEssentials.engine = engine;
    }
    public static void setQueue(Queue queue){
        GameEssentials.queue = queue;
    }
    public static void setWindow(JFrame window){
        GameEssentials.window = window;
    }
    public static void setSelectedPieceIndex(int index){
        if(index == -1 || (index >= 0 && index < queue.length())){
            GameEssentials.selectedPieceIndex = index;
            GameEssentials.selectedBlockIndex = -1;
            window.repaint();
        } else throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + queue.length());
    }
    public static void setSelectedBlockIndex(int index){
        if(selectedPieceIndex == -1){
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length 0");
        } else {
            // Try to get the piece
            int pieceLength = queue.get(selectedPieceIndex).length();
            if(index < -1 || index >= pieceLength){
                throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + pieceLength);
            } else {
                window.repaint();
                GameEssentials.selectedBlockIndex = index;
            }
        }
    }

    // Getters
    public static HexEngine engine(){return engine;}
    public static Queue queue(){return queue;}
    public static JFrame window(){return window;}
    public static int getSelectedPieceIndex(){return selectedPieceIndex;}
    public static int getSelectedBlockIndex(){return selectedBlockIndex;}
}
