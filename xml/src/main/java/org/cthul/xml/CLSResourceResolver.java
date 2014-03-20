package org.cthul.xml;

import java.io.InputStream;
import java.io.Reader;
import org.cthul.resolve.ObjectResolver;
import org.cthul.resolve.RResult;
import org.cthul.resolve.ResourceResolver;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Returns the schema file for a namespace uri.
 * <p/>
 * Usage:
 * <pre>
 * new CLSResourceResolver(OrgW3Resolver.INSTANCE, myFinder1, myFinder2);
 * </pre>
 * or
 * <pre>
 * ResourceResolver resolver = new CompositeResolver(
 *          OrgW3Finder.INSTANCE,
 *          myFinder1,
 *          myFinder2
 *      );
 * new CLSResourceResolver(resolver);
 * </pre>
 * 
 * @author Arian Treffer
 */
public class CLSResourceResolver extends ObjectResolver<LSInput, RuntimeException>
                                 implements LSResourceResolver {
    
    protected final DOMImplementationLS ls;

    public CLSResourceResolver(ResourceResolver resolver) {
        this(null, resolver);
    }

    public CLSResourceResolver(ResourceResolver... resolver) {
        this(null, resolver);
    }

    public CLSResourceResolver(DOMImplementationLS ls, ResourceResolver resolver) {
        super(resolver);
        this.ls = ls;
    }

    public CLSResourceResolver(DOMImplementationLS ls, ResourceResolver... resolver) {
        super(resolver);
        this.ls = ls;
    }
    
    @Override
    public LSInput resolveResource(String type, String namespaceURI,
                            String publicId, String systemId, String baseURI) {
        return resolve(namespaceURI, publicId, systemId, baseURI);
    }

    @Override
    protected LSInput result(RResult result) {
        if (ls != null) {
            LSInput lsi = ls.createLSInput();
            lsi.setPublicId(result.getPublicId());
            lsi.setSystemId(result.getSystemId());
            lsi.setBaseURI(result.getBaseUri());
            lsi.setCharacterStream(result.getReader());
            lsi.setByteStream(result.getInputStream());
            lsi.setStringData(result.getString());
        }
        return new LSInputResult(result); 
    }
    
    public static class LSInputResult implements LSInput {
        
        private final RResult result;

        public LSInputResult(RResult result) {
            this.result = result;
        }

        @Override
        public Reader getCharacterStream() {
            return result.asReader();
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getByteStream() {
            return result.asInputStream();
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getStringData() {
            return result.asString();
        }

        @Override
        public void setStringData(String stringData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSystemId() {
            return result.getSystemId();
        }

        @Override
        public void setSystemId(String systemId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPublicId() {
            return result.getPublicId();
        }

        @Override
        public void setPublicId(String publicId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getBaseURI() {
            return result.getBaseUri();
        }

        @Override
        public void setBaseURI(String baseURI) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getEncoding() {
            return result.getEncoding();
        }

        @Override
        public void setEncoding(String encoding) {
            result.setEncoding(encoding);
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            throw new UnsupportedOperationException();
        }
    }
}
