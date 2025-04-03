package Launcher.interactive;

import java.awt.*;

public class SettingButton extends LaunchButton {
    public SettingButton() {
        super("SETTING");
    }

    protected void clicked() {
        Launcher.LauncherGUI.toSettings();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchStartButtonBackgroundColor;
    }
}