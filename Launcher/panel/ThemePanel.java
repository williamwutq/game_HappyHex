package Launcher.panel;

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ThemePanel extends UniversalPanel {
    private JLabel launchThemeMainLabel;
    private JLabel launchThemeNormalLabel;
    private JLabel launchThemeDarkLabel;
    private JLabel launchThemeWhiteLabel;
    private SlidingButtonPanel launchThemeNormalButton;
    private SlidingButtonPanel launchThemeDarkButton;
    private SlidingButtonPanel launchThemeWhiteButton;

    public ThemePanel(){
        super();
        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
    }

    protected JComponent[] fetchContent() {
        launchThemeMainLabel = new JLabel("- Graphics Themes -");
        launchThemeMainLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchThemeMainLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchThemeMainLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchThemeMainLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchThemeMainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchThemeMainLabel.setBorder(new EmptyBorder(16, 0, 16, 0));

        launchThemeNormalLabel = new JLabel("Normal | ");
        launchThemeNormalLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchThemeNormalLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchThemeNormalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchThemeNormalLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchThemeNormalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchThemeNormalButton = new SlidingButtonPanel(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setTheme(2);
                setAllStates();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setTheme(4);
                setAllStates();
            }
        };
        launchThemeNormalButton.setState(LaunchEssentials.isSmallMode());

        JPanel launchThemeNormalPanel = new JPanel();
        launchThemeNormalPanel.setBackground(this.getBackground());
        launchThemeNormalPanel.setLayout(new BoxLayout(launchThemeNormalPanel, BoxLayout.X_AXIS));
        launchThemeNormalPanel.add(Box.createHorizontalGlue());
        launchThemeNormalPanel.add(launchThemeNormalLabel);
        launchThemeNormalPanel.add(launchThemeNormalButton);
        launchThemeNormalPanel.add(Box.createHorizontalGlue());

        launchThemeDarkLabel = new JLabel("Dark   | ");
        launchThemeDarkLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchThemeDarkLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchThemeDarkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchThemeDarkLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchThemeDarkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchThemeDarkButton = new SlidingButtonPanel(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setTheme(4);
                setAllStates();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setTheme(2);
                setAllStates();
            }
        };
        launchThemeDarkButton.setState(LaunchEssentials.isMediumMode());

        JPanel launchThemeDarkPanel = new JPanel();
        launchThemeDarkPanel.setBackground(this.getBackground());
        launchThemeDarkPanel.setLayout(new BoxLayout(launchThemeDarkPanel, BoxLayout.X_AXIS));
        launchThemeDarkPanel.add(Box.createHorizontalGlue());
        launchThemeDarkPanel.add(launchThemeDarkLabel);
        launchThemeDarkPanel.add(launchThemeDarkButton);
        launchThemeDarkPanel.add(Box.createHorizontalGlue());

        launchThemeWhiteLabel = new JLabel("White  | ");
        launchThemeWhiteLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchThemeWhiteLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchThemeWhiteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchThemeWhiteLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchThemeWhiteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchThemeWhiteButton = new SlidingButtonPanel(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setTheme(5);
                setAllStates();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setTheme(2);
                setAllStates();
            }
        };
        launchThemeWhiteButton.setState(LaunchEssentials.isLargeMode());

        JPanel launchThemeWhitePanel = new JPanel();
        launchThemeWhitePanel.setBackground(this.getBackground());
        launchThemeWhitePanel.setLayout(new BoxLayout(launchThemeWhitePanel, BoxLayout.X_AXIS));
        launchThemeWhitePanel.add(Box.createHorizontalGlue());
        launchThemeWhitePanel.add(launchThemeWhiteLabel);
        launchThemeWhitePanel.add(launchThemeWhiteButton);
        launchThemeWhitePanel.add(Box.createHorizontalGlue());

        return new JComponent[]{(JComponent) Box.createVerticalGlue(), launchThemeMainLabel, launchThemeNormalPanel, launchThemeDarkPanel, launchThemeWhitePanel, new QuitButton(), (JComponent) Box.createVerticalGlue()};
    }
    private void setAllStates(){
        int theme = LaunchEssentials.getTheme();
        launchThemeNormalButton.setState(theme == 2);
        launchThemeDarkButton.setState(theme == 4);
        launchThemeWhiteButton.setState(theme == 5);
    }
    protected JComponent[] fetchHeader() {
        return null;
    }
    public void recalculate(){
        super.recalculate();
        double referenceStartButtonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        LaunchButton.setSizeConstant((int)Math.round(referenceStartButtonSize*0.0100));
        Font labelFont = new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, (int)Math.round(referenceStartButtonSize/24.0));
        launchThemeMainLabel.setFont(labelFont);
        launchThemeNormalLabel.setFont(labelFont);
        launchThemeDarkLabel.setFont(labelFont);
        launchThemeWhiteLabel.setFont(labelFont);
        launchThemeMainLabel.setBorder(new EmptyBorder((int)Math.round(referenceStartButtonSize/120.0), 0, (int)Math.round(referenceStartButtonSize/120.0), 0));

        Dimension buttonSize = new Dimension((int)Math.round(referenceStartButtonSize/4), (int)Math.round(referenceStartButtonSize/12));
        launchThemeNormalButton.mandateSize(buttonSize);
        launchThemeDarkButton.mandateSize(buttonSize);
        launchThemeWhiteButton.mandateSize(buttonSize);
    }
}
