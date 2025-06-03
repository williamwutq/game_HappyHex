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
 * A thread-based implementation of the CommandProcessor interface that wraps another CommandProcessor
 * and provides periodic ping callbacks to signal it is alive. Supports master/slave roles that can be
 * reset on stop commands, with specific command handling as defined.
 */
public class ThreadedCommandProcessor implements CommandProcessor, Runnable {
    private volatile boolean running;
    private volatile boolean isMaster;
    private CommandProcessor wrappedProcessor;
    private CommandProcessor callbackProcessor;
    private volatile String threadName;
    private volatile String masterName;
    private volatile String slaveName;

    /**
     * Constructs a ThreadedCommandProcessor wrapping the provided CommandProcessor.
     *
     * @param processor the CommandProcessor to wrap
     * @throws IllegalArgumentException if processor is null
     */
    public ThreadedCommandProcessor(CommandProcessor processor) {
        if (processor == null) throw new IllegalArgumentException("Wrapped processor cannot be null");
        this.wrappedProcessor = processor;
        this.threadName = "CommandProcessor-" + System.identityHashCode(this);
        this.running = false;
        this.isMaster = true; // Default to master until configured otherwise
    }

    /**
     * Executes a command with optional arguments, handling specific commands as defined.
     *
     * @param command the command to execute
     * @param args an array of arguments for the command
     * @throws IllegalArgumentException if the command or arguments are invalid
     * @throws InterruptedException if the command execution is interrupted
     */
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        command = command.trim();
        if (command.isEmpty()) throw new IllegalArgumentException("Command cannot be empty");

