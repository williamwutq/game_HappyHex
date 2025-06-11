import sys
import hexio as io

if __name__ == '__main__':
    # Read arguments
    reader = io.HexReader("")
    if len(sys.argv) > 1:
        reader = io.HexReader(sys.argv[1])
    print(reader.get_path())
    print(reader.file_exist())
    print(reader.read())
    print(reader.game_exist())
    print(reader)
    for f in io.smart_read_f():
        if f.game_exist():
            print(f.get_path())