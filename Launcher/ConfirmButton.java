package Launcher;

import java.awt.*;

public class ConfirmButton extends LaunchButton{
    public ConfirmButton() {
        super("ENTER");
    }

    protected void clicked() {
    }

    protected Color fetchColor() {
        return LaunchEssentials.launchConfirmButtonBackgroundColor;
    }
}
