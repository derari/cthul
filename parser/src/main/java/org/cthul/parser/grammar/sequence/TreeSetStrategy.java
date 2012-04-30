package org.cthul.parser.grammar.sequence;

import java.util.TreeSet;

/**
 *
 * @author Arian Treffer
 */
public class TreeSetStrategy<E> extends CollectionStrategy<E, TreeSet<? super E>> {

    @Override
    public TreeSet<E> newInstance() {
        return new TreeSet<E>();
    }
    
}
