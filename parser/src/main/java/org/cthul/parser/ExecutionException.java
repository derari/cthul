package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public class ExecutionException extends RuntimeException {
    
    private static final long serialVersionUID = -3380906851858683724L;
    
    /**
     * Creates a new instance of <code>NoMatchException</code> without detail message.
     */
    public ExecutionException() {
    }

    /**
     * Constructs an instance of <code>NoMatchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExecutionException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>NoMatchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExecutionException(String msg, Object... args) {
        super(String.format(msg, args));
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
