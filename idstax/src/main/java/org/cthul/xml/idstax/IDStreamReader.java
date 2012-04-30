package org.cthul.xml.idstax;

/**
 *
 * @author Arian Treffer
 */
public interface IDStreamReader {

    public DataType next();

    public DataType nextTag();

    public boolean hasNext();

    public void close();

    public DataType getType();

    public int getEID();

    public ElementID getElementID();

    public ElementAttributes getAttributes();

    public String getText();

    public void require(int eID);

    public void require(DataType... dt);

    public void require(int eID, DataType dt);

}
