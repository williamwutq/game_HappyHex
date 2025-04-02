package Launcher;

import javax.swing.*;
import java.awt.*;

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
