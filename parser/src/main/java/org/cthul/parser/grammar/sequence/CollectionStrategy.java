package org.cthul.parser.grammar.sequence;

import java.util.Collection;

/**
 *
 * @author Arian Treffer
 */
public abstract class CollectionStrategy<E, C extends Collection<? super E>> 
                extends AbstractSequenceStrategy<E, C> {

    @Override
    public void add(C collection, E element) {
        collection.add(element);
    }
    
}
