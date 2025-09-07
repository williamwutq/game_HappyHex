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
import sys
from hex import HexEngine, Piece, Hex
from algos import nrsearch as alg
from algos import __all__ as allalgos

if __name__ == "__main__":
    # If there are arguments, get it
    if len(sys.argv) > 1:
        arg = sys.argv[1]
        if arg in allalgos:
            alg = getattr(__import__('algos', fromlist=[arg]), arg)
        else:
            path = arg
            if len(sys.argv) > 2:
                name = sys.argv[2]
            else:
                name = 'alg'
            # load alg from path
            import importlib.util
            spec = importlib.util.spec_from_file_location("custom_algo", path)
            custom_algo = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(custom_algo)
            alg = getattr(custom_algo, name, None)
            if alg is None:
                sys.exit(1)
    try:
        # Read commands from stdin
        for line in sys.stdin:
            line = line.strip()
            if line:
                parts = line.split(None, 1)
                command = parts[0]
                args = parts[1].split() if len(parts) > 1 else []
                if command == "interrupt":
                    sys.exit(0) # Terminate gracefully on interrupt
                elif command == "kill":
                    sys.exit(1) # Signal failure on kill
                elif command == 'pong':
                    pass
                elif command == 'ping':
                    print('pong', flush=True)
                elif command == 'move':
                    if len(args) > 1:
                        # engine
                        try:
                           engine = HexEngine(args[0])
                           engine.eliminate()
                        except ValueError as e:
                            continue
                        # queue
                        queue : list[Piece] = []
                        for arg in args[1:]:
                            try:
                                piece = Piece(int(arg))
                            except ValueError:
                                continue
                            except TypeError:
                                continue
                            queue.append(piece)
                        # evaluate
                        best_piece_option, best_position_option = alg(engine, queue)
                        # type check
                        if not isinstance(best_piece_option, int):
                            try:
                                best_piece_option = int(best_piece_option)
                            except ValueError:
                                continue
                        if not isinstance(best_position_option, Hex):
                            try:
                                best_position_option = Hex() + best_position_option
                            except ValueError:
                                continue
                        # call
                        print(f'move {best_position_option.i} {best_position_option.k} {best_piece_option}', flush=True)
    except KeyboardInterrupt:
        sys.exit(0)