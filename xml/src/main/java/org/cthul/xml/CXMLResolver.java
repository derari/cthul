package org.cthul.xml;

import java.io.*;
import javax.xml.stream.*;
import org.cthul.resolve.*;

/**
 *
 * @author Arian Treffer
 */
public class CXMLResolver extends AbstractResolver<Object, XMLStreamException>
                          implements XMLResolver {
    
    protected final XMLInputFactory inputFactory;

    public CXMLResolver(XMLInputFactory inputFactory, ResourceResolver resolver) {
        super(resolver);
        if (inputFactory == null) inputFactory = XMLInputFactory.newFactory();
        this.inputFactory = inputFactory;
    }

    public CXMLResolver(XMLInputFactory inputFactory, ResourceResolver... resolver) {
        super(resolver);
        if (inputFactory == null) inputFactory = XMLInputFactory.newFactory();
        this.inputFactory = inputFactory;
    }

    public CXMLResolver(ResourceResolver resolver) {
        this(null, resolver);
    }

    public CXMLResolver(ResourceResolver... resolver) {
        this(null, resolver);
    }

    @Override
    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        try {
            return resolve(namespace, publicID, systemID, baseURI);
        } catch (ResolvingException e) {
            throw new XMLStreamException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected Object result(RResult res) throws XMLStreamException {
        Reader r = res.getReader();
        if (r != null) return inputFactory.createXMLStreamReader(res.getSystemId(), r);
        InputStream is = res.getInputStream();
        if (is != null) return inputFactory.createXMLStreamReader(res.getSystemId(), is);
        String s = res.getString();
        if (s != null) return inputFactory.createXMLStreamReader(res.getSystemId(), new StringReader(s));
        throw new XMLStreamException("Cannot create result from " + res);
    }
    
}
