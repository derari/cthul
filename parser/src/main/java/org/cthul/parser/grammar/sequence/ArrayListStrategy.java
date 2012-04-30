package org.cthul.parser.grammar.sequence;

import java.util.ArrayList;

/**
 *
 * @author Arian Treffer
 */
public class ArrayListStrategy<E> extends CollectionStrategy<E, ArrayList<? super E>> {

    @Override
    public ArrayList<E> newInstance() {
        return new ArrayList<E>();
    }
    
}
