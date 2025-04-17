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
        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
    }
    protected JComponent[] fetchContent() {
        JComponent[] components = {fetchLaunchVersionLabel(), fetchLaunchHintLabel(), (JComponent) Box.createVerticalGlue(), new LoginButton(), new SettingButton(), new ThemeButton(), new StartButton(), (JComponent) Box.createVerticalGlue()};
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
        LaunchButton.setSizeConstant((int) Math.round(referenceStartButtonSize * 0.0080));
        double referenceGameNameSize = Math.min(getReferenceHeight()*2.5, getReferenceWidth());
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, (int)Math.round(referenceGameNameSize/24.0)));
        launchVersionLabel.setBorder(new EmptyBorder((int)Math.round(referenceGameNameSize/150.0)*2, 0, (int)Math.round(referenceGameNameSize/150.0), 0));
        launchHintLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, (int)Math.round(referenceStartButtonSize/48.0 + referenceGameNameSize/80.0)));
    }
}
