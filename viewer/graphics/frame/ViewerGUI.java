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

/**
 * The {@code ViewerGUI} class is the top-level container for the HappyHex game viewer interface.
 * It integrates the main game display {@link GameUI} and the input interface {@link EnterField}
 * into a single user interface component. It is responsible for initializing and laying out
 * these subcomponents and delegating paint operations.
 * <p>
 * The layout behavior is managed manually in {@link #doLayout()}, which assigns space based on
 * the current size of the viewer. The layout allocates:
 * <ul>
 *   <li>Approximately 2/15 of the vertical space to the {@code EnterField} (unless the keyboard is open, in which case it uses the entire space).</li>
 *   <li>The remaining 13/15 of the vertical space to the {@code GameUI} component.</li>
 * </ul>
 * <p>
 * During initialization:
 * <ul>
 *   <li>A {@link Controller} is created to mediate between file input, game state updates, and user interaction.</li>
 *   <li>The {@link GameUI} is bound to this controller for interaction and state visualization.</li>
 *   <li>The {@link EnterField} is initialized with a buffer length and registered as a file interface with the controller.</li>
 * </ul>
 * <p>
 * The {@code ViewerGUI} uses a white background and delegates painting to its children, avoiding any custom graphics painting.
 *
 * @see GameUI
 * @see EnterField
 * @see Controller
 * @see JComponent
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 */
public final class ViewerGUI extends JComponent {
    private final GameUI gameUI;
    private final EnterField enterField;
    /**
     * Constructs a new {@code ViewerGUI} with a {@link GameUI} and {@link EnterField}.
     * Initializes internal layout, controller bindings, and background settings.
     */
    public ViewerGUI(){
        Controller controller = new Controller();
        this.gameUI = new GameUI(controller);
        this.enterField = new EnterField(16);
        controller.bindFileGUI(enterField);
        this.setBackground(Color.WHITE);
        this.add(enterField);
        this.add(gameUI);
    }

    /**
     * Lays out the components within the {@code ViewerGUI}.
     * Allocates vertical space based on component height:
     * <ul>
     *   <li>If the keyboard is shown, {@code EnterField} takes the full height.</li>
     *   <li>Otherwise, {@code EnterField} takes 2/15 and {@code GameUI} takes 13/15 of the height.</li>
     * </ul>
     */
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

    /**
     * Paints this component by delegating to its children.
     * This method overrides the default {@code paint} to skip background or border painting.
     *
     * @param g the {@link Graphics} context to use for painting
     */
    public void paint(Graphics g) {
        paintChildren(g);
    }
}
