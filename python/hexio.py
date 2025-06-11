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

import hex as hx
from pathlib import Path

__all__ = ['read_f', 'smart_find_f', 'smart_read_f', 'HexReader']

script_path = Path(__file__).resolve()
data_path = script_path.parents[1] / 'data'

def read_f (name : str) -> str:
    """
    Reads the contents of a .hpyhex file as a hexadecimal string.
    :param name: The base name of the file (without extension) to read.
    :return: The file's contents as an uppercase hexadecimal string.
             Returns an empty string if the file cannot be read.
    """
    try:
        with open(data_path / (name + '.hpyhex'), 'rb') as file:
            data = file.read().hex().upper()
    except IOError: return ''
    return data


def smart_find_f () -> [Path]:
    """
    Scans the source data_path directory for all valid .hpyhex files.
    :return: A list of file paths pointing to .hpyhex files in the directory.
    """
    return [file for file in data_path.iterdir() if file.is_file() and file.suffix == '.hpyhex']


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
        """
        Constructs a HexLogger with a pre-assigned file name
        :param name: the assigned file name. HexLogger will try to find file in the same directory.
                     If file name is an empty string, it will redirect to a valid .hpyhex file.
        """
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
        """
        Returns the filepath of the game file the HexReader is pointing to.
        :return: The filepath of this game
        """
        return data_path / (self._name + '.hpyhex')

    def file_exist(self) -> bool:
        """
        Return whether such a target file exist. Normally, it should return True, unless
        the HexReader is created manually or file has been deleted.
        :return: True if file at get_path() exist
        """
        return self.get_path().is_file() and len(self.get_path().read_bytes()) != 0

    def game_exist(self) -> bool:
        """
        Return whether this HexReader has been populated with game data, no matter whether game file still exist.
        :return: True if this logger contains full game data
        """
        return self._g_

    def clear(self) -> None:
        """
        Erase the game data in this logger by reset them. This will not reset the file it points to or the player.
        This is almost equal to create another logger.
        :return: None
        """
        self._g_ = False # game has not been read
        self._g_completed = False
        self._g_turn = 0; self._g_score = 0
        self._g_engine = hx.HexEngine(1)
        self._g_queue = [hx.Piece()]
        self._g_m_origins = [hx.Hex()]
        self._g_m_pieces = [int()]
        self._g_m_queues = [[hx.Piece()]]

    def erase(self):
        """
        Erase the game data in this logger by reset them. This will not reset the file it points to or the player.
        This is almost equal to create another logger.
        :return: None
        """
        self.clear()

    def __fail(self, reason : str) -> None:
        """
        Internal method to use when reading data fails. Raise IOError.
        :param reason: the reason why reading data failed
        :raise IOError: Fail to read binary data because reason
        :return: None
        """
        self._g_ = False
        raise IOError ('Fail to read binary data because ' + reason)

    def __read(self) -> None:
        """
        Reads a binary log file and parses it into memory.
        This populates the engine, queue, moves, and other game data from binary data.
        The data format is "hex.binary".
        :return: None
        """
        hex_str = read_f(self._name)
        # Format check
        if not hex_str[:32] == '4B874B1E5A0F5A0F5A964B874B5A5A87':
            self.__fail('file header is corrupted')
        d_code = int(hex_str[32:48], 16)
        hex_str = hex_str[72:]
        d_id = int(hex_str[:8], 16)
        if not hex_str[8:24] == '214845582D42494E':
            self.__fail('file data start header is corrupted')
        d_turn = int(hex_str[24:32], 16)
        d_score = int(hex_str[32:40], 16)
        d_complete = int(hex_str[40:41], 16) % 2 == 0
        # try encoding
        d_obf_score = HexReader.obfuscate(HexReader.interleave_integers(d_score * d_score, d_id ^ d_turn))
        d_obf_turn = HexReader.obfuscate(HexReader.interleave_integers(d_turn * d_turn, d_id ^ d_score))
        d_obf_combined = ((d_obf_turn << 32) | (d_obf_score & 0xFFFFFFFF))
        d_obf_combined = HexReader.obfuscate(d_id * 43 ^ d_obf_combined ^ d_obf_turn) ^ d_obf_score
        if d_obf_combined != d_code:
            self.__fail('file data encoding is corrupted or version is not supported')
        if not hex_str[41:45] == 'FFFF':
            # If contains user info, ignore it
            if not hex_str[153:157] == 'FFFF':
                self.__fail('file data divider cannot be found at the correct position')
            else: hex_str = hex_str[157:]
        else: hex_str = hex_str[45:]
        # read uncolored
        try:
            # read engine
            d_engine_radius = int(hex_str[:4], 16)
            if d_engine_radius < 1:
                self.__fail('engine data cannot be read due to impossible radius')
            d_engine = hx.HexEngine(d_engine_radius)
            d_engine_block_index = 0
            d_engine_read_limit = (d_engine.length()+3)//4
            for i in range(d_engine_read_limit):
                d_engine_read_value = int(hex_str[4+i:5+i], 16)
                d_engine_read_bit = 0
                while d_engine_read_bit < 4 and d_engine_block_index < d_engine.length():
                    d_engine_block_state = (d_engine_read_value & (0x1 << d_engine_read_bit)) != 0
                    d_engine.get_block(d_engine_block_index).set_state(d_engine_block_state)
                    d_engine.get_block(d_engine_block_index).set_color(-2)
                    d_engine_read_bit += 1
                    d_engine_block_index += 1
            if not hex_str[d_engine_read_limit+4:d_engine_read_limit+6] == 'FF':
                self.__fail('file data divider cannot be found at the correct position')
            else: hex_str = hex_str[d_engine_read_limit+6:]
            # read queue
            d_queue_length = int(hex_str[:4], 16)
            d_queue_data = [hx.Piece()]
            for i in range(d_queue_length):
                d_queue_piece_byte = int(hex_str[i*2+4:i*2+6], 16)
                try:
                    d_queue_piece = hx.Piece.piece_from_byte(d_queue_piece_byte, -2)
                    d_queue_data.append(d_queue_piece)
                except ValueError:
                    self.__fail('failed to read queue data')
            if not hex_str[d_queue_length*2+4:d_queue_length*2+6] == 'FF':
                self.__fail('file data divider cannot be found at the correct position')
            else: hex_str = hex_str[d_queue_length*2+6:]
            # read moves
            d_move_origins = [hx.Hex()]
            d_move_indexes = [int()]
            d_move_queues = [[hx.Piece()]]
            for i in range(d_turn):
                d_move_base_index = i * (10 + 2 * d_queue_length)
                d_move_origin_i = int(hex_str[d_move_base_index:d_move_base_index+4], 16)
                d_move_origin_k = int(hex_str[d_move_base_index+4:d_move_base_index+8], 16)
                d_move_origins.append(hx.Hex.hex(d_move_origin_i, d_move_origin_k))
                d_move_index = int(hex_str[d_move_base_index+8:d_move_base_index+10], 16)
                d_move_indexes.append(d_move_index)
                d_move_queue_data = [hx.Piece()]
                for q in range(d_queue_length):
                    d_move_queue_piece_byte = int(hex_str[q*2+d_move_base_index+10:q*2+d_move_base_index+12], 16)
                    try:
                        d_move_queue_piece = hx.Piece.piece_from_byte(d_move_queue_piece_byte, -2)
                        d_move_queue_data.append(d_move_queue_piece)
                    except ValueError:
                        self.__fail('failed to read queue data in move data')
                d_move_queues.append(d_move_queue_data)
            d_move_read_length = (10 + 2 * d_queue_length) * d_turn
            if not hex_str[d_move_read_length:d_move_read_length+4] == 'FFFF':
                self.__fail('file data divider cannot be found at the correct position')
            else: hex_str = hex_str[d_move_read_length+4:]
            # final check
            if int(hex_str[:4], 16) != self.obfuscate(d_id)<<5 & 0xFFFF:
                self.__fail('file data encoding is corrupted or version is not supported')
        except Exception as exception_while_reading_uncolored:
            # read colored binary format
            try:
                # read engine
                d_engine_radius = int(hex_str[:4], 16)
                if d_engine_radius < 1:
                    self.__fail('engine data cannot be read due to impossible radius')
                d_engine = hx.HexEngine(d_engine_radius)
                d_engine_block_index = 0
                d_engine_read_limit = (d_engine.length()+3)//4
                for i in range(d_engine.length()):
                    d_engine_block_color = -1
                    c_engine_color = int(hex_str[4+i:5+i], 16)
                    if c_engine_color == 14:
                        d_engine_block_color = -2
                    elif 0 <= c_engine_color <= 13:
                        d_engine_block_color = c_engine_color
                    d_engine.get_block(i).set_color(d_engine_block_color)
                hex_str = hex_str[d_engine.length()+4:]
                for i in range(d_engine_read_limit):
                    d_engine_read_value = int(hex_str[4+i:5+i], 16)
                    d_engine_read_bit = 0
                    while d_engine_read_bit < 4 and d_engine_block_index < d_engine.length():
                        d_engine_block_state = (d_engine_read_value & (0x1 << d_engine_read_bit)) != 0
                        d_engine.get_block(d_engine_block_index).set_state(d_engine_block_state)
                        d_engine_read_bit += 1
                        d_engine_block_index += 1
                if not hex_str[d_engine_read_limit:d_engine_read_limit+2] == 'FF':
                    self.__fail('file data divider cannot be found at the correct position')
                else: hex_str = hex_str[d_engine_read_limit+2:]
                # read queue
                d_queue_length = int(hex_str[:4], 16)
                d_queue_data = [hx.Piece()]
                for i in range(d_queue_length):
                    d_queue_piece_byte = int(hex_str[i*3+4:i*3+6], 16)
                    d_queue_piece_color = -1
                    c_piece_color = int(hex_str[i*3+6:i*3+7], 16)
                    if c_piece_color == 14:
                        d_queue_piece_color = -2
                    elif 0 <= c_piece_color <= 13:
                        d_queue_piece_color = c_piece_color
                    try:
                        d_queue_piece = hx.Piece.piece_from_byte(d_queue_piece_byte, d_queue_piece_color)
                        d_queue_data.append(d_queue_piece)
                    except ValueError:
                        self.__fail('failed to read queue data')
                if not hex_str[d_queue_length*3+4:d_queue_length*3+6] == 'FF':
                    self.__fail('file data divider cannot be found at the correct position')
                else: hex_str = hex_str[d_queue_length*3+6:]
                # read moves
                d_move_origins = [hx.Hex()]
                d_move_indexes = [int()]
                d_move_queues = [[hx.Piece()]]
                for i in range(d_turn):
                    d_move_base_index = i * (10 + 3 * d_queue_length)
                    d_move_origin_i = int(hex_str[d_move_base_index:d_move_base_index+4], 16)
                    d_move_origin_k = int(hex_str[d_move_base_index+4:d_move_base_index+8], 16)
                    d_move_origins.append(hx.Hex.hex(d_move_origin_i, d_move_origin_k))
                    d_move_index = int(hex_str[d_move_base_index+8:d_move_base_index+10], 16)
                    d_move_indexes.append(d_move_index)
                    d_move_queue_data = [hx.Piece()]
                    for q in range(d_queue_length):
                        d_move_queue_piece_byte = int(hex_str[q*3+d_move_base_index+10:q*3+d_move_base_index+12], 16)
                        d_move_queue_piece_color = -1
                        c_move_piece_color = int(hex_str[q*3+d_move_base_index+12:q*3+d_move_base_index+13], 16)
                        if c_move_piece_color == 14:
                            d_move_queue_piece_color = -2
                        elif 0 <= c_move_piece_color <= 13:
                            d_move_queue_piece_color = c_move_piece_color
                        try:
                            d_move_queue_piece = hx.Piece.piece_from_byte(d_move_queue_piece_byte, d_move_queue_piece_color)
                            d_move_queue_data.append(d_move_queue_piece)
                        except ValueError:
                            self.__fail('failed to read queue data in move data')
                    d_move_queues.append(d_move_queue_data)
                d_move_read_length = (10 + 3 * d_queue_length) * d_turn
                if not hex_str[d_move_read_length:d_move_read_length+4] == 'FFFF':
                    self.__fail('file data divider cannot be found at the correct position')
                else: hex_str = hex_str[d_move_read_length+4:]
                # final check
                if int(hex_str[:4], 16) != self.obfuscate(d_id)<<5 & 0xFFFF:
                    self.__fail('file data encoding is corrupted or version is not supported')
            except Exception as exception_while_reading_colored:
                raise IOError (' in both uncolored and colored format because in uncolored format, '
                               + exception_while_reading_uncolored.__str__() + '; in colored format, '
                               + exception_while_reading_colored.__str__())
        # record
        self._g_ = True
        self._g_completed = d_complete
        self._g_turn = d_turn
        self._g_score = d_score
        self._g_engine = d_engine
        self._g_queue = d_queue_data
        self._g_m_origins = d_move_origins
        self._g_m_pieces = d_move_indexes
        self._g_m_queues = d_move_queues

    def read(self) -> bool:
        try:
            try:
                self.__read()
            except IOError | ValueError | TypeError:
                return False
        except TypeError:
            return False
        return True

    def __str__(self) -> str:
        """
        Returns a String representation of the HexLogger, containing all its essential information.

        This String representation contains the following elements:

        - The path to the file that the logger is pointing to and operating for.
        - The complete status of the game contained in the logger and the file.
        - The current HexEngine representing the recorded game field.
        - The current Piece queue in the recorded game.
        - The moves, containing centers Hex coordinates and Piece placed, in the recorded game.

        This method use __str__ methods in the contained objects to generate string representations.
        :return: string representation of the HexLogger as str
        """
        builder = ["GameLogger[type = HexReader, path = ", (data_path / self._name).__fspath__(), ".hpyhex, completed = ",
                   str(self._g_completed), ", turn = ", str(self._g_turn), ", score = ", str(self._g_score),
                   ", data = HexData[engine = ", str(self._g_engine), ", queue = {"]

        if self._g_queue: builder.append(", ".join(str(item) for item in self._g_queue))
        builder.append("}, moves = {")

        if self._g_m_pieces and self._g_m_origins:
            moves = [
                f"HexMove[center = {self._g_m_origins[i]}, piece = {self._g_m_queues[i][self._g_m_pieces[i]]}]"
                for i in range(len(self._g_m_origins))
            ]
            builder.append(", ".join(moves))

        builder.append("}]]")
        return "".join(builder)


def smart_read_f () -> [HexReader]:
    """
    Constructs and reads all valid HexReader objects from .hpyhex files.
    :returns: A list of HexReader objects for which the both game file and data exists, with .read() already called on each.
    """
    ps = [file.name.removesuffix('.hpyhex') for file in smart_find_f()]
    rs = [HexReader(p) for p in ps]
    for r in rs: r.read()
    return [r for r in rs if r.game_exist()]
