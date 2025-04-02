package Launcher.interactive;

import Launcher.LaunchEssentials;

import java.awt.*;

public class SettingButton extends LaunchButton {
    public SettingButton() {
        super("SETTING");
    }

    protected void clicked() {
        LaunchEssentials.toSettings();
    }

    protected Color fetchColor() {
        return LaunchEssentials.launchStartButtonBackgroundColor;
    }
}