package Launcher;

import Launcher.IO.Username;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GameLoginField extends JTextField implements ActionListener {
    public GameLoginField(){
        super(Username.MAX_LENGTH);
        this.setBorder(new CompoundBorder(new LineBorder(Color.black, 2), new EmptyBorder(12,6,12,6)));
        Dimension dimension = new Dimension(100,100);
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.addActionListener(this);
    }

    public void setDimension(int size){
        this.setBorder(new CompoundBorder(new LineBorder(Color.black, 2), new EmptyBorder(size * 2, size,size *2 , size)));
        Dimension dimension = new Dimension(size * Username.MAX_LENGTH + size * 2, size * 6);
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
    }

    public void actionPerformed(ActionEvent e) {

    }
}
