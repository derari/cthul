package org.cthul.resolve;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static org.cthul.resolve.RRequest.NULL_STR;
import org.cthul.resolve.results.BytesResult;
import org.cthul.resolve.results.FileResult;
import org.cthul.resolve.results.InputStreamResult;
import org.cthul.resolve.results.StringResult;
import org.cthul.resolve.results.URLResult;

/**
 * Result of resource resolution.
 * <p>
 * Should be sub-classed to implement {@link #createReader()}, 
 * {@link #getString()}, or {@link #getEncoding()} and one of
 * {@link #createInputStream()}, {@link #createByteBuffer()}, 
 * {@link #createURLConnection()}, or {@link #getResourceURL()}.
 * @see BytesResult
 * @see FileResult
 * @see InputStreamResult
 * @see StringResult
 * @see URLResult
 */
public class RResult {
    
    private final RRequest request;
    private final String systemId;
    private String resolvedSystemId = RRequest.NULL_STR;
    private String defaultEncoding = null;

    public RResult(RRequest request) {
        this(request, null);
    }
    
    public RResult(RRequest request, String systemId) {
        this.request = request;
        this.systemId = systemId;
    }
    
    public RResult(String uri, String publicId, String systemId, String baseUri) {
        this(new RRequest(uri, publicId, systemId, baseUri), systemId);
    }

    /**
     * Returns the request for this result.
     * @return request
     */
    public RRequest getRequest() {
        return request;
    }

    /**
     * Returns the request URI
     * @return request URI
     */
    public String getUri() {
        return request.getUri();
    }
    
    /**
     * Returns the request public ID
     * @return public ID
     */
    public String getPublicId() {
        return request.getPublicId();
    }

    /**
     * Returns the system ID of the resolved resource.
     * @return system ID
     * @see #getResolvedSystemId() 
     */
    public String getSystemId() {
        return systemId != null ? systemId : request.getSystemId();
    }

    /**
     * Returns the base URI of the request
     * @return base URI
     */
    public String getBaseUri() {
        return request.getBaseUri();
    }
    
    /**
     * Returns {@linkplain #getUri() uri} or {@linkplain #getResolvedSystemId() resolved system id}.
     * @return uri or system id
     */
    public String getUriOrId() {
        String uri = getUri();
        if (uri != null) return uri;
        return getResolvedSystemId();
    }

    /**
     * Creates a URI from the {@linkplain #getBaseUri() base URI} and 
     * the {@linkplain #getSystemId() system ID}.
     * @return uri
     */
    public String getResolvedSystemId() {
        if (resolvedSystemId == (Object) NULL_STR) {
            resolvedSystemId = resolveSystemId();
        }
        return resolvedSystemId;
    }
    
    protected String resolveSystemId() {
        String mySysId = getSystemId();
        String reqSysId = getRequest().getSystemId();
        if (Objects.equals(mySysId, reqSysId)) {
            return getRequest().getResolvedSystemId();
        }
        return expandSystemId(getBaseUri(), mySysId);
    }
    
    /**
     * Calculates the resource location.
     * @param baseId
     * @param systemId
     * @return schema file path
     */
    protected String expandSystemId(String baseId, String systemId) {
        return getRequest().expandSystemId(baseId, systemId);
    }

    /**
     * Returns the resource as a reader, or {@code null}.
     * @return reader
     * @see #asReader()
     */
    public Reader getReader() {
        try {
            return createReader();
        } catch (Exception e) {
            throw resolvingException(e);
        }
    }
    
    /**
     * Optional: creates a reader to access the resource.
     * @return reader
     * @throws Exception 
     */
    protected Reader createReader() throws Exception {
        return null;
    }
    
    /**
     * Returns the resource as input stream, or {@code null}
     * @return input stream
     * @see #asInputStream() 
     */
    public InputStream getInputStream() {
        try {
            return createInputStream();
        } catch (Exception e) {
            throw resolvingException(e);
        }
    }
    
    /**
     * Optional: creates an input stream to access the resource.
     * @return input stream
     * @throws Exception 
     */
    protected InputStream createInputStream() throws Exception {
        return null;
    }
    
    /**
     * Returns the resource as string, or {@code null}
     * @return string
     */
    public String getString() {
        return null;
    }
    
    /**
     * Returns the resource as byte buffer, or {@code null}
     * @return string
     */
    public ByteBuffer getByteBuffer() {
        try {
            return createByteBuffer();
        } catch (Exception e) {
            throw resolvingException(e);
        }
    }
    
    /**
     * Optional: creates a byte buffer to access the resource.
     * @return byte buffer
     * @throws Exception 
     */
    protected ByteBuffer createByteBuffer() throws Exception {
        return null;
    }
    
    /**
     * Returns the resource as URL connection, or {@code null}
     * @return URL connection
     */
    public URLConnection getURLConnection() {
        try {
            return createURLConnection();
        } catch (Exception e) {
            throw resolvingException(e);
        }
    }
    
