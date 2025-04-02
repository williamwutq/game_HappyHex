package Launcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends UniversalPanel {
    private JLabel launchEnterTextPrompt;
    private JLabel[] launchEnterTextRequirements;
    private GameLoginField textField;
    public LoginPanel(){
        super();
    }
    protected JComponent[] fetchContent() {
        // Prompts
        launchEnterTextPrompt = new JLabel("Enter Username");
        launchEnterTextPrompt.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, 40));
        launchEnterTextPrompt.setForeground(LaunchEssentials.launchVersionFontColor);
        launchEnterTextPrompt.setHorizontalAlignment(SwingConstants.CENTER);
        launchEnterTextPrompt.setVerticalAlignment(SwingConstants.CENTER);
        launchEnterTextPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchEnterTextPrompt.setBorder(new EmptyBorder(12, 0, 6, 0));
        String[] launchEnterText = {
                "Rules:                                                       ",
                "1. Must be between 3 - 24 characters long, inclusive.    ",
                "2. Only contain 1-9, A-Z, a-z, dash, underline or space. ",
                "3. Must contain at least one letter.                     ",
                "4. Special Symbols such as #%$ are not allowed.          ",
                "5. Cannot start or end with dash, underline or spaces.   ",
                "6. Cannot be one of the keywords used by the game system."
        };
        launchEnterTextRequirements = new JLabel[7];
        for (int i = 0; i < launchEnterTextRequirements.length; i++) {
            launchEnterTextRequirements[i] = new JLabel();
            JLabel label = launchEnterTextRequirements[i];
            label.setText(launchEnterText[i]);
            label.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.PLAIN, 20));
            label.setForeground(LaunchEssentials.launchVersionFontColor);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        launchEnterTextRequirements[0].setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, 20));

        // Text field
        textField = new GameLoginField();
        textField.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, 20));
        return new JComponent[]{(JComponent) Box.createVerticalGlue(), launchEnterTextPrompt, launchEnterTextRequirements[0],
                launchEnterTextRequirements[1], launchEnterTextRequirements[2], launchEnterTextRequirements[3],
                launchEnterTextRequirements[4], launchEnterTextRequirements[5], launchEnterTextRequirements[6],
                (JComponent) Box.createVerticalGlue(), textField, (JComponent) Box.createVerticalGlue()};
    }
    protected JComponent[] fetchHeader() {
        return null;
    }
    public void recalculate(){
        super.recalculate();
        double referenceEnterTextSize = Math.min(getReferenceHeight()*2, getReferenceWidth());
        launchEnterTextPrompt.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.ITALIC, (int)Math.round(referenceEnterTextSize/24.0)));
        launchEnterTextPrompt.setBorder(new EmptyBorder((int)Math.round(referenceEnterTextSize/150.0)*2, 0, (int)Math.round(referenceEnterTextSize/150.0), 0));

        launchEnterTextRequirements[0].setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, (int)Math.round(referenceEnterTextSize/48.0)));
        for (int i = 1; i < launchEnterTextRequirements.length; i++) {
            launchEnterTextRequirements[i].setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.PLAIN, (int)Math.round(referenceEnterTextSize/48.0)));
        }

        textField.setFont(new Font(LaunchEssentials.launchEnterUsernameFont, Font.BOLD, (int)Math.round(referenceEnterTextSize/48.0)));
        textField.setDimension((int)Math.round(referenceEnterTextSize/48.0));
    }
}
