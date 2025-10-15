package util.fgui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A utility class for creating and displaying test windows containing GUI components.
 * This class provides static methods to create {@link JFrame} instances that host
 * specified components or components provided by {@link GraphicsProvider} instances.
 * The created windows are configured with default settings suitable for testing purposes,
 * such as size, close operation, and background color.
 * <p>
 * The class is not instantiable and serves solely as a collection of static utility methods.
 * <p>
 * The main methods are {@link #create} and {@link #test}, which accepts multiple overloads
 * to accommodate different use cases, including automatic title generation based on the
 * component's class name, and handling of {@code GraphicsProvider} instances to manage
 * component lifecycle.
 *
 * @see JFrame
 * @see Component
 * @see GraphicsProvider
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class TestWindow {
    private TestWindow() {
        // Prevent instantiation
    }
    /**
     * Creates a JFrame containing the specified component with a title inferred from the component's class name.
     * The window is sized to 600x600 pixels and will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     *
     * @param c the component to be displayed in the frame
     * @return the created JFrame
     * @throws NullPointerException if the component is null
     */
    public static JFrame create(Component c) {
        if (c == null) throw new NullPointerException("Component cannot be null");
        String name = c.getClass().getSimpleName();
        if (name.isEmpty()) name = "Anonymous Component";
        return create(c, name + " Test", new Dimension(600, 600));
    }
    /**
     * Creates a JFrame containing the specified component.
     * The window is sized to 600x600 pixels and will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     *
     * @param c the component to be displayed in the frame
     * @param title the title of the frame
     * @return the created JFrame
     * @throws NullPointerException if the component is null
     */
    public static JFrame create(Component c, String title) {
        if (c == null) throw new NullPointerException("Component cannot be null");
        return create(c, title, new Dimension(600, 600));
    }
    /**
     * Creates a JFrame containing the specified component with the specified size.
     * The window will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     *
     * @param c the component to be displayed in the frame
     * @param title the title of the frame
     * @param size the size of the frame
     * @return the created JFrame
     * @throws NullPointerException if the component is null
     */
    public static JFrame create(Component c, String title, Dimension size) {
        if (c == null) throw new NullPointerException("Component cannot be null");
        if (title == null) title = "Test";
        if (size == null) size = new Dimension(600, 600);
        JFrame f = new JFrame();
        f.setSize(size);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Color solidColor = c.getBackground();
        if (solidColor.getAlpha() != 255) {
            // If the background color is not fully opaque, set it to opaque
            solidColor = new Color(solidColor.getRed(), solidColor.getGreen(), solidColor.getBlue());
        }
        f.setBackground(solidColor);
        f.add(c);
        f.setTitle(title);
        return f;
    }
    /**
     * Creates a JFrame containing the component provided by the specified GraphicsProvider with a title inferred from
     * the provider's class name. The window is sized to 600x600 pixels and will close when the user exits, and the
     * GraphicsProvider's close method will be called to release resources.
     * The background color of the window is set to match the background color of the component.
     *
     * @param provider the GraphicsProvider that provides the component to be displayed in the frame
     * @return the created JFrame
     * @throws NullPointerException if the GraphicsProvider is null or if it returns a null component
     */
    public static JFrame create(GraphicsProvider provider) {
        if (provider == null) throw new NullPointerException("GraphicsProvider cannot be null");
        String name = provider.getClass().getSimpleName();
        if (name.isEmpty()) name = "Anonymous Component Provider";
        return create(provider, name + " Test", new Dimension(600, 600));
    }
    /**
     * Creates a JFrame containing the component provided by the specified GraphicsProvider.
     * The window is sized to 600x600 pixels and will close when the user exits, and the GraphicsProvider's
     * close method will be called to release resources. The background color of the window is set to match
     * the background color of the component.
     *
     * @param provider the GraphicsProvider that provides the component to be displayed in the frame
     * @param title the title of the frame
     * @return the created JFrame
     * @throws NullPointerException if the GraphicsProvider is null or if it returns a null component
     */
    public static JFrame create(GraphicsProvider provider, String title) {
        if (provider == null) throw new NullPointerException("GraphicsProvider cannot be null");
        return create(provider, title, new Dimension(600, 600));
    }
    /**
     * Creates a JFrame containing the component provided by the specified GraphicsProvider with the specified size.
     * The window will close when the user exits, and the GraphicsProvider's close method will be called to release resources.
     * The background color of the window is set to match the background color of the component.
     *
     * @param provider the GraphicsProvider that provides the component to be displayed in the frame
     * @param title the title of the frame
     * @param size the size of the frame
     * @return the created JFrame
     * @throws NullPointerException if the GraphicsProvider is null or if it returns a null component
     */
    public static JFrame create(GraphicsProvider provider, String title, Dimension size) {
        if (provider == null) throw new NullPointerException("GraphicsProvider cannot be null");
        if (title == null) title = "Test";
        if (size == null) size = new Dimension(600, 600);
        provider.start();
        Component c = provider.get();
        if (c == null) throw new NullPointerException("GraphicsProvider returned null component");
        JFrame f = new JFrame();
        f.setSize(size);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Color solidColor = c.getBackground();
        if (solidColor.getAlpha() != 255) {
            // If the background color is not fully opaque, set it to opaque
            solidColor = new Color(solidColor.getRed(), solidColor.getGreen(), solidColor.getBlue());
        }
        f.setBackground(solidColor);
        f.add(c);
        f.setTitle(title);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                provider.close();
                f.dispose();
            }
        });
        return f;
    }
    /**
     * Creates and displays a test window containing the specified component.
     * The window is sized to 600x600 pixels and will close when the user exits.
     * The background color of the window is set to match the background color of the component.
     * The title of the window is inferred from the component's class name.
     *
     * @param c the component to be displayed in the test window
     */
    public static void test(Component c) {
        JFrame f = create(c);
        f.setVisible(true);
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
        JFrame f = create(c, title);
        f.setVisible(true);
    }
    /**
     * Creates and displays a test window containing the component provided by the specified GraphicsProvider
     * with a title inferred from the provider's class name. The window is sized to 600x600 pixels and will close
     * when the user exits, and the GraphicsProvider's close method will be called to release resources.
     * The background color of the window is set to match the background color of the component.
     *
     * @param provider the GraphicsProvider that provides the component to be displayed in the test window
     */
    public static void test(GraphicsProvider provider) {
        JFrame f = create(provider);
        f.setVisible(true);
    }
    /**
     * Creates and displays a test window containing the component provided by the specified GraphicsProvider.
     * The window is sized to 600x600 pixels and will close when the user exits, and the GraphicsProvider's
     * close method will be called to release resources. The background color of the window is set to match
     * the background color of the component.
     *
     * @param provider the GraphicsProvider that provides the component to be displayed in the test window
     * @param title the title of the test window
     */
    public static void test(GraphicsProvider provider, String title) {
        JFrame f = create(provider, title);
        f.setVisible(true);
    }
}
