package org.cthul.resolve;

import java.io.InputStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class ResourceResolverTest {

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

    public ResourceResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        instance = newTestInstance();
    }
    
    protected RRequest r(String uri) {
        return new RRequest(uri, null, null, null);
    }

    @Test
    public void w3XMLSchema_resource_exists() throws Exception {
        RResult res = instance.resolve(r(OrgW3Resolver.NS_W3_XMLSCHEMA));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }

    @Test
    public void w3XML_resource_exists() throws Exception {
        RResult res = instance.resolve(r(OrgW3Resolver.NS_W3_XML));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }

    @Test
    public void menu_schema_file_exists() throws Exception {
        RResult res = instance.resolve(r(NS_MENU));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }
    
    @Test
    public void cars_schema_file_exists() throws Exception {
        RResult res = instance.resolve(r(NS_CARS));
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream().read(), is(greaterThan(-1)));
    }

}