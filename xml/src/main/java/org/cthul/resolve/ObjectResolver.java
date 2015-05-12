package org.cthul.resolve;

/**
 * Uses {@link ResourceResolver}s to create objects of type {@code T}.
 * 
 * @param <T> type of created objects
 * @param <E> exception thrown during result creation
 */
public abstract class ObjectResolver<T, E extends Exception> {
    
    protected final ResourceResolver resolver;

    public ObjectResolver(ResourceResolver resolver) {
        this.resolver = resolver;
    }
    
    public ObjectResolver(ResourceResolver... resolver) {
        this(new CompositeResolver(resolver));
    }    
    
    protected T resolve(String uri, String publicId, String systemId, String baseURI) throws E {
        RRequest req = new RRequest(uri, publicId, systemId, baseURI);
        return resolve(req);
    }
    
    protected T resolve(String uri) throws E {
        RRequest req = new RRequest(uri);
        return resolve(req);
    }
    
    protected T resolve(String systemId, String baseURI) throws E {
        RRequest req = new RRequest(systemId, baseURI);
        return resolve(req);
    }
    
    protected T resolve(RRequest req) throws E {
        RResponse res = resolver.resolve(req);
        if (res == null) res = req.noResultResponse();
        if (res.hasResult()) {
            RResult result = res.getResult();
            log_resolved(result);
            return result(result);
        } else {
            log_notFound(res);
            return noResult(res);
        }
    }
    
    protected void log_notFound(RResponse res) {
//        log.warn("Could not resolve resource%if[ %<s at] %s", req.getSystemId(), req.getUriOrId());
    }
    
    protected void log_resolved(RResult res) {
//        log.info("Resolved %s%if[ as %<s]", res.getRequest().getUriOrId(), res.getSystemId());
    }

    protected T noResult(RResponse res) throws E {
        return null;
    }
    
    protected abstract T result(RResult result) throws E;
}
