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

package GUI;

import Launcher.LaunchEssentials;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoplayInteractive {
    private final Color quitNormalColor = LaunchEssentials.launchQuitButtonBackgroundColor;
    private final Color quitHoverColor = new Color(207, 129, 11);
    private final Color borderColor = Color.BLACK;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Runnable autoplayRun;
    private final Runnable autoplayClose;
    public AutoplayInteractive(Runnable autoplayRun, Runnable autoplayClose){
        this.autoplayRun = autoplayRun;
        this.autoplayClose = autoplayClose;
    }
    private void startAuto(){
        if (isRunning.getAndSet(true)){
            if (autoplayRun != null){
                autoplayRun.run();
            }
        }
    }
    private void stopAuto(){
        if (isRunning.getAndSet(false)){
            if (autoplayClose != null){
                autoplayClose.run();
            }
        }
    }
    private void quitGame(){
        // -------------------------------------- !TO BE IMPLEMENTED! --------------------------------------
        // Some code to quit this thing
    }
    private class AutoplayControl extends JPanel{
        private CircularButton quitButton, autoButton;
        private AutoplayControl(){
            quitButton = new QuitButton() {};
        }
    }
    /**
     * A custom {@link CircularButton} that represents a quit button for the AutoplayInteractive component.
     * <p>
     * The {@code QuitButton} extends {@link CircularButton} to provide a specialized button with a custom
     * arrow-shaped path, resembling a "quit" or "exit" symbol, and animated color transitions between a
     * normal and hover state. It uses a {@link DynamicColor} to smoothly transition between two colors
     * (normal and hover) when the mouse enters or exits the button, triggering a repaint to reflect the
     * updated color. Clicking the button invokes the {@link AutoplayInteractive#quitGame()} method to
     * exit the game.
     * <p>
     * The button's shape is a circular boundary (inherited from {@link CircularButton}) with a custom
     * inner path defined as a stylized arrow, scaled to 75% of the button's radius with a fixed width
     * for the arrow lines. The color of the arrow is dynamically updated using the {@link DynamicColor}
     * instance, which animates between the normal color ({@code quitNormalColor}) and hover color
     * ({@code quitHoverColor}) using a sigmoid interpolation curve.
     * <p>
     * This class overrides key methods from {@link CircularButton} to implement:
     * <ul>
     *   <li>Custom arrow-shaped path rendering via {@link #createCustomPath(int, int, double)}.</li>
     *   <li>Dynamic color retrieval via {@link #getColor()} using the {@link DynamicColor} instance.</li>
     *   <li>Mouse hover interactions via {@link #mouseEntered(MouseEvent)} and {@link #mouseExited(MouseEvent)}
     *       to start or restart the color animation.</li>
     *   <li>Action handling via {@link #actionPerformed(ActionEvent)} to trigger game exit.</li>
     * </ul>
     * <p>
     * The button is designed to be thread-safe, as the {@link DynamicColor} handles its animations in a
     * background thread and ensures synchronized access to shared state. The {@code repaint()} method is
     * called via the {@link DynamicColor}'s GUI updater to reflect color changes in the Swing Event Dispatch
     * Thread (EDT).
     *
     * @author William Wu
     * @version 1.4
     * @since 1.4
     * @see CircularButton
     * @see DynamicColor
     * @see AutoplayInteractive#quitGame()
     */
    private class QuitButton extends CircularButton{
        private final DynamicColor internalColor;
        /**
         * Constructs a {@code QuitButton} with a {@link DynamicColor} for animating between
         * {@code quitNormalColor} and {@code quitHoverColor}.
         * <p>
         * The button is initialized with a {@link DynamicColor} that transitions between the normal
         * and hover colors, triggering a repaint on each animation frame to update the button's
         * appearance.
         */
        private QuitButton(){
            internalColor = new DynamicColor(quitNormalColor, quitHoverColor, this::repaint);
        }
        /**
         * Handles the button click by invoking {@link AutoplayInteractive#quitGame()}.
         *
         * @param e the {@link ActionEvent} triggered by clicking the button
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            quitGame();
        }
        /**
         * Starts the color animation when the mouse enters the button's area, restarting the
         * transition from the current state to the hover color.
         *
         * @param e the {@link MouseEvent} triggered by mouse entry
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            internalColor.restart();
        }
        /**
         * Starts the color animation when the mouse exits the button's area, transitioning
         * back to the normal color.
         *
         * @param e the {@link MouseEvent} triggered by mouse exit
         */
        @Override
        public void mouseExited(MouseEvent e) {
            internalColor.start();
        }
        /**
         * Creates a custom arrow-shaped path for the quit button.
         * <p>
         * The path is a stylized arrow, scaled to 75% of the button's radius, with a fixed
         * line width proportional to the radius. The arrow is drawn as a closed path with
         * six points, forming a shape that suggests exiting or closing.
         *
         * @param cx the x-coordinate of the button's center
         * @param cy the y-coordinate of the button's center
         * @param radius the radius of the button
         * @return a {@link Path2D.Double} representing the arrow-shaped path
         */
        @Override
        protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
            radius *= 0.75;
            double width = radius / 4;
            Path2D.Double path = new Path2D.Double();
            path.moveTo(cx + width, cy + radius);
            path.lineTo(cx - width * 2, cy);
            path.lineTo(cx + width, cy - radius);
            path.lineTo(cx - width, cy - radius);
            path.lineTo(cx - width * 4, cy);
            path.lineTo(cx - width, cy + radius);
            path.closePath();
            return path;
        }
        /**
         * Retrieves the current color of the button from the {@link DynamicColor}.
         * <p>
         * The color is dynamically interpolated between {@code quitNormalColor} and
         * {@code quitHoverColor} based on the current animation position.
         *
         * @return the current {@link Color} of the button
         */
        @Override
        protected Color getColor() {
            return internalColor.get();
        }
    }
    /**
     * A custom Swing {@link JButton button} rendered in the shape of a circle inspired by
     * {@code viewer.graphics.interactive.HexButton}.
     * <p>
     * The {@code HexButton} is designed with a circular shape and support a variety of interactions by overriding methods.
     * {@link #createCustomPath custom drawable path} can be filled inside the button. All paths are
     * defined by the {@link Path2D.Double} class, and quadratic {@link Path2D.Double#quadTo Bézier curves}
     * are applied as round corners for smooth graphics.
     * <p>
     * The class also provides caching mechanisms to avoid recalculating the {@link #getCachedCircle()} circle}
     * and {@link #getCachedCustomPath custom path} unless the component size changes.
     * <p>
     * Subclasses can override {@link #createCustomPath(int, int, double)} to render their own content
     * inside the button while preserving the core shape and behavior. Subclasses wanting to use the
     * function of the button must also override {@link #actionPerformed(ActionEvent)} to detect actions.
     * Subclasses wanting to implement hovering detection must also override {@link #mouseEntered(MouseEvent)}
     * and {@link #mouseReleased(MouseEvent)} to detect actions, and may optionally override {@link #getColor()}
     * to implement animated or specialized colors.
     * <p>
     * The button automatically handles {@link #repaint() painting} and resizing, and disables
     * adding any child components or container listeners. It is not opaque and uses anti-aliased
     * graphics for smooth rendering.
     *
     * @author William Wu
     * @version 1.4
     * @since 1.4
     * @see JButton
     * @see Path2D.Double
     */
    private abstract class CircularButton extends JButton implements ActionListener, MouseListener {
        private Path2D.Double cachedCircle, cachedCustomPath;
        private int cachedCircleWidth, cachedCircleHeight, cachedCustomPathWidth, cachedCustomPathHeight;
        /**
         * Constructs a new {@code CircularButton} with default settings.
         * <p>
         * The button is initialized as non-opaque, with no layout manager, a zero-pixel empty border,
         * and aligned to the center. Effect handling are not set. The constructor also sets up
         * internal caches for the circle and any custom path rendering, and registers itself as
         * its own action and mouse event listener.
         */
        public CircularButton(){
            this.cachedCircle = null;
            this.cachedCustomPath = null;
            this.cachedCircleWidth = -1;
            this.cachedCircleHeight = -1;
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

        /**
         * Retrieves the cached circle shape corresponding to the current dimensions of the button.
         * <p>
         * If the cached circle is null or the dimensions have changed since the last computation,
         * a new circle is generated using the current width and height of the button.
         * The circle is centered within the component and its size is determined by the minimum
         * radius that fits within the width and height constraints.
         *
         * @return a {@link Path2D.Double} representing the rounded circle for the current size
         */
        private Path2D.Double getCachedCircle() {
            int w = getWidth();
            int h = getHeight();
            if (cachedCircle == null || w != cachedCircleWidth || h != cachedCircleHeight) {
                double diameter = Math.min(w, h);
                double topLeftX = (w - diameter) / 2.0;
                double topLeftY = (h - diameter) / 2.0;
                cachedCircle = new Path2D.Double(new Ellipse2D.Double(topLeftX, topLeftY, diameter, diameter));
                cachedCircleWidth = w;
                cachedCircleHeight = h;
            }
            return cachedCircle;
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
                double minRadius = Math.min(w, h) / 2.0;
                cachedCustomPath = createCustomPath(w / 2, h / 2, minRadius);
                cachedCustomPathWidth = w;
                cachedCustomPathHeight = h;
            }
            return cachedCustomPath;
        }
        /**
         * Update the cached custom path shape corresponding to the current dimensions of the button.
         * <p>
         * No matter the size and state of the current custom path, a new custom path will be generated
         * using the current width and height of the button. This method allows subclasses to change
         * custom shapes inside the hexagon button and update the shape used for drawing.
         *
         * @see #createCustomPath(int, int, double)
         */
        protected final void updateCachedCustomPath() {
            int w = getWidth();
            int h = getHeight();
            double minRadius = Math.min(w, h) / 2.0;
            cachedCustomPath = createCustomPath(w / 2, h / 2, minRadius);
            cachedCustomPathWidth = w;
            cachedCustomPathHeight = h;
        }
        /**
         * Paints the visual appearance of the circular button.
         * <p>
         * This method draws a circular outline with the border color and fills it with the transitioning color.
         * The custom path is filled with the border color.
         * <p>
         * This component should have no children, so none will be painted.
         *
         * @param g the {@link Graphics} context to use for painting
         */
        public final void paint(java.awt.Graphics g) {
            double minRadius = Math.min(getWidth(), getHeight()) / 2.0;
            Path2D.Double circle = getCachedCircle();
            Path2D.Double custom = getCachedCustomPath();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int stroke = (int) (minRadius / 12.0);
            if (stroke < 1) stroke = 1;
            g2.setStroke(new BasicStroke(stroke));
            g2.setColor(borderColor);
            g2.draw(circle);
            g2.setColor(getColor());
            g2.fill(circle);
            if (custom != null){
                g2.setColor(borderColor);
                g2.fill(custom);
            }
            g2.dispose();
        }
        /**
         * Determines whether a given point is within the circular boundary of the button.
         * <p>
         * This is used to provide accurate hit detection that matches the visual circular shape,
         * rather than the default rectangular bounds.
         *
         * @param x the x-coordinate of the point to test
         * @param y the y-coordinate of the point to test
         * @return {@code true} if the point lies within the cached circle, {@code false} otherwise
         * @see JButton#contains
         */
        public final boolean contains(int x, int y) {
            return getCachedCircle().contains(x, y);
        }
        /**
         * Sets the bounds of the component and invalidates any cached shapes if dimensions have changed.
         * <p>
         * If the new width or height differs from the current dimensions, both the cached circle
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
                cachedCircle = null;
                cachedCustomPath = null;
            }
            super.setBounds(x, y, width, height);
        }
        /**
         * Generate a customized path inside the circular button.
         *
         * @param cx     the x-coordinate of the center of the circular button
         * @param cy     the y-coordinate of the center of the circular button
         * @param radius the overall size (radius) of the circular button
         * @return a customized {@link Path2D.Double} path for the button inside the circle.
         */
        protected Path2D.Double createCustomPath(int cx, int cy, double radius){
            return null;
        }
        /**
         * Return the color currently in use by the button as background.
         *
         * @return the background {@link Color} that should be used by the button for printing.
         */
        protected Color getColor(){
            return borderColor;
        }
    }
    public static void main(String[] args){
        AutoplayInteractive interactive = new AutoplayInteractive(null, null);
        JFrame mainFrame = new JFrame("HappyHex AutoplayInteractive Test");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(Color.WHITE);
        mainFrame.setSize(new Dimension(400, 400));
        mainFrame.setMinimumSize(new Dimension(400, 400));
        mainFrame.add(interactive.new AutoplayControl().quitButton);
        mainFrame.setVisible(true);
    }
}
