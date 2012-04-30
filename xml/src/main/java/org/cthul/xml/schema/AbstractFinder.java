/*
 * 
 */

package org.cthul.xml.schema;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractFinder implements SchemaFinder {

    private final Map<String, String> schemaMap = new HashMap<>();
    private final Map<Pattern, String> domainMap = new HashMap<>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AbstractFinder(String... schemas) {
        addSchemas(schemas);
    }

    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AbstractFinder(Map<String, String> schemas) {
        addSchemas(schemas);
    }

    public AbstractFinder() {
    }
    
    /**
     * Returns a schema finder that uses this schema finder for look-up,
     * but is not mutable.
     * @return 
     */
    public SchemaFinder immutable() {
        return new CompositeFinder(this);
    }

    public AbstractFinder addSchema(String uri, String resource) {
        schemaMap.put(uri, resource);
        return this;
    }

    public AbstractFinder addSchemas(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addSchema(values[i], values[i+1]);
        }
        return this;
    }

    public AbstractFinder addSchemas(Map<String, String> schemas) {
        schemaMap.putAll(schemas);
        return this;
    }
    
    public AbstractFinder addDomain(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(Pattern.quote(domain));
        addDomainPattern(domainPattern, altPath);
        return this;
    }
    
    public AbstractFinder addDomains(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addDomain(values[i], values[i+1]);
        }
        return this;
    }
    
    public AbstractFinder addDomainPattern(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(domain);
        addDomainPattern(domainPattern, altPath);
        return this;
    }
    
    public AbstractFinder addDomainPattern(Pattern pattern, String replacement) {
        domainMap.put(pattern, replacement);
        return this;
    }
    
    public AbstractFinder addDomainPatterns(String... values) {
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
    public InputStream find(String uri) {
        String source = resolve(uri);
        if (source == null) return null;
        return get(source);
    }

    protected abstract InputStream get(String source);

}
