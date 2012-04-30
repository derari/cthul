package org.cthul.xml.idstax;

import java.io.File;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.cthul.xml.schema.CompositeFinder;
import org.cthul.xml.schema.FileFinder;
import org.cthul.xml.schema.SchemaFinder;
import org.cthul.xml.schema.SchemaResolver;
import org.cthul.xml.validation.ValidatingXMLInputFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.xml.idstax.DataType.*;


/**
 *
 * @author Arian Treffer
 */
public class BasicIDStreamReaderTest {
    
    public BasicIDStreamReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test() throws Exception {
        SchemaResolver schemas = new SchemaResolver(newTestSchemaFinder());
        File f = new File("src/test/resources/valid-menu.xml");
        XMLStreamReader xmlReader = new ValidatingXMLInputFactory(schemas).createXMLStreamReader(f);
        MenuResolver resolver = new MenuResolver();
        BasicIDStreamReader reader = new BasicIDStreamReader(xmlReader, resolver);
        Meal meal = parseMenu(reader);
        assertThat(meal.price, is(1));
        assertThat(meal.name, is("Soup"));
        assertThat(meal.description, is("It's just a soup."));
    }
    
    private Meal parseMenu(IDStreamReader reader) {
        Meal meal = null;
        reader.nextTag();
        assertThat(reader.getEID(), is(MenuSchema.MENU));
        while (reader.nextTag() == _START_) {
            assertThat(reader.getEID(), is(MenuSchema.MEAL));
            meal = parseMeal(reader);
        }
        return meal;
    }
    
    private Meal parseMeal(IDStreamReader reader) {
        assert reader.getEID() == MenuSchema.MEAL;
        Meal meal = new Meal();
        meal.price = Integer.parseInt(reader.getAttributes().getValueByID(MenuSchema.PRICE));
        while (reader.nextTag() == _START_) {
            switch (reader.getEID()) {
                case MenuSchema.NAME:
                    meal.name = parseString(reader);
                    break;
                case MenuSchema.DESCRIPTION:
                    meal.description = parseString(reader);
                    break;
                default:
                    reader.require(MenuSchema.NAME);
            }
        }
        return meal;
    }
    
    private String parseString(IDStreamReader reader) {
        if (reader.next() == _CHARACTERS_) {
            String value = reader.getText();
            reader.next();
            return value;
        }
        return null;
    }
    
    private static final String NS_MENU =
            "http://cthul.org/idstax/test/schema/menu";

    private static final String NS_CARS =
            "http://cthul.org/idstax/test/schema/cars";

    private static final String _NS_TEST =
            "http://cthul.org/idstax/test/";
    
    public static SchemaFinder newTestSchemaFinder() {
        FileFinder fileFinder = new FileFinder("src/test/resources/");
        fileFinder.addSchemas(
                NS_MENU, "schema/menu.xsd")
                .addDomain(
                _NS_TEST, "schema/");

        return new CompositeFinder(
                fileFinder,
                SchemaResolver.ORG_W3_SCHEMA_FINDER);
    }
    
}
