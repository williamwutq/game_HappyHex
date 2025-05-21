package viewer.graphics.interactive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

abstract class HexButton extends JButton implements ActionListener {
    public HexButton (){
        this.setOpaque(false);
        this.setBackground(Color.BLACK);
    }
    // Prevent children
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}

    public static void main(String[] args){
        viewer.Viewer.test(new HexButton(){
            public void actionPerformed(ActionEvent e) {}
        });
    }
}
