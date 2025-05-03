package Launcher.panel;

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class SettingPanel extends UniversalPanel {
    private JLabel launchSettingTitleLabel;
    private JLabel launchSettingEasyModeLabel;
    private JLabel launchSettingRestartGameLabel;
    private JLabel launchSettingGameSizeLabel;
    private JLabel launchSettingGameSmallLabel;
    private JLabel launchSettingGameMediumLabel;
    private JLabel launchSettingGameLargeLabel;
    private SlidingButton launchSettingEasyModeButton;
    private SlidingButton launchSettingRestartGameButton;
    private SlidingButton launchSettingGameSmallButton;
    private SlidingButton launchSettingGameMediumButton;
    private SlidingButton launchSettingGameLargeButton;

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

        launchSettingEasyModeLabel = new JLabel("    Easy Mode | ");
        launchSettingEasyModeLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchSettingEasyModeLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingEasyModeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingEasyModeLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingEasyModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingEasyModeButton = new SlidingButton(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setEasyMode();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setNormalMode();
            }
        };
        launchSettingEasyModeButton.setState(LaunchEssentials.isEasyMode());

        JPanel launchSettingEasyModePanel = new JPanel();
        launchSettingEasyModePanel.setBackground(this.getBackground());
        launchSettingEasyModePanel.setLayout(new BoxLayout(launchSettingEasyModePanel, BoxLayout.X_AXIS));
        launchSettingEasyModePanel.add(Box.createHorizontalGlue());
        launchSettingEasyModePanel.add(launchSettingEasyModeLabel);
        launchSettingEasyModePanel.add(launchSettingEasyModeButton);
        launchSettingEasyModePanel.add(Box.createHorizontalGlue());

        launchSettingRestartGameLabel = new JLabel("Restart Games | ");
        launchSettingRestartGameLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchSettingRestartGameLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingRestartGameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingRestartGameLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingRestartGameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingRestartGameButton = new SlidingButton(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setRestartGame(true);
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setRestartGame(false);
            }
        };
        launchSettingRestartGameButton.setState(LaunchEssentials.isRestartGame());

        JPanel launchSettingRestartGamePanel = new JPanel();
        launchSettingRestartGamePanel.setBackground(this.getBackground());
        launchSettingRestartGamePanel.setLayout(new BoxLayout(launchSettingRestartGamePanel, BoxLayout.X_AXIS));
        launchSettingRestartGamePanel.add(Box.createHorizontalGlue());
        launchSettingRestartGamePanel.add(launchSettingRestartGameLabel);
        launchSettingRestartGamePanel.add(launchSettingRestartGameButton);
        launchSettingRestartGamePanel.add(Box.createHorizontalGlue());

        launchSettingGameSizeLabel = new JLabel("- Game Size -");
        launchSettingGameSizeLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchSettingGameSizeLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingGameSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingGameSizeLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingGameSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingGameSizeLabel.setBorder(new EmptyBorder(16, 0, 16, 0));

        launchSettingGameSmallLabel = new JLabel(" Small  (R=5) | ");
        launchSettingGameSmallLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchSettingGameSmallLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingGameSmallLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingGameSmallLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingGameSmallLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingGameSmallButton = new SlidingButton(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setSmallMode();
                setThreeStates();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setMediumMode();
                setThreeStates();
            }
        };
        launchSettingGameSmallButton.setState(LaunchEssentials.isSmallMode());

        JPanel launchSettingGameSmallPanel = new JPanel();
        launchSettingGameSmallPanel.setBackground(this.getBackground());
        launchSettingGameSmallPanel.setLayout(new BoxLayout(launchSettingGameSmallPanel, BoxLayout.X_AXIS));
        launchSettingGameSmallPanel.add(Box.createHorizontalGlue());
        launchSettingGameSmallPanel.add(launchSettingGameSmallLabel);
        launchSettingGameSmallPanel.add(launchSettingGameSmallButton);
        launchSettingGameSmallPanel.add(Box.createHorizontalGlue());

        launchSettingGameMediumLabel = new JLabel("Medium  (R=8) | ");
        launchSettingGameMediumLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchSettingGameMediumLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingGameMediumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingGameMediumLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingGameMediumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingGameMediumButton = new SlidingButton(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setMediumMode();
                setThreeStates();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setSmallMode();
                setThreeStates();
            }
        };
        launchSettingGameMediumButton.setState(LaunchEssentials.isMediumMode());

        JPanel launchSettingGameMediumPanel = new JPanel();
        launchSettingGameMediumPanel.setBackground(this.getBackground());
        launchSettingGameMediumPanel.setLayout(new BoxLayout(launchSettingGameMediumPanel, BoxLayout.X_AXIS));
        launchSettingGameMediumPanel.add(Box.createHorizontalGlue());
        launchSettingGameMediumPanel.add(launchSettingGameMediumLabel);
        launchSettingGameMediumPanel.add(launchSettingGameMediumButton);
        launchSettingGameMediumPanel.add(Box.createHorizontalGlue());

        launchSettingGameLargeLabel = new JLabel(" Large (R=11) | ");
        launchSettingGameLargeLabel.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchSettingGameLargeLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchSettingGameLargeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchSettingGameLargeLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchSettingGameLargeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchSettingGameLargeButton = new SlidingButton(){
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setLargeMode();
                setThreeStates();
            }
            @Override
            protected void turnedOff(){
                super.turnedOff();
                LaunchEssentials.setSmallMode();
                setThreeStates();
            }
        };
        launchSettingGameLargeButton.setState(LaunchEssentials.isLargeMode());

        JPanel launchSettingGameLargePanel = new JPanel();
        launchSettingGameLargePanel.setBackground(this.getBackground());
        launchSettingGameLargePanel.setLayout(new BoxLayout(launchSettingGameLargePanel, BoxLayout.X_AXIS));
        launchSettingGameLargePanel.add(Box.createHorizontalGlue());
        launchSettingGameLargePanel.add(launchSettingGameLargeLabel);
        launchSettingGameLargePanel.add(launchSettingGameLargeButton);
        launchSettingGameLargePanel.add(Box.createHorizontalGlue());

        return new JComponent[]{launchSettingTitleLabel, (JComponent) Box.createVerticalGlue(), launchSettingEasyModePanel, launchSettingRestartGamePanel, launchSettingGameSizeLabel, launchSettingGameSmallPanel, launchSettingGameMediumPanel, launchSettingGameLargePanel, new QuitButton(), (JComponent) Box.createVerticalGlue()};
    }
    private void setThreeStates(){
        launchSettingGameSmallButton.setState(LaunchEssentials.isSmallMode());
        launchSettingGameMediumButton.setState(LaunchEssentials.isMediumMode());
        launchSettingGameLargeButton.setState(LaunchEssentials.isLargeMode());
    }
    protected JComponent[] fetchHeader() {
        return null;
    }
    public void recalculate(){
        super.recalculate();
        double referenceGameNameSize = Math.min(getReferenceHeight()*1.5, getReferenceWidth());
        double referenceStartButtonSize = Math.min(getReferenceHeight(), getReferenceWidth());
        LaunchButton.setSizeConstant((int)Math.round(referenceStartButtonSize*0.0100));
        launchSettingTitleLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.PLAIN, (int)Math.round(referenceGameNameSize/24.0)));
        launchSettingTitleLabel.setBorder(new EmptyBorder((int)Math.round(referenceGameNameSize/150.0)*2, 0, (int)Math.round(referenceGameNameSize/150.0), 0));
        Font labelFont = new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, (int)Math.round(referenceStartButtonSize/24.0));
        launchSettingEasyModeLabel.setFont(labelFont);
        launchSettingRestartGameLabel.setFont(labelFont);
        launchSettingGameSizeLabel.setFont(labelFont);
        launchSettingGameSmallLabel.setFont(labelFont);
        launchSettingGameMediumLabel.setFont(labelFont);
        launchSettingGameLargeLabel.setFont(labelFont);
        launchSettingGameSizeLabel.setBorder(new EmptyBorder((int)Math.round(referenceStartButtonSize/120.0), 0, (int)Math.round(referenceStartButtonSize/120.0), 0));

        Dimension buttonSize = new Dimension((int)Math.round(referenceStartButtonSize/4), (int)Math.round(referenceStartButtonSize/12));
        launchSettingEasyModeButton.mandateSize(buttonSize);
        launchSettingRestartGameButton.mandateSize(buttonSize);
        launchSettingGameSmallButton.mandateSize(buttonSize);
        launchSettingGameMediumButton.mandateSize(buttonSize);
        launchSettingGameLargeButton.mandateSize(buttonSize);
    }
}
