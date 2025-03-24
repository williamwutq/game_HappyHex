package GUI;

import Hex.Block;
import Hex.HexEngine;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class HexButton extends JButton implements ActionListener, MouseListener {
    private static final double extended = 1.1;
    private static final int alphaHide = 200;
    private static double size;
    private static HexEngine engine;
    private int index;
    private boolean hover;
    public HexButton(int index){
        super();
        // Store values
        this.index = index;
        this.hover = false;
        // Basic settings
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setLayout(null);
        this.setForeground(engine.getBlock(index).color());
        this.setBackground(new Color(0,0,0,0));
        this.setBorder(new EmptyBorder(0,0,0,0));
        // Size
        resetSize();
        // Listeners
        this.addActionListener(this);
        this.addMouseListener(this);
    }
    public void resetSize(){
        int width = (int) Math.round(4 * size * GameEssentials.sinOf60);
        int height = (int) Math.round(2 * size);
        Dimension dimension = new Dimension(width, height);
        Block block = engine.getBlock(index); // Fetch block
        this.setSize(dimension);
        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);
        if (hover){
            int x = (int) Math.round(size * 2 * block.X() + (1 - extended) * size);
            int y = (int) Math.round(size * 2 * (block.Y() + engine.getRadius() * 0.75 - 0.75) + (1 - extended) * size);
            this.setBounds(x, y, (int) Math.round(extended * 4 * size * GameEssentials.sinOf60), (int) Math.round(extended * 2 * size));
        } else {
            int x = (int) Math.round(size * 2 * block.X());
            int y = (int) Math.round(size * 2 * (block.Y() + engine.getRadius() * 0.75 - 0.75));
            this.setBounds(x, y, width, height);
        }
    }
    public static double getActiveSize(){
        return size;
    }
    public static void setSize(double size){
        HexButton.size = size;
    }
    public static void setEngine(HexEngine engine){
        HexButton.engine = engine;
    }
    // Prevent children
    public java.awt.Component add(java.awt.Component comp) {return comp;}
    protected void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public void addContainerListener(java.awt.event.ContainerListener l) {}
    // Paint: only paint this component
    public void paint(java.awt.Graphics g) {
        resetSize();
        // Fetch block color
        Color blockColor = engine.getBlock(index).color();
        if(hover) {
            Color color = new Color(blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue(), alphaHide);
            GameEssentials.paintHexagon(g, color, (extended-1)/2, (extended-1)/2, size, extended);
        } else {
            GameEssentials.paintHexagon(g, blockColor, size);
        }
    }
    // Actions
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {
        hover = true;
        resetSize();
    }
    public void mouseExited(MouseEvent e) {
        hover = false;
        resetSize();
    }
    public void actionPerformed(ActionEvent e){
        // When it is clicked do something
    }
}
