package Launcher;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

public class LaunchButton extends JButton implements ActionListener {
    private static int sizeConstant = 6;
    public LaunchButton(String text){
        super();
        this.setText(text);
        this.setForeground(LaunchEssentials.launchStartButtonFontColor);
        this.setBackground(LaunchEssentials.launchStartButtonBackgroundColor);
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
        this.setFont(new Font(LaunchEssentials.launchStartButtonFont, Font.BOLD, sizeConstant*5));
        this.addActionListener(this);
    }
    public void paint(Graphics g) {
        this.setBorder(new EmptyBorder(sizeConstant, sizeConstant * 2, sizeConstant, sizeConstant * 2));
        this.setFont(new Font(LaunchEssentials.launchStartButtonFont, Font.BOLD, sizeConstant * 5));
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
    public void actionPerformed(ActionEvent e) {
    }
}
