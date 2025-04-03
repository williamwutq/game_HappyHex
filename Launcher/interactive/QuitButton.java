package Launcher.interactive;

import java.awt.*;

public class QuitButton extends LaunchButton {
    public QuitButton() {
        super(" QUIT ");
    }

    @Override
    protected void clicked() {
        // Log if there exist game and score
        if(Launcher.LaunchEssentials.isGameStarted() && GUI.GameEssentials.turn != 0){
            GUI.GameEssentials.logGame();
        }
        Launcher.LauncherGUI.returnHome();
    }

    protected Color fetchColor() {
        return Launcher.LaunchEssentials.launchQuitButtonBackgroundColor;
    }
}
