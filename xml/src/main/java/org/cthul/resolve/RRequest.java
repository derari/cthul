package org.cthul.resolve;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A resource request.
 * Can contain any subset of following parameters:
 * <ul>
 * <li>uri: absolute unique identifier of the resource</li>
 * <li>publicId</li>
 * <li>systemId: absolute or relative path to the resource, can be system specific</li>
 * <li>baseUri: uri of the request sender</li>
 * </ul>
 * 
 * @author Arian Treffer
 */
public class RRequest {
    
    
    static final String NULL_STR = new String();
    
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

    /**
     * Creates a resource request.
     * All parameters are optional.
     * 
     * @param systemId  System internal ID of the requested resource
     * @param baseUri   URI of the system sending the request
     */
    public RRequest(String systemId, String baseUri) {
        this.uri = null;
        this.publicId = null;
        this.systemId = systemId;
        this.baseUri = baseUri;
    }

    /**
     * Creates a resource request.
     * 
     * @param uri       URI of the requested resource
     */
    public RRequest(String uri) {
        this.uri = uri;
        this.publicId = null;
        this.systemId = null;
        this.baseUri = null;
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
     * Returns {@linkplain #getUri() uri} or {@linkplain #getResolvedSystemId() resolved system id}.
     * @return uri or system id
     */
    public String getUriOrId() {
        String u = getUri();
        if (u != null) return u;
        return getResolvedSystemId();
    }

    /**
     * Creates a URI from the {@link #getBaseUri() base URI} and 
     * the {@link #getSystemId() system ID}.
     * @return uri
     */
    public String getResolvedSystemId() {
        if (resolvedSystemId == (Object) NULL_STR) {
            resolvedSystemId = resolveSystemId();
        }
        return resolvedSystemId;
    }
    
    protected String resolveSystemId() {
        return expandSystemId(getBaseUri(), getSystemId());
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
        String s = getUri();
        if (s == null && resolvedSystemId != (Object) NULL_STR) s = resolvedSystemId;
        if (s == null) s = getSystemId();
        if (s == null) s = getPublicId();
        if (s == null) return "RRequest@" + Integer.toHexString(hashCode());
        return "RRequest(" + s + ")";
    }
}
