package stml;

/**
 * Exception class for STML-related errors.
 * This class extends RuntimeException and can be used to represent various errors that occur during STML parsing, validation, or processing.
 * <p>
 * This class provides multiple constructors to create exceptions with different levels of detail, including messages and causes.
 *
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public class StmlException extends RuntimeException {
    public StmlException(String message) {
        super(message);
    }
    public StmlException(String message, Throwable cause) {
        super(message, cause);
    }
    public StmlException(Throwable cause) {
        super(cause);
    }
    public StmlException() {
        super();
    }
}
