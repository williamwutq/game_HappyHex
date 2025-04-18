package GUI.animation;

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

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * An abstract class for creating a simple linear, finite-duration animation in Swing.
 * This handler is designed for simple temporary effects such as fading, expanding, or progress animations.
 * <p>
 * The animation runs for a specified number of frames, each separated by a given delay in milliseconds.
 * The animation progresses linearly, and each frame is rendered via the abstract {@link #paintFrame} method.
 * Once the animation finishes, the component removes itself from its parent container. This component is
 * disposable and will not be able to run again once start is triggered.
 * <p>
 * The animation can be initiated using a start listener, accessible via the {@link #getStartListener} method.
 * This returns an {@link ActionListener} that, when triggered, calls the {@link #start} method to begin the
 * animation. The start listener is particularly useful for integrating the animation with other Swing components,
 * such as buttons or timers. For example, attaching the start listener to a button's action allows the animation
 * to begin when the button is clicked, enabling seamless coordination with user interactions or other events in
 * the application.
 * <p>
 * Additionally, an end listener can be specified either through the constructor or by using the
 * {@link #setEndListener} method. This {@code ActionListener} is executed exactly once, immediately after the
 * animation completes and the component is removed from its parent container. The end listener is useful for
 * triggering follow-up actions, such as starting another animation, updating the UI, or signaling the completion
 * of a visual effect. For instance, an end listener could be used to display a new component or transition to a
 * different application state once a fade-out effect finishes, ensuring smooth sequencing of events in the user
 * interface.
 * <p>
 * Example Usage of listeners:
 * </p>
 * <pre>{@code
 * Boo firstAnimation = new Boo(50, 20);
 * Boo secondAnimation = new Boo(50, 20);
 * JButton startButton = new JButton("Start");
 * firstAnimation.setEndListener(secondAnimation.getStartListener());
 * startButton.addActionListener(firstAnimation.getStartListener());
 * }</pre>
 * <p>
 * In this example, a {@code JButton} named {@code startButton} is linked to the {@link #startListener} of
 * {@code firstAnimation}, initiating it when clicked. The {@link #endListener} of {@code firstAnimation} is set to
 * the {@code startListener} of {@code secondAnimation}, ensuring that {@code secondAnimation} begins immediately
 * after {@code firstAnimation} completes. This creates a sequence where clicking the button triggers
 * {@code firstAnimation}, and upon its completion, {@code secondAnimation} starts automatically, enabling smooth,
 * sequential visual effects.
 * <p>
 * The {@code Animation} class provide a graphic {@link Component} template but cannot be used directly. To use the
 * functions provided by {@code Animation}, it is necessary to create a subclass or an anonymous subclass, overriding
 * the {@link #paintFrame} method. The progress variable passed in the method can be used to create linear animations,
 * or be manipulated to create more advanced animations using the {@link Math} library.
 * <p>
 * Note: This component does not allow adding child components and ignores layout, background, opacity,
 * and border settings.
 * <p>
 * Example: The following subclass creates a fading animation that transitions a colored rectangle
 * from fully opaque to fully transparent:
 * <pre>{@code
 * public class FadeOutAnimation extends Animation {
 *     private Color color;
 *     private int width;
 *     private int height;
 *
 *     public FadeOutAnimation(int totalFrames, int frameTime, Color color, int width, int height) {
 *         super(totalFrames, frameTime);
 *         this.color = color;
 *         this.width = width;
 *         this.height = height;
 *         setBounds(0, 0, width, height);
 *     }
 *
 *     protected void paintFrame(Graphics graphics, double progress) {
 *         graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((1 - progress) * 255)));
 *         graphics.fillRect(0, 0, width, height);
 *     }
 * }
 * }</pre>
 * <p>
 * The {@code FadeOutAnimation} subclass creates a simple animation that fades a colored rectangle from fully opaque
 * to fully transparent over a specified duration. It extends the {@code Animation} class, taking parameters for the
 * total number of frames, frame time, initial color, and rectangle dimensions. The {@code paintFrame} method draws a
 * rectangle with the given color, adjusting its alpha value based on the animation's progress to achieve the fading effect.
 * Once the animation completes, the component removes itself from its parent container, as defined in the parent class.
 */
public abstract class Animation extends Component{
    /** Current frame progress of the animation. */
    private int progress;
    /** Total number of frames in the animation. */
    private int totalFrames;
    /** Time between frames in milliseconds. */
    private int frameTime;
    /** Whether this animation is active. */
    private boolean active;
    /** ActionListener that starts animation. */
    private final ActionListener startListener;
    /** ActionListener to be completed after animation. */
    private ActionListener endListener;
    /** The Timer used to execute actions. */
    private final Timer timer = new Timer(true);

    /**
     * Constructs an Animation object with a given number of total frames and frame time.
     *
     * @param totalFrames the total number of frames to render.
     * @param frameTime   the delay between each frame in milliseconds (default to 1 millisecond, must be greater than 0).
     */
    public Animation(int totalFrames, int frameTime){
        super();
        this.progress = 0;
        this.totalFrames = totalFrames;
        this.frameTime = 1;
        if (frameTime > 0) {
            this.frameTime = frameTime;
        }
        this.active = false;
        this.startListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        };
        this.endListener = null;
        // Set minimal properties to prevent layout/painting conflicts
        super.setBackground(new Color(0,0,0,0));
        super.setBounds(new Rectangle(0, 0, 1, 1));
    }

    /**
     * Constructs an Animation object with a given number of total frames, frame time, start and end actions
     *
     * @param totalFrames the total number of frames to render.
     * @param frameTime   the delay between each frame in milliseconds (default to 1 millisecond, must be greater than 0).
     * @param endListener   the actionListener that will be trigger at the end of the animation.
     */
    public Animation(int totalFrames, int frameTime, ActionListener endListener){
        super();
        this.progress = 0;
        this.totalFrames = totalFrames;
        this.frameTime = 1;
        if (frameTime > 0) {
            this.frameTime = frameTime;
        }
        this.active = false;
        this.startListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        };
        this.endListener = endListener;
        // Set minimal properties to prevent layout/painting conflicts
        super.setBackground(new Color(0,0,0,0));
        super.setBounds(new Rectangle(0, 0, 1, 1));
    }

    /**
     * Advances the animation by one frame and repaints the component.
     * If the animation has completed, the component removes itself from its parent.
     */
    private void nextFrame(){
        if(progress < totalFrames){
            progress ++;
            this.repaint();
            TimerTask task = new TimerTask() {
                public void run(){nextFrame();}
            };
            timer.schedule(task, frameTime);
        } else {
            this.active = false;
            try{
                Container parent = this.getParent();
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            } catch (Exception e) {
                this.repaint(); // Fallback if parent is null or removal fails
            }
            if(endListener != null){
                // Trigger endListener
                endListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }

    /**
     * Gets the total duration of the animation in milliseconds.
     *
     * @return the total time in milliseconds.
     */
    public int getTotalTime(){
        return totalFrames * frameTime;
    }

    /**
     * Sets a new total frame count for the animation.
     *
     * @param count the new total number of frames (must be non-negative).
     */
    public void setTotalFrames(int count){
        if(count >= 0) {
            totalFrames = count;
        }
    }

    /**
     * Sets the time between each frame in milliseconds.
     *
     * @param time the new frame time (must be greater than 0).
     */
    public void setFrameTime(int time){
        if (time > 0) {
            this.frameTime = time;
        }
    }

    /**
     * Starts the animation if it has frames to render.
     */
    public void start(){
        if(totalFrames != 0 && progress < totalFrames && !this.active){
            this.active = true;
            this.repaint();
            TimerTask task = new TimerTask() {
                public void run(){nextFrame();}
            };
            timer.schedule(task, frameTime);
        }
    }

    /**
     * Gets the actionLister that will trigger this animation.
     *
     * @return the start actionListener, which will start this animation.
     */
    public ActionListener getStartListener(){
        return startListener;
    }

    /**
     * Sets the actionLister that will be triggered after this animation.
     *
     * @param listener the end actionListener to be completed after animation.
     */
    public void setEndListener(ActionListener listener){
        this.endListener = listener;
    }

    /**
     * Called by Swing to paint the component.
     * Delegates to {@link #paintFrame(java.awt.Graphics, double)} using normalized progress.
     *
     * @param graphics the Graphics context in which to paint.
     */
    public void paint(java.awt.Graphics graphics){
        if(active) {
            paintFrame(graphics, progress / (double) totalFrames);
        }
    }

    /**
     * Renders the animation frame.
     * Subclasses must implement this to define how each animation frame looks.
     *
     * @param graphics the Graphics context to draw on.
     * @param progress a double between 0 and 1 indicating animation progress.
     */
    abstract protected void paintFrame(java.awt.Graphics graphics, double progress);

    // --- Disabled functions for lightweight rendering ---
    /** Disabled: This component does not support child components. */
    public final Component add(Component comp) { return comp; }

    /** Disabled: Prevents changing background color. */
    public final void setBackground(Color color) {}
}