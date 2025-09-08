'''
A module providing utility functions for machine learning models in Hex. This can be viewed as an extension to the `hex` module.
This module is extracted from the HappyHex machine learning project, and is copyrighted by its contributors.
For the original source, visit: github.com/williamwutq/hpyhexml
In the original source, this is named as 'hex.py'
'''

from hex import HexEngine, Piece, Hex
from random import random, shuffle
from math import exp

__all__ = ['sigmoid_like', 'non_negative', 'non_positive', 'softmax_rank_score',
           'flatten_engine', 'flatten_queue', 'flatten_piece',
           'flatten_single_desired', 'label_single_desired',]

def gaussian(x, c=1.0):
    '''
    Simplified Gaussian function.

    Parameters:
        x (float): The input value to evaluate.
        c (float): The inverse of standard deviation of the Gaussian. Default is 1.0.
    Returns:
        float: The output value, which is always between 0.0 and 1.0, and approaches 0 as x increases.
    '''
    return exp(-(x * c) ** 2)

def sigmoid_like(x, k=1.0, p=2.0):
    '''
    Smooth sigmoid-like decay function.

    Parameters:
        x (float): The input value to evaluate.
        k (float): The steepness of the curve. Default is 1.0.
        p (float): The power of the decay. Default is 2.0.
    Returns:
        float: The output value, which is between 0.0 and 1.0 and approaches 0 as x increases.
    '''
    return 1.0 / (1.0 + (k * x) ** p)

def non_negative(x):
    '''
    An activation function that activates if x is greater than or equal to 0.
    Returns 1.0 if x is greater than or equal to 0, otherwise returns 0.0.

    Use of this function in a machine learning model is discouraged.

    Use of this function for indexing can guarantee that the output is a non-negative number, but it might be floating point.

    Parameters:
        x (float): The input value to evaluate.
    Returns:
        float: 1.0 if x >= 0, otherwise 0.0.
    '''
    if (x >= 0.0):
        return 1.0
    else:
        return 0.0

def non_positive(x):
    '''
    An activation function that activates if x is less than or equal to 0.
    Returns 1.0 if x is less than or equal to 0, otherwise returns 0.0.

    Use of this function in a machine learning model is discouraged.

    Use of this function for indexing cannot guarantee that the output is one-hot encoded.

    Parameters:
        x (float): The input value to evaluate.
    Returns:
        float: 1.0 if x <= 0, otherwise 0.0.
    '''
    if (x <= 0.0):
        return 1.0
    else:
        return 0.0

def softmax_rank_score(rank, length) -> float:
    '''
    Compute the soft label score for a given rank position in categorical cross-entropy.

    This function is guaranteed to be able to return values that add up to 1.0, if the input and iteration process are correct.

    Scoring Rules:
    - Rank 0 gets the highest score.
    - Each subsequent rank receives half the score of the previous rank (score ∝ 2^-rank-1).
    - The last two ranks are treated equally, sharing the same score.
    - All scores across ranks sum to 1, suitable for soft categorical cross-entropy targets.

    Parameters:
        rank (int): The rank position of the piece, starting from 0.
        length (int): The total number of pieces in the engine.
    Returns:
        score (float): The score for the given rank position.
    Raises:
        ValueError: If length is 0, or if rank is larger than or equal to length.
    '''
    if length == 0:
        raise ValueError("Length cannot be 0")
    elif length == 1:
        return 1.0
    elif rank >= length:
        raise ValueError("Rank cannot be larger than or equal to length")
    elif rank == length - 1:
        return 2 ** -rank
    else:
        return 2 ** (-rank - 1)

