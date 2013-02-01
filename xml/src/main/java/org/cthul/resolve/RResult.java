package org.cthul.resolve;

import java.io.InputStream;
import java.io.Reader;

/**
 * Result of id resolution.
 * <p/>
 * Should be subclassed to implement {@link #createReader()}, 
 * {@link #createInputStream()} and {@link #getEncoding()}, 
 * or {@link #getString()}.
 * 
 * @author Arian Treffer
 */
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
        } catch (Exception e) {
            throw new ResolvingException(toString(), e);
        }
    }
    
    protected Reader createReader() throws Exception {
        return null;
    }
    
    public InputStream getInputStream() {
        try {
            return createInputStream();
        } catch (Exception e) {
            throw new ResolvingException(toString(), e);
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

    @Override
    public String toString() {
        String s = getSystemId();
        if (s == null) s = getUri();
        if (s == null) s = getPublicId();
        Class clazz = getClass();
        String cn = clazz.isAnonymousClass() ? "RResult" : clazz.getSimpleName();
        Class decl = clazz.getEnclosingClass();
        if (cn != null) cn = decl.getSimpleName() + "." + cn;
        return cn + "(" + s + ")";
    }    
}
