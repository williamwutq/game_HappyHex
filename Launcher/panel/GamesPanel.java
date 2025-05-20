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

package Launcher.panel;

import Launcher.LaunchEssentials;

import javax.swing.*;
import java.awt.*;

public class GamesPanel extends JPanel {
    double size;
    private JScrollPane scrollPane;
    public GamesPanel(){
        super();
        this.setOpaque(true);
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        size = 1;
        for (int i = 0; i < 20; i++) {
            this.add(new ListGame("Item " + (i + 1)));
        }
        scrollPane = new JScrollPane(this);
        scrollPane.setBackground(LaunchEssentials.launchBackgroundColor);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants. HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }
    public JScrollPane getScrollPane(){return scrollPane;}

    public void doLayout(){
        Dimension rawSize = this.getParent().getBounds().getSize();
        size = Math.min(rawSize.width, rawSize.height) / 12.0;
        int size5 = (int) (size*5.0);
        for (Component game : getComponents()){
            if (game instanceof ListGame listGame){
                listGame.setPreferredSize(new Dimension(rawSize.width, size5));
                listGame.setSize(new Dimension(rawSize.width, size5));
            }
        }
        super.doLayout();
    }


    public void paint(Graphics g){
        doLayout();
        super.paintChildren(g);
    }

    private class ListGame extends JPanel {
        public ListGame(String title) {
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            setAlignmentX(CENTER_ALIGNMENT);
            setAlignmentY(CENTER_ALIGNMENT);
            setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
            setLayout(new BorderLayout());
            setOpaque(false);

            JLabel label = new JLabel(title);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(label, BorderLayout.CENTER);

            JButton button = new JButton("Action");
            button.setPreferredSize(new Dimension(80, 30));
            add(button, BorderLayout.EAST);
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int sizeInt = (int) size;
            int sizeQ = (int) (size/4.0);
            int sizeP = (int) (size/8.0);
            g2.setColor(this.getBackground());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillRoundRect(3+sizeP, 3+sizeP, getWidth()-6-sizeQ, getHeight()-6-sizeQ, sizeInt, sizeInt);
            g2.dispose();
            //Draw a circle for now
        }
    }
}
