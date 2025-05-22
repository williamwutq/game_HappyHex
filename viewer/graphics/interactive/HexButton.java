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
    /** Reference X coordinates for the 6 vertices of a unit hexagon with upper left corner at (0,0). */
    private final double[] xReferencePoints = {0, sinOf60 * 0.9, sinOf60 * 0.9, 0, sinOf60 * -0.9, sinOf60 * -0.9};
    /** Reference Y coordinates for the 6 vertices of a unit hexagon with upper left corner at (0,0). */
    private final double[] yReferencePoints = {0.9, 0.45, -0.45, -0.9, -0.45, 0.45};
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

    public final void paint(java.awt.Graphics g) {
        double minRadius = Math.min(getWidth() / sinOf60 / 2.0, getHeight() / 2.0);
        paintRoundedHexagon((Graphics2D) g, getWidth() / 2, getHeight() / 2, minRadius);
    }

    protected Path2D.Double obtainPath(int cx, int cy, double radius){
        return null;
    }

    private void paintRoundedHexagon(Graphics2D g2, int cx, int cy, double radius) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int stroke = (int) (radius / 12.0);
        if (stroke < 1) stroke = 1;
        Path2D.Double hexagon = new Path2D.Double();
        Path2D.Double custom = this.obtainPath(cx, cy, radius);

        for (int i = 0; i < 6; i++) {
            // Vectors to prev and next
            double dx1 = xReferencePoints[(i + 4) % 6];
            double dy1 = yReferencePoints[(i + 4) % 6];
            double dx2 = xReferencePoints[(i + 2) % 6];
            double dy2 = yReferencePoints[(i + 2) % 6];

            // Normalize
            double len1 = Math.hypot(dx1, dy1);
            double len2 = Math.hypot(dx2, dy2);
            dx1 /= len1;
            dy1 /= len1;
            dx2 /= len2;
            dy2 /= len2;

            // Points before and after the corner
            double fromX = xReferencePoints[i] + dx1 / 3;
            double fromY = yReferencePoints[i] + dy1 / 3;
            double toX = xReferencePoints[i] + dx2 / 3;
            double toY = yReferencePoints[i] + dy2 / 3;

            if (i == 0) {
                hexagon.moveTo(cx + radius * fromX, cy+ radius * fromY);
            } else {
                hexagon.lineTo(cx + radius * fromX, cy+ radius * fromY);
            }
            hexagon.quadTo(cx + radius * xReferencePoints[i], cy+ radius * yReferencePoints[i], cx + radius * toX, cy+ radius * toY);
        }

        hexagon.closePath();
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
    }

    public static void main(String[] args){
        viewer.Viewer.test(new HexButton(){
            public void actionPerformed(ActionEvent e) {}
            protected Path2D.Double obtainPath(int cx, int cy, double radius) {
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
