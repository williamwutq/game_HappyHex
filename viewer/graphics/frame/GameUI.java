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

import hex.HexEngine;
import hex.Piece;
import viewer.graphics.interactive.HexButton;
import viewer.graphics.interactive.SpeedSlider;
import viewer.logic.ActionGUIInterface;
import viewer.logic.Controller;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;

/**
 * The {@code GameUI} component is a graphical user interface component for the HappyHex game.
 * It arranges control buttons in the four corners of the game area and places a speed slider
 * at the bottom. This class is responsible for rendering the game interface and providing
 * user interaction to control the simulation via a {@link Controller}.
 * <p>
 * It uses the following components:
 * <ul>
 *   <li>{@link GamePanel} - the main display area for the HappyHex game grid.</li>
 *   <li>{@link HexButton} - custom, round-cornered, hexagonal buttons for controlling the game flow.</li>
 *   <li>{@link SpeedSlider} - a slider to dynamically adjust the game's speed in real-time.</li>
 *   <li>{@link InfoPanel} - two seven segment displays for game score and turn information.</li>
 *   <li>{@link Controller} - a thread-safe backend controller used to start, stop, advance, or reverse game states.</li>
 * </ul>
 * <p>
 * Layout behavior:
 * <ul>
 *   <li>The {@link InfoPanel} is horizontally aligned above of the {@code GamePanel} and other components.</li>
 *   <li>The {@link GamePanel} is centered and scaled based on component dimensions.</li>
 *   <li>The {@code forwardButton} and {@code backwardButton} are placed in the bottom-left and bottom-right corners, respectively.</li>
 *   <li>The {@code advanceButton} and {@code retreatButton} are placed in the top-right and top-left corners, respectively.</li>
 *   <li>The {@link SpeedSlider} is horizontally aligned at the bottom of the {@code GamePanel}.</li>
 * </ul>
 *
 * @author William Wu
 * @version 1.1 (HappyHex 1.4)
 * @since 1.0 (HappyHex 1.3)
 * @see #GameUI
 * @see Controller
 * @see InfoPanel
 * @see GamePanel
 * @see HexButton
 * @see SpeedSlider
 * @see JComponent
 */
public final class GameUI extends JComponent implements ActionGUIInterface {
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private static final double root2 = Math.sqrt(2);
    private final HexButton forwardButton, backwardButton, advanceButton, retreatButton;
    private final GamePanel gamePanel;
    private final Controller controller;
    private final SpeedSlider slider;
    private final InfoPanel infoPanel;
    private boolean forward, backward;

