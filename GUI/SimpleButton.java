package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

abstract class SimpleButton extends JButton implements ActionListener, ComponentListener {
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
        this.addComponentListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        this.clicked();
    }
    abstract void clicked();
    private void resetSize(){
        this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, size));
    }
    public static void setSize(int size){
        SimpleButton.size = size;
    }
    public static int getActiveSize(){
        return size;
    }
    public final void componentResized(ComponentEvent e) {
        this.resetSize();
        this.repaint();
    }
    public final void componentMoved(ComponentEvent e) {
        this.resetSize();
        this.repaint();
    }
    public final void componentShown(ComponentEvent e) {
        this.resetSize();
        this.repaint();
    }
    public final void componentHidden(ComponentEvent e) {}
}
