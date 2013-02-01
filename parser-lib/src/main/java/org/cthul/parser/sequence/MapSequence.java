package org.cthul.parser.sequence;

import java.util.Map;

/**
 *
 * @author Arian Treffer
 */
public abstract class MapSequence<K, V, M extends Map<K, V>> 
                extends SequenceBuilderBase<Map.Entry<K, V>, M> {

    @Override
    public void add(M map, Map.Entry<K, V> element) {
        map.put(element.getKey(), element.getValue());
    }
    
}
