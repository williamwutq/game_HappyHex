# Copied from HappyHex machine learning project.
# Copyrighted by contributors to github.com/williamwutq/hpyhexml.
# Distributed under the MIT License.

import keras
from ..hex import HexEngine, Piece, Hex
import tensorflow as tf
import numpy as np
from .. import ml_hex as hx
from .hexcnn import HexDynamicConv, HexConv

keras.config.enable_unsafe_deserialization()

def predict_data(model: keras.Model, engine: HexEngine, queue: list[Piece]) -> tuple[int, Hex]:
    """
    Predict the output for a given engine and queue using the trained model.

    The model should output a flattened array of shape (num_blocks * queue_size,).
    Each index corresponds to a piece in the queue and a block in the engine.
    The value at each index represents the predicted score for placing that piece in that block.

    Parameters:
        model (keras.Model): The trained Keras model for prediction.
        engine (HexEngine): The HexEngine instance representing the current state of the game.
        queue (list[Piece]): A list of Piece instances representing the queue of pieces.
    Returns:
        tuple[int, Hex]: A tuple containing the index of the piece in the queue and the predicted Hex coordinate.
    """
    input_data = hx.flatten_engine(engine) + hx.flatten_queue(queue)
    input_array = np.array(input_data)
    input_array = input_array.reshape(1, -1)  # Reshape to add batch dimension
    prediction = model(input_array, training=False).numpy()  # Call the model directly instead of using predict to avoid creation of new tensors
    # Cast it to a float array
    prediction = prediction.astype(float)[0]
    # Check the shape of the prediction, it should be num_blocks * queue_size
    if prediction.shape != (len(engine) * len(queue),):
        raise ValueError(f"Prediction shape mismatch: expected {(len(engine) * len(queue),)}, got {prediction.shape}. Did you forget to flatten the output?")
    engine_length = len(engine)
    # Set impossible to -1
    for idx, piece in enumerate(queue):
        possible = engine.check_positions(piece)
        for i in range(engine_length):
            if not engine.coordinate_block(i) in possible:
                prediction[i + engine_length * idx] = -1.0
    # Find the index of the maximum value in the prediction
    max_index = int(np.argmax(prediction)) # Get the index of the maximum value as a python integer
    result_piece = max_index // engine_length  # Determine which piece in the queue it corresponds to
    result_hex = engine.coordinate_block(max_index % engine_length)  # Determine the Hex coordinate from the index
    return (result_piece, result_hex)

def create_model_predictor(model_path: str, func_name: str) -> callable:
    """
    Dynamically create a named prediction function that loads a model and calls predict_data.

    Model is loaded at creation time.

    The function created will follow the algorithm signature:
    ```python
    def algorithm(engine: HexEngine, queue: list[Piece]) -> tuple[int, Hex]:
    ```
    
    Parameters:
        model_path (str): Path to the Keras model file.
        func_name (str): Desired function name (string).
    Returns:
        function: A named function that can be used as a predictor.
    Raises:
        TypeError: If model_path or func_name is not a string.
        ValueError: If func_name is an empty string.
    """
    if not isinstance(model_path, str):
        raise TypeError("model_path must be a string")
    if not isinstance(func_name, str):
        raise TypeError("func_name must be a string")
    func_name = func_name.strip()
    if not func_name:
        raise ValueError("func_name cannot be an empty string")
    model = keras.models.load_model(model_path, custom_objects={"HexDynamicConv": HexDynamicConv, "HexConv": HexConv})
    model.call = tf.function(model.call, reduce_retracing=True) # Reduce retracing to avoid memory leaks

    def predictor(engine, queue):
        try:
            return predict_data(model, engine, queue)
        except Exception as e:
            raise ValueError(f"Prediction failed because {e}")

    # Assign function name dynamically
    predictor.__name__ = func_name

    return predictor