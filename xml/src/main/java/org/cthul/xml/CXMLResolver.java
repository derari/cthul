package org.cthul.xml;

import java.io.*;
import javax.xml.stream.*;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import org.cthul.resolve.*;

/**
 *
 * @author Arian Treffer
 */
public class CXMLResolver implements XMLResolver {
    
    protected final XMLInputFactory inputFactory;
    protected final ResourceResolver resolver;

    public CXMLResolver(XMLInputFactory inputFactory, ResourceResolver resolver) {
        if (inputFactory == null) inputFactory = XMLInputFactory.newFactory();
        this.inputFactory = inputFactory;
        this.resolver = resolver;
    }
    
    public CXMLResolver(ResourceResolver resolver) {
        this(null, resolver);
    }
    
    public CXMLResolver(XMLInputFactory inputFactory, ResourceResolver... resolver) {
        this(inputFactory, new CompositeResolver(resolver));
    }

    public CXMLResolver(ResourceResolver... resolver) {
        this(null, resolver);
    }
    @Override
    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        try {
            RRequest req = new RRequest(namespace, publicID, systemID, baseURI);
            RResult res = resolver.resolve(req);
            return result(res);
        } catch (ResolvingException e) {
            throw new XMLStreamException(e.getMessage(), e.getCause());
        }
    }

    protected Object result(RResult res) throws XMLStreamException {
        Reader r = res.getReader();
        if (r != null) return inputFactory.createXMLStreamReader(res.getSystemId(), r);
        InputStream is = res.getInputStream();
        if (is != null) return inputFactory.createXMLStreamReader(res.getSystemId(), is);
        String s = res.getString();
        if (s != null) return inputFactory.createXMLStreamReader(res.getSystemId(), new StringReader(s));
        return null;
    }
    
}
