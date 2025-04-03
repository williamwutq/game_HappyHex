package Launcher.interactive;

import java.awt.*;

public class LoginButton extends LaunchButton {
    public LoginButton() {
        super("LOG  IN");
    }

    public void clicked() {
        Launcher.LauncherGUI.toLogInPage();
    }

    @Override
    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchStartButtonBackgroundColor;
    }
}