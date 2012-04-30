package org.cthul.xml.idstax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static javax.xml.stream.events.XMLEvent.*;

/**
 *
 * @author Arian Treffer
 */
public class BasicIDStreamReader extends AbstractIDStreamReader {

    private final XMLStreamReader reader;
    private final ElementIDResolver idResolver;
    private final BasicElementAttributes attributes;
    
    private DataType currentType = null;
    private ElementID currentID = null;

    public BasicIDStreamReader(XMLStreamReader reader, ElementIDResolver idResolver) {
        this.reader = reader;
        this.idResolver = idResolver;
        this.attributes = new BasicElementAttributes(reader, idResolver);
    }

    @Override
    public DataType next() {
        currentType = fetchNext();
        attributes.update();
        if (currentType.isTagEvent()) {
            currentID = idResolver.getElementID(
                            reader.getNamespaceURI(), 
                            reader.getLocalName());
        } else {
            currentID = null;
        }
        return currentType;
    }
    
    private DataType fetchNext() {
        try {
            while (true) {
                switch (reader.next()) {
                    case START_DOCUMENT:
                    case END_DOCUMENT:
                        break;
                    case START_ELEMENT:
                        return DataType._START_;
                    case END_ELEMENT:
                        return DataType._END_;
                    case CHARACTERS:
                        return DataType._CHARACTERS_;
                    default:
                        throw new IllegalArgumentException(
                                "Unexpected event type " + reader.getEventType());
                }
                if (!reader.hasNext()) {
                    return DataType._EOF_;
                }
            }
        } catch (XMLStreamException e) {
            throw IDStreamException.asRuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.hasNext();
        } catch (XMLStreamException e) {
            throw IDStreamException.asRuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (XMLStreamException e) {
            throw IDStreamException.asRuntimeException(e);
        }
    }

    @Override
    public DataType getType() {
        return currentType;
    }

    @Override
    public int getEID() {
        return currentID.getID();
    }

    @Override
    public ElementID getElementID() {
        return currentID;
    }

    @Override
    public ElementAttributes getAttributes() {
        return attributes;
    }

    @Override
    public String getText() {
        return reader.getText();
    }
}
