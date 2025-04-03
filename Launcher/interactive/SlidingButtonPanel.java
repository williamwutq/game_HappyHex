package Launcher.interactive;

import Launcher.LaunchEssentials;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class SlidingButtonPanel extends JPanel implements ComponentListener {
    private boolean state;
    private String onText;
    private String offText;
    private SlidingButton button;
    private Color onColor;
    private Color offColor;
    public SlidingButtonPanel(){
        super();
        this.state = false;
        this.onText = "ON";
        this.offText = "OFF";
        this.onColor = Color.GREEN;
        this.offColor = Color.RED;
        this.button = new SlidingButton();
        this.setBackground(offColor);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(new EmptyBorder(0,0,0,0));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.add(button);
        this.add(Box.createHorizontalGlue());
        this.addComponentListener(this);
        this.mandateSize(new Dimension(1,1));
    }
    public final void setTexts(String onText, String offText){
        this.onText = onText;
        this.offText = offText;
    }
    public final void setBackground(Color on, Color off){
        this.onColor = on;
        this.offColor = off;
    }
    public final void setBackground(Color color){
        super.setBackground(color);
        if(button != null) {
            button.setForeground(color);
        }
    }

    protected void turnedOn(){
        this.setBackground(onColor);
        this.remove(1);
        this.add(Box.createHorizontalGlue(), 0);
        this.revalidate();
    }
    protected void turnedOff(){
        this.setBackground(offColor);
        this.remove(0);
        this.add(Box.createHorizontalGlue(), 1);
        this.revalidate();
    }
    public boolean getState(){
        return state;
    }
    public void mandateSize(Dimension size) {
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        recalculate();
        repaint();
    }
    private void recalculate(){
        Dimension dimension = new Dimension(this.getWidth()*2/3, this.getHeight());
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        button.setMaximumSize(dimension);
        int size = Math.min(this.getWidth()*2/3, this.getHeight())/2;
        button.setFont(new Font(LaunchEssentials.launchSettingsSlidingButtonFont, Font.BOLD, size));
    }

    public final void componentResized(ComponentEvent e) {
        recalculate();
        repaint();
    }
    public final void componentMoved(ComponentEvent e) {
        recalculate();
        repaint();
    }
    public final void componentShown(ComponentEvent e) {
        recalculate();
        repaint();
    }
    public final void componentHidden(ComponentEvent e) {}

    private final class SlidingButton extends JButton implements ActionListener{
        private SlidingButton(){
            this.setText(offText);
            this.setBackground(Color.WHITE);
            this.setOpaque(true);
            this.setFont(new Font(LaunchEssentials.launchSettingsSlidingButtonFont, Font.BOLD, 20));
            this.setBorder(new EmptyBorder(0,0,0,0));
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);
            this.addActionListener(this);
        }
        @Override
        public final void actionPerformed(ActionEvent e) {
            if(state){
                state = false;
                this.setText(offText);
                this.setForeground(offColor);
                turnedOff();
            } else {
                state = true;
                this.setText(onText);
                this.setForeground(onColor);
                turnedOn();
            }
        }
    }
}