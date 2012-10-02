package org.cthul.xml.schema;

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

/**
 *
 * @author Arian Treffer
 */
public class LSStreamInput implements LSInput {

    private InputStream input;
    private String publicId, systemId, baseURI;

    public LSStreamInput(InputStream input, 
                         String publicId, String systemId, String baseURI,
                         String actualUri) {
        this.input = input;
        this.publicId = publicId != null ? publicId : actualUri;
        this.systemId = systemId != null ? systemId : actualUri;
        this.baseURI = baseURI != null ? baseURI : actualUri;
    }

    public LSStreamInput(InputStream input, String type, String namespaceURI, 
                         String publicId, String systemId, String baseURI, 
                         String actualUri) {
        this(input, publicId, systemId, baseURI, actualUri);
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getByteStream() {
        return input;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStringData() {
        return null;
    }

    @Override
    public void setStringData(String stringData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBaseURI() {
        return baseURI;
    }

    @Override
    public void setBaseURI(String baseURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException();
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
