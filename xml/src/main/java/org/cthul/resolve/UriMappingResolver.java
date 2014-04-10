package org.cthul.resolve;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.cthul.strings.RegEx;

/**
 * Uses regular expressions to convert a {@link RRequest#getUri() requested URI}
 * into an internal format. 
 * Subclasses are responsible for finding a result for the mapped string.
 * @see ClassResourceResolver
 * @see FileResolver
 * @author Arian Treffer
 */
public abstract class UriMappingResolver extends ResourceResolverBase {

//    private boolean simpleQuote = false;
    private final Map<String, String> schemaMap = new HashMap<>();
    private final Map<Pattern, String> domainMap = new HashMap<>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UriMappingResolver(String... schemas) {
        addSchemas(schemas);
    }

    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UriMappingResolver(Map<String, String> schemas) {
        addSchemas(schemas);
    }

    public UriMappingResolver() {
    }
    
//    /**
//     * Make this use {@link Pattern#quote(java.lang.String)} instead of
//     * {@link RegEx#quote(java.lang.String)} for creating patterns from domains.
//     * @return this
//     */
    /**
     * Configures that {@link Pattern#quote(java.lang.String)} will be used
     * to escape domain names. Enabled by default.
     * @return this
     */
    public UriMappingResolver useSimpleQuoting() {
//        simpleQuote = true;
        return this;
    }
    
    protected String quote(String pattern) {
//        if (simpleQuote) {
            return Pattern.quote(pattern);
//        } else {
//            return RegEx.quote(pattern);
//        }
    }
    
    /**
     * Alias for {@link #addResource(java.lang.String, java.lang.String)}
     * @param uri
     * @param schemas
     * @return this
     */
    public UriMappingResolver addSchema(String uri, String schemas) {
        return addResource(uri, schemas);
    }

    /**
     * Alias for {@link #addResources(java.lang.String[])}
     * @param schemas
     * @return this
     */
    public UriMappingResolver addSchemas(String... schemas) {
        return addResources(schemas);
    }

    /**
     * Alias for {@link #addResources(java.util.Map)}
     * @param schemas
     * @return 
     */
    public UriMappingResolver addSchemas(Map<String, String> schemas) {
        return addResources(schemas);
    }
    
    /**
     * Adds a single resource that can be looked-up by its uri.
     * @param uri
     * @param resource
     * @return this
     */
    public UriMappingResolver addResource(String uri, String resource) {
        schemaMap.put(uri, resource);
        return this;
    }

    /**
     * Calls {@link #addResource(java.lang.String, java.lang.String)} with
     * pairs taken from the argument array.
     * @param resources array of uri-resource pairs
     * @return this
     * @throws IllegalArgumentException if length of resources is not even
     */
    public UriMappingResolver addResources(String... resources) {
        if (resources.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < resources.length; i += 2) {
            addSchema(resources[i], resources[i+1]);
        }
        return this;
    }

    /**
     * Adds all resources so that they can be looked up by the uris.
     * @param resources uri-resource map
     * @return this
     */
    public UriMappingResolver addResources(Map<String, String> resources) {
        schemaMap.putAll(resources);
        return this;
    }
    
    /**
     * Adds a domain for relative lookup.
     * All uris starting with {@code domain} will be replaced with 
     * {@code replacement}. In the replacement string {@code "$1"}, will
     * be replaced with path of the request uri.
     * Path separators ({@code '/') between the domain and the path will be removed.
     * @param domain
     * @param replacement
     * @return this
     */
    public UriMappingResolver addDomain(String domain, String replacement) {
        addDomainPattern(quote(domain) + "[/]*(.*)", replacement);
        return this;
    }
    
    /**
     * Adds a domain for which paths will be looked up.
     * 
     * Equivalent to {@link #addDomain(java.lang.String, java.lang.String) addDomain(domain, "$1")}.
     * @param domain
     * @return this
     */
    public UriMappingResolver addDomain(String domain) {
        return addDomain(domain, "$1");
    }
    
    /**
     * Calls {@link #addDomain(java.lang.String, java.lang.String)} with
     * pairs from the argument array.
     * @param values
     * @throws IllegalArgumentException if length of resources is not even
     * @return this
     */
    public UriMappingResolver addDomains(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addDomain(values[i], values[i+1]);
        }
        return this;
    }
    
    /**
     * Adds a rule to look-up all uris as they are.
     * Equivalent to {@link #addDomain(java.lang.String, java.lang.String) addDomain("", "$1")}.
     * @return this
     */
    public UriMappingResolver lookupAll() {
        return addDomain("", "$1");
    }
    