    /**
     * Optional: creates a URL connection to access the resource.
     * By default, tries to connect to the {@linkplain #getResourceURL() resource URL}.
     * @return URL connection
     * @throws Exception 
     */
    protected URLConnection createURLConnection() throws Exception {
        URL url = getResourceURL();
        if (url == null) return null;
        URLConnection uc = url.openConnection();
        // encoding should be applied when connecting
        return uc;
    }
    
    /**
     * Returns the resource as URL, or {@code null}
     * @return URL
     * @throws Exception 
     */
    public URL getResourceURL() throws Exception {
        return null;
    }
    
    /**
     * Return the {@linkplain #getResourceURL() resource URL}, 
     * or the  {@linkplain #getResolvedSystemId() system ID} as URL.
     * @return url
     * @throws Exception 
     */
    protected URL getResourceURLOrSystemId() throws Exception {
        URL url = getResourceURL();
        if (url != null) return url;
        return new URL(getResolvedSystemId());
    }
    
    /**
     * Sets the default encoding, which is used when no other encoding is defined.
     * @param enc 
     */
    public void setDefaultEncoding(String enc) {
        defaultEncoding = enc;
    }
    
    /**
     * Tries to set the result's encoding, returns true iff it was successful.
     * @param enc
     * @return true iff successful
     */
    public boolean trySetEncoding(String enc) {
        setDefaultEncoding(enc);
        return Objects.equals(enc, getEncoding());
    }
    
    /**
     * Sets the result's encoding. 
     * Throws an excption if encoding could not be changed.
     * @param enc 
     * @throws IllegalStateException if encoding could not be changed.
     */
    public void setEncoding(String enc) {
        if (!trySetEncoding(enc)) {
            throw new IllegalStateException(
                    this + ": cannot change encoding from " +
                    getEncoding() + " to " + enc);
        }
    }
    
    /**
     * Returns the resource's encoding.
     * @return encoding
     */
    public String getEncoding() {
        return defaultEncoding;
    }
    
    /**
     * Returns the charset for the result's {@linkplain #getEncoding() encoding},
     * or the default charset of this JVM.
     * @return charset
     */
    public Charset getCharset() {
        String enc = getEncoding();
        if (enc == null) {
            return Charset.defaultCharset();
        } else {
            return Charset.forName(enc);
        }
    }
    
    /**
     * Returns the resource as a reader, converting it if necessary.
     * @return reader
     */
    public Reader asReader() {
        Reader r = getReader();
        if (r != null) return r;
        String s = getString();
        if (s != null) return new StringReader(s);
        InputStream is = fromStreamOrBufferOrUrl();
        if (is != null) return readerFrom(is);
        throw resourceUnaccessible();
    }
    
    /**
     * Returns the resource as an input stream, converting it if necessary.
     * @return input stream
     */
    public InputStream asInputStream() {
        InputStream is = fromStreamOrBufferOrUrl();
        if (is != null) return is;
        String s = fromStringOrReader();
        if (s != null) return streamFrom(s);
        throw resourceUnaccessible();
    }
    
    /**
     * Returns the resource as a byte buffer, converting it if necessary.
     * @return byte buffer
     */
    public ByteBuffer asByteBuffer() {
        ByteBuffer bb = getByteBuffer();
        if (bb != null) return null;
        InputStream is = fromStreamOrUrl();
        if (is != null) return bufferFrom(is);
        String s = fromStringOrReader();
        if (s != null) return RResult.this.bufferFrom(s);
        throw resourceUnaccessible();
    }
    
    /**
     * Returns the resource as a string, converting it if necessary.
     * @return string
     */
    public String asString() {
        String s = fromStringOrReader();
        if (s != null) return s;
        ByteBuffer bb = getByteBuffer();
        if (bb != null) return stringFrom(bb);
        InputStream is = fromStreamOrUrl();
        if (is != null) {
            Reader r = readerFrom(is);
            return stringFrom(r);
        }
        throw resourceUnaccessible();
    }
    
    /**
     * Returns the resource as URL connection, converting it if necessary.
     * @return URL connection
     */
    public URLConnection asURLConnection() {
        URLConnection uc = getURLConnection();
        if (uc != null) return uc;
        try {
            return new InputStreamURLConnection(
                    getResourceURLOrSystemId(), 
                    this, 
                    getDefaultUrlConnectionHeader());
        } catch (Exception e) {
            throw resolvingException(e);
        }
    }
    
    protected Map<String, String> getDefaultUrlConnectionHeader() {
        return null;
    }
    
    public BufferedInputStream asBufferedInputStream() {
        InputStream is = asInputStream();
        if (is == null) return null;
        if (is instanceof BufferedInputStream) return (BufferedInputStream) is;
        return new BufferedInputStream(is);
    }
    
    protected String fromStringOrReader() {
        String s = getString();
        if (s != null) return s;
        Reader r = getReader();
        if (r != null) return stringFrom(r);
        return null;
    }
    
