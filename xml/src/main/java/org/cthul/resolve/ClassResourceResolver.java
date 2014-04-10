package org.cthul.resolve;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Looks up resources in the classpath relative to a given class.
 * @see Class#getResource(java.lang.String)
 * @author Arian Treffer
 */
public class ClassResourceResolver extends UriMappingResolver {

    private final Class<?> clazz;

    public ClassResourceResolver(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ClassResourceResolver(Class<?> clazz, String... schemas) {
        super(schemas);
        this.clazz = clazz;
    }

    public ClassResourceResolver(Class<?> clazz, Map<String, String> schemas) {
        super(schemas);
        this.clazz = clazz;
    }

    @Override
    protected RResult get(RRequest request, String source) {
        final URL url = clazz.getResource(source);
        if (url == null) return null;
        return new RResult(request, url.toString()) {
            @Override
            public InputStream createInputStream() throws IOException {
                return url.openStream();
            }
        };
    }
}
