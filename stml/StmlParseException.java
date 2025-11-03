package stml;

/**
 * Exception class for STML parsing errors.
 * This class extends StmlException and is used to represent errors that occur specifically during the parsing of STML files.
 * <p>
 * This class provides multiple constructors to create exceptions with different levels of detail, including messages and causes.
 *
 * @author William Wu
 * @version STML 1.0
 * @since STML 1.0
 */
public class StmlParseException extends StmlException {
    private int lineNumber = -1;
    private int columnNumber = -1;
    public StmlParseException(String message, int lineNumber, int columnNumber) {
        super(message + " (at line " + lineNumber + ", column " + columnNumber + ")");
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    public StmlParseException(String message) {
        super(message);
    }
    public StmlParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public StmlParseException(Throwable cause) {
        super(cause);
    }
    public StmlParseException() {
        super();
    }
}
