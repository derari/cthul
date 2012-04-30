package org.cthul.xml.schema;

import java.io.InputStream;

/**
 * Finds and opens schema files.
 * 
 * @author Arian Treffer
 */
public interface SchemaFinder {

    /**
     * Find and open a schema file, 
     * return {@code null} if it could not be resolved.
     * @param uri namespace or file location
     * @return 
     */
    public InputStream find(String uri);

}
