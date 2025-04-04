package GUI.animation;


import javax.swing.*;
import java.awt.*;

public class CenteringEffect extends Animation {
    public CenteringEffect(int totalFrames, int frameTime){
        super(totalFrames, frameTime);
    }
    @Override
    protected void paintFrame(java.awt.Graphics g, double progress){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)(progress * 50), (int)(progress * 50));
    }
    public static void main(String[] args){
        JFrame mainFrame = new JFrame("CenteringEffectTest");
        JPanel mainPanel = new JPanel(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(400, 400);
        mainFrame.setBackground(Color.CYAN);
        mainPanel.setBackground(Color.CYAN);
        Animation effect = new CenteringEffect(5000, 1);
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
