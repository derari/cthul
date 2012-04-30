package org.cthul.xml.validation;

import java.io.File;
import org.cthul.proc.P0;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.xml.schema.SchemaResolver;
import org.cthul.xml.schema.SchemaResolverTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.CthulMatchers.*;

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
        SchemaResolver schemas = new SchemaResolver(SchemaResolverTest.newTestInstance());
        TestHandler handler = new TestHandler();
        File f = new File("src/test/resources/valid-menu.xml");
        new ValidatingSAXParser(schemas).parse(f, handler);
    }
    
    @Test
    public void parse_invalid_file() throws Exception {
        final SchemaResolver schemas = new SchemaResolver(SchemaResolverTest.newTestInstance());
        final TestHandler handler = new TestHandler();
        final File f = new File("src/test/resources/invalid-menu.xml");
        Proc parse = Procs.invoke(new ValidatingSAXParser(schemas), "parse", f, handler);
        assertThat(parse, raises(SAXParseException.class));
        
    }
    
    private static class TestHandler extends DefaultHandler {
        
    }
}
