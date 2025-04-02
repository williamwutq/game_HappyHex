package Launcher.interactive;

import Launcher.LaunchEssentials;

import java.awt.*;

public class LoginButton extends LaunchButton {
    public LoginButton() {
        super("LOG  IN");
    }

    public void clicked() {
        LaunchEssentials.toLogInPage();
    }

    @Override
    protected Color fetchColor() {
        return LaunchEssentials.launchStartButtonBackgroundColor;
    }
}