package org.cthul.xml.idstax;

/**
 * An element type has a namespace and a name. Together, they should be unique
 * and are mapped to an ID.
 * @author Arian Treffer
 */
public interface ElementID {

    public String getNamespace();

    public String getName();

    public int getID();

}