        switch (command.toLowerCase()) {
            case "kill":
                running = false;
                isMaster = true; // Default
                System.out.println(threadName + " killed forcefully");
                break;

            case "stop":
                if (isMaster) {
                    if (callbackProcessor != null) {
                        callbackProcessor.execute("stop", new String[]{"0"});
                        // Wait for callback to respond with stop 0
                        Thread.sleep(1000); // Simplified wait
                    }
                    running = false;
                } else {
                    if (callbackProcessor != null) {
                        callbackProcessor.execute("stop", new String[]{"0"});
                    }
                    isMaster = true; // Default
                }
                System.out.println(threadName + " stopped");
                break;

            case "stop 0":
                if (isMaster) {
                    running = false;
                } else {
                    if (callbackProcessor != null) {
                        callbackProcessor.execute("stop", new String[]{"0"});
                    }
                    isMaster = true; // Default
                }
                if (args.length == 0) {
                    System.out.println(threadName + " stopped");
                } else {
                    System.out.println(threadName + " stopped because " + args[0]);
                }
                break;

            case "ping":
                if (args.length != 2) throw new IllegalArgumentException("Ping requires start and end times");
                try {
                    int start = Integer.parseInt(args[0]);
                    int end = Integer.parseInt(args[1]);
                    if (callbackProcessor != null) {
                        final long delay = (start + end)/2;
                        String[] finalArgs = args;
                         new Thread(() -> {
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {}
                            try {
                                callbackProcessor.execute("ping", finalArgs);
                            } catch (Exception e) {
                                // Assume callback is dead
                                if (!isMaster) {
                                    stop();
                                } else {
                                    interrupt();
                                    try {
                                        execute("start", new String[]{masterName, slaveName});
                                    } catch (InterruptedException ex) {

                                    }
                                }
                            }
                        }).start();
                    }
                    System.out.println(threadName + " as " + (isMaster ? "master" : "slave") + " PING!");
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Ping arguments must be integers");
                }
                break;

            case "sleep":
                if (args.length != 1) throw new IllegalArgumentException("Sleep requires one argument");
                try {
                    int millis = Integer.parseInt(args[0]);
                    Thread.sleep(millis);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Sleep argument must be an integer");
                }
                break;

            case "run":
            case "start":
                String zero = "0";
                if (args.length == 0) {
                    if (isMaster){
                        callbackProcessor.execute("start", new String[]{threadName, zero});
                        if (!running) {
                            running = true;
                        }
                        break;
                    } else {
                        args = new String[]{threadName, zero};
                    }
                }
                if (args.length != 2) throw new IllegalArgumentException("Start requires master and slave names");
                if (zero.equals(args[0]) == zero.equals(args[1])) { // XOR
                    throw new IllegalArgumentException("One of master or slave name must be 0");
                }
                masterName = zero.equals(args[0]) ? threadName : args[0];
                slaveName = zero.equals(args[1]) ? threadName : args[1];
                isMaster = zero.equals(args[0]);
                if (isMaster && callbackProcessor != null) {
                    // Wait for callback to respond
                    Thread.sleep(1000); // Simplified wait
                    callbackProcessor.execute("ping", new String[]{"800", "1200"});
                } else if (!isMaster && callbackProcessor != null) {
                    callbackProcessor.execute("start", new String[]{zero, threadName});
                }
                if (!running) {
                    running = true;
                }
                break;

            case "interrupt":
                interrupt();
                if (args.length == 0) {
                    System.out.println(threadName + " interrupted");
                } else {
                    System.out.println(threadName + " interrupted because " + args[0]);
                }
                break;

            case "schedule":
                if (args.length < 2) throw new IllegalArgumentException("Schedule requires millis, command, and args");
                try {
                    int millis = Integer.parseInt(args[0]);
                    final String scheduledCommand = args[1];
                    final String[] scheduledArgs = new String[args.length - 2];
                    System.arraycopy(args, 2, scheduledArgs, 0, args.length - 2);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {}
                        try {
                            execute(scheduledCommand, scheduledArgs);
                        } catch (Exception e) {

                        }
                    }).start();
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Schedule millis must be an integer");
                }
                break;

            default:
                wrappedProcessor.execute(command, args);
                break;
        }
    }

    /**
     * Retrieves the callback processor.
     *
     * @return the callback processor, or null if none is set
     */
    @Override
    public CommandProcessor getCallBackProcessor() {
        return callbackProcessor;
    }

    /**
     * Sets the callback processor, ensuring it is not the same as this instance.
     *
     * @param processor the callback processor to set
     * @throws IllegalArgumentException if processor is this instance
     */
    @Override
    public void setCallBackProcessor(CommandProcessor processor) throws IllegalArgumentException {
        if (this == processor) throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        this.callbackProcessor = processor;
    }

    /**
     * Checks if the thread is running.
     *
     * @return true if the thread is running, false otherwise
     */
    public boolean getState() {
        return running;
    }

    /**
     * Runs the thread, periodically sending ping commands to the callback processor.
     */
    @Override
    public void run() {
        running = true;
        try {
            execute("run");
        } catch (InterruptedException e) {}
    }

    /**
     * Interrupts the thread.
     */
    public void interrupt() {
        running = false;
    }

    /**
     * Stops the processor, toggling the master/slave role.
     */
    public void stop() {
        try {
            execute("stop", new String[]{});
        } catch (Exception e) {
            // Handle or log error
        }
    }

    /**
     * Sets the wrapped CommandProcessor.
     *
     * @param processor the CommandProcessor to wrap
     * @throws IllegalArgumentException if processor is null
     */
    public void setCommandProcessor(CommandProcessor processor) {
        if (processor == null) throw new IllegalArgumentException("Wrapped processor cannot be null");
        this.wrappedProcessor = processor;
    }

    /**
     * Gets the wrapped CommandProcessor.
     *
     * @return the wrapped CommandProcessor
     */
    public CommandProcessor getCommandProcessor() {
        return wrappedProcessor;
    }

    public static void main(String[] args){
        ThreadedCommandProcessor master = new ThreadedCommandProcessor(new CommandProcessor() {
            @Override
            public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
                System.out.println("master execution");
            }
        });
        ThreadedCommandProcessor slave = new ThreadedCommandProcessor(new CommandProcessor() {
            @Override
            public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
                System.out.println("slave execution");
            }
        });
        master.setCallBackProcessor(slave);
        slave.setCallBackProcessor(master);
        master.run();
    }
}
