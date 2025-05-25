/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package viewer.graphics.frame;

import hex.Block;
import hex.HexEngine;
import hex.Piece;
import viewer.logic.GameGUIInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 * {@code GamePanel} is a custom awt graphical component for rendering the game board for HappyHex.
 * <p>
 * This panel is responsible for:
 * <ul>
 *   <li>Displaying the current state of the game board via a {@link HexEngine} instance</li>
 *   <li>Visualizing the queue of upcoming game {@link Piece}s</li>
 *   <li>Rendering hexagonal tiles with accurate geometry based on panel size and layout</li>
 *   <li>Drawing tiles in different visual states: filled, empty, highlighted filled, and highlighted empty</li>
 * </ul>
 * <p>
 * Geometry for the hexagonal tiles is based on a reference unit hexagon, scaled dynamically to fit
 * within the panel dimensions while maintaining proper aspect ratios. The rendering uses Java 2D
 * graphics primitives via {@link GeneralPath} objects to draw each group of blocks efficiently.
 * <p>
 * Visual attributes:
 * <ul>
 *   <li>Filled blocks are rendered in a light gray tone</li>
 *   <li>Empty blocks are rendered in a dark gray tone</li>
 *   <li>Highlighted filled and empty blocks are rendered in distinct lighter and darker shades</li>
 * </ul>
 *
 * <p>
 * The class supports interaction with the core game logic via the {@link GameGUIInterface} interface:
 * <ul>
 *   <li>{@link #setEngine(HexEngine)} and {@link #getEngine()} to update or retrieve the game board</li>
 *   <li>{@link #setQueue(Piece[])} and {@link #getQueue()} to manage upcoming blocks</li>
 * </ul>
 * <p>
 * This class is designed to be immutable in terms of layout configuration and internal rendering mechanics,
 * but mutable in terms of game state references (engine and queue), which can be updated externally.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see GameGUIInterface
 * @see HexEngine
 * @see Piece
 * @see Block
 */
public final class GamePanel extends Component implements GameGUIInterface {
    /** Constant value representing sin(60°), used in hexagon geometry calculations. */
    public static final double sinOf60 = Math.sqrt(3) / 2;
    /** The hexagonal game engine containing the main board state. */
    private HexEngine engine;
    /** The queue of upcoming pieces to be displayed. */
    private Piece[] queue;
    /** Size of each hexagon based on panel dimensions and board layout. */
    private double size;
    /** Half the height of the panel, used to center drawing vertically. */
    private int halfHeight;
    /** Half the width of the panel, used to center drawing horizontally. */
    private int halfWidth;
    /** Path containing all filled (state = true) {@link Block} to be painted. */
    private final GeneralPath filledBlocks;
    /** Path containing all empty (state = false) {@link Block} to be painted. */
    private final GeneralPath emptyBlocks;
    /** Path containing all highlighted filled {@link Block} to be painted. */
    private final GeneralPath highlightedFilledBlocks;
    /** Path containing all highlighted empty {@link Block} to be painted. */
    private final GeneralPath highlightedEmptyBlocks;
    /** Reference X coordinates for the 6 vertices of a unit hexagon with upper left corner at (0,0). */
    private final double[] xReferencePoints = {sinOf60, sinOf60 * 1.9, sinOf60 * 1.9, sinOf60, sinOf60 * 0.1, sinOf60 * 0.1};
    /** Reference Y coordinates for the 6 vertices of a unit hexagon with upper left corner at (0,0). */
    private final double[] yReferencePoints = {1.9, 1.45, 0.55, 0.1, 0.55, 1.45};
    /**
     * Constructs a {@code GamePanel} with the given engine and queue.
     * @param engine the {@link HexEngine} managing the current game board state
     * @param queue the array of {@link Piece} representing the upcoming blocks
     */
    public GamePanel(HexEngine engine, Piece[] queue){
        this.engine = engine;
        this.queue = queue;
        filledBlocks = new GeneralPath();
        emptyBlocks = new GeneralPath();
        highlightedFilledBlocks = new GeneralPath();
        highlightedEmptyBlocks = new GeneralPath();
        resetSize();
    }
    /**
     * Set the engine to be displayed by this {@code GamePanel}.
     * @param engine the {@link HexEngine} managing the current game board state
     */
    public void setEngine(HexEngine engine){
        this.engine = engine.clone();
        repaint();
    }
    /**
     * Set the piece queue to be displayed by this {@code GamePanel}.
     * @param queue the {@link Piece} queue representing the upcoming blocks
     */
    public void setQueue(Piece[] queue){
        this.queue = queue.clone();
        repaint();
    }
    /**
     * Set the engine currently on display by this {@code GamePanel}.
     * @return a clone of the {@link HexEngine} managing the current game board state
     */
    public HexEngine getEngine(){
        return engine.clone();
    }
    /**
     * Returns the piece queue currently on display by this {@code GamePanel}.
     * @return a clone of the {@link Piece} queue representing the upcoming blocks
     */
    public Piece[] getQueue(){
        return queue.clone();
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
     * Returns the active size of hexagon blocks contained in this {@code GamePanel}.
     * The size will be dynamically calculated.
     *
     * @return the active radius of hexagon blocks
     */
    public double getActiveSize(){
        resetSize();
        return size;
    }
    /**
     * Paints this {@code GamePanel}, including the game board and queued pieces.
     * This method clears the screen, recalculates hexagon sizes, draws all {@link Block}s,
     * and fills them with appropriate colors based on state.
     * @param g the Graphics context used for painting
     */
    public void paint(Graphics g){
        resetSize();
        filledBlocks.reset();
        emptyBlocks.reset();
        highlightedFilledBlocks.reset();
        highlightedEmptyBlocks.reset();
        // Paint queue
        Block block;
        double move = (engine.getRadius() - 1) * 0.75;
        for (int i = 0; i < queue.length; i ++){
            double x = (i - queue.length * 0.5) * 3 * sinOf60 + sinOf60;
            for (int j = 0; j < queue[i].length(); j ++){
                block = queue[i].getBlock(j);
                if (block != null) {
                    paintHexagon( block.X() + x, block.Y() + move, block.getColor());
                }
            }
        }
        // Paint engine
        move = engine.getRadius() * sinOf60 - sinOf60 * 0.5;
        for (int i = 0; i < engine.length(); i ++){
            block = engine.getBlock(i);
            paintHexagon(block.X() - move, block.Y() - 1.75, block.getColor());
        }
        Graphics2D g3 = (Graphics2D) g.create();
        g3.setColor(new Color(170, 170, 170));
        g3.fill(filledBlocks);
        g3.dispose();
        g3 = (Graphics2D) g.create();
        g3.setColor(Color.DARK_GRAY);
        g3.fill(emptyBlocks);
        g3.dispose();
        g3 = (Graphics2D) g.create();
        g3.setColor(new Color(221, 221, 221));
        g3.fill(highlightedFilledBlocks);
        g3.dispose();
        g3 = (Graphics2D) g.create();
        g3.setColor(new Color(102, 102, 102));
        g3.fill(highlightedEmptyBlocks);
        g3.dispose();
    }
    /**
     * Adds a hexagon to the appropriate path (filled or empty) based on its state and coordinates.
     * @param x the X-coordinate in board space
     * @param y the Y-coordinate in board space
     * @param color the color index of the block, 0 represent highlighted, -1 represent empty, -2 represent filled.
     */
    public final void paintHexagon(double x, double y, int color) {
        GeneralPath path;
        if (color == -2){
            path = filledBlocks;
        } else if (color == 0){
            path = highlightedFilledBlocks;
        } else if (color == 1){
            path = highlightedEmptyBlocks;
        } else {
            path = emptyBlocks;
        }
        path.moveTo(halfWidth + size * (2 * x + xReferencePoints[0]), halfHeight + size * (2 * y + yReferencePoints[0]));
        for (int i = 1; i < 6; i++) {
            path.lineTo(halfWidth + size * (2 * x + xReferencePoints[i]), halfHeight + size * (2 * y + yReferencePoints[i]));
        }
        path.closePath();
    }
}
