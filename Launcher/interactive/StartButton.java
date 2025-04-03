package Launcher.interactive;

import java.awt.*;

public class StartButton extends LaunchButton {
    public StartButton() {
        super(" START ");
    }

    @Override
    protected void clicked() {
        Launcher.LauncherGUI.startGame();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchStartButtonBackgroundColor;
    }
}
