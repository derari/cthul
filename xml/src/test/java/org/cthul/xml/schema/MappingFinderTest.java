package org.cthul.xml.schema;

import java.io.InputStream;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.junit.*;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class MappingFinderTest {
    
    public MappingFinderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private MappingFinderImpl instance;
    
    @Before
    public void setUp() {
        instance = new MappingFinderImpl();
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


    public class MappingFinderImpl extends MappingFinder {

        @Override
        public String resolve(String uri) {
            return super.resolve(uri);
        }
        
        @Override
        public InputStream get(String source) {
            return null;
        }
    }
}
