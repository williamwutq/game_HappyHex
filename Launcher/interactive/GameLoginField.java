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

package Launcher.interactive;

import io.Username;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;

import static achievements.abstractimpl.MarkableAchievement.markIfExists;

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
        this.setBorder(new CompoundBorder(new LineBorder(Launcher.LaunchEssentials.launchVersionFontColor, 2), new EmptyBorder(6,0,6,0)));
        this.setBackground(Launcher.LaunchEssentials.launchLoginFieldBackgroundColor);
        Dimension dimension = new Dimension(375,50);

        // Limit length
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= Username.MAX_LENGTH) {
                    super.insertString(fb, offset, string, attr);
                }
                String text = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (text.toLowerCase().contains("c++") || text.toLowerCase().contains("cpp")){
                    markIfExists("ChappyHex");
                }
            }
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if ((fb.getDocument().getLength() - length + text.length()) <= Username.MAX_LENGTH) {
                    super.replace(fb, offset, length, text, attrs);
                }
                String totalText = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (totalText.toLowerCase().contains("c++") || totalText.toLowerCase().contains("cpp")){
                    markIfExists("ChappyHex");
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
        this.setBorder(new CompoundBorder(new LineBorder(Launcher.LaunchEssentials.launchVersionFontColor, 2), new EmptyBorder(size, size*2, size, size*2)));
        Dimension dimension = new Dimension(size * Username.MAX_LENGTH, size * 3);
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
    }
}
