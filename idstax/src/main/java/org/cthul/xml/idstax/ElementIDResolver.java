package org.cthul.xml.idstax;

import java.util.Iterator;

/**
 * Provides the names and IDs of all expected element types.
 * @author Arian Treffer
 */
public interface ElementIDResolver extends Iterable<ElementID> {

    public int getEID(String ns, String name);

    public ElementID getElementID(String ns, String name);

    public ElementID getElementID(int id);

    @Override
    public Iterator<ElementID> iterator();

}
