package org.cthul.resolve.results;

import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import org.cthul.resolve.RRequest;
import org.cthul.resolve.RResult;

public abstract class InputStreamResult extends RResult {

    public InputStreamResult(RRequest request) {
        super(request);
    }

    public InputStreamResult(RRequest request, String systemId) {
        super(request, systemId);
    }

    public InputStreamResult(String uri, String publicId, String systemId, String baseUri) {
        super(uri, publicId, systemId, baseUri);
    }

    @Override
    public ResultType getResultType() {
        return ResultType.STREAM;
    }

    @Override
    protected abstract InputStream createInputStream() throws Exception;
    
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
        return stringFrom(readerFrom(getInputStream()));
    }
}
