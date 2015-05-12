package org.cthul.resolve.results;

import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import org.cthul.resolve.RRequest;
import org.cthul.resolve.RResult;

public class URLResult extends RResult {

    private final URL resourceUrl;

    public URLResult(RRequest request, Path path) throws MalformedURLException {
        this(request, path.toUri().toURL());
    }
    
    public URLResult(RRequest request, URL resourceUrl) {
        super(request, resourceUrl.toString());
        this.resourceUrl = resourceUrl;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.URL;
    }

    @Override
    public URL getResourceURL() throws Exception {
        return resourceUrl;
    }

    @Override
    protected InputStream createInputStream() throws Exception {
        return streamFromURLConnection();
    }

    @Override
    public ByteBuffer asByteBuffer() {
        return bufferFrom(getInputStream());
    }

    @Override
    public Reader asReader() {
        return readerFrom(getInputStream());
    }

    @Override
    public String asString() {
        return stringFrom(asReader());
    }
}
