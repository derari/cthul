package org.cthul.xml.schema;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.strings.RegEx;

/**
 *
 * @author Arian Treffer
 */
public abstract class MappingFinder extends AbstractFinder {

    private boolean simpleQuote = false;
    private final Map<String, String> schemaMap = new HashMap<>();
    private final Map<Pattern, String> domainMap = new HashMap<>();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MappingFinder(String... schemas) {
        addSchemas(schemas);
    }

    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MappingFinder(Map<String, String> schemas) {
        addSchemas(schemas);
    }

    public MappingFinder() {
    }
    
    public MappingFinder useSimpleQuoting() {
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
    
    public MappingFinder addSchema(String uri, String resource) {
        schemaMap.put(uri, resource);
        return this;
    }

    public MappingFinder addSchemas(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addSchema(values[i], values[i+1]);
        }
        return this;
    }

    public MappingFinder addSchemas(Map<String, String> schemas) {
        schemaMap.putAll(schemas);
        return this;
    }
    
    public MappingFinder addDomain(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(quote(domain));
        addDomainPattern(domainPattern, altPath);
        return this;
    }
    
    public MappingFinder addDomains(String... values) {
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException
                    ("Expected even number of arguments");
        }
        for (int i = 0; i < values.length; i += 2) {
            addDomain(values[i], values[i+1]);
        }
        return this;
    }
    
    public MappingFinder addDomainPattern(String domain, String altPath) {
        Pattern domainPattern = Pattern.compile(domain);
        addDomainPattern(domainPattern, altPath);
        return this;
    }
    
    public MappingFinder addDomainPattern(Pattern pattern, String replacement) {
        domainMap.put(pattern, replacement);
        return this;
    }
    
    public MappingFinder addDomainPatterns(String... values) {
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
