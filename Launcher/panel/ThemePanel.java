package Launcher.panel;

import Launcher.LaunchEssentials;
import Launcher.interactive.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class ThemePanel extends UniversalPanel {
    private JLabel launchThemeMainLabel;
    private ArrayList<JLabel> launchThemeLabels;
    private ArrayList<SlidingButtonPanel> launchThemeButtons;
    private ArrayList<Integer> targetThemeIDs;
    private ArrayList<special.SpecialFeature> specialThemes;

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
        targetThemeIDs = new ArrayList<>();
        launchThemeLabels = new ArrayList<>();
        launchThemeButtons = new ArrayList<>();
        specialThemes = new ArrayList<>();

        // Create special Features
        specialThemes.add(special.FeatureFactory.createFeature()); // Normal
        // To be implemented

        // Add theme IDs
        targetThemeIDs.add(2); // Normal
        targetThemeIDs.add(4); // Dark
        targetThemeIDs.add(5); // White

        // Create assembled panels
        String[] texts = new String[] {"Normal", "Dark  ", "White "};

        // Create array
        JComponent[] result = new JComponent[4 + targetThemeIDs.size()];
        result[0] = (JComponent) Box.createVerticalGlue();
        result[1] = launchThemeMainLabel;
        for(int i = 0; i < targetThemeIDs.size(); i ++){
            result[i + 2] = createThemeSelectorPanel(texts[i]);
        }
        result[result.length - 2] = new QuitButton();
        result[result.length - 1] = (JComponent) Box.createVerticalGlue();
        return result;
    }
    private void setAllStates(){
        int theme = LaunchEssentials.getTheme();
        for (int i = 0; i < launchThemeButtons.size(); i++) {
            launchThemeButtons.get(i).setState(theme == targetThemeIDs.get(i));
        }
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
    public JPanel createThemeSelectorPanel(String text) {
        // Compute theme IDs
        final int targetThemeID = targetThemeIDs.get(launchThemeLabels.size());
        final int defaultThemeID = (launchThemeLabels.size() == 0) ? targetThemeIDs.get(1) : targetThemeIDs.get(0);

        // Create and set the visual properties of the theme label
        JLabel label = new JLabel(text + " | ");
        label.setFont(new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, 40));
        label.setForeground(LaunchEssentials.launchVersionFontColor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchThemeLabels.add(label);

        // Create the toggle button and define behavior for ON and OFF states
        SlidingButtonPanel button = new SlidingButtonPanel() {
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
        button.setState(LaunchEssentials.getTheme() == targetThemeID);
        launchThemeButtons.add(button);

        // Assemble the panel layout as a centered horizontal layout
        JPanel panel = new JPanel();
        panel.setBackground(this.getBackground());
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(label);
        panel.add(button);
        panel.add(Box.createHorizontalGlue());

        return panel; // Return the complete panel
    }
}
