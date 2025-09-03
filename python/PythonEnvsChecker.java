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

import java.util.concurrent.Future;

/**
 * Java end of Python machine learning environments checker.
 * <p>
 * This class is responsible for checking the availability of python machine learning
 * environments required by the application, particularly TensorFlow and PyTorch.
 * It does so by executing a Python script that attempts to import these libraries
 * and reports back their availability.
 * <p>
 * In addition, if needed, this class can also handle the installation of these
 * libraries using pip, ensuring that the required environments are set up correctly.
 */
public class PythonEnvsChecker {
    private static final String[] KEYS = {
        "Installed", "Error"
    };
    private static final String CHECKER_SCRIPT = "python/envs.py";
    private PythonEnvsChecker() {
        // Prevent instantiation
    }

    /**
     * Checks if TensorFlow is installed in the Python environment.
     * If not installed, it attempts to install it.
     *
     * @return true if TensorFlow is installed or successfully installed, false otherwise.
     */
    public static boolean checkAndInstallTensorFlow() {
        String result = executePython(CHECKER_SCRIPT, "tf", KEYS);
        if ("Error".equals(result)) {
            return false;
        } else if ("Installed".equals(result)) {
            return true;
        }
        return false;
    }
    /**
     * Checks if PyTorch is installed and attempts to install it if not.
     *
     * @return true if PyTorch is installed or was successfully installed, false otherwise.
     */
    public static boolean checkAndInstallTorch() {
        String result = executePython(CHECKER_SCRIPT, "torch", KEYS);
        if ("Error".equals(result)) {
            return false;
        } else if ("Installed".equals(result)) {
            return true;
        }
        return false;
    }
    /**
     * Asynchronously checks and installs TensorFlow if not present.
     * @return A Future representing the pending completion of the task, with a Boolean result
     */
    public static Future<Boolean> checkAndInstallTensorFlowAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(PythonEnvsChecker::checkAndInstallTensorFlow);
    }
    /**
     * Asynchronously checks and installs PyTorch if not present.
     * @return A Future representing the pending completion of the task, with a Boolean result
     */
    public static Future<Boolean> checkAndInstallTorchAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(PythonEnvsChecker::checkAndInstallTorch);
    }
    /**
     * Executes a Python script and listens for its outputs.
     * If any of the specified termination keys are detected in the output, the execution is terminated.
     * Returns the key that caused the termination, or null if the script completed without triggering any termination keys.
     * <p>
     * The method is blocking and will wait for the script to complete or be terminated.
     *
     * @param scriptPath      The path to the Python script to be executed.
     * @param terminationKeys An array of strings that, if found in the output, will cause the execution to terminate.
     * @return The termination key that caused the script to stop, or null if it completed normally.
     */
    public static String executePython(String scriptPath, String arg, String[] terminationKeys) {
        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, arg);
        processBuilder.redirectErrorStream(true);
        String terminationKey = null;
        try {
            Process process = processBuilder.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                boolean terminated = false;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    for (String key : terminationKeys) {
                        if (line.contains(key)) {
                            process.destroy();
                            terminated = true;
                            terminationKey = key;
                            break;
                        }
                    }
                    if (terminated) {
                        break;
                    }
                }
            }
            process.waitFor();
        } catch (Exception ignored) {
        }
        return terminationKey;
    }
}
