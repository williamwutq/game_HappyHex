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
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Swing-based interactive component for controlling autoplay functionality in a game or animation.
 * <p>
 * The {@code AutoplayInteractive} class provides a user interface for starting, stopping, and adjusting
 * the speed of an autoplay feature, as well as quitting the associated game or animation. It encapsulates
 * a state machine managed by the inner class {@link AutoplayInteractive.AutoplayControl}, which is a custom
 * {@link javax.swing.JButton} that renders a rounded rectangular button with dynamic color animations and
 * child components (buttons and labels) to reflect different states.
 * <p>
 * <b>Implementation Details:</b>
 * <ul>
 *   <li>The interactive component is implemented via the {@link AutoplayInteractive.AutoplayControl} inner
 *       class, which manages a state machine with five states: two-button mode (quit and start autoplay),
 *       autoplay-on transition, autoplay-off transition, slow autoplay, and fast autoplay.</li>
 *   <li>Child buttons ({@link AutoplayInteractive.QuitButton}, {@link AutoplayInteractive.AutoOnButton},
 *       {@link AutoplayInteractive.AutoOffButton}) extend the abstract {@link AutoplayInteractive.CircularButton}
 *       class, providing circular buttons with custom shapes (e.g., arrow for quit, triangle for start) and
 *       animated color transitions using {@link DynamicColor}.</li>
 *   <li>The class uses three {@link java.lang.Runnable} callbacks provided at construction:
 *       {@code autoplayRun} to start the autoplay process, {@code autoplayClose} to stop it, and
 *       {@code quitGame} to exit the game. These are invoked by internal methods
 *       ({@link #startAuto()}, {@link #stopAuto()}, {@link #quitGame()}) triggered by user interactions.</li>
 *   <li>Thread safety is ensured through an {@link java.util.concurrent.atomic.AtomicBoolean} ({@code isRunning})
 *       for tracking autoplay state and synchronization within {@link AutoplayInteractive.AutoplayControl} for
 *       managing its state and animations. All Swing operations occur on the Event Dispatch Thread (EDT).</li>
 *   <li>Resources (e.g., {@link DynamicColor} animations) are cleaned up via {@code removeNotify()} in
 *       {@link AutoplayInteractive.AutoplayControl} and its child buttons, with an additional
 *       {@link AutoplayInteractive.AutoplayControl#quitAutoplayImmediately()} method for immediate cleanup.</li>
 * </ul>
 * <p>
 * <b>Retrieving the Interactive Component:</b>
 * The interactive component is retrieved via the {@link #fetchControl()} method, which returns the
 * {@link AutoplayInteractive.AutoplayControl} instance as a {@link javax.swing.JComponent}. This component
 * can be added to a Swing container (e.g., a {@link javax.swing.JPanel}) to integrate the autoplay controls
 * into a larger UI. The component dynamically updates its appearance based on user interactions (mouse hovers,
 * clicks) and state transitions, providing visual feedback through animations and label/button changes.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * AutoplayInteractive autoplay = new AutoplayInteractive(
 *     () -> System.out.println("Autoplay started"),
 *     () -> System.out.println("Autoplay stopped"),
 *     () -> System.out.println("Game quit")
 * );
 * JPanel panel = new JPanel();
 * panel.add(autoplay.fetchControl());
 * }</pre>
 * <p>
 * This class is designed to be flexible, allowing external systems to define the behavior of autoplay and
 * game quitting through callbacks, while providing a visually engaging and thread-safe UI component.
 *
 * @author William Wu
 * @version 1.4
 * @since 1.4
 * @see AutoplayInteractive.AutoplayControl
 * @see AutoplayInteractive.CircularButton
 * @see DynamicColor
 * @see javax.swing.JComponent
 */
public class AutoplayInteractive {
    private static final double sinOf60 = Math.sqrt(3) / 2;
    private static final double radiusMultiplier = 0.94;
    private final Color quitNormalColor = LaunchEssentials.launchQuitButtonBackgroundColor;
    private final Color quitHoverColor = new Color(207, 129, 11);
    private final Color autoOnNormalColor = new Color(21, 102, 207);
    private final Color autoOnHoverColor = new Color(21, 207, 164);
    private final Color autoOffNormalColor = new Color(62, 152, 2);
    private final Color autoOffHoverColor = new Color(34, 232, 143);
    private final Color autoSlowNormalColor = new Color(81, 24, 179);
    private final Color autoSlowHoverColor = new Color(105, 47, 205);
    private final Color autoFastNormalColor = new Color(225, 23, 62);
    private final Color autoFastHoverColor = new Color(236, 80, 109);
    private final Color autoAnimationStartColor = new Color(200, 49, 214);
    private final Color autoAnimationEndColor = new Color(56, 216, 64);
    private final Color backgroundNormalColor = new Color(69, 172, 145);
    private final Color backgroundHoverColor = new Color(4, 62, 47);
    private final Color borderColor = Color.BLACK;
    private final String autoFont = LaunchEssentials.launchSettingsSlidingButtonFont;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Runnable autoplayRun, autoplayClose, quitGame;
    private final AutoplayControl control;

    /**
     * Constructs an {@code AutoplayInteractive} instance with callbacks for controlling autoplay and game termination.
     * <p>
     * This constructor initializes the {@code AutoplayInteractive} with three {@link java.lang.Runnable} callbacks:
     * {@code autoplayRun} to start the autoplay process, {@code autoplayClose} to stop it, and {@code quitGame}
     * to exit the associated game or animation. It creates an instance of {@link AutoplayInteractive.AutoplayControl}
     * to serve as the interactive UI component, which manages the state machine and user interactions. The
     * {@code isRunning} flag is initialized to {@code false}, indicating that autoplay is not active at construction.
     * The provided callbacks are stored for use in {@link #startAuto()}, {@link #stopAuto()}, and {@link #quitGame()}.
     * <p>
     * The constructed instance is ready to be integrated into a Swing UI via {@link #fetchControl()}, which returns
     * the {@link AutoplayInteractive.AutoplayControl} component.
     *
     * @param autoplayRun   the {@link java.lang.Runnable} to execute when starting autoplay, may be null
     * @param autoplayClose the {@link java.lang.Runnable} to execute when stopping autoplay, may be null
     * @param quitGame      the {@link java.lang.Runnable} to execute when quitting the game, may be null
     * @see AutoplayInteractive.AutoplayControl
     * @see #fetchControl()
     * @see #startAuto()
     * @see #stopAuto()
     * @see #quitGame()
     */
    public AutoplayInteractive(Runnable autoplayRun, Runnable autoplayClose, Runnable quitGame){
        this.autoplayRun = autoplayRun;
        this.autoplayClose = autoplayClose;
        this.quitGame = quitGame;
        this.control = new AutoplayControl();
    }
    /**
     * Starts the autoplay process and advances the control's state.
     * <p>
     * This method sets the {@code isRunning} flag to {@code true} using an atomic operation to prevent
     * concurrent starts. If the autoplay is not already running, it invokes the {@code autoplayRun}
     * callback (if provided) to initiate the external autoplay process. It then triggers a state transition
     * in the {@link AutoplayInteractive.AutoplayControl} via {@link AutoplayInteractive.AutoplayControl#nextState()}.
     * This method is called internally by {@link AutoplayInteractive.AutoOnButton} when clicked.
     *
     * @see AutoplayInteractive.AutoOnButton
     * @see AutoplayInteractive.AutoplayControl#nextState()
     */
    private void startAuto(){
        if (isRunning.getAndSet(true)){
            if (autoplayRun != null){
                autoplayRun.run();
            }
        }
        control.nextState();
    }
    /**
     * Stops the autoplay process and advances the control's state.
     * <p>
     * This method sets the {@code isRunning} flag to {@code false} using an atomic operation to prevent
     * concurrent stops. If the autoplay is currently running, it invokes the {@code autoplayClose}
     * callback (if provided) to terminate the external autoplay process. It then triggers a state transition
     * in the {@link AutoplayInteractive.AutoplayControl} via {@link AutoplayInteractive.AutoplayControl#nextState()}.
     * This method is called internally by {@link AutoplayInteractive.AutoOffButton} when clicked.
     *
     * @see AutoplayInteractive.AutoOffButton
     * @see AutoplayInteractive.AutoplayControl#nextState()
     */
    private void stopAuto(){
        if (isRunning.getAndSet(false)){
            if (autoplayClose != null){
                autoplayClose.run();
            }
        }
        control.nextState();
    }
    /**
     * Quits the game and immediately resets the autoplay control.
     * <p>
     * This method invokes {@link AutoplayInteractive.AutoplayControl#quitAutoplayImmediately()} to reset
     * the control's state and stop its animations, then runs the {@code quitGame} callback to perform
     * the external game termination logic. It is called internally by {@link AutoplayInteractive.QuitButton}
     * when clicked, ensuring the autoplay control is cleaned up before the game exits.
     *
     * @see AutoplayInteractive.QuitButton
     * @see AutoplayInteractive.AutoplayControl#quitAutoplayImmediately()
     */
    private void quitGame(){
        control.quitAutoplayImmediately();
        quitGame.run();
    }
    /**
     * Retrieves the interactive autoplay control component.
     * <p>
     * This method returns the {@link AutoplayInteractive.AutoplayControl} instance as a
     * {@link javax.swing.JComponent}, which can be added to a Swing container to integrate the autoplay
     * controls into a user interface. The returned component manages its own state, animations, and
     * user interactions (e.g., mouse hovers, clicks) to control the autoplay feature.
     *
     * @return the {@link AutoplayInteractive.AutoplayControl} instance as a {@link javax.swing.JComponent}
     * @see AutoplayInteractive.AutoplayControl
     */
    public JComponent fetchControl(){
        return control;
    }
    /**
     * A custom {@link javax.swing.JButton} that provides interactive controls for managing autoplay functionality.
     * <p>
     * The {@code AutoplayControl} class extends {@link javax.swing.JButton} and implements {@link java.awt.event.MouseListener}
     * and {@link java.awt.event.ActionListener} to handle user interactions for controlling an autoplay feature.
     * It features a state machine to manage different visual and functional states, including buttons and labels for
     * starting, stopping, and adjusting the speed of autoplay, as well as quitting the associated game or animation.
     * The button is rendered as a rounded rectangle with dynamic color animations provided by {@link DynamicColor}.
     * <p>
     * The state machine supports the following states:
     * <ul>
     *   <li><b>State 1</b>: Displays two buttons ({@code quitButton} and {@code autoOnButton}) for quitting the game
     *       or starting autoplay.</li>
     *   <li><b>State 2</b>: Transitional state showing an "AUTO" label during the animation to autoplay on.</li>
     *   <li><b>State 3</b>: Transitional state showing an "AUTO" label during the animation to autoplay off.</li>
     *   <li><b>State 4</b>: Displays one button ({@code autoOffButton}) and a "slow" label, indicating slow autoplay speed.</li>
     *   <li><b>State 5</b>: Displays one button ({@code autoOffButton}) and a "fast" label, indicating fast autoplay speed.</li>
     * </ul>
     * <p>
     * The class manages child components ({@code quitButton}, {@code autoOnButton}, {@code autoOffButton}, {@code autoLabel},
     * {@code slowLabel}, and {@code fastLabel}) and dynamically adds or removes them based on the current state.
     * Color animations are handled by a {@link DynamicColor} instance ({@code dynamicBackground}), which transitions
     * between normal and hover colors or animation-specific colors, triggering repaints via {@link #checkAndPaint()} or
     * {@link #repaint()}. The layout is customized in {@link #doLayout()} to position children based on cached dimensions.
     * <p>
     * Thread safety is ensured through a {@code stateLock} object, synchronizing access to the {@code state} and
     * {@code dynamicBackground} fields. All Swing-related operations (e.g., painting, layout, component addition/removal)
     * occur on the Event Dispatch Thread (EDT), while {@link DynamicColor} animations run in a background thread, with
     * updates safely scheduled on the EDT.
     * <p>
     * Resource management is handled via {@link #removeNotify()}, which stops the {@code dynamicBackground} animation,
     * and through the child buttons' own {@code removeNotify()} methods, which stop their respective {@link DynamicColor}
     * animations. The {@link #quitAutoplayImmediately()} method provides an explicit way to reset the state and stop
     * animations before dereferencing, complementing automatic cleanup when the component is removed from its container.
     * <p>
     * External control is supported via {@link #quitAutoplay()} to transition to state 1 (autoplay off) and
     * {@link #quitAutoplayImmediately()} to immediately reset the state and clean up resources.
     * Mouse interactions trigger hover animations in states 1, 4, and 5, and action events toggle between slow and fast
     * autoplay speeds in states 4 and 5.
     * <p>
     * The button's appearance is optimized with cached shapes ({@link #getCachedCircle()}) to improve rendering performance,
     * and hit detection is customized via {@link #contains(int, int)} to match the rounded rectangular shape.
     *
     * @author William Wu
     * @version 1.4
     * @since 1.4
     * @see DynamicColor
     * @see CircularButton
     * @see AutoplayInteractive
     */
    private class AutoplayControl extends JButton implements MouseListener, ActionListener{
        // States: 1 - two buttons include quit
        //         2 - animation transition to auto on
        //         3 - animation transition to auto off
        //         4 - one button with speed adjust to SLOW
        //         5 - one button with speed adjust to FAST
        private RoundRectangle2D.Double cachedOutline;
        private int cachedOutlineWidth, cachedOutlineHeight, state;
        private double cachedDiameter, cachedX, cachedY;
        private DynamicColor dynamicBackground;
        private final JLabel autoLabel, slowLabel, fastLabel;
        private final CircularButton quitButton, autoOnButton, autoOffButton;
        private final Object stateLock;
        /**
         * Constructs a new {@code AutoplayControl} instance.
         * <p>
         * Initializes the autoplay control as a centered {@link javax.swing.JButton} with mouse and action
         * listeners. Sets up child components ({@code quitButton}, {@code autoOnButton}, {@code autoOffButton},
         * and labels) and initializes the state machine by transitioning to the initial state. All cached
         * fields are set to default values, and a synchronization lock is created for thread-safe state
         * management.
         *
         * @see #nextState()
         * @see javax.swing.JButton
         */
        private AutoplayControl(){
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);
            this.addMouseListener(this);
            this.addActionListener(this);
            this.quitButton = new QuitButton();
            this.autoOnButton = new AutoOnButton();
            this.autoOffButton = new AutoOffButton();
            this.autoLabel = new JLabel("AUTO");
            this.autoLabel.setVerticalAlignment(SwingConstants.CENTER);
            this.autoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.autoLabel.setFont(new Font(autoFont, Font.BOLD, 1));
            this.slowLabel = new JLabel("slow");
            this.slowLabel.setVerticalAlignment(SwingConstants.CENTER);
            this.slowLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.slowLabel.setFont(new Font(autoFont, Font.PLAIN, 1));
            this.fastLabel = new JLabel("fast");
            this.fastLabel.setVerticalAlignment(SwingConstants.CENTER);
            this.fastLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.fastLabel.setFont(new Font(autoFont, Font.PLAIN, 1));
            this.stateLock = new Object();
            this.cachedOutline = null;
            this.cachedOutlineWidth = -1;
            this.cachedOutlineHeight = -1;
            this.cachedDiameter = -1;
            this.dynamicBackground = null;
            this.state = 0; nextState();
        }
        /**
         * Quit autoplay by an external source. This will transition the state machine to state 1 eventually.
         * <p>
         * This method updates the {@code state} field and configures the component's appearance
         * (e.g., adding/removing child components, updating {@code dynamicBackground}) based on the
         * new state. If the current state is with autoplay on, the transitional state is used; if the current state
         * is already in autoplay off or is transitioning to autoplay off, nothing will be executed; if the current
         * state is transitioning to autoplay on, transition will be reversed.
         *
         * @see #getState()
         * @see DynamicColor#start()
         * @see javax.swing.JComponent#revalidate()
         */
        public void quitAutoplay(){
            synchronized (stateLock) {
                if (state == 2) {
                    state = 4;
                    double position = 0.0;
                    if (this.dynamicBackground != null){
                        dynamicBackground.stop();
                        position = dynamicBackground.position();
                    }
                    this.dynamicBackground = new DynamicColor(autoAnimationEndColor, autoAnimationStartColor, this::checkAndPaint);
                    this.dynamicBackground.setDuration(500);
                    this.dynamicBackground.applyPosition(1 - position);
                    this.dynamicBackground.start();
                    this.removeAll();
                    this.add(autoLabel);
                    this.revalidate();
                } else if (state == 4 || state == 5) {
                    state = 3;
                    if (this.dynamicBackground != null) dynamicBackground.stop();
                    this.dynamicBackground = new DynamicColor(autoAnimationEndColor, autoAnimationStartColor, this::checkAndPaint);
                    this.dynamicBackground.setDuration(500);
                    this.dynamicBackground.start();
                    this.removeAll();
                    this.add(autoLabel);
                    this.revalidate();
                }
            }
        }
        /**
         * Quit autoplay immediately by an external source and dispose of all resources.
         * <p>
         * This method will set the state back to 0 and dispose of all buttons. Usually, this is called before
         * the {@link AutoplayControl} is dereferenced to ensure proper closing of resources. However, if this
         * component is removed from container, resources should be automatically closed.
         *
         * @see #removeNotify()
         * @see DynamicColor#start()
         */
        public void quitAutoplayImmediately(){
            synchronized (stateLock) {
                state = 0;
                if (this.dynamicBackground != null) dynamicBackground.stop();
                this.removeAll();
            }
        }
        /**
         * Transitions the autoplay control to the next state in its state machine.
         * <p>
         * This method updates the {@code state} field and configures the component's appearance
         * (e.g., adding/removing child components, updating {@code dynamicBackground}) based on the
         * new state. It stops any existing animation before starting a new one and ensures thread-safe
         * access to shared state using synchronization. The component is revalidated after changes.
         *
         * @see #getState()
         * @see DynamicColor#start()
         * @see javax.swing.JComponent#revalidate()
         */
        private void nextState(){
            synchronized (stateLock) {
                if (this.dynamicBackground != null) dynamicBackground.stop();
                if (state == 1) {
                    state = 2;
                    this.dynamicBackground = new DynamicColor(autoAnimationStartColor, autoAnimationEndColor, this::checkAndPaint);
                    this.dynamicBackground.setDuration(500);
                    this.dynamicBackground.start();
                    this.removeAll();
                    this.add(autoLabel);
                    this.revalidate();
                } else if (state == 2) {
                    state = 4;
                    this.dynamicBackground = new DynamicColor(autoSlowNormalColor, autoSlowHoverColor, this::repaint);
                    this.dynamicBackground.setDuration(1200);
                    this.removeAll();
                    this.add(slowLabel);
                    this.add(autoOffButton);
                    this.revalidate();
                } else if (state == 3 || state == 0) {
                    state = 1;
                    this.dynamicBackground = new DynamicColor(backgroundNormalColor, backgroundHoverColor, this::repaint);
                    this.dynamicBackground.setDuration(1200);
                    this.removeAll();
                    this.add(quitButton);
                    this.add(autoOnButton);
                    this.revalidate();
                } else if (state == 4 || state == 5) {
                    state = 3;
                    this.dynamicBackground = new DynamicColor(autoAnimationEndColor, autoAnimationStartColor, this::checkAndPaint);
                    this.dynamicBackground.setDuration(500);
                    this.dynamicBackground.start();
                    this.removeAll();
                    this.add(autoLabel);
                    this.revalidate();
                }
            }
        }
        /**
         * Retrieves the current state of the autoplay control.
         * <p>
         * This method provides thread-safe access to the {@code state} field, which represents the current
         * state in the autoplay control's state machine (e.g., 1 for two-button state, 2 for auto-on
         * animation, etc.). The state is accessed within a synchronized block to ensure consistency.
         *
         * @return the current state as an integer
         * @see #nextState()
         */
        private int getState(){
            synchronized (stateLock) {
                return state;
            }
        }
        /**
         * Retrieves the current dynamic background color animation.
         * <p>
         * This method provides thread-safe access to the {@code dynamicBackground} field, which manages
         * the color animation for the button's background. It returns the current {@code DynamicColor}
         * instance or null if no animation is active, synchronized to prevent concurrent modification.
         *
         * @return the current {@link DynamicColor} instance, or null if none is set
         * @see DynamicColor
         */
        private DynamicColor getDynamicBackground(){
            synchronized (stateLock) {
                return this.dynamicBackground;
            }
        }
        /**
         * Retrieves the current background color from the dynamic animation.
         * <p>
         * This method returns the interpolated color from {@code dynamicBackground} if it exists, or falls
         * back to the default {@code borderColor} if no animation is active. The color is used for filling
         * the button's background during painting.
         *
         * @return the current {@link java.awt.Color} for the button's background
         * @see DynamicColor#get()
         */
        private Color getColor(){
            DynamicColor dc = getDynamicBackground();
            if (dc != null) {
                return dc.get();
            } else return borderColor;
        }
        /**
         * Checks if the background color animation is complete and triggers the next state if so, then repaints.
         * <p>
         * This method is invoked by the {@code dynamicBackground} animation to check if it has reached completion
         * (position 1.0). If complete, it calls {@link #nextState()} to transition to the next state in the
         * autoplay control's state machine. Regardless of completion, it triggers a repaint to update the
         * component's appearance with the current animation color. This is used in transitional states.
         *
         * @see DynamicColor#position()
         * @see #nextState()
         * @see #repaint()
         */
        private void checkAndPaint() {
            DynamicColor dc = getDynamicBackground();
            if (dc != null && dc.position() == 1.0) {
                nextState();
            }
            repaint();
        }
        /**
         * Renders the visual appearance of the autoplay control button.
         * <p>
         * This method draws a rounded rectangular outline with a specified stroke width and fills it with
         * the current color obtained from {@link #getColor()}. Antialiasing is enabled for smooth rendering.
         * Child components are painted after the background to ensure proper layering.
         *
         * @param g the {@link java.awt.Graphics} context used for painting
         * @see #getCachedCircle()
         * @see #getColor()
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        public final void paint(java.awt.Graphics g) {
            int stroke = Math.min(getWidth() / 72, getHeight() / 24);
            if (stroke < 1) stroke = 1;
            RoundRectangle2D.Double circle = getCachedCircle();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(stroke));
            g2.setColor(borderColor);
            g2.draw(circle);
            g2.setColor(getColor());
            g2.fill(circle);
            g2.dispose();
            paintChildren(g);
        }

        /** {@inheritDoc} It will do nothing. */
        public final void mouseClicked(MouseEvent e) {}
        /** {@inheritDoc} It will do nothing. */
        public final void mousePressed(MouseEvent e) {}
        /** {@inheritDoc} It will do nothing. */
        public final void mouseReleased(MouseEvent e) {}
        /**
         * Starts the color animation when the mouse enters the panel's area, restarting the
         * transition from the current state to the hover color. This is only active when other animations are not playing.
         *
         * @param e the {@link MouseEvent} triggered by mouse entry
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            int state = getState();
            if (state == 1 || state == 4 || state == 5) {
                getDynamicBackground().restart();
            }
        }
        /**
         * Starts the color animation when the mouse exits the panel's area, transitioning
         * back to the normal color. This is only active when other animations are not playing.
         *
         * @param e the {@link MouseEvent} triggered by mouse exit
         */
        @Override
        public void mouseExited(MouseEvent e) {
            int state = getState();
            if (state == 1 || state == 4 || state == 5) {
                getDynamicBackground().start();
            }
        }
        /**
         * Handles the button click by invoking toggling speed of autoplay.
         *
         * @param e the {@link ActionEvent} triggered by clicking the button
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (stateLock) {
                if (this.dynamicBackground != null) dynamicBackground.stop();
                if (state == 4) {
                    this.state = 5;
                    this.dynamicBackground = new DynamicColor(autoFastNormalColor, autoFastHoverColor, this::repaint);
                    this.dynamicBackground.setDuration(1200);
                    this.removeAll();
                    this.add(fastLabel);
                    this.add(autoOffButton);
                    this.revalidate();
                } else if (state == 5) {
                    this.state = 4;
                    this.dynamicBackground = new DynamicColor(autoSlowNormalColor, autoSlowHoverColor, this::repaint);
                    this.dynamicBackground.setDuration(1200);
                    this.removeAll();
                    this.add(slowLabel);
                    this.add(autoOffButton);
                    this.revalidate();
                }
            }
        }
        /**
         * Retrieves the cached outline corresponding to the current dimensions of the button.
         * <p>
         * If the cached circle is null or the dimensions have changed since the last computation,
         * a new shape is generated using the current width and height of the button.
         * The shape is centered within the component and its size is determined by the minimum
         * radius that fits within the width and height constraints.
         *
         * @return a {@link Path2D.Double} representing the outline for the current size
         */
        private RoundRectangle2D.Double getCachedCircle() {
            int w = getWidth();
            int h = getHeight();
            if (cachedOutline == null || w != cachedOutlineWidth || h != cachedOutlineHeight) {
                cachedDiameter = Math.min(w / 3, h);
                cachedX = (w - cachedDiameter * 3) * 0.5;
                cachedY = (h - cachedDiameter) * 0.5;
                double diameter = cachedDiameter * radiusMultiplier;
                double iw = diameter + cachedDiameter * 2;
                double x = (w - iw) * 0.5;
                double y = (h - diameter) * 0.5;
                cachedOutline = new RoundRectangle2D.Double(x, y, iw, diameter, diameter, diameter);
                cachedOutlineWidth = w;
                cachedOutlineHeight = h;
            }
            return cachedOutline;
        }
        /**
         * Determines whether a given point is within the circular boundary of the button.
         * <p>
         * This is used to provide accurate hit detection that matches the visual circular shape,
         * rather than the default rectangular bounds.
         *
         * @param x the x-coordinate of the point to test
         * @param y the y-coordinate of the point to test
         * @return {@code true} if the point lies within the cached shape, {@code false} otherwise
         * @see JButton#contains
         */
        public final boolean contains(int x, int y) {
            return getCachedCircle().contains(x, y);
        }
        /**
         * Sets the bounds of the component and invalidates the cached shape if size has changed.
         * <p>
         * If the new width or height differs from the current dimensions, the cached path is cleared
         * so it can be regenerated with the new size.
         *
         * @param x      the new x-coordinate of the component
         * @param y      the new y-coordinate of the component
         * @param width  the new width of the component
         * @param height the new height of the component
         * @see JComponent#setBounds
         */
        public void setBounds(int x, int y, int width, int height) {
            if (width != getWidth() || height != getHeight()) {
                cachedOutline = null;
            }
            super.setBounds(x, y, width, height);
        }
        /**
         * Lays out the child components based on the current state and cached dimensions.
         * <p>
         * This method positions the child components (e.g., buttons and labels) according to the current
         * state of the autoplay control. It uses cached dimensions from {@link #getCachedCircle()} to
         * calculate appropriate bounds for each child, ensuring proper alignment and sizing. Fonts for
         * labels are also updated dynamically based on the cached diameter.
         *
         * @see #getCachedCircle()
         * @see javax.swing.JComponent#doLayout()
         */
        public void doLayout(){
            getCachedCircle();
            int s = getState();
            int d = (int) cachedDiameter;
            int x = (int) cachedX;
            int y = (int) cachedY;
            if (s == 1){
                quitButton.setBounds(x, y, d, d);
                autoOnButton.setBounds(x + d * 2, y, d, d);
            } else if (s == 2 || s == 3){
                autoLabel.setBounds(x, y, d * 3, d);
                autoLabel.setFont(new Font(autoFont, Font.BOLD, d / 2));
            } else if (s == 4){
                autoOffButton.setBounds(x, y, d, d);
                slowLabel.setBounds(x + d, y, d * 2, d);
                slowLabel.setFont(new Font(autoFont, Font.BOLD, d / 2));
            } else if (s == 5){
                autoOffButton.setBounds(x + d * 2, y, d, d);
                fastLabel.setBounds(x, y, d * 2, d);
                fastLabel.setFont(new Font(autoFont, Font.BOLD, d / 2));
            }
        }
        /**
         * Terminates the background color animation when the component is removed from its container.
         * This method stops the {@code dynamicBackground} animation by calling its {@code stop()} method,
         * ensuring that the associated animation thread is safely terminated. This prevents resource leaks
         * when the component is no longer part of the UI hierarchy. The operation is synchronized to ensure
         * thread-safe access to the {@code dynamicBackground} field.
         *
         * @see DynamicColor#stop()
         * @see javax.swing.JComponent#removeNotify()
         */
        @Override
        public void removeNotify() {
            super.removeNotify();
            synchronized (stateLock) {
                if (dynamicBackground != null) {
                    dynamicBackground.stop();
                }
            }
        }
    }
    /**
     * A custom {@link CircularButton} that represents a stop button for the autoplay.
     * <p>
     * The {@code AutoOffButton} extends {@link CircularButton} to provide a specialized button with a custom
     * rectangular path, resembling a "pause" symbol, and animated color transitions between a
     * normal and hover state. It uses a {@link DynamicColor} to smoothly transition between two colors
     * (normal and hover) when the mouse enters or exits the button, triggering a repaint to reflect the
     * updated color. Clicking the button invokes the {@link AutoplayInteractive#stopAuto()} method to
     * stop the autoplay.
     * <p>
     * The button's shape is a circular boundary (inherited from {@link CircularButton}) with a custom inner
     * path defined as a square, scaled to half of the button's radius with a fixed width for the side
     * length of the square. The color of the rectangle is dynamically updated using the {@link DynamicColor}
     * instance, which animates between the normal color ({@code autoOffNormalColor}) and hover color
     * ({@code autoOffHoverColor}) using a sigmoid interpolation curve.
     * <p>
     * This class overrides key methods from {@link CircularButton} to implement:
     * <ul>
     *   <li>Custom rectangular path rendering via {@link #createCustomPath(int, int, double)}.</li>
     *   <li>Dynamic color retrieval via {@link #getColor()} using the {@link DynamicColor} instance.</li>
     *   <li>Mouse hover interactions via {@link #mouseEntered(MouseEvent)} and {@link #mouseExited(MouseEvent)}
     *       to start or restart the color animation.</li>
     *   <li>Action handling via {@link #actionPerformed(ActionEvent)} to stop autoplay.</li>
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
     * @see AutoplayInteractive#stopAuto()
     */
    private class AutoOffButton extends CircularButton{
        private final DynamicColor internalColor;
        /**
         * Constructs a {@code AutoOffButton} with a {@link DynamicColor} for animating between
         * {@code autoOnNormalColor} and {@code autoOnHoverColor}.
         * <p>
         * The button is initialized with a {@link DynamicColor} that transitions between the normal
         * and hover colors, triggering a repaint on each animation frame to update the button's
         * appearance.
         */
        private AutoOffButton(){
            internalColor = new DynamicColor(autoOffNormalColor, autoOffHoverColor, this::repaint);
            internalColor.setDuration(400);
        }
        /**
         * Handles the button click by invoking {@link AutoplayInteractive#stopAuto()}.
         *
         * @param e the {@link ActionEvent} triggered by clicking the button
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            stopAuto();
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
         * Creates a custom rectangular path for the autoplay on button.
         * <p>
         * The path consists of two identical rectangles forming a square of radius that is
         * half of the radius of the circle. These rectangles are drawn as a closed path with
         * six points, forming a shape that suggests pausing or stopping.
         *
         * @param cx the x-coordinate of the button's center
         * @param cy the y-coordinate of the button's center
         * @param radius the radius of the button
         * @return a {@link Path2D.Double} representing the special path
         */
        @Override
        protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
            radius *= 0.5;
            Path2D.Double path = new Path2D.Double();
            final double y1 = cy + radius;
            final double y2 = cy - radius;
            final double x1 = cx + radius;
            final double x2 = cx + radius * 0.2;
            final double x3 = cx - radius * 0.2;
            final double x4 = cx - radius;
            path.moveTo(x1, y1);
            path.lineTo(x2, y1);
            path.lineTo(x2, y2);
            path.lineTo(x1, y2);
            path.closePath();
            path.moveTo(x3, y1);
            path.lineTo(x4, y1);
            path.lineTo(x4, y2);
            path.lineTo(x3, y2);
            return path;
        }
        /**
         * Retrieves the current color of the button from the {@link DynamicColor}.
         * <p>
         * The color is dynamically interpolated between {@code autoOffNormalColor} and
         * {@code autoOffHoverColor} based on the current animation position.
         *
         * @return the current {@link Color} of the button
         */
        @Override
        protected Color getColor() {
            return internalColor.get();
        }
        /**
         * Terminates the button's color animation when the button is removed from its container.
         * This method stops the {@code internalColor} animation by calling its {@code stop()} method,
         * ensuring that the associated animation thread is safely terminated. This prevents resource leaks
         * when the button is no longer part of the UI hierarchy.
         *
         * @see DynamicColor#stop()
         * @see javax.swing.JComponent#removeNotify()
         */
        @Override
        public void removeNotify() {
            super.removeNotify();
            internalColor.stop();
        }
    }
    /**
     * A custom {@link CircularButton} that represents a start button for the autoplay.
     * <p>
     * The {@code AutoOnButton} extends {@link CircularButton} to provide a specialized button with a custom
     * triangular path, resembling a "start" symbol, and animated color transitions between a
     * normal and hover state. It uses a {@link DynamicColor} to smoothly transition between two colors
     * (normal and hover) when the mouse enters or exits the button, triggering a repaint to reflect the
     * updated color. Clicking the button invokes the {@link AutoplayInteractive#startAuto()} method to
     * start the autoplay.
     * <p>
     * The button's shape is a circular boundary (inherited from {@link CircularButton}) with a custom inner
     * path defined as an equilateral triangle, scaled to 68% of the button's radius with a fixed width
     * for the arrow lines. The color of the triangle is dynamically updated using the {@link DynamicColor}
     * instance, which animates between the normal color ({@code autoOnNormalColor}) and hover color
     * ({@code autoOnHoverColor}) using a sigmoid interpolation curve.
     * <p>
     * This class overrides key methods from {@link CircularButton} to implement:
     * <ul>
     *   <li>Custom triangular path rendering via {@link #createCustomPath(int, int, double)}.</li>
     *   <li>Dynamic color retrieval via {@link #getColor()} using the {@link DynamicColor} instance.</li>
     *   <li>Mouse hover interactions via {@link #mouseEntered(MouseEvent)} and {@link #mouseExited(MouseEvent)}
     *       to start or restart the color animation.</li>
     *   <li>Action handling via {@link #actionPerformed(ActionEvent)} to trigger autoplay.</li>
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
     * @see AutoplayInteractive#startAuto()
     */
    private class AutoOnButton extends CircularButton{
        private final DynamicColor internalColor;
        /**
         * Constructs a {@code AutoOnButton} with a {@link DynamicColor} for animating between
         * {@code autoOnNormalColor} and {@code autoOnHoverColor}.
         * <p>
         * The button is initialized with a {@link DynamicColor} that transitions between the normal
         * and hover colors, triggering a repaint on each animation frame to update the button's
         * appearance.
         */
        private AutoOnButton(){
            internalColor = new DynamicColor(autoOnNormalColor, autoOnHoverColor, this::repaint);
            internalColor.setDuration(400);
        }
        /**
         * Handles the button click by invoking {@link AutoplayInteractive#startAuto()}.
         *
         * @param e the {@link ActionEvent} triggered by clicking the button
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            startAuto();
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
         * Creates a custom triangle-shaped path for the autoplay on button.
         * <p>
         * The path is a stylized triangle, scaled to 75% of the button's radius, with a fixed
         * line width proportional to the radius. The triangle is drawn as a closed path with
         * six points, forming a shape that suggests starting.
         *
         * @param cx the x-coordinate of the button's center
         * @param cy the y-coordinate of the button's center
         * @param radius the radius of the button
         * @return a {@link Path2D.Double} representing the triangular path
         */
        @Override
        protected Path2D.Double createCustomPath(int cx, int cy, double radius) {
            radius *= 0.68;
            Path2D.Double path = new Path2D.Double();
            final double x = cx - radius * (sinOf60 * 2 - 1 / sinOf60);
            path.moveTo(x, cy + radius);
            path.lineTo(x, cy - radius);
            path.lineTo(cx + radius / sinOf60, cy);
            path.closePath();
            return path;
        }
        /**
         * Retrieves the current color of the button from the {@link DynamicColor}.
         * <p>
         * The color is dynamically interpolated between {@code autoOnNormalColor} and
         * {@code autoOnHoverColor} based on the current animation position.
         *
         * @return the current {@link Color} of the button
         */
        @Override
        protected Color getColor() {
            return internalColor.get();
        }
        /**
         * Terminates the button's color animation when the button is removed from its container.
         * This method stops the {@code internalColor} animation by calling its {@code stop()} method,
         * ensuring that the associated animation thread is safely terminated. This prevents resource leaks
         * when the button is no longer part of the UI hierarchy.
         *
         * @see DynamicColor#stop()
         * @see javax.swing.JComponent#removeNotify()
         */
        @Override
        public void removeNotify() {
            super.removeNotify();
            internalColor.stop();
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
        /**
         * Terminates the button's color animation when the button is removed from its container.
         * This method stops the {@code internalColor} animation by calling its {@code stop()} method,
         * ensuring that the associated animation thread is safely terminated. This prevents resource leaks
         * when the button is no longer part of the UI hierarchy.
         *
         * @see DynamicColor#stop()
         * @see javax.swing.JComponent#removeNotify()
         */
        @Override
        public void removeNotify() {
            super.removeNotify();
            internalColor.stop();
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
     * The class also provides caching mechanisms to avoid recalculating the {@link #getCachedCircle() circle}
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
                double diameter = Math.min(w, h) * radiusMultiplier;
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
                double minRadius = Math.min(w, h) * 0.5 * radiusMultiplier;
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
}
