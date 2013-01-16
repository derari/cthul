package org.cthul.resolve;

import java.io.*;

/**
 * Looks up resources on the local file system.
 * 
 * @author Arian Treffer
 */
public class FileResolver extends UriMappingResolver {

    private final String prefix;

    public FileResolver() {
        this("");
    }

    public FileResolver(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected RResult get(RRequest request, String source) {
        final File f = new File(prefix, source);
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
                prefix + (m.isEmpty() ? "" : ": " + m) + ")";
    }

}
