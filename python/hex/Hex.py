import math

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
    def I(self):
        """Return raw I-coordinate."""
        return self._x

    def J(self):
        """Return raw J-coordinate."""
        return self._x + self._y

    def K(self):
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
        return self._x == other._x + 2 and self._y == other._y - 1

    def front_j(self, other):
        """Check if this Hex is one unit higher on J-axis."""
        return self._x == other._x + 1 and self._y == other._y + 1

    def front_k(self, other):
        """Check if this Hex is one unit higher on K-axis."""
        return self._x == other._x - 1 and self._y == other._y + 2

    def back_i(self, other):
        """Check if this Hex is one unit lower on I-axis."""
        return self._x == other._x - 2 and self._y == other._y + 1

    def back_j(self, other):
        """Check if this Hex is one unit lower on J-axis."""
        return self._x == other._x - 1 and self._y == other._y - 1

    def back_k(self, other):
        """Check if this Hex is one unit lower on K-axis."""
        return self._x == other._x + 1 and self._y == other._y - 2

    def equals(self, other):
        """Check if this Hex equals another Hex."""
        return self._x == other._x and self._y == other._y

    def in_range(self, radius):
        """Check if Hex is within given radius from origin."""
        return (0 <= self.get_line_i() < radius * 2 - 1 and
                -radius < self.get_line_j() < radius and
                0 <= self.get_line_k() < radius * 2 - 1)

    # Rectangular coordinates
    def X(self):
        """Convert to rectangular X coordinate."""
        return self._half_sin_of_60 * (self._x + self._y)

    def Y(self):
        """Convert to rectangular Y coordinate."""
        return (self._x - self._y) / 4.0

    def __str__(self):
        """Return string representation for debugging."""
        return (f"Hex[raw = {{{self.I()}, {self.J()}, {self.K()}}}, "
                f"line = {{{self.get_line_i()}, {self.get_line_j()}, {self.get_line_k()}}}, "
                f"rect = {{{self.X()}, {self.Y()}}}]")

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
        return Hex(self._x + other._x, self._y + other._y)

    def subtract(self, other):
        """Return new Hex with subtracted coordinates."""
        return Hex(self._x - other._x, self._y - other._y)

    def set(self, other):
        """Set this Hex to another's coordinates."""
        self._x = other._x
        self._y = other._y

    def this_hex(self):
        """Return this Hex as a new instance using line coordinates."""
        return Hex.hex(self.get_line_i(), self.get_line_k())

    def __copy__(self):
        """Return a copy of this Hex."""
        return Hex(self._x, self._y)

    def __deepcopy__(self, memo):
        """Return a deep copy of this Hex."""
        return Hex(self._x, self._y)
