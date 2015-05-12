package org.cthul.resolve.results;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import org.cthul.resolve.RRequest;
import org.cthul.resolve.RResult;

/**
 * Result based on a byte array or ByteBuffer.
 */
public class BytesResult extends RResult {
    
    private final ByteBuffer buffer;

    public BytesResult(RRequest request, String systemId, byte[] data) {
        this(request, systemId, data, false);
    }
    
    public BytesResult(RRequest request, String systemId, byte[] data, boolean copy) {
        this(request, systemId, ByteBuffer.wrap(copy ? data.clone() : data));
    }
    
    public BytesResult(RRequest request, String systemId, ByteBuffer data) {
        this(request, systemId, data, false);
    }
    
    public BytesResult(RRequest request, String systemId, ByteBuffer data, boolean copy) {
        super(request, systemId);
        if (copy) {
            this.buffer = ByteBuffer.allocate(data.remaining());
            this.buffer.put(data);
        } else {
            this.buffer = data;
        }
    }
    
    public BytesResult(RRequest request, String systemId, InputStream is) throws IOException {
        super(request, systemId);
        this.buffer = bufferFrom(is);
    }

    @Override
    public ResultType getResultType() {
        return ResultType.BUFFER;
    }

    @Override
    protected ByteBuffer createByteBuffer() throws Exception {
        return buffer.asReadOnlyBuffer();
    }

    @Override
    public InputStream asInputStream() {
        return streamFrom(getByteBuffer()); 
   }

    @Override
    public Reader asReader() {
        return readerFrom(asInputStream());
    }

    @Override
    public String asString() {
        return stringFrom(getByteBuffer());
    }
}
