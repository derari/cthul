package org.cthul.resolve;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Result of id resolution.
 * <p/>
 * Should be subclassed to implement {@link #createReader()}, 
 * {@link #createInputStream()} and {@link #getEncoding()}, 
 * {@link #createByteBuffer()} and {@link #getEncoding()}, 
 * or {@link #getString()}.
 * 
 * @author Arian Treffer
 */
public class RResult {
    
    private final RRequest request;
    private final String systemId;
    private String defaultEncoding = null;

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
        return defaultEncoding;
    }
    
    public String getString() {
        return null;
    }
    
    public ByteBuffer getByteBuffer() {
        try {
            return createByteBuffer();
        } catch (Exception e) {
            throw new ResolvingException(toString(), e);
        }
    }
    
    protected ByteBuffer createByteBuffer() throws Exception {
        return null;
    }
    
    public Reader asReader() {
        Reader r = getReader();
        if (r != null) return r;
        String s = getString();
        if (s != null) return new StringReader(s);
        InputStream is = fromStreamOrBuffer();
        if (is != null) return streamAsReader(is);
        return null;
    }
    
    public InputStream asInputStream() {
        InputStream is = fromStreamOrBuffer();
        if (is != null) return is;
        String s = fromStringOrReader();
        if (s != null) return stringAsStream(s);
        return null;
    }
    
    public ByteBuffer asByteBuffer() {
        ByteBuffer bb = getByteBuffer();
        if (bb != null) return null;
        InputStream is = getInputStream();
        if (is != null) return streamAsBuffer(is);
        String s = fromStringOrReader();
        if (s != null) return stringAsBuffer(s);
        return null;
    }
    
    public String asString() {
        String s = fromStringOrReader();
        if (s != null) return s;
        InputStream is = fromStreamOrBuffer();
        if (is != null) {
            Reader r = streamAsReader(is);
            return readerAsString(r);
        }
        return null;
    }
    
    private String fromStringOrReader() {
        String s = getString();
        if (s != null) return s;
        Reader r = getReader();
        if (r != null) return readerAsString(r);
        return null;
    }
    
    private InputStream fromStreamOrBuffer() {
        InputStream is = getInputStream();
        if (is != null) return is;
        ByteBuffer bb = getByteBuffer();
        if (bb != null) return bufferAsStream(bb);
        return null;
    }
    
    protected String readerAsString(Reader r) {
        try {
            final char[] buf = new char[BUF_SIZE];
            final StringWriter sw = new StringWriter();
            int n;
            while (-1 < (n = r.read(buf))) {
                sw.write(buf, 0, n);
            }
            return sw.toString();
        } catch (IOException e) {
            throw new ResolvingException(toString(), e);
        }
    }


    protected Reader streamAsReader(InputStream is) throws ResolvingException {
        try {
            String enc = getEncoding();
            if (enc == null) {
                return new InputStreamReader(is);
            } else {
                return new InputStreamReader(is, enc);
            }
        } catch (UnsupportedEncodingException e) {
            throw new ResolvingException(toString(), e);
        }
    }

    protected InputStream stringAsStream(String s) throws ResolvingException {
        byte[] data = stringAsBytes(s);
        return new ByteArrayInputStream(data);
    }

    protected ByteBuffer stringAsBuffer(String s) throws ResolvingException {
        byte[] data = stringAsBytes(s);
        return ByteBuffer.wrap(data);
    }

    protected InputStream bufferAsStream(ByteBuffer bb) {
        return new ByteBufferInputStream(bb);
    }
    
    protected ByteBuffer streamAsBuffer(InputStream is) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(BUF_SIZE); 
            byte[] buf = new byte[BUF_SIZE];
            int n;
            while(-1 < (n = is.read(buf))) {
                out.write(buf, 0, n);
            }
            return ByteBuffer.wrap(out.toByteArray());
        } catch (IOException e) {
            throw new ResolvingException(toString(), e);
        }
    }
    
    protected byte[] stringAsBytes(String s) throws ResolvingException {
        defaultEncoding = getEncoding();
        if (defaultEncoding == null) defaultEncoding = "UTF-8";
        try {
            return s.getBytes(defaultEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new ResolvingException(toString(), e);
        }
    }
    
    @Override
    public String toString() {
        String s = getSystemId();
        if (s == null) s = getUri();
        if (s == null) s = getPublicId();
        Class clazz = getClass();
        String cn = clazz.isAnonymousClass() ? "RResult" : clazz.getSimpleName();
        Class decl = clazz.getEnclosingClass();
        if (decl != null) cn = decl.getSimpleName() + "." + cn;
        return cn + "(" + s + ")";
    }

    private static final int BUF_SIZE = 8192;
    
    protected static class ByteBufferInputStream extends InputStream {

        ByteBuffer buf;

        public ByteBufferInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        @Override
        public int read(byte[] bytes, int off, int len) throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }
}
