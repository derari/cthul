package org.cthul.resolve;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Looks up resources in the classpath of a given class.
 * @see ClassLoader#getResource(java.lang.String)
 */
public class ClassLoaderResourceResolver extends UriMappingResolver {

    private final ClassLoader clazzLoader;

    private static ClassLoader getClassLoader(ClassLoader cl) {
        if (cl != null) return cl;
        return Thread.currentThread().getContextClassLoader();
    }
    
    public ClassLoaderResourceResolver() {
        this(null);
    }
    
    public ClassLoaderResourceResolver(ClassLoader cl) {
        this.clazzLoader = getClassLoader(cl);
    }

    public ClassLoaderResourceResolver(ClassLoader cl, String... schemas) {
        super(schemas);
        this.clazzLoader = getClassLoader(cl);
    }

    public ClassLoaderResourceResolver(ClassLoader cl, Map<String, String> schemas) {
        super(schemas);
        this.clazzLoader = getClassLoader(cl);
    }

    @Override
    protected RResult get(RRequest request, String source) {
        final URL url = clazzLoader.getResource(source);
        if (url == null) return null;
        return new RResult(request, url.toString()) {
            @Override
            public InputStream createInputStream() throws IOException {
                return url.openStream();
            }
        };
    }
}
