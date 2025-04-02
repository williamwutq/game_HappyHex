package Launcher.interactive;

import Launcher.LaunchEssentials;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class SlidingButtonPanel extends JPanel {
    private boolean state;
    private String onText;
    private String offText;
    private Color backgroundColor;
    public SlidingButtonPanel(Color backgroundColor){
        super();
        this.state = false;
        this.onText = "ON";
        this.offText = "OFF";
        this.backgroundColor = backgroundColor;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(new LineBorder(Color.BLACK, 2));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setBackground(backgroundColor);
        this.add(new SlidingButton());
        this.add(Box.createHorizontalGlue());
    }
    public void setTexts(String onText, String offText){
        this.onText = onText;
        this.offText = offText;
    }

    protected void turnedOn(){
        this.remove(1);
        this.add(Box.createHorizontalGlue(), 0);
        this.revalidate();
    }
    protected void turnedOff(){
        this.remove(0);
        this.add(Box.createHorizontalGlue(), 1);
        this.revalidate();
    }
    private class SlidingButton extends JButton implements ActionListener{
        private SlidingButton(){
            this.setText(offText);
            this.setForeground(backgroundColor);
            this.setBackground(Color.WHITE);
            this.setOpaque(true);
            this.setFont(new Font(LaunchEssentials.launchSettingsSlidingButtonFont, Font.BOLD, 20));
            this.setBorder(new EmptyBorder(0,0,0,0));
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);
            this.addActionListener(this);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
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
    }
    public static void main (String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setTitle("SlidingButtonPanel Tests");
        frame.setLayout(new BorderLayout());
        frame.add(new SlidingButtonPanel(Color.BLUE), BorderLayout.CENTER);
        frame.setVisible(true);
    }
}