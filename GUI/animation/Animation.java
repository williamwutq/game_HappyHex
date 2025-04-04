package GUI.animation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * An abstract class for creating a simple linear, finite-duration animation in Swing.
 *
 * <p>The animation runs for a specified number of frames, each separated by a given delay in milliseconds.
 * The animation progresses linearly, and each frame is rendered via the abstract {@code paintFrame()} method.
 * Once the animation finishes, the component removes itself from its parent container.</p>
 *
 * <p>Designed for simple temporary effects such as fading, expanding, or progress animations.</p>
 *
 * <p>Note: This component does not allow adding child components and ignores layout, background, opacity, and border settings.</p>
 */
abstract class Animation extends JComponent implements ActionListener{
    /** Current frame progress of the animation. */
    private int progress;

    /** Total number of frames in the animation. */
    private int totalFrames;

    /** Time between frames in milliseconds. */
    private int frameTime;

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
        // Set minimal properties to prevent layout/painting conflicts
        super.setLayout(null);
        super.setBackground(new Color(0,0,0,0));
        super.setBounds(new Rectangle(0, 0, 1, 1));
        super.setBorder(new EmptyBorder(0,0,0,0));
        super.setOpaque(false);
    }

    /**
     * Advances the animation by one frame and repaints the component.
     * If the animation has completed, the component removes itself from its parent.
     */
    private void nextFrame(){
        if(progress < totalFrames){
            progress ++;
            this.repaint();
            Timer timer = new Timer(frameTime, this);
            timer.setRepeats(false);
            timer.start();
        } else {
            try{
                Container parent = this.getParent();
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            } catch (Exception e) {
                this.repaint(); // Fallback if parent is null or removal fails
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
        if(totalFrames == 0 || progress < totalFrames){
            Timer timer = new Timer(frameTime, this);
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Called by Swing to paint the component.
     * Delegates to {@link #paintFrame(java.awt.Graphics, double)} using normalized progress.
     *
     * @param graphics the Graphics context in which to paint.
     */
    public void paint(java.awt.Graphics graphics){
        paintFrame(graphics, progress / (double) totalFrames);
    }

    /**
     * Renders the animation frame.
     * Subclasses must implement this to define how each animation frame looks.
     *
     * @param graphics the Graphics context to draw on.
     * @param progress a double between 0 and 1 indicating animation progress.
     */
    abstract void paintFrame(java.awt.Graphics graphics, double progress);

    /**
     * Called by Timer to advance the animation.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        nextFrame();
    }

    // --- Disabled functions for lightweight rendering ---
    /** Disabled: This component does not support child components. */
    public final Component add(Component comp) { return comp; }

    /** Disabled: Prevents adding components internally. */
    protected final void addImpl(Component comp, Object constraints, int index) {}

    /** Disabled: Prevents adding container listeners. */
    public final void addContainerListener(ContainerListener l) {}

    /** Disabled: Prevents changing opacity. */
    public final void setOpaque(boolean opaque) {}

    /** Disabled: Prevents changing background color. */
    public final void setBackground(Color color) {}

    /** Disabled: Prevents changing border. */
    public final void setBorder(Border border) {}
}
