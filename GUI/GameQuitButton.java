package GUI;

public final class GameQuitButton extends SimpleButton {
    public GameQuitButton(){
        super("QUIT", GameEssentials.gameQuitFontColor);
    }
    public void clicked() {
        // Custom code to execute when the frame is closing
        if(Launcher.LaunchEssentials.isGameStarted()) {
            // Log if it has score and reset
            if (GameEssentials.getTurn() != 0) {
                GameEssentials.logGame();
            }
            GameEssentials.resetGame();
        }
        Launcher.LauncherGUI.returnHome();
    }
}
