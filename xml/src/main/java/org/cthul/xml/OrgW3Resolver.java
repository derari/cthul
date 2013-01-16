package org.cthul.xml;

import java.util.HashMap;
import java.util.Map;
import org.cthul.resolve.ClassResourceResolver;
import org.cthul.resolve.ResourceResolver;

/**
 * A {@link ResourceResolver} that provides the basic XML schemas.
 * @author Arian Treffer
 */
public class OrgW3Resolver extends ClassResourceResolver {
    
    /**
     * @return {@link #INSTANCE}
     */
    public static ResourceResolver getInstance() {
        return INSTANCE;
    }
    
    /** {@value} */
    public static final String NS_W3_XMLSCHEMA =
            "http://www.w3.org/2001/XMLSchema";
    /** {@value} */
    public static final String NS_W3_XML =
            "http://www.w3.org/XML/1998/namespace";
    /** {@value} */
    public static final String NS_W3_XML_XSD =
            "http://www.w3.org/2001/xml.xsd";

    public static Map<String, String> getSchemaMap() {
        final Map<String, String> result = new HashMap<>();
        result.put(NS_W3_XMLSCHEMA,    "/org/w3/XMLSchema.xsd");
        result.put(NS_W3_XML,          "/org/w3/xml.xsd");
        result.put(NS_W3_XML_XSD,      "/org/w3/xml.xsd");
        return result;
    }
    
    public static final ResourceResolver INSTANCE = new OrgW3Resolver().immutable();
    
    public OrgW3Resolver(Class<?> clazz) {
        super(clazz, getSchemaMap());
    }
    
    public OrgW3Resolver() {
        this(OrgW3Resolver.class);
    }
    
}
