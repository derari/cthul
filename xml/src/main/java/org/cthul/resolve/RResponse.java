package org.cthul.resolve;

import java.util.List;

/**
 * Response of a resource look-up.
 */
public interface RResponse {
    
    /**
     * Returns the request that was responded to.
     * @return request
     */
    RRequest getRequest();
    
    /**
     * Returns true if look-up found a result.
     * @return true if result is present
     */
    boolean hasResult();
    
    /**
     * Returns result if present, otherwise throws {@link #asException()}.
     * @return result
     */
    RResult getResult();
    
    /**
     * Returns a list of non-critical exceptions that occurred during look-up.
     * @return list of exceptions.
     */
    List<Exception> getWarningsLog();
    
    /**
     * Represents the warnings as single exception.
     * Returns null if result is present and no warnings occurred.
     * @return resolving exception
     */
    ResolvingException asException();
}
