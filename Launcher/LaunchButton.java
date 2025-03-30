package Launcher;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;

public class LaunchButton extends JButton implements ActionListener {
    public LaunchButton(){
        this.setText("START");
        this.setForeground(LaunchEssentials.launchStartButtonFontColor);
        this.setBackground(LaunchEssentials.launchStartButtonBackgroundColor);
        this.setOpaque(true);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setFont(new Font(LaunchEssentials.launchStartButtonFont, Font.BOLD, 30));
        this.setBorder(new EmptyBorder(5,15,5,15));
        this.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        LaunchEssentials.startGame();
    }
}
