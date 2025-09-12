/*
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
 */

package achievements;

import java.io.IOException;

/**
 * Exception thrown when there is an error during data serialization or deserialization for game data.
 * This exception indicates that the data could not be properly converted to or from a specific format.
 * It extends {@link IOException} to indicate that it is related to input/output operations.
 *
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class DataSerializationException extends IOException {
  /**
   * Constructs a new DataSerializationException with the specified detail message.
   *
   * @param message the detail message
   */
  public DataSerializationException(String message) {
    super(message);
  }
  /**
   * Constructs a new DataSerializationException with the specified detail message and type information.
   *
   * @param message the detail message
   * @param type    the Class type related to the serialization error
   */
  public DataSerializationException(String message, Class<?> type) {
    super(message + " for type: " + type.getName());
  }
  /**
   * Constructs a new DataSerializationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public DataSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
  /**
   * Constructs a new DataSerializationException with the specified detail message, type information, and cause.
   *
   * @param message the detail message
   * @param type    the Class type related to the serialization error
   * @param cause   the cause of the exception
   */
  public DataSerializationException (String message, Class<?> type, Throwable cause) {
    super(message + " for type: " + type.getName(), cause);
  }
  /**
   * Constructs a new DataSerializationException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public DataSerializationException(Throwable cause) {
    super(cause);
  }
}
