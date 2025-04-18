package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public final class InlineInfoPanel extends JComponent implements ComponentListener {
    private String title;
    private String info;
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JPanel linePanel;
    private int size;

    public InlineInfoPanel(){
        super();
        this.title = "TITLE";
        this.info = "INFO";
        this.size = 1;
        this.titleLabel = new JLabel(title);
        this.infoLabel = new JLabel(info);
        this.linePanel = new JPanel(null);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // GameEssentials.gameBackGroundColor
        this.setBackground(GameEssentials.gameBackgroundColor);
        this.addComponentListener(this);
        Dimension lineSize = new Dimension(1,1);
        linePanel.setBackground(GameEssentials.gameDisplayFontColor);
        linePanel.setPreferredSize(lineSize);
        linePanel.setMinimumSize(lineSize);
        linePanel.setMaximumSize(lineSize);
        titleLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        titleLabel.setForeground(GameEssentials.gameDisplayFontColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.PLAIN, 1));
        infoLabel.setForeground(GameEssentials.gameDisplayFontColor);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);
        infoLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        // Additions
        this.add(Box.createGlue());
        this.add(titleLabel);
        this.add(linePanel);
        this.add(infoLabel);
        this.add(Box.createGlue());
    }
    public void setTitle(String newTitle){
        this.title = newTitle;
        titleLabel.setText(title);
        this.repaint();
    }
    public void setInfo(String newInfo){
        this.info = newInfo;
        infoLabel.setText(info);
        this.repaint();
    }
    public void recalculate(){
        titleLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, size));
        infoLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.PLAIN, size));
        Dimension lineSize = new Dimension(this.getWidth() - titleLabel.getWidth() - infoLabel.getWidth(),size/8);
        linePanel.setPreferredSize(lineSize);
        linePanel.setMinimumSize(lineSize);
        linePanel.setMaximumSize(lineSize);
    }
    public void setSize(int newSize){
        size = newSize;
        recalculate();
    }
    public final void componentResized(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentMoved(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentShown(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public final void componentHidden(ComponentEvent e) {}
}
