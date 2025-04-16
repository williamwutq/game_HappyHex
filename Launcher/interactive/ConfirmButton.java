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
        } else if (player.equals("Normal")){
            if(Hex.Queue.getPieceProcessorID() != 0){
                textField.setText("USING NORMAL DIFFICULTY!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
                Hex.Queue.changePieceProcessor(special.FeatureFactory.createFeature());
                System.out.println("Game difficulty switched to normal.");
            } else {
                // Get angry
                textField.setText("GAME KEYWORD PROHIBITED!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerErrorFontColor);
                System.err.println("Attempted login failed.");
            }
        } else if (player.equals("Hard") || player.equals("Evil")){
            if(Hex.Queue.getPieceProcessorID() != 2){
                textField.setText("RELEASING THE HARD MODE!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
                Hex.Queue.changePieceProcessor(special.FeatureFactory.createFeature("Hex.Piece"));
                System.out.println("Game difficulty switched to hard.\nThis is only enabled if in settings," +
                        " easyMode is turned OFF.\nType \"Normal\" into this field to switch back to normal.");
            } else {
                // Get angry
                textField.setText("GAME KEYWORD PROHIBITED!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerErrorFontColor);
                System.err.println("Attempted login failed.");
            }
        } else {
            // Validate user input
            Launcher.LaunchEssentials.setCurrentPlayer(player, player.toHash());
            textField.setText("SUCCESSFUL PLAYER LOGIN!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
            System.out.println("Logged in as " + player + ".");
        }
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchConfirmButtonBackgroundColor;
    }
}
