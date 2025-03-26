package GUI;

import Hex.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

abstract class HexButton extends JButton implements ActionListener, MouseListener {
    private static final double extended = 1.1;
    private static final int alphaHide = 200;
    private static double size;
    private final int index;
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
        this.setForeground(fetchColor());
        this.setBackground(new Color(0,0,0,0));
        this.setBorder(new EmptyBorder(0,0,0,0));
        // Listeners
        this.addActionListener(this);
        this.addMouseListener(this);
    }
    protected final int getIndex(){
        return index;
    }
    protected final boolean isHovering(){
        return hover;
    }
    public final void resetSize(){
        Block block = fetchBlock(); // Fetch block
        if(block == null) {
            Dimension minDimension = new Dimension(1,1);
            this.setSize(minDimension);
            this.setMinimumSize(minDimension);
            this.setMaximumSize(minDimension);
            this.setPreferredSize(minDimension);
            this.setBounds(new Rectangle(minDimension));
        } else {
            int width = (int) Math.round(4 * size * GameEssentials.sinOf60);
            int height = (int) Math.round(2 * size);
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            if (hover) {
                int x = (int) Math.round(size * 2 * (block.X() + fetchWidthExtension() * GameEssentials.sinOf60) + (1 - extended) * size);
                int y = (int) Math.round(size * 2 * (block.Y() + fetchHeightExtension() * 0.75) + (1 - extended) * size);
                this.setBounds(x, y, (int) Math.round(extended * 4 * size * GameEssentials.sinOf60), (int) Math.round(extended * 2 * size));
            } else {
                int x = (int) Math.round(size * 2 * (block.X() + fetchWidthExtension() * GameEssentials.sinOf60));
                int y = (int) Math.round(size * 2 * (block.Y() + fetchHeightExtension() * 0.75));
                this.setBounds(x, y, width, height);
            }
        }
    }
    public static double getActiveSize(){
        return size;
    }
    public static void setSize(double size){
        HexButton.size = size;
    }
    // Methods to fetch extension in width and height, default is 0
    protected int fetchWidthExtension(){return 0;}
    protected int fetchHeightExtension(){return 0;}
    // Abstract things as different ways to fetch color and block, and different reactions
    protected abstract Block fetchBlock();
    protected Color fetchColor(){
        Block block = fetchBlock();
        if(block == null) {
            return new Color(0,0,0,0);
        } else return fetchBlock().color();
    }
    protected void clicked(){}
    protected void hovered(){}
    protected void removed(){}
    // Prevent children
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}
    // Paint: only paint this component
    public final void paint(java.awt.Graphics g) {
        resetSize();
        // Fetch block color
        Color blockColor = fetchColor();
        if (hover) {
            Color color = new Color(blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue(), alphaHide);
            GameEssentials.paintHexagon(g, color, (extended - 1) / 2, (extended - 1) / 2, size, extended);
        } else {
            GameEssentials.paintHexagon(g, blockColor, size);
        }
    }
    // Actions
    public final void mouseClicked(MouseEvent e) {}
    public final void mousePressed(MouseEvent e) {}
    public final void mouseReleased(MouseEvent e) {}
    public final void mouseEntered(MouseEvent e) {
        hover = true;
        resetSize();
        hovered();
    }
    public final void mouseExited(MouseEvent e) {
        hover = false;
        resetSize();
        removed();
    }
    public final void actionPerformed(ActionEvent e){
        clicked();
    }
}
