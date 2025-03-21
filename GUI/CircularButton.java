package GUI;

import Hex.Hex;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CircularButton extends JButton implements ActionListener {
    public CircularButton(Hex position, JPanel GamePanel){
        super();
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e){
        // When it is clicked do something
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Test: CircularButton");
        JPanel panel = new JPanel();
        JButton circularbutton = new CircularButton(new Hex(), null);
        panel.setLayout(null);
        panel.add(circularbutton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
