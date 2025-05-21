package viewer.graphics.interactive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

abstract class HexButton extends JButton implements ComponentListener {
    public HexButton (){
        this.setOpaque(false);
        this.setBackground(Color.BLACK);
    }
    public void componentHidden(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {doLayout();repaint();}
    public void componentMoved(ComponentEvent e) {doLayout();repaint();}
    public void componentResized(ComponentEvent e) {doLayout();repaint();}

    public static void main(String[] args){
        viewer.Viewer.test(new HexButton(){

        });
    }
}
