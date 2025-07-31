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

package viewer.GameViewer.app.Contents.app;

import viewer.graphics.frame.KeyboardHelper;
import viewer.graphics.frame.ViewerGUI;
import viewer.graphics.frame.DropInFileAdaptor;
import viewer.logic.Controller;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.HeadlessException;
import java.awt.dnd.DropTarget;
import java.util.Objects;

/**
 * Main file of Game Viewer, which is used as a tool to view past games by reading and showing game files.
 * <p>
 * Run {@link #main} to launch the game viewer.
 *
 * @since 1.0 (HappyHex 1.3)
 * @version 1.1 (HappyHex 1.4)
 * @author William Wu
 */
public final class Viewer {
    private static Controller controller;
    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class is not meant to be instantiated, as it only contains static methods.
     */
    private Viewer() {
        // Prevent instantiation
    }
    /**
     * The main method to launch the HappyHex Game Viewer.
     * It initializes the controller, sets up the main frame, and adds the GUI component.
     * It also fetches the icon image for the application and parses command line arguments.
     *
     * @param args command line arguments, which can specify a file to open
     */
    public static void main(String[] args){
        controller = new Controller();
        JFrame frame = new JFrame();
        Dimension min = new Dimension(240, 300);
        Dimension start = new Dimension(400, 500);
        Image image = fetchIconImage();
        if (image != null){
            Taskbar.getTaskbar().setIconImage(image);
            frame.setIconImage(fetchIconImage());
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("HappyHex Game Viewer Version 1.1");
        frame.setSize(start);
        frame.setPreferredSize(start);
        frame.setMinimumSize(min);
        frame.setBackground(Color.WHITE);
        ViewerGUI gui = new ViewerGUI(controller);
        parseArgs(args);
        initDropTarget(frame);
        KeyboardHelper.attachListener(frame, gui.getKeyboardListener());
        frame.add(gui);
        frame.setVisible(true);
    }
    /**
     * Fetches the icon image for the viewer from the resources.
     * If the image is not found, returns null.
     *
     * @return the icon image, or null if not found
     */
    private static Image fetchIconImage(){
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(Viewer.class.getResource("Viewer.png")));
            return icon.getImage();
        } catch (NullPointerException e) {
            return null;
        }
    }
    /**
     * Parses command line arguments to determine the file to be opened.
     * If a valid file name is provided, it notifies the controller to open that file.
     *
     * @param args the command line arguments
     * @since 1.1 (HappyHex 1.4)
     */
    private static void parseArgs(String[] args) {
        if (args == null || args.length == 0) return;
        String filename;
        if (args.length == 1) {
            filename = args[0].trim();
        } else if (args.length == 2 && (
            args[0].equals("-file") || args[0].equals("-f") || args[0].equals("-o"))) {
            filename = args[1].trim();
        } else {
            return;
        }
        if (filename.endsWith(".hpyhex")) {
            filename = filename.substring(0, filename.length() - 7);
        }
        controller.onFileChosen(filename);
    }
    /**
     * Initializes the drop target for the main frame to allow file dropping.
     * If the environment does not support file dropping, it catches the exception and ignores it.
     *
     * @param frame the main frame of the viewer
     */
    private static void initDropTarget(JFrame frame){
        try {
            DropTarget dropTarget = new DropTarget(frame, new DropInFileAdaptor(controller));
            frame.setDropTarget(dropTarget);
        } catch (HeadlessException ignored) {
            // If the environment does not support file dropping, GameViewers disable this feature.
        }
    }
}
