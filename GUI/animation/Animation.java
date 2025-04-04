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
 *
 * <p>The animation runs for a specified number of frames, each separated by a given delay in milliseconds.
 * The animation progresses linearly, and each frame is rendered via the abstract {@code paintFrame()} method.
 * Once the animation finishes, the component removes itself from its parent container. This component is
 * disposable and will not be able to run again once start is triggered.</p>
 *
 * <p>Designed for simple temporary effects such as fading, expanding, or progress animations.</p>
 *
 * <p>Note: This component does not allow adding child components and ignores layout, background, opacity, and border settings.</p>
 */
abstract class Animation extends Component{
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
    abstract void paintFrame(java.awt.Graphics graphics, double progress);

    // --- Disabled functions for lightweight rendering ---
    /** Disabled: This component does not support child components. */
    public final Component add(Component comp) { return comp; }

    /** Disabled: Prevents changing background color. */
    public final void setBackground(Color color) {}
}