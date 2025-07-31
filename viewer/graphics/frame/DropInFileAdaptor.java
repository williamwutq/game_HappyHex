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

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

/**
 * The {@code DropInFileAdaptor} class is a custom drop target adapter that handles file drop events
 * in a GUI component, specifically designed for the HappyHex game viewer.
 * <p>
 * It accepts files dropped onto the component, checks if the file is a valid game file,
 * and notifies the controller with the file path if valid.
 * <p>
 * This class extends {@link DropTargetAdapter} to provide custom behavior for handling file drops.
 *
 * @see DropTargetAdapter
 * @see Controller#onFileChosen(String)
 * @author William Wu
 * @version 1.1 (HappyHex 1.4)
 * @since 1.1 (HappyHex 1.4)
 */
public class DropInFileAdaptor extends DropTargetAdapter{
    private final Controller controller;
    /**
     * Constructs a DropInFileAdaptor with the specified controller.
     * <p>
     * This adaptor is used to handle file drop events in a GUI component.
     * It accepts files dropped onto the component, check if the file is a valid game file,
     * and notifies the controller with the file path if valid.
     *
     * @param controller the controller to handle file drop events
     */
    public DropInFileAdaptor(Controller controller) {
        super();
        this.controller = controller;
    }
    /**
     * Handles the drop event when files are dropped onto the component.
     * <p>
     * It accepts the drop, retrieves the list of dropped files, and checks if the first file
     * is a valid game file (with a .hpyhex extension or without an extension). If valid,
     * it notifies the controller with the file path to load the file.
     *
     * @param event the drop event containing information about the dropped files
     */
    @Override
    public void drop(DropTargetDropEvent event) {
        try {
            event.acceptDrop(DnDConstants.ACTION_COPY);
            Object droppedList = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            if (droppedList instanceof List) {
                List<File> droppedFiles = (List<File>) droppedList;
                if (!droppedFiles.isEmpty()){
                    File file = droppedFiles.getFirst();
                    String filename = file.getAbsolutePath();

                    if (filename.endsWith(".hpyhex")) {
                        filename = filename.substring(0, filename.length() - 7);
                    }
                    controller.onFileChosen(filename);
                }
            }
        } catch (Exception ignored) {}
    }
}
