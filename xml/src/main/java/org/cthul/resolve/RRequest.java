package org.cthul.resolve;

/**
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
    private String expandSystemId(String baseId, String systemId) {
        if (baseId == null || baseId.isEmpty()) return systemId;
        if (systemId == null || systemId.isEmpty()) return baseId;
        int lastSep = baseId.lastIndexOf('/');
        if (lastSep < 0) return systemId;
        return baseId.substring(0, lastSep+1) + systemId;
    }
    
}
