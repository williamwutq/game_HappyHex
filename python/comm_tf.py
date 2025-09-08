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
import sys, os
from hex import HexEngine, Piece, Hex

# This script only allow loading of one model, specified by path
# Usage: python comm_tf.py model_path
# The model should be a keras model saved using model.save('path')

if __name__ == "__main__":
    sys.path.append(os.path.dirname(os.path.dirname(__file__))) # Add parent directory to path
    # If there are arguments, get it
    if len(sys.argv) > 1:
        path = sys.argv[1]
        # Create model from path
        try:
            from python.tensorflowimpl.autoplayimpl import create_model_predictor
            from keras import backend
        except ImportError as e:
            sys.exit(1)
        try:
            alg = create_model_predictor(path, 'alg')
        except Exception as e:
            sys.exit(1)
    else:
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
                        try:
                            best_piece_option, best_position_option = alg(engine, queue)
                        except Exception as e:
                            print(f"Error encountered: {e}", flush=True)
                            break
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
        print('Interrupted', flush=True)
        backend.clear_session() # Clear TensorFlow session to free resources
        sys.exit(0)