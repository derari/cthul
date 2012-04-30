package org.cthul.xml.schema;

import org.cthul.proc.Proc;
import java.io.InputStream;
import org.cthul.proc.Procs;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CthulMatchers.*;

/**
 *
 * @author Arian Treffer
 */
public class AbstractFinderTest {
    
    public AbstractFinderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private AbstractFinderImpl instance;
    
    @Before
    public void setUp() {
        instance = new AbstractFinderImpl();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_addSchemas() {
        Proc addSchemas = Procs.invoke(instance, "addSchemas", String[].class);
        assertThat(addSchemas.call("a", "b"), not(raisesException()));
        assertThat(addSchemas.call("a", "b", "c"), 
                raises(IllegalArgumentException.class));
    }

    @Test
    public void resolveDomain() {
        instance.addDomainPatterns(
                "http://(.*)\\.example\\.org/(.*)", "./example/$1/$2.xsd");
        
        assertThat(instance.resolve("http://a.example.org/b/c"), 
                   is("./example/a/b/c.xsd"));
    }


    public class AbstractFinderImpl extends AbstractFinder {

        @Override
        public String resolve(String uri) {
            return super.resolve(uri);
        }
        
        public InputStream get(String source) {
            return null;
        }
    }
}
