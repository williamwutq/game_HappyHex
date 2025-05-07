/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public final class GameInfoPanel extends JComponent implements ComponentListener {
    private String title;
    private String info;
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JPanel linePanel;
    public GameInfoPanel(){
        super();
        this.title = "TITLE";
        this.info = "INFO";
        this.titleLabel = new JLabel(title);
        this.infoLabel = new JLabel(info);
        this.linePanel = new JPanel(null);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GameEssentials.gameBackgroundColor);
        this.addComponentListener(this);
        Dimension lineSize = new Dimension(1,2);
        linePanel.setBackground(GameEssentials.gameDisplayFontColor);
        linePanel.setPreferredSize(lineSize);
        linePanel.setMinimumSize(lineSize);
        linePanel.setMaximumSize(lineSize);
        titleLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, 1));
        titleLabel.setForeground(GameEssentials.gameDisplayFontColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.PLAIN, 1));
        infoLabel.setForeground(GameEssentials.gameDisplayFontColor);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Additions
        this.add(Box.createVerticalGlue());
        this.add(titleLabel);
        this.add(linePanel);
        this.add(infoLabel);
        this.add(Box.createVerticalGlue());
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
        int size = (int)Math.round(Math.min(this.getHeight()*0.4, this.getWidth()*0.2));
        Dimension lineSize = new Dimension(this.getWidth(),size/8);
        linePanel.setPreferredSize(lineSize);
        linePanel.setMinimumSize(lineSize);
        linePanel.setMaximumSize(lineSize);
        titleLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.BOLD, size));
        infoLabel.setFont(new Font(GameEssentials.gameDisplayFont, Font.PLAIN, size));
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