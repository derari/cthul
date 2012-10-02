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
 * <p/>
 * Usage:
 * <pre>
 * new SchemaResolver(SchemaResolver.ORG_W3_SCHEMA_FINDER, myFinder1, myFinder2);
 * </pre>
 * or
 * <pre>
 * SchemaFinder finder = new CompositeFinder(
 *          OrgW3Finder.INSTANCE,
 *          myFinder1,
 *          myFinder2
 *      );
 * new SchemaResolver(finder);
 * </pre>
 * 
 * @author Arian Treffer
 */
public class SchemaResolver implements LSResourceResolver {
    
    static final CLogger log = CLoggerFactory.getClassLogger();

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
