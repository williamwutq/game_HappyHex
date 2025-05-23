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

public final class SpeedSlider extends JComponent implements MouseListener, MouseMotionListener {
    private static final Color borderColor = new Color(85, 85, 85);
    private int knobX = 0; // current knob position
    private final int knobWidth = 40;
    private final int knobHeight = 20;
    private final int trackHeight = 10;
    private final int arc = 10;
    private boolean dragging = false;
    private int dragOffsetX = 0;
    private final Object lock = new Object();
    private Thread runnerThread;
    public SpeedSlider(){
        this.setBackground(borderColor);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHints(hints);

        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;
        int trackX = 20;
        int trackWidth = width - 40;
        int trackY = centerY - trackHeight / 2;

        knobX = Math.max(trackX, Math.min(knobX, trackX + trackWidth - knobWidth));

        int knobY = centerY - knobHeight / 2;

        g2.setColor(getBackground());
        g2.fillRoundRect(trackX, trackY, trackWidth, trackHeight, arc, arc);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(knobX, knobY, knobWidth, knobHeight, arc, arc);
        g2.setColor(getBackground());
        g2.drawRoundRect(knobX, knobY, knobWidth, knobHeight, arc, arc);

        g2.dispose();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int knobY = getHeight() / 2 - knobHeight / 2;
        Rectangle knobBounds = new Rectangle(knobX, knobY, knobWidth, knobHeight);

        if (knobBounds.contains(e.getPoint())) {
            // Start dragging
            dragging = true;
            dragOffsetX = e.getX() - knobX;
        } else {
            // Click-to-jump
            int mouseX = e.getX();
            int trackX = 20;
            int trackWidth = getWidth() - 40;
            int minX = trackX;
            int maxX = trackX + trackWidth - knobWidth;

            runnerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (lock) {
                        if (mouseX < knobX) {
                            knobX = Math.max(knobX - 1, minX);
                        } else if (mouseX > knobX + knobWidth) {
                            knobX = Math.min(knobX + 1, maxX);
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
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            int trackX = 20;
            int trackWidth = getWidth() - 40;
            int minX = trackX;
            int maxX = trackX + trackWidth - knobWidth;

            knobX = e.getX() - dragOffsetX;
            knobX = Math.max(minX, Math.min(knobX, maxX));
            repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args){
        viewer.Viewer.test(new SpeedSlider());
    }
}
