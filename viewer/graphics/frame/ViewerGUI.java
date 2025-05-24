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

package viewer.graphics.frame;

import viewer.logic.Controller;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;

public final class ViewerGUI extends JComponent {
    private final GameUI gameUI;
    private final EnterField enterField;
    public ViewerGUI(){
        Controller controller = new Controller();
        this.gameUI = new GameUI(controller);
        this.enterField = new EnterField(16);
        controller.bindFileGUI(enterField);
        this.setBackground(Color.WHITE);
        this.add(enterField);
        this.add(gameUI);
    }

    public void doLayout(){
        int h = this.getHeight();
        int w = this.getWidth();
        int hf = h * 2 / 15;
        int hg = h * 13 / 15;
        gameUI.setBounds(0, hf, w, hg);
        if (enterField.isKeyboardShown()){
            enterField.setBounds(0, 0, w, h);
        } else {
            enterField.setBounds(0, 0, w, hf);
        }
    }

    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        paintChildren(g);
    }
}
