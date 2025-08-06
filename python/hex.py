'''
This module implements a simplified and optimized hexagonal grid system designed for machine learning applications. 
This module is duplicated from the hpyhexml repository, with modifications to improve memory efficiency and performance.
This module does not contain colors, as it is unnecessary for this python script to implement graphical features.
The system is built with memory efficiency and computational performance in mind, leveraging several key optimizations:
1. Flyweight Pattern in Hex Class:
    - The `Hex` class uses a flyweight pattern to cache instances of small hexagonal coordinates, reducing memory usage.
    - It directly applies line coordinates (i, k) instead of raw coordinates (i, j, k), simplifying calculations and improving performance.
2. Pre-Cached Singleton Piece Class:
    - The `Piece` class represents a logical structure of 7 blocks using a pre-cached singleton pattern.
    - All possible states (0-127) are precomputed and stored, allowing for efficient reuse and memory savings.
3. Boolean-Based Grid Representation:
    - Neither the `Piece` class nor the `HexEngine` class stores actual block data.
    - Instead, they use boolean arrays to represent the occupancy state of blocks, significantly reducing memory overhead.
4. Enhanced Compatibility:
    - The system is designed to be compatible with tuples and lists, making it easier to integrate with other Python libraries and frameworks.
5. Comprehensive Documentation:
    - The module is thoroughly documented, providing clear explanations of the design, functionality, and usage of each class and method.
Classes:
    - `Hex`: Represents a hexagonal grid coordinate using a custom line-based coordinate system.
    - `Piece`: Represents a shape or unit made up of 7 blocks, optimized with a pre-cached singleton pattern.
    - `HexEngine`: Provides a complete engine for managing a hexagonal grid, including operations like adding pieces, eliminating lines, and computing entropy.
Usage:
    This module is ideal for applications requiring efficient hexagonal grid management, such as games, simulations, or machine learning tasks.
'''

from typing import Union

'''
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
'''

