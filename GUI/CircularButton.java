package GUI;

import Hex.Hex;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class CircularButton extends JButton implements ActionListener {
    public CircularButton(Hex position, JPanel GamePanel){
        super();
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setForeground(Color.BLACK);
        this.setBackground(new Color(0,0,0,0));
        this.setBorder(new EmptyBorder(0,0,0,0));
        this.addActionListener(this);
    }
    public void setSize(int size){
        Dimension dimension = new Dimension(size, size);
        this.setSize(dimension);
        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);
    }
    public void setPosition(int x, int y){
        this.setBounds(x - this.getWidth()/2,y - this.getHeight()/2, this.getWidth(), this.getHeight());
    }

    @Override
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        g.setColor(this.getForeground());
        g.fillOval(0,0,this.getWidth(), this.getHeight());
    }

    public void actionPerformed(ActionEvent e){
        // When it is clicked do something
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Test: CircularButton");
        JPanel panel = new JPanel();
        CircularButton circularbutton = new CircularButton(new Hex(), null);
        circularbutton.setSize(50);
        circularbutton.setPosition(30, 60);
        panel.setLayout(null);
        panel.add(circularbutton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
