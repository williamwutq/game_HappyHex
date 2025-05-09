from hex import Hex

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

    def __init__(self, i_or_hex=0, k_or_color=0, color_or_state=None, state=None):
        """
        Initialize a Block with various parameter combinations.

        Args:
            i_or_hex: i-coordinate or Hex object.
            k_or_color: k-coordinate or color index.
            color_or_state: Color index or state boolean.
            state: State boolean.
        """
        if isinstance(i_or_hex, Hex):
            super().__init__()
            self.set(i_or_hex)
            if isinstance(k_or_color, int) and color_or_state is None:
                self._color = k_or_color
                self._state = False
            elif isinstance(k_or_color, int) and isinstance(color_or_state, bool):
                self._color = k_or_color
                self._state = color_or_state
            else:
                self._color = -1
                self._state = False
        else:
            super().__init__(i_or_hex, k_or_color)
            if color_or_state is None and state is None:
                self._color = -1
                self._state = False
            elif isinstance(color_or_state, int) and state is None:
                self._color = color_or_state
                self._state = False
            elif isinstance(color_or_state, bool):
                self._color = -2 if color_or_state else -1
                self._state = color_or_state
            elif isinstance(color_or_state, int) and isinstance(state, bool):
                self._color = color_or_state
                self._state = state

    @staticmethod
    def block(i, k, color):
        """Create a Block using line indices with specified color."""
        return Block(0, 0, color).shift_i(k).shift_k(i)

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
