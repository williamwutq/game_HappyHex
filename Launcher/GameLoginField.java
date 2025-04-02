package Launcher;

import Launcher.IO.Username;

import java.awt.*;
import java.awt.event.*;

public class GameLoginField extends TextField implements ActionListener {
    public GameLoginField(){
        super(Username.MAX_LENGTH);
    }

    public void actionPerformed(ActionEvent e) {

    }
}
