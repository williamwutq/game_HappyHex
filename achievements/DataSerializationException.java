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
