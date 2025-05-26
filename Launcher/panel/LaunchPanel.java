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

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LaunchPanel extends UniversalPanel {
    private JLabel launchVersionLabel;
    private JLabel launchHintLabel;

    public LaunchPanel(){
        super();
    }
    protected JComponent[] fetchContent() {
        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
        JComponent[] components = {fetchLaunchVersionLabel(), fetchLaunchHintLabel(), (JComponent) Box.createVerticalGlue(), new LoginButton(), new SettingButton(), new ThemeButton(), new ResumeButton(), new StartButton(), (JComponent) Box.createVerticalGlue()};
        return components;
    }
    protected JComponent[] fetchHeader() {
        return new JComponent[]{(JComponent) Box.createVerticalGlue()};
    }

    private JLabel fetchLaunchVersionLabel(){
        launchVersionLabel = new JLabel(" Version " + LaunchEssentials.currentGameVersion + " ");
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, 40));
        launchVersionLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchVersionLabel.setBorder(new EmptyBorder(12, 0, 6, 0));
        return launchVersionLabel;
    }
    private JLabel fetchLaunchHintLabel(){
        launchHintLabel = new JLabel(Launcher.LauncherGUI.getGameHint());
        launchHintLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 20));
        launchHintLabel.setForeground(LaunchEssentials.launchHintFontColor);
        launchHintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchHintLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchHintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return launchHintLabel;
    }

    public void recalculate(){
        super.recalculate();
        double referenceStartButtonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        LaunchButton.setSizeConstant(referenceStartButtonSize * 0.01);
        double referenceGameNameSize = Math.min(getReferenceHeight()*1.8, getReferenceWidth());
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, (int)Math.round(referenceGameNameSize/30.0)));
        launchVersionLabel.setBorder(new EmptyBorder((int)Math.round(referenceGameNameSize/150.0)*2, 0, (int)Math.round(referenceGameNameSize/150.0), 0));
        launchHintLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, (int)Math.round(referenceStartButtonSize/48.0 + referenceGameNameSize/90.0)));
    }
}
