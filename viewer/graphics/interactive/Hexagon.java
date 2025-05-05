package viewer.graphics.interactive;

import hex.Block;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;

public abstract class Hexagon extends Component{
    public static final double sinOf60 = Math.sqrt(3) / 2;
    private static double size;
    private final int index;
    public Hexagon(int index){
        super();
        this.index = index;
        this.setForeground(null);
        this.setBackground(null);
    }
    protected final int getIndex(){
        return index;
    }
    public static double getActiveSize(){
        return size;
    }
    public static void setSize(double size){
        Hexagon.size = size;
    }
    // Methods to fetch extension in width and height, default is 0
    protected int fetchWidthExtension(){return 0;}
    protected int fetchHeightExtension(){return 0;}
    // Method to fetch raw extension in width and height, default is 0
    protected int fetchRawWidthExtension(){return 0;}
    protected int fetchRawHeightExtension(){return 0;}
    // Abstract way to fetch block
    protected abstract Block fetchBlock();
    // Prevent children
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}
    // Paint: reset size and pain this component
    public final void paint(java.awt.Graphics g) {
        Block block = fetchBlock(); // Fetch block
        if(block == null) {
            Dimension minDimension = new Dimension(1,1);
            this.setSize(minDimension);
            this.setMinimumSize(minDimension);
            this.setMaximumSize(minDimension);
            this.setPreferredSize(minDimension);
            this.setBounds(new Rectangle(minDimension));
        } else {
            int width = (int) Math.round(2 * size * sinOf60);
            int height = (int) Math.round(2 * size);
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * (block.X() + fetchWidthExtension() * 0.5 * sinOf60));
            int y = (int) Math.round(size * 2 * (block.Y() + fetchHeightExtension() * 0.75));
            this.setBounds(x+ fetchRawWidthExtension(), y + fetchRawHeightExtension(), width, height);
            // Paint Basic Polygon
            if (block.getState()){
                g.setColor(Color.LIGHT_GRAY);
            } else {
                g.setColor(Color.GRAY);
            }
            int[] xPoints = new int[6];
            int[] yPoints = new int[6];
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                xPoints[i] = (int) Math.round(size * (sinOf60 + Math.sin(angle) * 0.9));
                yPoints[i] = (int) Math.round(size * (1.0 + Math.cos(angle) * 0.9));
            }
            g.fillPolygon(xPoints, yPoints, 6);
            // Paint Highlight
            xPoints = new int[4];
            yPoints = new int[4];
            xPoints[0] = (int) Math.round(size * sinOf60);
            yPoints[0] = (int) Math.round(size * 1.63);
            xPoints[1] = (int) Math.round(size * sinOf60 * 1.63);
            yPoints[1] = (int) Math.round(size * 1.315);
            xPoints[2] = (int) Math.round(size * sinOf60 * 1.72);
            yPoints[2] = (int) Math.round(size * 1.36);
            xPoints[3] = xPoints[0];
            yPoints[3] = (int) Math.round(size * 1.72);
            g.setColor(Color.BLACK);
            g.fillPolygon(xPoints, yPoints, 4);
        }
    }
    public String toString(){
        return "Hexagon[Index = " + index + ", Block = " + fetchBlock().toString() + ", Size = " + size + "]";
    }
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Hexagon hexagon) {
            return index == hexagon.index && fetchBlock().equals(hexagon.fetchBlock());
        } else return false;
    }
    public int hashCode() {
        Block block = fetchBlock();
        if (block == null){
            return 0;
        } else {
            return block.hashCode() + index * 31;
        }
    }
}
