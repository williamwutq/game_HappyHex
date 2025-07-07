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
import time
from typing import Optional, List
from comm import CommandProcessor
import hex

__all__ = ['MainProcessor']

def str_to_bools(s: str) -> list[bool]:
    true_set = {'X', '1', 'T'}
    false_set = {'O', '0', 'F'}
    result = []
    for char in s:
        if char in true_set:
            result.append(True)
        elif char in false_set:
            result.append(False)
        else:
            raise ValueError(f"Invalid character '{char}' in input string")
    return result


class MainProcessor(CommandProcessor):
    def __init__(self):
        self._callback: Optional[CommandProcessor] = None

    def execute_command(self, command: str, args: List[str]) -> None:
        if self._callback is not None:
            if command == 'ping':
                self._callback.execute('pong')
            elif command == 'pong':
                pass
            elif command == 'hex':
                if len(args) == 2:
                    i = int(args[0])
                    k = int(args[1])
                    hex_coo = hex.Hex.hex(i, k)
                    self._callback.execute(f'print {hex_coo}')
                else: raise ValueError('Missing or extra coordinates for hex')
            elif command == 'piece':
                if len(args) == 1:
                    p = hex.Piece.piece_from_byte(int(args[0]), -1)
                    self._callback.execute(f'print {p}')
                else: raise ValueError('Missing or extra argument for piece')
            elif command == 'engine':
                if len(args) == 1:
                    bools = str_to_bools(args[0])
                    e = hex.HexEngine.engine_from_booleans(bools)
                    self._callback.execute(f'print {e}')
                else: raise ValueError('Missing or extra argument for engine')
            elif command == 'move':
                if len(args) > 1:
                    # engine
                    bools = str_to_bools(args[0])
                    engine = hex.HexEngine.engine_from_booleans(bools)
                    engine.eliminate()
                    # queue
                    queue : list[hex.Piece] = []
                    for arg in args[1:]:
                        piece = hex.Piece.piece_from_byte(int(arg), -1)
                        queue.append(piece)
                    # evaluate
                    t = time.time()
                    options = []
                    for piece_option in range(len(queue)):
                        piece = queue[piece_option]
                        for coordinate in engine.check_positions(piece):
                            options.append((piece_option, coordinate, engine.compute_entropy_index(coordinate, piece)))
                    print (time.time() - t)
                    # choose best option
                    best_placement = max(options, key=lambda item: item[2])
                    best_piece_option, best_position_option, best_score_result = best_placement
                    # callback
                    self._callback.execute(f'move {best_position_option.get_line_i()} {best_position_option.get_line_k()} {best_piece_option}')
                else: raise ValueError('Missing arguments for move')
            else: raise ValueError('Invalid command')

    def get_callback_processor(self) -> Optional['CommandProcessor']:
        return self._callback
    def set_callback_processor(self, processor: Optional['CommandProcessor']) -> None:
        if processor is self:
            raise ValueError("Cannot add self as callback processor")
        else: self._callback = processor
