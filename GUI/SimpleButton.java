package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public abstract class SimpleButton extends JButton implements ActionListener{
    private static int size = 1;
    public SimpleButton(String text, Color color){
        super(text);
        this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, size));
        this.setBorder(new EmptyBorder(0,0,0,0));
        this.setForeground(color);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        this.clicked();
    }
    protected abstract void clicked();
    public void resetSize(){
        this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, size));
    }
    public static void setSize(int size){
        SimpleButton.size = size;
    }
    public static int getActiveSize(){
        return size;
    }
}
