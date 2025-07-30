package viewer.graphics.interactive;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/**
 * A lightweight Swing component that simulates a seven-segment digital display.
 * <p>
 * The {@code SevenSegment} class provides a simple graphical representation of
 * seven-segment digits and characters, similar to those found in digital clocks,
 * calculators, and LED displays. It supports digits (0–9), uppercase and lowercase
 * letters (A–Z, a–z), and a subset of punctuation characters including '-', '.', ':',
 * and others. The component is fully scalable and customizable through its
 * {@code setCharSize} method.
 *
 * <h2>Usage</h2>
 * The following example describe setting up a seven segment display with character 'A'.
 * <pre>{@code
 * SevenSegment display = new SevenSegment();
 * display.setCharacter('A');  // Show character 'A'
 * display.setCharSize(50);    // Set display size
 * }</pre>
 *
 * <h2>Supported Characters</h2>
 * <ul>
 *   <li>Digits: 0–9</li>
 *   <li>Alphabet: A–Z (case insensitive)</li>
 *   <li>Special characters: '-', '.', ',', ':', ';', '=', '<', '>', '^', '*', '/', '\', '|', '[', ']', '(', ')', '{', '}'</li>
 * </ul>
 * Unsupported characters will be rendered as blank (no lit segments).
 *
 * <h2>Segment Mapping</h2>
 * The display is composed of 7 segments (labeled a–g) in the following layout:
 * <pre>
 *  -a-
 * b   c
 *  -d-
 * e   f
 *  -d-
 * </pre>
 * Each segment is mapped to a polygon based on predefined coordinates that are scaled
 * relative to the component's current size.
 * <p>
 * The segments are centered around the top corner, with no padding around the display.
 *
 * @version 1.1 (HappyHex 1.4)
 * @author William Wu
 * @since 1.0 (HappyHex 1.3)
 * @see JComponent
 */
public final class SevenSegment extends JComponent {
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
            {true, true, false, true, true, false, false},
            {true, true, false, false, true, true, true},
            {false, true, false, true, true, true, false},
            {false, false, true, false, false, true, false},
            {false, false, true, false, false, true, true},
            {false, true, true, true, true, true, false},
            {false, true, false, false, true, false, true},
            {true, false, false, false, true, true, false},
            {false, false, false, true, true, true, false},
            {false, false, false, true, true, true, true},
            {true, true, true, true, true, false, false},
            {true, true, true, true, false, true, false},
            {false, false, false, true, true, false, false},
            {true, true, false, true, false, true, true},
            {false, true, false, true, true, false, true},
            {false, true, true, false, true, true, true},
            {false, false, false, false, true, true, true},
            {false, true, true, false, false, false, true},
            {false, true, false, true, false, true, false},
            {false, true, true, true, false, true, true},
            {true, false, true, true, true, false, true},
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
    private Path2D.Double cachedPath;
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
        // Recalculate path
        cachedPath = new Path2D.Double(Path2D.WIND_NON_ZERO);
        for (int k = 0; k < 7; k ++){
            if (states[k]){
                cachedPath.moveTo(size * refPosX[k][0], size * refPosY[k][0]);
                for (int i = 1; i < 6; i++){
                    cachedPath.lineTo(size * refPosX[k][i], size * refPosY[k][i]);
                }
                cachedPath.closePath();
            }
        }
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
        if (size > 0 && size != this.size) {
            this.size = size;
            // Recalculate path
            cachedPath = new Path2D.Double(Path2D.WIND_NON_ZERO);
            for (int k = 0; k < 7; k ++){
                if (states[k]){
                    cachedPath.moveTo(size * refPosX[k][0], size * refPosY[k][0]);
                    for (int i = 1; i < 6; i++){
                        cachedPath.lineTo(size * refPosX[k][i], size * refPosY[k][i]);
                    }
                    cachedPath.closePath();
                }
            }
        }
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
     * Checks if the given character is valid for display.
     *
     * @since 1.1 (HappyHex 1.4)
     * @param c character to check.
     * @return true if the character is valid, false otherwise.
     */
    public static boolean isValidCharacter(char c){
        return ('0' <= c && c <= '9') || ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') ||
                c == ' ' || "-+~,._;:=^*'\"`<>/|\\[](){}".indexOf(c) != -1;
    }
    /**
     * Returns an array of special characters supported by this seven-segment display.
     *
     * @since 1.1 (HappyHex 1.4)
     * @return an array of supported characters.
     */
    public static char[] getSupportedSpecialCharacters() {
        return new char[]{
                ' ', '+', '-', '~', '.', ',', ':', ';', '=', '<', '>', '^',
                '*', '\'', '`', '"', '/', '|', '\\',
                '[', ']', '{', '}', '(', ')'
        };
    }
    /**
     * Maps a character to its segment states.
     *
     * @param c character to map.
     * @return boolean array representing which segments to light.
     */
    private static boolean[] getStates(char c){
        if (c == '-' || c == '~' || c == '+'){
            boolean[] result = new boolean[7];
            result[3] = true;
            return result;
        } else if (c == ',' || c == '.' || c == '_' || c == 7){
            boolean[] result = new boolean[7];
            result[6] = true;
            return result;
        } else if (c == ';' || c == ':' || c == '='){
            boolean[] result = new boolean[7];
            result[3] = true;
            result[6] = true;
            return result;
        } else if (c == '^'){
            return new boolean[]{true, true, true, false, false, false, false};
        } else if (c == '*'){
            return new boolean[]{true, true, false, false, false, false, false};
        } else if (c == '\'' || c == '`'){
            return new boolean[]{false, true, false, false, false, false, false};
        } else if (c == '\"'){
            return new boolean[]{false, true, true, false, false, false, false};
        } else if (c == '<'){
            return new boolean[]{true, true, false, true, false, false, false};
        } else if (c == '>'){
            return new boolean[]{true, false, true, true, false, false, false};
        } else if (c == '/' || c == '|' || c == '\\'){
            return new boolean[]{false, true, false, false, true, false, false};
        } else if (c == '[' || c == '(' || c == '{'){
            return new boolean[]{true, true, false, false, true, false, true};
        } else if (c == ']' || c == ')' || c == '}'){
            return new boolean[]{true, false, true, false, false, true, true};
        } else if ('0' <= c && c <= '9'){
            return data[c - '0'];
        } else if ('A' <= c && c <= 'Z') {
            return data[c - 55];
        } else if ('a' <= c && c <= 'z') {
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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(Color.WHITE);
        try {
            g2.fill(cachedPath);
            g2.draw(cachedPath); // Fix for too thin lines
        } catch (Exception e) {}
    }
}
