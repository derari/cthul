/*
 * 
 */

package org.cthul.xml.schema;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author Arian Treffer
 */
public class FileFinder extends MappingFinder {

    private final String prefix;

    public FileFinder() {
        this("");
    }

    public FileFinder(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected InputStream get(String source) {
        File f = new File(prefix + source);
        if (!f.exists()) return null;
        try {
            return new BufferedInputStream(new FileInputStream(f));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

}
