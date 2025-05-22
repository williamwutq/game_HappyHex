package viewer.graphics.interactive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

abstract class HexButton extends JButton implements ActionListener, MouseListener {
    private static final Color borderColor = new Color(85, 85, 85);
    /** Constant value representing sin(60°), used in hexagon geometry calculations. */
    private static final double sinOf60 = Math.sqrt(3) / 2;
    /** Reference X coordinates for the 6 vertices of a unit hexagon with center at (0,0). */
    private static final double[] xRel = {0, sinOf60 * 0.9, sinOf60 * 0.9, 0, sinOf60 * -0.9, sinOf60 * -0.9};
    /** Reference Y coordinates for the 6 vertices of a unit hexagon with center at (0,0). */
    private static final double[] yRel = {0.9, 0.45, -0.45, -0.9, -0.45, 0.45};
    private Path2D.Double cachedHexagon = null;
    private int cachedWidth = -1;
    private int cachedHeight = -1;
    private boolean hover;
    public HexButton (){
        this.hover = false;
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
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}

    public final void mouseClicked(MouseEvent e) {}
    public final void mousePressed(MouseEvent e) {}
    public final void mouseReleased(MouseEvent e) {}
    public final void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();
    }
    public final void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
    }

    private Path2D.Double getCachedHexagon() {
        int w = getWidth();
        int h = getHeight();
        if (cachedHexagon == null || w != cachedWidth || h != cachedHeight) {
            double minRadius = Math.min(w / sinOf60 / 2.0, h / 2.0);
            cachedHexagon = createRoundedHexagon(w / 2, h / 2, minRadius);
            cachedWidth = w;
            cachedHeight = h;
        }
        return cachedHexagon;
    }

    public final void paint(java.awt.Graphics g) {
        double minRadius = Math.min(getWidth() / sinOf60 / 2.0, getHeight() / 2.0);
        Path2D.Double hexagon = getCachedHexagon();
        Path2D.Double custom = createPath(getWidth() / 2, getHeight() / 2, minRadius);
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
    public boolean contains(int x, int y) {
        return getCachedHexagon().contains(x, y);
    }
    public void setBounds(int x, int y, int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            cachedHexagon = null; // Invalidate cache
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
    protected Path2D.Double createPath(int cx, int cy, double radius){
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
            public void actionPerformed(ActionEvent e) {}
            protected Path2D.Double createPath(int cx, int cy, double radius) {
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
