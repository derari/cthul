package org.cthul.xml.schema;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arian Treffer
 */
public class OrgW3Finder {
    
    public static final SchemaFinder INSTANCE ;

    /**
     * @return {@link #INSTANCE}
     */
    public static SchemaFinder getInstance() {
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
    
    static {
        INSTANCE = new ResourceFinder(OrgW3Finder.class, getSchemaMap()).immutable();
    }

}
