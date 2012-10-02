package org.cthul.xml.schema;

import java.io.*;
import org.w3c.dom.ls.LSInput;

/**
 *
 * @author Arian Treffer
 */
public class LSStreamInput implements LSInput {

    private InputStream input;
    private String /*type, namespaceURI,*/ publicId, systemId, baseURI;
    private String stringData = null;

    public LSStreamInput(InputStream input, //String type, String namespaceURI,
                         String publicId, String systemId, String baseURI,
                         String actualUri) {
        this.input = input;
//        this.type = type;
//        this.namespaceURI = namespaceURI != null ? namespaceURI : actualUri;
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
        return null;//new StringReader(getStringData());
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getByteStream() {
        return input; //new ByteArrayInputStream(getStringData().getBytes());
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStringData() {
        return null;
//        if (stringData == null) {
//            try {
//                StringBuilder sb = new StringBuilder();
//                BufferedReader br = new BufferedReader(
//                                            new InputStreamReader(input));
//                String line = br.readLine();
//                while (line != null) {
//                    sb.append(line).append('\n');
//                    line = br.readLine();
//                }
//                stringData = sb.toString();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//        return stringData;
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
        return "UTF-8";
    }

    @Override
    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getCertifiedText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        throw new UnsupportedOperationException();
    }
}
