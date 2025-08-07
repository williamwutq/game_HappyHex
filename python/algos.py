'''
A collection of algorithms for the HappyHex game.
This is a simplified duplication of the algos module from the hpyhex python package, found at:
github.com/williamwutq/hpyhexml/hpyhex/algos.py
'''

from hex import HexEngine, Piece, Hex
import random as rd
from math import exp

"""
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
"""

__all__ = ['random', 'first', 'nrminimax', 'nrsearch']

def random(engine: HexEngine, queue: list[Piece]) -> tuple[int, Hex]:
    '''
    A random algorithm that returns the first piece and position.
    This algorithm randomly selects a piece from the queue and a valid position for that piece.
    If no valid positions are found, it raises a ValueError.

    Parameters:
        engine (HexEngine): The HexEngine instance to use for checking valid positions.
        queue (list[Piece]): The list of pieces available to play.
    Returns:
        placement (tuple[int, Hex]): A tuple containing the index of the selected piece and the position (Hex coordinate) to place it.
    Raises:
        ValueError: If the queue is empty or no valid positions are found for the pieces in the queue.
    '''
    if not queue:
        raise ValueError("Queue is empty")
    else:
        options = []
        seen_pieces = {}
        for piece_index, piece in enumerate(queue):
            key = int(piece)
            if key in seen_pieces: continue
            seen_pieces[key] = piece_index
            for coord in engine.check_positions(piece):
                options.append((piece_index, coord))
        if not options:
            raise ValueError("No valid options found")
        return rd.choice(options)
    
def first(engine: HexEngine, queue: list[Piece]) -> tuple[int, Hex]:
    '''
    A first selection algorithm that returns the first valid piece and position.
    This algorithm checks the first piece in the queue and returns the first valid position for that piece.
    If no valid positions are found, it raises a ValueError.
    
    Parameters:
        engine (HexEngine): The HexEngine instance to use for checking valid positions.
        queue (list[Piece]): The list of pieces available to play.
    Returns:
        placement (tuple[int, Hex]): A tuple containing the index of the selected piece and the position (Hex coordinate) to place it.
    Raises:
        ValueError: If the queue is empty or no valid positions are found for the first piece in the queue.
    '''
    if not queue:
        raise ValueError("Queue is empty")
    result_index = 0
    for piece_index, piece in enumerate(queue):
        positions = engine.check_positions(piece)
        if positions:
            result_index = piece_index
            coord = positions[0]
            break
    else:
        raise ValueError("No valid options found")
    return (result_index, coord)

def nrminimax(engine: HexEngine, queue : list[Piece]) -> tuple[int, Hex]:
    '''
    A heuristic algorithm that selects the best piece and position based on the current entropy, and other indicators of the game state.
    
    Parameters:
        engine (HexEngine): The game engine.
        queue (list[Piece]): The queue of pieces available for placement.
    Returns:
        placement (tuple[int, Hex]): A tuple containing the index of the best piece and the best position to place it.
    Raises:
        ValueError: If the queue is empty or no valid positions are found for the pieces in the queue.
    '''
    options = []
    w_current_entropy = engine.compute_entropy() - 0.21
    seen_pieces = {}
    for piece_index, piece in enumerate(queue):
        key = int(piece)
        if key in seen_pieces:
            continue
        seen_pieces[key] = piece_index
        for coord in engine.check_positions(piece):
            # Inline compute_weighted_index
            score = engine.compute_dense_index(coord, piece) * 4
            copy_engine = engine.__copy__()
            copy_engine.add_piece(coord, piece)
            score += len(copy_engine.eliminate()) / copy_engine.radius * 5
            x = copy_engine.compute_entropy() - w_current_entropy
            score += 7 / (1 + exp(-3 * x))
            # Append scored option
            options.append((piece_index, coord, score))
    if not options:
        raise ValueError("No valid options found")
    # Choose best option
    best_placement = max(options, key=lambda item: item[2])
    best_piece_option, best_position_option, best_score_result = best_placement
    return (best_piece_option, best_position_option)

def nrsearch(engine: HexEngine, queue : list[Piece]) -> tuple[int, Hex]:
    '''
    A heuristic algorithm that selects the best piece and position based on the dense index, and score gain of the game state.
    This algorithm computes the dense index and score gain for each piece and position, and returns the one with the highest score.
    
    In the nrminimax package, this is the best algorithm to use.
    
    Parameters:
        engine (HexEngine): The game engine.
        queue (list[Piece]): The queue of pieces available for placement.
    Returns:
        placement (tuple[int, Hex]): A tuple containing the index of the best piece and the best position to place it.
    Raises:
        ValueError: If the queue is empty or no valid positions are found for the pieces in the queue.
    '''
    options = []
    seen_pieces = {}
    for piece_index, piece in enumerate(queue):
        key = int(piece)
        if key in seen_pieces: continue
        seen_pieces[key] = piece_index
        for coord in engine.check_positions(piece):
            score = engine.compute_dense_index(coord, piece) + len(piece)
            copy_engine = engine.__copy__()
            copy_engine.add_piece(coord, piece)
            score += len(copy_engine.eliminate()) / engine.radius
            options.append((piece_index, coord, score))
    if not options:
        raise ValueError("No valid options found")
    best_placement = max(options, key=lambda item: item[2])
    best_piece_option, best_position_option, best_score_result = best_placement
    return (best_piece_option, best_position_option)