package viewer.graphics.interactive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * A custom Swing {@link JButton button} rendered in the shape of a rounded hexagon.
 * <p>
 * The {@code HexButton} is designed with a unique {@link #createRoundedHexagon hexagonal} shape
 * and supports {@link #isHovering hover} interaction, during which a
 * {@link #createCustomPath custom drawable path} can be filled inside the button. All paths are
 * defined by the {@link Path2D.Double} class, and quadratic {@link Path2D.Double#quadTo Bézier curves}
 * are applied as round corners for smooth graphics.
 * <p>
 * The class also provides caching mechanisms to avoid recalculating the {@link #getCachedHexagon hexagon}
 * and {@link #getCachedCustomPath custom path} unless the component size changes.
 * <p>
 * Subclasses can override {@link #createCustomPath(int, int, double)} to render their own content
 * inside the hexagon while preserving the core shape and behavior. Subclasses wanting to use the
 * function of the button must also override {@link #actionPerformed(ActionEvent)} to detect actions.
 * <p>
 * The button automatically handles {@link #repaint() painting}, mouse hover feedback, and disables
 * adding any child components or container listeners. It is not opaque and uses anti-aliased
 * graphics for smooth rendering.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see JButton
 * @see Path2D.Double
 */
abstract class HexButton extends JButton implements ActionListener, MouseListener {
    /** Border color of the HexButton, which is the button color normally and the button border color when hovered. */
    private static final Color borderColor = new Color(85, 85, 85);
    /** Constant value representing sin(60°), used in hexagon geometry calculations. */
    private static final double sinOf60 = Math.sqrt(3) / 2;
    /** Reference X coordinates for the 6 vertices of a unit hexagon with center at (0,0). */
    private static final double[] xRel = {0, sinOf60 * 0.9, sinOf60 * 0.9, 0, sinOf60 * -0.9, sinOf60 * -0.9};
    /** Reference Y coordinates for the 6 vertices of a unit hexagon with center at (0,0). */
    private static final double[] yRel = {0.9, 0.45, -0.45, -0.9, -0.45, 0.45};
    private Path2D.Double cachedHexagon, cachedCustomPath;
    private int cachedHexagonWidth, cachedHexagonHeight, cachedCustomPathWidth, cachedCustomPathHeight;
    private boolean hover;
    /**
     * Constructs a new {@code HexButton} with default settings.
     * <p>
     * The button is initialized as non-opaque, with no layout manager, a zero-pixel empty border,
     * and aligned to the center. Hover effects and painting are handled internally. The constructor
     * also sets up internal caches for the hexagon and any custom path rendering, as well as
     * registers itself as its own action and mouse event listener.
     */
    public HexButton (){
        this.hover = false;
        this.cachedHexagon = null;
        this.cachedCustomPath = null;
        this.cachedHexagonWidth = -1;
        this.cachedHexagonHeight = -1;
        this.cachedCustomPathWidth = -1;
        this.cachedCustomPathHeight = -1;
        this.setOpaque(false);
        this.setBackground(borderColor);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setLayout(null);
        this.addActionListener(this);
        this.addMouseListener(this);
        this.setBorder(new EmptyBorder(0,0,0,0));
    }
    // Prevent children
    /** Disabled: This component does not support child components. */
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    /** Disabled: This component does not support child components. */
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    /** Disabled: This component does not support container. */
    public final void addContainerListener(java.awt.event.ContainerListener l) {}

    /** {@inheritDoc} It will do nothing. */
    public final void mouseClicked(MouseEvent e) {}
    /** {@inheritDoc} It will do nothing. */
    public final void mousePressed(MouseEvent e) {}
    /** {@inheritDoc} It will do nothing. */
    public final void mouseReleased(MouseEvent e) {}
    /** {@inheritDoc} It will {@link #repaint} the component and set the hover statues to true. */
    public final void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();
    }
    /** {@inheritDoc} It will {@link #repaint} the component and set the hover statues to false. */
    public final void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
    }

    /**
     * Determine whether mouse is currently hovering over this component, return true if hovering.
     * @return true if mouse is hovering over this HexButton
     */
    protected boolean isHovering(){
        return hover;
    }

    /**
     * Retrieves the cached rounded hexagon shape corresponding to the current dimensions of the button.
     * <p>
     * If the cached hexagon is null or the dimensions have changed since the last computation,
     * a new rounded hexagon is generated using the current width and height of the button.
     * The hexagon is centered within the component and its size is determined by the minimum
     * radius that fits within the width and height constraints.
     *
     * @return a {@link Path2D.Double} representing the rounded hexagon for the current size
     * @see #createRoundedHexagon(int, int, double)
     */
    private Path2D.Double getCachedHexagon() {
        int w = getWidth();
        int h = getHeight();
        if (cachedHexagon == null || w != cachedHexagonWidth || h != cachedHexagonHeight) {
            double minRadius = Math.min(w / sinOf60 / 2.0, h / 2.0);
            cachedHexagon = createRoundedHexagon(w / 2, h / 2, minRadius);
            cachedHexagonWidth = w;
            cachedHexagonHeight = h;
        }
        return cachedHexagon;
    }
    /**
     * Retrieves the cached custom path shape corresponding to the current dimensions of the button.
     * <p>
     * If the cached custom path is null or the dimensions have changed since the last computation,
     * a new custom path is generated using the current width and height of the button.
     * This method allows subclasses to define custom shapes inside the hexagon button
     * by overriding the {@link #createCustomPath(int, int, double)} method.
     *
     * @return a {@link Path2D.Double} representing the custom path for the current size
     * @see #createCustomPath(int, int, double)
     */
    private Path2D.Double getCachedCustomPath() {
        int w = getWidth();
        int h = getHeight();
        if (cachedCustomPath == null || w != cachedCustomPathWidth || h != cachedCustomPathHeight) {
            double minRadius = Math.min(w / sinOf60 / 2.0, h / 2.0);
            cachedCustomPath = createCustomPath(w / 2, h / 2, minRadius);
            cachedCustomPathWidth = w;
            cachedCustomPathHeight = h;
        }
        return cachedCustomPath;
    }

    /**
     * Paints the visual appearance of the hexagon button.
     * <p>
     * This method draws a hexagonal outline and fills it depending on the hover state.
     * If the button is hovered, the custom path is filled with the border color.
     * Otherwise, the hexagon is filled with the border color and the custom path is filled in white.
     * Calculates the stroke width based on the component size.
     * <p>
     * This component should have no children, so none will be painted.
     *
     * @param g the {@link Graphics} context to use for painting
     */
    public final void paint(java.awt.Graphics g) {
        double minRadius = Math.min(getWidth() / sinOf60 / 2.0, getHeight() / 2.0);
        Path2D.Double hexagon = getCachedHexagon();
        Path2D.Double custom = getCachedCustomPath();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int stroke = (int) (minRadius / 12.0);
        if (stroke < 1) stroke = 1;
        g2.setStroke(new BasicStroke(stroke));
        g2.setColor(borderColor);
        g2.draw(hexagon);
        if (hover){
            if (custom != null){
                g2.fill(custom);
            }
        } else {
            g2.fill(hexagon);
            if (custom != null){
                g2.setColor(Color.WHITE);
                g2.fill(custom);
            }
        }
        g2.dispose();
    }
    /**
     * Determines whether a given point is within the hexagonal boundary of the button.
     * <p>
     * This is used to provide accurate hit detection that matches the visual hexagonal shape,
     * rather than the default rectangular bounds.
     *
     * @param x the x-coordinate of the point to test
     * @param y the y-coordinate of the point to test
     * @return {@code true} if the point lies within the hexagon, {@code false} otherwise
     * @see JButton#contains
     */
    public boolean contains(int x, int y) {
        return getCachedHexagon().contains(x, y);
    }
    /**
     * Sets the bounds of the component and invalidates any cached shapes if dimensions have changed.
     * <p>
     * If the new width or height differs from the current dimensions, both the cached hexagon
     * and the custom path are cleared so they can be regenerated with the new size.
     *
     * @param x      the new x-coordinate of the component
     * @param y      the new y-coordinate of the component
     * @param width  the new width of the component
     * @param height the new height of the component
     * @see JComponent#setBounds
     */
    public void setBounds(int x, int y, int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            // Invalidate cache
            cachedHexagon = null;
            cachedCustomPath = null;
        }
        super.setBounds(x, y, width, height);
    }

    /**
     * Generate a customized path inside the hexagon button.
     *
     * @param cx     the x-coordinate of the center of the hexagon button
     * @param cy     the y-coordinate of the center of the hexagon button
     * @param radius the overall size (radius) of the hexagon button
     * @return a customized {@link Path2D.Double} path for the button inside the hexagon
     */
    protected Path2D.Double createCustomPath(int cx, int cy, double radius){
        return null;
    }
    /**
     * Generate a rounded hexagon centered at the given coordinates.
     * <p>
     * The hexagon is created using a predefined set of relative coordinates that approximate
     * a regular hexagon with rounded corners. Corner radius is assumed to be 1/3 of the total
     * radius, and each corner is rounded using quadratic {@link Path2D.Double#quadTo Bézier curves}.
     *
     * @param cx     the x-coordinate of the center of the hexagon button
     * @param cy     the y-coordinate of the center of the hexagon button
     * @param radius the overall size (radius) of the hexagon button
     * @return a {@link Path2D.Double} path created to represent a rounded hexagon
     */
    private static Path2D.Double createRoundedHexagon(int cx, int cy, double radius) {
        Path2D.Double hexagon = new Path2D.Double();

        for (int i = 0; i < 6; i++) {
            // Points before and after the corner
            double fromX = xRel[i] + xRel[(i + 4) % 6] / 2.7;
            double fromY = yRel[i] + yRel[(i + 4) % 6] / 2.7;
            double toX = xRel[i] + xRel[(i + 2) % 6] / 2.7;
            double toY = yRel[i] + yRel[(i + 2) % 6] / 2.7;

            if (i == 0) {
                hexagon.moveTo(toAbsolute(cx, radius, fromX), toAbsolute(cy, radius, fromY));
            } else {
                hexagon.lineTo(toAbsolute(cx, radius, fromX), toAbsolute(cy, radius, fromY));
            }
            hexagon.quadTo(toAbsolute(cx, radius, xRel[i]), toAbsolute(cy, radius, yRel[i]), toAbsolute(cx, radius, toX), toAbsolute(cy, radius, toY));
        }
        hexagon.closePath();
        return hexagon;
    }
    /**
     * Converts a relative coordinate (range [-1, 1]) into an absolute pixel coordinate.
     *
     * @param center   the center coordinate (x or y) in pixels
     * @param radius   the radius of the shape
     * @param relative the relative coordinate to be scaled and offset
     * @return the absolute coordinate in pixels
     */
    protected static double toAbsolute(int center, double radius, double relative){
        return center + relative * radius;
    }

    public static void main(String[] args){
        viewer.Viewer.test(new HexButton(){
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
        });
    }
}
