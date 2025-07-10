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

package game;

import comm.CommandProcessor;
import hex.HexEngine;
import hex.Piece;

public class GameCommandProcessor implements CommandProcessor {
    private CommandProcessor callBackProcessor;
    GameGUIInterface gameGUI;
    public GameCommandProcessor(GameGUIInterface gameGUI){
        callBackProcessor = null;
        this.gameGUI = gameGUI;
    }
    @Override
    public CommandProcessor getCallBackProcessor(){
        return callBackProcessor;
    }
    @Override
    public void setCallBackProcessor(CommandProcessor processor) throws IllegalArgumentException, UnsupportedOperationException {
        if (this == processor){
            throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        }
        callBackProcessor = processor;
    }
    public void query() throws InterruptedException {
        HexEngine engine = gameGUI.getEngine();
        Piece[] queue = gameGUI.getQueue();

        if (callBackProcessor == null) {
            throw new IllegalStateException("Call back processor is not properly initialized");
        } else {
            callBackProcessor.execute("move " + getEngineString(engine) + " " + getQueueString(queue));
        }
    }
    public static String getEngineString(HexEngine engine) {
        if (engine == null || engine.length() == 0){
            return "";
        } else {
            StringBuilder builder = new StringBuilder(engine.getBlock(0).getState() ? "X" : "O");
            for (int i = 1; i < engine.length(); i ++){
                builder.append(engine.getBlock(i).getState() ? "X" : "O");
            }
            return builder.toString();
        }
    }
    public static String getQueueString(Piece[] pieces) {
        if (pieces == null || pieces.length == 0){
            return "";
        } else {
            StringBuilder builder = new StringBuilder(getPieceString(pieces[0]));
            for (int i = 1; i < pieces.length; i ++){
                builder.append(" ").append(getPieceString(pieces[i]));
            }
            return builder.toString();
        }
    }
    public static String getPieceString(Piece piece) {
        return Byte.toString(piece.toByte());
    }
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
            gameGUI.move(gameGUI.getEngine().getBlockIndex(i, k), index);
        } else throw new IllegalArgumentException("Illegal command for this GameCommandProcessor");
    }
}
