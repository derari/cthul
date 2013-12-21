package org.cthul.resolve;

import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;

/**
 * Uses {@link ResourceResolver}s to create objects of type {@code T}.
 * 
 * @author Arian Treffer
 * @param <T> type of created objects
 * @param <E> exception thrown during result creation
 */
public abstract class ObjectResolver<T, E extends Exception> {
    
    protected CLogger log = CLoggerFactory.getLogger(getClass());
    protected final ResourceResolver resolver;

    public ObjectResolver(ResourceResolver resolver) {
        this.resolver = resolver;
    }
    
    public ObjectResolver(ResourceResolver... resolver) {
        this(new CompositeResolver(resolver));
    }    
    
    protected T resolve(String uri, String publicId, String systemId, String baseURI) throws E {
        RRequest req = new RRequest(uri, publicId, systemId, baseURI);
        RResult res = resolver.resolve(req);
        if (res == null) {
            if (uri != null) 
                log.warn("Could not resolve schema %s %if[as %<s]", uri, systemId);
            return null;
        }
        
        log.info("Resolved %s %if[as %<s]", uri, res.getSystemId());
        return result(res);
    }

    protected abstract T result(RResult result) throws E;
    
}
