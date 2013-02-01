package org.cthul.parser.sequence;

import java.util.LinkedList;
import org.cthul.parser.util.Inject;

/**
 *
 * @author Arian Treffer
 */
@Inject(factory="instance")
public class LinkedListSequence<E> extends CollectionSequence<E, LinkedList<? super E>> {

    @Override
    public LinkedList<E> newInstance() {
        return new LinkedList<>();
    }
    
    private static final LinkedListSequence INSTANCE = new LinkedListSequence();

    public static <E> LinkedListSequence<E> instance() {
        return INSTANCE;
    }
    
}
