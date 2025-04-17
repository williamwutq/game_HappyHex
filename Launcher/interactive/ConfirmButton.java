package Launcher.interactive;

import Launcher.IO.Username;
import Launcher.LaunchEssentials;

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
            System.err.println(Launcher.IO.GameTime.generateSimpleTime() + " Login: Attempted login failed.");
        } else if (player.isKeyword()){
            failLogin();
        } else if (player.equals("Normal")){
            if(Hex.Queue.getPieceProcessorID() != 0){
                textField.setText("USING NORMAL DIFFICULTY!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
                Hex.Queue.changePieceProcessor(special.FeatureFactory.createFeature());
                System.out.println(Launcher.IO.GameTime.generateSimpleTime() + " SpecialFeature: Game difficulty switched to normal.");
            } else {
                failLogin();
            }
        } else if (player.equals("Hard") || player.equals("Evil")){
            if(Hex.Queue.getPieceProcessorID() != 2){
                textField.setText("RELEASING THE HARD MODE!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerSpecialFontColor);
                Hex.Queue.changePieceProcessor(special.FeatureFactory.createFeature("Hex.Piece", "Hard"));
                System.out.println(Launcher.IO.GameTime.generateSimpleTime() + " Special Feature: Game difficulty switched to hard.\n" +
                        "                                         This is only enabled if in settings, easyMode is turned OFF.\n" +
                        "                                         Type \"Normal\" into this field to switch back to normal.");
            } else {
                failLogin();
            }
        } else if (player.equals("God")){
            if(Hex.Queue.getPieceProcessorID() != 5 && LaunchEssentials.isEasyMode()){
                textField.setText("THE DIVINE INTERVENTION!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerSpecialFontColor);
                Hex.Queue.changePieceProcessor(special.FeatureFactory.createFeature("Hex.Piece", "God"));
                System.out.println(Launcher.IO.GameTime.generateSimpleTime() + " Special Feature: You have unlocked God Mode.\n" +
                        "                                         This means the game will try to ensure that, your game will not end.\n" +
                        "                                         Type \"Normal\" into this field to switch back to normal.");
            } else {
                failLogin();
            }
        } else {
            // Validate user input
            Launcher.LaunchEssentials.setCurrentPlayer(player, player.toHash());
            textField.setText("SUCCESSFUL PLAYER LOGIN!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
            System.out.println(Launcher.IO.GameTime.generateSimpleTime() + " Login: Logged in as " + player + ".");
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
    private void failLogin(){
        textField.setText("GAME KEYWORD PROHIBITED!");
        textField.setForeground(Launcher.LaunchEssentials.launchPlayerErrorFontColor);
        System.err.println(Launcher.IO.GameTime.generateSimpleTime() + " Login: Attempted login failed.");
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchConfirmButtonBackgroundColor;
    }
}
