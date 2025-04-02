package Launcher.panel;

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LaunchPanel extends UniversalPanel {
    private JLabel launchVersionLabel;
    private final LaunchButton startButton;
    private final LaunchButton loginButton;
    private final LaunchButton settingButton;

    public LaunchPanel(){
        super();
        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
        startButton = new StartButton();
        loginButton = new LoginButton();
        settingButton = new SettingButton();
    }

    protected JComponent[] fetchContent() {
        JComponent[] components = {fetchLaunchVersionLabel(), (JComponent) Box.createVerticalGlue(), new LoginButton(), new SettingButton(), new StartButton(), (JComponent) Box.createVerticalGlue()};
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

    public void recalculate(){
        super.recalculate();
        double referenceStartButtonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        LaunchButton.setSizeConstant((int) Math.round(referenceStartButtonSize * 0.0100));
        double referenceGameNameSize = Math.min(getReferenceHeight()*2.5, getReferenceWidth());
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, (int)Math.round(referenceGameNameSize/24.0)));
        launchVersionLabel.setBorder(new EmptyBorder((int)Math.round(referenceGameNameSize/150.0)*2, 0, (int)Math.round(referenceGameNameSize/150.0), 0));
    }
}
