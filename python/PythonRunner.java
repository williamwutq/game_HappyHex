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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * The {@code PythonRunner} class is a simple Java utility that launches and executes
 * a Python script using the system's Python interpreter.
 * <p>
 * This class demonstrates how to:
 * <ul>
 *     <li>Use {@link ProcessBuilder} to run an external Python script</li>
 *     <li>Redirect and read the script's standard output and error streams</li>
 *     <li>Wait for the script to complete and capture its exit code</li>
 * </ul>
 *
 * <p><b>Note:</b> This assumes the presence of a valid Python 3 interpreter named
 * {@code python3} in the system's PATH and a script located at {@code python/main.py}.
 */
public class PythonRunner {
    /**
     * Main entry point for the {@code PythonRunner} class.
     * <p>
     * It attempts to execute a Python script and prints all the output from the script
     * to the console. If the script completes successfully, it prints the exit code.
     * If there is an error during execution, it prints the stack trace.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args){
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "python/main.py", "start", "Java", "0");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read the output
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to exit
            int exitCode = process.waitFor();
            System.out.println("Python process finished with exit code " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
