package Launcher;

import GUI.GameEssentials;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LaunchPanel extends JPanel implements ComponentListener {
    private final JPanel gameNameLabelPanel;
    private final String gameNameString = "⬢HAPPY⬢⬢HEX⬢";
    private final JLabel[] gameNameLabels;
    private final JLabel launchVersionLabel;
    private final JPanel launchAuthorPanel;
    private final JLabel launchAuthorLabelA;
    private final JLabel launchAuthorLabelWW;
    private final JLabel launchAuthorLabelGame;
    private final JLabel launchCopyrightLabel;
    private final LaunchButton startButton;
    private final LaunchButton loginButton;
    private final LaunchButton settingButton;

    public LaunchPanel(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(LaunchEssentials.launchBackgroundColor);
        this.add(Box.createVerticalGlue());
        gameNameLabelPanel = new JPanel();
        gameNameLabelPanel.setLayout(new BoxLayout(gameNameLabelPanel, BoxLayout.X_AXIS));
        gameNameLabelPanel.setBackground(LaunchEssentials.launchTitlePanelBackgroundColor);
        gameNameLabelPanel.add(Box.createHorizontalGlue());
        gameNameLabels = new JLabel[12];
        int gameNameLabelIndexShift = (int)(Math.random()*12);
        for (int i = 0; i < 12; i ++){
            gameNameLabels[i] = new JLabel();
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, 80));
            gameNameLabels[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            gameNameLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            gameNameLabels[i].setText(gameNameString.substring(i, i+1));
            gameNameLabels[i].setForeground(GameEssentials.interpolate(GameEssentials.getIndexedPieceColor((gameNameLabelIndexShift+i*5)%12), LaunchEssentials.launchTitlePanelBackgroundColor, 4));
            gameNameLabelPanel.add(gameNameLabels[i]);
        }
        gameNameLabelPanel.add(Box.createHorizontalGlue());
        this.add(gameNameLabelPanel);

        launchVersionLabel = new JLabel(" Version " + LaunchEssentials.currentGameVersion + " ");
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, 40));
        launchVersionLabel.setForeground(LaunchEssentials.launchVersionFontColor);
        launchVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchVersionLabel.setBorder(new EmptyBorder(12, 0, 6, 0));
        this.add(launchVersionLabel);

        launchAuthorPanel = new JPanel();
        launchAuthorPanel.setLayout(new BoxLayout(launchAuthorPanel, BoxLayout.X_AXIS));
        launchAuthorPanel.setBorder(new EmptyBorder(5,5,5,5));
        launchAuthorPanel.setBackground(new Color(0,0,0,0));
        launchAuthorLabelA = new JLabel("  A ");
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchAuthorLabelA.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchAuthorLabelA.setHorizontalAlignment(SwingConstants.LEFT);
        launchAuthorLabelA.setVerticalAlignment(SwingConstants.CENTER);
        launchAuthorLabelA.setAlignmentY(Component.CENTER_ALIGNMENT);
        launchAuthorLabelWW = new JLabel("W.W");
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, 28));
        launchAuthorLabelWW.setForeground(LaunchEssentials.launchWWFontColor);
        launchAuthorLabelWW.setHorizontalAlignment(SwingConstants.CENTER);
        launchAuthorLabelWW.setVerticalAlignment(SwingConstants.CENTER);
        launchAuthorLabelWW.setAlignmentY(Component.CENTER_ALIGNMENT);
        launchAuthorLabelGame = new JLabel(" Game  ");
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchAuthorLabelGame.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchAuthorLabelGame.setHorizontalAlignment(SwingConstants.RIGHT);
        launchAuthorLabelGame.setVerticalAlignment(SwingConstants.CENTER);
        launchAuthorLabelGame.setAlignmentY(Component.CENTER_ALIGNMENT);
        launchCopyrightLabel = new JLabel("  ©2025 William Wu  ");
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, 20));
        launchCopyrightLabel.setForeground(LaunchEssentials.launchAuthorFontColor);
        launchCopyrightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        launchCopyrightLabel.setVerticalAlignment(SwingConstants.CENTER);
        launchCopyrightLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        launchAuthorPanel.add(launchAuthorLabelA);
        launchAuthorPanel.add(launchAuthorLabelWW);
        launchAuthorPanel.add(launchAuthorLabelGame);
        launchAuthorPanel.add(Box.createHorizontalGlue());
        launchAuthorPanel.add(launchCopyrightLabel);

        LaunchButton.setSizeConstant(6);
        LaunchButton.setBackGroundColor(LaunchEssentials.launchBackgroundColor);
        startButton = new StartButton();
        loginButton = new LoginButton();
        settingButton = new SettingButton();

        this.add(Box.createVerticalGlue());
        this.add(loginButton);
        this.add(settingButton);
        this.add(startButton);
        this.add(Box.createVerticalGlue());
        this.add(launchAuthorPanel);
        this.addComponentListener(this);
    }
    public void recalculate(){
        double referenceHeight = this.getSize().getHeight();
        double referenceWidth = this.getSize().getWidth();

        double referenceGameNameSize = Math.min(referenceHeight*2.5, referenceWidth);
        for (int i = 0; i < 12; i ++){
            gameNameLabels[i].setFont(new Font(LaunchEssentials.launchTitleFont, Font.BOLD, (int)Math.round(referenceGameNameSize/10)));
        }
        launchVersionLabel.setFont(new Font(LaunchEssentials.launchVersionFont, Font.ITALIC, (int)Math.round(referenceGameNameSize/24.0)));
        launchVersionLabel.setBorder(new EmptyBorder((int)Math.round(referenceGameNameSize/150.0)*2, 0, (int)Math.round(referenceGameNameSize/150.0), 0));

        double referenceStartButtonSize = Math.min(referenceHeight, referenceWidth);
        LaunchButton.setSizeConstant((int)Math.round(referenceStartButtonSize*0.0100));

        double referenceLaunchAuthorSize = Math.min(referenceHeight, referenceWidth*2.25);
        int referenceLaunchAuthorPanelBorderSize = (int)Math.round(referenceLaunchAuthorSize / 160.0);
        launchAuthorPanel.setBorder(new EmptyBorder(referenceLaunchAuthorPanelBorderSize, referenceLaunchAuthorPanelBorderSize, referenceLaunchAuthorPanelBorderSize, referenceLaunchAuthorPanelBorderSize));
        launchAuthorLabelA.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int)Math.round(referenceLaunchAuthorSize/40.0)));
        launchAuthorLabelWW.setFont(new Font(LaunchEssentials.launchWWFont, Font.PLAIN, (int)Math.round(referenceLaunchAuthorSize*0.03)));
        launchAuthorLabelGame.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int)Math.round(referenceLaunchAuthorSize/40.0)));
        launchCopyrightLabel.setFont(new Font(LaunchEssentials.launchAuthorFont, Font.PLAIN, (int)Math.round(referenceLaunchAuthorSize/40.0)));
    }

    public void componentResized(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public void componentMoved(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public void componentShown(ComponentEvent e) {
        this.recalculate();
        this.repaint();
    }
    public void componentHidden(ComponentEvent e) {}
}