def gaussian_softmax_rank_score(rank, length):
    '''
    Use gaussian to compute the soft label score for a given rank position in categorical cross-entropy.

    This function is guaranteed to be able to return values that add up to 1.0, if the input and iteration process are correct.

    Scoring Rules:
    - Rank 0 gets the highest score.
    - Each subsequent rank receives a score based on the Gaussian function.

    Parameters:
        rank (int): The rank position of the piece, starting from 0.
        length (int): The total number of pieces in the engine.
    Returns:
        score (float): The score for the given rank position.
    Raises:
        ValueError: If length is 0, or if rank is larger than or equal to length.
    '''
    if length == 0:
        raise ValueError("Length cannot be 0")
    elif rank >= length:
        raise ValueError("Rank cannot be larger than or equal to length")
    # Compute unnormalized Gaussian scores for all ranks
    scores = [exp(-r ** 2) for r in range(length)]

    # Normalize scores to sum up to 1
    total = sum(scores)
    return scores[rank] / total

def flatten_engine(engine: HexEngine) -> list[float]:
    '''
    (**Input**) Flatten a HexEngine instance into a list of floats, where each float represents the occupancy state of engine blocks.s

    Parameters:
        piece (Piece): The HexEngine instance to flatten.
    Returns:
        vector (list[float]): A list of floats representing the engine. They are either 0.0 or 1.0.
    Raises:
        TypeError: If the engine is not an instance of HexEngine.
    '''
    if not isinstance(engine, HexEngine):
        raise TypeError("engine must be an instance of HexEngine")
    return [float(b) for b in engine] # Use the HexEngine's iterator, which returns booleans

def flatten_queue(queue: list[Piece]) -> list[float]:
    '''
    (**Input**) Flatten the queue of pieces into a list of floats.

    Parameters:
        queue (list[Piece]): A list of Piece instances to flatten.
    Returns:
        vector (list[float]): A list of floats representing the queue. They are either 0.0 or 1.0.
    Raises:
        TypeError: If the queue is not a list of Piece instances.
    '''
    if not isinstance(queue, list) or not all(isinstance(piece, Piece) for piece in queue):
        raise TypeError("queue must be a list of Piece instances")
    array = []
    for piece in queue:
        array.extend(float(b) for b in piece)
    return array

def flatten_piece(piece: Piece) -> list[float]:
    '''
    (**Input**) Flatten a single Piece into a list of floats.

    Parameters:
        piece (Piece): The Piece instance to flatten.
    Returns:
        vector (list[float]): A list of floats representing the Piece. They are either 0.0 or 1.0.
    Raises:
        TypeError: If the piece is not an instance of Piece.
    '''
    if not isinstance(piece, Piece):
        raise TypeError("piece must be an instance of Piece")
    return [float(b) for b in piece] # Use the Piece's iterator, which returns booleans

def flatten_single_desired(engine: int | HexEngine, desired: list[tuple[int, Hex]],
                           descend = lambda x: x,
                           swap_noise: float = 0.0, score_noise: float = 0.0
                           ) -> list[float]:
    '''
    (**Output**) Flatten a single desired output into a list of floats.

    This function does not guarantees that the output is one-hot encoded nor vector sum to 1.

    Parameters:
        engine (int | HexEngine): The HexEngine instance to get the size or the engine radius. It will not be modified.
        desired (list[tuple[int, Hex]]): A list of tuples containing piece indices and Hex positions.
        decend (callable): A function to calculate the score based on the index.
        swap_noise (float): Probability of swapping adjacent items in the desired list.
        score_noise (float): Noise factor to apply to the score.
    Returns:
        vector (list[float]): A list of floats representing the desired output.
    Raises:
        TypeError: If the engine is not an instance of HexEngine or desired is not a tuple of (int, Hex).
        ValueError: If the Hex position is invalid for the given piece index.
    '''
    if isinstance(engine, int):
        # If engine is an integer, create a HexEngine with that radius
        engine = HexEngine(engine)
    elif not isinstance(engine, HexEngine):
        raise TypeError("engine must be an instance of HexEngine or an integer representing the radius")
    if not isinstance(desired, list) or not all(isinstance(d, tuple) and len(d) == 2 for d in desired):
        raise TypeError("desired must be a list of tuples of (piece_index, Hex)")
    # Create an empty array filled with zeros
    output = [0.0] * len(engine)
    # Swap is noise is introduced
    if swap_noise > 0.0:
        for index in range(len(desired)):
            if index == 0:
                continue
            if random() < swap_noise:
                # Swap this item with the previous one
                desired[index], desired[index - 1] = desired[index - 1], desired[index]
    for index, (piece_index, coord) in enumerate(desired):
        # Calculate the score
        score = descend(index) * (1.0 + score_noise * (random() - 0.5))
        # Get the index in the flattened array
        block_index = engine.index_block(coord)
        if block_index != -1:
            output[block_index] = score
        else:
            raise ValueError(f"Invalid Hex position {coord} for piece index {piece_index}.")
    return output

