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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;

public final class SpeedSlider extends JComponent implements MouseListener, MouseMotionListener {
    private static final Color borderColor = new Color(85, 85, 85);
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private static final double[] xRel = {0, sinOf60 * 0.9, sinOf60 * 0.9, 0, sinOf60 * -0.9, sinOf60 * -0.9};
    private static final double[] yRel = {0.9, 0.45, -0.45, -0.9, -0.45, 0.45};
    private double knobPosition = 0.0; // current knob position
    private boolean dragging = false;
    private int dragOffsetX = 0;
    private final Object lock = new Object();
    private Thread runnerThread;
    public SpeedSlider(){
        this.setBackground(borderColor);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setOpaque(false);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        int w = getWidth();
        int h = getHeight();
        int eh = h * 3 / 8;
        int qh = h / 4;
        int hh = h / 2;
        int knobX = (int)(knobPosition * (w - h)) + hh;

        g2.setStroke(new BasicStroke((float) hh / 8));
        g2.setColor(getBackground());
        g2.fillRoundRect(eh, eh, w - hh - qh, qh, qh, qh);
        g2.setColor(Color.WHITE);
        g2.fill(createRoundedHexagon(knobX, hh, hh));
        g2.setColor(getBackground());
        g2.draw(createRoundedHexagon(knobX, hh, hh));
        g2.dispose();
    }

    public void mousePressed(MouseEvent e) {
        int h = getHeight();
        int hh = h / 2;
        double halfH = h / 2.0;
        int knobX = hh + (int)(knobPosition * (getWidth()-h));
        Path2D.Double knobBounds = createRoundedHexagon(knobX, hh, halfH);
        if (knobBounds.contains(e.getPoint())) {
            // Start dragging
            dragging = true;
            dragOffsetX = e.getX() - knobX;
        } else {
            // Click-to-move
            double mousePosition = (e.getX() - halfH) / (getWidth()-h);
            double step = 0.001;

            runnerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (lock) {
                        if (mousePosition < knobPosition) {
                            if (knobPosition >= mousePosition + step){
                                knobPosition -= step;
                            }
                        } else if (mousePosition > knobPosition) {
                            if (knobPosition <= mousePosition - step){
                                knobPosition += step;
                            }
                        }
                        if (knobPosition > 1){
                            knobPosition = 1;
                        } else if (knobPosition < 0){
                            knobPosition = 0;
                        }
                        repaint();
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            runnerThread.setDaemon(true);
            runnerThread.start();
        }
    }
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        if (runnerThread != null) {
            runnerThread.interrupt();
            runnerThread = null;
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            double h = getHeight();
            double hh = h / 2;
            knobPosition = (e.getX() - hh - dragOffsetX) / (getWidth() - h);
            if (knobPosition > 1){
                knobPosition = 1;
            } else if (knobPosition < 0){
                knobPosition = 0;
            }
            repaint();
        }
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
    private static double toAbsolute(int center, double radius, double relative){
        return center + relative * radius;
    }

    // Prevent children
    /** Disabled: This component does not support child components. */
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    /** Disabled: This component does not support child components. */
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    /** Disabled: This component does not support container. */
    public final void addContainerListener(java.awt.event.ContainerListener l) {}

    public static void main(String[] args){
        viewer.Viewer.test(new SpeedSlider());
    }
}
