package Launcher.interactive;

import java.awt.*;

public class ThemeButton extends LaunchButton {
    public ThemeButton() {
        super("THEMES");
    }

    protected void clicked() {
        Launcher.LauncherGUI.toThemes();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchStartButtonBackgroundColor;
    }
}
