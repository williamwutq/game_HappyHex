package Launcher;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

public class LaunchButton extends JButton implements ActionListener {
    private final int sizeConstant = 6;
    public LaunchButton(){
        super();
        this.setText("START");
        this.setForeground(LaunchEssentials.launchStartButtonFontColor);
        this.setBackground(LaunchEssentials.launchStartButtonBackgroundColor);
        this.setOpaque(true);
        this.setBorder(new EmptyBorder(sizeConstant, sizeConstant * 2, sizeConstant, sizeConstant * 2));

        this.setLayout(null);
        this.setBounds(0,0,this.getWidth(),this.getHeight());
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setFont(new Font(LaunchEssentials.launchStartButtonFont, Font.BOLD, 30));
        this.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        LaunchEssentials.startGame();
    }
}
