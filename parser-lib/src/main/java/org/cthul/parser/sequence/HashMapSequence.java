package org.cthul.parser.sequence;

import java.util.HashMap;
import org.cthul.parser.util.Inject;

@Inject(factory="instance")
public class HashMapSequence<K, V> extends MapSequence<K, V, HashMap<K, V>> {

    @Override
    public HashMap<K, V> newInstance() {
        return new HashMap<>();
    }
    
    private static final HashMapSequence INSTANCE = new HashMapSequence();

    public static <K, V> HashMapSequence<K, V> instance() {
        return INSTANCE;
    }
}
