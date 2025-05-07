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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Keyboard extends Component {
    private final static String font = "Courier";
    private double size;
    private ActionListener listener;
    private Key[][] keys;
    public Keyboard(ActionListener listener){
        this.listener = listener;
        resetSize();
        // Construct keys
        keys = new Key[5][5];
        keys[0][0] = new Key(" 0 ");
        keys[0][1] = new Key(" 1 ");
        keys[0][2] = new Key(" 2 ");
        keys[0][3] = new Key(" 3 ");
        keys[0][4] = new Key(" 4 ");
        keys[1][0] = new Key(" 5 ");
        keys[1][1] = new Key(" 6 ");
        keys[1][2] = new Key(" 7 ");
        keys[1][3] = new Key(" 8 ");
        keys[1][4] = new Key(" 9 ");
        keys[2][0] = new Key(" A ");
        keys[2][1] = new Key(" B ");
        keys[2][2] = new Key(" C ");
        keys[2][3] = new Key(" - ");
        keys[2][4] = new Key(" + ");
        keys[3][0] = new Key(" D ");
        keys[3][1] = new Key(" E ");
        keys[3][2] = new Key(" F ");
        keys[3][3] = new Key("DEL");
        keys[3][4] = new Key("CLR");
        keys[4][0] = new Key("HOM");
        keys[4][1] = new Key(" < ");
        keys[4][2] = new Key(" > ");
        keys[4][3] = new Key("CNF");
        keys[4][4] = new Key("ENT");
    }
    /**
     * Reset the size of individual keys in the panel by to match the maximum size allowed in this panel.
     */
    public boolean resetSize(){
        // Calculate size
        double newSize = (Math.min((getHeight()-6) * 0.2, (getWidth()-6) * 0.2));
        if (Math.abs(newSize - size) < 1) return false;
        size = newSize;
        return true;
    }
    public void paint(Graphics g){
        if (resetSize()){
            for (Key[] keyArr : keys) {
                for (Key key : keyArr) {
                    key.resetSize();
                }
            }
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(187, 187, 187));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) size, (int) size);
        g2.dispose();
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < keys[i].length; j++) {
                paintKey(g, keys[i][j], j, i);
            }
        }
    }
    private class Key extends Button implements ActionListener {
        private String key;
        private Key(String key){
            this.key = key;
            setFont(new Font(font, Font.BOLD, (int) (size/2)));
            setSize((int) size, (int) size);
            setForeground(Color.DARK_GRAY);
            setLabel(key);
            addActionListener(this);
        }
        public void resetSize(){
            setFont(new Font(font, Font.BOLD, (int) (size/2)));
            setSize((int) size, (int) size);
        }
        public void actionPerformed(ActionEvent e) {
            listener.actionPerformed(new ActionEvent(e, e.getID(), "press " + key));
        }
    }
    private void paintKey(Graphics g, Key key, int row, int col){
        Graphics2D g2 = (Graphics2D) g.create((int)(size * row)+3, (int)(size * col)+3, (int)size, (int)size);
        g2.setColor(Color.WHITE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect((int)(size * 0.1), (int)(size * 0.1), (int)(size * 0.8), (int)(size * 0.8), (int)(size * 0.8), (int)(size * 0.8));
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font(font, Font.BOLD, (int) (size * 0.4)));
        g2.drawString(key.key, (int) (size*0.14), (int) (size * 0.64));
        g2.dispose();
    }
    public static void main(String[] args){
        viewer.Viewer.test(new Keyboard(e -> {
            String str = e.getActionCommand();
            System.out.println(str.substring(str.length() - 3));
        }));
    }
}
