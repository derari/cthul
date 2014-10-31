package org.cthul.xml.validation;

import java.io.*;
import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.*;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Creates a StAX reader that validates the input file.
 * <p>
 * Kind of a hack, until StAX with proper validation is available.
 */
public class ValidatingXMLInputFactory extends XMLInputFactory {
    
    private static XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
    
    private final XMLInputFactory inputFactory;
    private final SchemaFactory schemaFactory;

    public ValidatingXMLInputFactory(LSResourceResolver resolver) {
        this(XMLInputFactory.newFactory(), 
             SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI));
        schemaFactory.setResourceResolver(resolver);
    }

    public ValidatingXMLInputFactory(XMLInputFactory inputFactory, SchemaFactory schemaFactory) {
        this.inputFactory = inputFactory;
        this.schemaFactory = schemaFactory;
    }
    
    private ByteArrayOutputStream buffer() {
        return new ByteArrayOutputStream(8*1024);
    }
    
    private InputStream is(ByteArrayOutputStream buf) {
        return is(buf.toByteArray());
    }
    
    private InputStream is(byte[] bytes) {
        int skip = skipWhitespace(bytes);
        return new ByteArrayInputStream(bytes, skip, bytes.length-skip);        
    }
    
    private InputStream validate(XMLEventReader unvalidatedInput) throws XMLStreamException {
        return validate(new StAXSource(unvalidatedInput));
    }
    
    private InputStream validate(XMLStreamReader unvalidatedInput) throws XMLStreamException {
        return validate(new StAXSource(unvalidatedInput));
    }
        
    private InputStream validate(StAXSource unvalidatedInput) throws XMLStreamException {
        ByteArrayOutputStream buf = buffer();
        // validate the file, the output is enriched with data from the schema
        // (e.g., default values) and is then used for parsing.
        XMLStreamWriter validatedOutput = outputFactory.createXMLStreamWriter(buf);
        validate(unvalidatedInput, new StAXResult(validatedOutput));
        return is(buf);
    }
    
    private InputStream validate(InputStream is, String systemId, String encoding) throws XMLStreamException {
        InputSource input = new InputSource(is);
        input.setEncoding(encoding);
        input.setSystemId(systemId);
        return validate(input);
    }
    
    private InputStream validate(Reader r, String systemId) throws XMLStreamException {
        InputSource input = new InputSource(r);
        input.setSystemId(systemId);
        return validate(input);
    }
    
    static final SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
    
    private InputStream validate(InputSource unvalidatedInput) throws XMLStreamException {
        try {
            ByteArrayOutputStream buf = buffer();
            TransformerHandler t = tf.newTransformerHandler();
            t.setResult(new StreamResult(buf));
            
            validate(new SAXSource(unvalidatedInput), new SAXResult(t));
            return is(buf);
        } catch (TransformerConfigurationException e) {
            throw new XMLStreamException(e);
        }
    }
    
    // validate the file, the output is enriched with data from the schema
    // (e.g., default values) and is then used for parsing.    
    private void validate(Source src, Result res) throws XMLStreamException {
        try {
            Validator v = schemaFactory.newSchema().newValidator();
            v.setResourceResolver(schemaFactory.getResourceResolver());
            v.validate(src, res);
        } catch (SAXException | IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private int skipWhitespace(byte[] bytes) {
        final int len = bytes.length;
        for (int i = 0; i < len; i++) {
            switch (bytes[i]) {
                case '\n':
                case '\r':
                case '\t':
                case ' ':
                    break;
                default:
                    return i;
            }
        }
        return len;
    }
    
    private XMLStreamReader stream(String systemId, InputStream is) throws XMLStreamException {
        return inputFactory.createXMLStreamReader(systemId, is);
    }
    
    private XMLEventReader event(String systemId, InputStream is) throws XMLStreamException {
        return inputFactory.createXMLEventReader(systemId, is);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return stream(null, validate(reader, null));
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return stream(null, validate(
                inputFactory.createXMLStreamReader(source)));
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return stream(null, validate(stream, null, null));
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        return stream(null, validate(stream, null, encoding));
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return stream(systemId, validate(stream, systemId, null));
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return stream(systemId, validate(reader, systemId));
    }

    @Override
    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        return event(null, validate(reader, null));
    }

    @Override
    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        return event(systemId, validate(reader, systemId));
    }

    @Override
    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        return event(null, validate(reader));
    }

    @Override
    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return event(null, validate(
                inputFactory.createXMLStreamReader(source)));
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
        return event(null, validate(stream, null, null));
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        return event(null, validate(stream, null, encoding));
    }

    @Override
    public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
        return event(systemId, validate(stream, systemId, null));
    }

    @Override
    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        return inputFactory.createFilteredReader(
                stream(null, validate(reader)), 
                filter);
    }

    @Override
    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        return inputFactory.createFilteredReader(
                event(null, validate(reader)), 
                filter);
    }

    @Override
    public XMLResolver getXMLResolver() {
        return inputFactory.getXMLResolver();
    }

    @Override
    public void setXMLResolver(XMLResolver resolver) {
        inputFactory.setXMLResolver(resolver);
    }

    @Override
    public XMLReporter getXMLReporter() {
        return inputFactory.getXMLReporter();
    }

    @Override
    public void setXMLReporter(XMLReporter reporter) {
        inputFactory.setXMLReporter(reporter);
    }

    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        inputFactory.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return inputFactory.getProperty(name);
    }

    @Override
    public boolean isPropertySupported(String name) {
        return inputFactory.isPropertySupported(name);
    }

    @Override
    public void setEventAllocator(XMLEventAllocator allocator) {
        inputFactory.setEventAllocator(allocator);
    }

    @Override
    public XMLEventAllocator getEventAllocator() {
        return inputFactory.getEventAllocator();
    }

    public XMLStreamReader createXMLStreamReader(File f) throws IOException, XMLStreamException {
        return createXMLStreamReader(f.getCanonicalPath(), new FileInputStream(f));
    }
    
    public XMLEventReader createXMLEventReader(File f) throws IOException, XMLStreamException {
        return createXMLEventReader(f.getCanonicalPath(), new FileInputStream(f));
    }
    
}
