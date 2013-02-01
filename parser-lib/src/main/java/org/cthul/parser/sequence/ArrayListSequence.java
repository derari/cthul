package org.cthul.parser.sequence;

import java.util.ArrayList;
import org.cthul.parser.util.Inject;

/**
 *
 * @author Arian Treffer
 */
@Inject(factory="instance")
public class ArrayListSequence<E> extends CollectionSequence<E, ArrayList<? super E>> {

    @Override
    public ArrayList<E> newInstance() {
        return new ArrayList<>();
    }
    
    private static final ArrayListSequence INSTANCE = new ArrayListSequence();

    public static <E> ArrayListSequence<E> instance() {
        return INSTANCE;
    }
    
    
    
}
