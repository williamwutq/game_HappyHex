package Launcher.panel;

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class SettingPanel extends UniversalPanel {
    private JLabel launchSettingTitleLabel;
    public SettingPanel(){
        super();
        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
    }

    protected JComponent[] fetchContent() {
        launchSettingTitleLabel = new JLabel("Settings");
        launchSettingTitleLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, 40));
        launchSettingTitleLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingTitleLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingTitleLabel.setBorder(new EmptyBorder(12, 0, 6, 0));
        return new JComponent[]{launchSettingTitleLabel, (JComponent) Box.createVerticalGlue()};
    }
    protected JComponent[] fetchHeader() {
        return null;
    }
    public void recalculate(){
        double referenceGameNameSize = Math.min(getReferenceHeight()*2.5, getReferenceWidth());
        double referenceStartButtonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        LaunchButton.setSizeConstant((int)Math.round(referenceStartButtonSize*0.0100));
        launchSettingTitleLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, (int)Math.round(referenceGameNameSize/24.0)));
        launchSettingTitleLabel.setBorder(new EmptyBorder((int)Math.round(referenceGameNameSize/150.0)*2, 0, (int)Math.round(referenceGameNameSize/150.0), 0));
    }
}
