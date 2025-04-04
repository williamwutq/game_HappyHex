package GUI.animation;

import GUI.GameEssentials;

import java.awt.*;

public class CenteringEffect extends Animation {
    private Hex.Block block;
    public CenteringEffect(Hex.Block block){
        super(2000, 1);
        this.block = block;
        resetSize();
        GameEssentials.window().revalidate();
    }
    public final void resetSize(){
        if(block == null) {
            Dimension minDimension = new Dimension(1,1);
            this.setSize(minDimension);
            this.setMinimumSize(minDimension);
            this.setMaximumSize(minDimension);
            this.setPreferredSize(minDimension);
            this.setBounds(new Rectangle(minDimension));
        } else {
            double size = GUI.HexButton.getActiveSize();
            int width = (int) Math.round(2 * size * GameEssentials.sinOf60);
            int height = (int) Math.round(2 * size);
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * block.X());
            int y = (int) Math.round(size * 2 * (block.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75));
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), width, height);
        }
    }
    @Override
    protected void paintFrame(java.awt.Graphics g, double progress){
        resetSize();
        int x = 0;
        int y = 0;
        double size = GUI.HexButton.getActiveSize();
        double fill = 1.1 - (progress*0.2);
        int[] xPoints = new int[14];
        int[] yPoints = new int[14];
        for (int i = 0; i < 7; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = (int) Math.round(size * (x * 2 + GameEssentials.sinOf60 + Math.sin(angle) * 0.9));
            yPoints[i] = (int) Math.round(size * (y * 2 + 1.0 + Math.cos(angle) * 0.9));
        }
        for (int i = 0; i < 7; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i + 7] = (int) Math.round(size * (x * 2 + GameEssentials.sinOf60 + Math.sin(angle) * fill));
            yPoints[i + 7] = (int) Math.round(size * (y * 2 + 1.0 + Math.cos(angle) * fill));
        }
        g.setColor(GameEssentials.darkenColor(block.color()));
        g.fillPolygon(xPoints, yPoints, 14);
    }
}
