package Launcher.interactive;

import Launcher.LaunchEssentials;

import java.awt.*;

public class StartButton extends LaunchButton {
    public StartButton() {
        super(" START ");
    }

    @Override
    protected void clicked() {
        LaunchEssentials.startGame();
    }

    protected Color fetchColor() {
        return LaunchEssentials.launchStartButtonBackgroundColor;
    }
}
