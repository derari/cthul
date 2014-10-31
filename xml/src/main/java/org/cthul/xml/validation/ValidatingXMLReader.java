package org.cthul.xml.validation;

import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.*;

/**
 * An XML reader that validates the input against schemas on the fly.
 * The schema has to be referenced in the xml-file and is loaded using the
 * {@link LSResourceResolver} of the {@link SchemaFactory}.
 */
public class ValidatingXMLReader implements XMLReader {
    
    private final SchemaFactory schemaFactory;
    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ContentHandler contentHandler = null;
    private ErrorHandler errorHandler = null;

    public ValidatingXMLReader(LSResourceResolver resolver) {
        schemaFactory = SchemaFactory.newInstance(
                                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(resolver);
    }

    public ValidatingXMLReader(SchemaFactory schemaFactory) {
        this.schemaFactory = schemaFactory;
    }

    public SchemaFactory getSchemaFactory() {
        return schemaFactory;
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException(name);
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException(name);
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException(name);
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException(name);
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this.dtdHandler = handler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        Validator v = schemaFactory.newSchema().newValidator();
        v.setResourceResolver(schemaFactory.getResourceResolver());
        v.setErrorHandler(errorHandler);
        
        SAXResult result = new SAXResult();
        result.setHandler(contentHandler);

        v.validate(new SAXSource(this, input), result);
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        parse(new InputSource(systemId));
    }
    
}
