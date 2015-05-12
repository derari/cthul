package org.cthul.resolve;

/**
 * Finds a requested resource.
 */
public interface ResourceResolver {
   
    /**
     * @param request
     * @return result representing the resource, or {@code null} if none was found.
     */
    RResponse resolve(RRequest request);
    
}
