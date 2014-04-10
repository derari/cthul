package org.cthul.resolve;

/**
 *
 */
public abstract class ResourceResolverBase implements ResourceResolver {
    
    /**
     * Returns a schema finder that uses this schema finder for look-up,
     * but is not mutable.
     * @return resource resolver
     */
    public ResourceResolver immutable() {
        return new CompositeResolver(this);
    }
    
    public ResourceResolver join(ResourceResolver... others) {
        return CompositeResolver.join(this, others);
    }
}