class Hex:
    '''
    Represents a hexagonal grid coordinate using a custom line-based coordinate system.

    This class models hexagonal positions with two line coordinates (i, k), implicitly
    defining the third axis (j) as `j = k - i` to maintain hex grid constraints.
    It supports standard arithmetic, equality, and hashing operations, as well as
    compatibility with coordinate tuples.

    For small grids, Hex instances are cached for performance, allowing more efficient memory usage
    and faster access. The caching is limited to a range of -64 to 64 for both i and k coordinates.

    Use of Hex over tuples is recommended for clarity and to leverage the singleton feature of small Hexes.

    Coordinate Systems:
        - Raw Coordinates (i, j, k): Three axes satisfying i + j + k = 0, where
          each axis is diagonal to the others at 60° increments.
        - Line Coordinates (i, k): Derived coordinates representing distances
          perpendicular to axes, simplifying grid operations.

    Note:
        - This class is immutable and optimized with __slots__.
        - Raw coordinate methods (__i__, __j__, __k__) are retained for backward compatibility.
        - Only basic functionality is implemented; complex adjacency, iteration,
          and mutability features are omitted for simplicity.

    Attributes:
        i (int): The line i coordinate.
        j (int): The computed line j coordinate (k - i).
        k (int): The line k coordinate.
    '''
    __slots__ = ('__i', '__k')
    __cache_min = -64
    __cache_max = 64
    __cache = {}

    def __new__(cls, i=0, k=0) -> 'Hex':
        if not isinstance(i, int) or not isinstance(k, int):
            raise TypeError("Coordinates must be integers")
        if cls.__cache_min <= i <= cls.__cache_max and cls.__cache_min <= k <= cls.__cache_max:
            key = (i, k)
            if key not in cls.__cache:
                instance = super().__new__(cls)
                instance.__i = i
                instance.__k = k
                cls.__cache[key] = instance
            return cls.__cache[key]
        # Fallback for out-of-range Hexes (no caching)
        instance = super().__new__(cls)
        instance.__i = i
        instance.__k = k
        return instance
    
    def __init__(self, i: int=0, k: int=0) -> None:
        '''
        Initialize a Hex coordinate at (i, k). Defaults to (0, 0).
        Arguments:
            i (int): The I-line coordinate of the hex.
            k (int): The K-line coordinate of the hex.
        Returns:
            None
        Raises:
            TypeError: If i or k is not an integer.
        '''
        pass # __new__ handles initialization and caching, so __init__ is empty.
    
    @property
    def i(self) -> int:
        '''
        Get the I-line coordinate of the hex.
        Returns:
            int: The I-line coordinate.
        '''
        return self.__i
    
    @property
    def j(self) -> int:
        '''
        Get the J-line coordinate of the hex.
        Returns:
            int: The J-line coordinate.
        '''
        return self.__k - self.__i
    
    @property
    def k(self) -> int:
        '''
        Get the K-line coordinate of the hex.
        Returns:
            int: The K-line coordinate.
        '''
        return self.__k
    
    def __iter__(self) -> iter:
        '''
        Return an iterator over the hex coordinates.
        Yields:
            int: The I-line coordinate of the hex.
            int: The K-line coordinate of the hex.
        '''
        yield self.__i
        yield self.__k
    
    def __i__(self) -> int:
        '''
        Return the raw i coordinate of the hex.
        Returns:
            int: The raw i coordinate.
        '''
        return self.__k * 2 - self.__i
    
    def __j__(self) -> int:
        '''
        Return the raw j coordinate of the hex.
        Returns:
            int: The raw j coordinate.
        '''
        return self.__i + self.__k
    
    def __k__(self) -> int:
        '''
        Return the raw k coordinate of the hex.
        Returns:
            int: The raw k coordinate.
        '''
        return self.__i * 2 - self.__k
    
    def __str__(self) -> str:
        '''
        Return a string representation of the hex coordinates.
        Format: Hex(i, j, k), where i, j, and k are the line coordinates.
        Returns:
            str: The string representation of the hex.
        '''
        return f"Hex({self.__i}, {self.j}, {self.__k})"
    
    def __repr__(self) -> str:
        '''
        Return a string representation of the hex coordinates for debugging.
        Format: Hex(i, j, k), where i, j, and k are the line coordinates.
        Returns:
            str: The string representation of the hex.
        '''
        return f"({self.__i}, {self.__k})"
    
    def __eq__(self, value: Union['Hex', tuple]) -> bool:
        '''
        Check equality with another Hex or a tuple of coordinates.
        Arguments:
            value (Hex or tuple): The value to compare with.
        Returns:
            bool: True if the coordinates match, False otherwise.
        '''
        if isinstance(value, Hex):
            return self.__i == value.__i and self.__k == value.__k
        elif isinstance(value, tuple) and len(value) == 2:
            return self.__i == value[0] and self.__k == value[1]
        return False
    
    def __hash__(self) -> int:
        '''
        Return a hash of the hex coordinates.
        Returns:
            int: The hash value of the hex coordinates.
        '''
        return hash((self.__i, self.__k))
    
    def __add__(self, other: Union['Hex', tuple]) -> 'Hex':
        '''
        Add another Hex or a tuple of coordinates to this hex.
        Arguments:
            other (Hex or tuple): The value to add.
        Returns:
            Hex: A new Hex with the added coordinates.
        Raises:
            TypeError: If the other operand is not a Hex or a tuple of coordinates.
        '''
        if isinstance(other, Hex):
            return Hex(self.__i + other.__i, self.__k + other.__k)
        elif isinstance(other, tuple) and len(other) == 2:
            return Hex(self.__i + other[0], self.__k + other[1])
        raise TypeError("Unsupported type for addition with Hex")
    
    def __radd__(self, other: Union['Hex', tuple]) -> 'Hex':
        '''
        Reverse addition of this hex to another Hex or a tuple.
        Arguments:
            other (Hex or tuple): The value to add this hex to.
        Returns:
            Hex: A new Hex with the added coordinates.
        Raises:
            TypeError: If the other operand is not a Hex or a tuple of coordinates.
        '''
        if isinstance(other, Hex):
            return Hex(other.__i + self.__i, other.__k + self.__k)
        elif isinstance(other, tuple) and len(other) == 2:
            return Hex(other[0] + self.__i, other[1] + self.__k)
        raise TypeError("Unsupported type for reverse addition with Hex")
    
    def __sub__(self, other: Union['Hex', tuple]) -> 'Hex':
        '''
        Subtract another Hex or a tuple of coordinates from this hex.
        Arguments:
            other (Hex or tuple): The value to subtract.
        Returns:
            Hex: A new Hex with the subtracted coordinates.
        Raises:
            TypeError: If the other operand is not a Hex or a tuple of coordinates.
        '''
        if isinstance(other, Hex):
            return Hex(self.__i - other.__i, self.__k - other.__k)
        elif isinstance(other, tuple) and len(other) == 2:
            return Hex(self.__i - other[0], self.__k - other[1])
        raise TypeError("Unsupported type for subtraction with Hex")
    
    def __rsub__(self, other: Union['Hex', tuple]) -> 'Hex':
        '''
        Reverse subtraction of this hex from another Hex or a tuple.
        Arguments:
            other (Hex or tuple): The value to subtract this hex from.
        Returns:
            Hex: A new Hex with the subtracted coordinates.
        Raises:
            TypeError: If the other operand is not a Hex or a tuple of coordinates.
        '''
        if isinstance(other, Hex):
            return Hex(other.__i - self.__i, other.__k - self.__k)
        elif isinstance(other, tuple) and len(other) == 2:
            return Hex(other[0] - self.__i, other[1] - self.__k)
        raise TypeError("Unsupported type for reverse subtraction with Hex")
    
    def __copy__(self) -> 'Hex':
        '''
        Create a copy of this Hex.
        Returns:
            Hex: A new Hex with the same coordinates.
        '''
        return Hex(self.__i, self.__k)
    
    def __deepcopy__(self, memo=None) -> 'Hex':
        '''
        Create a deep copy of this Hex.
        Arguments:
            memo (dict): A dictionary to keep track of copied objects.
        Returns:
            Hex: A new Hex with the same coordinates.
        '''
        if memo is None:
            memo = {}
        if id(self) in memo:
            return memo[id(self)]
        new_hex = Hex(self.__i, self.__k)
        memo[id(self)] = new_hex
        return new_hex
    
    def __bool__(self) -> bool:
        '''
        Check if the Hex is not at the origin (0, 0).
        Returns:
            bool: True if the Hex is not at the origin, False otherwise.
        '''
        return self.__i != 0 or self.__k != 0
    
    def shift_i(self, units: int) -> 'Hex':
        '''
        Return a new Hex shifted along the i-axis by units.
        Arguments:
            units (int): The number of units to shift along the i-axis.
        Returns:
            Hex: A new Hex shifted by the specified units along the i-axis.
        Raises:
            TypeError: If units is not an integer.
        '''
        return Hex(self.__i + units, self.__k)
    
    def shift_j(self, units: int) -> 'Hex':
        '''
        Return a new Hex shifted along the j-axis by units.
        Arguments:
            units (int): The number of units to shift along the j-axis.
        Returns:
            Hex: A new Hex shifted by the specified units along the j-axis.
        Raises:
            TypeError: If units is not an integer.
        '''
        return Hex(self.__i - units, self.__k + units)
    
    def shift_k(self, units: int) -> 'Hex':
        '''
        Return a new Hex shifted along the k-axis by units.
        Arguments:
            units (int): The number of units to shift along the k-axis.
        Returns:
            Hex: A new Hex shifted by the specified units along the k-axis.
        Raises:
            TypeError: If units is not an integer.
        '''
        return Hex(self.__i, self.__k + units)
    