    /**
     * Constructs a {@code GameUI} instance linked to the specified {@link Controller}.
     * Initializes buttons and binds the controller to the game panel.
     * <p>
     * The {@code forwardButton} toggles the game’s forward simulation mode.
     * If the simulation is not running, clicking this button starts it via {@link Controller#run()}.
     * Otherwise, it stops the simulation via {@link Controller#stop()}.
     * <p>
     * The {@code backwardButton} toggles the game’s reverse simulation mode.
     * If the simulation is not running in reverse, clicking this button starts reverse simulation
     * via {@link Controller#back()}. Otherwise, it stops the simulation via {@link Controller#stop()}.
     * <p>
     * The {@code advanceButton} advances the simulation by one step forward.
     * This invokes {@link Controller#advance()} directly.
     * <p>
     * The {@code retreatButton} rewinds the simulation by one step.
     * This invokes {@link Controller#retreat()} directly.
     * <p>
     * The {@link SpeedSlider} component allows dynamic adjustment of the game's execution speed.
     * When the slider changes, the interval is passed to {@link Controller#setSpeed(int)},
     * by implementing the {@link SpeedSlider.SpeedChangeListener}, and is used for execution
     * in a thread-safe manner in {@code Controller}.
     * <p>
     * The slider value is inversely mapped to an interval using the formula:
     * <code>interval = (int)Math.pow(1024, 1 - sliderValue)</code>
     * This means the minimal interval is 1 millisecond per frame, maximum 1024 milliseconds per frame.
     * <p>
     * The {@code Controller} is a thread-safe backend controller used to start, stop, advance, or
     * reverse game states, is can be used to run simulations at various speeds. It must be passed in
     * for file support, because this {@code GameUI} is only meant for displaying game information, not
     * helping users with finding targeted game file.
     *
     * @param controller the backend controller responsible for game logic execution and state control.
     */
    public GameUI(Controller controller){
        this.forward = true;
        this.backward = true;
        this.controller = controller;
        this.slider = new SpeedSlider();
        this.gamePanel = new GamePanel(new HexEngine(5), new Piece[]{});
        this.infoPanel = new InfoPanel();
        controller.bindGameGUI(gamePanel);
        controller.bindInfoGUI(infoPanel);
        controller.bindActionGUI(this);

        forwardButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                if (forward){
                    backward = true;
                    backwardButton.updateCachedCustomPath();
                    backwardButton.repaint();
                    controller.run();
                } else controller.stop();
                forward = !forward;
                updateCachedCustomPath();
                repaint();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2;
                Path2D.Double path = new Path2D.Double();
                if (forward) {
                    path.moveTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy + radius);
                    path.lineTo(cx - radius * (sinOf60 * 2 - 1 / sinOf60), cy - radius);
                    path.lineTo(cx + radius / sinOf60, cy);
                } else {
                    radius /= root2;
                    path.moveTo(cx + radius, cy + radius);
                    path.lineTo(cx - radius, cy + radius);
                    path.lineTo(cx - radius, cy - radius);
                    path.lineTo(cx + radius, cy - radius);
                }
                path.closePath();
                return path;
            }
        };
        backwardButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                if (backward){
                    forward = true;
                    forwardButton.updateCachedCustomPath();
                    forwardButton.repaint();
                    controller.back();
                } else controller.stop();
                backward = !backward;
                updateCachedCustomPath();
                repaint();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2;
                Path2D.Double path = new Path2D.Double();
                if (backward) {
                    path.moveTo(cx + radius * (sinOf60 * 2 - 1 / sinOf60), cy + radius);
                    path.lineTo(cx + radius * (sinOf60 * 2 - 1 / sinOf60), cy - radius);
                    path.lineTo(cx - radius / sinOf60, cy);
                } else {
                    radius /= root2;
                    path.moveTo(cx + radius, cy + radius);
                    path.lineTo(cx - radius, cy + radius);
                    path.lineTo(cx - radius, cy - radius);
                    path.lineTo(cx + radius, cy - radius);
                }
                path.closePath();
                return path;
            }
        };
        advanceButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                controller.advance();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2 * sinOf60;
                double width = radius / 4;
                Path2D.Double path = new Path2D.Double();
                path.moveTo(cx - width, cy + radius);
                path.lineTo(cx + width * 2, cy);
                path.lineTo(cx - width, cy - radius);
                path.lineTo(cx + width, cy - radius);
                path.lineTo(cx + width * 4, cy);
                path.lineTo(cx + width, cy + radius);
                path.closePath();
                return path;
            }
        };
        retreatButton = new HexButton(){
            public void actionPerformed(ActionEvent e) {
                controller.retreat();
            }
            protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
                radius /= 2 * sinOf60;
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
        };
        slider.setSpeedChangeListener(new SpeedSlider.SpeedChangeListener() {
            public void onSpeedChanged(double newSpeed) {
                newSpeed = 1-newSpeed;
                int base = 1024;
                int interval = (int)Math.pow(base, newSpeed);
                controller.setSpeed(interval);
            }
        });
        slider.setKnobPosition(0.5);
        controller.setSpeed(32);

        this.setBackground(Color.WHITE);
        this.setOpaque(false);
        this.add(gamePanel);
        this.add(infoPanel);
        this.add(forwardButton);
        this.add(backwardButton);
        this.add(advanceButton);
        this.add(retreatButton);
        this.add(slider);
    }
    /**
     * {@inheritDoc}
     * The method lays out the subcomponents of this {@code GameUI} according to the current size of the component.
     *
     * <p>It calculates scaling based on the engine radius and centers the game panel. The hexagonal buttons
     * are positioned in corners relative to this panel, and the slider is positioned horizontally at the bottom.
     *
     * @see GamePanel#resetSize()
     * @see InfoPanel#doLayout()
     * @see HexButton#setBounds(int, int, int, int)
     */
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        int er = gamePanel.getEngine().getRadius();

        double halfHeight = h/2.0-3;
        double halfWidth = w/2.0-3;
        int length = er * 2 - 1;
        double vertical = er * 1.5 + 2;
        double s = (Math.min(halfHeight / vertical, halfWidth / sinOf60 / length));
        gamePanel.setBounds(3, (int) (3 + s * 2), w, h - (int)(3 * s));
        s = gamePanel.getActiveSize(); // update size

        double vs = (er * 1.5 - 0.25) * s; // vertical shift
        int bb = h/2 + (int)((er * 1.5 - 3) * s) + 3; // button bound
        int tb = h/2 - (int)((er * 1.5 + 1.5) * s) + 3; // top bound
        int sb = h/2 + (int)((er * 1.5 + 2.5) * s) + 3; // slider bound
        int ib = h/2 - (int)((er * 1.5 + 3.5) * s) + 3; // infoPanel bound
        double hs = (er * 2 * sinOf60 - sinOf60) * s; // horizontal shift
        int lb = (int)(w*0.5 - hs); // left bound
        int rb = (int)(w*0.5 + hs); // right bound
        int r = (int)Math.min(vs * 3 / 8, hs * 3 / 8); // button radius
        infoPanel.setBounds(lb, ib, (int)(hs * 2), (int)(s * 2));
        slider.setBounds(lb, sb, (int)(hs * 2), (int)s);
        backwardButton.setBounds(lb, bb - r, r, r);
        forwardButton.setBounds(rb - r, bb - r, r, r);
        retreatButton.setBounds(lb, tb, r, r);
        advanceButton.setBounds(rb - r, tb, r, r);
    }
    /**
     * Paints the entire component using {@link Graphics} by drawing the background, the game and information panels, and all controls.
     * This overrides the default paint behavior of {@link JComponent}. Extra children of this component, if included,
     * will not be painted.
     *
     * @see GamePanel#paint
     * @see InfoPanel#paint
     * @see HexButton#paint
     * @see SpeedSlider#paint
     * @param g the graphics context in which to paint.
     */
    public void paint(Graphics g){
        gamePanel.paint(g.create(gamePanel.getX(), gamePanel.getY(), gamePanel.getWidth(), gamePanel.getHeight()));
        infoPanel.paint(g.create(infoPanel.getX(), infoPanel.getY(), infoPanel.getWidth(), infoPanel.getHeight()));
        forwardButton.paint(g.create(forwardButton.getX(), forwardButton.getY(), forwardButton.getWidth(), forwardButton.getHeight()));
        backwardButton.paint(g.create(backwardButton.getX(), backwardButton.getY(), backwardButton.getWidth(), backwardButton.getHeight()));
        advanceButton.paint(g.create(advanceButton.getX(), advanceButton.getY(), advanceButton.getWidth(), advanceButton.getHeight()));
        retreatButton.paint(g.create(retreatButton.getX(), retreatButton.getY(), retreatButton.getWidth(), retreatButton.getHeight()));
        slider.paint(g.create(slider.getX(), slider.getY(), slider.getWidth(), slider.getHeight()));
    }

    /**
     * {@inheritDoc}
     * This implementation stops the forward and backward buttons from being active.
     * @since 1.1 (HappyHex 1.4)
     */
    @Override
    public void onIncrement() {
        stopButtons();
    }
    /**
     * {@inheritDoc}
     * This implementation stops the forward and backward buttons from being active.
     * @since 1.1 (HappyHex 1.4)
     */
    @Override
    public void onDecrement() {
        stopButtons();
    }
    /**
     * {@inheritDoc}
     * This implementation does nothing because slider already knows speed changes.
     * @since 1.1 (HappyHex 1.4)
     */
    @Override
    public void onSpeedChanged(int delay) {}
    /**
     * {@inheritDoc}
     * This implementation does nothing because buttons already know run start.
     * @since 1.1 (HappyHex 1.4)
     */
    @Override
    public void onRunStart() {}
    /**
     * {@inheritDoc}
     * This implementation stops the forward and backward buttons from being active.
     * @since 1.1 (HappyHex 1.4)
     */
    @Override
    public void onRunStop() {
        stopButtons();
    }
    /**
     * Stops the forward and backward buttons from being active and updates their appearance.
     * @since 1.1 (HappyHex 1.4)
     */
    private void stopButtons() {
        if (!forward) {
            forward = true;
            forwardButton.updateCachedCustomPath();
            forwardButton.repaint();
        }
        if (!backward) {
            backward = true;
            backwardButton.updateCachedCustomPath();
            backwardButton.repaint();
        }
    }
}
