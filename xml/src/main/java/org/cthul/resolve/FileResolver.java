package org.cthul.resolve;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Looks up resources on the local file system.
 */
public class FileResolver extends UriMappingResolver {

    private final Path base;
    private final Path canonicalBase;

    public FileResolver(File base, String baseCheck) {
        this.base = Paths.get(base.toURI());
        this.canonicalBase = baseCheck != null 
                ? Paths.get(baseCheck).normalize().toAbsolutePath() 
                : null;
    }
    
    public FileResolver() {
        this(null, (String) null);
    }

    public FileResolver(String base) {
        this(new File(base));
    }

    public FileResolver(File base) {
        this(base, base);
    }

    public FileResolver(File base, File check) {
        this(base, canonicalPath(check));
    }
    
    private static String canonicalPath(File f) {
        if (f == null) return null;
        try {
            return f.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected RResult get(RRequest request, String source) {
        final Path p = base.resolve(source).normalize();
        if (canonicalBase != null) {
            if (!p.toAbsolutePath().startsWith(canonicalBase)) {
                return null;
            }
        }
        if (!Files.isRegularFile(p)) return null;
        return new RResult(request, p.toUri().toString()){
            @Override
            public InputStream createInputStream() throws IOException {
                return Files.newInputStream(p, StandardOpenOption.READ);
            }
            @Override
            protected ByteBuffer createByteBuffer() throws Exception {
                FileChannel fc = FileChannel.open(p, StandardOpenOption.READ);
                return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            }
        };
    }

    @Override
    public String toString() {
        String m = getMappingString();
        return getClass().getSimpleName() + "(" +
                base + (m.isEmpty() ? "" : ": " + m) + ")";
    }

}
