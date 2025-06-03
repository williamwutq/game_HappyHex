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

import comm.CommandProcessor;
import hex.Hex;
import hex.Piece;

public class GameCommandProcessor implements CommandProcessor {
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        if (command.equals("move") && args.length == 3){
            // Parse
            int i, k, index;
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Command move is invalid because I-line coordinate of move is not integer");
            }
            try {
                k = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Command move is invalid because K-line coordinate of move is not integer");
            }
            try {
                index = Integer.parseUnsignedInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Command move is invalid because piece index is not unsigned integer");
            }
            // Move
            Hex origin = Hex.hex(i, k); Piece piece;
            try {
                piece = GameEssentials.queue().get(index);
                GameEssentials.setSelectedPieceIndex(index);
                GameEssentials.setSelectedBlockIndex(0);
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Command move is invalid because piece index out of bounds");
            }
            // Move origin
            origin = origin.add(piece.getBlock(0).thisHex());
            GameEssentials.addMove(origin);
        } else throw new IllegalArgumentException("Illegal command for this GameCommandProcessor");
    }
}
