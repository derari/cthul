package org.cthul.matchers.fluent.gen.iface;

import com.thoughtworks.qdox.JavaDocBuilder;
import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author derari
 */
public class InterfaceGeneratorTestBase {
    
    protected JavaDocBuilder doc;
    protected FluentMethodCollector collector;
    
    @Before
    public void setUp() {
        doc = new JavaDocBuilder();
        doc.addSourceTree(new File("src/test/java"));
        collector = new FluentMethodCollector(doc);
    }
    
    @After
    public void tearDown() {
        collector = null;
    }
    
    protected List<FluentMethod> getMethods(Class clazz) {
        String name = classToString(clazz);
        collector.addMethods(name);
        List<FluentMethod> methods = collector.getMethods();
        return methods;
    }

    protected String classToString(Class clazz) {
        String name = clazz.getCanonicalName();
        if (clazz.getDeclaringClass() != null) {
            int i = name.lastIndexOf('.');
            name = name.substring(0, i) + "$" + name.substring(i+1);
        }
        return name;
    }
    
}
