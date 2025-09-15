package util.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for creating and displaying a test window containing a specified component.
 */
public class TestWindow {
    /**
     * Creates and displays a test window containing the specified component.
     * The window is sized to 600x600 pixels and will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     * The title of the window is inferred from the component's class name.
     *
     * @param c the component to be displayed in the test window
     */
    public static void test(Component c) {
        test(c, c.getClass().getSimpleName() + " Test");
    }
    /**
     * Creates and displays a test window containing the specified component.
     * The window is sized to 600x600 pixels and will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     *
     * @param c the component to be displayed in the test window
     * @param title the title of the test window
     */
    public static void test(Component c, String title) {
        JFrame f = new JFrame();
        f.setSize(600, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBackground(c.getBackground());
        f.add(c);
        f.setTitle(title);
        f.setVisible(true);
    }
    /**
     * Creates a JFrame containing the specified component.
     * The window is sized to 600x600 pixels and will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     *
     * @param c the component to be displayed in the frame
     * @param title the title of the frame
     * @return the created JFrame
     */
    public static JFrame createFrame(Component c, String title) {
        JFrame f = new JFrame();
        f.setSize(600, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBackground(c.getBackground());
        f.add(c);
        f.setTitle(title);
        return f;
    }
}
