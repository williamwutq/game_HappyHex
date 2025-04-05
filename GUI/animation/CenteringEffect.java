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
            double extended = 1.2;
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * block.X() + (1 - extended) * size);
            int y = (int) Math.round(size * 2 * (block.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75) + (1 - extended) * size);
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), (int) Math.round(extended * 2 * size * GameEssentials.sinOf60), (int) Math.round(extended * 2 * size));
        }
    }
    @Override
    protected void paintFrame(java.awt.Graphics g, double progress){
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double extended = 1.2;
        double fill = extended - (progress*0.3);
        int[] xPoints = new int[14];
        int[] yPoints = new int[14];
        for (int i = 0; i < 7; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = (int) Math.round(size * ((extended - 1) + GameEssentials.sinOf60 + Math.sin(angle) * 0.9));
            yPoints[i] = (int) Math.round(size * ((extended - 1) + 1.0 + Math.cos(angle) * 0.9));
        }
        for (int i = 0; i < 7; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i + 7] = (int) Math.round(size * ((extended - 1) + GameEssentials.sinOf60 + Math.sin(angle) * fill));
            yPoints[i + 7] = (int) Math.round(size * ((extended - 1) + 1.0 + Math.cos(angle) * fill));
        }
        g.setColor(GameEssentials.whitenColor(GameEssentials.dimColor(block.color())));
        g.fillPolygon(xPoints, yPoints, 14);
    }
}
