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

package viewer.GameViewer.app.Contents.app.logic;

/**
 * The {@code FileGUIInterface} is a GUI interface that handle file input/output
 * and data loading for the game viewer. This includes selecting files, notifying
 * the controller of file changes, and displaying the active file name.
 * <p>
 * This interface is intended to be implemented by any GUI class where
 * the user enter targeting file name for data loading.
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 */
public interface FileGUIInterface {
    /**
     * Called by the controller to update the GUI with the current file name.
     *
     * @param filename the name of the file currently loaded or selected
     */
    void setFilename(String filename);
    /**
     * Called by the controller to update the GUI with the current file name.
     * Notifies the controller that a new file has been chosen by the user.
     * This will prompt the controller to load a new game file.
     *
     * @return the name of the file currently loaded or selected
     */
    String getFilename();

    /**
     * Sets a listener that will be notified when the name or filename changes.
     * This method allows the implementing class to register a callback that will
     * be triggered when a new file is chosen or a name change event occurs.
     *
     * @param listener the {@link NameChangeListener} instance to be notified of name or file changes
     */
    void setNameChangeListener(NameChangeListener listener);

    /**
     * A listener interface for receiving notifications when a new file has been selected.
     * <p>
     * Classes that are interested in being notified when a file has been selected
     * should implement this interface.
     * @author William Wu
     * @version 1.0 (HappyHex 1.3)
     * @since 1.0 (HappyHex 1.3)
     */
    interface NameChangeListener{
        /**
         * Notifies the controller that a new file has been chosen by the user.
         * This will prompt the controller to load a new game file.
         *
         * @param filename the path or name of the selected file
         */
        void onFileChosen(String filename);
    }
}

