package org.cthul.resolve;

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
public class UriMappingResolverTest {
    
    public UriMappingResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private UriMappingResolverImpl instance;
    
    @Before
    public void setUp() {
        instance = new UriMappingResolverImpl();
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

    public class UriMappingResolverImpl extends UriMappingResolver {

        @Override
        public String resolve(String uri) {
            return super.resolve(uri);
        }

        @Override
        protected RResult get(RRequest request, String source) {
            return null;
        }
        
    }
}
