package org.cthul.resolve;

import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class ClassLoaderResourceResolverTest {

    private ClassLoaderResourceResolver resolver;
    
    @Before
    public void setUp() {
        resolver = new ClassLoaderResourceResolver();
    }
    
    @Test
    public void test_addDomain() throws Exception {
        resolver.addDomain("http://www.w3.org/2001/",    "org/w3/$1.xsd");
        RRequest req = new RRequest("http://www.w3.org/2001/xml");
        assertThat(resolver.resolve(req).getSystemId(), endsWith("/org/w3/xml.xsd"));
    }

}