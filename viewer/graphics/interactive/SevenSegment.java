package viewer.graphics.interactive;

import javax.swing.*;
import java.awt.*;

final class SevenSegment extends JComponent {
    private static final boolean[][] data = {
            {true, true, true, false, true, true, true},
            {false, false, true, false, false, true, false},
            {true, false, true, true, true, false, true},
            {true, false, true, true, false, true, true},
            {false, true, true, true, false, true, false},
            {true, true, false, true, false, true, true},
            {true, true, false, true, true, true, true},
            {true, false, true, false, false, true, false},
            {true, true, true, true, true, true, true},
            {true, true, true, true, false, true, true},
            {true, true, true, true, true, true, false},
            {false, true, false, true, true, true, true},
            {true, true, false, false, true, false, true},
            {false, false, true, true, true, true, true},
            {true, true, false, true, true, false, true},
            {true, true, false, true, true, false, false}
    };
    private static final double[][] refPosX = {
            {0.5, 0.25, 0.5, 2, 2.25, 2},
            {0, 0.25, 0.5, 0.5, 0.25, 0},
            {2, 2.25, 2.5, 2.5, 2.25, 2},
            {0.5, 0.25, 0.5, 2, 2.25, 2},
            {0, 0.25, 0.5, 0.5, 0.25, 0},
            {2, 2.25, 2.5, 2.5, 2.25, 2},
            {0.5, 0.25, 0.5, 2, 2.25, 2}
    };
    private static final double[][] refPosY = {
            {0, 0.25, 0.5, 0.5, 0.25, 0},
            {0.5, 0.25, 0.5, 2, 2.25, 2},
            {0.5, 0.25, 0.5, 2, 2.25, 2},
            {2, 2.25, 2.5, 2.5, 2.25, 2},
            {2.5, 2.25, 2.5, 4, 4.25, 4},
            {2.5, 2.25, 2.5, 4, 4.25, 4},
            {4, 4.25, 4.5, 4.5, 4.25, 4}
    };
    private char character;
    private boolean[] states;
    private double size;
    /** Creates an empty seven-segment display component. */
    public SevenSegment(){
        this.character = ' ';
        this.size = 1;
        states = new boolean[7];
    }
    /**
     * Sets the displayed character and updates the segment states.
     *
     * @param character character to display (0-9, A-F, a-f, or '-').
     */
    public void setCharacter(char character){
        this.character = character;
        states = getStates(character);
    }
    /**
     * Return the current displayed character.
     *
     * @return the character current on display.
     */
    public char getCharacter(){
        return this.character;
    }
    /**
     * Set the relative size of this {@code SevenSegment} display.
     *
     * @param size the new size of the {@code SevenSegment} display to be used.
     */
    public void setCharSize(double size){
        if (size > 0) this.size = size;
    }
    /**
     * Get the relative size of this {@code SevenSegment} display.
     *
     * @return the relative size of this display current in use.
     */
    public double getCharSize(){
        return size;
    }
    /**
     * Maps a character to its segment states.
     *
     * @param c character to map.
     * @return boolean array representing which segments to light.
     */
    private static boolean[] getStates(char c){
        if (c == '-'){
            boolean[] result = new boolean[7];
            result[3] = true;
            return result;
        } else if ('0' <= c && c <= '9'){
            return data[c - '0'];
        } else if ('A' <= c && c <= 'F') {
            return data[c - 55];
        } else if ('a' <= c && c <= 'f') {
            return data[c - 87];
        } else return new boolean[7];
    }
    /**
     * Paints the current character using white-filled polygon segments.
     *
     * @param g the graphics context.
     */
    public void paint(Graphics g){
        // Draw key background
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        int[] positionX = new int[6];
        int[] positionY = new int[6];
        for (int k = 0; k < 7; k ++){
            if (states[k]){
                for (int i = 0; i < 6; i++){
                    positionX[i] = (int) (size * refPosX[k][i]);
                    positionY[i] = (int) (size * refPosY[k][i]);
                }
                g2.fillPolygon(positionX, positionY, 6);
            }
        }
    }
}
