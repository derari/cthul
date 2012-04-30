package org.cthul.xml.idstax;

import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author derari
 */
public class BasicElementAttributes implements ElementAttributes {

    private final XMLStreamReader reader;
    private final ElementIDResolver idResolver;

    public BasicElementAttributes(XMLStreamReader reader, ElementIDResolver idResolver) {
        this.reader = reader;
        this.idResolver = idResolver;
    }
    
    private boolean updateRequired = true;
    private int attC = 0;
    private int[] indexToIDMap = new int[16];
    
    private void ensureIsUpdated() {
        if (updateRequired) {
            updateRequired = false;
            attC = reader.getAttributeCount();
            ensureIndexMapSize(attC);
            for (int i = 0; i < attC; i++) {
                indexToIDMap[i] = idResolver.getEID(
                        reader.getAttributeNamespace(i), 
                        reader.getAttributeLocalName(i));
            }
        }
    }

    private void ensureIndexMapSize(final int attC) {
        if (indexToIDMap.length < attC) {
            int len = indexToIDMap.length;
            while (len < attC) len *= 2;
            indexToIDMap = new int[len];
        }
    }
    
    private int indexOfID(int atID) {
        ensureIsUpdated();
        final int c = attC;
        final int[] map = indexToIDMap; 
        for (int i = 0; i < c; i++) {
            if (map[i] == atID) {
                return i;
            }
        }
        return -1;
    }
    
    public void update() {
        updateRequired = true;
    }

    @Override
    public int getID(int index) {
        ensureIsUpdated();
        return indexToIDMap[index];
    }

    @Override
    public String getValue(int index) {
        return reader.getAttributeValue(index);
    }

    @Override
    public int getCount() {
        return attC;
    }
    
    @Override
    public String getValueByID(int atID) {
        int i = indexOfID(atID);
        if (i < 0) {
            return null;
        } else {
            return getValue(i);
        }
    }
    
    @Override
    public boolean hasAttribute(int atID) {
        return indexOfID(atID) >= 0;
    }
    
}
