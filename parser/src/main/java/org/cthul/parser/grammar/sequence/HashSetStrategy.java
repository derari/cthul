package org.cthul.parser.grammar.sequence;

import java.util.HashSet;

/**
 *
 * @author Arian Treffer
 */
public class HashSetStrategy<E> extends CollectionStrategy<E, HashSet<? super E>> {

    @Override
    public HashSet<E> newInstance() {
        return new HashSet<E>();
    }
    
}
