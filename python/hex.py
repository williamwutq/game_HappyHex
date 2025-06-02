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

import copy
import math
from math import log2, exp
from abc import ABC, abstractmethod
from typing import List

__all__ = ['Hex', 'Block', 'HexEngine', 'Piece', 'HexGrid', '__sin60__']
__sin60__ = math.sqrt(3) / 2

class Hex:
    """
    The Hex class represents a 2D coordinate in a hexagonal grid system using
    a specialized integer coordinate model. It supports both raw coordinate access
    and derived line-based computations across three axes: I, J, and K.

    Coordinate System:
    - Designed by William Wu.
    - Axes I, J, K run diagonally through the hexagonal grid.
    - I+ is 60 degrees to J+, J+ is 60 degrees to K+, K+ is 60 degrees to J-.
    - Coordinates (i, k) correspond to a basis for representing any hexagon.
    - Raw coordinates: distance along one axis multiplied by 2, with i - j + k = 0.
    - Line coordinates: distance perpendicular to axes, with I + J - K = 0.
    - Line coordinates are preferred due to complexities with raw coordinates.

    Coordinate System Implementation:
    - x and y are base values stored in each Hex instance.
    - I = x, K = y, J = x + y.
    - Line indices: get_line_i = (2y + x) / 3, get_line_j = (x - y) / 3, get_line_k = (2x + y) / 3.

    Functionality:
    - Access raw coordinates: I(), J(), K().
    - Access line coordinates: get_line_i(), get_line_j(), get_line_k().
    - Create Hex objects: constructors or factory methods hex().
    - Move along I, J, K axes: move_i(), move_j(), move_k().
    - Addition/subtraction: add(), subtract().
    - Check line alignment and adjacency: in_line_i(), adjacent(), etc.
    - Determine relative orientation: front(), back(), and axis-specific versions.
    - Clone instances.

    Usage Notes:
    - Use factory method hex(i, k) instead of direct constructors for correct line coordinate shifts.
    """
    _half_sin_of_60 = math.sqrt(3) / 4

    def __init__(self, i=0, k=0):
        """Initialize a Hex coordinate at (i, k). Defaults to (0, 0)."""
        self._x = i
        self._y = k

    @staticmethod
    def hex(i=0, k=0):
        """Create a Hex coordinate using line indices, shifted accordingly."""
        return Hex().shift_i(k).shift_k(i)

    # Raw coordinates
    def __i__(self):
        """Return raw I-coordinate."""
        return self._x

    def __j__(self):
        """Return raw J-coordinate."""
        return self._x + self._y

    def __k__(self):
        """Return raw K-coordinate."""
        return self._y

    # Line coordinates
    def get_line_i(self):
        """Compute line index along I-axis."""
        return (2 * self._y + self._x) // 3

    def get_line_j(self):
        """Compute line index along J-axis."""
        return (self._x - self._y) // 3

    def get_line_k(self):
        """Compute line index along K-axis."""
        return (2 * self._x + self._y) // 3

    def get_lines(self):
        """Return string of line indices: {I = i, J = j, K = k}."""
        return f"{{I = {self.get_line_i()}, J = {self.get_line_j()}, K = {self.get_line_k()}}}"

    # Line booleans
    def in_line_i(self, line_or_hex):
        """Check if Hex is in the given I-line or same I-line as another Hex."""
        if isinstance(line_or_hex, int):
            return self.get_line_i() == line_or_hex
        return self.get_line_i() == line_or_hex.get_line_i()

    def in_line_j(self, line_or_hex):
        """Check if Hex is in the given J-line or same J-line as another Hex."""
        if isinstance(line_or_hex, int):
            return self.get_line_j() == line_or_hex
        return self.get_line_j() == line_or_hex.get_line_j()

    def in_line_k(self, line_or_hex):
        """Check if Hex is in the given K-line or same K-line as another Hex."""
        if isinstance(line_or_hex, int):
            return self.get_line_k() == line_or_hex
        return self.get_line_k() == line_or_hex.get_line_k()

    def adjacent(self, other):
        """Check if this Hex is adjacent to another Hex."""
        return self.front(other) or self.back(other)

    def front(self, other):
        """Check if this Hex is in front of another Hex on any axis."""
        return self.front_i(other) or self.front_j(other) or self.front_k(other)

    def back(self, other):
        """Check if this Hex is behind another Hex on any axis."""
        return self.back_i(other) or self.back_j(other) or self.back_k(other)

    def front_i(self, other):
        """Check if this Hex is one unit higher on I-axis."""
        return self._x == other.__i__() + 2 and self._y == other.__k__() - 1

    def front_j(self, other):
        """Check if this Hex is one unit higher on J-axis."""
        return self._x == other.__i__() + 1 and self._y == other.__k__() + 1

    def front_k(self, other):
        """Check if this Hex is one unit higher on K-axis."""
        return self._x == other.__i__() - 1 and self._y == other.__k__() + 2

    def back_i(self, other):
        """Check if this Hex is one unit lower on I-axis."""
        return self._x == other.__i__() - 2 and self._y == other.__k__() + 1

    def back_j(self, other):
        """Check if this Hex is one unit lower on J-axis."""
        return self._x == other.__i__() - 1 and self._y == other.__k__() - 1

    def back_k(self, other):
        """Check if this Hex is one unit lower on K-axis."""
        return self._x == other.__i__() + 1 and self._y == other.__k__() - 2

    def __eq__(self, other):
        """Check if this Hex equals another Hex."""
        return self._x == other.__i__() and self._y == other.__k__()

    def in_range(self, radius):
        """Check if Hex is within given radius from origin."""
        return (0 <= self.get_line_i() < radius * 2 - 1 and
                -radius < self.get_line_j() < radius and
                0 <= self.get_line_k() < radius * 2 - 1)

    # Rectangular coordinates
    def __x__(self):
        """Convert to rectangular X coordinate."""
        return self._half_sin_of_60 * (self._x + self._y)

    def __y__(self):
        """Convert to rectangular Y coordinate."""
        return (self._x - self._y) / 4.0

    def __str__(self):
        """Return string representation for debugging."""
        return (f"Hex[raw = {{{self.__i__()}, {self.__j__()}, {self.__k__()}}}, "
                f"line = {{{self.get_line_i()}, {self.get_line_j()}, {self.get_line_k()}}}, "
                f"rect = {{{self.__x__()}, {self.__y__()}}}]")

    # Coordinate manipulation
    def move_i(self, unit):
        """Move Hex along I-axis by unit."""
        self._x += 2 * unit
        self._y -= unit

    def move_j(self, unit):
        """Move Hex along J-axis by unit."""
        self._x += unit
        self._y += unit

    def move_k(self, unit):
        """Move Hex along K-axis by unit."""
        self._x -= unit
        self._y += 2 * unit

    def shift_i(self, unit):
        """Return new Hex shifted along I-axis."""
        return Hex(self._x + 2 * unit, self._y - unit)

    def shift_j(self, unit):
        """Return new Hex shifted along J-axis."""
        return Hex(self._x + unit, self._y + unit)

    def shift_k(self, unit):
        """Return new Hex shifted along K-axis."""
        return Hex(self._x - unit, self._y + 2 * unit)

    # Add and subtract
    def __add__(self, other):
        """Return new Hex with summed coordinates."""
        if issubclass(type(other), type(Hex())) and not type(other) == type(Hex()):
            return other + self
        else:
            return Hex(self._x + other.__i__(), self._y + other.__k__())

    def __sub__(self, other):
        """Return new Hex with subtracted coordinates."""
        return Hex(self._x - other.__i__(), self._y - other.__k__())

    def set(self, other):
        """Set this Hex to another Hex's coordinates."""
        self._x = other.__i__()
        self._y = other.__k__()

    def __this__(self):
        """Return this Hex as a new instance using line coordinates."""
        return Hex.hex(self.get_line_i(), self.get_line_k())

    def __copy__(self):
        """Return a copy of this Hex."""
        return Hex(self._x, self._y)

    def __deepcopy__(self, memo):
        """Return a deep copy of this Hex."""
        return Hex(self._x, self._y)


