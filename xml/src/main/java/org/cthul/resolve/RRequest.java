package org.cthul.resolve;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A resource request.
 * 
 * @author Arian Treffer
 */
public class RRequest {
    
    
    private static final String NULL_STR = new String();
    
    private final String uri;
    private final String publicId;
    private final String systemId;
    private final String baseUri;
    private String resolvedSystemId = NULL_STR;

    /**
     * Creates a resource request.
     * All parameters are optional.
     * 
     * @param uri       URI of the requested resource
     * @param publicId  Public ID of the requested resource
     * @param systemId  System internal ID of the requested resource
     * @param baseUri   URI of the system sending the request
     */
    public RRequest(String uri, String publicId, String systemId, String baseUri) {
        this.uri = uri;
        this.publicId = publicId;
        this.systemId = systemId;
        this.baseUri = baseUri;
    }

    public String getUri() {
        return uri;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getBaseUri() {
        return baseUri;
    }

    /**
     * Create a URI from the {@link #getBaseUri() base URI} and 
     * the {@link #getSystemId() system ID}.
     * @return uri
     */
    public String getResolvedSystemId() {
        if (resolvedSystemId == (Object) NULL_STR) {
            resolvedSystemId = expandSystemId(baseUri, systemId);
        }
        return resolvedSystemId;
    }
    
    /**
     * Calculates the schema file location.
     * @param baseId
     * @param systemId
     * @return schema file path
     */
    protected String expandSystemId(String baseId, String systemId) {
        if (baseId == null || baseId.isEmpty()) return systemId;
        if (systemId == null || systemId.isEmpty()) return baseId;
        try {
            return new URI(baseId).resolve(new URI(systemId)).toASCIIString();
    //        
    //        int lastSep = baseId.lastIndexOf('/');
    //        return baseId.substring(0, lastSep+1) + systemId;
    //        return baseId.substring(0, lastSep+1) + systemId;
        } catch (URISyntaxException ex) {
            throw new ResolvingException(toString(), ex);
        }
    }
    
    @Override
    public String toString() {
        String s = getSystemId();
        if (s == null) s = getUri();
        if (s == null) s = getPublicId();
        return "RRequest(" + s + ")";
    }
    
}
