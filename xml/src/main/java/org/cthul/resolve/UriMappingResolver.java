package org.cthul.resolve;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.strings.RegEx;

/**
 *
 * @author Arian Treffer
 */
public abstract class UriMappingResolver extends AbstractResolver {

    private boolean simpleQuote = false;
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
    
    public UriMappingResolver useSimpleQuoting() {
        simpleQuote = true;
        return this;
    }
    
    protected String quote(String pattern) {
        if (simpleQuote) {
            return Pattern.quote(pattern);
        } else {
            return RegEx.quote(pattern);
        }
    }
    
    public UriMappingResolver addSchema(String uri, String resource) {
        schemaMap.put(uri, resource);
        return this;
    }

    public UriMappingResolver addSchemas(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addSchema(values[i], values[i+1]);
        }
        return this;
    }

    public UriMappingResolver addSchemas(Map<String, String> schemas) {
        schemaMap.putAll(schemas);
        return this;
    }
    
    public UriMappingResolver addDomain(String domain) {
        return addDomain(domain, "$1");
    }
    
    public UriMappingResolver addDomain(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(quote(domain) + "(.*)");
        addDomainPattern(domainPattern, altPath);
        return this;
    }
    
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
    
    public UriMappingResolver addDomainPattern(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(domain);
        addDomainPattern(domainPattern, altPath);
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
        return resolve(request, request.getUri());
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

    protected abstract RResult get(RRequest request, String source);
    
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

    /** for debugging purposes */
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
            }
        }
        if (schemaSize > 0 && domainSize > 0) sb.append("; ");
        for (Pattern domain: domainMap.keySet()) {
            if (d > 0) sb.append(", ");
            sb.append(domain.pattern());
            d++;
            if (s > 2 && domainSize > 3) {
                sb.append(", ");
                sb.append(domainSize - s);
                sb.append(" more");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getMappingString() + ")";
    }

}