    protected InputStream fromStreamOrBuffer() {
        InputStream is = getInputStream();
        if (is != null) return is;
        ByteBuffer bb = getByteBuffer();
        if (bb != null) return streamFrom(bb);
        return null;
    }
    
    protected InputStream fromStreamOrBufferOrUrl() {
        InputStream is = fromStreamOrBuffer();
        if (is != null) return is;
        return streamFromURLConnection();
    }
    
    protected InputStream fromStreamOrUrl() {
        InputStream is = getInputStream();
        if (is != null) return is;
        return streamFromURLConnection();
    }
    
    protected String stringFrom(Reader r) {
        if (r == null) return null;
        try (Reader __ = r) {
            final char[] buf = new char[BUF_SIZE];
            final StringWriter sw = new StringWriter();
            int n;
            while (-1 < (n = r.read(buf))) {
                sw.write(buf, 0, n);
            }
            return sw.toString();
        } catch (IOException e) {
            throw resolvingException(e);
        }
    }
    
    protected String stringFrom(ByteBuffer bb) {
        if (bb == null) return null;
        return getCharset().decode(bb).toString();
    }

    protected Reader readerFrom(InputStream is) throws ResolvingException {
        if (is == null) return null;
        return new InputStreamReader(is, getCharset());
    }

    protected InputStream streamFrom(String s) throws ResolvingException {
        if (s == null) return null;
        byte[] data = bytesFrom(s);
        return new ByteArrayInputStream(data);
    }

    protected InputStream streamFrom(ByteBuffer bb) {
        if (bb == null) return null;
        return new ByteBufferInputStream(bb);
    }
    
    protected InputStream streamFromURLConnection() {
        return streamFrom(getURLConnection());
    }
    
    protected InputStream streamFrom(URLConnection uc) {
        if (uc == null) return null;
        try {
            uc.connect();
            String enc = uc.getContentEncoding();
            if (enc != null) setDefaultEncoding(enc);
            return uc.getInputStream();
        } catch (IOException e) {
            throw resolvingException(e);
        }
    }
    
    protected ByteBuffer bufferFrom(String s) throws ResolvingException {
        if (s == null) return null;
        return getCharset().encode(s);
    }

    protected ByteBuffer bufferFrom(InputStream is) {
        if (is == null) return null;
        try (InputStream __ = is) {
            if (is instanceof FileInputStream) {
                FileInputStream fis = (FileInputStream) is;
                return fis.getChannel().map(
                        FileChannel.MapMode.READ_ONLY, 
                        0, fis.getChannel().size());
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(BUF_SIZE); 
            byte[] buf = new byte[BUF_SIZE];
            int n;
            while(-1 < (n = is.read(buf))) {
                out.write(buf, 0, n);
            }
            return ByteBuffer.wrap(out.toByteArray());
        } catch (IOException e) {
            throw resolvingException(e);
        }
    }
    
    protected byte[] bytesFrom(String s) throws ResolvingException {
        if (s == null) return null;
        return s.getBytes(getCharset());
    }
    
    protected RuntimeException resolvingException(Throwable t) {
        return new ResolvingException(toString(), t);
    }
    
    protected RuntimeException resourceUnaccessible() {
        return new ResolvingException(toString() + " is not accessible");
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
        if (s == null) return cn;
        return cn + "(" + s + ")";
    }

    private static final int BUF_SIZE = 8192;
    
    protected static class ByteBufferInputStream extends InputStream {

        final ByteBuffer buf;

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

        @Override
        public void close() throws IOException {
        }
    }
    
    protected static class InputStreamURLConnection extends URLConnection {

        private final RResult result;
        private final Map<String, String> headerMap;
        private InputStream is = null;
        private List<String> headerKeys = null;

        public InputStreamURLConnection(URL url, RResult result, Map<String, String> headerMap) {
            super(url);
            this.result = result;
            if (headerMap != null) {
                this.headerMap = new HashMap<>(headerMap);
            } else {
                this.headerMap = new HashMap<>();
            }
        }

        @Override
        public synchronized void connect() throws IOException {
            if (connected) return;
            connected = true;
            is = result.asInputStream();
            String enc = headerMap.get("content-encoding");
            if (enc != null) {
                result.setDefaultEncoding(enc);
            } else {
                headerMap.put("content-encoding", result.getEncoding());
            }
            headerKeys = new ArrayList<>(headerMap.keySet());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return is;
        }

        @Override
        public String getHeaderField(String name) {
            return headerMap.get(name);
        }

        @Override
        public String getHeaderField(int n) {
            return getHeaderField(headerKeys.get(n));
        }

        @Override
        public String getHeaderFieldKey(int n) {
            if (n < 0 || n >= headerKeys.size()) return null;
            return headerKeys.get(n);
        }

        @Override
        public Map<String, List<String>> getHeaderFields() {
            Map<String, List<String>> fields = new HashMap<>();
            for (Map.Entry<String, String> e: headerMap.entrySet()) {
                fields.put(e.getKey(), Arrays.asList(e.getValue()));
            }
            return fields;
        }
    }
}
