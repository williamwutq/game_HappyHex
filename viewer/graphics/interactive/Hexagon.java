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

package viewer.graphics.interactive;

import hex.Block;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;

/**
 * A lightweight graphical component used to render a single hexagonal cell on a 2D plane.
 * <p>
 * Each {@code Hexagon} instance is associated with a {@link Block} that provides its state (occupied or not)
 * and its Cartesian coordinates. This component is suitable for visualizing a large collection of such
 * blocks in a hexagonal grid-based display.
 * <p>
 * To use this class, one should subclass it and implement the {@link #fetchBlock()} method to supply
 * the correct {@code Block} for rendering. All hexagons are drawn at the same global size, set by
 * {@link #setSize(double)}, which should be synchronized across the entire display. Upon size changes,
 * it's recommended to repaint all active hexagons.
 * <p>
 * This class is not designed to be used in lightweight component hierarchies (e.g., containers). For simple,
 * fixed-use cases (e.g., rendering only a few static blocks), copy the rendering logic instead of using
 * this component directly.
 * @version 1.0 (HappyHex 1.3)
 * @author William Wu
 * @since 1.0 (HappyHex 1.3)
 */
public abstract class Hexagon extends Component{
    /** Constant value representing sin(60°), used in hexagon geometry calculations. */
    public static final double sinOf60 = Math.sqrt(3) / 2;
    /** Shared size for all hexagons. This value determines their width and height. */
    private static double size;
    /** The unique index of this hexagon, typically used as an identifier or array index. */
    private final int index;
    /**
     * Constructs a new Hexagon component with the given index.
     * @param index the unique index of this hexagon
     */
    public Hexagon(int index){
        super();
        this.index = index;
        this.setForeground(null);
        this.setBackground(null);
    }
    /**
     * Returns the index associated with this hexagon, which is used to fetch its corresponding block.
     * @return the index of this hexagon.
     * @see #fetchBlock()
     */
    protected final int getIndex(){
        return index;
    }

    /**
     * Returns the globally active size for all {@link Hexagon}.
     * @return the shared size used for rendering hexagons
     */
    public static double getActiveSize(){
        return size;
    }
    /**
     * Sets the global size used for rendering all {@link Hexagon}.
     * It is recommended to trigger a {@link #repaint} for all hexagons after this is set.
     * @param size the new size for all hexagons
     */
    public static void setSize(double size){
        Hexagon.size = size;
    }
    // Methods to fetch extension in width and height, default is 0
    /**
     * Optional width extension offset (in logical units) for position calculation.
     * Subclasses can override this to shift the hexagon layout.
     * @return the width extension, default is 0
     */
    protected int fetchWidthExtension(){return 0;}
    /**
     * Optional height extension offset (in logical units) for position calculation.
     * Subclasses can override this to shift the hexagon layout.
     * @return the height extension, default is 0
     */
    protected int fetchHeightExtension(){return 0;}
    // Method to fetch raw extension in width and height, default is 0
    /**
     * Raw pixel-based horizontal offset applied after calculating position.
     * @return the raw width extension in pixels, default is 0
     */
    protected int fetchRawWidthExtension(){return 0;}
    /**
     * Raw pixel-based vertical offset applied after calculating position.
     * @return the raw height extension in pixels, default is 0
     */
    protected int fetchRawHeightExtension(){return 0;}
    // Abstract way to fetch block
    /**
     * Retrieve the {@link Block} associated with this hexagon represented by the index.
     * @return the block representing this hexagon's data and state
     */
    protected abstract Block fetchBlock();
    // Prevent children
    /**
     * Prevents child components from being added to this hexagon.
     * @param comp ignored
     * @return the component passed in (no-op)
     */
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    /** No-op method to prevent adding container listeners. */
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    /** No-op method to prevent adding container listeners. */
    public final void addContainerListener(java.awt.event.ContainerListener l) {}
    // Paint: reset size and pain this component
    /**
     * Paints this hexagon based on its current {@code Block} state and position.
     * If the block is null, this component is set to 1x1 pixels. Otherwise, it computes
     * its position and draws a filled hexagon with a basic highlight for visual clarity.
     * @param g the Graphics context to use for painting
     */
    public final void paint(java.awt.Graphics g) {
        Block block = fetchBlock(); // Fetch block
        if(block == null) {
            Dimension minDimension = new Dimension(1,1);
            this.setSize(minDimension);
            this.setMinimumSize(minDimension);
            this.setMaximumSize(minDimension);
            this.setPreferredSize(minDimension);
            this.setBounds(new Rectangle(minDimension));
        } else {
            int width = (int) Math.round(2 * size * sinOf60);
            int height = (int) Math.round(2 * size);
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * (block.X() + fetchWidthExtension() * 0.5 * sinOf60));
            int y = (int) Math.round(size * 2 * (block.Y() + fetchHeightExtension() * 0.75));
            this.setBounds(x+ fetchRawWidthExtension(), y + fetchRawHeightExtension(), width, height);
            // Paint Basic Polygon
            if (block.getState()){
                g.setColor(Color.LIGHT_GRAY);
            } else {
                g.setColor(Color.GRAY);
            }
            int[] xPoints = new int[6];
            int[] yPoints = new int[6];
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                xPoints[i] = (int) Math.round(size * (sinOf60 + Math.sin(angle) * 0.9));
                yPoints[i] = (int) Math.round(size * (1.0 + Math.cos(angle) * 0.9));
            }
            g.fillPolygon(xPoints, yPoints, 6);
            // Paint Highlight
            xPoints = new int[4];
            yPoints = new int[4];
            xPoints[0] = (int) Math.round(size * sinOf60);
            yPoints[0] = (int) Math.round(size * 1.63);
            xPoints[1] = (int) Math.round(size * sinOf60 * 1.63);
            yPoints[1] = (int) Math.round(size * 1.315);
            xPoints[2] = (int) Math.round(size * sinOf60 * 1.72);
            yPoints[2] = (int) Math.round(size * 1.36);
            xPoints[3] = xPoints[0];
            yPoints[3] = (int) Math.round(size * 1.72);
            g.setColor(Color.BLACK);
            g.fillPolygon(xPoints, yPoints, 4);
        }
    }
    /**
     * Returns a string representation of this hexagon, including its index and associated block.
     * @return a string representing the hexagon's index and block
     */
    public String toString(){
        return "Hexagon[Index = " + index + ", Block = " + fetchBlock().toString() + ", Size = " + size + "]";
    }
    /**
     * Determines equality based on both the index and the associated {@link Block}.
     * @param other the object to compare
     * @return true if the other object is a Hexagon with the same index and block
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Hexagon hexagon) {
            return index == hexagon.index && fetchBlock().equals(hexagon.fetchBlock());
        } else return false;
    }
    /**
     * Computes a hash code based on the block's hash and the index.
     * @return the hash code for this hexagon
     */
    public int hashCode() {
        Block block = fetchBlock();
        if (block == null){
            return 0;
        } else {
            return block.hashCode() + index * 31;
        }
    }
}
