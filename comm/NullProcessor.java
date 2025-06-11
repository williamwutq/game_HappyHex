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

package comm;

/**
 * A placeholder implementation of the {@code CommandProcessor} interface.
 * <p>
 * The {@code NullProcessor} is a stub implementation used in contexts where a
 * command processor is required but no actual command execution should occur.
 * It raises an exception on any attempt to execute a command, making it useful
 * for guarding against uninitialized or invalid processor references.
 * <p>
 * It supports setting and retrieving a callback processor to conform to
 * the {@code CommandProcessor} interface, but executing a command with this class
 * is considered an error.
 * @version 1.3.3
 * @author William Wu
 * @since 1.3.3
 */
public class NullProcessor implements CommandProcessor{
    private CommandProcessor callback;
    /**
     * Constructs a {@code NullProcessor} with no callback processor assigned.
     */
    public NullProcessor(){
        this.callback = null;
    }
    /**
     * Returns the callback processor associated with this processor, if any.
     * @return the callback processor, or {@code null} if not set
     */
    @Override
    public CommandProcessor getCallBackProcessor(){
        return callback;
    }
    /**
     * Sets the callback processor to be used after command execution.
     * <p>
     * The callback processor must not be the same instance as this processor.
     * If {@code null} is provided, any existing callback processor is cleared.
     * @param processor the processor to set as a callback
     * @throws IllegalArgumentException if attempting to set the callback processor to self
     */
    @Override
    public void setCallBackProcessor(CommandProcessor processor) throws IllegalArgumentException{
        // Null is allowed
        if (this == processor) throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        this.callback = processor;
    }
    /**
     * Raises an error when an attempt is made to execute a command.
     * <p>
     * This method exists to fulfill the {@code CommandProcessor} interface contract,
     * but always throws an exception to indicate that this processor is not
     * intended for use in actual command execution.
     * @param command the command to be executed
     * @param args    the list of arguments for the command
     * @throws IllegalArgumentException always thrown to indicate execution is invalid
     * @throws InterruptedException if the command execution is interrupted or command interrupt current thread
     */
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        throw new IllegalArgumentException("Attempted execution call to NullProcessor");
    }
}
