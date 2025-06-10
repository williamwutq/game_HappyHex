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
import java.util.ArrayList;

public class ThemePanel extends UniversalPanel {
    private JLabel launchThemeMainLabel;
    private LaunchButton quitButton;
    private ArrayList<JLabel> launchThemeLabels;
    private ArrayList<JPanel> launchThemePanels;
    private ArrayList<SlidingButton> launchThemeButtons;
    private ArrayList<Integer> targetThemeIDs;
    private ArrayList<special.SpecialFeature> specialThemes;

    public ThemePanel(){
        super();
        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
    }

    public void resetColor(){
        super.resetColor();
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        launchThemeMainLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
        quitButton.resetColor();
        for(JLabel label : launchThemeLabels){
            label.setForeground(LaunchEssentials.launchVersionFontColor);
        }
        for(JPanel panel : launchThemePanels){
            panel.setBackground(LaunchEssentials.launchBackgroundColor);
        }
        for(SlidingButton button : launchThemeButtons){
            button.resetColor();
        }
    }

    protected JComponent[] fetchContent() {
        quitButton = new QuitButton();
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
        launchThemePanels = new ArrayList<>();
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
            launchThemePanels.add((JPanel) result[i + 2]);
        }
        result[result.length - 2] = quitButton;
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
        LaunchButton.setSizeConstant(referenceStartButtonSize*0.01);
        Font labelFont = new Font(LaunchEssentials.launchSettingsFont, Font.PLAIN, (int)Math.round(referenceStartButtonSize/24.0));
        launchThemeMainLabel.setFont(labelFont);
        for(JLabel label : launchThemeLabels){
            label.setFont(labelFont);
        }
        launchThemeMainLabel.setBorder(new EmptyBorder((int)Math.round(referenceStartButtonSize/120.0), 0, (int)Math.round(referenceStartButtonSize/120.0), 0));

        Dimension buttonSize = new Dimension((int)Math.round(referenceStartButtonSize/4), (int)Math.round(referenceStartButtonSize/12));
        for(SlidingButton button : launchThemeButtons){
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
        SlidingButton button = new SlidingButton() {
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