class Piece:
    '''
    Represents a shape or unit made up of 7 Block instances,
    typically forming a logical structure such as a game piece.

    This implementation of piece contains no blocks, and instead only contains 
    a list of boolean values representing the occupancy state of each block.

    This is a singleton class, meaning that each unique Piece state is cached
    and reused to save memory and improve performance.

    Attributes:
        positions (list[Hex]): A list of Hex coordinates representing the positions of the blocks in the piece.
        states (list[bool]): A list of boolean values representing the occupancy state of each block in the piece.
    '''
    __slots__ = ('__states',)
    __cache = {}
    positions = (Hex(-1, -1), Hex(-1, 0), Hex(0, -1), Hex(0, 0), Hex(0, 1), Hex(1, 0), Hex(1, 1))

    def __new__(cls, states: Union[list[bool], int] = None) -> 'Piece':
        if isinstance(states, int):
            key = states
        elif states is None:
            key = 0
        elif isinstance(states, list) and len(states) == 7 and all(isinstance(s, bool) for s in states):
            key = sum((1 << (6 - i)) if s else 0 for i, s in enumerate(states))
        else:
            raise TypeError("Invalid type for states")
        if key not in cls.__cache:
            raise ValueError(f"Piece with state {key} has not been initialized in cache")
        return cls.__cache[key]
    
    @classmethod
    def __initialize_cache(cls) -> None:
        '''
        Initialize the cache with all possible Piece states (0-127).
        This method is called automatically when the class is defined.
        Returns:
            None
        '''
        for key in range(128):
            instance = object.__new__(cls)
            # Make __states immutable tuple
            instance.__states = tuple((key & (1 << (6 - i))) != 0 for i in range(7))
            cls.__cache[key] = instance
    
    def __init__(self, states: Union[list[bool], int] = None) -> None:
        '''
        Initialize a Piece with a list of occupancy states.
        Arguments:
            states (list[bool] | list[Block] | int, optional):
                - A list of 7 boolean values representing the occupancy state of each block.
                - A list of Block objects, where each Block's state is used to determine occupancy.
                - An integer between 0 and 127, where each bit represents the occupancy state of a block.
        Raises:
            TypeError: If states is not a list of boolean values, valid byte, or Block objects.
        Returns:
            None
        '''
        pass # This is a singleton class, so the __new__ method handles initialization.

    def __repr__(self) -> str:
        '''
        Return a string representation of the Piece in byte format.
        This representation is useful for debugging and serialization.
        Returns:
            str: A string representation of the Piece in byte format.
        '''
        return str(self.__int__())
    
    def __str__(self) -> str:
        '''
        Return a string representation of the Piece.
        Format: Piece{Block(i, j, k, state), ...}, where i, j, and k are the line coordinates of each block,
        and state is the occupancy state, if occupied, else "null".
        Returns:
            str: The string representation of the Piece.
        '''
        return "Piece{" + ", ".join(
            f"({self.positions[i].i}, {self.positions[i].k}, {self.__states[i]})" for i in range(7)
        ) + "}"
    
    def __iter__(self) -> iter:
        '''
        Return an iterator over the occupancy states of the Piece.
        Yields:
            bool: The occupancy state of each block in the Piece.
        '''
        return iter(self.__states)

    def __len__(self) -> int:
        '''
        Return the number of occupied blocks in the Piece.
        Returns:
            int: The number of occupied blocks in the Piece.
        '''
        return sum(self.__states)
    
    def __bool__(self) -> bool:
        '''
        Check if the Piece has any occupied blocks.
        Returns:
            bool: True if any block is occupied, False otherwise.
        '''
        return any(self.__states)
    
    def __int__(self) -> int:
        '''
        Return a byte representation of the blocks in a standard 7-Block piece.
        Returns:
            int: A byte representation of the Piece, where each bit represents the occupancy state of a
        '''
        data = 0
        for i, state in enumerate(self.__states):
            data <<= 1
            if state:
                data += 1
        return data
    
    def states(self) -> tuple[bool, ...]:
        '''
        Get the tuple of boolean values representing the occupancy state of each block in the Piece.
        Returns:
            tuple[bool, ...]: The tuple of boolean values for the Piece.
        '''
        return self.__states
    
    def __eq__(self, other: Union['Piece', int]) -> bool:
        '''
        Returns True if the occupancy states match, False otherwise.
        Arguments:
            other (Piece): The Piece to compare with.
        Returns:
            bool: True if the occupancy states match, False otherwise.
        '''
        if isinstance(other, int):
            return self.__int__() == other
        if not isinstance(other, Piece):
            return False
        return self.__states == other.__states
    
    def __hash__(self) -> int:
        '''
        Return a hash of the Piece's occupancy states.
        This method directly uses the byte representation of the Piece to generate a hash value.
        Returns:
            int: The hash value of the Piece.
        '''
        return self.__int__()
    
    def count_neighbors(self, coo: Union[Hex, tuple]) -> int:
        '''
        Count occupied neighboring Blocks around the given Hex position.

        Checks up to six adjacent positions to the block at Hex coordinate.
        A neighbor is occupied if the block is non-null and its state is True.

        Parameters:
            coo (Hex | tuple): The Hex coordinate to check for neighbors.
        Returns:
            int: The count of occupied neighboring Blocks.
        Raises:
            TypeError: If coo is not a Hex or a tuple of coordinates.
        '''
        count = 0
        if isinstance(coo, tuple):
            if len(coo) == 2:
                coo = Hex(coo[0], coo[1])
            elif len(coo) == 3:
                coo = Hex(coo[0], coo[2])
            else:
                raise TypeError("Invalid type for Hex coordinates")
        elif not isinstance(coo, Hex):
            raise TypeError("Invalid type for Hex coordinates")
        try:
            if self.__states[self.positions.index(coo)]:
                for pos in self.positions:
                    try:
                        if self.__states[self.positions.index(pos + coo)]:
                            count += 1
                    except ValueError:
                        continue
            return count
        except ValueError:
            return 0

