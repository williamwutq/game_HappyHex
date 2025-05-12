from python import hex as hx
import math
from pathlib import Path

__all__ = ['read_f', 'HexReader']
__sin60__ = math.sqrt(3) / 2
__version__ = "1.3.0"
__version_info__ = (1,3,0)

script_path = Path(__file__).resolve()
data_path = script_path.parents[1] / 'data'

def read_f (name : str):
    try:
        with open(data_path / (name + '.hpyhex'), 'rb') as f:
            data = f.read().hex().upper()
    except IOError: return ''
    return data


def smart_find_f ():
    return [f for f in data_path.iterdir() if f.is_file() and f.suffix == '.hpyhex']


class HexReader:
    def __init__(self, name : str):
        if len(name) == 0:
            dirs = smart_find_f()
            if len(dirs) != 0:
                self._name = dirs[0].name
            else:
                raise IOError ('HexReader cannot be created because no file is found in the data folder')
        else: self._name = name
        # other things to initialize
        self._g_ = False # game has not been read
        self._g_completed = False
        self._g_turn = 0; self._g_score = 0
        self._g_engine = hx.HexEngine(1)
        self._g_queue = [hx.Piece()]
        self._g_m_origins = [hx.Hex()]
        self._g_m_pieces = [int()]
        self._g_m_queues = [[hx.Piece()]]

    def get_path(self):
        return data_path / (self._name + '.hpyhex')

    def file_exist(self):
        return self.get_path().is_file() and len(self.get_path().read_bytes()) != 0

    def game_exist(self):
        return self._g_

if __name__ == '__main__':
    print(read_f("4AF5CB710848FEA4"))
    print(HexReader("4AF5CB710848FEA4").file_exist())