class Block(Hex):
    """
    The Block class extends Hex and represents a colored block with an occupancy state
    within the hexagonal grid system.

    Block inherits the coordinate system from Hex. See Hex for details on the hexagonal
    coordinate system, including both raw and line-based coordinates.

    In addition to Hex functionality, each Block instance encapsulates:
    - A color index: -1 for empty, -2 for default filled, 0-n for real colors.
    - A boolean state: True for occupied, False for unoccupied.

    The class provides constructors and factory methods for creating blocks using
    standard (i, k) coordinates or line indices, and methods for moving, shifting,
    adding, subtracting coordinates, and modifying/retrieving state and color.
    """

    def __init__(self, __hex__ = Hex(), color = 0, state=False):
        """
        Initialize a Block with various parameter combinations.

        :param __hex__: Hex object.
        :param color: k-coordinate or color index.
        :param state: State boolean.
        """
        super().__init__()
        self.set(__hex__)
        self._color = color
        self._state = state

    @staticmethod
    def block(i, k, color):
        """Create a Block using line indices with specified color."""
        return Block(Hex(), color).shift_i(k).shift_k(i)

    # Getters
    def get_color(self):
        """Return the color index of the block."""
        return self._color

    def get_state(self):
        """Return the occupancy state of the block."""
        return self._state

    def __str__(self):
        """Return string representation for debugging with line coordinates."""
        return (f"Block[color = {self._color}, coordinates = {{"
                f"{self.get_line_i()}, {self.get_line_j()}, {self.get_line_k()}}}, "
                f"state = {self._state}]")

    def __copy__(self):
        """Return a copy of this Block."""
        block = Block(self.__this__(), self._color, self._state)
        return block

    def __deepcopy__(self, memo):
        """Return a deep copy of this Block."""
        block = Block(self.__this__(), self._color, self._state)
        return block

    # Setters
    def set_color(self, color):
        """Set the color index of the block."""
        self._color = color

    def set_state(self, state):
        """Set the occupancy state of the block."""
        self._state = state

    def change_state(self):
        """Toggle the occupancy state of the block."""
        self._state = not self._state

    # Shift methods
    def shift_i(self, unit):
        """Return this Block shifted along I-axis."""
        self.move_i(unit)
        return self

    def shift_j(self, unit):
        """Return this Block shifted along J-axis."""
        self.move_j(unit)
        return self

    def shift_k(self, unit):
        """Return this Block shifted along K-axis."""
        self.move_k(unit)
        return self

    # Add and subtract
    def __add__(self, other):
        """Return new Block with summed coordinates."""
        return Block(self.__this__() + other, self._color, self._state)

    def __sub__(self, other):
        """Return new Block with subtracted coordinates."""
        return Block(self.__this__() - other, self._color, self._state)

    def basic_str(self):
        """Return minimal string representation with line coordinates and state."""
        return (f"{{{self.get_line_i()}, {self.get_line_j()}, {self.get_line_k()}, "
                f"{self._state}}}")


