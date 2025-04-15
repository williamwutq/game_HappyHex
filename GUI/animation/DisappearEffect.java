package GUI.animation;

import GUI.GameEssentials;

import java.awt.*;

public class DisappearEffect extends Animation {
    private Hex.Hex hex;
    private Color color;
    public DisappearEffect(Hex.Block block){
        super(2000, 1);
        this.hex = block.thisHex();
        this.color = new Color(block.color().getRed(), block.color().getGreen(), block.color().getBlue());
        resetSize();
    }
    public final void resetSize(){
        if(hex == null) {
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
            double extended = 1.9;
            Dimension dimension = new Dimension(width, height);
            this.setSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);
            this.setPreferredSize(dimension);
            int x = (int) Math.round(size * 2 * hex.X() + (1 - extended) * size);
            int y = (int) Math.round(size * 2 * (hex.Y() + (GameEssentials.engine().getRadius() - 1) * 0.75) + (1 - extended) * size);
            this.setBounds(x + GameEssentials.getGamePanelWidthExtension(), y + GameEssentials.getGamePanelHeightExtension(), (int) Math.round(extended * 2 * size * GameEssentials.sinOf60), (int) Math.round(extended * 2 * size));
        }
    }
    @Override
    protected void paintFrame(Graphics g, double progress) {
        resetSize();
        double size = GUI.HexButton.getActiveSize();
        double extended = 1.9;
        double ratio = 0.13;
        progress = 1 - progress;
        int[] xPoints = new int[40];
        int[] yPoints = new int[40];
        for (int i = 0; i < 40; i++) {
            double angle = Math.toRadians(9 * i);
            double radius = size * progress * (8 + Math.sin(20 * (angle - progress)) - Math.sin(20 * (angle + progress)));
            xPoints[i] = (int) Math.round(GameEssentials.sinOf60 * size + Math.cos(angle) * radius * ratio + size * (extended - 1));
            yPoints[i] = (int) Math.round(size + Math.sin(angle) * radius * ratio + size * (extended - 1));
        }
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(progress*255)));
        g.fillPolygon(xPoints, yPoints, 40);
    }
}
