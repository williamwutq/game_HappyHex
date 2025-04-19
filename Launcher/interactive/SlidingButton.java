package Launcher.interactive;

import Launcher.LaunchEssentials;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SlidingButton extends JComponent implements ComponentListener, GUI.Recolorable {
    private boolean state;
    private int radius;
    private int innerGap;
    private int halfBorderGap;
    private String onText;
    private String offText;
    private InnerButton button;
    private Color onColor;
    private Color offColor;
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
        radius = Math.min(this.getWidth()*2/3, this.getHeight());
        halfBorderGap = radius / 16;
        innerGap = radius / 12;
        button.setFont(new Font(LaunchEssentials.launchSettingsSlidingButtonFont, Font.BOLD, radius/2 - innerGap));
        this.setBorder(new EmptyBorder(halfBorderGap, halfBorderGap, halfBorderGap, halfBorderGap));
    }
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
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.getBackground());
        g2.fillRoundRect(halfBorderGap, halfBorderGap, getWidth() - 2 * halfBorderGap, getHeight() - 2 * halfBorderGap, radius - halfBorderGap, radius - halfBorderGap);
        g2.dispose();
        super.paintChildren(g);
    }

    private final class InnerButton extends JButton implements ActionListener{
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