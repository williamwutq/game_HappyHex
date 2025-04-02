package Launcher;

import java.awt.*;
import java.awt.event.ActionEvent;

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