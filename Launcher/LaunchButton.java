package Launcher;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

public abstract class LaunchButton extends JButton implements ActionListener {
    private static int sizeConstant = 1;
    private static Color backGroundColor = Color.WHITE;
    public LaunchButton(String text){
        super();
        this.setText(text);
        this.setForeground(backGroundColor);
        this.setBackground(fetchColor());
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setBorder(new EmptyBorder(sizeConstant, sizeConstant * 2, sizeConstant, sizeConstant * 2));

        this.setLayout(null);
        this.setBounds(0,0,this.getWidth(),this.getHeight());
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setFont(new Font(LaunchEssentials.launchButtonFont, Font.BOLD, sizeConstant * 5));
        this.addActionListener(this);
    }
    public final void paint(Graphics g) {
        this.setBorder(new EmptyBorder(sizeConstant, sizeConstant * 2, sizeConstant, sizeConstant * 2));
        this.setFont(new Font(LaunchEssentials.launchButtonFont, Font.BOLD, sizeConstant * 5));
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), sizeConstant * 6, sizeConstant * 6);
        g2.dispose();
        super.paintComponent(g);
    }
    public static void setSizeConstant(int size){
        sizeConstant = size;
    }
    public static void setBackGroundColor(Color color){
        backGroundColor = color;
    }
    public final void actionPerformed(ActionEvent e) {
        clicked();
    }
    public final String toString(){
        return "LaunchButton[Class = " + getClass().getName() + ", Text = " + this.getText() + ", Color = " + this.getBackground() + "]";
    }
    // Prevent children
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}
    // Abstract methods
    abstract protected void clicked();
    abstract protected Color fetchColor();
}
