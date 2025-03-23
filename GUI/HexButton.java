package GUI;

import Hex.Block;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class HexButton extends JButton implements ActionListener {
    private Block block;
    private static double size;
    private static int engineRadius;
    public HexButton(Block block){
        super();
        this.block = block;
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setForeground(Color.WHITE);
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
        int y = (int) Math.round(size * 2 * (block.Y() + engineRadius * 0.75));
        this.setBounds(x, y, (int) Math.round(4 * size * GameEssentials.sinOf60), 2 * (int)size);
        this.addActionListener(this);
    }
    public void resetSize(){
        Dimension dimension = new Dimension((int) Math.round(4 * size * GameEssentials.sinOf60), 2 * (int)size);
        this.setSize(dimension);
        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);
        int x = (int) Math.round(size * 2 * block.X());
        int y = (int) Math.round(size * 2 * (block.Y() + engineRadius * 0.75));
        this.setBounds(x, y, (int) Math.round(4 * size * GameEssentials.sinOf60), 2 * (int)size);
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

    @Override
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        GameEssentials.paintHexagon(g, block.color(), size);
    }

    public void actionPerformed(ActionEvent e){
        // When it is clicked do something
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Test: CircularButton");
        JPanel panel = new JPanel();
        int engineRadius = 3;
        HexButton.setEngineRadius(3);
        HexButton.setSize(40);
        panel.setLayout(null);
        panel.add(new HexButton(new Block()));
        panel.add(new HexButton(Block.block(1,1, Color.GREEN)));
        panel.add(new HexButton(Block.block(1,0, Color.BLUE)));
        panel.add(new HexButton(Block.block(engineRadius,0, Color.BLUE)));
        panel.add(new HexButton(Block.block(0,1, Color.RED)));
        panel.add(new HexButton(Block.block(0,engineRadius, Color.RED)));
        panel.add(new HexButton(Block.block(engineRadius*2-1,engineRadius*2-1, Color.GREEN)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        Dimension dm = new Dimension((int)((engineRadius * 8)*GameEssentials.sinOf60*size), (engineRadius * 3 + 2)*(int)size+28);
        frame.setSize(dm);
        frame.setMinimumSize(dm);
        frame.setVisible(true);
    }
}
