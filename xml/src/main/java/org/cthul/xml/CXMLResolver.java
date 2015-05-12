package org.cthul.xml;

import java.io.Reader;
import java.io.StringReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.cthul.resolve.*;

/**
 * Returns the schema file for a namespace uri.
 * <p>
 * Usage:
 * <pre>
 * new CXMLResolver(OrgW3Resolver.INSTANCE, myFinder1, myFinder2);
 * </pre>
 * or
 * <pre>
 * ResourceResolver resolver = new CompositeResolver(
 *          OrgW3Finder.INSTANCE,
 *          myFinder1,
 *          myFinder2
 *      );
 * new CXMLResolver(resolver);
 * </pre>
 */
public class CXMLResolver extends ObjectResolver<Object, XMLStreamException>
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
        if (res.getResultType().isCharacterData() || res.getEncoding() != null) {
            // use result encoding
            return inputFactory.createXMLStreamReader(res.getSystemId(), res.asReader());
        } else {
            // let xml reader handle encoding
            return inputFactory.createXMLStreamReader(res.getSystemId(), res.asInputStream());
        }
    }
    
}
