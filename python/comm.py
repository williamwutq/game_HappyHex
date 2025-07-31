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
import sys
from abc import ABC, abstractmethod
from typing import Optional, List

__all__ = ['CommandProcessor', 'NullProcessor', 'MainProcessor']


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


class NullProcessor(CommandProcessor):
    """
    A placeholder implementation of the CommandProcessor interface.

    The NullProcessor is a stub implementation used in contexts where a
    command processor is required but no actual command execution should occur.
    It raises an exception on any attempt to execute a command, making it useful
    for guarding against uninitialized or invalid processor references.

    It supports setting and retrieving a callback processor to conform to
    the CommandProcessor interface, but executing a command with this class
    is considered an error.
    """
    def __init__(self):
        """
        Initializes the NullProcessor with no callback processor assigned.
        """
        self._callback: Optional[CommandProcessor] = None
    def execute_command(self, command: str, args: List[str]) -> None:
        """
        Raises an error when an attempt is made to execute a command.

        This method exists to fulfill the CommandProcessor interface contract,
        but always raises an error to indicate that this processor is not
        intended for use in actual command execution.

        :param command: The command to be executed.
        :param args: The list of arguments for the command.

        :raises ValueError: Always raised to indicate that command execution is invalid.
        """
        raise ValueError('Attempted execution call to NullProcessor')
    def get_callback_processor(self) -> Optional['CommandProcessor']:
        """
        Returns the callback processor associated with this processor, if any.

        :returns: The callback processor, or None if not set.
        """
        return self._callback
    def set_callback_processor(self, processor: Optional['CommandProcessor']) -> None:
        """
        Sets the callback processor to be used after command execution.

        The callback processor must not be the same instance as this processor.
        If `None` is provided, any existing callback processor is cleared.

        :param processor: The processor to set as a callback.

        :raise ValueError: If attempting to set the callback processor to self.
        """
        if processor is self:
            raise ValueError("Cannot add self as callback processor")
        else: self._callback = processor


class PeripheralCommandProcessor(CommandProcessor):
    """
    A command processor that acts as an interface between this Python subprocess and an external controller.

    This processor is responsible for:

    - Receiving commands (typically from stdin) issued by an external controller.
    - Formatting and forwarding its own commands (as responses) to stdout.
    - Delegating commands to an optional callback processor (typically a MainProcessor implementation).

    This design enables a two-way protocol where this process can act on incoming instructions
    and also issue outbound commands or termination signals based on internal logic.

    Expected protocol:

    - Incoming lines are read from stdin, parsed, and dispatched via `respond_to_call()`.
    - Valid commands may be forwarded to the callback processor.
    - Special commands "interrupt" and "kill" will terminate this process with appropriate exit codes.
    - Outbound responses are printed to stdout via `execute_command()`.

    This processor typically delegates meaningful work to a `MainProcessor`, which may:

    - Modify internal state
    - Issue outbound commands (via this processor's `execute_command`)
    - Decide whether to continue or issue an "interrupt" to end the session.
    """
    def __init__(self):
        """
        Initializes the processor with no callback processor set.
        The callback should later be set to a valid CommandProcessor (e.g., MainProcessor).
        """
        self._callback_processor: Optional[CommandProcessor] = None

    def execute_command(self, command: str, args: List[str]) -> None:
        """
        Sends a command and its arguments to stdout as a response to the controller.

        This is typically called by the callback processor (MainProcessor) to send
        results, follow-up commands, or status messages back to the controlling program.

        :param command: The command to be sent and executed.
        :param args: The list of arguments for the command.
        """
        if command == "interrupt":
            sys.exit(0) # Terminate gracefully on interrupt
        elif command == "kill":
            sys.exit(1) # Signal failure on kill
        # Format response as command and args, send to stdout for execution
        response = f"{command} {' '.join(args)}"
        print(response, flush=True)

    def respond_to_call(self, command_string: str) -> None:
        """
        Processes an incoming command string from the controller.

        Parses the command string into a command and arguments, and then:

        - Terminates the process on "interrupt" or "kill".
        - Delegates the command to the callback processor if one is set.

        Behavior:
            - "interrupt" → exit(0)  (normal termination)
            - "kill"      → exit(1)  (failure termination)
            - other       → delegate to callback processor, if set

        :param command_string: The raw command line string from stdin.
        """
        if command_string is not None:
            command_string = command_string.strip()
            if command_string:
                parts = command_string.split(None, 1)
                command = parts[0]
                args = parts[1].split() if len(parts) > 1 else []
                if command == "interrupt":
                    sys.exit(0) # Terminate gracefully on interrupt
                elif command == "kill":
                    sys.exit(1) # Signal failure on kill
                elif self._callback_processor:
                    # If callback processor is set, delegate the response to it
                    try:
                        self._callback_processor.execute_command(command, args)
                    except ValueError:
                        pass # Ignore invalid commands
                    except InterruptedError:
                        sys.exit(0) # Terminate on interrupt

    def get_callback_processor(self) -> Optional['CommandProcessor']:
        """
        Returns the callback processor associated with this processor, if any.

        :returns: The callback processor, or None if not set.
        """
        return self._callback_processor

    def set_callback_processor(self, processor: Optional['CommandProcessor']) -> None:
        """
        Sets the callback processor to be used after command execution.

        The callback processor must not be the same instance as this processor.
        If `None` is provided, any existing callback processor is cleared.

        :param processor: The processor to set as a callback.

        :raise ValueError: If attempting to set the callback processor to self.
        """
        if processor is self:
            raise ValueError("Cannot add self as callback processor")
        self._callback_processor = processor



# processor importing
try:
    from autoplay import MainProcessor
except ImportError:
    try:
        from autoplayimpl import MainProcessor
    except ImportError:
        class MainProcessor(NullProcessor):
            pass


if __name__ == "__main__":
    processor = PeripheralCommandProcessor()
    callback = MainProcessor()
    try:
        callback.set_callback_processor(processor)
    except NotImplementedError:
        pass
    try:
        processor.set_callback_processor(callback)
    except NotImplementedError:
        pass
    try:
        # Read commands from stdin
        for line in sys.stdin:
            processor.respond_to_call(line)
    except KeyboardInterrupt:
        sys.exit(0)