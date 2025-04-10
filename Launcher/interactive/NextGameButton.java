package Launcher.interactive;

public class NextGameButton extends GUI.SimpleButton {
    public NextGameButton(){
        super("NEW GAME", Launcher.LaunchEssentials.launchNewButtonBackgroundColor);
    }
    protected void clicked() {
        Launcher.LauncherGUI.startGame();
    }
}