class HexGrid(ABC):
    """
    Interface for a two-dimensional hexagonal grid composed of Block objects.

    The HexGrid represents a grid layout based on axial coordinates (I-line and K-line),
    where each valid position may contain a Block object. This interface supports querying,
    iteration, merging, and neighbor-counting functionalities.

    It is designed to be flexible enough to support a wide range of hex-based systems,
    including tile-based games, simulations, or cellular automata. Implementations must
    ensure consistent behavior when accessing blocks by coordinate or index, and must
    provide methods for determining grid boundaries and merging with other grids.

    Coordinate System:
    - Uses a hexagonal coordinate system with positions indexed by (i, k),
      representing the I-line and K-line respectively, corresponding to Hex objects.
    - See Hex for detailed description of the coordinate system.

    Usage:
    - Iterate through blocks using length() and get_block(index).
    - Access specific positions using get_block(__hex__).
    - Merge grids using add(origin, other) and check in-bounds with in_range(__hex__).
    """

    @abstractmethod
    def length(self) -> int:
        """
        Return the number of Block objects in the grid.
        Use with get_block(index) to iterate through all blocks.
        """
        pass

    @abstractmethod
    def blocks(self) -> list[Block]:
        """
        Return a list of all non-null Block objects in the grid,
        sorted by Hex coordinate.
        """
        pass

    @abstractmethod
    def in_range(self, __hex__ : Hex) -> bool:
        """
        Check if the specified Hex coordinate are within the valid range of the grid.

        :param __hex__: The Hex coordinate.

        :return: True if coordinates are within range, False otherwise.
        """
        pass

    @abstractmethod
    def get_block(self, *args) -> Block:
        """
        Retrieve the Block at specified coordinates or index.

        :param args: Either Hex coordinates or a single index.

        :return: The block at the specified coordinates/index, or None if not found or out of range.
        """
        pass

    @abstractmethod
    def add(self, origin: Hex, other: 'HexGrid') -> None:
        """
        Add all blocks from another HexGrid to this grid, aligning them based on a specified Hex coordinate.

        :param origin: The reference Hex position for alignment.
        :param other: The other HexGrid to merge into this grid.

        :raise ValueError: If the grids cannot be merged due to alignment issues.
        """
        pass

    def add_without_shift(self, other: 'HexGrid') -> None:
        """
        Add all blocks from another HexGrid to this grid without shifting positions.

        :param other: The other HexGrid to merge into this grid.

        :raise ValueError: If the grids cannot be merged.
        """
        self.add(Hex(), other)

    def count_neighbors(self, __hex__: Hex, include_null: bool) -> int:
        """
        Count occupied neighboring Blocks around the given Hex position.

        Checks up to six adjacent positions to the block at Hex coordinate.
        A neighbor is occupied if the block is non-null and its state is True.
        Null or out-of-bounds neighbors are counted as occupied if include_null is True.

        :param __hex__: The Hex coordinate.
        :param include_null: Treat null/out-of-bounds neighbors as occupied (True) or unoccupied (False).

        :return: Number of occupied neighbors. Returns 0 if the center position is out of range.
        """
        count = 0
        if self.in_range(__hex__):
            __i__ = __hex__.get_line_i()
            __k__ = __hex__.get_line_k()
            neighbors = [
                (__i__ - 1, __k__ - 1),
                (__i__ - 1, __k__),
                (__i__, __k__ - 1),
                (__i__, __k__ + 1),
                (__i__ + 1, __k__),
                (__i__ + 1, __k__ + 1)
            ]
            for ni, nk in neighbors:
                n_hex = Hex.hex(ni, nk)
                if self.in_range(n_hex):
                    block = self.get_block(n_hex)
                    if block and block.get_state():
                        count += 1
                elif include_null:
                    count += 1
        return count



