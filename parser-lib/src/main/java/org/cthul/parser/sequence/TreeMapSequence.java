package org.cthul.parser.sequence;

import java.util.TreeMap;
import org.cthul.parser.util.Inject;

@Inject(factory="instance")
public class TreeMapSequence<K, V> extends MapSequence<K, V, TreeMap<K, V>> {

    @Override
    public TreeMap<K, V> newInstance() {
        return new TreeMap<>();
    }
    
    private static final TreeMapSequence INSTANCE = new TreeMapSequence();

    public static <K, V> TreeMapSequence<K, V> instance() {
        return INSTANCE;
    }
}
