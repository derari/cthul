package org.cthul.resolve;

/**
 * Finds a requested resource.
 * 
 * @author Arian Treffer
 */
public interface ResourceResolver {
   
    /**
     * @param request
     * @return result representing the resource, or {@code null} if none was found.
     */
    RResult resolve(RRequest request);
    
}
