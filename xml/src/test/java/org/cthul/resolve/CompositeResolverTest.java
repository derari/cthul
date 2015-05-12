package org.cthul.resolve;

import org.cthul.xml.OrgW3Resolver;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class CompositeResolverTest {

    /*
     * Actually, this only tests the 3 schema finders
     */
    
    private static final String NS_MENU =
            "http://cthul.org/xml/test/schema/menu";

    private static final String NS_CARS =
            "http://cthul.org/xml/test/schema/cars";

    private static final String _NS_TEST =
            "http://cthul.org/xml/test/(.*)";
    
    public static ResourceResolver newTestInstance() {
        FileResolver fileFinder = new FileResolver("src/test/resources/");
        fileFinder.addSchemas(
                    NS_MENU, "schema/menu.xsd")
                .addDomainPatterns(
                    _NS_TEST, "$1.xsd");

        return new CompositeResolver(
                fileFinder,
                OrgW3Resolver.INSTANCE);
    }

    private ResourceResolver instance;

    @Before
    public void setUp() {
        instance = newTestInstance();
    }
    
    protected RRequest r(String uri) {
        return new RRequest(uri);
    }
    
    protected RResult resolve(RRequest request) {
        return instance.resolve(request).getResult();
    }

    @Test
    public void test_w3XMLSchema_resource_exists() throws Exception {
        RResult res = resolve(r(OrgW3Resolver.NS_W3_XMLSCHEMA));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }

    @Test
    public void test_w3XML_resource_exists() throws Exception {
        RResult res = resolve(r(OrgW3Resolver.NS_W3_XML));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }

    @Test
    public void test_menu_schema_file_exists() throws Exception {
        RResult res = resolve(r(NS_MENU));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }
    
    @Test
    public void test_cars_schema_file_exists() throws Exception {
        RResult res = resolve(r(NS_CARS));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }

}