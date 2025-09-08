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

import io.GameTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
 *
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class PythonEnvsChecker {
    /**
     * A record class to document the information of a machine model that can run HappyHex.
     * This class contains the name, path, framework, engine type, and queue type of the model.
     * <p>
     * The name is the identifier of the model, the path is the file path to the model,
     * the engine is an integer representing the specific engine radius and queue size supported by the model,
     * and the specific framework is the machine learning framework used to create the model.
     * <p>
     * For queue size, if the value is null, it means the model support all queue sizes.
     * For engine radius, if the value is null, it means the model supports all engine radii down to 2.
     * <p>
     * This is a record class, getters are the same name as the fields. This class is immutable and all objects
     * are generated in advance. The class is only used inside the PythonEnvsChecker class to store model information.
     */
    private record ModelInformation(String name, String path, String frameWork, Integer engine, Integer queue){
    }
    private static final String[] KEYS = {
        "Installed", "Error", "Available", "Unavailable"
    };
    private static final String CHECKER_SCRIPT = "python/envs.py";
    private static final List<ModelInformation> supportedModels = Arrays.asList(new ModelInformation[]{
            new ModelInformation("CNN_1", "hex_tensorflow_cnn_5_3_stack_1.keras", "tf", 5, 3),
            new ModelInformation("CNN_4", "hex_tensorflow_cnn_5_3_stack_4.keras", "tf", 5, 3),
            new ModelInformation("CNN_40", "hex_tensorflow_cnn_5_3_stack_4_refined_0.keras", "tf", 5, 3),
            new ModelInformation("CNN_41", "hex_tensorflow_cnn_5_3_stack_4_refined_1.keras", "tf", 5, 3),
    });
    private static final List<ModelInformation> availableModels = Collections.synchronizedList(new ArrayList<ModelInformation>());
    private static volatile boolean isTorchAvailable = false;
    private static volatile boolean isTensorFlowAvailable = false;
    private static volatile boolean isTorchGPUAvailable = false;
    private static volatile boolean isTensorFlowGPUAvailable = false;
    private static volatile boolean isMLAvailable = false;
    private static volatile boolean isGPUAvailable = false;
    private PythonEnvsChecker() {
        // Prevent instantiation
    }
    /**
     * Injects a new model into the supported models list.
     * This method allows adding new models dynamically to the list of supported models.
     * It checks for the validity of the framework, engine, and queue parameters before adding.
     * If a model with the same name or path already exists, it will not be added again.
     * <p>
     * Although this method allows adding new models, it does not guarantee that those models will be added
     * to the available models list. To update the available models list, call {@link #updateAvailableModels()}.
     * <p>
     * Only valid models that exist as files in the "python/models/" directory will be added to the available models list.
     * In a compiled evironment, this means open the application folder, navigate to "python/models/" and place the model files there.
     *
     * @param name      The name of the model.
     * @param path      The file path to the model.
     * @param framework The machine learning framework used by the model ("tf" for TensorFlow, "torch" for PyTorch).
     * @param engine    The engine radius supported by the model. If null, supports all engine radii down to 2.
     * @param queue     The queue size supported by the model. If null, supports all queue sizes.
     * @return true if the model was successfully added, false if it already exists or parameters are invalid.
     * @throws IllegalArgumentException if the framework is invalid or if engine/queue values are out of bounds.
     */
    public static boolean injectModel(String name, String path, String framework, Integer engine, Integer queue) {
        ModelInformation model = new ModelInformation(name, path, framework, engine, queue);
        // Check framework validity
        if (!framework.equals("tf") && !framework.equals("torch")) {
            throw new IllegalArgumentException("Invalid framework " + framework);
        }
        // Check engine and queue validity
        if (engine != null && engine < 2) {
            throw new IllegalArgumentException("Engine radius must be at least 2");
        }
        if (queue != null && queue < 1) {
            throw new IllegalArgumentException("Queue size must be at least 1");
        }
        // Check if model already exists
        for (ModelInformation m : supportedModels) {
            if (m.name().equals(name) || m.path().equals(path)) {
                return false; // Model already exists
            }
        }
        return supportedModels.add(model);
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
     * Checks if TensorFlow with GPU support is available.
     * This method checks if TensorFlow is installed and if it has GPU support.
     * It only returns true if TensorFlow is installed can access a GPU device.
     * @return true if TensorFlow with GPU support is available, false otherwise.
     */
    public static boolean checkTensorFlowGPU() {
        String result = executePython(CHECKER_SCRIPT, "gpu tf", KEYS);
        if ("Unavailable".equals(result)) {
            return false;
        } else if ("Available".equals(result)) {
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
     * Checks if PyTorch with GPU support is available.
     * This method checks if PyTorch is installed and if it has GPU support.
     * It only returns true if PyTorch is installed can access a GPU device.
     * @return true if PyTorch with GPU support is available, false otherwise.
     */
    public static boolean checkTorchGPU() {
        String result = executePython(CHECKER_SCRIPT, "gpu torch", KEYS);
        if ("Unavailable".equals(result)) {
            return false;
        } else if ("Available".equals(result)) {
            return true;
        }
        return false;
    }
    /**
     * Asynchronously checks and installs TensorFlow if not present.
     * @return A Future representing the pending completion of the task, with a Boolean result
     */
    public static CompletableFuture<Boolean> checkAndInstallTensorFlowAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(PythonEnvsChecker::checkAndInstallTensorFlow);
    }
    /**
     * Asynchronously checks and installs PyTorch if not present.
     * @return A Future representing the pending completion of the task, with a Boolean result
     */
    public static CompletableFuture<Boolean> checkAndInstallTorchAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(PythonEnvsChecker::checkAndInstallTorch);
    }
    /**
     * Asynchronously checks for TensorFlow GPU support.
     * @return A Future representing the pending completion of the task, with a Boolean result
     */
    public static CompletableFuture<Boolean> checkTensorFlowGPUAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(PythonEnvsChecker::checkTensorFlowGPU);
    }
    /**
     * Asynchronously checks for PyTorch GPU support.
     * @return A Future representing the pending completion of the task, with a Boolean result
     */
    public static CompletableFuture<Boolean> checkTorchGPUAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(PythonEnvsChecker::checkTorchGPU);
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
                    System.out.println(GameTime.generateSimpleTime() + " Hpyhexml (Python): " + line);
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
    /**
     * Checks if a specific machine learning model is available as a file.
     * This method can be used to verify the presence of models such "hex_torch_5_n_large.pth" or "hex_tensorflow_5_3_large.keras".
     *
     * @param model The information of the model to check.
     *              The model information should include a valid path.
     * @return true if the model is available, false otherwise.
     */
    private static boolean isModelAvailable(ModelInformation model) {
        java.io.File modelFile = new java.io.File("python/models/" + model.path());
        return modelFile.exists() && modelFile.isFile();
    }
    /**
     * Update the list of available machine learning models that can run HappyHex.
     * This method checks the availability of each model defined in supportedModels.
     */
    public static void updateAvailableModels() {
        availableModels.clear();
        for (ModelInformation model : supportedModels) {
            if (isModelAvailable(model)) {
                availableModels.add(model);
            }
        }
    }

    /**
     * Runs the PythonEnvsChecker to check and install machine learning environments.
     * This method will start checking for TensorFlow and PyTorch asynchronously.
     * It will also update the list of available models that can run HappyHex.
     * <p>
     * This method should be called at the start of the application to ensure that the machine learning
     * environments are checked and installed before any model is used.
     */
    public static void run() {
        // Start futures
        CompletableFuture<Boolean> tfFuture = checkAndInstallTensorFlowAsync();
        CompletableFuture<Boolean> torchFuture = checkAndInstallTorchAsync();
        // When either completes with true, set isMLAvailable
        tfFuture.thenAccept(result -> {
            if (result) {
                isTensorFlowAvailable = true;
                isMLAvailable = true;
            }
            CompletableFuture<Boolean> tfGPUFuture = checkTensorFlowGPUAsync();
            tfGPUFuture.thenAccept(gpuResult -> {
                if (gpuResult) {
                    isTensorFlowGPUAvailable = true;
                    isGPUAvailable = true;
                }
            });
        });
        torchFuture.thenAccept(result -> {
            if (result) {
                isTorchAvailable = true;
                isMLAvailable = true;
            }
            CompletableFuture<Boolean> torchGPUFuture = checkTorchGPUAsync();
            torchGPUFuture.thenAccept(gpuResult -> {
                if (gpuResult) {
                    isTorchGPUAvailable = true;
                    isGPUAvailable = true;
                }
            });
        });
        // Call updateAvailableModels to refresh the list of available models
        updateAvailableModels();
    }
    /**
     * Checks if machine learning environments are available.
     * This method will return true if either TensorFlow or PyTorch is installed and available, and a machine learning model is present.
     * This method does not guarantee that the model will run correctly with the installed libraries.
     *
     * @return true if at least one of the machine learning libraries is available, false otherwise.
     */
    public static boolean isMLAvailable() {
        return isMLAvailable;
    }
    /**
     * Checks if a GPU is available for machine learning tasks.
     * This method will return true if either TensorFlow or PyTorch with GPU support is available.
     *
     * @return true if a GPU is available, false otherwise.
     */
    public static boolean isGPUAvailable() {
        return isGPUAvailable;
    }
    /**
     * Returns the names of all available machine learning models that can run HappyHex.
     * The models are defined in the supportedModels array and checked for availability.
     * <p>
     * The data contained here maybe out of date.
     *
     * @return An array of strings containing the names of available models.
     */
    public static String[] availableModels() {
        String[] modelNames = new String[availableModels.size()];
        for (int i = 0; i < availableModels.size(); i++) {
            modelNames[i] = availableModels.get(i).name();
        }
        return modelNames;
    }
    /**
     * Checks if a specific model can run with the available machine learning environments.
     * This method checks against the list of available models that have been updated.
     *
     * @param modelName The name of the model to check.
     * @return true if the model is available and can run, false otherwise.
     */
    public static boolean canRunModel(String modelName) {
        if (!isMLAvailable) {
            return false; // No ML environment available
        }
        for (ModelInformation model : availableModels) {
            if (model.name().equals(modelName)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if a model can run with the specified engine radius and queue size.
     * This method checks against the list of available models that have been updated.
     * <p>
     * If the model's engine or queue is null, it means the model supports all engine radii or queue sizes respectively.
     *
     * @param engineRadius The engine radius to check.
     * @param queueSize    The queue size to check.
     * @return true if a model can run with the specified parameters, false otherwise.
     */
    public static boolean canRunModel(int engineRadius, int queueSize) {
        if (!isMLAvailable) {
            return false; // No ML environment available
        }
        for (ModelInformation model : availableModels) {
            if ((model.engine() == null || model.engine() == engineRadius)
             && (model.queue() == null || model.queue() == queueSize)
             && (model.frameWork().equals("tf") && isTensorFlowAvailable || model.frameWork().equals("torch") && isTorchAvailable)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns the names of all runnable models that can run with the specified engine radius and queue size.
     * This method checks against the list of available models that have been updated.
     * <p>
     * If the model's engine or queue is null, it means the model supports all engine radii or queue sizes respectively.
     *
     * @param engineRadius The engine radius to check.
     * @param queueSize    The queue size to check.
     * @return An array of strings containing the names of runnable models.
     */
    public static String[] runnableModels(int engineRadius, int queueSize) {
        if (!isMLAvailable) {
            return new String[0]; // No ML environment available
        }
        List<String> runnableModels = new ArrayList<>();
        for (ModelInformation model : availableModels) {
            if ((model.engine() == null || model.engine() == engineRadius)
                && (model.queue() == null || model.queue() == queueSize)
                && (model.frameWork().equals("tf") && isTensorFlowAvailable || model.frameWork().equals("torch") && isTorchAvailable)) {
                runnableModels.add(model.name());
            }
        }
        return runnableModels.toArray(new String[0]);
    }
    /**
     * Returns the file path of a specific model that can run HappyHex.
     * This method checks against the list of available models that have been updated.
     * <p>
     * However, because the availability of models is always outdated, this method may not reflect the actual availability of the model.
     *
     * @param modelName The name of the model to get the path for.
     * @return The file path of the model, or null if the model is not found.
     */
    public static String getModelPath(String modelName) {
        for (ModelInformation model : availableModels) {
            if (model.name().equals(modelName)) {
                return "python/models/" + model.path();
            }
        }
        return null; // Model not found
    }
    /**
     * Returns the name of a specific model given its file path.
     * This method checks against the list of available models that have been updated.
     * <p>
     * However, because the availability of models is always outdated, this method may not reflect the actual availability of the model.
     *
     * @param modelPath The file path of the model to get the name for.
     * @return The name of the model, or null if the model is not found.
     */
    public static String getModelName(String modelPath) {
        for (ModelInformation model : availableModels) {
            if (model.path().equals(modelPath)) {
                return model.name();
            }
        }
        return null; // Model not found
    }
    /**
     * Returns the machine learning framework used by a specific model.
     * This method checks against the list of supported models.
     *
     * @param modelName The name of the model to get the framework for.
     * @return The framework of the model ("tf" for TensorFlow, "torch" for PyTorch), or null if the model is not found.
     */
    public static String getModelFramework(String modelName) {
        for (ModelInformation model : supportedModels) {
            if (model.name().equals(modelName)) {
                return model.frameWork();
            }
        }
        return null; // Model not found
    }
}
