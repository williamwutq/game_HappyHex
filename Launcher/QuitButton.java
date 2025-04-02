package Launcher;

import java.awt.*;

public class QuitButton extends LaunchButton{
    public QuitButton() {
        super(" QUIT ");
    }

    @Override
    protected void clicked() {
        // LaunchEssentials.returnHome();
    }

    protected Color fetchColor() {
        return LaunchEssentials.launchQuitButtonBackgroundColor;
    }
}
