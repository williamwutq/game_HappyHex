package GUI.animation;

import GUI.GameEssentials;

import java.awt.*;

public class DisappearEffect extends Animation {
    private Hex.Block block;
    public DisappearEffect(Hex.Block block){
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
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), (int) Math.round(2 * size * GameEssentials.sinOf60), (int) Math.round(2 * size));
        }
    }
    @Override
    void paintFrame(Graphics graphics, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        int[] xPoints = new int[40];
        int[] yPoints = new int[40];
        for (int i = 0; i < 20; i++) {
            double angle = Math.toRadians(18 * i);
            double radius = size * progress * (8 + Math.sin(20 * (angle - progress)) - Math.sin(20 * (angle + progress)));
            xPoints[i] = (int) Math.round(Math.cos(angle) * radius);
            yPoints[i] = (int) Math.round(Math.sin(angle) * radius);
        }
    }
}
