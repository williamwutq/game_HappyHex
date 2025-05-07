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

package viewer.graphics.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Keyboard extends JComponent {
    private static final String keyFont = "Courier";
    private static final Color keyColor = new Color(85, 85, 85);
    private double size;
    private ActionListener listener;
    private Key[][] keys;
    public Keyboard(ActionListener listener){
        this.listener = listener;
        this.setBackground(keyColor);
        this.setLayout(null);
        this.setDoubleBuffered(true);
        // Key initialization
        String[][] labels = {
                {" 0 ", " 1 ", " 2 ", " 3 ", " 4 "},
                {" 5 ", " 6 ", " 7 ", " 8 ", " 9 "},
                {" A ", " B ", " C ", " - ", " + "},
                {" D ", " E ", " F ", "DEL", "CLR"},
                {"HOM", " < ", " > ", "CNF", "ENT"}
        };
        keys = new Key[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Key key = new Key(labels[i][j]);
                keys[i][j] = key;
                add(key);
            }
        }
    }

    /**
     * Recalculates key layout and size when the component is resized.
     */
    public void doLayout() {
        int halfHeight = getHeight()/2;
        int halfWidth = getWidth()/2;
        size = Math.min((getHeight() - 6) * 0.19, (getWidth() - 6) * 0.19);
        int sizeInt = (int) size;
        int size04 = (int)(size * 0.4);

        for (int i = 0; i < keys.length; i++) {
            int y = (int) ((i - 2.5) * size) + halfHeight;
            for (int j = 0; j < keys[i].length; j++) {
                int x = (int) ((j - 2.5) * size) + halfWidth;
                keys[i][j].setBounds(x, y, sizeInt, sizeInt);
                keys[i][j].setFont(new Font(keyFont, Font.BOLD, size04));
            }
        }
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        int sizeInt = (int) size;
        g2.setColor(this.getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, sizeInt, sizeInt);
        g2.dispose();
        paintChildren(g);
    }
    private class Key extends JButton implements ActionListener {
        private String key;
        private Key(String key){
            this.key = key;
            setText(key);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            addActionListener(this);
        }
        public void resetSize(){
            setFont(new Font(keyFont, Font.BOLD, (int) (size/2)));
            setSize((int) size, (int) size);
        }
        public void paint(Graphics g){
            int sizeInt = (int) size;
            int size8 = (int)(0.8 * size);
            int size1 = (int)(0.1 * size);

            // Draw key background
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(size1, size1, size8, size8, size8, size8);

            // Draw label
            g2.setColor(keyColor);
            g2.setFont(this.getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(key, (sizeInt - fm.stringWidth(key)) / 2, (sizeInt - size1 + fm.getAscent()) / 2);
            g2.dispose();
        }
        public void actionPerformed(ActionEvent e) {
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(this, e.getID(), "press " + key));
            }
        }
    }
    public static void main(String[] args){
        viewer.Viewer.test(new Keyboard(e -> {
            String str = e.getActionCommand();
            System.out.println(str.substring(str.length() - 3));
        }));
    }
}
