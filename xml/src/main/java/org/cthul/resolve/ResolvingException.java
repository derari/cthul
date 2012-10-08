package org.cthul.resolve;

/**
 *
 * @author Arian Treffer
 */
public class ResolvingException extends RuntimeException {

    public ResolvingException() {
    }

    public ResolvingException(String message) {
        super(message);
    }

    public ResolvingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolvingException(Throwable cause) {
        super(cause);
    }
    
}
