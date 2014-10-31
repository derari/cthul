package org.cthul.resolve.results;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.cthul.resolve.RRequest;
import org.cthul.resolve.RResult;

public class FileResult extends RResult {
    
    private final Path path;

    public FileResult(RRequest request, Path path) {
        super(request, path.toUri().toString());
        this.path = path;
    }
    
    public FileResult(RRequest request, Path path, String encoding) {
        this(request, path);
        setDefaultEncoding(encoding);
    }

    public FileResult(RRequest request, File file) {
        this(request, Paths.get(file.toURI()));
    }
    
    public FileResult(RRequest request, File path, String encoding) {
        this(request, path);
        setDefaultEncoding(encoding);
    }
    
    @Override
    public InputStream createInputStream() throws IOException {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }
    
    @Override
    protected ByteBuffer createByteBuffer() throws Exception {
        FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
        return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    }

    @Override
    public URL getResourceURL() throws Exception {
        return path.toUri().toURL();
    }
}
