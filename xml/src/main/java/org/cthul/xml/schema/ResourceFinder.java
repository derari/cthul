/*
 * 
 */

package org.cthul.xml.schema;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author Arian Treffer
 */
public class ResourceFinder extends MappingFinder {

    private final Class<?> clazz;

    public ResourceFinder(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ResourceFinder(Class<?> clazz, String... schemas) {
        super(schemas);
        this.clazz = clazz;
    }

    public ResourceFinder(Class<?> clazz, Map<String, String> schemas) {
        super(schemas);
        this.clazz = clazz;
    }

    @Override
    protected InputStream get(String source) {
        return clazz.getResourceAsStream(source);
    }

}
