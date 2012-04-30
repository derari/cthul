/*
 * 
 */

package org.cthul.xml.idstax;

/**
 *
 * @author Arian Treffer
 */
public class IDStreamException extends RuntimeException {
    
    private static final long serialVersionUID = 8290944925613496167L;

    public static RuntimeException asRuntimeException(Throwable t) {
        if (t instanceof RuntimeException) return (RuntimeException)t;
        return new IDStreamException(t);
    }

    /**
     * Creates a new instance of <code>IDStreamException</code> without detail message.
     */
    public IDStreamException() {
    }

    /**
     * Constructs an instance of <code>IDStreamException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IDStreamException(String msg) {
        super(msg);
    }

    public IDStreamException(String msg, Throwable t) {
        super(msg, t);
    }

    public IDStreamException(Throwable t) {
        super(t);
    }
}
