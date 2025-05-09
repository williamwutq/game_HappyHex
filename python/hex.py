import math
from abc import ABC, abstractmethod


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
        return self._x == other.__i__ + 2 and self._y == other.__k__ - 1

    def front_j(self, other):
        """Check if this Hex is one unit higher on J-axis."""
        return self._x == other.__i__ + 1 and self._y == other.__k__ + 1

    def front_k(self, other):
        """Check if this Hex is one unit higher on K-axis."""
        return self._x == other.__i__ - 1 and self._y == other.__k__ + 2

    def back_i(self, other):
        """Check if this Hex is one unit lower on I-axis."""
        return self._x == other.__i__ - 2 and self._y == other.__k__ + 1

    def back_j(self, other):
        """Check if this Hex is one unit lower on J-axis."""
        return self._x == other.__i__ - 1 and self._y == other.__k__ - 1

    def back_k(self, other):
        """Check if this Hex is one unit lower on K-axis."""
        return self._x == other.__i__ + 1 and self._y == other.__k__ - 2

    def equals(self, other):
        """Check if this Hex equals another Hex."""
        return self._x == other.__i__ and self._y == other.__k__

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
    def add(self, other):
        """Return new Hex with summed coordinates."""
        return Hex(self._x + other.__i__, self._y + other.__k__)

    def subtract(self, other):
        """Return new Hex with subtracted coordinates."""
        return Hex(self._x - other.__i__, self._y - other.__k__)

    def set(self, other):
        """Set this Hex to another Hex's coordinates."""
        self._x = other.__i__
        self._y = other.__j__

    def this_hex(self):
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

        Args:
            __hex__: Hex object.
            color: k-coordinate or color index.
            state: State boolean.
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

    def __basic_str(self):
        """Return minimal string representation with line coordinates and state."""
        return (f"{{{self.get_line_i()}, {self.get_line_j()}, {self.get_line_k()}, "
                f"{self._state}}}")

    def __copy__(self):
        """Return a copy of this Block."""
        block = Block(self.this_hex(), self._color, self._state)
        return block

    def __deepcopy__(self, memo):
        """Return a deep copy of this Block."""
        block = Block(self.this_hex(), self._color, self._state)
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
    def add(self, other):
        """Return new Block with summed coordinates."""
        return Block(self.this_hex().add(other), self._color, self._state)

    def subtract(self, other):
        """Return new Block with subtracted coordinates."""
        return Block(self.this_hex().subtract(other), self._color, self._state)


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

        Args:
            __hex__: The Hex coordinate.

        Returns:
            bool: True if coordinates are within range, False otherwise.
        """
        pass

    @abstractmethod
    def get_block(self, *args) -> Block:
        """
        Retrieve the Block at specified coordinates or index.

        Args:
            *args: Either Hex coordinates or a single index.

        Returns:
            Block: The block at the specified coordinates/index, or None if not found or out of range.
        """
        pass

    @abstractmethod
    def add(self, origin: Hex, other: 'HexGrid') -> None:
        """
        Add all blocks from another HexGrid to this grid, aligning them based on a specified Hex coordinate.

        Args:
            origin: The reference Hex position for alignment.
            other: The other HexGrid to merge into this grid.

        Raises:
            ValueError: If the grids cannot be merged due to alignment issues.
        """
        pass

    def add_without_shift(self, other: 'HexGrid') -> None:
        """
        Add all blocks from another HexGrid to this grid without shifting positions.

        Args:
            other: The other HexGrid to merge into this grid.

        Raises:
            ValueError: If the grids cannot be merged.
        """
        self.add(Hex(), other)

    def count_neighbors(self, __hex__: Hex, include_null: bool) -> int:
        """
        Count occupied neighboring Blocks around the given Hex position.

        Checks up to six adjacent positions to the block at Hex coordinate.
        A neighbor is occupied if the block is non-null and its state is True.
        Null or out-of-bounds neighbors are counted as occupied if include_null is True.

        Args:
            __hex__: The Hex coordinate.
            include_null: Treat null/out-of-bounds neighbors as occupied (True) or unoccupied (False).

        Returns:
            int: Number of occupied neighbors. Returns 0 if the center position is out of range.
        """
        count = 0
        if self.in_range(__hex__):
            __i__ = __hex__.__i__()
            __k__ = __hex__.__k__()
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

        Args:
            length: Number of blocks this piece can hold (minimum 1). Defaults to 1.
            color: Color index for this piece's blocks. Defaults to None for default constructor.
        """
        if length < 1:
            length = 1
        self._blocks = [None] * length
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

        Args:
            block_or_hex: Block to add or Hex coordinates for a new block.

        Returns:
            bool: True if the block was added, False if the piece is full.
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

        Args:
            *args: Either a Hex object or a single index.

        Returns:
            Block: The block at the specified coordinates/index, or None if not found.
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

        Args:
            __hex__: The Hex coordinate.

        Returns:
            bool: True if the block exists and is occupied, False otherwise.
        """
        block = self.get_block(__hex__)
        return block is not None and block.get_state()

    def add(self, origin: Hex, other: HexGrid) -> None:
        """
        Prohibited operation: Pieces cannot be merged with other grids.

        Args:
            origin: The reference Hex position for alignment.
            other: The other HexGrid to merge.

        Raises:
            ValueError: Always, as merging grids with pieces is not allowed.
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
            str_builder.append("null" if self._blocks[0] is None else self._blocks[0].__basic_str())
        for block in self._blocks[1:]:
            str_builder.append(", ")
            str_builder.append("null" if block is None else block.__basic_str())
        str_builder.append("}")
        return "".join(str_builder)

    def to_byte(self) -> int:
        """
        Return a byte representation of the blocks in a standard 7-Block piece.

        Empty blocks are 0s, filled blocks are 1s. Does not record color.

        Returns:
            int: A byte representing this 7-block piece, with the first bit empty.
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

        Args:
            data: Byte data representing the piece.
            color: Color for this piece's blocks.

        Returns:
            Piece: A piece constructed from the byte data with the specified color.

        Raises:
            ValueError: If data represents an empty piece or has an extra bit.
        """
        if data < 0:
            raise ValueError("Data must have empty most significant bit")
        if data == 0:
            raise ValueError("Data must contain at least one block")

        count = bin(data).count("1")
        piece = Piece(count, color)
        if data >> 6 & 1: piece.add(Hex.hex(-1, -1))
        if data >> 5 & 1: piece.add(Hex.hex(-1, 0))
        if data >> 4 & 1: piece.add(Hex.hex(0, -1))
        if data >> 3 & 1: piece.add(Hex.hex(0, 0))
        if data >> 2 & 1: piece.add(Hex.hex(0, 1))
        if data >> 1 & 1: piece.add(Hex.hex(1, 0))
        if data & 1: piece.add(Hex.hex(1, 1))
        return piece

    def equals(self, piece: 'Piece') -> bool:
        """
        Compare this piece to another for equality.

        Two pieces are equal if they have the same length and identical Block positions.
        Color index is not considered.

        Args:
            piece: The other piece to compare to.

        Returns:
            bool: True if pieces are structurally equal, False otherwise.
        """
        if piece.length() != self.length():
            return False
        self._sort()
        piece._sort()
        for i in range(self.length()):
            if not self._blocks[i].equals(piece._blocks[i]):
                return False
        return True