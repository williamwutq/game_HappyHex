package Launcher.interactive;

import java.awt.*;

public class QuitButton extends LaunchButton {
    public QuitButton() {
        super(" QUIT ");
    }

    @Override
    protected void clicked() {
        Launcher.LaunchEssentials.returnHome();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchQuitButtonBackgroundColor;
    }
}
