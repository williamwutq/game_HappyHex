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
        size = Math.min(this.getBounds().width, this.getBounds().height) / 12.0;
        for (Component game : getComponents()){
            if (game instanceof ListGame listGame){
                listGame.setPreferredSize(new Dimension(this.getBounds().width, (int) size));
                listGame.setSize(new Dimension(this.getBounds().width, (int) size));
            }
        }
        super.doLayout();
    }


    public void paint(Graphics g){
        super.paintChildren(g);
    }

    private class ListGame extends JPanel {
        public ListGame(String title) {
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            setAlignmentX(CENTER_ALIGNMENT);
            setAlignmentY(CENTER_ALIGNMENT);
            setBackground(Color.GREEN);
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
            super.paintComponent(g);
            //Draw a circle for now
            g.setColor(Color.BLUE);
            g.fillOval(10, 50, 20, 20);
        }
    }
}
