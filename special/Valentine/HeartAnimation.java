package special.Valentine;

import GUI.GameEssentials;
import GUI.animation.Animation;

import java.awt.*;

public class HeartAnimation extends Animation {
    private Hex.Hex hex;
    private Color color;
    public HeartAnimation(Hex.Block block){
        super(2000, 1);
        this.hex = block.thisHex();
        this.color = new Color(255, 102, 153);
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
            double extended = 3;
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
        double extended = 3;
        progress = 1 - progress;
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(progress*255)));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double radius = size * 2;

        // Formula: x^2+y^2 <= k*x^{4/9}*y*(ln(r))^2+r^2
        int coordinateX = (int) Math.round(size * (extended + GameEssentials.sinOf60 - 1));
        int coordinateY = 0;// (int) Math.round(size * extended);
        int limitX = (int) Math.round(radius * 1.6);
        int limitY = (int) Math.round(radius * 2.025);
        for (int x = 0; x <= limitX; x++) {
            for (int y = -limitY; y <= limitY; y++) {
                double leftSide = x * x + y * y;
                double rightSide = Math.pow(x, 4.0 / 9) * y * Math.pow(Math.log(radius), 2) + radius * radius;
                if (leftSide <= rightSide) {
                    // Use symmetry over the y-axis
                    graphics.fillRect(centerX + x + coordinateX, centerY - y + coordinateX, 1, 1);
                    graphics.fillRect(centerX - x + coordinateY, centerY - y + coordinateY, 1, 1);
                }
            }
        }
    }
}
