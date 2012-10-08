package org.cthul.xml.validation;

import java.io.File;
import javax.xml.stream.*;
import org.cthul.proc.P0;
import org.cthul.proc.Proc;
import org.cthul.resolve.ResourceResolverTest;
import org.cthul.xml.CLSResourceResolver;
import org.junit.*;
import org.xml.sax.SAXParseException;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Arian Treffer
 */
public class ValidatingXMLInputFactoryTest {

    public ValidatingXMLInputFactoryTest() {
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
    public void parse_valid_file() throws Exception {
        CLSResourceResolver schemas = new CLSResourceResolver(ResourceResolverTest.newTestInstance());
        File f = new File("src/test/resources/valid-menu.xml");
        XMLStreamReader reader = new ValidatingXMLInputFactory(schemas).createXMLStreamReader(f);
        reader.next();
        reader.require(XMLStreamConstants.START_ELEMENT, null, null);
    }

    @Test
    public void parse_invalid_file() throws Exception {
        final CLSResourceResolver schemas = new CLSResourceResolver(ResourceResolverTest.newTestInstance());
        final File f = new File("src/test/resources/invalid-menu.xml");
        Proc parse = new P0() {
            @Override
            protected Object run() throws Throwable {
                XMLStreamReader reader = new ValidatingXMLInputFactory(schemas).createXMLStreamReader(f);
                reader.next();
                reader.require(XMLStreamConstants.START_ELEMENT, null, null);
                return null;
            }
        };
        assertThat(parse, raises(XMLStreamException.class, causedBy(SAXParseException.class)));

    }
}
