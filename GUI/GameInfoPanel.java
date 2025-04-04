package GUI;

import javax.swing.*;
import java.awt.*;

public class GameInfoPanel extends JPanel{
    private String title;
    private String info;
    private JLabel titleLabel;
    private JLabel infoLabel;
    public GameInfoPanel(){
        super();
        this.title = "TITLE";
        this.info = "INFO";
        this.titleLabel = new JLabel(title);
        this.infoLabel = new JLabel(info);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(0,0,0,0));
        titleLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 40));
        titleLabel.setForeground(GameEssentials.gameDisplayFontColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.PLAIN, 40));
        infoLabel.setForeground(GameEssentials.gameDisplayFontColor);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Additions
        this.add(Box.createVerticalGlue());
        this.add(titleLabel);
        this.add(infoLabel);
        this.add(Box.createVerticalGlue());
    }
    public void setTitle(String newTitle){
        this.title = title;
        this.repaint();
    }
    public void setInfo(String newInfo){
        this.info = info;
        this.repaint();
    }
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setBackground(Color.CYAN);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setTitle("GameInfoPanel Test");
        frame.setSize(400, 400);
        frame.add(new GameInfoPanel());
        frame.setVisible(true);
    }
}
