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

package Launcher.interactive;

import GUI.ColorAnimator;
import GUI.GameEssentials;
import Launcher.LaunchEssentials;

import java.awt.*;

public class StartButton extends LaunchButton {
    private static ColorAnimator dynamicColor;
    public StartButton() {
        super(" START ");
        if (dynamicColor != null) dynamicColor.stop();
        dynamicColor = new ColorAnimator(new Color[]{Launcher.LaunchEssentials.launchStartButtonBackgroundColor,
                    GameEssentials.interpolate(Launcher.LaunchEssentials.launchStartButtonBackgroundColor,
                    LaunchEssentials.launchBackgroundColor, 1)}, 1000, this::repaint);
        dynamicColor.start();
    }

    @Override
    protected void clicked() {
        Launcher.LauncherGUI.startGame("");
    }

    protected Color fetchColor() {
        if (dynamicColor == null) {
            return Launcher.LaunchEssentials.launchStartButtonBackgroundColor;
        } else return dynamicColor.get();
    }
    public Color getBackground(){
        return fetchColor();
    }
}
