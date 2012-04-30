package org.cthul.xml.schema;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.cthul.log.CLogger;
import org.cthul.log.CLoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Returns the schema file for a namespace uri.
 * 
 * @author Arian Treffer
 */
public class SchemaResolver implements LSResourceResolver {
    
    static final CLogger log = CLoggerFactory.getClasslogger();

    /**
     * Provides the W3 schemas.
     */
    public static final SchemaFinder ORG_W3_SCHEMA_FINDER;

    public static final String NS_W3_XMLSCHEMA =
            "http://www.w3.org/2001/XMLSchema";
    public static final String NS_W3_XML =
            "http://www.w3.org/XML/1998/namespace";
    public static final String NS_W3_XML_XSD =
            "http://www.w3.org/2001/xml.xsd";

    static {
        Map<String, String> orgW3 = new HashMap<>();
        orgW3.put(NS_W3_XMLSCHEMA,    "/org/w3/XMLSchema.xsd");
        orgW3.put(NS_W3_XML,          "/org/w3/xml.xsd");
        orgW3.put(NS_W3_XML_XSD,      "/org/w3/xml.xsd");

        ORG_W3_SCHEMA_FINDER = new ResourceFinder(SchemaResolver.class)
                .addSchemas(orgW3).immutable();
    }

    private final SchemaFinder finder;

    public SchemaResolver(SchemaFinder finder) {
        this.finder = finder;
    }
    
    public SchemaResolver(SchemaFinder... finder) {
        this(new CompositeFinder(finder));
    }

    private InputStream find(String uri) {
        if (uri == null) return null;
        return finder.find(uri);
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI,
                            String publicId, String systemId, String baseURI) {

        String uri;
        InputStream source;
        
        if (namespaceURI == null) {
            // this is no real schema
            return null;
        }

        uri = namespaceURI;
        source = find(namespaceURI);
        
        if (source == null) {
            uri = expandSystemId(baseURI, systemId);
            source = find(uri);
        }

        if (source == null) {
            log.warn("Could not resolve schema %s in %s", namespaceURI, uri);
            return null;
        }

        log.info("Resolved %s as %s", namespaceURI, uri);
        return new LSStreamInput(source, type, namespaceURI, publicId,
                                 systemId, baseURI, uri);
    }
    
    /**
     * Calculates the schema file location.
     * @param baseId
     * @param systemId
     * @return 
     */
    private String expandSystemId(String baseId, String systemId) {
        if (baseId == null || baseId.isEmpty()) return systemId;
        if (systemId == null || systemId.isEmpty()) return baseId;
        int lastSep = baseId.lastIndexOf('/');
        if (lastSep < 0) return systemId;
        return baseId.substring(0, lastSep+1) + systemId;
    }

}
