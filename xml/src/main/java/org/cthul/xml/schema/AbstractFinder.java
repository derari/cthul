package org.cthul.xml.schema;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractFinder implements SchemaFinder {
    
    /**
     * Returns a schema finder that uses this schema finder for look-up,
     * but is not mutable.
     * @return 
     */
    public SchemaFinder immutable() {
        return new CompositeFinder(this);
    }

}
