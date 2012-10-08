package org.cthul.resolve;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractResolver implements ResourceResolver {
    
    /**
     * Returns a schema finder that uses this schema finder for look-up,
     * but is not mutable.
     * @return resource resolver
     */
    public ResourceResolver immutable() {
        return new CompositeResolver(this);
    }

}
