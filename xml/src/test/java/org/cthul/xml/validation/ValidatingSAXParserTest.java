package org.cthul.xml.validation;

import java.io.File;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.resolve.ResourceResolverTest;
import org.cthul.xml.CLSResourceResolver;
import org.junit.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import static org.cthul.matchers.CthulMatchers.raises;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author derari
 */
public class ValidatingSAXParserTest {
    
    public ValidatingSAXParserTest() {
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
        TestHandler handler = new TestHandler();
        File f = new File("src/test/resources/valid-menu.xml");
        new ValidatingSAXParser(schemas).parse(f, handler);
        assertThat(handler.code1, is("1"));
        assertThat(handler.code2, is("2"));
    }
    
    @Test
    public void parse_valid_file2() throws Exception {
        CLSResourceResolver schemas = new CLSResourceResolver(ResourceResolverTest.newTestInstance());
        TestHandler handler = new TestHandler();
        File f = new File("src/test/resources/a.xml");
        new ValidatingSAXParser(schemas).parse(f, handler);
    }
    
    @Test
    public void parse_valid_file3() throws Exception {
        CLSResourceResolver schemas = new CLSResourceResolver(ResourceResolverTest.newTestInstance());
        TestHandler handler = new TestHandler();
        File f = new File("src/test/resources/schema/a.xsd");
        new ValidatingSAXParser(schemas).parse(f, handler);
    }
    
    @Test
    public void parse_invalid_file() throws Exception {
        final CLSResourceResolver schemas = new CLSResourceResolver(ResourceResolverTest.newTestInstance());
        final TestHandler handler = new TestHandler();
        final File f = new File("src/test/resources/invalid-menu.xml");
        Proc parse = Procs.invoke(new ValidatingSAXParser(schemas), "parse", f, handler);
        assertThat(parse, raises(SAXParseException.class));
        
    }
    
    private static class TestHandler extends DefaultHandler {
        
        String code1, code2;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("menu")) {
                code1 = attributes.getValue("code1");
                code2 = attributes.getValue("code2");
            }
        }
        
    }
}