def flatten_multiple_desired(engine: int | HexEngine, queue: int | list[Piece],
                             desired: list[tuple[int, Hex]], descend = lambda x: x,
                             swap_noise: float = 0.0, score_noise: float = 0.0) -> list[float]:
    '''
    (**Output**) Flatten a multi-queue desired output into a list of floats.

    This function does not guarantees that the output is one-hot encoded nor vector sum to 1.

    Parameters:
        engine (int | HexEngine): The HexEngine instance to get the size or the engine radius. It will not be modified.
        queue (int | list[Piece]): The queue length or a list of Piece instances. If a list, it will be converted to its length.
        desired (list[tuple[int, Hex]]): A list of tuples containing piece indices and Hex positions.
        decend (callable): A function to calculate the score based on the index.
        swap_noise (float): Probability of swapping adjacent items in the desired list.
        score_noise (float): Noise factor to apply to the score.
    Returns:
        vector (list[float]): A list of floats representing the desired output. It will be of size len(engine) * len(queue_length).
    Raises:
        TypeError: If the engine is not an instance of HexEngine or an integer, or if queue is not a positive integer or a list of Piece instances.
        ValueError: If the Hex position is invalid for the given piece index.
    '''
    if isinstance(engine, int):
        # If engine is an integer, create a HexEngine with that radius
        engine = HexEngine(engine)
    elif not isinstance(engine, HexEngine):
        raise TypeError("engine must be an instance of HexEngine or an integer representing the radius")
    if not queue:
        raise TypeError("queue must be a positive integer representing the queue length or a list of Piece instances")
    elif isinstance(queue, list):
        queue = len(queue)
    elif not isinstance(queue, int) or queue < 0:
        raise TypeError("queue must be a positive integer representing the queue length or a list of Piece instances")
    if not isinstance(desired, list) or not all(isinstance(d, tuple) and len(d) == 2 for d in desired):
        raise TypeError("desired must be a list of tuples of (piece_index, Hex)")
    # Create an empty array filled with zeros
    engine_length = len(engine)
    output = [0.0] * (engine_length * queue)
    # Swap is noise is introduced
    if swap_noise > 0.0:
        for index in range(len(desired)):
            if index == 0:
                continue
            if random() < swap_noise:
                # Swap this item with the previous one
                desired[index], desired[index - 1] = desired[index - 1], desired[index]
    for index, (piece_index, coord) in enumerate(desired):
        # Calculate the score
        score = descend(index) * (1.0 + score_noise * (random() - 0.5))
        # Get the index in the flattened array
        block_index = engine.index_block(coord)
        if block_index != -1:
            output[block_index + engine_length * piece_index] = score
        else:
            raise ValueError(f"Invalid Hex position {coord} for piece index {piece_index}.")
    return output

