package Launcher.interactive;

import Launcher.IO.Username;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;

public class GameLoginField extends JTextField{
    public GameLoginField(){
        super(Username.MAX_LENGTH);
        String text = Launcher.LaunchEssentials.getCurrentPlayer();
        if(text.equals("Guest")) {
            this.setText("ENTER THE USERNAME HERE!");
            this.setForeground(Launcher.LaunchEssentials.launchPlayerPromptFontColor);
        } else {
            this.setText(text);
            this.setForeground(Launcher.LaunchEssentials.launchPlayerNameFontColor);
        }
        this.setBorder(new CompoundBorder(new LineBorder(Color.black, 2), new EmptyBorder(6,0,6,0)));
        Dimension dimension = new Dimension(375,50);

        // Limit length
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= Username.MAX_LENGTH) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if ((fb.getDocument().getLength() - length + text.length()) <= Username.MAX_LENGTH) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void setDimension(int size){
        this.setBorder(new CompoundBorder(new LineBorder(Color.black, 2), new EmptyBorder(size, size*2, size, size*2)));
        Dimension dimension = new Dimension(size * Username.MAX_LENGTH, size * 3);
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
    }
}
