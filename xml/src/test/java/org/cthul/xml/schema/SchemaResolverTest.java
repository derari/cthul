package org.cthul.xml.schema;

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
public class SchemaResolverTest {

    /*
     * Actually, this only tests the 3 schema finders
     */
    
    private static final String NS_MENU =
            "http://cthul.org/xml/test/schema/menu";

    private static final String NS_CARS =
            "http://cthul.org/xml/test/schema/cars";

    private static final String _NS_TEST =
            "http://cthul.org/xml/test/(.*)";
    
    public static SchemaFinder newTestInstance() {
        FileFinder fileFinder = new FileFinder("src/test/resources/");
        fileFinder.addSchemas(
                    NS_MENU, "schema/menu.xsd")
                .addDomainPatterns(
                    _NS_TEST, "$1.xsd");

        return new CompositeFinder(
                fileFinder,
                SchemaResolver.ORG_W3_SCHEMA_FINDER);
    }

    private SchemaFinder instance;

    public SchemaResolverTest() {
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

    @Test
    public void w3XMLSchema_resource_exists() throws Exception {
        InputStream is = instance.find(SchemaResolver.NS_W3_XMLSCHEMA);
        assertThat(is, is(notNullValue()));
        assertThat(is.read(), is(greaterThan(-1)));
    }

    @Test
    public void w3XML_resource_exists() throws Exception {
        InputStream is = instance.find(SchemaResolver.NS_W3_XML);
        assertThat(is, is(notNullValue()));
        assertThat(is.read(), is(greaterThan(-1)));
    }

    @Test
    public void menu_schema_file_exists() throws Exception {
        InputStream is = instance.find(NS_MENU);
        assertThat(is, is(notNullValue()));
        assertThat(is.read(), is(greaterThan(-1)));
    }
    
    @Test
    public void cars_schema_file_exists() throws Exception {
        InputStream is = instance.find(NS_CARS);
        assertThat(is, is(notNullValue()));
        assertThat(is.read(), is(greaterThan(-1)));
    }

}