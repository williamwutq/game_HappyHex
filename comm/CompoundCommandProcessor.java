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

import java.util.ArrayList;
import java.util.Collection;

/**
 * A composite implementation of the {@link CommandProcessor} interface that delegates command execution to a list of
 * sub-processors in a sequential manner. This class manages a collection of {@link CommandProcessor} instances and
 * attempts to execute a command on each sub-processor until one succeeds or all fail. It also supports callback
 * functionality by maintaining a reference to a callback processor and propagating it to all sub-processors.
 * <p>
 * The {@code CompoundCommandProcessor} is designed for scenarios where multiple command processors need to be
 * coordinated to handle a command. It processes commands by iterating through its list of sub-processors in order,
 * attempting to execute the command on each until one succeeds or an unrecoverable error occurs. If no sub-processor
 * can handle the command, an exception is thrown. The class is thread-safe, using synchronization to manage access to
 * the sub-processor list.
 * <p>
 * <b>Usage Notes:</b>
 * <ul>
 *     <li>Ensure sub-processors are added before executing commands, as an empty processor list will throw an
 *         {@link IllegalArgumentException}.</li>
 *     <li>Be cautious when adding sub-processors during command execution, as this may lead to inconsistent behavior
 *         due to concurrent modification. The class uses synchronization to mitigate this, but external coordination
 *         may be needed in highly concurrent environments.</li>
 *     <li>Do not callback processors on sub processors of this compound processors, as that may result in unexpected
 *         behaviors.</li>
 *     <li>When setting a callback processor, ensure it is not the same as this instance or any sub-processor to avoid
 *         circular references.</li>
 *     <li>Handle {@link InterruptedException} appropriately in multi-threaded environments, as it may be thrown during
 *         command execution.</li>
 * </ul>
 * <p>
 * This implementation is suitable for use cases where commands need to be processed by multiple handlers in a specific
 * order, such as in a chain-of-responsibility pattern or when aggregating multiple command processors into a single
 * interface.
 * @version 1.3.3
 * @author William Wu
 * @since 1.3.3
 */
public class CompoundCommandProcessor implements CommandProcessor{
    private CommandProcessor callBackProcessor;
    private final ArrayList<CommandProcessor> processorList;
    /**
     * Constructs a new {@code CompoundCommandProcessor} with an empty list of sub-processors and no callback processor.
     */
    public CompoundCommandProcessor(){
        callBackProcessor = null;
        processorList = new ArrayList<CommandProcessor>();
    }