def label_single_desired(engine: int | HexEngine, desired: tuple[int, Hex]) -> list[int]:
    '''
    (**Output**) Label a single desired output into a list of integers.

    This function guarantees that the output is one-hot encoded or an error is raised.

    Parameters:
        engine (HexEngine): The HexEngine instance to get the size or the the engine radius. It will not be modified.
        desired (tuple[int, Hex]): A tuple containing the piece index and Hex position.
    Returns:
        vector (list[int]): A list of integers representing the desired output.
    Raises:
        TypeError: If the engine is not an instance of HexEngine or desired is not a tuple of (int, Hex).
        ValueError: If the Hex position is invalid for the given piece index.
    '''
    if isinstance(engine, int):
        # If engine is an integer, create a HexEngine with that radius
        engine = HexEngine(engine)
    elif not isinstance(engine, HexEngine):
        raise TypeError("engine must be an instance of HexEngine or an integer representing the radius")
    if not isinstance(desired, tuple) or len(desired) != 2:
        raise TypeError("desired must be a tuple of (piece_index, Hex)")
    if not isinstance(desired[0], int) or not isinstance(desired[1], Hex):
        raise TypeError("desired must be a tuple of (int, Hex)")
    output = [0] * len(engine)
    piece_index, coord = desired
    block_index = engine.index_block(coord)
    if block_index != -1:
        output[block_index] = 1
    else:
        raise ValueError(f"Invalid Hex position {coord} for piece index {piece_index}.")
    return output

def label_multiple_desired(engine: int | HexEngine, queue: int | list[Piece], desired: tuple[int, Hex]) -> list[int]:
    '''
    (**Output**) Label a multi-queue desired output into a list of integers.

    This function guarantees that the output is one-hot encoded or an error is raised.

    Parameters:
        engine (int HexEngine): The HexEngine instance to get the size or the engine radius. It will not be modified.
        queue (int | list[Piece]): The queue length or a list of Piece instances. If a list, it will be converted to its length.
        desired (tuple[int, Hex]): A tuple containing the piece index and Hex position.
    Returns:
        vector (list[int]): A list of integers representing the desired output. It will be of size len(engine) * len(queue_length).
    Raises:
        TypeError: If the engine is not an instance of HexEngine or an integer, or if queue is not a positive integer or a list of Piece instances.
        ValueError: If the Hex position is invalid for the given piece index.
    '''
    if isinstance(engine, int):
        # If engine is an integer, create a HexEngine with that radius
        engine = HexEngine(engine)
    elif not isinstance(engine, HexEngine):
        raise TypeError("engine must be an instance of HexEngine or an integer representing the radius")
    if not queue:
        raise TypeError("queue must be a positive integer representing the queue length or a list of Piece instances")
    elif isinstance(queue, list):
        queue = len(queue)
    elif not isinstance(queue, int) or queue < 0:
        raise TypeError("queue must be a positive integer representing the queue length or a list of Piece instances")
    if not isinstance(desired, tuple) or len(desired) != 2:
        raise TypeError("desired must be a tuple of (piece_index, Hex)")
    if not isinstance(desired[0], int) or not isinstance(desired[1], Hex):
        raise TypeError("desired must be a tuple of (int, Hex)")
    output = [0] * (len(engine) * queue)
    piece_index, coord = desired
    block_index = engine.index_block(coord)
    if block_index != -1:
        output[block_index + len(engine) * piece_index] = 1
    else:
        raise ValueError(f"Invalid Hex position {coord} for piece index {piece_index}.")
    return output

def argument_queue(data: list[tuple[HexEngine, list[Piece], list[tuple[int, Hex]]]]) -> None:
    '''
    Argument the queue data by randomly shuffling the pieces in the queue but preserving the correspondence of piece index in the desired output.

    This function is in place and directly modify the data. Nothing is returned.

    Parameters:
        data (list[tuple[HexEngine, list[Piece], list[tuple[int, Hex]]]]): The training data to argument.
    Returns:
        None
    '''
    for engine, queue, options in data:
        n = len(queue)
        if n <= 1:
            continue
        # p[new_index] = old_index
        p = list(range(n))
        shuffle(p)
        # inv[old_index] = new_index
        inv = [0] * n
        for new_index, old_index in enumerate(p):
            inv[old_index] = new_index
        # Reorder queue in-place
        queue[:] = [queue[old_index] for old_index in p]
        # Remap options in-place
        for k in range(len(options)):
            old_i, coord = options[k]
            options[k] = (inv[old_i], coord)