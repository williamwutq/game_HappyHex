from python import hex as hx
import math
from pathlib import Path

__all__ = ['read_f', 'HexReader']
__sin60__ = math.sqrt(3) / 2
__version__ = "1.3.0"
__version_info__ = (1,3,0)

script_path = Path(__file__).resolve()
data_path = script_path.parents[1] / 'data'

def read_f (name : str) -> str:
    try:
        with open(data_path / (name + '.hpyhex'), 'rb') as f:
            data = f.read().hex().upper()
    except IOError: return ''
    return data


def smart_find_f ():
    return [f for f in data_path.iterdir() if f.is_file() and f.suffix == '.hpyhex']


class HexReader:
    PRIME = 0xC96C5795D7870F3D
    """ A large prime number used in the hash function for mixing bits. """
    SHIFTS = [31, 37, 41, 27, 23, 29, 33, 43]
    """ Bit shift values used for the hashing obfuscation process. """

    @staticmethod
    def interleave_integers(even: int, odd: int) -> int:
        """
        Interleaves the bits of two 32-bit integers into a single 64-bit integer.
        The 'even' integer contributes to even bit positions (0,2,4,...) and
        the 'odd' integer to odd bit positions (1,3,5,...).

        :param even: 32-bit integer for even bit positions
        :param odd:  32-bit integer for odd bit positions
        :return:     64-bit integer with interleaved bits
        """
        # Mask to 32 bits and treat as unsigned
        e = even & 0xFFFFFFFF
        o = odd  & 0xFFFFFFFF

        # Spread bits of each half into the low 64-bit lanes
        e = (e | (e << 16)) & 0x0000FFFF0000FFFF
        o = (o | (o << 16)) & 0x0000FFFF0000FFFF
        e = (e | (e << 8)) & 0x00FF00FF00FF00FF
        o = (o | (o << 8)) & 0x00FF00FF00FF00FF
        e = (e | (e << 4)) & 0x0F0F0F0F0F0F0F0F
        o = (o | (o << 4)) & 0x0F0F0F0F0F0F0F0F
        e = (e | (e << 2)) & 0x3333333333333333
        o = (o | (o << 2)) & 0x3333333333333333
        e = (e | (e << 1)) & 0x5555555555555555
        o = (o | (o << 1)) & 0x5555555555555555

        # Combine and mask: bits of o shifted into odd positions, then XOR (equivalent to OR here)
        result = e ^ (o << 1)
        return result & 0xFFFFFFFFFFFFFFFF

    @staticmethod
    def obfuscate(input_val: int) -> int:
        """
        Obfuscates a 64-bit integer using bit shifts and prime multiplications.
        This function provides a layered transformation for hashing purposes.

        :param input_val: The raw integer to obfuscate.
        :return: Obfuscated integer.
        """
        input_val &= 0xFFFFFFFFFFFFFFFF  # Ensure 64-bit unsigned behavior

        input_val ^= (input_val << HexReader.SHIFTS[0]) | (input_val >> HexReader.SHIFTS[1])
        input_val *= HexReader.PRIME
        input_val &= 0xFFFFFFFFFFFFFFFF  # Keep within 64-bit

        input_val ^= (input_val << HexReader.SHIFTS[2]) | (input_val >> HexReader.SHIFTS[3])
        input_val *= HexReader.PRIME
        input_val &= 0xFFFFFFFFFFFFFFFF

        input_val ^= (input_val << HexReader.SHIFTS[4]) | (input_val >> HexReader.SHIFTS[5])
        input_val *= HexReader.PRIME
        input_val &= 0xFFFFFFFFFFFFFFFF

        input_val ^= (input_val << HexReader.SHIFTS[6]) | (input_val >> HexReader.SHIFTS[7])
        input_val &= 0xFFFFFFFFFFFFFFFF
        return input_val

    def __init__(self, name : str) -> None:
        if len(name) == 0:
            dirs = smart_find_f()
            if len(dirs) != 0:
                self._name = dirs[0].name.removesuffix('.hpyhex')
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

    def get_path(self) -> Path:
        return data_path / (self._name + '.hpyhex')

    def file_exist(self) -> bool:
        return self.get_path().is_file() and len(self.get_path().read_bytes()) != 0

    def game_exist(self) -> bool:
        return self._g_

    def __fail__(self, reason : str):
        self._g_ = False
        raise IOError ('Fail to read binary data because ' + reason)

    def __read__(self):
        hex_str = read_f(self._name)
        # Format check
        if not hex_str[:32] == "4B874B1E5A0F5A0F5A964B874B5A5A87":
            self.__fail__("file header is corrupted")
        d_code = int(hex_str[32:48], 16)
        hex_str = hex_str[72:]
        d_id = int(hex_str[:8], 16)
        if not hex_str[8:24] == "214845582D42494E":
            self.__fail__("file data start header is corrupted")
        d_turn = int(hex_str[24:32], 16)
        d_score = int(hex_str[32:40], 16)
        d_complete = int(hex_str[40:41], 16) % 2 == 0
        # try encoding
        d_obf_score = HexReader.obfuscate(HexReader.interleave_integers(d_score * d_score, d_id ^ d_turn))
        d_obf_turn = HexReader.obfuscate(HexReader.interleave_integers(d_turn * d_turn, d_id ^ d_score))
        d_obf_combined = ((d_obf_turn << 32) | (d_obf_score & 0xFFFFFFFF))
        d_obf_combined = HexReader.obfuscate(d_id * 43 ^ d_obf_combined ^ d_obf_turn) ^ d_obf_score
        if d_obf_combined != d_code:
            self.__fail__("file data encoding is corrupted or version is not supported")
        if not hex_str[41:45] == "FFFF":
            self.__fail__("file data divider cannot be found at the correct position")
        # read engine

        return d_complete





if __name__ == '__main__':
    reader = HexReader("")
    print(reader.get_path())
    print(reader.file_exist())
    print(reader.game_exist())
    print(reader.__read__())
