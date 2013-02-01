package org.cthul.parser.sequence;

import java.util.Collection;

/**
 *
 * @author Arian Treffer
 */
public abstract class CollectionSequence<E, C extends Collection<? super E>> 
                extends SequenceBuilderBase<E, C> {

    @Override
    public void add(C collection, E element) {
        collection.add(element);
    }
    
}
