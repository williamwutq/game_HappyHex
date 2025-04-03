package Launcher.interactive;

import java.awt.*;

public class QuitButton extends LaunchButton {
    public QuitButton() {
        super(" QUIT ");
    }

    @Override
    protected void clicked() {
        Launcher.LauncherGUI.returnHome();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchQuitButtonBackgroundColor;
    }
}
