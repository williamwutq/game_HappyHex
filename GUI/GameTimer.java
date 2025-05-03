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

package GUI;

import javax.swing.*;
import java.awt.event.*;

public class GameTimer extends Timer implements ActionListener {
    public GameTimer() {
        super(GameEssentials.getDelay(), null);
        this.setRepeats(false);
        this.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Run animation
        hex.Block[] eliminated = GameEssentials.engine().eliminate();
        // Add animation
        for(hex.Block block : eliminated){
            GameEssentials.addAnimation(GameEssentials.createDisappearEffect(block));
            GameEssentials.addAnimation(GameEssentials.createCenterEffect(new hex.Block(block, GameEssentials.gameBlockDefaultColor)));
        }
        // Add score
        GameEssentials.incrementScore(5 * eliminated.length);
        // Check end after eliminate
        GameEssentials.checkEnd();
        GameEssentials.window().repaint();
    }
}
