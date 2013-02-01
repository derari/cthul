package org.cthul.parser.sequence;

import java.util.HashSet;
import org.cthul.parser.util.Inject;

/**
 *
 * @author Arian Treffer
 */
@Inject(factory="instance")
public class HashSetSequence<E> extends CollectionSequence<E, HashSet<? super E>> {

    @Override
    public HashSet<E> newInstance() {
        return new HashSet<>();
    }
    
    private static final HashSetSequence INSTANCE = new HashSetSequence();

    public static <E> HashSetSequence<E> instance() {
        return INSTANCE;
    }
       
}
