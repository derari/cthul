package org.cthul.resolve;

import java.util.HashMap;
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
    
    public UriMappingResolver addDomain(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(quote(domain));
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

    protected String resolve(String uri) {
        String result = schemaMap.get(uri);
        if (result != null) return result;

        for (Map.Entry<Pattern, String> domain: domainMap.entrySet()) {
            Matcher m = domain.getKey().matcher(uri);
            if (m.find()) {
                StringBuffer sb = new StringBuffer();
                m.appendReplacement(sb, domain.getValue());
                m.appendTail(sb);
                return sb.toString();
            }
        }
        
        return null;
    }

    @Override
    public RResult resolve(RRequest request) {
        String source = resolve(request.getUri());
        if (source == null) return null;
        return get(request, source);
    }
    
    protected abstract RResult get(RRequest request, String source);

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
