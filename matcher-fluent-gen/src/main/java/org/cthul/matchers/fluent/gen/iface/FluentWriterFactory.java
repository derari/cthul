package org.cthul.matchers.fluent.gen.iface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 *
 * @author derari
 */
public class FluentWriterFactory {

    private final File outDir;

    public FluentWriterFactory(File outDir) {
        this.outDir = outDir;
    }
    
    public FluentWriter create(String pkg, String name) {
        String path = pkg.replace('.', '/') + "/" + name + ".java";
        File f = new File(outDir, path);
        if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) {
            throw new RuntimeException(f.getParent() + " cannot be created");
        }
        try {
            return new FluentWriter(new FileOutputStream(f));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
