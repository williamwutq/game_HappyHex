__all__ = ['algorithm']

from typing import List
from hex import HexEngine, Piece, Hex

def algorithm(engine: HexEngine, queue : List[Piece]) -> tuple[int, Hex]:
    """
    A heuristic algorithm that selects the best piece and position based on the current entropy of the game state.
    :param engine: The game engine
    :param queue: The queue of pieces available for placement
    :return: A tuple containing the index of the best piece and the best position to place it
    """
    options = []
    w_current_entropy = engine.compute_entropy() - 0.21
    seen_pieces = {}
    for piece_index, piece in enumerate(queue):
        key = piece.to_byte()
        if key in seen_pieces: continue
        seen_pieces[key] = piece_index
        for coord in engine.check_positions(piece):
            score = engine.compute_weighted_index(coord, piece, w_current_entropy)
            options.append((piece_index, coord, score))
                    # choose best option
    best_placement = max(options, key=lambda item: item[2])
    best_piece_option, best_position_option, best_score_result = best_placement
    return (best_piece_option, best_position_option)