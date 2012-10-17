package org.cthul.xml;

import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;
import org.cthul.resolve.*;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractResolver<T, E extends Exception> {
    
    protected CLogger log = CLoggerFactory.getLogger(getClass());
    protected final ResourceResolver resolver;

    public AbstractResolver(ResourceResolver resolver) {
        this.resolver = resolver;
    }
    
    public AbstractResolver(ResourceResolver... resolver) {
        this(new CompositeResolver(resolver));
    }    
    
    protected T resolve(String uri, String publicId, String systemId, String baseURI) throws E {
        RRequest req = new RRequest(uri, publicId, systemId, baseURI);
        RResult res = resolver.resolve(req);

        if (res == null) {
            log.warn("Could not resolve schema %s %if[as %<s]", uri, systemId);
            return null;
        }

        log.info("Resolved %s %if[as %<s]", uri, res.getSystemId());
        return result(res);
    }

    protected abstract T result(RResult result) throws E;
    
}
