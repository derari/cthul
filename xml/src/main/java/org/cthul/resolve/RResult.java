package org.cthul.resolve;

import java.io.InputStream;
import java.io.Reader;

public class RResult {
    
    private final RRequest request;
    private final String systemId;

    public RResult(RRequest request) {
        this(request, null);
    }
    
    public RResult(RRequest request, String systemId) {
        this.request = request;
        this.systemId = systemId;
    }

    public RRequest getRequest() {
        return request;
    }

    public String getUri() {
        return request.getUri();
    }
    
    public String getPublicId() {
        return request.getPublicId();
    }

    public String getSystemId() {
        return systemId != null ? systemId : request.getSystemId();
    }

    public String getBaseUri() {
        return request.getBaseUri();
    }

    public Reader getReader() {
        try {
            return createReader();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ResolvingException(e);
        }
    }
    
    protected Reader createReader() throws Exception {
        return null;
    }
    
    public InputStream getInputStream() {
        try {
            return createInputStream();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ResolvingException(e);
        }
    }
    
    protected InputStream createInputStream() throws Exception {
        return null;
    }
    
    public String getEncoding() {
        return null;
    }
    
    public String getString() {
        return null;
    }
    
}
