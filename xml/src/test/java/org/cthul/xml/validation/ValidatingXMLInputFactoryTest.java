package org.cthul.xml.validation;

import java.io.File;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.cthul.proc.P0;
import org.cthul.proc.Proc;
import org.cthul.resolve.ResourceResolverTest;
import org.cthul.xml.CLSResourceResolver;
import org.junit.Test;
import org.xml.sax.SAXParseException;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class ValidatingXMLInputFactoryTest {

    @Test
    public void test_parse_valid_file() throws Exception {
        CLSResourceResolver schemas = new CLSResourceResolver(ResourceResolverTest.newTestInstance());
        File f = new File("src/test/resources/valid-menu.xml");
        XMLStreamReader reader = new ValidatingXMLInputFactory(schemas).createXMLStreamReader(f);
        reader.next();
        reader.require(XMLStreamConstants.START_ELEMENT, null, "menu");
        assertThat(reader.getAttributeValue(null, "code1"), is("1"));
        assertThat(reader.getAttributeValue(null, "code2"), is("2"));
    }

    @Test
    public void test_parse_invalid_file() throws Exception {
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
