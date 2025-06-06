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
 * A grouped implementation of the {@link CommandProcessor} interface that organizes a named collection of sub-processors.
 * This class routes commands to a specific sub-processor based on a unique name and provides centralized support for
 * control commands like "kill" and "interrupt", which are broadcast to all sub-processors.
 * <p>
 * The {@code GroupedCommandProcessor} maintains two synchronized lists: one for processor names and one for corresponding
 * {@link CommandProcessor} instances. Each sub-processor is registered with a unique name, and command sare dispatched
 * only to the processor that matches the given name. This implementation handles the command with exact routing, and
 * if the command name is not found, it fails with an {@link IllegalArgumentException}.
 *
 * <p>
 * <b>Usage Notes:</b>
 * <ul>
 *     <li>Use {@link #addSubProcessor(String, CommandProcessor)} to register processors with a unique name.
 *         Names must not be {@code "kill"} or {@code "interrupt"} as those are reserved for system-wide broadcast commands.</li>
 *     <li>Re-adding a processor with an existing name replaces the previous one.</li>
 *     <li>Calling {@code execute("kill")} or {@code execute("interrupt")} will invoke the corresponding method
 *         on all sub-processors, ignoring any exceptions that may be thrown.</li>
 *     <li>When executing any other command, the first argument of {@code args} is interpreted as a sub-command
 *         and passed along with the remaining arguments to the matched sub-processor.</li>
 *     <li>Supports callback functionality: when a callback processor is set on this instance,
 *         it is propagated to all sub-processors that support callbacks.</li>
 *     <li>This class is thread-safe; modifications to sub-processor lists are synchronized using synchronized collections
 *         and appropriate locking during iteration.</li>
 *     <li>Setting this instance as its own callback is not allowed and will throw {@link IllegalArgumentException}.</li>
 * </ul>
 *
 * <p>
 * <b>Design Considerations:</b>
 * <ul>
 *     <li>Processor names are stored separately from processors to support fast lookups and updates.</li>
 *     <li>Two lists are used in parallel (name list and processor list) to maintain order and support positional operations.</li>
 *     <li>The design avoids complex fallback logic and instead opts for direct and predictable command routing.</li>
 *     <li>The internal synchronization strategy is suitable for moderate concurrency;
 *         external synchronization may be required for high-concurrency scenarios with frequent updates.</li>
 * </ul>
 * This implementation is suitable for use cases where each command maps to a well-defined named handler,
 * such as command dispatchers, remote shells, or interpreters with modular subsystems.
 * @version 1.3.3
 * @author William Wu
 * @since 1.3.3
 */
public class GroupedCommandProcessor implements CommandProcessor{
    private final ArrayList<CommandProcessor> processorList;
    private final ArrayList<String> nameList;
    private CommandProcessor callbackProcessor;

    /**
     * Constructs a new {@code GroupedCommandProcessor} with empty sub-processor and name lists.
     * Both lists are initialized as synchronized collections to ensure thread safety.
     */
    public GroupedCommandProcessor() {
        this.processorList = new ArrayList<>();
        this.nameList = new ArrayList<>();
    }
    /**
     * Adds or replaces a sub-processor associated with the given name.
     * If a processor with the same name already exists, it is replaced.
     * Reserved names {@code "kill"} and {@code "interrupt"} are not allowed.
     *
     * @param name      the unique name for the sub-processor
     * @param processor the processor to associate with the name
     * @throws IllegalArgumentException if the name is null, "kill", or "interrupt"
     */
    public void addSubProcessor(String name, CommandProcessor processor) {
        if (name == null || name.equals("kill") || name.equals("interrupt")) {
            throw new IllegalArgumentException("Name cannot be null or reserved word: kill, interrupt");
        }
        synchronized (processorList) {
            int index = nameList.indexOf(name);
            if (index != -1) {
                processorList.set(index, processor);
            } else {
                nameList.add(name);
                processorList.add(processor);
            }
        }
    }
    /**
     * Adds or replaces multiple sub-processors in a batch operation.
     * Each processor is associated with a corresponding name in the provided collections.
     * <p>
     * If a name already exists, its associated processor is replaced. Reserved names
     * {@code "kill"} and {@code "interrupt"} are not allowed and will result in an exception.
     * <p>
     * Both collections must be non-null and must be equal in size.
     * All invalid processor items are ignored during the addition process.
     *
     * @param names       a collection of unique names to associate with the processors
     * @param processors  a collection of {@link CommandProcessor} instances corresponding to each name
     * @throws IllegalArgumentException if the collections are null, not the same size,
     *                                  contain null elements, or contain reserved names
     */
    public void addSubProcessor(Collection<String> names, Collection<CommandProcessor> processors) {
        if (names == null || processors == null) {
            throw new IllegalArgumentException("Name and processor collections cannot be null");
        }
        if (names.size() != processors.size()) {
            throw new IllegalArgumentException("Name and processor collections must be the same size");
        }
        int size = names.size();
        String[] nameArray = names.toArray(new String[0]);
        CommandProcessor[] processorArray = processors.toArray(new CommandProcessor[0]);

        synchronized (processorList) {
            for (int i = 0; i < size; i++) {
                String name = nameArray[i];
                CommandProcessor processor = processorArray[i];
                if (name == null || processor == null) {
                    continue; // skip invalid
                } else if ("kill".equals(name) || "interrupt".equals(name)) {
                    continue; // skip invalid
                }
                int index = nameList.indexOf(name);
                if (index != -1) {
                    processorList.set(index, processor);
                } else {
                    nameList.add(name);
                    processorList.add(processor);
                }
            }
        }
    }

    /**
     * Executes a command with its arguments.
     * <p>
     * If the command is {@code "kill"} or {@code "interrupt"}, it will invoke that command
     * on all sub-processors, ignoring any thrown exceptions.
     * <p>
     * Otherwise, it will look up a sub-processor by name (equal to the command string),
     * and delegate the first argument in {@code args} as the sub-command, with the remaining
     * args passed as arguments.
     *
     * @param command the command to execute (name of the sub-processor or special command)
     * @param args    arguments to pass to the sub-processor (must include sub-command)
     * @throws IllegalArgumentException if the command is unrecognized or arguments are missing
     * @throws InterruptedException     if any processor signals interruption during execution
     */
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        if ("kill".equals(command) || "interrupt".equals(command)) {
            synchronized (processorList) {
                for (CommandProcessor processor : processorList) {
                    try {
                        processor.execute(command, args);
                    } catch (Exception ignored) {
                        // Ignore all exceptions during kill/interrupt
                    }
                }
            }

        } else synchronized (processorList) {
            int index = nameList.indexOf(command);
            if (index == -1) {
                throw new IllegalArgumentException("Unknown command does not match any processor name " + command);
            }
            CommandProcessor processor = processorList.get(index);
            if (args.length == 0) {
                throw new IllegalArgumentException("Subcommand required for sub processor " + command);
            }
            String subCommand = args[0];
            String[] subArgs = new String[args.length-1];
            System.arraycopy(args, 1, subArgs, 0, args.length-1);
            processor.execute(subCommand, subArgs);
        }
    }
    /**
     * Removes the sub-processor at the specified index and returns it. If the processor name is not found, returns null.
     *
     * @param name the name of the sub-processor to remove
     * @return the removed sub-processor, or null if the name is invalid
     */
    public CommandProcessor removeSubProcessor(String name){
        CommandProcessor processor;
        synchronized (processorList) {
            try {
                int index = nameList.indexOf(name);
                nameList.remove(index);
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
        boolean success = true;
        synchronized (processorList) {
            int index = processorList.indexOf(processor);
            if (index >= 0){
                try{
                    nameList.remove(index);
                    processorList.remove(index);
                } catch (IndexOutOfBoundsException e) {
                    success = false;
                }
            } else success = false;
        }
        return success;
    }
    /**
     * Clears all sub-processors from the list.
     */
    public void clear(){
        synchronized (processorList) {
            processorList.clear();
            nameList.clear();
        }
    }
    /**
     * Retrieves the callback processor associated with this compound processor.
     *
     * @return the callback processor, or null if none is set
     */
    @Override
    public CommandProcessor getCallBackProcessor() {
        return callbackProcessor;
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
        callbackProcessor = processor;
        synchronized (processorList) {
            for (CommandProcessor subProcessor : processorList){
                try {
                    subProcessor.setCallBackProcessor(processor);
                } catch (IllegalArgumentException | UnsupportedOperationException ignored) {}
            }
        }
    }
}
