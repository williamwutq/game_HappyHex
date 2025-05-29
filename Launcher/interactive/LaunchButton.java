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

package Launcher.interactive;

import Launcher.LaunchEssentials;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

public abstract class LaunchButton extends JButton implements ActionListener, Launcher.Recolorable {
    private static double sizeConstant = 1;
    private static Color backGroundColor;
    public LaunchButton(String text){
        super();
        this.setText(text);
        this.setForeground(backGroundColor);
        this.setBackground(fetchColor());
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        int size2 = (int) Math.round(sizeConstant * 2);
        int size3 = (int) Math.round(sizeConstant * 3);
        this.setBorder(new EmptyBorder(size2, size3, size2, size3));

        this.setLayout(null);
        this.setBounds(0,0,this.getWidth(),this.getHeight());
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setFont(new Font(LaunchEssentials.launchButtonFont, Font.BOLD, (int) Math.round(sizeConstant * 5)));
        this.addActionListener(this);
    }
    public final void paint(Graphics g) {
        int size = (int) Math.round(sizeConstant);
        int size2 = (int) Math.round(sizeConstant * 2);
        int size3 = (int) Math.round(sizeConstant * 3);
        int size6 = (int) Math.round(sizeConstant * 6);
        this.setBorder(new EmptyBorder(size2, size3, size2, size3));
        this.setFont(new Font(LaunchEssentials.launchButtonFont, Font.BOLD, (int) Math.round(sizeConstant * 5)));
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(size, size, getWidth()-size2, getHeight()-size2, size6, size6);
        g2.dispose();
        super.paintComponent(g);
    }
    public static void setSizeConstant(double size){
        sizeConstant = size;
    }
    public static void setBackGroundColor(Color color){
        backGroundColor = color;
    }
    public final void actionPerformed(ActionEvent e) {
        clicked();
    }
    public final void resetColor(){
        this.setForeground(backGroundColor);
        this.setBackground(fetchColor());
    }
    public final String toString(){
        return "LaunchButton[Class = " + getClass().getName() + ", Text = " + this.getText() + ", Color = " + this.getBackground() + "]";
    }
    // Prevent children
    public final java.awt.Component add(java.awt.Component comp) {return comp;}
    protected final void addImpl(java.awt.Component comp, Object constraints, int index) {}
    public final void addContainerListener(java.awt.event.ContainerListener l) {}
    // Abstract methods
    abstract protected void clicked();
    abstract protected Color fetchColor();
}
