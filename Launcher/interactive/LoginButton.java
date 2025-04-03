package Launcher.interactive;

import java.awt.*;

public class LoginButton extends LaunchButton {
    public LoginButton() {
        super("LOG  IN");
    }

    public void clicked() {
        Launcher.LaunchEssentials.toLogInPage();
    }

    @Override
    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchStartButtonBackgroundColor;
    }
}