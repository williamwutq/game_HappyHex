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

import comm.CommandProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * A command processor that communicates with a Python subprocess.
 * <p>
 * This class launches a Python process using the provided script path and enables two-way
 * communication with it via standard input and output streams. Commands and arguments are
 * written to the Python process's input stream, and any response from the output stream is
 * interpreted as a new command to be handled by the callback processor.
 * <p>
 * The processor listens asynchronously for output from the Python subprocess. Each line of
 * output is treated as a command string and passed to the callback processor for handling.
 * If the callback throws an {@link IllegalArgumentException}, it is ignored. If it throws
 * an {@link InterruptedException}, the Python process is terminated and the callback processor
 * is interrupted via interruption by sending the {@code "interrupt"} command.
 * <p>
 * The processor is closable via the {@link AutoCloseable} interface. When closing, it shuts down
 * the python process and its callback processor.
 *
 * <p>Requirements enforced by this implementation:
 * <ul>
 *   <li>The callback processor must not be {@code null} when {@link #execute(String, String[])}
 *   is called, or an {@link IllegalStateException} is thrown.</li>
 *   <li>The Python process is terminated if the callback throws {@link InterruptedException} or
 *   if the process ends naturally.</li>
 * </ul></p>
 * @version 2.0
 * @author William Wu
 * @since 1.4
 */
public class PythonCommandProcessor implements CommandProcessor, AutoCloseable {
    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private volatile CommandProcessor callbackProcessor;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Thread outputListenerThread;

    /**
     * Constructs a new {@code PythonCommandProcessor}, starting a Python 3 process using the
     * given script path. The Python process must support receiving commands on standard input
     * and producing results on standard output, one line at a time.
     *
     * @param pathToPythonScript the path to the Python script to run
     * @throws IOException if the process cannot be started
     */
    public PythonCommandProcessor(String pathToPythonScript) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python3", pathToPythonScript);
        this.process = pb.start();
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        this.outputListenerThread = new Thread(this::listenForOutput);
        this.outputListenerThread.setDaemon(true);
        this.outputListenerThread.start();
    }
    /**
     * Constructs a new {@code PythonCommandProcessor}, starting a Python 3 process using the
     * given script path and additional arguments. The Python process must support receiving
     * commands on standard input and producing results on standard output, one line at a time.
     *
     * @param pathToPythonScript the path to the Python script to run
     * @param args additional arguments to pass to the Python script
     * @throws IOException if the process cannot be started
     */
    public PythonCommandProcessor(String pathToPythonScript, String[] args) throws IOException {
        String[] command = new String[args.length + 2];
        command[0] = "python3";
        command[1] = pathToPythonScript;
        System.arraycopy(args, 0, command, 2, args.length);
        ProcessBuilder pb = new ProcessBuilder(command);
        this.process = pb.start();
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        this.outputListenerThread = new Thread(this::listenForOutput);
        this.outputListenerThread.setDaemon(true);
        this.outputListenerThread.start();
    }

    /**
     * Continuously listens for output from the Python process and forwards it to the
     * callback processor as a command string.
     */
    private void listenForOutput() {
        try {
            String line;
            CommandProcessor processor = this.getCallBackProcessor();
            while ((line = reader.readLine()) != null) {
                final String received = line.trim();
                if (processor != null && !received.isEmpty()) {
                    try {
                        executor.submit(() -> {
                            try {
                                processor.execute(received);
                            } catch (IllegalArgumentException ignored) {
                            } catch (InterruptedException e) {
                                processorInterrupted(processor);
                            }
                        });
                    } catch (RejectedExecutionException ignored) {
                        processorInterrupted(processor);
                    }
                }
            }
        } catch (IOException e) {
            // Handle stream closure or process termination silently
        } finally {
            CommandProcessor processor = this.getCallBackProcessor();
            if (processor != null) {
                processorInterrupted(processor);
            }
        }
    }

    /**
     * Terminates the Python process and interrupts the callback processor if supported.
     *
     * @param processor the callback processor to notify
     */
    private void processorInterrupted(CommandProcessor processor) {
        process.destroy();
        try {
            processor.execute("interrupt");
        } catch (InterruptedException | IllegalArgumentException ignored) {
        }
    }
    /**
     * Sends a command and its arguments to the Python process through its standard input.
     * Requires a non-null callback processor.
     *
     * @param command the command to send
     * @param args the arguments to send with the command
     * @throws IllegalStateException if no callback processor is set
     * @throws IllegalArgumentException if command is invalid
     * @throws InterruptedException if the thread is interrupted during execution
     */
    @Override
    public void execute(String command, String[] args) throws IllegalArgumentException, InterruptedException {
        if (command.equals("interrupt")){
            process.destroy();
        } else if (command.equals("kill")){
            close();
        }
        if (callbackProcessor == null) throw new IllegalStateException("Callback processor must not be null");
        try {
            synchronized (writer) {
                writer.write(command);
                for (String arg : args) {
                    writer.write(" ");
                    writer.write(arg);
                }
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to send command to Python process or command is invalid because " + e.getMessage());
        }
    }

    /**
     * Returns the current callback processor.
     *
     * @return the current callback processor, or {@code null} if none is set
     */
    @Override
    public CommandProcessor getCallBackProcessor() {
        return callbackProcessor;
    }
    /**
     * Sets the callback processor. Must not be the same instance as this processor.
     *
     * @param processor the callback processor to use
     * @throws IllegalArgumentException if the processor is the same as this instance
     */
    @Override
    public void setCallBackProcessor(CommandProcessor processor) {
        if (this == processor) throw new IllegalArgumentException("Cannot add instance processor itself as callback processor");
        this.callbackProcessor = processor;
    }

    /**
     * Closes the processor, terminating the Python process and shutting down background tasks.
     * This functions that same as {@link #execute(String) execute("kill")};
     */
    public void close() {
        executor.shutdownNow();
        try {
            writer.close();
        } catch (IOException ignored) {}
        try{
            reader.close();
        } catch (IOException ignored) {}
        process.destroy();
    }
}