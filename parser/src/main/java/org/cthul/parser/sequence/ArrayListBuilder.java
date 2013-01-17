package org.cthul.parser.sequence;

import java.util.ArrayList;

/**
 *
 * @author Arian Treffer
 */
public class ArrayListBuilder<E> extends CollectionBuilder<E, ArrayList<? super E>> {

    @Override
    public ArrayList<E> newInstance() {
        return new ArrayList<>();
    }
    
}
