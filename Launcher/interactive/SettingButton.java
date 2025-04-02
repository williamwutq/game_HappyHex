package Launcher.interactive;

import Launcher.LaunchEssentials;

import java.awt.*;

public class SettingButton extends LaunchButton {
    public SettingButton() {
        super("SETTING");
    }

    protected void clicked() {
        LaunchEssentials.toLSettings();
    }

    protected Color fetchColor() {
        return LaunchEssentials.launchStartButtonBackgroundColor;
    }
}