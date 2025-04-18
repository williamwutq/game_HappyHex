package GUI.animation;

import GUI.GameEssentials;

import java.awt.*;

/**
 * An animation that draws a hexagon outline centered on a specified {@code Hex.Block},
 * decreasing in opaqueness and size over time.
 * <p>
 * This class extends {@link Animation} to create a visual effect where a hexagon outline,
 * initially larger than the standard hexagon size, gradually shrinks and fades into the background.
 * The hexagon's position and color are derived from a {@link Hex.Block} object, with its center
 * defined by the block's X and Y coordinates. The animation uses a fixed duration of 2000 frames
 * with a 1ms delay between frames, creating a smooth transition effect suitable for highlighting
 * a specific block in the game.
 */
public class CenteringEffect extends Animation {
    private Hex.Block block;
    /**
     * Constructs a {@code CenteringEffect} animation for a given {@link Hex.Block}.
     * This animation last for 2 seconds, with 1 millisecond per frame.
     * @param block the {@code Hex.Block} defining the hexagon's center position and color.
     */
    public CenteringEffect(Hex.Block block){
        super(2000, 1);
        this.block = block;
        resetSize();
        GameEssentials.window().revalidate();
    }
    /**
     * Resets the size and position of the animation component based on the block's properties.
     * <p>
     * This method calculates the dimensions and bounds of the hexagon outline using the active
     * hexagon size from {@link GUI.HexButton#getActiveSize()} and the block's coordinates. The hexagon
     * is scaled to 1.2 times the standard size and positioned with an offset to ensure proper
     * alignment within the game panel, accounting for extensions from
     * {@link GameEssentials#getGamePanelWidthExtension()} and
     * {@link GameEssentials#getGamePanelHeightExtension()}.</p>
     */
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
    /**
     * Renders a single frame of the animation, drawing a hexagon outline that shrinks and fades.
     * <p>
     * This method draws a 12-point polygon representing inner and outer borders of a hexagon,
     * with the outer border shrinking from 1.2 times the standard size to 0.9 times based on
     * the animation's progress. The color is interpolated between the block's color (dimmed)
     * and the game's background color, creating a fading effect. The hexagon is centered on
     * the block's coordinates, adjusted for the game panel's offsets.
     * <p>
     * This method does not use the {@link GameEssentials#paintHexagon} method, but implement drawing
     * with a similar logic.
     *
     * @param g the {@code Graphics} context to draw on.
     * @param progress a value between 0 and 1 indicating the animation's progress.
     * @see #resetSize()
     */
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
        g.setColor(GameEssentials.processColor(GameEssentials.whitenColor(GameEssentials.dimColor(block.color()))));
        g.fillPolygon(xPoints, yPoints, 14);
    }
}
