package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends UniversalPanel {
    public LoginPanel(){
        super();
    }
    protected JComponent[] fetchContent() {
        return new JComponent[]{(JComponent) Box.createVerticalGlue()};
    }
    protected JComponent[] fetchHeader() {
        return null;
    }
}