# Pre-populate the cache with all possible Piece states (0-127)
Piece._Piece__initialize_cache()


class HexEngine:
    '''
    The HexEngine class provides a complete engine for managing a two-dimensional hexagonal
    block grid used for constructing and interacting with hex-based shapes in the game.

    The engine does not actually contain any blocks, but instead contains a list of booleans
    representing the occupancy state of each block in the hexagonal grid. The correspondence is achieved
    through optimized indexing and coordinate transformations.

    Grid Structure:
    - Uses an axial coordinate system (i, k), where i - j + k = 0, and j is derived as j = i + k.
    - Three axes: I, J, K. I+ is 60° from J+, J+ is 60° from K+, K+ is 60° from I-.
    - Raw coordinates: distance along an axis multiplied by 2.
    - Line-coordinates (I, K) are perpendicular distances to axes, calculated from raw coordinates.
    - Blocks are stored in a sorted array by increasing raw coordinate i, then k.

    Grid Size:
    - Total blocks for radius r: Aₖ = 1 + 3*r*(r-1)
    - Derived from: Aₖ = Aₖ₋₁ + 6*(k-1); A₁ = 1

    Machine Learning:
    - Supports reward functions for evaluating action quality.
    - check_add discourages invalid moves (e.g., overlaps).
    - compute_dense_index evaluates placement density for rewarding efficient gap-filling.

    Attributes:
        radius (int): The radius of the hexagonal grid, defining the size of the grid.
        states (list[bool]): A list of booleans representing the occupancy state of each block in the grid.
    '''
    __slots__ = ('__radius', '__states')
    @staticmethod
    def __in_range(coo: Union[Hex, tuple], radius: int) -> bool:
        '''
        Check if a Hex coordinate is within the specified radius of the hexagonal grid.
        Arguments:
            coo: Hex coordinate to check.
            radius: Radius of the hexagonal grid.
        Returns:
            bool: True if the coordinate is within range, False otherwise.
        '''
        if isinstance(coo, Hex):
            i, j, k = coo.i, coo.j, coo.k
        elif isinstance(coo, tuple):
            if len(coo) == 2:
                i, k = coo[0], coo[1]
                j = k - i
            elif len(coo) == 3:
                i, j, k = coo
            else:
                return False
        return (0 <= i < radius * 2 - 1 and
                -radius < j < radius and
                0 <= k < radius * 2 - 1)
    
    @staticmethod
    def solve_radius(length: int) -> int:
        """
        Solves for the radius of a HexEngine based on its length.
        Arguments:
            length (int): The length of the hexagonal grid.
        Returns:
            int: The radius of the hexagonal grid, or -1 if the length is invalid.
        """
        valid_pairs = {
            7: 2, 19: 3, 37: 4, 61: 5, 91: 6, 127: 7, 169: 8, 217: 9, 271: 10, 331: 11, 397: 12, 469: 13
        }
        if length <= 1:
            return -1
        if 0 <= length <= 546:
            v = valid_pairs.get(length, -1)
            return v if v is not None else -1
        if length % 3 != 1:
            return -1
        target = (length - 1) // 3
        for x in range(1, target + 1):
            if x * (x - 1) == target:
                return x
        return -1
    
    def __init__(self, arg: Union[int, list[bool], str]) -> None:
        '''
        Construct a HexEngine with the specified radius.

        This method initializes the hexagonal grid with a given radius,
        creating an array of booleans to represent the grid.

        Arguments:
            arg (int | list[bool] | list[Block]): 
                - An integer representing the radius of the hexagonal grid.
                - A list of booleans representing the occupancy state of each block.
                - A string representation of the occupancy state, either as 'X'/'O' or '1'/'0'.
        Raises:
            TypeError: If radius is not an integer.
            ValueError: If radius is less than 1.
        '''
        if isinstance(arg, int):
            if arg < 1:
                raise ValueError("Radius must be greater than 0")
            self.__radius = arg
            self.__states = [False] * (1 + 3 * arg * (arg - 1))
        elif isinstance(arg, str):
            # Accept a string representation of either X/O or 1/0
            arg = arg.strip()
            if not all(c in "01" for c in arg) or all(c in "XO" for c in arg):
                raise ValueError("String must contain only '0' or '1', or 'X' or 'O'")
            self.__radius = self.solve_radius(len(arg))
            if self.__radius < 1:
                raise ValueError("Invalid length for hexagonal grid")
            self.__states = [c == '1' or c == 'X' for c in arg]
        elif isinstance(arg, list):
            if all(isinstance(s, bool) for s in arg):
                radius = self.solve_radius(len(arg))
                if radius < 1:
                    raise ValueError("Invalid length for hexagonal grid")
                self.__states = arg
            else:
                raise TypeError("Invalid type for HexEngine initialization")
        else:
            raise TypeError("Invalid type for HexEngine initialization")

    @property
    def radius(self) -> int:
        '''
        Get the radius of the hexagonal grid.
        Returns:
            int: The radius of the hexagonal grid.
        '''
        return self.__radius
    
    @property
    def states(self) -> list[bool]:
        '''
        Get the occupancy states of the hexagonal grid blocks.
        Returns:
            list[bool]: The occupancy states of the blocks in the grid.
        '''
        return self.__states
    
    def __eq__(self, value):
        '''
        Check equality with another HexEngine or a list of booleans.
        Returns True if the states match, False otherwise.
        Arguments:
            value (HexEngine | list[bool]): The HexEngine or list of booleans to compare with.
        Returns:
            bool: True if the states match, False otherwise.
        '''
        if isinstance(value, HexEngine):
            return self.__states == value.__states
        elif isinstance(value, list):
            return self.__states == value
        return False
    
    def __hash__(self) -> int:
        '''
        Return a hash of the HexEngine's occupancy states.
        This method uses the tuple representation of the states for hashing.
        Returns:
            int: The hash value of the HexEngine.
        '''
        return hash(tuple(self.__states))
    
    def __len__(self) -> int:
        '''
        Get the number of blocks in the hexagonal grid.
        Returns:
            int: The number of blocks in the grid.
        '''
        return len(self.__states)
    
    def __iter__(self) -> iter:
        '''
        Return an iterator over the occupancy states of the hexagonal grid blocks.
        Yields:
            bool: The occupancy state of each block in the grid.
        '''
        yield from self.__states
    
    def __repr__(self):
        '''
        Return a string representation of the grid block states.
        This representation is useful for debugging and serialization.
        Format: "1" for occupied blocks, "0" for unoccupied blocks.
        Returns:
            str: A string representation of the grid block states.
        '''
        return ''.join(['1' if b else '0' for b in self.__states])

    def __str__(self) -> str:
        '''
        Return a string representation of the grid block states.
        Format: "HexEngine[blocks = {block1, block2, ...}]",
        where each block is represented by its string representation.
        Returns:
            str: The string representation of the HexEngine.
        '''
        return "HexEngine[blocks = {" + ', '.join(
            f"({self.coordinate_block(i).i}, {self.coordinate_block(i).k}, {self.__states[i]})"
            for i in range(len(self.__states))) + "}]"

    def __copy__(self) -> 'HexEngine':
        '''
        Create a deep copy of the HexEngine.
        Returns:
            HexEngine: A new HexEngine instance with the same radius and blocks.
        '''
        new_engine = HexEngine(self.__radius)
        new_engine.__states = self.__states[:]
        return new_engine
    
    def __deepcopy__(self, memo=None) -> 'HexEngine':
        '''
        Create a deep copy of the HexEngine.
        Arguments:
            memo (dict): A dictionary to keep track of copied objects.
        Returns:
            HexEngine: A new HexEngine instance with the same radius and blocks.
        '''
        if memo is None:
            memo = {}
        if id(self) in memo:
            return memo[id(self)]
        new_engine = HexEngine(self.__radius)
        new_engine.__states = self.__states[:]
        memo[id(self)] = new_engine
        return new_engine
    
    def reset(self) -> None:
        '''
        Reset the HexEngine grid to its initial state, clearing all blocks.
        This method reinitializes the grid, setting all blocks to unoccupied.
        Returns:
            None
        '''
        self.__states = [False] * (1 + 3 * self.__radius * (self.__radius - 1))

    def in_range(self, coo: Union[Hex, tuple]) -> bool:
        '''
        Check if a Hex coordinate is within the radius of the hexagonal grid.
        Arguments:
            coo: Hex coordinate to check.
        Returns:
            bool: True if the coordinate is within range, False otherwise.
        '''
        return self.__in_range(coo, self.__radius)

    def index_block(self, coo: Union[Hex, tuple]) -> int:
        '''
        Get the index of the Block at the specified Hex coordinate.

        This method is heavily optimized for performance and achieves O(1) complexity by using direct index formulas
        based on the hexagonal grid's structure. It calculates the index based on the I and K coordinates of the Hex.
        Args:
            coo: The Hex coordinate.
        Returns:
            int: Index of the block, or -1 if not found.
        '''
        if self.in_range(coo):
            r = self.__radius
            if isinstance(coo, Hex):
                i = coo.i
                k = coo.k
            elif isinstance(coo, tuple):
                if len(coo) == 2:
                    i, k = coo[0], coo[1]
                elif len(coo) == 3:
                    i, k = coo[0], coo[2]
                else:
                    return -1
            if i < r:
                return k + i * r + i * (i - 1) // 2
            else:
                return k - (r - 1) ** 2 + i * r * 3 - i * (i + 5) // 2
        return -1
    
    def coordinate_block(self, index: int) -> Hex:
        '''
        Get the Hex coordinate of the Block at the specified index.

        This method retrieves the Hex coordinate based on the index in the hexagonal grid.
        If the index is out of range, raise ValueError.
        Args:
            index (int): The index of the block.
        Returns:
            Hex: The Hex coordinate of the block.
        Raises:
            ValueError: If the index is out of range.
        '''
        l = len(self.__states)
        r = self.__radius
        for i in range(r):
            if index < i + r:
                return Hex(i, index)
            index -= i + r
        for i in range(r):
            if index < 2 * r - 2 - i:
                return Hex(i + r, index + i + 1)
            index -= 2 * r - 2 - i
        else:
            raise ValueError("Index out of range")

    def get_state(self, coo: Union[Hex, tuple, int]) -> bool:
        '''
        Get the Block occupancy state at the specified Hex coordinate or index.

        This method retrieves the Block state based on either a Hex coordinate or an index.
        If the coordinate is out of range, raise ValueError.
        Args:
            coo: The Hex coordinate or index of the block.
        Returns:
            state (bool): The occupancy state at the specified coordinate or index.
        Raises:
            ValueError: If the coordinate is out of range or index is invalid.
            TypeError: If the coordinate type is unsupported.
        '''
        if isinstance(coo, (Hex, tuple)):
            index = self.index_block(coo)
            if index == -1:
                raise ValueError("Coordinate out of range")
            return self.__states[index]
        elif isinstance(coo, int):
            if 0 <= coo < len(self.__states):
                return self.__states[coo]
            else:
                raise ValueError("Coordinate out of range")
        else:
            raise TypeError("Unsupported type for block operation")
    
    def set_state(self, coo: Union[Hex, tuple, int], state: bool) -> None:
        '''
        Set the occupancy state of the Block at the specified Hex coordinate.

        This method updates the state of a Block at the given coordinate.
        If the coordinate is out of range, raise ValueError.

        Args:
            coo: The Hex coordinate of the block to set.
            state (bool): The new occupancy state to set for the block.

        Raises:
            ValueError: If the coordinate is out of range.
            TypeError: If the coordinate type is unsupported, or state is not a boolean.
        '''
        if not isinstance(state, bool):
            raise TypeError("State must be a boolean value")
        if isinstance(coo, Hex):
            # Get the index of the block using the optimized index_block method
            index = self.index_block(coo)
            if index == -1:
                raise ValueError("Coordinate out of range")
            self.__states[index] = state
            return
        elif isinstance(coo, tuple):
            if len(coo) == 2:
                index = self.index_block(coo)
            elif len(coo) == 3:
                # If coo is a 3-tuple, we need to know whether it represents a coordinate or a Block
                if isinstance(coo[2], bool):
                    index = self.index_block(coo[:2])
                elif isinstance(coo[2], int):
                    index = self.index_block(coo)
                else:
                    raise TypeError("Unsupported type for block operation")
            if index == -1:
                raise ValueError("Coordinate out of range")
            self.__states[index] = state
        elif isinstance(coo, int):
            if 0 <= coo < len(self.__states):
                self.__states[coo] = state
                return
            else:
                raise ValueError("Coordinate out of range")
        else:
            raise TypeError("Unsupported type for block operation")

    def check_add(self, coo: Union[Hex, tuple], piece: Union[Piece, int]) -> bool:
        '''
        Check if a Piece can be added to the hexagonal grid without overlaps.

        This method checks if the Piece can be placed on the grid without overlapping
        any existing occupied blocks. It returns True if the Piece can be added,
        otherwise returns False.

        Args:
            piece (Piece): The Piece to check for addition.
        Returns:
            bool: True if the Piece can be added, False otherwise.
        Raises:
            TypeError: If the piece is not a valid Piece instance.
        '''
        if isinstance(piece, int):
            piece = Piece(piece)
        elif not isinstance(piece, Piece):
            raise TypeError("Piece must be an instance of Piece or an integer representing a Piece state")
        
        for i in range(7):
            if piece.states()[i]:
                try:
                    if self.get_state(piece.positions[i] + coo):
                        return False
                except ValueError:
                    return False
        return True
    
    def add_piece(self, coo: Union[Hex, tuple], piece: Union[Piece, int]) -> None:
        '''
        Add a Piece to the hexagonal grid at the specified Hex coordinate.

        This method places the Piece on the grid, updating the occupancy state of
        the blocks based on the Piece's states. If the Piece cannot be added due to
        overlaps or out-of-range coordinates, it raises a ValueError.

        Args:
            piece (Piece): The Piece to add to the grid.
        Raises:
            TypeError: If the piece is not a valid Piece instance.
            ValueError: If the Piece cannot be added due to overlaps or out-of-range coordinates.
        '''
        if isinstance(piece, int):
            piece = Piece(piece)
        elif not isinstance(piece, Piece):
            raise TypeError("Piece must be an instance of Piece or an integer representing a Piece state")
        
        if not self.check_add(coo, piece):
            raise ValueError("Cannot add piece due to overlaps or out-of-range coordinates")
        
        for i in range(7):
            if piece.states()[i]:
                self.set_state(piece.positions[i] + coo, True)

    def check_positions(self, piece: Union[Piece, int]) -> list[Hex]:
        '''
        Return all valid positions where another grid can be added.

        This method returns a list of Hex coordinate candidates where the Piece can be added
        without overlaps. It checks each position in the Piece and returns the Hex coordinates
        of the occupied blocks.
        If the Piece is not valid, it raises a ValueError.

        Args:
            piece (Piece): The Piece to check for occupied positions.
        Returns:
            list[Hex]: A list of Hex coordinates for the occupied blocks in the Piece.
        Raises:
            TypeError: If the piece is not a valid Piece instance.
        '''
        if isinstance(piece, int):
            piece = Piece(piece)
        elif not isinstance(piece, Piece):
            raise TypeError("Piece must be an instance of Piece or an integer representing a Piece state")
        positions = []
        for a in range(self.__radius * 2):
            for b in range(self.__radius * 2):
                hex = Hex(a, b)
                if self.check_add(hex, piece):
                    positions.append(hex)
        return positions

    def eliminate(self) -> list[Hex]:
        '''
        Eliminate fully occupied lines along I, J, or K axes and return eliminated coordinates.

        Modifies the grid permanently.

        Returns:
            List[Hex]: coordinates eliminated.
        '''
        eliminate = []
        # Find candidates
        self.__eliminate_i(eliminate)
        self.__eliminate_j(eliminate)
        self.__eliminate_k(eliminate)
        # Eliminate
        for coo in eliminate:
            self.set_state(coo, False)
        return eliminate
    
    def __eliminate_i(self, eliminate : list[Hex]) -> None:
        '''
        Identify coordinates along I axis that can be eliminated and insert them into the input list

        Arguments:
            eliminate: The list of coordinates to insert into.
        '''
        r = self.__radius
        for i in range(r):
            _all_valid = True
            _index = i * (r * 2 + i - 1) // 2
            for b in range(r + i):
                if not self.__states[_index + b]:
                    _all_valid = False
                    break
            if _all_valid:
               eliminate.extend(self.coordinate_block(_index + b) for b in range(r + i))
        const_term = r * (r * 3 - 1) // 2
        for i in range(r - 2, -1, -1):
            _all_valid = True
            _index = const_term + (r - i - 2) * (r * 3 - 1 + i) // 2
            for b in range(r + i):
                if not self.__states[_index + b]:
                    _all_valid = False
                    break
            if _all_valid:
                eliminate.extend(self.coordinate_block(_index + b) for b in range(r + i))

    def __eliminate_j(self, eliminate : list[Hex]) -> None:
        '''
        Identify coordinates along J axis that can be eliminated and insert them into the input list

        Arguments:
            eliminate: The list of coordinates to insert into.
        '''
        radius = self.__radius
        for r in range(radius):
            _index = r
            _all_valid = True
            for c in range(1, radius):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += radius + c
            for c in range(radius - r):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += 2 * radius - c - 1
            if _all_valid:
                _index = r
                for c in range(1, radius):
                    eliminate.append(self.coordinate_block(_index))
                    _index += radius + c
                for c in range(radius - r):
                    eliminate.append(self.coordinate_block(_index))
                    _index += 2 * radius - c - 1
        for r in range(1, radius):
            _index = radius * r + r * (r - 1) // 2
            _start_index = _index
            _all_valid = True
            for c in range(1, radius - r):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += radius + c + r
            for c in range(radius):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += 2 * radius - c - 1
            if _all_valid:
                for c in range(1, radius - r):
                    eliminate.append(self.coordinate_block(_start_index))
                    _start_index += radius + c + r
                for c in range(radius):
                    eliminate.append(self.coordinate_block(_start_index))
                    _start_index += 2 * radius - c - 1

    def __eliminate_k(self, eliminate : list[Hex]) -> None:
        '''
        Identify coordinates along K axis that can be eliminated and insert them into the input list

        Args:
            eliminate: The list of coordinates to insert into.
        '''
        radius = self.__radius
        for r in range(radius):
            _index = r
            _all_valid = True
            for c in range(radius - 1):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += radius + c
            for c in range(r + 1):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += 2 * radius - c - 2
            if _all_valid:
                _index = r
                for c in range(radius - 1):
                    eliminate.append(self.coordinate_block(_index))
                    _index += radius + c
                for c in range(r + 1):
                    eliminate.append(self.coordinate_block(_index))
                    _index += 2 * radius - c - 2
        for r in range(1, radius):
            _index = radius * (r + 1) + r * (r + 1) // 2 - 1
            _start_index = _index
            _all_valid = True
            for c in range(r, radius - 1):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += radius + c
            for c in range(radius - 1, -1, -1):
                if not self.__states[_index]:
                    _all_valid = False
                    break
                _index += radius + c - 1
            if _all_valid:
                for c in range(r, radius - 1):
                    eliminate.append(self.coordinate_block(_start_index))
                    _start_index += radius + c
                for c in range(radius - 1, -1, -1):
                    eliminate.append(self.coordinate_block(_start_index))
                    _start_index += radius + c - 1

    def count_neighbors(self, coo: Union[Hex, tuple]) -> int:
        '''
        Count occupied neighboring Blocks around the given Hex position.

        Checks up to six adjacent positions to the block at Hex coordinate.
        A neighbor is occupied if the block is null or its state is True.

        Parameters:
            coo (Hex | tuple): The Hex coordinate to check for neighbors.
        Returns:
            int: The count of occupied neighboring Blocks.
        Raises:
            TypeError: If coo is not a Hex or a tuple of coordinates.
        '''
        count = 0
        if isinstance(coo, tuple):
            if len(coo) == 2:
                coo = Hex(coo[0], coo[1])
            elif len(coo) == 3:
                coo = Hex(coo[0], coo[2])
            else:
                raise TypeError("Invalid type for Hex coordinates")
        elif not isinstance(coo, Hex):
            raise TypeError("Invalid type for Hex coordinates")
        
        for pos in Piece.positions:
            target = pos + coo
            if self.in_range(target):
                if self.get_state(target):
                    count += 1
            else:
                count += 1 # Padding counts as a neighbor
        return count
    
    def get_pattern(self, coo: Union[Hex, tuple]) -> int:
        '''
        Determine the pattern of blocks around the given position in the hexagonal grid, including the block itself.

        This method checks up to seven positions in a hexagonal box centered at coordinates (i, k).
        It returns a value representing the pattern of occupied/unoccupied blocks, ignoring block colors.
        The pattern is encoded as a 7-bit integer (0 to 127) based on the state of the central block
        and its six neighbors. If a neighboring position is out of range or contains a None block,
        it is treated as occupied or unoccupied based on the include_null flag.

        Args:
            coo (Hex | tuple): The hex coordinate of the block at the center of the box.

        Returns:
            pattern (int): A number in the range [0, 127] representing the pattern of blocks in the hexagonal box.
        '''
        pattern = 0
        for i in range(7):
            i <<= 1
            try:
                if self.get_state(Piece.positions[i] + coo):
                    pattern + 1
            except ValueError:
                continue
        return pattern

    def compute_dense_index(self, coo: Hex, piece: Piece) -> float:
        '''
        Compute a density index score for hypothetically placing another piece.

        Returns a value between 0 and 1 representing surrounding density.
        A score of 1 means all surrounding blocks would be filled, 0 means the grid would be alone.

        Args:
            coo: Position for hypothetical placement.
            piece: The Piece to evaluate.

        Returns:
            float: Density index (0 to 1), or 0 if placement is invalid or no neighbors exist.
        '''
        total_possible = 0
        total_populated = 0
        for i in range(7):
            if piece.states()[i]:
                placed_block = piece.positions[i] + coo
                try:
                    if self.get_state(placed_block):
                        return 0.0
                except ValueError:
                    return 0.0
                total_possible += 6 - piece.count_neighbors(placed_block)
                total_populated += self.count_neighbors(placed_block)
        return total_populated / total_possible if total_possible > 0 else 0.0

    def compute_entropy(self) -> float:
        '''
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
            entropy (float): The entropy of the grid in bits, a non-negative value representing the randomness
                   of block arrangements. Returns 0.0 for a uniform grid (all filled or all empty)
                   or if no valid patterns are counted.
        '''
        from math import log2
        pattern_counts = [0] * 128
        pattern_total = 0
        radius = self.__radius - 1

        for i in range(len(self.__states)):
            center = self.coordinate_block(i)
            if self.__in_range(center.shift_j(1), radius):
                pattern = self.get_pattern(center)
                pattern_counts[pattern] += 1
                pattern_total += 1

        entropy = 0.0
        for count in pattern_counts:
            if count > 0:
                p = count / pattern_total
                entropy -= p * log2(p)
        return entropy