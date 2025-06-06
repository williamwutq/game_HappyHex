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

from abc import ABC, abstractmethod
from typing import Optional, List

__all__ = ['CommandProcessor']


class CommandProcessor(ABC):
    """
    An abstract base class for processing textual commands with optional arguments,
    and for supporting callback-based delegation or chaining of command processors.

    This class serves as a flexible and extensible framework for implementing
    command execution logic in Python. Inspired by a Java interface, it defines a
    consistent and structured way to parse, interpret, and act upon command strings.

    ### Core Responsibilities

    The core purpose of this class is to define the behavior of:

    - **Parsing** a full command string into its constituent command and arguments.
    - **Delegating** the parsed command to an implementation-defined method for processing.
    - **Supporting optional callback chaining**, enabling scenarios where command processors can pass execution responsibilities to other processors.

    ### Entry Point

    The main entry point is the `execute(command_string)` method, which performs the following:

    - Trims the input string.
    - Splits it into a command and its arguments by locating the first whitespace.
    - If no whitespace is found, the entire string is treated as a single command with no arguments.
    - Delegates actual command processing to the `execute_command(command, args)` method.

    This separation of parsing and execution allows for cleaner implementations and reusability
    of core parsing logic.

    ### Callback Functionality

    This interface optionally supports **callback processors**, allowing one command processor
    to forward or delegate commands to another. This enables layered or chained execution
    models. By default,:

    - `get_callback_processor()` returns `None`.
    - `set_callback_processor()` raises `NotImplementedError`, indicating that callback support is not enabled.

    Implementations can override these methods to provide meaningful callback behaviors.
    Care is taken to prevent circular references or illegal configurations (e.g., setting
    the processor as its own callback target).

    ### Exception Handling

    This class raises:

    - `ValueError` for null or empty commands, or for illegal argument use.
    - `InterruptedError` (Python equivalent) to represent interrupted execution states.

    This interface is designed to be flexible and extensible, allowing implementations
    to handle a wide range of command formats and execution logic while maintaining a
    consistent interface for command processing and callback management.
    """

    def execute(self, command_string: str) -> None:
        """
        Parses a command string into command and arguments, then delegates to execute(command, args).

        :param command_string: The full command string to process.
        :raises ValueError: If command is None or empty.
        :raises InterruptedError: If command execution is interrupted.
        """
        if command_string is None:
            raise ValueError("Command cannot be null")
        command_string = command_string.strip()
        if not command_string:
            raise ValueError("Command cannot be empty")
        parts = command_string.split(None, 1)
        command = parts[0]
        args = parts[1].split() if len(parts) > 1 else []
        self.execute_command(command, args)

    @abstractmethod
    def execute_command(self, command: str, args: List[str]) -> None:
        """
        Executes a command with arguments. Must be implemented by subclasses.

        :param command: The command to execute.
        :param args: List of arguments for the command.
        :raises ValueError: If command or args are invalid.
        :raises InterruptedError: If execution is interrupted.
        """
        pass

    def get_callback_processor(self) -> Optional['CommandProcessor']:
        """
        Returns the callback processor, if any.

        :return: An instance of CommandProcessor or None.
        """
        return None

    def set_callback_processor(self, processor: Optional['CommandProcessor']) -> None:
        """
        Sets a callback processor.

        :param processor: Another CommandProcessor instance or None.
        :raises ValueError: If the processor is the same instance as self.
        :raises NotImplementedError: If callback setting is not supported.
        """
        if processor is self:
            raise ValueError("Cannot add self as callback processor")
        raise NotImplementedError("Callback not supported for this command processor")
