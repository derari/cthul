package org.cthul.parser.sequence;

import java.util.TreeSet;
import org.cthul.parser.util.Inject;

/**
 *
 * @author Arian Treffer
 */
@Inject(factory="instance")
public class TreeSetSequence<E> extends CollectionSequence<E, TreeSet<? super E>> {

    @Override
    public TreeSet<E> newInstance() {
        return new TreeSet<>();
    }
    
    private static final TreeSetSequence INSTANCE = new TreeSetSequence();

    public static <E> TreeSetSequence<E> instance() {
        return INSTANCE;
    }
    
}
