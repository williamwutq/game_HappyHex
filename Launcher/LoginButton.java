package Launcher;

import java.awt.*;

public class LoginButton extends LaunchButton{
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