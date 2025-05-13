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

package Launcher.panel;

import Launcher.interactive.*;

import javax.swing.*;
import java.awt.*;

public class ResumePanel extends UniversalPanel {
    private JPanel buttonsPanel;
    public ResumePanel (){super();}
    protected JComponent[] fetchContent() {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setBackground(this.getBackground());
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(new QuitButton());
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(new StartButton());
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(new LaunchButton(" NEW "){
            @Override
            protected void clicked() {
                Launcher.LauncherGUI.startGame();
            }
            @Override
            protected Color fetchColor() {
                return Launcher.LaunchEssentials.launchConfirmButtonBackgroundColor;
            }
        });
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.setDoubleBuffered(true);
        return new JComponent[]{new GamesPanel(), buttonsPanel};
    }

    protected JComponent[] fetchHeader() {
        return new JComponent[0];
    }

    public void recalculate() {
        super.recalculate();
        double referenceEnterTextSize = Math.min(getReferenceHeight()*2, getReferenceWidth());
        LaunchButton.setSizeConstant(referenceEnterTextSize/144.0);
        Dimension buttonsPanelDimensionConstant = new Dimension((int) getReferenceWidth(), (int) (referenceEnterTextSize * 0.4));
        buttonsPanel.setMinimumSize(buttonsPanelDimensionConstant);
        buttonsPanel.setMaximumSize(buttonsPanelDimensionConstant);
    }
}
