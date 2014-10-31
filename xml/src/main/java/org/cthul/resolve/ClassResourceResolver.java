package org.cthul.resolve;

import java.net.URL;
import java.util.Map;
import org.cthul.resolve.results.URLResult;

/**
 * Looks up resources in the classpath relative to a given class.
 * @see Class#getResource(java.lang.String)
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
        return new URLResult(request, url);
    }
}
