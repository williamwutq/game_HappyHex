package GUI.animation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

abstract class Animation extends JComponent implements ActionListener{
    private int progress;
    private int totalFrames;
    private int frameTime;
    public Animation(int totalFrames, int frameTime){
        super();
        this.progress = 0;
        this.totalFrames = totalFrames;
        this.frameTime = 100;
        if (frameTime <= 100000 && frameTime >= 10) {
            this.frameTime = frameTime;
        }
        super.setLayout(null);
        super.setBackground(new Color(0,0,0,0));
        super.setBounds(new Rectangle(0, 0, 1, 1));
        super.setBorder(new EmptyBorder(0,0,0,0));
        super.setOpaque(false);
    }
    private void nextFrame(){
        if(progress < totalFrames){
            progress ++;
            this.repaint();
            System.out.println(progress);
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
                this.repaint();
            }
        }
    }
    public int getTotalTime(){
        return totalFrames * frameTime;
    }
    public void setTotalFrames(int count){
        if(count >= 0) {
            totalFrames = count;
        }
    }
    public void setFrameTime(int time){
        if (time <= 100000 && time >= 10) {
            this.frameTime = time;
        }
    }
    public void start(){
        if(totalFrames == 0 || progress < totalFrames){
            Timer timer = new Timer(frameTime, this);
            timer.setRepeats(false);
            timer.start();
        }
    }
    public void paint(java.awt.Graphics graphics){
        paintFrame(graphics, progress/(double)totalFrames);
    }
    abstract void paintFrame(java.awt.Graphics graphics, double progress);
    // Prevent Overrides
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}
    public final void setOpaque(boolean opaque){}
    public final void setBackground(Color color){}
    public final void setBorder(Border border){}

    @Override
    public void actionPerformed(ActionEvent e) {
        nextFrame();
    }
}
