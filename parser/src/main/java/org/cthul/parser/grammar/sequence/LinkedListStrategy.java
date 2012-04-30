package org.cthul.parser.grammar.sequence;

import java.util.LinkedList;

/**
 *
 * @author Arian Treffer
 */
public class LinkedListStrategy<E> extends CollectionStrategy<E, LinkedList<? super E>> {

    @Override
    public LinkedList<E> newInstance() {
        return new LinkedList<E>();
    }
    
}
