package Launcher.panel;

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Arrays;

public class ThemePanel extends UniversalPanel {
    private JLabel launchThemeMainLabel;
    private int totalThemes;
    private JLabel[] launchThemeLabels;
    private SlidingButtonPanel[] launchThemeButtons;

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

        // Create arrays
        totalThemes = 3;
        launchThemeLabels = new JLabel[totalThemes];
        launchThemeButtons = new SlidingButtonPanel[totalThemes];

        // Create assembled panels
        JPanel launchThemeNormalPanel = createThemeSelectorPanel(0,"Normal", 2, 4);
        JPanel launchThemeDarkPanel = createThemeSelectorPanel(1, "Dark  ", 4, 2);
        JPanel launchThemeWhitePanel = createThemeSelectorPanel(2, "White ", 5, 2);

        return new JComponent[]{(JComponent) Box.createVerticalGlue(), launchThemeMainLabel, launchThemeNormalPanel, launchThemeDarkPanel, launchThemeWhitePanel, new QuitButton(), (JComponent) Box.createVerticalGlue()};
    }
    private void setAllStates(){
        int theme = LaunchEssentials.getTheme();
        launchThemeButtons[0].setState(theme == 2);
        launchThemeButtons[1].setState(theme == 4);
        launchThemeButtons[2].setState(theme == 5);
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
        for(JLabel label : launchThemeLabels){
            label.setFont(labelFont);
        }
        launchThemeMainLabel.setBorder(new EmptyBorder((int)Math.round(referenceStartButtonSize/120.0), 0, (int)Math.round(referenceStartButtonSize/120.0), 0));

        Dimension buttonSize = new Dimension((int)Math.round(referenceStartButtonSize/4), (int)Math.round(referenceStartButtonSize/12));
        for(SlidingButtonPanel button : launchThemeButtons){
            button.mandateSize(buttonSize);
        }
    }
    public JPanel createThemeSelectorPanel(int index, String text, int targetThemeID, int defaultThemeID) {
        // Create and set the visual properties of the theme label
        launchThemeLabels[index] = new JLabel(text + " | ");
        launchThemeLabels[index].setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        launchThemeLabels[index].setForeground(LaunchEssentials.launchVersionFontColor);
        launchThemeLabels[index].setHorizontalAlignment(SwingConstants.CENTER);
        launchThemeLabels[index].setVerticalAlignment(SwingConstants.CENTER);
        launchThemeLabels[index].setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create the toggle button and define behavior for ON and OFF states
        launchThemeButtons[index] = new SlidingButtonPanel() {
            @Override
            protected void turnedOn() {
                super.turnedOn();
                LaunchEssentials.setTheme(targetThemeID); // Set to the selected theme
                setAllStates();
            }
            @Override
            protected void turnedOff() {
                super.turnedOff();
                LaunchEssentials.setTheme(defaultThemeID); // Revert to default theme
                setAllStates();
            }
        };

        // Set the initial button state based on current theme
        launchThemeButtons[index].setState(LaunchEssentials.getTheme() == targetThemeID);

        // Assemble the panel layout as a centered horizontal layout
        JPanel panel = new JPanel();
        panel.setBackground(this.getBackground());
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(launchThemeLabels[index]);
        panel.add(launchThemeButtons[index]);
        panel.add(Box.createHorizontalGlue());

        return panel; // Return the complete panel
    }
}
