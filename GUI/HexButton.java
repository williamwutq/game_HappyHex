package GUI;

import Hex.Block;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class HexButton extends JButton implements ActionListener, MouseListener {
    private static final double extended = 1.1;
    private static final int alphaHide = 200;
    private static double size;
    private static int engineRadius;
    private Block block;
    private boolean hover;
    public HexButton(Block block){
        super();
        this.block = block;
        this.hover = false;
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setForeground(block.color());
        this.setBackground(new Color(0,0,0,0));
        this.setBorder(new EmptyBorder(0,0,0,0));
        double width = 4 * size * GameEssentials.sinOf60;
        double height = 2 * size;
        Dimension dimension = new Dimension((int) Math.round(width), (int) Math.round(height));
        this.setSize(dimension);
        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);
        int x = (int) Math.round(size * 2 * block.X());
        int y = (int) Math.round(size * 2 * (block.Y() + engineRadius * 0.75 - 0.75));
        this.setBounds(x, y, (int) Math.round(4 * size * GameEssentials.sinOf60), 2 * (int)size);
        this.addActionListener(this);
        this.addMouseListener(this);
    }
    public void resetSize(){
        Dimension dimension = new Dimension((int) Math.round(4 * size * GameEssentials.sinOf60), 2 * (int)size);
        this.setSize(dimension);
        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);
        if (hover){
            int x = (int) Math.round(size * 2 * block.X() + (1 - extended) * size);
            int y = (int) Math.round(size * 2 * (block.Y() + engineRadius * 0.75 - 0.75) + (1 - extended) * size);
            this.setBounds(x, y, (int) Math.round(extended * 4 * size * GameEssentials.sinOf60), (int) Math.round(extended * 2 * size));
        } else {
            int x = (int) Math.round(size * 2 * block.X());
            int y = (int) Math.round(size * 2 * (block.Y() + engineRadius * 0.75 - 0.75));
            this.setBounds(x, y, (int) Math.round(4 * size * GameEssentials.sinOf60), 2 * (int)size);
        }
    }
    public static double getActiveSize(){
        return size;
    }
    public static void setSize(double size){
        HexButton.size = size;
    }
    public static int getActiveEngineRadius(){
        return engineRadius;
    }
    public static void setEngineRadius(int radius){
        HexButton.engineRadius = radius;
    }
    public void setBlock(Block block){
        this.block = block;
        repaint();
    }
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        resetSize();
        if(hover) {
            Color color = new Color(block.color().getRed(), block.color().getGreen(), block.color().getBlue(), alphaHide);
            GameEssentials.paintHexagon(g, color, (extended-1)/2, (extended-1)/2, size, extended);
        } else {
            GameEssentials.paintHexagon(g, block.color(), size);
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {
        hover = true;
        resetSize();
        Color c = block.color();
    }
    public void mouseExited(MouseEvent e) {
        hover = false;
        resetSize();
    }

    public void actionPerformed(ActionEvent e){
        // When it is clicked do something
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Test: CircularButton");
        JPanel panel = new JPanel();
        int engineRadius = 5;
        HexButton.setEngineRadius(engineRadius);
        HexButton.setSize(60);
        panel.setLayout(null);
        panel.add(new HexButton(new Block()));
        panel.add(new HexButton(Block.block(1,1, Color.GREEN)));
        panel.add(new HexButton(Block.block(1,0, Color.BLUE)));
        panel.add(new HexButton(Block.block(engineRadius-1,0, Color.BLUE)));
        panel.add(new HexButton(Block.block(0,1, Color.RED)));
        panel.add(new HexButton(Block.block(0,engineRadius-1, Color.RED)));
        panel.add(new HexButton(Block.block(engineRadius*2-2,engineRadius*2-2, Color.GREEN)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        Dimension dm = new Dimension((int)((engineRadius * 8 - 4)*GameEssentials.sinOf60*size), (engineRadius * 3 - 1)*(int)size+28);
        frame.setSize(dm);
        frame.setMinimumSize(dm);
        frame.setVisible(true);
    }
}
