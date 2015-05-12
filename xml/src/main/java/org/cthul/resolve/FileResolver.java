package org.cthul.resolve;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cthul.resolve.results.FileResult;

/**
 * Looks up resources on the local file system.
 */
public class FileResolver extends UriMappingResolver {

    private final Path base;
    private final Path canonicalBase;

    /**
     * 
     * @param base base path, relative to which all requests will be looked up
     * @param check path that contains all available files. Access outside
     *          of this path will be prevented. If null, every file in the
     *          file system can be accessed.
     */
    public FileResolver(Path base, Path check) {
        this.base = base;
        this.canonicalBase = check != null 
                ? base.resolve(check).normalize().toAbsolutePath() 
                : null;
    }

    /**
     * 
     * @param base base path, relative to which all requests will be looked up
     * @param check path that contains all available files. Access outside
     *          of this path will be prevented. If null, every file in the
     *          file system can be accessed.
     */
    public FileResolver(File base, String check) {
        this(base != null ? Paths.get(base.toURI()) : null,
                check != null ? Paths.get(check) : null);
    }
    
    public FileResolver() {
        this(null, (String) null);
    }

    /**
     * 
     * @param base base path, relative to which all requests will be looked up
     */
    public FileResolver(String base) {
        this(new File(base));
    }

    /**
     * 
     * @param base base path, relative to which all requests will be looked up
     */
    public FileResolver(File base) {
        this(base, base);
    }

    /**
     * 
     * @param base base path, relative to which all requests will be looked up
     * @param check path that contains all available files. Access outside
     *          of this path will be prevented. If null, every file in the
     *          file system can be accessed.
     */
    public FileResolver(File base, File check) {
        this(base, canonicalPath(check));
    }

    @Override
    public UriMappingResolver lookupAll() {
        addDomain("file:/");
        return super.lookupAll();
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
    protected RResponse get(RRequest request, String source) {
        final Path p;
        try {
            p = base != null ?
                    base.resolve(source).normalize() :
                    Paths.get(source);
        } catch (InvalidPathException e) {
            return request.noResultResponse().withWarning(e);
        }
        if (canonicalBase != null) {
            if (!p.toAbsolutePath().startsWith(canonicalBase)) {
                return null;
            }
        }
        if (!Files.isRegularFile(p)) return null;
        return new FileResult(request, p);
    }

    @Override
    public String toString() {
        String m = getMappingString();
        return getClass().getSimpleName() + "(" +
                (base == null ? "" : base) + 
                (m.isEmpty() ? "" : ": " + m) + ")";
    }

}
