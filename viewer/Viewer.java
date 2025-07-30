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

package viewer;

import viewer.graphics.frame.KeyboardHelper;
import viewer.graphics.frame.ViewerGUI;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Taskbar;
import java.util.Objects;

/**
 * Main file of Game Viewer, which is used as a tool to view past games by reading and showing game files.
 * <p>
 * Run {@link #main} to launch the game viewer.
 */
public final class Viewer {
    public static void main(String[] args){
        JFrame frame = new JFrame();
        Dimension min = new Dimension(240, 300);
        Dimension start = new Dimension(400, 500);
        Image image = fetchIconImage();
        if (image != null){
            Taskbar.getTaskbar().setIconImage(image);
            frame.setIconImage(fetchIconImage());
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("HappyHex Game Viewer Version 1.0");
        frame.setSize(start);
        frame.setPreferredSize(start);
        frame.setMinimumSize(min);
        frame.setBackground(Color.WHITE);
        ViewerGUI gui = new ViewerGUI();
        frame.add(gui);
        KeyboardHelper.attachListener(frame, gui.getKeyboardListener());
        frame.setVisible(true);
    }
    private static Image fetchIconImage(){
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(Viewer.class.getResource("Viewer.png")));
            return icon.getImage();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
