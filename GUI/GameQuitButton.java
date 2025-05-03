/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package GUI;

public final class GameQuitButton extends SimpleButton {
    public GameQuitButton(){
        super("QUIT", GameEssentials.gameQuitFontColor);
    }
    protected void clicked() {
        // Custom code to execute when the frame is closing
        if(Launcher.LaunchEssentials.isGameStarted()) {
            // Log if it has score and reset
            if (GameEssentials.getTurn() != 0) {
                System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game ends by force quitting.");
                GameEssentials.logGame();
            } else {
                System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game quits without change.");
            }
            GameEssentials.resetGame();
        }
        Launcher.LauncherGUI.returnHome();
    }
}
