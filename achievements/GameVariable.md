# Game Variable Scripting Language Manual

This scripting language provides a simple way to access and manipulate game-related variables using expressions. It is parsed by the `GameVariableSupplier.parse(String)` method and supports predefined constants, unary operations, binary operations, casting, and nested expressions.

All operations are **case-insensitive**.

---

## Purpose

This language is primarily used for defining conditions and calculations in game achievements,
allowing for dynamic and complex criteria based on the current state of the game. This is not a
general-purpose programming language, but rather a specialized tool for game variable manipulation.
Compared to writing custom Java code, it offers a safer and more controlled environment for users to define
expressions without risking the integrity of the game.

Like Python, this language supports statements that are written in a natural language style, making it
more accessible to users who may not be familiar with traditional programming syntax.

This language maybe embedded in configuration files following their own syntax rules. For example, in JSON configs,
strings may need to be escaped with backslashes, and in certain achievement configuration languages, game variables
may be enclosed in `${}`.

---

## Grammar

```
<expr>     ::= <term> | <expr> <binop> <expr>
<term>     ::= <const> | <variable> | <unaryop> <term> | '(' <expr> ')'
<const>    ::= <integer> | <double>
<variable> ::= ZERO | ONE | PI | HEX | RANDOM | LENGTH | RADIUS | LINES | SIZE
             | FIRST | LAST | ANY | SCORE | TURN | FILL | ENTROPY | UNO | BIG_BLOCK
<unaryop>  ::= int | integer | double | float | - | neg | negate | negative | abs | absolute
             | sq | sqr | square | squared | sqrt | squareroot | square_root | square-root
             | bool | boolean | not | ! | sizeof | colorof | invert | uncolor | select
<binop>    ::= + | adds | add | plus | addition
             | - | subtracts | subtract | minus | subtraction
             | * | multiplies | multiply | times | time | multiplication
             | / | divides | divide | division
             | % | mod | modulo | modulos | remainder
             | ^ | pow | power | exp | exponent
             | max | maximum | min | minimum | avg | average | mean
             | equals | equal | == | is | same
             | equals_exact | equal_exact | === | is_exact | same_exact
             | not_equals | not_equal | != | not | is_not | not_same
             | greater | > | lesser | <
             | insert | remove | alter | paint
```

---

The variable itself is an expression that evaluates to either a floating point number, an integer, or a Piece.
If an operation is not valid for the given types, it will return `null`. These are the only valid types. During
evaluation, no errors will be thrown, and any invalid operation will simply return `null`.

The language do not use the following symbols: `, ; : [ ] { } \ " ' & | ? @ $` to avoid conflicts with other configuration syntaxes.

---

## Precedence

From highest to lowest:

1. Casts and unary operations
2. Min/Max/Avg
3. Boolean Comparisons (`equals`, `greater`, etc.)
4. Power and piece modification (`insert`, `remove`, `alter`, `paint`)
5. Multiplication, division, modulo
6. Addition and subtraction
7. Natural order is **right to left** for same precedence level.

Parentheses `()` can always be used to override precedence.

---

## Predefined Variables and Constants

#### ZERO

Always returns `0`.

#### ONE

Always returns `1`.

#### PI

Returns the mathematical constant π.

#### HEX

Returns `6` (number of sides on a hexagon).

#### RANDOM

Returns a random double between `0.0` (inclusive) and `1.0` (exclusive).

#### LENGTH

Returns the length of the engine.

#### RADIUS

Returns the radius of the engine.

#### LINES

Returns the number of lines in the engine.

#### SIZE

Returns the number of pieces in the queue.

#### FIRST

Returns the first piece in the queue.

#### LAST

Returns the last piece in the queue.

#### ANY

Returns a random piece from the queue.

#### SCORE

Returns the current score.

#### TURN

Returns the current turn number.

#### FILL

Returns the percentage of the engine that is filled.

#### ENTROPY

Returns the entropy of the engine.

#### UNO

Returns the constant piece `UNO`.

#### BIG_BLOCK

Returns the constant piece `BIG_BLOCK`.

#### Integer/Double literals

Any number literal is supported (e.g., `42`, `3.14`).

---

## Unary Operations

#### int / integer

Cast to integer.

#### double / float

Cast to double.

#### - / neg / negate / negative

Negates a number.

#### abs / absolute

Absolute value.

#### sq / sqr / square / squared

Square a number.

#### sqrt / squareroot / square_root / square-root

Square root.

#### bool / boolean

Converts number to boolean (0 → 0, nonzero → 1).

#### not / !

Logical NOT (0 → 1, nonzero → 0).

#### sizeof

Number of digits in an integer or length of a Piece.

#### colorof

Color of a Piece (as color index).

#### invert

Invert a Piece pattern.

#### uncolor

Remove color from a Piece (set to -2).

#### select

Random integer from `0` to given integer (exclusive).

---

## Binary Operations

#### + / add / plus / addition

Addition.

#### - / subtract / minus / subtraction

Subtraction.

#### * / multiply / times / multiplication

Multiplication.

#### / / divide / division

Division (null if divide by zero).

#### % / mod / modulo / remainder

Modulo (null if divide by zero).

#### ^ / pow / power / exp / exponent

Exponentiation.

#### max / maximum

Maximum of two numbers.

#### min / minimum

Minimum of two numbers.

#### avg / average / mean

Average of two numbers (integer division).

#### equals / == / is / same

Approximate equality.

#### equals_exact / === / is_exact / same_exact

Exact equality.

#### not_equals / != / not / is_not / not_same

Not equal (approximate).

#### greater / >

Greater than.

#### lesser / <

Less than.

#### insert

Insert a block into a Piece at given index.

#### remove

Remove a block from a Piece at given index.

#### alter

Toggle a block in a Piece at given index.

#### paint

Change a Piece’s color to given index.

---

## Examples

```
size * 3 > radius
```

Checks if the piece queue size is greater than three times the engine radius.

```
pi * sq radius
```

Approximates area of the engine (circle assumption).

```
invert (first paint (0 max (colorof first)))
```

Inverts the first piece and paints it color 0 if its color index is negative.

```
((sizeof TURN) greater 3) * (sqrt SCORE + 3) + (not ((sizeof TURN) greater 3)) * 30
```

Returns 30 if turn < 1000, else returns `sqrt(score) + 3`.

---