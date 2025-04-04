package GUI.animation;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CenteringEffect extends Animation {
    private int x;
    public CenteringEffect(int totalFrames, int frameTime, int x){
        super(totalFrames, frameTime);
        this.x = x;
    }
    public CenteringEffect(int totalFrames, int frameTime, int x, ActionListener listener){
        super(totalFrames, frameTime, listener);
        this.x = x;
    }
    @Override
    protected void paintFrame(java.awt.Graphics g, double progress){
        g.setColor(Color.BLACK);
        g.fillRect(x, (int)(progress * 150), 50, 50);
    }
    public static void main(String[] args){
        JFrame mainFrame = new JFrame("CenteringEffectTest");
        JPanel mainPanel = new JPanel(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 400);
        mainFrame.setBackground(Color.CYAN);
        mainPanel.setBackground(Color.CYAN);
        Animation effect = new CenteringEffect(1000, 1, 0);
        Animation effect2 = new CenteringEffect(1000, 1, 50);
        effect.setPreferredSize(new Dimension(400, 400));
        effect2.setPreferredSize(new Dimension(400, 400));
        effect.setSize(new Dimension(400, 400));
        effect2.setSize(new Dimension(400, 400));
        effect.setBounds(new Rectangle(0, 0, 400, 400));
        effect2.setBounds(new Rectangle(0, 0, 400, 400));
        effect.setEndListener(effect2.getStartListener());

        mainPanel.add(effect);
        mainPanel.add(effect2);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
        effect.start();
    }
}
