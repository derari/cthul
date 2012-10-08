package org.cthul.xml.validation;

import java.io.*;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.cthul.xml.CLSResourceResolver;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.*;

/**
 * A SAX parser that validates the input against schemas on the fly.
 * The schema has to be referenced in the xml-file and is loaded using a
 * {@link LSResourceResolver }.
 * 
 * @author Arian Treffer
 * @see CLSResourceResolver
 */
public class ValidatingSAXParser {

    private SchemaFactory schemaFactory;

    public ValidatingSAXParser(LSResourceResolver resolver) {
        schemaFactory = SchemaFactory.newInstance(
                                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(resolver);
    }

    public void parse(File file, ContentHandler handler) throws FileNotFoundException, SAXException, IOException {
        parse(new FileInputStream(file), file.getCanonicalPath(), handler);
    }

    public void parse(InputStream input, String systemId, ContentHandler handler) throws SAXException, IOException {
        InputSource is = new InputSource(input);
        is.setSystemId(systemId);

        parse(is, handler);
    }
    
    public void parse(InputSource input, ContentHandler handler) throws SAXException, IOException {
        Validator v = schemaFactory.newSchema().newValidator();
        v.setResourceResolver(schemaFactory.getResourceResolver());

        v.validate(new SAXSource(input), new SAXResult(handler));
    }

}