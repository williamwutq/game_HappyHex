package GUI.animation;

import GUI.GameEssentials;

import javax.swing.*;
import java.awt.*;

public class CenteringEffect extends HexEffect {
    public CenteringEffect(){
        super();
    }
    @Override
    protected void paintFrame(java.awt.Graphics g, double progress){
        g.setColor(this.getForeground());
        g.fillRect(0, 0, (int)(progress * 50), (int)(progress * 50));
    }
    public static void main(String[] args){
        JFrame mainFrame = new JFrame("CenteringEffectTest");
        JPanel mainPanel = new JPanel(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(400, 400);
        mainFrame.setBackground(Color.CYAN);
        mainPanel.setBackground(Color.CYAN);
        HexEffect effect = new CenteringEffect();
        effect.setTotalFrames(50);
        effect.setFrameTime(100);
        effect.setForeground(Color.BLACK);
        effect.setPreferredSize(new Dimension(400, 400));
        effect.setSize(new Dimension(400, 400));
        effect.setBounds(new Rectangle(0, 0, 400, 400));
        System.out.println(effect.getTotalTime());

        mainPanel.add(effect);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        effect.start();
    }
}
