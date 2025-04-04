package GUI.animation;


import GUI.GameEssentials;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class CenteringEffect extends Animation {
    public CenteringEffect(int totalFrames, int frameTime){
        super(totalFrames, frameTime);
    }
    @Override
    protected void paintFrame(java.awt.Graphics g, double progress){
        int x = 0;
        int y = 0;
        int size = 40;
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
        g.setColor(Color.RED);
        g.fillPolygon(xPoints, yPoints, 14);
    }
    public static void main(String[] args){
        JFrame mainFrame = new JFrame("CenteringEffectTest");
        JPanel mainPanel = new JPanel(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 400);
        mainFrame.setBackground(Color.CYAN);
        mainPanel.setBackground(Color.CYAN);
        Animation effect = new CenteringEffect(300, 1);
        effect.setPreferredSize(new Dimension(400, 400));
        effect.setSize(new Dimension(400, 400));
        effect.setBounds(new Rectangle(0, 0, 400, 400));

        mainPanel.add(effect);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        effect.start();
    }
}
