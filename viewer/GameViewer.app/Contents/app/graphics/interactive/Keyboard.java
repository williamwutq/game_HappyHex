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

package viewer.GameViewer.app.Contents.app.graphics.interactive;

import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A custom Swing component representing a 5x5 on-screen hexadecimal keyboard
 * designed for hexadecimal and command inputs.
 * <p>
 * Each key is rendered as a circular button with a white fill, bold Courier font,
 * and gray text color. The buttons are laid out symmetrically and scale based on
 * the component's current size.
 *
 * <h2>Layout and Appearance</h2>
 * <ul>
 *     <li>5 rows × 5 columns of keys.</li>
 *     <li>Each button is circular with diameter {@code 4 × gap}, where
 *         {@code gap = 0.1 × key diameter}.</li>
 *     <li>Key font: {@code Courier}, bold, size 40% of key diameter.</li>
 *     <li>Key background: white.</li>
 *     <li>Key text color: dark gray ({@code RGB(85,85,85)}).</li>
 * </ul>
 *
 * This component is fully resizable and recalculates key sizes and positions on layout changes.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see JComponent
 * @see JButton
 */
public final class Keyboard extends JComponent {
    private static final String keyFont = "Courier";
    private static final Color keyColor = new Color(85, 85, 85);
    private double size;
    private final ActionListener listener;
    private final Key[][] keys;
    /**
     * Constructs a new {@code Keyboard} component with an action listener
     * that will receive {@link ActionEvent}s when any key is pressed.
     *
     * @param listener the action listener to notify on key press
     * @see ActionListener
     */
    public Keyboard(ActionListener listener){
        this.listener = listener;
        this.setBackground(keyColor);
        this.setLayout(null);
        this.setDoubleBuffered(true);
        // Key initialization
        String[][] labels = {
                {"0", "1", "2", "3", "4"},
                {"5", "6", "7", "8", "9"},
                {"A", "B", "C", "-", "+"},
                {"D", "E", "F", "DEL", "CLR"},
              {"STT", "<", ">", "END", "ENT"}
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
     * Lays out the keys based on the current component size.
     * The button size is dynamically computed to fit the component.
     */
    public void doLayout() {
        int halfHeight = getHeight()/2;
        int halfWidth = getWidth()/2;
        size = Math.min((getHeight() - 6) * 0.19, (getWidth() - 6) * 0.19);
        int sizeInt = (int) size;
        Font sizedFont = new Font(keyFont, Font.BOLD, (int)(size * 0.4));

        for (int i = 0; i < keys.length; i++) {
            int y = (int) ((i - 2.5) * size) + halfHeight;
            for (int j = 0; j < keys[i].length; j++) {
                int x = (int) ((j - 2.5) * size) + halfWidth;
                keys[i][j].setBounds(x, y, sizeInt, sizeInt);
                keys[i][j].setFont(sizedFont);
            }
        }
    }
    /**
     * Paints the keyboard background and round-cornered border with antialiasing.
     * @param g the {@code Graphics} context to use for painting
     */
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        int sizeInt = (int) size;
        g2.setColor(this.getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, sizeInt, sizeInt);
        g2.dispose();
        paintChildren(g);
    }
    /**
     * A circular key button within the keyboard.
     * Handles its own rendering and dispatches action events to the keyboard listener.
     */
    private final class Key extends JButton implements ActionListener {
        /** Label and command for this key. */
        private final String key;
        /**
         * Constructs a key with a specific label.
         *
         * @param key label and command string for the key.
         */
        private Key(String key){
            this.key = key;
            setText(key);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            addActionListener(this);
        }
        /**
         * Custom rendering of the circular key. It draws a white filled circle and dark label text in the center.
         * @param g the {@code Graphics} context to use for painting
         */
        public void paint(Graphics g){
            int sizeInt = (int) size;
            int size8 = (int)(0.8 * size);
            int size1 = (int)(0.1 * size);

            // Draw key background
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillOval(size1, size1, size8, size8);

            // Draw label
            g2.setColor(keyColor);
            g2.setFont(this.getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(key, (sizeInt - fm.stringWidth(key)) / 2, (sizeInt - size1 + fm.getAscent()) / 2);
            g2.dispose();
        }
        /**
         * Overrides hit-test logic to match circular button shape.
         *
         * @param x x-coordinate of the mouse relative to the key
         * @param y y-coordinate of the mouse relative to the key
         * @return true if the point is inside the circular area
         */
        public boolean contains(int x, int y) {
            double radius = size * 0.4;
            double half = size * 0.5;
            double dx = x - half, dy = y - half;
            return dx * dx + dy * dy <= radius * radius;
        }
        /**
         * Dispatches a formatted action event to the external listener when the key is pressed.
         *
         * @param e the original ActionEvent triggered by the button
         */
        public void actionPerformed(ActionEvent e) {
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(this, e.getID(), "press " + key));
            }
        }
    }
}