//    /**
//     * Adds a rule to look-up all uris as they are.
//     * Equivalent to {@link #addDomain(java.lang.String, java.lang.String) addDomain("", "$1")}.
//     * @return this
//     */
//    public UriMappingResolver lookupAllAbsolute() {
//        return addDomain("", "$1");
//    }
    
    public UriMappingResolver addDomainPattern(String domain, String replacement) {
        Pattern domainPattern = Pattern.compile(domain);
        addDomainPattern(domainPattern, replacement);
        return this;
    }
    
    public UriMappingResolver addDomainPattern(Pattern pattern, String replacement) {
        domainMap.put(pattern, replacement);
        return this;
    }
    
    public UriMappingResolver addDomainPatterns(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addDomainPattern(values[i], values[i+1]);
        }
        return this;
    }
    
    protected Iterator<String> resolver(String uri) {
        return new Resolver(uri);
    }
    
    protected String mapSchema(String uri) {
        return schemaMap.get(uri);
    }
    
    protected Iterator<Map.Entry<Pattern, String>> domains() {
        return domainMap.entrySet().iterator();
    }
    
    protected String mapDomain(Map.Entry<Pattern, String> domain, String uri) {
        Matcher m = domain.getKey().matcher(uri);
        if (!m.find()) return null;
        StringBuffer sb = new StringBuffer();
        m.appendReplacement(sb, domain.getValue());
        m.appendTail(sb);
        return sb.toString();
    }

    @Override
    public RResult resolve(RRequest request) {
        return resolve(request, uri(request));
    }
    
    protected String uri(RRequest request) {
        return request.getUriOrId();
    }
    
    protected RResult resolve(RRequest request, String uri) {
        final Iterator<String> resolver = resolver(uri);
        while (resolver.hasNext()) {
            String src = resolver.next();
            if (src == null) continue;
            RResult res = get(request, src);
            if (res != null) return res;
        }
        return null;
    }

    /**
     * Returns a resource for the requested uri, or {@code null} if it could
     * not be found.
     * @param request
     * @param uri
     * @return result or {@code null}
     */
    protected abstract RResult get(RRequest request, String uri);
    
    protected class Resolver implements Iterator<String> {
        
        protected final String uri;
        protected int stage = 0;
        protected String next = null;
        protected Iterator<Map.Entry<Pattern, String>> patterns = null;

        public Resolver(String uri) {
            this.uri = uri;
        }

        @Override
        public boolean hasNext() {
            return checkNext();
        }

        @Override
        public String next() {
            if (!checkNext()) {
                throw new IllegalStateException("No more elements");
            }
            final String result = next;
            next = null;
            return result;
        }
        
        protected boolean checkNext() {
            if (next != null) return true;
            if (stage == 0) {
                stage++;
                next = mapSchema(uri);
                if (next != null) return true;
            }
            if (stage == 1) {
                stage++;
                patterns = domains();
            }
            while (patterns.hasNext()) {
                next = mapDomain(patterns.next(), uri);
                if (next != null) return true;
            }
            return false;
        }

        @Override
        public void remove() { throw new UnsupportedOperationException(); }
        
    }

    /** string representation for debugging purposes */
    protected String getMappingString() {
        StringBuilder sb = new StringBuilder();
        final int schemaSize = schemaMap.size();
        final int domainSize = domainMap.size();
        int s = 0, d = 0;
        for (String schema: schemaMap.keySet()) {
            if (s > 0) sb.append(", ");
            sb.append(schema);
            s++;
            if (s > 2 && schemaSize > 3) {
                sb.append(", ");
                sb.append(schemaSize - s);
                sb.append(" more");
                break;
            }
        }
        if (schemaSize > 0 && domainSize > 0) sb.append("; ");
        for (Pattern domain: domainMap.keySet()) {
            if (d > 0) sb.append(", ");
            sb.append(domain.pattern());
            d++;
            if (d > 2 && domainSize > 3) {
                sb.append(", ");
                sb.append(domainSize - s);
                sb.append(" more");
                break;
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getMappingString() + ")";
    }
}