class Piece(HexGrid):
    """
    Represents a shape or unit made up of multiple Block instances,
    typically forming a logical structure such as a game piece.

    A Piece implements the HexGrid interface and behaves like a small,
    self-contained hexagonal grid. It holds a fixed-size array of Block
    elements and supports block addition, color management, coordinate
    lookup, and comparison.

    It is recommended not to modify a Piece once created and filled with
    sufficient Blocks. If changes are needed, copy the piece block by block.
    For efficiency, cloning is not supported, and multiple references to the
    same object are preferred.

    Coordinate access uses the 'line' system (I, K), defined in Hex, which
    simplifies hex grid navigation by avoiding raw (I, J, K) coordinates.

    Example creation:
        p = Piece(3, 0)
        p.add(Block.block(0, 0, 0))
        p.add(Block.block(0, 1, 0))
        p.add(Block.block(1, 1, 0))
    This produces a shape with blocks at line coordinates (0,0), (0,1), and (1,1).
    """

    def __init__(self, length: int = 1, color: int = None):
        """
        Initialize a Piece with specified capacity and color, or a default single block.

        :param length: Number of blocks this piece can hold (minimum 1). Defaults to 1.
        :param color: Color index for this piece's blocks. Defaults to None for default constructor.
        """
        if length < 1:
            length = 1
        self._blocks = [Block()] * length
        for i in range(length):
            self._blocks[i] = None
        self._color = color

    def set_color(self, color: int) -> None:
        """Set the color of this piece and apply it to all current blocks."""
        self._color = color
        for block in self._blocks:
            if isinstance (block, Block) and block is not None:
                block.set_color(color)

    def get_color(self) -> int:
        """Return the current color of this piece."""
        return self._color

    def add_hex(self, block_or_hex: Block | Hex) -> bool:
        """
        Add a Block or create a Block at Hex coordinates to the first available slot.

        Automatically applies the current color and marks the block as occupied.
        If the piece is full, the block is not added.

        :param block_or_hex: Block to add or Hex coordinates for a new block.

        :return bool: True if the block was added, False if the piece is full.
        """
        for i in range(self.length()):
            if self._blocks[i] is None:
                if isinstance(block_or_hex, Block):
                    self._blocks[i] = block_or_hex
                else:
                    self._blocks[i] = Block.block(block_or_hex.get_line_i(), block_or_hex.get_line_k(), self._color)
                self._blocks[i].set_color(self._color)
                self._blocks[i].set_state(True)
                return True
        return False

    # HexGrid implementation
    def length(self) -> int:
        """Return the number of Block objects in the piece."""
        return len(self._blocks) if self._blocks is not None else 0

    def blocks(self) -> list[Block]:
        """
        Return all Block elements in the piece.

        Null blocks are replaced with dummy placeholders at (0, 0) to preserve order.
        """
        self._sort()
        result = self._blocks.copy()
        for i in range(self.length()):
            if result[i] is None:
                result[i] = Block(Hex(), self._color)
        return result

    def in_range(self, __hex__: Hex) -> bool:
        """Check if a block exists at the given Hex coordinate."""
        return self.get_block(__hex__) is not None

    def get_block(self, *args) -> Block | None:
        """
        Retrieve a Block at specified Hex coordinates or index.

        :param args: Either a Hex object or a single index.

        :return: The block at the specified coordinates/index, or None if not found.
        """
        if len(args) == 1 and isinstance(args[0], int):
            return self._blocks[args[0]]
        elif len(args) == 1 and isinstance(args[0], Hex):
            __hex__ = args[0]
            for block in self._blocks:
                if isinstance(block, Block) and block is not None:
                    if block.get_line_i() == __hex__.get_line_i() and block.get_line_k() == __hex__.get_line_k():
                        return block
        return None

    def get_state(self, __hex__: Hex) -> bool:
        """
        Retrieve the state of a Block at the specified Hex coordinates.

        Returns False if the block does not exist.

        :param __hex__: The Hex coordinate.

        :return: True if the block exists and is occupied, False otherwise.
        """
        block = self.get_block(__hex__)
        return block is not None and block.get_state()

    def add(self, origin: Hex, other: HexGrid) -> None:
        """
        Prohibited operation: Pieces cannot be merged with other grids.

        :param origin: The reference Hex position for alignment.
        :param other: The other HexGrid to merge.

        :raise ValueError: Always, as merging grids with pieces is not allowed.
        """
        raise ValueError("Adding Grid to piece prohibited. Please add block by block.")

    def _sort(self) -> None:
        """Sort Blocks in-place using insertion sort based on Hex line-coordinates (I, K)."""
        n = len(self._blocks)
        for i in range(1, n):
            key = self._blocks[i]
            j = i - 1
            if isinstance(key, Block) and key is not None:
                while j >= 0 and (self._blocks[j] is None or ((isinstance(self._blocks[j], Block)) and (
                        self._blocks[j].get_line_i() > key.get_line_i() or
                        (self._blocks[j].get_line_i() == key.get_line_i() and
                         self._blocks[j].get_line_k() > key.get_line_k())))):
                    self._blocks[j + 1] = self._blocks[j]
                    j -= 1
                self._blocks[j + 1] = key
            else:
                self._blocks[j + 1] = key

    def __str__(self) -> str:
        """Return a string representation of the piece and its block line coordinates."""
        str_builder = ["Piece{"]
        if self._blocks:
            str_builder.append("null" if self._blocks[0] is None else self._blocks[0].basic_str())
        for block in self._blocks[1:]:
            str_builder.append(", ")
            str_builder.append("null" if block is None else block.basic_str())
        str_builder.append("}")
        return "".join(str_builder)

    def to_byte(self) -> int:
        """
        Return a byte representation of the blocks in a standard 7-Block piece.

        Empty blocks are 0s, filled blocks are 1s. Does not record color.

        :returns: A byte representing this 7-block piece, with the first bit empty.
        """
        data = 0
        if self.get_state(Hex.hex(-1, -1)): data += 1
        data <<= 1
        if self.get_state(Hex.hex(-1, 0)): data += 1
        data <<= 1
        if self.get_state(Hex.hex(0, -1)): data += 1
        data <<= 1
        if self.get_state(Hex.hex(0, 0)): data += 1
        data <<= 1
        if self.get_state(Hex.hex(0, 1)): data += 1
        data <<= 1
        if self.get_state(Hex.hex(1, 0)): data += 1
        data <<= 1
        if self.get_state(Hex.hex(1, 1)): data += 1
        return data & 0x7F

    @staticmethod
    def piece_from_byte(data: int, color: int) -> 'Piece':
        """
        Construct a standard 7 or fewer Block piece from byte data.

        :param data: Byte data representing the piece.
        :param color: Color for this piece's blocks.

        :returns: A piece constructed from the byte data with the specified color.

        :raise ValueError: If data represents an empty piece or has an extra bit.
        """
        if data < 0:
            raise ValueError("Data must have empty most significant bit")
        if data == 0:
            raise ValueError("Data must contain at least one block")

        count = bin(data).count("1")
        piece = Piece(count, color)
        if data >> 6 & 1: piece.add_hex(Hex.hex(-1, -1))
        if data >> 5 & 1: piece.add_hex(Hex.hex(-1, 0))
        if data >> 4 & 1: piece.add_hex(Hex.hex(0, -1))
        if data >> 3 & 1: piece.add_hex(Hex.hex(0, 0))
        if data >> 2 & 1: piece.add_hex(Hex.hex(0, 1))
        if data >> 1 & 1: piece.add_hex(Hex.hex(1, 0))
        if data & 1: piece.add_hex(Hex.hex(1, 1))
        return piece

    def __eq__(self, piece: 'Piece') -> bool:
        """
        Compare this piece to another for equality.

        Two pieces are equal if they have the same length and identical Block positions.
        Color index is not considered.

        :param piece: The other piece to compare to.

        :return: True if pieces are structurally equal, False otherwise.
        """
        if piece.length() != self.length():
            return False
        self._sort()
        piece._sort()
        for i in range(self.length()):
            if not self._blocks[i] == piece._blocks[i]:
                return False
        return True