    /**
     * Executes a command by delegating it to the sub-processors in sequence. The command is passed to each sub-processor
     * until one successfully executes it or all sub-processors fail, resulting in an exception. If the sub-processor
     * list is empty, an exception is thrown immediately.
     *
     * @param command the command to execute
     * @param args an array of arguments for the command
     * @throws IllegalArgumentException if the sub-processor list is empty or no sub-processor can execute the command
     * @throws InterruptedException if the command execution is interrupted
     */
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        int size;
        synchronized (processorList) {
            size = processorList.size();
        }
        if (size == 0) {
            throw new IllegalArgumentException("Command not executed because compound processor has no connected processors");
        } else {
            executeSub(command, args, 0);
        }
    }
    /**
     * Recursively attempts to execute the command on the sub-processor at the specified index. If the sub-processor
     * throws an {@link IllegalArgumentException}, the method proceeds to the next sub-processor. If all sub-processors
     * are exhausted without success, an exception is thrown.
     *
     * @param command the command to execute
     * @param args an array of arguments for the command
     * @param index the index of the current sub-processor to try
     * @throws IllegalArgumentException if no sub-processor can execute the command
     * @throws InterruptedException if the command execution is interrupted
     */
    private void executeSub(String command, String[] args, int index) throws IllegalArgumentException, InterruptedException {
        int size; CommandProcessor processor;
        synchronized (processorList) {
            size = processorList.size();
            processor = processorList.get(index);
        }
        if (index >= size) {
            throw new IllegalArgumentException("Command " + command + " not executable in any of sub-processors of this compound processor instance");
        } else {
            try {
                processor.execute(command, args);
            } catch (IllegalArgumentException e) {
                executeSub(command, args, index + 1);
            } catch (InterruptedException e) {
                throw new InterruptedException("Interrupted while " + processor + " is attempting to execute command " + command);
            }
        }
    }
    /**
     * Adds a sub-processor to the end of the sub-processor list. If processor is null, no action is taken.
     *
     * @param processor the sub-processor to add
     */
    public void addSubProcessor(CommandProcessor processor){
        if (processor != null) {
            synchronized (processorList) {
                processorList.add(processor);
            }
        }
    }
    /**
     * Adds a sub-processor at the specified index in the sub-processor list. If the index is out of bounds, the
     * processor is added at the beginning (if index < 0) or at the end (if index > size). If processor is null, no
     * action is taken.
     *
     * @param index the index at which to add the sub-processor
     * @param processor the sub-processor to add
     */
    public void addSubProcessor(int index, CommandProcessor processor){
        if (processor != null) {
            synchronized (processorList) {
                try {
                    processorList.add(index, processor);
                } catch (IndexOutOfBoundsException e) {
                    if (index < 0) {
                        processorList.addFirst(processor);
                    } else {
                        processorList.add(processor);
                    }
                }
            }
        }
    }
    /**
     * Adds a collection of sub-processors to the end of the sub-processor list. If the collection is null, no action is taken.
     *
     * @param processors the collection of sub-processors to add
     */
    public void addSubProcessor(Collection<CommandProcessor> processors){
        if (processors != null) {
            synchronized (processorList) {
                processorList.addAll(processors);
            }
        }
    }
    /**
     * Removes the sub-processor at the specified index and returns it. If the index is out of bounds, returns null.
     *
     * @param index the index of the sub-processor to remove
     * @return the removed sub-processor, or null if the index is invalid
     */
    public CommandProcessor removeSubProcessor(int index){
        CommandProcessor processor;
        synchronized (processorList) {
            try {
                processor = processorList.remove(index);
            } catch (IndexOutOfBoundsException e) {
                processor = null;
            }
        }
        return processor;
    }
    /**
     * Removes the specified sub-processor from the list and returns whether the removal was successful.
     *
     * @param processor the sub-processor to remove
     * @return true if the sub-processor was removed, false otherwise
     */
    public boolean removeSubProcessor(CommandProcessor processor){
        boolean success;
        synchronized (processorList) {
            success = processorList.remove(processor);
        }
        return success;
    }
    /**
     * Clears all sub-processors from the list.
     */
    public void clear(){
        synchronized (processorList) {
            processorList.clear();
        }
    }
    /**
     * Retrieves the callback processor associated with this compound processor.
     *
     * @return the callback processor, or null if none is set
     */
    @Override
    public CommandProcessor getCallBackProcessor() {
        return callBackProcessor;
    }
    /**
     * Sets the callback processor for this compound processor and propagates it to all sub-processors. Ensures that the
     * callback processor is not the same as this instance or any sub-processor.
     *
     * @param processor the callback processor to set
     * @throws IllegalArgumentException if the processor is this instance or a sub-processor
     */
    @Override
    public void setCallBackProcessor(CommandProcessor processor) throws IllegalArgumentException, UnsupportedOperationException {
        if (this == processor) throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        synchronized (processorList) {
            for (CommandProcessor subProcessor : processorList){
                if (subProcessor == processor) throw new IllegalArgumentException("Cannot add sub-processor as callback processor");
            }
        }
        // Add after completing check
        callBackProcessor = processor;
        synchronized (processorList) {
            for (CommandProcessor subProcessor : processorList){
                try {
                    subProcessor.setCallBackProcessor(processor);
                } catch (IllegalArgumentException | UnsupportedOperationException ignored) {}
            }
        }
    }
}
