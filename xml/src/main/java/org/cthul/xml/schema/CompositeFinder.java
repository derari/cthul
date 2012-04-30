package org.cthul.xml.schema;

import java.io.InputStream;

/**
 *
 * @author Arian Treffer
 */
public class CompositeFinder implements SchemaFinder {

    private final SchemaFinder[] inputs;

    public CompositeFinder(SchemaFinder... inputs) {
        this.inputs = inputs;
    }

    @Override
    public InputStream find(String name) {
        for (SchemaFinder i: inputs) {
            InputStream is = i.find(name);
            if (is != null) return is;
        }
        return null;
    }

}