class HexEngine(HexGrid):
    """
    The HexEngine class implements the HexGrid interface and provides a complete engine
    for managing a two-dimensional hexagonal block grid used for constructing and
    interacting with hex-based shapes in the game.

    The engine maintains an array of Block instances arranged in a hexagonal pattern
    with its leftmost Block at origin (0,0), and provides operations such as:
    - Grid initialization and reset
    - Automatic coloring through color indexes
    - Efficient block lookup using binary search
    - Grid placement validation and piece insertion
    - Line detection and elimination across I/J/K axes
    - Deep copy support through the clone method

    Grid Structure:
    - Uses an axial coordinate system (i, k), where i - j + k = 0, and j is derived as j = i + k.
    - Three axes: I, J, K. I+ is 60° from J+, J+ is 60° from K+, K+ is 60° from I-.
    - Raw coordinates: distance along an axis multiplied by 2.
    - Line-coordinates (I, K) are perpendicular distances to axes, calculated from raw coordinates.
    - Blocks are stored in a sorted array by increasing raw coordinate i, then k.

    Grid Size:
    - Total blocks for radius r: Aₖ = 1 + 3*r*(r-1)
    - Derived from: Aₖ = Aₖ₋₁ + 6*(k-1); A₁ = 1

    Block Coloring:
    - Default: two colors for empty (False) and filled (True) states.
    - State updates via set_state or initialization/reset change colors automatically.
    - set_block allows manual color assignment.

    Machine Learning:
    - Supports reward functions for evaluating action quality.
    - check_add discourages invalid moves (e.g., overlaps).
    - compute_dense_index evaluates placement density for rewarding efficient gap-filling.
    """

    def __init__(self, radius: int):
        """
        Construct a HexEngine with the specified radius and default colors.

        Populates the hexagonal block grid with valid blocks, tested via Block.in_range.
        Blocks are inserted in row-major order (by i, then k) for binary search efficiency.

        Args:
            radius: Radius of the hexagonal grid, must be greater than 1.
        """
        self._radius = radius
        # Calculate array size: Aₖ = 1 + 3*r*(r-1)
        self._blocks = [Block()] * (1 + 3 * radius * (radius - 1))
        # Populate grid
        index = 0
        for a in range(radius * 2):
            for b in range(radius * 2):
                block = Block(Hex())
                block.move_i(b)
                block.move_k(a)
                if block.in_range(radius):
                    self._blocks[index] = block
                    index += 1
        # Blocks are sorted by I, then K

    def reset(self) -> None:
        """Reset all blocks to their default state and color."""
        new_blocks = [Block(block) for block in self._blocks]
        self._blocks = new_blocks

    def get_radius(self) -> int:
        """Return the radius of the grid."""
        return self._radius

    # HexGrid implementation
    def length(self) -> int:
        """Return the number of blocks in the grid."""
        return len(self._blocks) if self._blocks is not None else 0

    def blocks(self) -> List[Block]:
        """Return all blocks in the grid."""
        return self._blocks

    def in_range(self, __hex__: Hex) -> bool:
        """Check if a hexagonal coordinate is within the grid bounds."""
        return __hex__.in_range(self._radius)

    def get_block(self, *args) -> Block | None:
        """
        Retrieve a Block at the given Hex coordinate or array index.

        Args:
            *args: Either a Hex object or a single index.

        Returns:
            Block: The Block if found, or None if out of range.
        """
        if len(args) == 1 and isinstance(args[0], int):
            return self._blocks[args[0]]
        elif len(args) == 1 and isinstance(args[0], Hex):
            __hex__ = args[0]
            if self.in_range(__hex__):
                index = self._search(__hex__.get_line_i(), __hex__.get_line_k(), 0, self.length() - 1)
                if index >= 0:
                    return self._blocks[index]
        return None

    def set_block(self, __hex__: Hex, block: Block) -> None:
        """
        Set the Block at a specific grid coordinate using binary search.

        Args:
            __hex__: The Hex coordinate.
            block: The new Block to place.
        """
        if self.in_range(__hex__):
            index = self._search(__hex__.get_line_i(), __hex__.get_line_k(), 0, self.length() - 1)
            if index >= 0:
                self._blocks[index] = block

    def set_state(self, __hex__: Hex, state: bool) -> None:
        """
        Set the state of a Block at a specific grid coordinate using binary search.

        Automatically sets the color based on state (-2 for True, -1 for False).

        Args:
            __hex__: The Hex coordinate.
            state: The new state (True = occupied).
        """
        if self.in_range(__hex__):
            index = self._search(__hex__.get_line_i(), __hex__.get_line_k(), 0, self.length() - 1)
            if index >= 0:
                block = self._blocks[index]
                if block.get_state() != state:
                    block.set_state(state)
                    block.set_color(-2 if state else -1)

    def _search(self, i: int, k: int, start: int, end: int) -> int:
        """
        Perform a binary search to locate a block at (i, k).

        Assumes the array is sorted by I, then K.

        Args:
            i: I coordinate.
            k: K coordinate.
            start: Search range start index.
            end: Search range end index.

        Returns:
            int: Index of the block, or -1 if not found.
        """
        if start > end:
            return -1
        mid = (start + end) // 2
        block = self._blocks[mid]
        if block.get_line_i() == i and block.get_line_k() == k:
            return mid
        elif block.get_line_i() < i or (block.get_line_i() == i and block.get_line_k() < k):
            return self._search(i, k, mid + 1, end)
        else:
            return self._search(i, k, start, mid - 1)

    def check_add(self, origin: Hex, other: HexGrid) -> bool:
        """
        Check if another grid can be added at the given origin without overlap or out-of-bounds.

        Args:
            origin: Origin offset for placement.
            other: The other HexGrid to check.

        Returns:
            bool: True if placement is valid.
        """
        for block in other.blocks():
            if block is not None and block.get_state():
                placed_block = block + origin
                target = self.get_block(placed_block)
                if target is None or target.get_state():
                    return False
        return True

    def add(self, origin: Hex, other: HexGrid) -> None:
        """
        Add another grid to this grid at the given origin.

        Modifies the grid permanently. Throws an exception if placement is invalid.

        Args:
            origin: Offset for placement.
            other: The grid to add.

        Raises:
            ValueError: If placement is out of bounds or overlaps.
        """
        for block in other.blocks():
            if block is not None and block.get_state():
                placed_block = block + origin
                target = self.get_block(placed_block)
                if target is None:
                    raise ValueError("Block out of grid when adding")
                if target.get_state():
                    raise ValueError("Cannot add into existing block")
                self.set_block(placed_block, placed_block)

    def check_positions(self, other: HexGrid) -> List[Hex]:
        """
        Return all valid positions where another grid can be added.

        Args:
            other: The HexGrid to place.

        Returns:
            List[Hex]: List of possible Hex origins for valid placement.
        """
        positions = []
        for block in self._blocks:
            hex_coord = block.__this__()
            if self.check_add(hex_coord, other):
                positions.append(hex_coord)
        return positions

    def eliminate(self) -> List[Block]:
        """
        Eliminate fully occupied lines along I, J, or K axes and return eliminated blocks.

        Modifies the grid permanently.

        Returns:
            List[Block]: Blocks eliminated.
        """
        eliminate = []
        # Check I
        for i in range(self._radius * 2 - 1):
            line = [b for b in self._blocks if b.get_line_i() == i]
            if all(b.get_state() for b in line):
                eliminate.extend(line)
        # Check J
        for j in range(1 - self._radius, self._radius):
            line = [b for b in self._blocks if b.get_line_j() == j]
            if all(b.get_state() for b in line):
                eliminate.extend(line)
        # Check K
        for k in range(self._radius * 2 - 1):
            line = [b for b in self._blocks if b.get_line_k() == k]
            if all(b.get_state() for b in line):
                eliminate.extend(line)
        # Eliminate
        eliminated = []
        for block in eliminate:
            eliminated.append(block.__copy__())
            self.set_state(block, False)
        return eliminated

    def check_eliminate(self) -> bool:
        """
        Check if any full line can be eliminated.

        Returns:
            bool: True if at least one line is full.
        """
        for i in range(self._radius * 2 - 1):
            if self.check_eliminate_i(i):
                return True
        for j in range(1 - self._radius, self._radius):
            if self.check_eliminate_j(j):
                return True
        for k in range(self._radius * 2 - 1):
            if self.check_eliminate_k(k):
                return True
        return False

    def check_eliminate_i(self, i: int) -> bool:
        """
        Check if the entire line of constant I can be eliminated.

        Args:
            i: The I-line to check.

        Returns:
            bool: True if all blocks are filled.
        """
        return all(b.get_state() for b in self._blocks if b.get_line_i() == i)

    def check_eliminate_j(self, j: int) -> bool:
        """
        Check if the entire line of constant J can be eliminated.

        Args:
            j: The J-line to check.

        Returns:
            bool: True if all blocks are filled.
        """
        return all(b.get_state() for b in self._blocks if b.get_line_j() == j)

    def check_eliminate_k(self, k: int) -> bool:
        """
        Check if the entire line of constant K can be eliminated.

        Args:
            k: The K-line to check.

        Returns:
            bool: True if all blocks are filled.
        """
        return all(b.get_state() for b in self._blocks if b.get_line_k() == k)

    def compute_dense_index(self, origin: Hex, other: HexGrid) -> float:
        """
        Compute a density index score for hypothetically placing another grid.

        Returns a value between 0 and 1 representing surrounding density.
        A score of 1 means all surrounding blocks would be filled, 0 means the grid would be alone.

        Args:
            origin: Position for hypothetical placement.
            other: The HexGrid to evaluate.

        Returns:
            float: Density index (0 to 1), or 0 if placement is invalid or no neighbors exist.
        """
        total_possible = 0
        total_populated = 0
        for block in other.blocks():
            if block is not None and block.get_state():
                placed_block = block + origin
                target = self.get_block(placed_block)
                if target is None or target.get_state():
                    return 0.0
                total_possible += 6 - other.count_neighbors(placed_block, False)
                total_populated += self.count_neighbors(placed_block, True)
        return total_populated / total_possible if total_possible > 0 else 0.0

    def _get_pattern(self, __hex__: Hex, include_null: bool) -> int:
        """
        Determine the pattern of blocks around the given position in the hexagonal grid, including the block itself.

        This method checks up to seven positions in a hexagonal box centered at coordinates (i, k).
        It returns a value representing the pattern of occupied/unoccupied blocks, ignoring block colors.
        The pattern is encoded as a 7-bit integer (0 to 127) based on the state of the central block
        and its six neighbors. If a neighboring position is out of range or contains a None block,
        it is treated as occupied or unoccupied based on the include_null flag.

        Args:
            __hex__ (Hex): The hex coordinate of the block at the center of the box.
            include_null (bool): Whether to treat None or out-of-bounds neighbors as occupied (True) or unoccupied (False).

        Returns:
            int: A number in the range [0, 127] representing the pattern of blocks in the hexagonal box.
        """
        pattern = 0
        if self.in_range(__hex__.shift_j(-1)):
            if self.get_block(__hex__.shift_j(-1)).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        pattern <<= 1
        if self.in_range(__hex__.shift_i(-1)):
            if self.get_block(__hex__.shift_i(-1)).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        pattern <<= 1
        if self.in_range(__hex__.shift_k(-1)):
            if self.get_block(__hex__.shift_k(-1)).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        pattern <<= 1
        if self.in_range(__hex__):
            if self.get_block(__hex__).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        pattern <<= 1
        if self.in_range(__hex__.shift_k(1)):
            if self.get_block(__hex__.shift_k(1)).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        pattern <<= 1
        if self.in_range(__hex__.shift_i(1)):
            if self.get_block(__hex__.shift_i(1)).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        pattern <<= 1
        if self.in_range(__hex__.shift_j(1)):
            if self.get_block(__hex__.shift_j(1)).get_state():
                pattern += 1
        elif include_null:
            pattern += 1
        return pattern

    def compute_entropy(self) -> float:
        """
        Compute the entropy of the hexagonal grid based on the distribution of 7-block patterns.

        Entropy is calculated using the Shannon entropy formula, measuring the randomness of block
        arrangements in the grid. Each pattern consists of a central block and its six neighbors,
        forming a 7-block hexagonal box, as defined by the _get_pattern method. The entropy reflects
        the diversity of these patterns: a grid with randomly distributed filled and empty blocks
        has higher entropy than one with structured patterns (e.g., all blocks in a line or cluster).
        A grid with all blocks filled or all empty has zero entropy. Inverting the grid (swapping
        filled and empty states) results in the same entropy, as the pattern distribution is unchanged.

        The method iterates over all blocks within the grid's radius (excluding the outermost layer
        to ensure all neighbors are in range), counts the frequency of each possible 7-block pattern
        (2^7 = 128 patterns), and computes the entropy using the Shannon entropy formula:
            H = -Σ (p * log₂(p))
        where p is the probability of each pattern (frequency divided by total patterns counted).
        Blocks on the grid's boundary (beyond radius - 1) are excluded to avoid incomplete patterns.

        Returns:
            float: The entropy of the grid in bits, a non-negative value representing the randomness
                   of block arrangements. Returns 0.0 for a uniform grid (all filled or all empty)
                   or if no valid patterns are counted.
        """
        entropy = 0.0
        pattern_total = 0
        pattern_counts = [0] * 128  # 2^7 because there are 128 possible patterns
        for block in self._blocks:
            if block.__this__().shift_j(1).in_range(self.get_radius() - 1):
                # If it is possible to check patterns without going out of bounds
                pattern_total += 1
                pattern_counts[self._get_pattern(block.__this__(), False)] += 1
        for count in pattern_counts:
            if count > 0:
                p = count / pattern_total
                entropy -= p * log2(p)
        return entropy

    def compute_entropy_index(self, origin: Hex, other: HexGrid) -> float:
        """
        Compute an entropy-based index score for hypothetically placing another grid.

        The entropy index is a value between 0 and 1 that measures the change in the grid's entropy
        when the specified other grid is placed at the given origin coordinate. This index is derived
        by computing the difference in Shannon entropy (as calculated by compute_entropy) between
        the current grid and a hypothetical grid state after adding other and performing an elimination
        step. The entropy difference is adjusted by a constant offset (0.21) and transformed using a
        sigmoid function to map the result to the range [0, 1]. The sigmoid function used is:
            f(x) = 1 / (1 + e^(-3 * x))
        where x is the adjusted entropy difference. A higher index indicates a placement that results
        in a grid configuration with significantly different pattern diversity, which may be useful in
        reinforcement learning or game strategy evaluation to prioritize moves that increase or maintain
        randomness in block arrangements.

        The method operates on a deep copy of the HexEngine to avoid modifying the current grid state.
        It adds the other grid at the specified origin, performs an elimination step (e.g., removing
        completed lines or clusters as per game rules), and calculates the entropy difference.

        Args:
            origin (Hex): The position in the grid where the other grid is hypothetically placed.
            other (HexGrid): The HexGrid representing the piece to be evaluated for placement.

        Returns:
            float: A value between 0 and 1 representing the entropy-based index score, where higher
                   values indicate a greater change in pattern diversity after placement and elimination.

        Raises:
            ValueError: If the piece placement is invalid.
        """
        # Test move on a deep copy to avoid affecting the current grid
        copy_engine = copy.deepcopy(self)
        copy_engine.add(origin, other)
        copy_engine.eliminate()
        x = copy_engine.compute_entropy() - self.compute_entropy() - 0.21
        # Sigmoid: 1/(1+e^-k(x)) k=3
        return 1 / (1 + exp(-3 * x))

    def __str__(self) -> str:
        """Return a string representation of the grid color and block states."""
        str_builder = ["HexEngine[blocks = {"]
        if self._blocks:
            str_builder.append(self._blocks[0].basic_str())
        for block in self._blocks[1:]:
            str_builder.append(", ")
            str_builder.append(block.basic_str())
        str_builder.append("}]")
        return "".join(str_builder)

    def __copy__(self):
        """Return a deep copy of this HexEngine."""
        new_engine = HexEngine(self._radius)
        new_engine._blocks = [block.__copy__() for block in self._blocks]
        return new_engine

    def __deepcopy__(self, memo):
        """Return a deep copy of this HexEngine."""
        new_engine = HexEngine(self._radius)
        new_engine._blocks = [copy.deepcopy(block, memo) for block in self._blocks]
        return new_engine