package org.cthul.resolve;

import java.util.Iterator;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class UriMappingResolverTest {
    
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
        assertThat(addSchemas.call("a", "b"), _not(raisesException()));
        assertThat(addSchemas.call("a", "b", "c"), 
                raises(IllegalArgumentException.class));
    }
    
    @Test
    public void test_addDomain() {
        instance.addDomain("http://example.org", "./example$1.xsd");
        
        assertThat(resolve("http://example.org/b/c"), is("./example/b/c.xsd"));
    }

    @Test
    public void resolveDomain() {
        instance.addDomainPatterns(
                "http://(.*)\\.example\\.org/(.*)", "./example/$1/$2.xsd");
        
        assertThat(resolve("http://a.example.org/b/c"), is("./example/a/b/c.xsd"));
    }

    private String resolve(String uri) {
        return instance.resolver(uri).next();
    }

    public class UriMappingResolverImpl extends UriMappingResolver {

        @Override
        protected Iterator<String> resolver(String uri) {
            return super.resolver(uri);
        }

        @Override
        protected RResult get(RRequest request, String source) {
            return null;
        }
        
    }
}
