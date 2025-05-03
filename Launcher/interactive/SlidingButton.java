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

package Launcher.interactive;

import Launcher.LaunchEssentials;
import Launcher.Recolorable;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A custom Swing {@link JComponent} that implements a sliding toggle button with ON and OFF states.
 * The button features a visually appealing sliding animation effect, where an inner button moves
 * between the left (ON) and right (OFF) sides of the component, accompanied by customizable text
 * and background colors. It is designed for use in user interfaces requiring a compact, interactive
 * toggle control, such as settings panels or dashboards.
 * <p>
 * The component integrates with {@link LaunchEssentials} for consistent theming, including colors
 * and fonts, and implements the {@link Recolorable} interface to support dynamic color updates.
 * The button dynamically resizes based on its container, recalculating dimensions, borders, and
 * font sizes to maintain a polished appearance. It also provides hooks for subclasses to respond
 * to state changes via the {@link #turnedOn()} and {@link #turnedOff()} methods.
 * <p>
 * The sliding effect is achieved using a {@link BoxLayout} with horizontal glue components that
 * reposition the inner button. The component uses anti-aliased graphics for smooth, rounded edges
 * and supports mouse interactions to toggle the state. It is highly customizable, allowing
 * developers to mandate specific sizes, override state change behavior, and reset colors
 * dynamically.
 *
 * @author William Wu
 * @version 1.2
 * @see LaunchEssentials
 * @see Recolorable
 */
public class SlidingButton extends JComponent implements ComponentListener, Recolorable {
    /** The current state of the button: {@code true} for ON, {@code false} for OFF. */
    private boolean state;
    /** The radius used for rounded corners, derived from the component's dimensions. */
    private int radius;
    /** The gap between the inner button and its container, derived from the component's {@link #radius}. */
    private int innerGap;
    /** Half the border gap, used to calculate padding around the component, derived from the component's {@link #radius}. */
    private int halfBorderGap;
    /** The text displayed when the button is in the ON {@link #state}. Defaults to "ON". */
    private String onText;
    /** The text displayed when the button is in the OFF {@link #state}. Defaults to "OFF". */
    private String offText;
    /** The {@link InnerButton} component that handles user clicks and displays the state text. */
    private InnerButton button;
    /** The background {@link Color} used when the button is in the ON {@link #state}. */
    private Color onColor;
    /** The background {@link Color} used when the button is in the OFF {@link #state}. */
    private Color offColor;
    /**
     * Constructs a new {@code SlidingButton} with default settings. Initializes the button in the OFF state,
     * with default colors from {@link LaunchEssentials}, text ({@link #onText "ON"} and {@link #offText "OFF"}),
     * and a centered layout. If you wish to set the {@code #state} of this button, use {@link #setState(boolean)}.
     * The component is non-opaque, uses a {@link BoxLayout} for horizontal arrangement, and registers a
     * {@link ComponentListener} to handle resizing and visibility changes. The initial size is set to a minimal
     * dimension, which is recalculated dynamically when the component is displayed. This component is NOT a
     * {@link JButton}, but can retrieve user interaction via the {@link #turnedOn} and {@link #turnedOff} methods,
     * which could be overridden in subclasses.
     * <p>
     * The {@code InnerButton} is created and added to the layout, along with a horizontal lue component to facilitate
     * the sliding effect. The button's appearance is configured to be visually consistent with the application's theme.
     */
    public SlidingButton(){
        super();
        this.state = false;
        this.radius = 1;
        this.innerGap = 1;
        this.halfBorderGap = 1;
        this.onText = "ON";
        this.offText = "OFF";
        this.onColor = LaunchEssentials.launchSlidingButtonOnColor;
        this.offColor = LaunchEssentials.launchSlidingButtonOffColor;
        this.button = new InnerButton();
        this.setOpaque(false);
        this.setBackground(offColor);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(new EmptyBorder(1,1,1,1));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.add(button);
        this.add(Box.createHorizontalGlue());
        this.addComponentListener(this);
        this.mandateSize(new Dimension(1,1));
    }
    /**
     * Sets the background color of the component and synchronizes the inner button's
     * foreground color to ensure visual consistency.
     * <p>
     * This method overrides the default {@link JComponent#setBackground(Color)} to update the {@link InnerButton}
     * appearance, ensuring that text and other foreground elements remain visible against the new background.
     *
     * @param color The background color to set.
     */
    public final void setBackground(Color color){
        super.setBackground(color);
        if(button != null) {
            button.setForeground(color);
        }
    }

    /**
     * Called when the button transitions to the ON state. Updates the background color to {@link #onColor}
     * and repositions the inner button to the left side by adjusting the layout.
     * <p>
     * Subclasses may override this method to perform additional actions when the button is turned ON,
     * such as triggering events or updating external state. Overriding methods must call {@code super.turnedOn()}
     * to ensure proper layout and color updates.
     */
    protected void turnedOn(){
        this.setBackground(onColor);
        this.remove(1);
        this.add(Box.createHorizontalGlue(), 0);
        this.revalidate();
    }
    /**
     * Called when the button transitions to the OFF state. Updates the background color to {@link #offColor}
     * and repositions the inner button to the right side by adjusting the layout.
     * <p>
     * Subclasses may override this method to perform additional actions when the button is turned OFF,
     * such as triggering events or updating external state. Overriding methods must call {@code super.turnedOff()}
     * to ensure proper layout and color updates.
     */
    protected void turnedOff(){
        this.setBackground(offColor);
        this.remove(0);
        this.add(Box.createHorizontalGlue(), 1);
        this.revalidate();
    }
    /**
     * Returns the current {@link #state} of the button.
     * @return {@code true} if the button is in the ON state, {@code false} if in the OFF state.
     */
    public boolean getState(){
        return state;
    }

    /**
     * Sets the size of the component to the specified dimension, ensuring consistent sizing.
     * Triggers a recalculation of internal dimensions and repaints the component to reflect the new size.
     * <p>
     * This method is useful for enforcing specific dimensions in layouts where the
     * button must maintain a fixed size, such as in a settings panel or toolbar.
     *
     * @param size The dimension to set for the component's size.
     */
    public void mandateSize(Dimension size) {
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        recalculate();
        repaint();
    }
    /**
     * Helper method to recalculates the dimensions, borders, and font of the button based on the current component size.
     * Updates the inner button's size, the border gaps, the corner radius, and the font size to ensure a
     * visually consistent and proportional appearance.
     * <p>
     * This method is called automatically in response to resize, move, or show events, as well as when the size
     * is mandated explicitly. It ensures that the button scales appropriately while maintaining smooth, rounded
     * edges and readable text.
     */
    private void recalculate(){
        Dimension dimension = new Dimension(this.getWidth()*2/3, this.getHeight());
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        button.setMaximumSize(dimension);
        radius = Math.min(this.getWidth()*2/3, this.getHeight());
        halfBorderGap = radius / 16;
        innerGap = radius / 12;
        button.setFont(new Font(LaunchEssentials.launchSettingsSlidingButtonFont, Font.BOLD, radius/2 - innerGap));
        this.setBorder(new EmptyBorder(halfBorderGap, halfBorderGap, halfBorderGap, halfBorderGap));
    }
    /**
     * Sets the {@link #state} of the button and updates its appearance, including the text, background color,
     * and position of the inner button. Call this method whenever necessary.
     * <p>
     * If the new state differs from the current state, the method updates the {@link InnerButton}'s text ("ON" or "OFF"),
     * changes the background color, and repositions the inner button using the layout to create the sliding effect.
     * The component is {@link #revalidate revalidated} to ensure the inner button is on the correct side.
     *
     * @param state {@code true} to set the button to ON, {@code false} to set it to OFF.
     */
    public void setState(boolean state){
        if(this.state != state) {
            this.state = state;
            if (state) {
                button.setText(onText);
                this.setBackground(onColor);
                this.remove(1);
                this.add(Box.createHorizontalGlue(), 0);
                this.revalidate();
            } else {
                button.setText(offText);
                this.setBackground(offColor);
                this.remove(0);
                this.add(Box.createHorizontalGlue(), 1);
                this.revalidate();
            }
        }
    }

    /**
     * Recalculating dimensions and repainting the component to ensure it displays correctly when resized.
     * @param e The component event.
     */
    public final void componentResized(ComponentEvent e) {
        recalculate();
        repaint();
    }
    /**
     * Recalculating dimensions and repainting the component to ensure it displays correctly when moved.
     * @param e The component event.
     */
    public final void componentMoved(ComponentEvent e) {
        recalculate();
        repaint();
    }
    /**
     * Recalculating dimensions and repainting the component to ensure it displays correctly when made visible.
     * @param e The component event.
     */
    public final void componentShown(ComponentEvent e) {
        recalculate();
        repaint();
    }
    /**
     * No action is taken, as the component does not need to update when hidden.
     * @param e The component event.
     */
    public final void componentHidden(ComponentEvent e) {}
    /**
     * Resets the colors of the button to match the current settings in {@link LaunchEssentials}. Updates the
     * ON and OFF colors, the inner button's background, and the component's background based on the current state.
     * <p>
     * This method is part of the {@link Recolorable} interface and can be called when the application's color
     * scheme is updated, ensuring the button remains visually consistent with the theme.
     */
    public final void resetColor() {
        this.onColor = LaunchEssentials.launchSlidingButtonOnColor;
        this.offColor = LaunchEssentials.launchSlidingButtonOffColor;
        button.setBackground(LaunchEssentials.launchSlidingButtonEmptyColor);
        if (state) {
            this.setBackground(onColor);
        } else {
            this.setBackground(offColor);
        }
    }
    /**
     * Paints the {@link JComponent} by drawing a rounded rectangle background with anti-aliased edges.
     * The background color reflects the button's current state (ON or OFF).
     * <p>
     * The method uses a {@link Graphics2D} context to enable antialiasing for smooth rendering and draws
     * the background before delegating to {@link #paintChildren(Graphics)} to render the {@link InnerButton}.
     *
     * @param g The graphics context used for painting.
     * @see InnerButton#paint
     */
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.getBackground());
        g2.fillRoundRect(halfBorderGap, halfBorderGap, getWidth() - 2 * halfBorderGap, getHeight() - 2 * halfBorderGap, radius - halfBorderGap, radius - halfBorderGap);
        g2.dispose();
        super.paintChildren(g);
    }

    /**
     * Inner class representing the clickable button within the {@link SlidingButton}.
     * The inner button displays the state text ("ON" or "OFF") and handles user clicks to toggle
     * the button's state. It is rendered with a rounded rectangle background and supports
     * anti-aliased graphics for a smooth appearance. The clicking action is handled through
     * {@link #actionPerformed} and the {@link SlidingButton#turnedOn} or {@link SlidingButton#turnedOff}
     * methods in {@code SlidingButton}.
     */
    private final class InnerButton extends JButton implements ActionListener{
        /**
         * Constructs the inner button with default settings.
         * <p>
         * This initializes the button with the OFF state text, background color from {@link LaunchEssentials},
         * and a bold font. The button is configured to be non-focusable and centered within the parent component.
         * An {@link ActionListener} is added to handle {@link #actionPerformed click} events.
         * @see SlidingButton
         */
        private InnerButton(){
            this.setText(offText);
            this.setBackground(LaunchEssentials.launchSlidingButtonEmptyColor);
            this.setOpaque(true);
            this.setContentAreaFilled(false);
            this.setFocusPainted(false);
            this.setFont(new Font(LaunchEssentials.launchSettingsSlidingButtonFont, Font.BOLD, 20));
            this.setBorder(new EmptyBorder(1,1,1,1));
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);
            this.addActionListener(this);
        }
        /**
         * Handles click events by toggling the button's state and updating its appearance.
         * When clicked, the button switches between ON and OFF states, updates the displayed text,
         * and calls the appropriate state change method ({@link SlidingButton#turnedOn()} or
         * {@link SlidingButton#turnedOff()}).
         * @param e The action event triggered by the click.
         */
        @Override
        public final void actionPerformed(ActionEvent e) {
            if(state){
                state = false;
                this.setText(offText);
                turnedOff();
            } else {
                state = true;
                this.setText(onText);
                turnedOn();
            }
        }
        /**
         * Paints the inner button with a rounded rectangle background using anti-aliased graphics.
         * The button is presented with a reasonable {@link #innerGap gap} from its container calculated by {@link #recalculate()}.
         * The background is drawn with the color specified in {@link LaunchEssentials#launchSlidingButtonEmptyColor},
         * and the text is rendered by the {@link JButton}' painting method.
         * <p>
         * The rounded rectangle is sized to fit within the button's bounds, accounting for the inner gap
         * and border to maintain a consistent appearance with the parent component.
         *
         * @param g The graphics context used for painting.
         * @see SlidingButton#paint
         */
        public void paint(Graphics g){
            int combinedRadius = radius - 2 * (innerGap + halfBorderGap);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.getBackground());
            g2.fillRoundRect(innerGap, innerGap, this.getWidth() - 2 * innerGap, this.getHeight() - 2 * innerGap, combinedRadius, combinedRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}