package org.cthul.xml.idstax;

/**
 * A data element may have additional attributes. Attributes are identified by
 * their type ID, which is also managed by the ElementIDResolver.
 * 
 * @author Arian Treffer
 */
public interface ElementAttributes {

    public int getID(int index);
    
    public String getValue(int index);
    
    public int getCount();
    
    public String getValueByID(int atID);
    
    boolean hasAttribute(int atID);
    
}
