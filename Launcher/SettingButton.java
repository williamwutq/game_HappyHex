package Launcher;

import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingButton extends LaunchButton {
    public SettingButton() {
        super("SETTING");
    }

    protected void clicked() {

    }

    protected Color fetchColor() {
        return LaunchEssentials.launchStartButtonBackgroundColor;
    }
}