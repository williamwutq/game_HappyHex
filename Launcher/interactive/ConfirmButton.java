package Launcher.interactive;

import Launcher.IO.Username;

import javax.swing.*;
import java.awt.*;

public class ConfirmButton extends LaunchButton {
    private JTextField textField;
    public ConfirmButton(JTextField launchEnterTextField) {
        super("ENTER");
        this.textField = launchEnterTextField;
    }

    protected void clicked() {
        // Try to get text
        String text = null;
        try{
            text = textField.getText();
        } catch (Exception e) {}
        Username player = Username.getUsername(text);
        if(player == null){
            // Get angry
            textField.setText("INCORRECT NAMING FORMAT!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerErrorFontColor);
            System.err.println("Attempted login failed.");
        } else if (player.isKeyword()){
            // Get angry
            textField.setText("GAME KEYWORD PROHIBITED!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerErrorFontColor);
            System.err.println("Attempted login failed.");
        } else {
            // Validate user input
            Launcher.LaunchEssentials.setCurrentPlayer(player, player.toHash());
            textField.setText("SUCCESSFUL PLAYER LOGIN!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
            System.out.println("Logged in as " + player + ".");
        }
        // Start a timer to move text back to normal
        Timer timer = new Timer(1000, e -> {
            String name = Launcher.LaunchEssentials.getCurrentPlayer();
            if(name.equals("Guest")) {
                textField.setText("ENTER THE USERNAME HERE!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
            } else {
                textField.setText(name);
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerNameFontColor);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchConfirmButtonBackgroundColor;
    }
}
