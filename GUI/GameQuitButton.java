package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public final class GameQuitButton extends JButton implements ActionListener, ComponentListener {
    public GameQuitButton(){
        super("QUIT");
        this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        this.setBorder(new EmptyBorder(0,0,0,0));
        this.setForeground(GameEssentials.gameQuitFontColor);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.addActionListener(this);
        this.addComponentListener(this);
    }

    public void actionPerformed(ActionEvent e) {

    }
    public void recalculate(){
        int size = (int)Math.round(Math.min(this.getHeight()*0.4, this.getWidth()*0.2));
        this.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, size));
    }
    public final void componentResized(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentMoved(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentShown(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentHidden(ComponentEvent e) {}
}
