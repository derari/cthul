package org.cthul.xml.validation;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.cthul.proc.P0;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.xml.schema.SchemaResolver;
import org.cthul.xml.schema.SchemaResolverTest;
import org.xml.sax.SAXParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.CthulMatchers.*;

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
        SchemaResolver schemas = new SchemaResolver(SchemaResolverTest.newTestInstance());
        File f = new File("src/test/resources/valid-menu.xml");
        XMLStreamReader reader = new ValidatingXMLInputFactory(schemas).createXMLStreamReader(f);
        reader.next();
        reader.require(XMLStreamConstants.START_ELEMENT, null, null);
    }

    @Test
    public void parse_invalid_file() throws Exception {
        final SchemaResolver schemas = new SchemaResolver(SchemaResolverTest.newTestInstance());
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
