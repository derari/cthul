package org.cthul.resolve.results;

import java.io.InputStream;
import java.nio.ByteBuffer;
import org.cthul.resolve.RRequest;
import org.cthul.resolve.RResult;

public class StringResult extends RResult {

    private final String data;
    
    public StringResult(RRequest request, String systemId, String data) {
        super(request, systemId);
        this.data = data;
    }

    @Override
    public String getString() {
        return data;
    }

    @Override
    public InputStream asInputStream() {
        return streamFrom(getString());
    }

    @Override
    public ByteBuffer asByteBuffer() {
        return bufferFrom(getString());
    }
}
