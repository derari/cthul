package org.cthul.resolve;

/**
 *
 * @author Arian Treffer
 */
public interface ResourceResolver {
    
    RResult resolve(RRequest request);
    
}
