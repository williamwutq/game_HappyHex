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

package python;

/**
 * An interface for processing commands with optional arguments and callback functionality.
 * This interface provides a framework for parsing and executing commands, with support for
 * chaining command processors through callbacks. Implementations of this interface are
 * responsible for defining the behavior of command execution and callback handling.
 * <p>
 * The primary entry point for command execution is the {@link #execute(String)} method,
 * which accepts a command string, parses it into a command and its arguments, and delegates
 * to the {@link #execute(String, String[])} method for processing. The parsing logic splits
 * the command string at the first space, treating the first part as the command and the
 * remainder as arguments (split by whitespace). If no space is present, the entire string
 * is treated as the command with no arguments.
 * <p>
 * Callback functionality is supported through the {@link #getCallBackProcessor()} and
 * {@link #setCallBackProcessor(CommandProcessor)} methods. These allow implementations to
 * chain command processors, where one processor can delegate or trigger actions in another.
 * By default, callback support is disabled. Implementations can override these methods to
 * enable callback functionality.
 * <p>
 * This interface is designed to be flexible and extensible, allowing implementations
 * to handle a wide range of command formats and execution logic while maintaining a
 * consistent interface for command processing and callback management.
 * @version 1.3.3
 * @author William Wu
 * @since 1.3.3
 */
public interface CommandProcessor {
    /**
     * Executes a command string by parsing it into a command and its arguments.
     * The command string is trimmed and split into a command and an array of arguments
     * based on the first space character. If no space is found, the entire string is
     * treated as the command with no arguments.
     *
     * @param command the command string to process
     * @throws IllegalArgumentException if the command is null, empty, or invalid
     * @throws InterruptedException if the command execution is interrupted or command interrupt current thread
     */
    default void execute(String command) throws IllegalArgumentException, InterruptedException {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        command = command.trim();
        if (command.isEmpty()) throw new IllegalArgumentException("Command cannot be empty");
        int firstIndex = command.indexOf(" ");
        String[] args = new String[]{};
        if (firstIndex != -1) {
            args = command.substring(firstIndex + 1).split("\\s+");
            command = command.substring(0, firstIndex);
        }
        execute(command, args);
    }
    /**
     * Executes a command with its associated arguments.
     * Implementations must define the behavior for processing the command and its arguments.
     *
     * @param command the command to execute
     * @param args an array of arguments for the command
     * @throws IllegalArgumentException if the command or arguments are invalid
     * @throws InterruptedException if the command execution is interrupted or command interrupt current thread
     */
    void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException;
    /**
     * Retrieves the callback processor associated with this command processor.
     * By default, returns null, indicating no callback processor is set.
     * Implementations may override this to provide a specific callback processor.
     *
     * @return the callback processor, or null if none is set
     */
    default CommandProcessor getCallBackProcessor(){
        return null;
    }
    /**
     * Sets the callback processor for this command processor.
     * By default, this operation is unsupported and throws an UnsupportedOperationException.
     * Implementations may override this to allow setting a callback processor.
     * Null is a valid input, allowing the callback processor to be unset.
     *
     * @param processor the callback processor to set
     * @throws IllegalArgumentException if the processor is the same as this instance
     * @throws UnsupportedOperationException if setting a callback processor is not supported
     */
    default void setCallBackProcessor(CommandProcessor processor) throws IllegalArgumentException, UnsupportedOperationException {
        // Null is allowed
        if (this == processor) throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        throw new UnsupportedOperationException("Callback not supported for this command processor");
    }
}
