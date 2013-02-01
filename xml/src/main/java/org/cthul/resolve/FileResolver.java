package org.cthul.resolve;

import java.io.*;

/**
 * Looks up resources on the local file system.
 * 
 * @author Arian Treffer
 */
public class FileResolver extends UriMappingResolver {

    private final File base;
    private final String canonicalBase;

    public FileResolver() {
        this((File) null);
    }

    public FileResolver(String base) {
        this(new File(base));
    }

    public FileResolver(File base) {
        try {
            this.base = base;
            if (base == null) canonicalBase = null;
            else this.canonicalBase = base.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileResolver(File base, String baseCheck) {
        this.base = base;
        this.canonicalBase = baseCheck;
    }

    @Override
    protected RResult get(RRequest request, String source) {
        final File f = new File(base, source);
        if (canonicalBase != null) {
            try {
                if (!f.getCanonicalPath().startsWith(canonicalBase)) {
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!f.isFile()) return null;
        return new RResult(request, f.toURI().toString()){
            @Override
            public InputStream createInputStream() throws FileNotFoundException {
                return new FileInputStream(f);
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
