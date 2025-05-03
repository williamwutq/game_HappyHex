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

import io.Username;
import Launcher.LaunchEssentials;
import game.Queue;

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
            System.err.println(io.GameTime.generateSimpleTime() + " Login: Attempted login failed.");
        } else if ((player.equals("Guest") || player.equals("guest") || player.equals("GUEST")
                || player.equals("Logout") || player.equals("logout") || player.equals("LOGOUT")
                || player.equals("log out") || player.equals("Log out") || player.equals("LOG OUT")
                || player.equals("Out") || player.equals("out") || player.equals("OUT")) &&
                !Launcher.LaunchEssentials.getCurrentPlayer().equals("Guest")){
            Launcher.LaunchEssentials.setCurrentPlayer(new Username("Guest"), -1);
            textField.setText("SUCCESSFULLY LOGGED OUT!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
            System.out.println(io.GameTime.generateSimpleTime() + " Login: Logged out.");
        } else if (player.isKeyword()){
            failLogin();
        } else if (player.equals("Normal") || player.equals("NORMAL")){
            if(Queue.getPieceProcessorID() != 0){
                textField.setText("USING NORMAL DIFFICULTY!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
                Queue.changePieceProcessor(special.FeatureFactory.createFeature());
                System.out.println(io.GameTime.generateSimpleTime() + " SpecialFeature: Game difficulty switched to normal.");
            } else {
                failLogin();
            }
        } else if (player.equals("Hard") || player.equals("Evil") || player.equals("HARD") || player.equals("EVIL")){
            if(Queue.getPieceProcessorID() != 2){
                textField.setText("RELEASING THE HARD MODE!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerSpecialFontColor);
                Queue.changePieceProcessor(special.FeatureFactory.createFeature("hex.Piece", "Hard"));
                System.out.println(io.GameTime.generateSimpleTime() + " Special Feature: Game difficulty switched to hard.\n" +
                        "                                         This is only enabled if in settings, easyMode is turned OFF.\n" +
                        "                                         Type \"Normal\" into this field to switch back to normal.");
            } else {
                failLogin();
            }
        } else if (player.equals("Devil") || player.equals("DEVIL")){
            if(Queue.getPieceProcessorID() != 2){
                textField.setText("PLACE UNBREAKABLE CURSE!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerSpecialFontColor);
                Queue.changePieceProcessor(special.FeatureFactory.createFeature("hex.Piece", "Hard"));
                System.out.println(io.GameTime.generateSimpleTime() + " Special Feature: Game difficulty switched to hard.\n" +
                        "                                         This is only enabled if in settings, easyMode is turned OFF.\n" +
                        "                                         Type \"Normal\" into this field to switch back to normal.");
            } else {
                failLogin();
            }
        } else if (player.equals("God") || player.equals("GOD")){
            if(Queue.getPieceProcessorID() != 5 && LaunchEssentials.isEasyMode()){
                textField.setText("THE DIVINE INTERVENTION!");
                textField.setForeground(Launcher.LaunchEssentials.launchPlayerSpecialFontColor);
                Queue.changePieceProcessor(special.FeatureFactory.createFeature("hex.Piece", "God"));
                System.out.println(io.GameTime.generateSimpleTime() + " Special Feature: You have unlocked God Mode.\n" +
                        "                                         This means the game will try to ensure that, your game will not end.\n" +
                        "                                         Type \"Normal\" into this field to switch back to normal.");
            } else {
                failLogin();
            }
        } else if (!player.equals(Launcher.LaunchEssentials.getCurrentPlayer())){
            // Validate user input
            Launcher.LaunchEssentials.setCurrentPlayer(player, player.toHash());
            textField.setText("SUCCESSFUL PLAYER LOGIN!");
            textField.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
            System.out.println(io.GameTime.generateSimpleTime() + " Login: Logged in as " + player + ".");
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
        System.err.println(io.GameTime.generateSimpleTime() + " Login: Attempted login failed.");
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchConfirmButtonBackgroundColor;
    }
}
