package GUI;

import java.awt.*;

public class GameEssentials {
    public static final double sinOf60 = Math.sqrt(3) / 2;
    public static Color generateColor(){
        Color colors[] = new Color[12];
        colors[0] = new Color(0, 0, 240);
        colors[1] = new Color(0, 90, 200);
        colors[2] = new Color(0, 180, 180);
        colors[3] = new Color(0, 180, 100);
        colors[4] = new Color(0, 210, 0);
        colors[5] = new Color(90, 200, 0);
        colors[6] = new Color(180, 180, 0);
        colors[7] = new Color(200, 90, 0);
        colors[8] = new Color(210, 0, 0);
        colors[9] = new Color(200, 0, 120);
        colors[10] = new Color(180, 0, 180);
        colors[11] = new Color(90, 0, 200);
        return colors[(int)(Math.random() * 12)];
    }
    public static void paintHexagon(java.awt.Graphics g, Color color, int size){
        // Create Polygon
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = (int) Math.round(size * Math.sqrt(3) / 2 + size * Math.sin(angle));
            yPoints[i] = (int) Math.round(size + size * Math.cos(angle));
        }
        Polygon hexagon = new Polygon(xPoints, yPoints, 6);
        // Paint
        g.setColor(color);
        g.fillPolygon(hexagon);
    }
    public static void main(String[] args){
        // Debug code
    }
}
